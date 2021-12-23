package com.starterz.starterzservicescore.service

import com.starterz.starterzservicescore.entity.User
import com.starterz.starterzservicescore.entity.user.UserOauth
import com.starterz.starterzservicescore.repository.UserOauthRepository
import com.starterz.starterzservicescore.repository.UserRepository
import com.starterz.starterzservicescore.service.exception.AlreadyIntegratedException
import com.starterz.starterzservicescore.service.exception.UserNotFoundException
import com.starterz.starterzservicescore.service.exception.UserUpdateFailedException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class UserService(
    val userOAuthRepository: UserOauthRepository,
    val userRepository: UserRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun getUserByEmail(email: String): Mono<User> {
        return userRepository.findByEmail(email)
            .switchIfEmpty(Mono.defer { Mono.error(UserNotFoundException("User is not found for email: $email")) })
    }
}