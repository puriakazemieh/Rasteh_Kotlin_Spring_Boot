package com.kazemieh.rasteh

import com.kazemieh.rasteh.shared.security.jwt.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.web.config.EnableSpringDataWebSupport
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.client.RestTemplate

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableConfigurationProperties(JwtProperties::class)
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
class RastehApplication {
    @Bean
    fun restTemplate(): RestTemplate = RestTemplate()
}

fun main(args: Array<String>) {
    runApplication<RastehApplication>(*args)
}
