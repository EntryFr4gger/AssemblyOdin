package pt.isel.odin.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.odin.controller.utils.Config
import pt.isel.odin.controller.utils.ControllerTestUtils
import pt.isel.odin.http.controllers.Uris
import pt.isel.odin.http.controllers.user.models.GetAllUsersOutputModel
import pt.isel.odin.http.controllers.user.models.GetUserOutputModel
import pt.isel.odin.http.controllers.user.models.SaveUserInputModel
import pt.isel.odin.http.controllers.user.models.SaveUserOutputModel
import pt.isel.odin.http.controllers.user.models.UpdateUserInputModel
import pt.isel.odin.model.user.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(Config::class)
class UserControllerTest {

    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var testUtils: ControllerTestUtils

    private lateinit var client: WebTestClient

    @BeforeEach
    fun setup() {
        testUtils.cleanDatabase()
        client = testUtils.setupClient(port, Uris.Users.RESOURCE)
    }

    @Test
    fun `Get user by ID`() {
        // given: a user
        val user = testUtils.createUser()

        // when: getting the user by its ID
        val result = client.get()
            .uri("/${user.id}")
            .exchange()
            .expectStatus().isOk
            .expectBody(User::class.java)
            .returnResult()
            .responseBody

        // then: user matches the expected user
        assertEquals(user.email, result!!.email)
    }

    @Test
    fun `Get user by non-existent ID`() {
        // when: getting a non-existent user by its ID
        // then: a not found error is returned
        client.get().uri("/999")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.title").isEqualTo("User Not Found")
    }

    @Test
    fun `Get all users`() {
        // given: two users
        val role = testUtils.createRole()
        val user1 = testUtils.createUser(givenRole = role)
        val user2 = testUtils.createUser("test1@example.com", role)

        val expectedUsers = listOf(
            GetUserOutputModel(user1),
            GetUserOutputModel(user2)
        )

        // when: getting all users
        val result = client.get()
            .exchange()
            .expectStatus().isOk
            .expectBody(GetAllUsersOutputModel::class.java)
            .returnResult()
            .responseBody

        // then: the users match the expected users
        assertEquals(2, result!!.users.size)
        assertEquals(expectedUsers, result.users)
    }

    @Test
    fun `Save user`() {
        // given: a user
        val role = testUtils.createRole()
        val input = SaveUserInputModel(email = "newuser@example.com", username = "newuser", role = role.id!!)

        // when: saving the user
        val userId = client.post()
            .uri(Uris.Users.SAVE)
            .bodyValue(input)
            .exchange()
            .expectStatus().isCreated
            .expectBody(SaveUserOutputModel::class.java)
            .returnResult()
            .responseBody

        // and: getting the user by its ID
        val result = client.get()
            .uri("/${userId!!.id}")
            .exchange()
            .expectStatus().isOk
            .expectBody(User::class.java)
            .returnResult()
            .responseBody

        // then: the user matches the expected user
        assertEquals(input.email, result!!.email)
    }

    @Test
    fun `Save user with duplicate email`() {
        // given: a user with the same email
        val user = testUtils.createUser()
        val input = SaveUserInputModel(email = user.email, username = user.username, role = user.role.id!!)

        // when: saving the user
        // then: a conflict error is returned
        client.post()
            .uri(Uris.Users.SAVE)
            .bodyValue(input)
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectBody()
            .jsonPath("$.title").isEqualTo("A User Already Exists With That Email")
    }

    @Test
    fun `Save user with invalid email`() {
        // when: saving a user with an invalid email
        val input = SaveUserInputModel(email = "invalid", username = "sadsadas", role = 1)

        // then: a bad request error is returned
        client.post()
            .uri(Uris.Users.SAVE)
            .bodyValue(input)
            .exchange()
            .expectStatus().isEqualTo(400)
            .expectBody()
            .jsonPath("$.title").isEqualTo("Email Incorrect")
    }

