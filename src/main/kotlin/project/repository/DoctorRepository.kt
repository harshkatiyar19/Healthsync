package project.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import project.entity.DoctorInfo
import org.springframework.stereotype.Repository

@Repository
interface DoctorRepository : MongoRepository<DoctorInfo, ObjectId>{
    fun existsByEmail(doctorEmail:String): Boolean
    fun existsByPhno(doctorPhNo:String): Boolean
}