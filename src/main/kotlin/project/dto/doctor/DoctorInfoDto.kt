package project.dto.doctor

import org.bson.types.ObjectId
import project.entity.DoctorStatus
import project.entity.Gender
import project.entity.Workplace
import java.util.*

data class DoctorInfoDto(
    val doctorId: ObjectId,
    val doctorName:String,
    val doctorEmail:String,
    val doctorPhNo: String,
    val doctorGender: Gender,
    val doctorDob: Date,
    val doctorStatus: DoctorStatus,
    val doctorWorkHistory:List<Workplace>?= emptyList()
)
