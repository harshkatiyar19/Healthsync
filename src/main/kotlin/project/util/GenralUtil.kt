package project.util

import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import project.entity.Gender

@Component
class GenralUtil {

    fun isValidPhoneNumber(phone: String): Boolean {
        val phoneRegex = Regex("^\\+?[1-9]\\d{6,14}$")
        return phoneRegex.matches(phone)
    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        return emailRegex.matches(email)
    }


//    fun passwordEncode(password:String): String? {
//        return BCryptPasswordEncoder().encode(password)
//    }

    fun isValidPassword(password: String,confirmPassword: String): Boolean {
        val passwordRegex = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&#])[A-Za-z\\d@\$!%*?&#]{8,}$")
        return passwordRegex.matches(password)
    }

    fun isSamePassword(password: String,confirmPassword: String): Boolean {
        return password==confirmPassword
    }

    fun assignGender(genderCode:Int): Gender {
        val assign: Gender = when(genderCode){
            1->Gender.MALE
            2->Gender.FEMALE
            else -> {Gender.TRANSGENDER}
        }
        return assign
    }

}