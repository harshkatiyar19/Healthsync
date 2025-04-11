package project.service

import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationResults
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import project.dto.patient.DoctorVisitsCreationDto
import project.dto.patient.PatientInfoCreationDto
import project.dto.patient.PatientInfoDto
import project.dto.patient.ReportsCreationDto
import project.entity.*
import project.jwt.JwtUtil
import project.repository.DoctorRepository
import project.repository.OrganizationRepository
import project.repository.PatientRepository
import project.util.GenralUtil
import java.time.LocalDateTime

@Service
class PatientService(
    val mongoTemplate: MongoTemplate,
    val genralUtil: GenralUtil,
    val patientRepository: PatientRepository,
    val doctorRepository: DoctorRepository,
    val organizationRepository: OrganizationRepository,
    val jwtUtil: JwtUtil
) {

    ///////// Patient Controller
    //reads patient info
    fun getPatientInfo(phno: String): PatientInfoDto? {
        val query = Query(Criteria.where("patientPhNo").`is`(phno)).apply {
            fields().include("patientId")
                .include("patientName")
                .include("patientEmailId")
                .include("patientPhNo")
                .include("patientGender")
                .include("patientBloodGroup")
                .include("patientDob")
                .include("existingMedicalConditions")
                .include("doctorVisits")
        }

        val result = mongoTemplate.findOne(query, PatientInfo::class.java, "Patient")

        val currentDateTime = LocalDateTime.now()
        val filteredMedications = result?.doctorVisits
            ?.flatMap { it.medications }  // Extract medications from each visit
            ?.filter { it.endDate.isAfter(currentDateTime) }  // Keep only active medications
            ?: emptyList()

        return result?.let {
            PatientInfoDto(
                patientId = it.patientId,
                patientName = it.patientName,
                patientEmailId = it.email,
                patientPhNo = it.phno,
                patientGender = it.patientGender,
                patientBloodGroup = it.patientBloodGroup,
                existingMedicalConditions = it.existingMedicalConditions,
                existingMeds = filteredMedications,
                patientDob = it.patientDob
            )
        }
    }

    //fetches patient reports
    fun getPatientReports(phno: String): List<Reports> {
        val aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("phno").`is`(phno)), // Filter by patient phone number
            Aggregation.unwind("reports"),  // Flatten the reports array so that each report is a separate document
            Aggregation.replaceRoot("reports") // Replace the root document with the reports field
        )

        val result: AggregationResults<Reports> = mongoTemplate.aggregate(aggregation, "Patient", Reports::class.java)

        return result.mappedResults
    }

    fun getDoctorVisits(patientPhNo: String): List<DoctorVisits>? {
        val aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("phno").`is`(patientPhNo)), // Filter by patient phone number
            Aggregation.unwind("doctorVisits"),  // Flatten the reports array so that each report is a separate document
            Aggregation.replaceRoot("doctorVisits") // Replace the root document with the reports field
        )

        val result: AggregationResults<DoctorVisits> =
            mongoTemplate.aggregate(aggregation, "Patient", DoctorVisits::class.java)

        return result.mappedResults
    }

    fun getPatientMedicationsHistory(phno: String): List<Medications> {

        val query = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("phno").`is`(phno)),
            Aggregation.unwind("doctorVisits"),
            Aggregation.unwind("doctorVisits.medications"),
            Aggregation.replaceRoot("doctorVisits.medications")
        )

        val result: AggregationResults<Medications> = mongoTemplate.aggregate(query, "Patient", Medications::class.java)

        return result.mappedResults
    }

    ///////// Organization Controller
//adds new reports
    fun newReport(phno: String, reportsCreationDto: ReportsCreationDto): ResponseEntity<Any> {

        val organizationId = organizationRepository.findById(reportsCreationDto.organizationId).orElse(null)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Organization not found")

        val reports = Reports(
            organizationId = organizationId,
            reportName = reportsCreationDto.reportName,
            report = reportsCreationDto.report
        )
        val query = Query(Criteria.where("phno").`is`(phno)).apply {
            fields().include("reports")
        }
        val update = Update().push("reports", reports)

        val result = mongoTemplate.updateFirst(query, update, PatientInfo::class.java, "Patient")
        return when {
            result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.")
            else -> ResponseEntity.ok("New report added successfully.")
        }

    }


    /////////Doctor controller
    //add new dr visit
    fun newDoctorVisit(phno: String, doctorVisitsCreationDto: DoctorVisitsCreationDto): ResponseEntity<Any> {
        val doctorDetails = doctorRepository.findById(doctorVisitsCreationDto.doctorId).orElse(null)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found")

        val doctorVisits = DoctorVisits(
            doctor = doctorDetails,
            weight = doctorVisitsCreationDto.weight,
            height = doctorVisitsCreationDto.height,
            remarks = doctorVisitsCreationDto.remarks,
            medicalConditionName = doctorVisitsCreationDto.medicalConditionName,
            symptoms = doctorVisitsCreationDto.symptoms,
            tests = doctorVisitsCreationDto.tests,
            medications = doctorVisitsCreationDto.medications
        )
        val query = Query(Criteria.where("phno").`is`(phno)).apply {
            fields().include("doctorVisits")
        }
        val update = Update().push("doctorVisits", doctorVisits)

        val result = mongoTemplate.updateFirst(query, update, PatientInfo::class.java, "Patient")
        return when {
            result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.")
            else -> ResponseEntity.ok("New visit added successfully.")
        }
    }

    fun newMedicalConditions(patientPhNo: String, medicalConditionName: String,doctorId:ObjectId): ResponseEntity<Any> {

        val docId=doctorRepository.findById(doctorId).orElse(null)
            ?:return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found")

        val existingMedicalConditions = ExistingMedicalConditions(
            medicalConditionName = medicalConditionName,
            doctor = docId
        )

        val query = Query(Criteria.where("phno").`is`(patientPhNo)).apply {
            fields().include("existingMedicalConditions")
        }
        val update = Update().push("existingMedicalConditions", existingMedicalConditions)

        val result = mongoTemplate.updateFirst(query, update, PatientInfo::class.java, "Patient")
        return when {
            result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.")
            else -> ResponseEntity.ok("New medical condition added successfully.")
        }
    }
