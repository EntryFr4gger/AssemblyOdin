package pt.isel.odin.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

/**
 * Represents the attendance of a student in a theorical class.
 *
 * @property id the class attendance id
 * @property student the student that attended the class
 * @property tech the class that the student attended
 * @property attended if the student attended the class
 */
@Entity
@Table(name = "class_attendance")
class ClassAttendance(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @ManyToOne
    @JoinColumn(name = "student_id")
    val student: Student,

    @ManyToOne
    @JoinColumn(name = "tech_id")
    val tech: Tech,

    @Column(nullable = false)
    val attended: Boolean
)
