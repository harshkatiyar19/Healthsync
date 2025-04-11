package project.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import project.entity.Organization

@Repository
interface OrganizationRepository: MongoRepository<Organization, ObjectId>{
    fun existsByEmail(organizationEmail:String): Boolean
    fun existsByPhno(organizationPhNo:String): Boolean
}