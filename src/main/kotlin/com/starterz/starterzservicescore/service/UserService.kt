package com.starterz.starterzservicescore.service

import com.starterz.starterzservicescore.model.User
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
    val userRepository: UserRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    @Transactional
    fun updateKakaoConnectionId(userId: Long, kakaoConnectionId: String): Mono<User> {
        return userRepository
            .findById(userId)
            .switchIfEmpty(Mono.defer { Mono.error(UserNotFoundException("User is not found for ID: $userId")) })
            .flatMap { if (it.kakaoConnectionId.isNullOrEmpty()) Mono.just(it) else Mono.empty() }
            .switchIfEmpty(Mono.defer { Mono.error(AlreadyIntegratedException("Kakao is already connected for ID: $userId")) })
            .doOnNext { it.kakaoConnectionId = kakaoConnectionId }
            .flatMap(userRepository::save)
            .switchIfEmpty(Mono.defer { Mono.error(UserUpdateFailedException("Updating User failed for ID: $userId")) })
    }

    fun getUserByEmail(email: String): Mono<User> {
        return userRepository.findByEmail(email)
            .switchIfEmpty(Mono.defer { Mono.error(UserNotFoundException("User is not found for email: $email")) })
    }
}