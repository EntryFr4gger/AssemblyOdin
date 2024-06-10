package pt.isel.odin.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pt.isel.odin.repository.DepartmentRepository
import kotlin.jvm.optionals.toList

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
class DepartmentControllerTest {

    @Autowired
    lateinit var departmentRepository: DepartmentRepository

    @Test
    fun `Get Category`() {
        val cat = departmentRepository.findById(1)
        println(cat.toList())
    }

    /*@Test
    fun `Save User Repository`(){
        val user = User(
            username = "test",
            email = "asdsad@gmail.com",
        )
        userRepository.save(user)
    }

    @Test
    fun `Find User Repository`(){
        val user = userRepository.findById(1)
        println(user)
    }

    @Test
    fun `Delete User Repository`(){
        userRepository.deleteById(1)
    }*/
}
