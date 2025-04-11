package project.service

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import project.dto.organization.OrganizationCreationDto
import project.entity.Organization
import project.entity.OrganizationType
import project.repository.OrganizationRepository
import project.util.GenralUtil

@Service
class OrganizationService(val mongoTemplate: MongoTemplate,val genralUtil: GenralUtil,
                          val organizationRepository: OrganizationRepository
) {
    fun updatePhNo(organizationPhNo: String, newOrganizationPhNo: String): ResponseEntity<Any> {
//        if (genralUtil.isValidPhoneNumber(newOrganizationPhNo)){
//            if(!ifOrganizationPhnoExists(newOrganizationPhNo)){
//                val query = Query(Criteria.where("organizationPhNo").`is`(organizationPhNo))
//                val update = Update().set("organizationPhNo", newOrganizationPhNo)
//                val result=mongoTemplate.updateFirst(query, update, Organization::class.java)
//                return when {
//                    result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Organization not found.")
//                    result.modifiedCount == 0L -> ResponseEntity.ok("Organization phone no. was already up to date.")
//                    else -> ResponseEntity.ok("Organization phone no. updated successfully.")
//                }
//            }
//            else{
//                return ResponseEntity.badRequest().body("Given phone no exists")
//            }
//
//        } else {
//            return  ResponseEntity.badRequest().body("Invalid Phone no format ")
//        }

        when{
            !genralUtil.isValidPhoneNumber(newOrganizationPhNo)->
                return  ResponseEntity.badRequest().body("Invalid Phone no format ")
            ifOrganizationPhnoExists(newOrganizationPhNo)->
                return ResponseEntity.badRequest().body("Given phone no exists")
            else->{
                val query = Query(Criteria.where("phno").`is`(organizationPhNo))
                val update = Update().set("phno", newOrganizationPhNo)
                val result=mongoTemplate.updateFirst(query, update, Organization::class.java)
                return when {
                    result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Organization not found.")
                    result.modifiedCount == 0L -> ResponseEntity.ok("Organization phone no. was already up to date.")
                    else -> ResponseEntity.ok("Organization phone no. updated successfully.")
                }
            }
        }

    }

    fun updateEmail(organizationPhNo: String, newOrganizationEmail: String):ResponseEntity<Any> {
//        if (genralUtil.isValidEmail(newOrganizationEmail)){
//            if(!ifOrganizationEmailExists(newOrganizationEmail)){
//                val query = Query(Criteria.where("organizationPhNo").`is`(organizationPhNo))
//                val update = Update().set("organizationEmail", newOrganizationEmail)
//                val result=mongoTemplate.updateFirst(query, update, Organization::class.java)
//                return when {
//                    result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Organization not found.")
//                    result.modifiedCount == 0L -> ResponseEntity.ok("Organization email was already up to date.")
//                    else -> ResponseEntity.ok("Organization email updated successfully.")
//                }
//            }
//            else{
//                return ResponseEntity.badRequest().body("Given email exists")
//            }
//
//        } else {
//            return  ResponseEntity.badRequest().body("Invalid email format ")
//        }

        when{
            !genralUtil.isValidEmail(newOrganizationEmail)->
                return  ResponseEntity.badRequest().body("Invalid email format ")
            ifOrganizationEmailExists(newOrganizationEmail)->
                return ResponseEntity.badRequest().body("Given email exists")
            else->{
                val query = Query(Criteria.where("phno").`is`(organizationPhNo))
                val update = Update().set("email", newOrganizationEmail)
                val result=mongoTemplate.updateFirst(query, update, Organization::class.java)
                return when {
                    result.matchedCount == 0L -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Organization not found.")
                    result.modifiedCount == 0L -> ResponseEntity.ok("Organization email was already up to date.")
                    else -> ResponseEntity.ok("Organization email updated successfully.")
                }
            }
        }
        }



    fun save(organizationCreationDto: OrganizationCreationDto) :ResponseEntity<Any> {

//        lateinit var email:String
//        lateinit var phno:String

        val organizationType= when(organizationCreationDto.organizationTypeCode){
            1-> OrganizationType.LAB
            2-> OrganizationType.HOSPITAL
            3->OrganizationType.CLINIC
            else ->
                return ResponseEntity.badRequest().body("Invalid status code. Use 1 (LAB), 2 (HOSPITAL), 3 (CLINIC).")
        }


        when{
            !genralUtil.isValidEmail(organizationCreationDto.organizationEmail)->
                return ResponseEntity.badRequest().body("Invalid email format")
            ifOrganizationEmailExists(organizationCreationDto.organizationEmail)->
                return ResponseEntity.badRequest().body("Given email exists")
            !genralUtil.isValidPhoneNumber(organizationCreationDto.organizationPhNo)->
                return ResponseEntity.badRequest().body("Invalid Phone no format ")
            ifOrganizationPhnoExists(organizationCreationDto.organizationPhNo)->
                return ResponseEntity.badRequest().body("Given phone no exists")
            else->{
                val organizationInfo=Organization(
                    organizationType = organizationType,
                    organizationName = organizationCreationDto.organizationName,
                    email = organizationCreationDto.organizationEmail,
                    phno = organizationCreationDto.organizationPhNo,
                    organizationAddress = organizationCreationDto.organizationAddress
                )
                return ResponseEntity.ok( organizationRepository.save(organizationInfo))
            }
        }



//        if(genralUtil.isValidEmail(organizationCreationDto.organizationEmail)){
//            if(!ifOrganizationEmailExists(organizationCreationDto.organizationEmail)){
//                email=organizationCreationDto.organizationEmail
//            }
//            else{
//                return ResponseEntity.badRequest().body("Given email exists")
//            }
//        }
//        else{
//            return ResponseEntity.badRequest().body("Invalid email format")
//        }

//        if(genralUtil.isValidPhoneNumber(organizationCreationDto.organizationPhNo)){
//            if(!ifOrganizationPhnoExists(organizationCreationDto.organizationPhNo)){
//                phno=organizationCreationDto.organizationPhNo
//            }
//            else{
//                return ResponseEntity.badRequest().body("Given phone no exists")
//            }
//        }
//        else{
//            return ResponseEntity.badRequest().body("Invalid Phone no format ")
//        }


//        val organizationInfo=Organization(
//           organizationType = organizationType,
//           organizationName = organizationCreationDto.organizationName,
//           organizationEmail = email,
//           organizationPhNo = phno,
//           organizationAddress = organizationCreationDto.organizationAddress
//       )
//       return ResponseEntity.ok( organizationRepository.save(organizationInfo))
    }

    fun ifOrganizationEmailExists(email: String): Boolean {
        return   organizationRepository.existsByEmail(email)
    }

    fun ifOrganizationPhnoExists(phno: String): Boolean {
        return   organizationRepository.existsByPhno(phno)
    }

//    fun assignOrganizationType(organizationTypeCode:Int): OrganizationType {
//        val assign: OrganizationType
//        return assign
//    }
}