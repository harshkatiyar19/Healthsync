package project.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import project.dto.patient.ReportsCreationDto
import project.service.PatientService
import project.entity.Reports

@RestController
@RequestMapping("/labDashboard")
class LabController(
    val patientService: PatientService
){
    @PutMapping("/addReport/{patientPhNo}")
    fun addReport(@PathVariable patientPhNo:String, @RequestBody reportsCreationDto: ReportsCreationDto): ResponseEntity<Any> {
        return try {
            patientService.newReport(patientPhNo,reportsCreationDto)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding new medical condition.")
        }
    }
}