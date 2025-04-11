package project.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import project.service.PatientService

@RestController
@RequestMapping("/patientDashboard")
class PatientController(
    val patientService: PatientService
) {

    @GetMapping("/{patientPhNo}")
     fun findByPhNo(@PathVariable patientPhNo:String) : ResponseEntity<Any>
//     : PatientInfoDto?
          {
//        return patientService.getPatientInfo(patientPhNo)

        return try {
            val patientInfoDto = patientService.getPatientInfo(patientPhNo)
            if (patientInfoDto != null) {
                ResponseEntity.ok(patientInfoDto)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient with phone number $patientPhNo not found.")
            }
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while retrieving patient information")
        }
     }

    @GetMapping("/reports/{patientPhNo}")
    fun findReports(@PathVariable patientPhNo:String  ) : ResponseEntity<Any> {
//         patientService.getPatientReports(patientPhNo)
        return try {
            val patientReports = patientService.getPatientReports(patientPhNo)
            ResponseEntity.ok(patientReports)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while retrieving reports.}")
        }
    }

    @GetMapping("/drVisists/{patientPhNo}")
    fun findDrVisits(@PathVariable patientPhNo:String  )  : ResponseEntity<Any> {

        return try {
            val doctorVisits = patientService.getDoctorVisits(patientPhNo)
            ResponseEntity.ok(doctorVisits)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while retrieving doctor summary.")
        }
    }

    @GetMapping("/medicationHistory/{patientPhNo}")
    fun medicationHistory(@PathVariable patientPhNo:String  )
    : ResponseEntity<Any> {

        return try {
            val medicationHistory = patientService.getPatientMedicationsHistory(patientPhNo)
            ResponseEntity.ok(medicationHistory)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while retrieving medication history.")
        }
    }

//    @GetMapping("/existingMedicalConditions/{patientPhNo}")
//    fun existingMedicalConditions(@PathVariable patientPhNo:String  ) : List<ExistingMedicalConditions>? {
//        return patientService.getPatientMedicalConditions(patientPhNo)
//    }

}