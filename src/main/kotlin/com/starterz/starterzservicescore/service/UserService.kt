package com.starterz.starterzservicescore.service

import com.starterz.starterzservicescore.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository
) {
    fun findUserById(id: Long) {
    }
}