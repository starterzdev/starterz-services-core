package com.starterz.starterzservicescore.service

import com.starterz.starterzservicescore.model.User
import com.starterz.starterzservicescore.repository.UserRepository
import com.starterz.starterzservicescore.service.exception.UserNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class UserService(
    val userRepository: UserRepository
) {
    @Transactional
    fun updateKakaoConnectionId(email: String, kakaoConnectionId: Long): Mono<User> {
        return userRepository
            .findByEmail(email)
            .flatMap { user ->
                user.kakaoConnectionId = kakaoConnectionId
                userRepository.save(user)
            }
            .switchIfEmpty(Mono.defer { Mono.error(UserNotFoundException("User is not found for email: $email")) })
    }
}