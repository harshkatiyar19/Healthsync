package project.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import project.entity.AdminInfo
import org.springframework.stereotype.Repository

@Repository
interface AdminRepository: MongoRepository<AdminInfo, ObjectId>{
    fun existsByEmail(adminEmail:String): Boolean
    fun existsByPhno(adminPhNo:String): Boolean
}