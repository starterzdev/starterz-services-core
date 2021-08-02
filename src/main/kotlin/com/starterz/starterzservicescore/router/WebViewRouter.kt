package com.starterz.starterzservicescore.router

import com.starterz.starterzservicescore.handler.WebViewHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class WebViewRouter(private val webViewHandler: WebViewHandler) {
    @Bean
    fun webRoutes() = router {
        "/web".nest {
            "/verification".nest {
                GET("/complete", webViewHandler::renderVerificationOk)
                GET("/expired", webViewHandler::renderVerificationExpired)
                GET("/failed", webViewHandler::renderVerificationFailed)
            }
        }
    }
}