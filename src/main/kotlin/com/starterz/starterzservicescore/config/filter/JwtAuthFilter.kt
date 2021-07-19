package com.starterz.starterzservicescore.config.filter

import com.starterz.starterzservicescore.service.JwtService
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtAuthFilter(
    private val jwtService: JwtService
): WebFilter {
    companion object {
        const val HEADER_PREFIX = "Bearer "
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val bearerToken = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        if (!StringUtils.hasText(bearerToken) || !bearerToken!!.startsWith(HEADER_PREFIX))
            return chain.filter(exchange)
        val token = bearerToken.substring(HEADER_PREFIX.length)
        return jwtService.verifyAuthJwt(token)
            .map { UsernamePasswordAuthenticationToken(it, token, AuthorityUtils.commaSeparatedStringToAuthorityList("USER")) }
            .flatMap { chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(it)) }
            .onErrorResume { chain.filter(exchange) }
    }
}