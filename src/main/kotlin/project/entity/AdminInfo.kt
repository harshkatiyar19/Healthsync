package project.entity

import jakarta.annotation.Nonnull
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection="Admin")
data class AdminInfo(
    @Id
    val adminId:ObjectId= ObjectId.get(),
    val adminPassword:String,
    @Nonnull
    val adminName:String,
    @Nonnull
    @Indexed(unique = true)
    val email:String,
    @Nonnull
    @Indexed(unique = true)
    val phno:String,
    val adminGender: Gender,
    val adminDob: Date
    )