///////Admin controller

    fun updatePhNo(patientPhNo: String, newPatientPhNo: String): ResponseEntity<Any> {
//        if (genralUtil.isValidPhoneNumber(newPatientPhNo)) {
//            if (!ifPatientPhnoExists(newPatientPhNo)) {
//                val query = Query(Criteria.where("patientPhNo").`is`(patientPhNo))
//                val update = Update().set("patientPhNo", newPatientPhNo)
//                val result = mongoTemplate.updateFirst(query, update, PatientInfo::class.java)
//                return when {
//                    result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.")
//                    result.modifiedCount == 0L -> ResponseEntity.ok("Patient phone no. was already up to date.")
//                    else -> ResponseEntity.ok("Patient phone no. updated successfully.")
//                }
//            } else {
//
//            }
//
//        } else {
//
//        }

        when{
            !genralUtil.isValidPhoneNumber(newPatientPhNo)->return ResponseEntity.badRequest().body("Invalid Phone no format ")
            ifPatientPhnoExists(newPatientPhNo)->return ResponseEntity.badRequest().body("Given phone no exists")
            else->{
                val query = Query(Criteria.where("phno").`is`(patientPhNo))
                val update = Update().set("phno", newPatientPhNo)
                val result = mongoTemplate.updateFirst(query, update, PatientInfo::class.java)
                return when {
                    result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.")
                    result.modifiedCount == 0L -> ResponseEntity.ok("Patient phone no. was already up to date.")
                    else -> ResponseEntity.ok("Patient phone no. updated successfully.")
                }
            }
        }
    }

    fun updateEmail(patientPhNo: String, newPatientEmail: String): ResponseEntity<Any> {
//        if (genralUtil.isValidEmail(newPatientEmail)) {
//            if (!ifPatientEmailExists(newPatientEmail)) {
//                val query = Query(Criteria.where("patientPhNo").`is`(patientPhNo))
//                val update = Update().set("patientEmailId", newPatientEmail)
//                val result = mongoTemplate.updateFirst(query, update, PatientInfo::class.java)
//                return when {
//                    result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.")
//                    result.modifiedCount == 0L -> ResponseEntity.ok("Patient email was already up to date.")
//                    else -> ResponseEntity.ok("Patient email updated successfully.")
//                }
//            } else {
//                return ResponseEntity.badRequest().body("Given email exists")
//            }
//
//        } else {
//            return ResponseEntity.badRequest().body("Invalid email format ")
//        }

        when {
            !genralUtil.isValidEmail(newPatientEmail)->
                return ResponseEntity.badRequest().body("Invalid email format ")
            ifPatientEmailExists(newPatientEmail)->
                return ResponseEntity.badRequest().body("Given email exists")
            else->{
                val query = Query(Criteria.where("phno").`is`(patientPhNo))
                val update = Update().set("email", newPatientEmail)
                val result = mongoTemplate.updateFirst(query, update, PatientInfo::class.java)
                return when {
                    result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.")
                    result.modifiedCount == 0L -> ResponseEntity.ok("Patient email was already up to date.")
                    else -> ResponseEntity.ok("Patient email updated successfully.")
                }
            }
        }
    }

    fun save(patientInfoCreationDto: PatientInfoCreationDto): ResponseEntity<Any> {

        val gender = genralUtil.assignGender(patientInfoCreationDto.patientGenderCode)
        val bloodgroup = when (patientInfoCreationDto.patientBloodGroupCode) {
            1 -> BloodGroup.A_NEGATIVE
            2 -> BloodGroup.A_POSITIVE
            3 -> BloodGroup.AB_NEGATIVE
            4 -> BloodGroup.AB_POSITIVE
            5 -> BloodGroup.B_NEGATIVE
            6 -> BloodGroup.B_POSITIVE
            7 -> BloodGroup.O_NEGATIVE
            8 -> BloodGroup.O_POSITIVE
            else -> return ResponseEntity.badRequest()
                .body("Invalid status code. Use 1 (A-), 2 (A+), 3 (AB-), 4(AB+), 5(B-), 6(B+), 7(O-), 8(O+).")
        }

        when {
            !genralUtil.isValidEmail(patientInfoCreationDto.patientEmailId) ->
                return ResponseEntity.badRequest().body("Invalid email format")

            ifPatientEmailExists(patientInfoCreationDto.patientEmailId) ->
                return ResponseEntity.badRequest().body("Given email exists")

            !genralUtil.isValidPhoneNumber(patientInfoCreationDto.patientPhNo) ->
                return ResponseEntity.badRequest().body("Invalid Phone no format ")

            !ifPatientPhnoExists(patientInfoCreationDto.patientPhNo) ->
                return ResponseEntity.badRequest().body("Given phone no exists")

            !genralUtil.isValidPassword(
                patientInfoCreationDto.patientPassword,
                patientInfoCreationDto.patientConfirmPassword
            ) ->
                return ResponseEntity.badRequest().body("Invalid password format ")

            !genralUtil.isSamePassword(
                patientInfoCreationDto.patientPassword,
                patientInfoCreationDto.patientConfirmPassword
            ) ->
                return ResponseEntity.badRequest().body("Given passwords does not match")

            else -> {
                val patientInfo = PatientInfo(
                    patientPassword = patientInfoCreationDto.patientPassword,
                    patientName = patientInfoCreationDto.patientName,
                    email = patientInfoCreationDto.patientEmailId,
                    phno = patientInfoCreationDto.patientPhNo,
                    patientGender = gender,
                    patientBloodGroup = bloodgroup,
                    patientDob = patientInfoCreationDto.patientDob,
                )
                patientRepository.save(patientInfo)
                val userId=patientRepository.save(patientInfo).patientId
                val token = jwtUtil.generateToken(userId.toString())
                return ResponseEntity.ok(token)
            }
        }
    }


    /////general
    fun ifPatientEmailExists(email: String): Boolean {
        return patientRepository.existsByEmail(email)
    }

    fun ifPatientPhnoExists(phno: String): Boolean {
        return patientRepository.existsByPhno(phno)
    }

    fun updateMedicalConditions(patientPhNo: String, conditionId: ObjectId, newStatus: Boolean): ResponseEntity<Any> {
//        val query = Query(Criteria.where("patientId").`is`(ObjectId(patientId)))
//
//        val update = Update().set("existingMedicalConditions.$[elem].status", newStatus)
//
//        val updateOptions = UpdateOptions().arrayFilters(
//            listOf(
//                Document("elem.conditionId", ObjectId(conditionId))
//            )
//        )
//
//        val result: UpdateResult = mongoTemplate.updateFirst(query, update, updateOptions, PatientInfo::class.java, "Patient")
//
//        return when {
//            result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.")
//            result.modifiedCount == 0L -> ResponseEntity.ok("Condition found, but status was already set to $newStatus.")
//            else -> ResponseEntity.ok("Condition status updated to $newStatus successfully.")
//        }

        val db = mongoTemplate.db
        val collection = db.getCollection("Patient")

        val filter = Document("phno", patientPhNo)
        val update = Document("\$set", Document("existingMedicalConditions.\$[elem].status", newStatus))
        val updateOptions = UpdateOptions().arrayFilters(
            listOf(Document("elem.conditionId", conditionId))
        )

        val result = collection.updateOne(filter, update, updateOptions)

        return when {
            result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.")
            result.modifiedCount == 0L -> ResponseEntity.ok("Condition found, but status already set to $newStatus.")
            else -> ResponseEntity.ok("Condition status updated to $newStatus successfully.")
        }

    }



    ////// to be mapped to controller based on use case
//    fetches patients current medications
    fun getPatientMedications(phno: String): List<Medications> {
        val currentDateTime = LocalDateTime.now()
        val query2 = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("phno").`is`(phno)),
            Aggregation.unwind("doctorVisits"),
            Aggregation.unwind("doctorVisits.medications"),
            Aggregation.match(Criteria.where("doctorVisits.medications.endDate").gt(currentDateTime)),
            Aggregation.replaceRoot("doctorVisits.medications")
        )

        val result2: AggregationResults<Medications> =
            mongoTemplate.aggregate(query2, "Patient", Medications::class.java)

        return result2.mappedResults
    }

    fun getPatientMedicalConditions(patientPhNo: String): List<ExistingMedicalConditions>? {
        val aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("phno").`is`(patientPhNo)), // Filter by patient phone number
            Aggregation.unwind("existingMedicalConditions"),  // Flatten the reports array so that each report is a separate document
            Aggregation.replaceRoot("existingMedicalConditions") // Replace the root document with the reports field
        )

        val result: AggregationResults<ExistingMedicalConditions> =
            mongoTemplate.aggregate(aggregation, "Patient", ExistingMedicalConditions::class.java)

        return result.mappedResults
    }
}