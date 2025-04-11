package project.entity

import jakarta.annotation.Nonnull
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection="OrganizationDetails")
data class Organization(
    @Id
    val organizationId: ObjectId?=ObjectId.get(),
    val organizationType:OrganizationType,
    @Nonnull
    @Indexed(unique = true)
    val organizationName:String,
    @Nonnull
    @Indexed(unique = true)
    val email:String,
    @Nonnull
    @Indexed(unique = true)
    val phno:String,
    @Nonnull
    val organizationAddress:String,
)

enum class OrganizationType{LAB,HOSPITAL,CLINIC}