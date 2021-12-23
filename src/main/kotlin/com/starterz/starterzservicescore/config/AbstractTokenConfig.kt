package com.starterz.starterzservicescore.config

import lombok.Data
import java.time.Duration

@Data
abstract class AbstractTokenConfig {
    lateinit var issuer: String
    lateinit var audience: String
    lateinit var duration: Duration
    var signingKey: SigningKey = SigningKey()

    @Data
    class SigningKey {
        lateinit var alias: String
        lateinit var password: String
    }
}