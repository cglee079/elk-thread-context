package org.cglee079.hello.elkthread.config

import org.cglee079.hello.elkthread.config.filter.RequestLoggingFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebConfig {

    @Bean
    fun requestLoggingFilter(): FilterRegistrationBean<RequestLoggingFilter> =
        FilterRegistrationBean(RequestLoggingFilter()).apply {
            this.addUrlPatterns("/api/*")
        }
}