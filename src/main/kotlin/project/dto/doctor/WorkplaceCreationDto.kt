package project.dto.doctor

import org.bson.types.ObjectId
import java.time.LocalDateTime

data class WorkplaceCreationDto (
    val organizationId: ObjectId,
    val workplaceDepartment:String,
    val workplaceDesignation:String,
    val workplaceJoiningDate: LocalDateTime?=null,
    val workplaceLeavingDate: LocalDateTime?=null,
    val workplaceStatus:Boolean
)