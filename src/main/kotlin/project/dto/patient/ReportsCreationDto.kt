package project.dto.patient

import org.bson.types.ObjectId

data class ReportsCreationDto(
    val organizationId:ObjectId,
    val reportName:String,
    val report:String
)