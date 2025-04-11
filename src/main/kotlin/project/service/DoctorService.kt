package project.service

import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import project.dto.doctor.DoctorInfoCreationDto
import project.dto.doctor.WorkplaceCreationDto
import project.entity.DoctorInfo
import project.entity.DoctorStatus
import project.entity.Workplace
import project.repository.DoctorRepository
import project.repository.OrganizationRepository
import project.util.GenralUtil

@Service
class DoctorService(
    val mongoTemplate: MongoTemplate,
    val organizationRepository: OrganizationRepository,
    val doctorRepository: DoctorRepository,
    val genralUtil: GenralUtil
) {
    fun updatePhNo(doctorPhNo: String, newDoctorPhNo: String): ResponseEntity<Any> {
//        if (genralUtil.isValidPhoneNumber(newDoctorPhNo)){
//            if(!ifDoctorPhnoExists(newDoctorPhNo)){
//                val query = Query(Criteria.where("doctorPhNo").`is`(doctorPhNo))
//                val update = Update().set("doctorPhNo", newDoctorPhNo)
//                val result=mongoTemplate.updateFirst(query, update, DoctorInfo::class.java)
//                return when {
//                    result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found.")
//                    result.modifiedCount == 0L -> ResponseEntity.ok("Doctor phone no. was already up to date.")
//                    else -> ResponseEntity.ok("Doctor phone no. updated successfully.")
//                }
//            }
//            else{
//                return ResponseEntity.badRequest().body("Given phone no exists")
//            }
//        }
//        else{
//            return ResponseEntity.badRequest().body("Invalid Phone no format")
//        }
        when {
            !genralUtil.isValidPhoneNumber(newDoctorPhNo) ->
                return ResponseEntity.badRequest().body("Invalid Phone no format")

            ifDoctorPhnoExists(newDoctorPhNo) ->
                return ResponseEntity.badRequest().body("Given phone no exists")

            else -> {
                val query = Query(Criteria.where("phno").`is`(doctorPhNo))
                val update = Update().set("phno", newDoctorPhNo)
                val result = mongoTemplate.updateFirst(query, update, DoctorInfo::class.java)
                return when {
                    result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found.")
                    result.modifiedCount == 0L -> ResponseEntity.ok("Doctor phone no. was already up to date.")
                    else -> ResponseEntity.ok("Doctor phone no. updated successfully.")
                }
            }
        }
    }

    fun updateEmail(doctorPhNo: String, newDoctorEmail: String): ResponseEntity<Any> {
        when{
            !genralUtil.isValidEmail(newDoctorEmail)->
                return ResponseEntity.badRequest().body("Invalid email format.")
            ifDoctorEmailExists(newDoctorEmail)->
                return ResponseEntity.badRequest().body("Given email exists.")
            else->{
                val query = Query(Criteria.where("phno").`is`(doctorPhNo))
                val update = Update().set("email", newDoctorEmail)
                val result = mongoTemplate.updateFirst(query, update, DoctorInfo::class.java)
                return when {
                    result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found.")
                    result.modifiedCount == 0L -> ResponseEntity.ok("Doctor email was already up to date.")
                    else -> ResponseEntity.ok("Doctor email updated successfully.")
                }
            }
        }
//        if (genralUtil.isValidEmail(newDoctorEmail)) {
//            if (!ifDoctorEmailExists(newDoctorEmail)) {
//                val query = Query(Criteria.where("doctorPhNo").`is`(doctorPhNo))
//                val update = Update().set("doctorEmail", newDoctorEmail)
//                val result = mongoTemplate.updateFirst(query, update, DoctorInfo::class.java)
//                return when {
//                    result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found.")
//                    result.modifiedCount == 0L -> ResponseEntity.ok("Doctor email was already up to date.")
//                    else -> ResponseEntity.ok("Doctor email updated successfully.")
//                }
//            } else {
//                return ResponseEntity.badRequest().body("Given email exists.")
//            }
//        } else {
//            return ResponseEntity.badRequest().body("Invalid email format.")
//        }
    }

    fun updateStatus(doctorPhNo: String, newDoctorStatusCode: Int): ResponseEntity<Any> {
        val newDoctorStatus = when (newDoctorStatusCode) {
            1 -> DoctorStatus.ACTIVE
            2 -> DoctorStatus.SUSPENDED
            3 -> DoctorStatus.DEACTIVE
            else -> return ResponseEntity.badRequest()
                .body("Invalid status code. Use 1 (ACTIVE), 2 (SUSPENDED), or 3 (DEACTIVE).")
        }

        val query = Query(Criteria.where("phno").`is`(doctorPhNo))
        val update = Update().set("doctorStatus", newDoctorStatus)
        val result = mongoTemplate.updateFirst(query, update, DoctorInfo::class.java)
        return when {
            result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found.")
            result.modifiedCount == 0L -> ResponseEntity.ok("Doctor status was already up to date.")
            else -> ResponseEntity.ok("Doctor status updated successfully.")
        }
    }

    fun newWorkplace(doctorPhNo: String, newDoctorWorkplace: WorkplaceCreationDto): ResponseEntity<Any> {
        val workplaceDetails = organizationRepository.findById(newDoctorWorkplace.organizationId).orElse(null)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Organization not found")

        val workplace = Workplace(
            workplaceDetails = workplaceDetails,
            workplaceDepartment = newDoctorWorkplace.workplaceDepartment,
            workplaceDesignation = newDoctorWorkplace.workplaceDesignation,
            workplaceJoiningDate = newDoctorWorkplace.workplaceJoiningDate,
            workplaceLeavingDate = newDoctorWorkplace.workplaceLeavingDate,
            workplaceStatus = newDoctorWorkplace.workplaceStatus
        )

        val query = Query(Criteria.where("phno").`is`(doctorPhNo)).apply {
            fields().include("doctorWorkHistory")
        }
        val update = Update().push("doctorWorkHistory", workplace)

        val result = mongoTemplate.updateFirst(query, update, DoctorInfo::class.java, "Doctor")

        return when {
            result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found.")
            result.modifiedCount == 0L -> ResponseEntity.ok("Doctor new workplace was already up to date.")
            else -> ResponseEntity.ok("New Workplace Added successfully.")
        }

    }


    fun save(doctorInfoCreationDto: DoctorInfoCreationDto): ResponseEntity<Any> {

        val gender = genralUtil.assignGender(doctorInfoCreationDto.doctorGenderCode)

        val status = when (doctorInfoCreationDto.doctorStatusCode) {
            1 -> DoctorStatus.DEACTIVE
            2 -> DoctorStatus.SUSPENDED
            3 -> DoctorStatus.ACTIVE
            else -> {
                return ResponseEntity.badRequest()
                    .body("Invalid status code. Use 1 (ACTIVE), 2 (SUSPENDED), or 3 (DEACTIVE).")
            }
        }

        when {
            !genralUtil.isValidPassword(
                doctorInfoCreationDto.doctorPassword,
                doctorInfoCreationDto.doctorConfirmPassword
            ) ->
                return ResponseEntity.badRequest().body("Invalid password format ")

            !genralUtil.isSamePassword(
                doctorInfoCreationDto.doctorPassword,
                doctorInfoCreationDto.doctorConfirmPassword
            ) ->
                return ResponseEntity.badRequest().body("Given passwords does not match")

            !genralUtil.isValidEmail(doctorInfoCreationDto.doctorEmail) ->
                return ResponseEntity.badRequest().body("Invalid email format")

            ifDoctorEmailExists(doctorInfoCreationDto.doctorEmail) ->
                return ResponseEntity.badRequest().body("Given email exists")

            !genralUtil.isValidPhoneNumber(doctorInfoCreationDto.doctorPhNo) ->
                return ResponseEntity.badRequest().body("Invalid Phone no format ")

            ifDoctorPhnoExists(doctorInfoCreationDto.doctorPhNo) ->
                return ResponseEntity.badRequest().body("Given phone no exists")

            else -> {
                val doctorInfo = DoctorInfo(
                    doctorPassword = doctorInfoCreationDto.doctorPassword,
                    doctorName = doctorInfoCreationDto.doctorName,
                    email = doctorInfoCreationDto.doctorEmail,
                    phno = doctorInfoCreationDto.doctorPhNo,
                    doctorGender = gender,
                    doctorDob = doctorInfoCreationDto.doctorDob,
                    doctorStatus = status
                )
                return ResponseEntity.ok(doctorRepository.save(doctorInfo))
            }
        }

//        if(genralUtil.isValidPassword(doctorInfoCreationDto.doctorPassword,doctorInfoCreationDto.doctorConfirmPassword)){
//            if(genralUtil.isSamePassword(doctorInfoCreationDto.doctorPassword,doctorInfoCreationDto.doctorConfirmPassword)){
//                password= genralUtil.passwordEncode(doctorInfoCreationDto.doctorPassword).toString()
//            }
//            else{
//                return ResponseEntity.badRequest().body("Given passwords does not match")
//            }
//        }
//        else{
//            return ResponseEntity.badRequest().body("Invalid password format ")
//        }

//        if(genralUtil.isValidEmail(doctorInfoCreationDto.doctorEmail)){
//            if(!ifDoctorEmailExists(doctorInfoCreationDto.doctorEmail)){
//                email=doctorInfoCreationDto.doctorEmail
//            }
//            else{
//                return ResponseEntity.badRequest().body("Given email exists")
//            }
//        }
//        else{
//            return ResponseEntity.badRequest().body("Invalid email format")
//        }

//        if(genralUtil.isValidPhoneNumber(doctorInfoCreationDto.doctorPhNo)){
//            if(!ifDoctorPhnoExists(doctorInfoCreationDto.doctorPhNo)){
//                phno=doctorInfoCreationDto.doctorPhNo
//            }
//            else{
//                return ResponseEntity.badRequest().body("Given phone no exists")
//            }
//        }
//        else{
//            return ResponseEntity.badRequest().body("Invalid Phone no format ")
//        }


//       val doctorInfo=DoctorInfo(
//           doctorPassword = password,
//           doctorName = doctorInfoCreationDto.doctorName,
//           doctorEmail = email,
//           doctorPhNo = phno,
//           doctorGender = gender,
//           doctorDob = doctorInfoCreationDto.doctorDob,
//           doctorStatus = status
//       )
//        return ResponseEntity.ok(doctorRepository.save(doctorInfo))
    }

    fun ifDoctorEmailExists(email: String): Boolean {
        return doctorRepository.existsByEmail(email)
    }

    fun ifDoctorPhnoExists(phno: String): Boolean {
        return doctorRepository.existsByPhno(phno)
    }

//    private fun assignStatus(doctorStatusCode: Int): Any {
//        return when (doctorStatusCode) {
//            1 -> DoctorStatus.ACTIVE
//            2 -> DoctorStatus.SUSPENDED
//            3 -> DoctorStatus.DEACTIVE
//            else -> ResponseEntity.badRequest().body("Invalid status code. Use 1 (ACTIVE), 2 (SUSPENDED), or 3 (DEACTIVE).")
//        }
//    }

    ////////pending apis
    fun leaveWorkplace(doctorPhNo: String, workplaceId: ObjectId, newStatus: Boolean):ResponseEntity<Any> {
        val db = mongoTemplate.db
        val collection = db.getCollection("Doctor")

        val filter = Document("phno", doctorPhNo)

        val update = Document("\$set", Document("doctorWorkHistory.\$[elem].workplaceStatus", newStatus))
        val updateOptions = UpdateOptions().arrayFilters(
            listOf(Document("elem.workplaceId", workplaceId))
        )

        val result = collection.updateOne(filter, update, updateOptions)

        return when {
            result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found.")
            result.modifiedCount == 0L -> ResponseEntity.ok("workplace status already set. ")
            else -> ResponseEntity.ok("workplace status updated to successfully.")
        }
    }

    fun updateWorkplaceDepartment(
        doctorPhNo: String,
        workplaceId: ObjectId,
        newWorkplaceDepartment: String
    ) :ResponseEntity<Any>{
        val db = mongoTemplate.db
        val collection = db.getCollection("Doctor")

        val filter = Document("phno", doctorPhNo)

        val update = Document("\$set", Document("doctorWorkHistory.\$[elem].workplaceDepartment", newWorkplaceDepartment))
        val updateOptions = UpdateOptions().arrayFilters(
            listOf(Document("elem.workplaceId", workplaceId))
        )

        val result = collection.updateOne(filter, update, updateOptions)

        return when {
            result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.")
            result.modifiedCount == 0L -> ResponseEntity.ok("Condition found, but status already set to ")
            else -> ResponseEntity.ok("Condition status updated to successfully.")
        }
    }

    fun updateWorkplaceDesignation(doctorPhNo: String, workplaceId: ObjectId, newWorkplaceDesignation: String): ResponseEntity<Any> {
        val db = mongoTemplate.db
        val collection = db.getCollection("Doctor")

        val filter = Document("phno", doctorPhNo)

        val update = Document("\$set", Document("doctorWorkHistory.\$[elem].workplaceDesignation", newWorkplaceDesignation))
        val updateOptions = UpdateOptions().arrayFilters(
            listOf(Document("elem.workplaceId", workplaceId))
        )

        val result = collection.updateOne(filter, update, updateOptions)

        return when {
            result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found.")
            result.modifiedCount == 0L -> ResponseEntity.ok("Condition found, but status already set to ")
            else -> ResponseEntity.ok("Condition status updated to successfully.")
        }
    }
}