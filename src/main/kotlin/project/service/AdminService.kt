package project.service

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import project.dto.admin.AdminInfoCreationDto
import project.entity.AdminInfo
import project.repository.AdminRepository
import project.util.GenralUtil

@Service
class AdminService(val mongoTemplate: MongoTemplate, val genralUtil: GenralUtil, val adminRepository: AdminRepository) {

    fun updatePhNo(adminPhNo: String, newAdminPhNo: String): ResponseEntity<Any> {
        if (genralUtil.isValidPhoneNumber(newAdminPhNo)) {
            if (!ifAdminPhnoExists(newAdminPhNo)) {
                val query = Query(Criteria.where("phno").`is`(adminPhNo))
                val update = Update().set("phno", newAdminPhNo)
                val result = mongoTemplate.updateFirst(query, update, AdminInfo::class.java)
                return when {
                    result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found.")
                    result.modifiedCount == 0L -> ResponseEntity.ok("Admin phone no. was already up to date.")
                    else -> ResponseEntity.ok("Admin phone no. updated successfully.")
                }
            }
            else {
                return ResponseEntity.badRequest().body("Given phone no exists")
            }
        }
        else {
            return ResponseEntity.badRequest().body("Invalid Phone no format ")
        }
    }

    fun updateEmail(adminPhNo: String, newAdminEmail: String):ResponseEntity<Any> {
        if (genralUtil.isValidEmail(newAdminEmail)){
            if(!ifAdminEmailExists(newAdminEmail)){
                val query = Query(Criteria.where("phno").`is`(adminPhNo))
                val update = Update().set("email", newAdminEmail)
                val result=mongoTemplate.updateFirst(query, update, AdminInfo::class.java)
                return when {
                    result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found.")
                    result.modifiedCount == 0L -> ResponseEntity.ok("Admin email was already up to date.")
                    else -> ResponseEntity.ok("Admin email updated successfully.")
                }
            }
            else{
                return ResponseEntity.badRequest().body("Given email exists")
            }

        }
        else {
                return ResponseEntity.badRequest().body("Invalid email format ")
        }
    }


    fun save(adminInfoCreationDto: AdminInfoCreationDto): ResponseEntity<Any> {

        val gender=genralUtil.assignGender(adminInfoCreationDto.adminGenderCode)

        when{
            !genralUtil.isValidEmail(adminInfoCreationDto.adminEmail)->
                return ResponseEntity.badRequest().body("Invalid email format")
            ifAdminEmailExists(adminInfoCreationDto.adminEmail)->
                return ResponseEntity.badRequest().body("Given email exists")
            !genralUtil.isValidPhoneNumber(adminInfoCreationDto.adminPhNo)->
                return ResponseEntity.badRequest().body("Invalid Phone no format")
            ifAdminPhnoExists(adminInfoCreationDto.adminPhNo)->
                return ResponseEntity.badRequest().body("Given phone no exists")
            !genralUtil.isValidPassword(adminInfoCreationDto.adminPassword,adminInfoCreationDto.adminConfirmPassword)->
                return ResponseEntity.badRequest().body("Invalid password format ")
            genralUtil.isSamePassword(adminInfoCreationDto.adminPassword,adminInfoCreationDto.adminConfirmPassword)->
                return ResponseEntity.badRequest().body("Given passwords does not match")
            else->{
                val adminInfo=AdminInfo(
                    adminPassword = adminInfoCreationDto.adminPassword,
                    adminName = adminInfoCreationDto.adminName,
                    email = adminInfoCreationDto.adminEmail,
                    phno = adminInfoCreationDto.adminPhNo,
                    adminGender = gender,
                    adminDob = adminInfoCreationDto.adminDob
                )
                return ResponseEntity.ok(adminRepository.save(adminInfo))
            }
        }
//        if(genralUtil.isValidEmail(adminInfoCreationDto.adminEmail)){
//            if(!ifAdminEmailExists(adminInfoCreationDto.adminEmail)){
//                email=adminInfoCreationDto.adminEmail
//            }
//            else{
//                return ResponseEntity.badRequest().body("Given email exists")
//            }
//        }
//        else{
//            return ResponseEntity.badRequest().body("Invalid email format")
//        }

//        if(genralUtil.isValidPhoneNumber(adminInfoCreationDto.adminPhNo)){
//            if(!ifAdminPhnoExists(adminInfoCreationDto.adminPhNo)){
//                phno=adminInfoCreationDto.adminPhNo
//            }
//            else{
//                return ResponseEntity.badRequest().body("Given phone no exists")
//            }
//        }
//        else{
//            return ResponseEntity.badRequest().body("Invalid Phone no format ")
//        }

//        if(genralUtil.isValidPassword(adminInfoCreationDto.adminPassword,adminInfoCreationDto.adminConfirmPassword)){
//            if(genralUtil.isSamePassword(adminInfoCreationDto.adminPassword,adminInfoCreationDto.adminConfirmPassword)){
//                password= genralUtil.passwordEncode(adminInfoCreationDto.adminPassword).toString()
//            }
//            else{
//                return ResponseEntity.badRequest().body("Given passwords does not match")
//            }
//        }
//        else{
//            return ResponseEntity.badRequest().body("Invalid password format ")
//        }
//
//
//        val adminInfo=AdminInfo(
//            adminPassword = password,
//            adminName = adminInfoCreationDto.adminName,
//            adminEmail = email,
//            adminPhNo = phno,
//            adminGender = gender,
//            adminDob = adminInfoCreationDto.adminDob
//        )
//        return ResponseEntity.ok(adminRepository.save(adminInfo))
    }

    //admin
    fun ifAdminEmailExists(email: String): Boolean {
        return   adminRepository.existsByEmail(email)
    }

    fun ifAdminPhnoExists(phno: String): Boolean {
        return   adminRepository.existsByPhno(phno)
    }
}