    @Test
    fun `Save user with invalid name`() {
        // when: saving a user with an invalid name
        val input = SaveUserInputModel(email = "invalid@gmail.com", username = "", role = 1)

        // then: a bad request error is returned
        client.post()
            .uri(Uris.Users.SAVE)
            .bodyValue(input)
            .exchange()
            .expectStatus().isEqualTo(400)
            .expectBody()
            .jsonPath("$.title").isEqualTo("Name Incorrect")
    }

    @Test
    fun `Save user with invalid role`() {
        // when: saving a user with an invalid role
        val input = SaveUserInputModel(email = "invalid@gmail.com", username = "user", role = 999)

        // then: a bad request error is returned
        client.post()
            .uri(Uris.Users.SAVE)
            .bodyValue(input)
            .exchange()
            .expectStatus().isEqualTo(400)
            .expectBody()
            .jsonPath("$.title").isEqualTo("Role Incorrect")
    }

    @Test
    fun `Update user`() {
        // given: a user
        val user = testUtils.createUser()
        val input = UpdateUserInputModel(
            id = user.id!!,
            email = user.email,
            username = user.username,
            credits = 100,
            role = user.role.id!!
        )

        // when: updating the user
        val result = client.put()
            .uri(Uris.Users.UPDATE)
            .bodyValue(input)
            .exchange()
            .expectStatus().isOk
            .expectBody(GetUserOutputModel::class.java)
            .returnResult()
            .responseBody

        // then: the user matches the expected user
        assertEquals(input.email, result!!.email)
    }

    @Test
    fun `Update non-existent user`() {
        // when: updating a non-existent user
        val role = testUtils.createRole()
        val input = UpdateUserInputModel(
            id = 999,
            email = "updated5@example.com",
            username = "updateduser",
            credits = 100,
            role = role.id!!
        )

        // then: a not found error is returned
        client.put()
            .uri(Uris.Users.UPDATE)
            .bodyValue(input)
            .exchange()
            .expectStatus().isEqualTo(404)
            .expectBody()
            .jsonPath("$.title").isEqualTo("User Not Found")
    }

    @Test
    fun `Update user with invalid name`() {
        // when: updating a user with an invalid name
        val input = UpdateUserInputModel(id = 1, email = "invalid@gmail.com", username = "", credits = 100, role = 1)

        // then: a bad request error is returned
        client.put()
            .uri(Uris.Users.UPDATE)
            .bodyValue(input)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.title").isEqualTo("Name Incorrect")
    }

    @Test
    fun `Update user with invalid email`() {
        // when: updating a user with an invalid email
        val input = UpdateUserInputModel(id = 1, email = "invalid", username = "user", credits = 100, role = 1)

        // then: a bad request error is returned
        client.put()
            .uri(Uris.Users.UPDATE)
            .bodyValue(input)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.title").isEqualTo("Email Incorrect")
    }

    @Test
    fun `Update user with invalid role`() {
        // when: updating a user with an invalid role
        val input =
            UpdateUserInputModel(id = 1, email = "invalid@gmail.com", username = "user", credits = 100, role = 999)

        // then: a bad request error is returned
        client.put()
            .uri(Uris.Users.UPDATE)
            .bodyValue(input)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.title").isEqualTo("Role Incorrect")
    }

    @Test
    fun `Delete user`() {
        // given: a user
        val user = testUtils.createUser()

        // when: deleting the user
        val result = client.delete()
            .uri("/${user.id}")
            .exchange()
            .expectStatus().isOk
            .expectBody(GetUserOutputModel::class.java)
            .returnResult()
            .responseBody

        // then: the user matches the expected user
        assertEquals(user.email, result!!.email)
    }

    @Test
    fun `delete user - not found`() {
        // when: deleting a non-existent user
        // then: a not found error is returned
        client.delete()
            .uri("/999")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.title").isEqualTo("User Not Found")
    }
}
