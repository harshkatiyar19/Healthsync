package project.entity

import jakarta.annotation.Nonnull
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document(collection = "Doctor")
data class DoctorInfo(
    @Id
    val doctorId:ObjectId?=ObjectId.get(),
    val doctorPassword:String,
    @Nonnull
    val doctorName:String,
    @Nonnull
    @Indexed(unique = true)
    val email:String,
    @Nonnull
    @Indexed(unique = true)
    val phno: String,
    val doctorGender: Gender,
    val doctorDob: Date,
    val doctorStatus: DoctorStatus,
    val doctorWorkHistory:List<Workplace>?= emptyList()
)

data class Workplace(
    val workId:ObjectId=ObjectId.get(),
    @DBRef
    val workplaceDetails: Organization,
    @Nonnull
    val workplaceDepartment:String,
    @Nonnull
    val workplaceDesignation:String,
    @CreatedDate
    val workplaceJoiningDate: LocalDateTime?=null,
    val workplaceLeavingDate: LocalDateTime?=null,
    @Nonnull
    val workplaceStatus:Boolean
)

enum class DoctorStatus {ACTIVE,SUSPENDED,DEACTIVE}