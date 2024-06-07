package pt.isel.odin.config.spring.service

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Service
import pt.isel.odin.http.utils.isStudent
import pt.isel.odin.http.controllers.student.models.StudentRequest
import pt.isel.odin.http.controllers.user.models.UserRequest
import pt.isel.odin.service.student.StudentService
import pt.isel.odin.service.user.UserService

@Service
class OAuth2UserService(
    private val userService: UserService,
    private val studentService: StudentService

) : OidcUserService() {

    override fun loadUser(userRequest: OidcUserRequest): OidcUser {
        val oAuth2User = super.loadUser(userRequest)

        val email = oAuth2User.getAttribute<String>("email") ?: throw IllegalArgumentException("Email not found")
        val name = oAuth2User.getAttribute<String>("name") ?: throw IllegalArgumentException("Name not found")

        if (userService.getByEmail(email) != null) return oAuth2User

        if (isStudent(email)) {
            studentService.save(StudentRequest(email = email, username = name))
        } else {
            userService.save(UserRequest(email = email, username = name))
        }

        return oAuth2User
    }
}
