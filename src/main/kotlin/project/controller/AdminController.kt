package project.controller

import jakarta.validation.Valid
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import project.dto.admin.AdminInfoCreationDto
import project.dto.doctor.DoctorInfoCreationDto
import project.dto.doctor.WorkplaceCreationDto
import project.dto.organization.OrganizationCreationDto
import project.dto.patient.PatientInfoCreationDto
import project.service.AdminService
import project.service.DoctorService
import project.service.OrganizationService
import project.service.PatientService

@RestController
@RequestMapping("/adminPanel")
class AdminController(
    val adminService: AdminService,
    val doctorService: DoctorService,
    val organizationService: OrganizationService,
    val patientService: PatientService
) {
    //doctors
    @PostMapping("/newDoctor")
    fun newDoctor(@Valid @RequestBody doctorInfoCreationDto: DoctorInfoCreationDto): ResponseEntity<Any> {
        return try {
            doctorService.save(doctorInfoCreationDto)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error making a new doctor details")
        }
    }


    @PatchMapping("/updateDoctorPhNo/{doctorPhNo}")
    fun updateDoctorPhNo(
        @PathVariable doctorPhNo: String,
        @Valid @RequestParam newDoctorPhNo: String
    ): ResponseEntity<Any> {
        return try {
            doctorService.updatePhNo(doctorPhNo, newDoctorPhNo)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating doctor Phone no:")
        }
    }

    @PatchMapping("/updateDoctorEmail/{doctorPhNo}")
    fun updateDoctorEmail(
        @PathVariable doctorPhNo: String,
        @Valid @RequestParam newDoctorEmail: String
    ): ResponseEntity<Any> {
        return try {
            doctorService.updateEmail(doctorPhNo, newDoctorEmail)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating doctor email:")
        }

    }

    @PatchMapping("/updateDoctorStatus/{doctorPhNo}")
    fun updateDoctorStatus(
        @PathVariable doctorPhNo: String,
        @Valid @RequestParam newDoctorStatusCode: Int
    ): ResponseEntity<Any> {
        return try {
            doctorService.updateStatus(doctorPhNo, newDoctorStatusCode)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating doctor status:")
        }
    }

    @PutMapping("/newDoctorWorkSpace/{doctorPhNo}")
    fun newDoctorWorkSpace(
        @PathVariable doctorPhNo: String,
        @Valid @RequestBody newDoctorWorkplace: WorkplaceCreationDto
    ): ResponseEntity<Any> {
        return try {
            doctorService.newWorkplace(doctorPhNo, newDoctorWorkplace)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating doctor status:")
        }
    }

    //patient

    @PostMapping("/newPatient")
    fun newPatient(@Valid @RequestBody patientInfoCreationDto: PatientInfoCreationDto): ResponseEntity<Any> {
        return try {
            patientService.save(patientInfoCreationDto)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error making a new patient details")
        }
    }

    @PatchMapping("/updatePatientPhNo/{patientPhNo}")
    fun updatePatientPhNo(
        @PathVariable patientPhNo: String,
        @Valid @RequestParam newPatientPhNo: String
    ): ResponseEntity<Any> {
        return try {
            patientService.updatePhNo(patientPhNo, newPatientPhNo)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating Patient email:")
        }
    }

    @PatchMapping("/updatePatientEmail/{patientPhNo}")
    fun updatePatientEmail(
        @PathVariable patientPhNo: String,
        @Valid @RequestParam newPatientEmail: String
    ): ResponseEntity<Any> {
        return try {
            patientService.updateEmail(patientPhNo, newPatientEmail)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating patient email:")
        }
    }

    //admin
    @PostMapping("/newAdmin")
    fun newAdmin(@Valid @RequestBody adminInfoCreationDto: AdminInfoCreationDto): ResponseEntity<Any> {
        return try {
            adminService.save(adminInfoCreationDto)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error making a new admin details")
        }
    }


    @PatchMapping("/updateAdminPhNo/{adminPhNo}")
    fun updateAdminPhNo(@PathVariable adminPhNo: String, @Valid @RequestParam newAdminPhNo: String): ResponseEntity<Any> {
        return try {
            adminService.updatePhNo(adminPhNo, newAdminPhNo)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating Admin Phone no:")
        }
    }


    @PatchMapping("/updateAdminEmail/{adminPhNo}")
    fun updateAdminEmail(@PathVariable adminPhNo: String, @Valid @RequestParam newAdminEmail: String): ResponseEntity<Any> {
        return try {
            adminService.updateEmail(adminPhNo, newAdminEmail)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating Admin email:")
        }
    }

    //organization

    @PostMapping("/newOrganization")
    fun newOrganization(@Valid @RequestBody organizationCreationDto: OrganizationCreationDto): ResponseEntity<Any> {
        return try {
            organizationService.save(organizationCreationDto)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error making a new Organization details")
        }
    }

    @PatchMapping("/updateOrganizationPhNo/{organizationPhNo}")
    fun updateOrganizationPhNo(@PathVariable organizationPhNo: String, @Valid @RequestParam newOrganizationPhNo: String): ResponseEntity<Any> {
        return try {
            organizationService.updatePhNo(organizationPhNo, newOrganizationPhNo)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating organization Phone no:")
        }
    }

    @PatchMapping("/updateOrganizationEmail/{organizationPhNo}")
    fun updateOrganizationEmail(@PathVariable organizationPhNo: String, @Valid @RequestParam newOrganizationEmail: String): ResponseEntity<Any> {
        return try {
            organizationService.updateEmail(organizationPhNo, newOrganizationEmail)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating organization email:")
        }
    }
///////////////////////pending apis

    @PatchMapping("/leaveDoctorWorkSpace/{doctorPhNo}")
    fun leaveDoctorWorkSpace(@PathVariable doctorPhNo: String, @Valid @RequestParam workplaceId: ObjectId,@RequestParam  status:Boolean) : ResponseEntity<Any>{

        return try {
            doctorService.leaveWorkplace(doctorPhNo, workplaceId, status)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating workplace status")
        }
    }

    @PatchMapping("/updateDoctorWorkSpaceDepartment/{doctorPhNo}")
    fun updateDoctorWorkSpaceDepartment(
        @PathVariable doctorPhNo: String,
        @RequestParam workplaceId: ObjectId,
        @RequestParam newWorkplaceDepartment: String,
        @RequestParam newWorkplaceDesignation: String
    ) : ResponseEntity<Any>{
        return try {
            doctorService.updateWorkplaceDepartment(doctorPhNo, workplaceId, newWorkplaceDepartment)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating workplace designation")
        }
    }

    @PatchMapping("/updateDoctorWorkSpaceDesignation/{doctorPhNo}")
    fun updateDoctorWorkSpaceDesignation(
        @PathVariable doctorPhNo: String,
        @RequestParam workplaceId: ObjectId,
        @RequestParam newWorkplaceDesignation: String
    ): ResponseEntity<Any> {

        return try {
            doctorService.updateWorkplaceDesignation(doctorPhNo, workplaceId, newWorkplaceDesignation)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating workplace designation")
        }
    }


}