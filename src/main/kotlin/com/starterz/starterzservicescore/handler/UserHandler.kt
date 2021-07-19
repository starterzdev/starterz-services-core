package com.starterz.starterzservicescore.handler

import com.starterz.starterzservicescore.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class UserHandler(private val userRepository: UserRepository) {
    fun findAllUsers(request: ServerRequest): Mono<ServerResponse> {
        return userRepository.findAll()
            .collectList()
            .flatMap { ServerResponse.ok().bodyValue(it) }
    }

    fun findUserById(request: ServerRequest): Mono<ServerResponse> {
        return Mono.just(request.pathVariable("id"))
            .map(String::toLong)
            .flatMap(userRepository::findById)
            .switchIfEmpty(Mono.defer { Mono.error(Throwable("User not found")) })
            .flatMap { ServerResponse.ok().bodyValue(it) }
            .onErrorResume { ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue(it.localizedMessage) }
    }
}