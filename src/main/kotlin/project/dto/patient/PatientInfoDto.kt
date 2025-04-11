package project.dto.patient

import org.bson.types.ObjectId
import project.entity.BloodGroup
import project.entity.ExistingMedicalConditions
import project.entity.Gender
import project.entity.Medications
import java.util.*


data class PatientInfoDto(
    val patientId: ObjectId?,
    val patientName:String,
    val patientEmailId: String,
    val patientPhNo:String,
    val patientGender: Gender,
    val patientBloodGroup: BloodGroup,
    val patientDob:Date,
    val existingMedicalConditions:List<ExistingMedicalConditions>? = emptyList(),
    val existingMeds:List<Medications>? = emptyList()
//    val doctorVisits:List<DoctorVisits>? = emptyList(),
//    val reports:List<Reports>? = emptyList()
)