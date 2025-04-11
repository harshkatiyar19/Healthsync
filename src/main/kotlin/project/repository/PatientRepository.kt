package project.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import project.entity.PatientInfo
import org.springframework.stereotype.Repository

@Repository
interface PatientRepository : MongoRepository<PatientInfo, ObjectId>{
    fun existsByEmail(patientEmail:String): Boolean
    fun existsByPhno(patientPhNo:String): Boolean
}