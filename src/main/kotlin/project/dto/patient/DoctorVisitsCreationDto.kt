package project.dto.patient

import org.bson.types.ObjectId
import project.entity.DoctorInfo
import project.entity.Medications

data class DoctorVisitsCreationDto (

    val doctorId:ObjectId,
    val weight:Double,
    val height:Double,
    val remarks:List<String>,
    val medicalConditionName: List<String>,
    val symptoms :List<String>,
    val tests:List<String>,
    val medications:List<Medications>
)
