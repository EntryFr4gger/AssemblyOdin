package pt.isel.odin.model

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import kotlinx.datetime.LocalDate
import pt.isel.odin.utils.LocalDateConverter

/**
 * Represents the Pratical classes (VOC) in the system.
 *
 * @property id the VOC id
 * @property description the description of the VOC
 * @property student the student that is responsible for the VOC
 * @property curricularUnit the course that the VOC is about
 * @property started the date of the VOC
 * @property ended the length of the VOC
 * @property approved if the VOC was approved
 */
@Entity
@Table(name = "voc")
class Voc(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val description: String? = null,

    @Column(nullable = false)
    val approved: Boolean? = null,

    @ManyToOne
    val student: Student? = null,

    @ManyToOne
    val curricularUnit: CurricularUnit? = null,

    @Convert(converter = LocalDateConverter::class)
    val started: LocalDate? = null,

    @Convert(converter = LocalDateConverter::class)
    val ended: LocalDate? = null
)

fun Voc.copy(
    id: Long? = this.id,
    description: String? = this.description,
    approved: Boolean? = this.approved,
    student: Student? = this.student,
    curricularUnit: CurricularUnit? = this.curricularUnit,
    started: LocalDate? = this.started,
    ended: LocalDate? = this.ended
) = Voc(id, description, approved, student, curricularUnit, started, ended)
