package com.example.spring_test.observation

import io.micrometer.core.instrument.Measurement
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Statistic
import io.micrometer.core.instrument.observation.DefaultMeterObservationHandler
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import org.springframework.stereotype.Service
import java.util.stream.StreamSupport

@Service
class ObservationService {

    fun observe(){

        val meterRegistry: MeterRegistry = SimpleMeterRegistry() // 계측 가능

        val observationRegistry = ObservationRegistry.create()

        observationRegistry
            .observationConfig()
            .observationHandler(DefaultMeterObservationHandler(meterRegistry))

        val observation = Observation.createNotStarted("sample", observationRegistry)

        observation.observe {
            for(i in 0..100000){
                println("test$i")
            }
        }

        val maximumDuration = meterRegistry.meters.stream()
            .filter { m: Meter ->
                "sample" == m.id.name
            }
            .flatMap { m: Meter ->
                StreamSupport.stream(
                    m.measure().spliterator(),
                    false
                )
            }
            .filter { ms: Measurement -> ms.statistic == Statistic.MAX }
            .findFirst()
            .map { obj: Measurement -> obj.value }

        println("Duration : " + maximumDuration.get())
    }
}