package project.entity

import jakarta.annotation.Nonnull
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document(collection = "Patient")
data class PatientInfo(
    @Id
    val patientId: ObjectId?=ObjectId.get(),
    @Nonnull
    val patientPassword: String,
    @Nonnull
    val patientName:String,
    @Nonnull
    @Indexed(unique = true)
    val email: String,
    @Nonnull
    @Indexed(unique = true)
    val phno:String,
    @Nonnull
    val patientGender:Gender,
    val patientBloodGroup: BloodGroup,
    val patientDob: Date,
    val existingMedicalConditions: List<ExistingMedicalConditions>? = emptyList(),
    val doctorVisits:List<DoctorVisits>? = emptyList(),
    val reports:List<Reports>? = emptyList()
)

data class ExistingMedicalConditions(
    val conditionId:ObjectId= ObjectId.get(),
    @DBRef
    val doctor:DoctorInfo,
    @CreatedDate
    val timestamp: LocalDateTime? = null,
    val medicalConditionName: String,
    val status:Boolean?=true
)

data class DoctorVisits(
    val visitId:ObjectId= ObjectId.get(),
    @DBRef
    val doctor: DoctorInfo,
    @CreatedDate
    val timestamp: LocalDateTime?=null,
    val weight:Double,
    val height:Double,
    val remarks:List<String>,
    val medicalConditionName: List<String>,
    val symptoms :List<String>,
    val tests:List<String>,
    val medications:List<Medications>
)

data class Medications(
    @CreatedDate
    val timestamp:LocalDateTime?=null,
    val medicationName:String,
    val dose:String,
    val endDate:LocalDateTime,
    val doseTime:List<DoseTime>
)

data class DoseTime(
    val doseTimeSlot: DoseTimeSlot,
    val doseRemark:String
)

data class Reports(
    val reportId:ObjectId= ObjectId.get(),
    @DBRef
    val organizationId:Organization?,
    @CreatedDate
    val timestamp: LocalDateTime? = null,
    val reportName:String,
    val report:String
)

enum class Gender {MALE,FEMALE,TRANSGENDER}

enum class DoseTimeSlot {MORNING,NOON,EVENING,NIGHT}

enum class BloodGroup(val displayName: String) {
    O_POSITIVE("O+"),
    O_NEGATIVE("O-"),
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-");

    override fun toString(): String { return displayName }
}