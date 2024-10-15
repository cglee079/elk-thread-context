package org.cglee079.hello.elkthread.application.config

import io.sentry.Sentry
import org.cglee079.hello.elkthread.logger.MDCHelper
import org.cglee079.hello.elkthread.values.constant.SentryTag
import org.slf4j.MDC
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskDecorator
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class AsyncConfig {

    @Bean
    fun asyncTaskExecutor(): TaskExecutor =
        ThreadPoolTaskExecutor().apply {
            corePoolSize = 30
            maxPoolSize = 30
            threadNamePrefix = "DEFAULT-ASYNC-TASK-EXECUTOR"
            setWaitForTasksToCompleteOnShutdown(true)
            setAwaitTerminationSeconds(60)
            setTaskDecorator(AsyncTaskDecorator())
            initialize()
        }

    class AsyncTaskDecorator : TaskDecorator {

        override fun decorate(runnable: Runnable): Runnable {
            val contextMap = MDC.getCopyOfContextMap()
            val parentRequestId = MDCHelper.requestId
            val parentContextId = MDCHelper.contextId

            return Runnable {
                MDC.setContextMap(contextMap)
                Sentry.setTag(SentryTag.REQUEST_ID, parentRequestId)
                Sentry.setTag(SentryTag.CONTEXT_ID, parentContextId)

                runnable.run()

                Sentry.removeTag(SentryTag.REQUEST_ID)
                Sentry.removeTag(SentryTag.CONTEXT_ID)
                MDC.clear()
            }
        }
    }

}

