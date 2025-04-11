package project.dto.organization

data class OrganizationCreationDto (
    val organizationTypeCode: Int,
    val organizationName:String,
    val organizationEmail:String,
    val organizationPhNo:String,
    val organizationAddress:String,
)