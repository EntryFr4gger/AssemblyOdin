package pt.isel.odin.http.controllers.section.models

import pt.isel.odin.http.controllers.user.models.GetUserOutputModel
import pt.isel.odin.model.Section


data class GetSectionOutputModel(
    val id: Long,
    val name: String,
    val summary: String,
    val students: List<GetUserOutputModel>
) {
    constructor(section: Section) : this(
        id = section.id!!,
        name = section.name,
        summary = section.summary,
        students = section.students
            .map { GetUserOutputModel(it) }
    )
}
