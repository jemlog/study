package com.example.spring_test.observation

import io.micrometer.observation.Observation

import io.micrometer.observation.ObservationHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SimpleLoggingHandler : ObservationHandler<Observation.Context?> {
    override fun supportsContext(context: Observation.Context): Boolean {
        return true
    }

    override fun onStart(context: Observation.Context) {
        log.info("Starting")
    }

    override fun onScopeOpened(context: Observation.Context) {
        log.info("Scope opened")
    }

    override fun onScopeClosed(context: Observation.Context) {
        log.info("Scope closed")
    }

    override fun onStop(context: Observation.Context) {
        log.info("Stopping")
    }

    override fun onError(context: Observation.Context) {
        log.info("Error")
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(SimpleLoggingHandler::class.java)
    }
}