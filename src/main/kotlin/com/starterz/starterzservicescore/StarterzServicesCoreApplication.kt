package com.starterz.starterzservicescore

import kotlinx.coroutines.debug.CoroutinesBlockHoundIntegration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.blockhound.BlockHound
import reactor.blockhound.integration.ReactorIntegration
import reactor.blockhound.integration.StandardOutputIntegration

@SpringBootApplication
class StarterzServicesCoreApplication

fun main(args: Array<String>) {
	BlockHound.builder()
		.with(ReactorIntegration())
		.with(CoroutinesBlockHoundIntegration())
		.with(StandardOutputIntegration())
		.install();
	runApplication<StarterzServicesCoreApplication>(*args)
}
