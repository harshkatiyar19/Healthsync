package project.dto.doctor

import java.util.*

data class DoctorInfoCreationDto(

    val doctorPassword:String,
    val doctorConfirmPassword:String,
    val doctorName:String,
    val doctorEmail:String,
    val doctorPhNo: String,
    val doctorGenderCode: Int,
    val doctorDob: Date,
    val doctorStatusCode: Int
)
