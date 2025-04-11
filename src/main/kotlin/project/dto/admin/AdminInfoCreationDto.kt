package project.dto.admin

import java.util.*

data class AdminInfoCreationDto(
    val adminPassword:String,
    val adminConfirmPassword:String,
    val adminName:String,
    val adminEmail:String,
    val adminPhNo:String,
    val adminGenderCode: Int,
    val adminDob: Date
)
