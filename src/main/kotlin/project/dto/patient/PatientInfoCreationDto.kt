package project.dto.patient

import java.util.*

data class PatientInfoCreationDto(
    val patientPassword: String,
    val patientConfirmPassword:String,
    val patientName:String,
    val patientEmailId: String,
    val patientPhNo:String,
    val patientGenderCode:Int ,
    val patientBloodGroupCode: Int,
    val patientDob: Date,
)