package project.controller

import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import project.dto.patient.DoctorVisitsCreationDto
import project.service.PatientService

@RestController
@RequestMapping("/doctorDashboard")
class DoctorController(
    val patientService: PatientService
){
    @PutMapping("/updatePatient/{patientPhNo}")//change to id
    fun addNewDoctorVisit(@PathVariable patientPhNo:String,@RequestBody doctorVisitsCreationDto: DoctorVisitsCreationDto): ResponseEntity<Any> {
        return try {
            patientService.newDoctorVisit(patientPhNo,doctorVisitsCreationDto)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding new doctor visit.")
        }
    }

    @PutMapping("/addMedicalConditions/{patientPhNo}")//change to id
    fun addMedicalConditions(@PathVariable patientPhNo:String,@RequestParam medicalConditionName: String,@RequestParam doctorId: ObjectId): ResponseEntity<Any>{
        return try {
            patientService.newMedicalConditions(patientPhNo,medicalConditionName,doctorId)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding new medical condition.")
        }
    }


    @PatchMapping("/updateMedicalConditions/{patientPhNo}")//change to id
    fun updateMedicalConditions(@PathVariable patientPhNo:String,@RequestParam conditionId: ObjectId,@RequestParam status: Boolean): ResponseEntity<Any>{
        return try {
            patientService.updateMedicalConditions(patientPhNo,conditionId,status)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating medical condition.")
        }
    }

}