package pt.isel.odin.service.implementations

import org.springframework.stereotype.Service
import pt.isel.odin.model.Student
import pt.isel.odin.repository.StudentRepository
import pt.isel.odin.service.exception.NotFoundException
import pt.isel.odin.service.interfaces.StudentService

@Service
class StudentServiceImpl(private val studentRepository: StudentRepository) : StudentService {

    override fun getById(id: Long): Student {
        return studentRepository.findById(id).orElseThrow { NotFoundException("No Student Found") }
    }

    override fun getAll(): List<Student> {
        return studentRepository.findAll()
    }

    override fun save(student: Student): Student {
        return studentRepository.save(student)
    }

    override fun delete(id: Long) {
        studentRepository.deleteById(id)
    }
}
