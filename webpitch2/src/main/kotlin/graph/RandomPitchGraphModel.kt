package graph

import kotlinx.coroutines.delay
import mu.KotlinLogging
import kotlin.random.Random

class RandomPitchGraphModel : PitchGraphModel() {
    val random = Random(1)
    var timeStep = 0L
    var idCounter = 0

    var running = false


    private val logger = KotlinLogging.logger {}


    suspend fun start() {
        if (running) {
            // Already running
            return
        }
        running = true

        while (running) {
            delay(500)

            logger.debug { "Adding point at time step: $timeStep" }

            addPitchDataWithTime(PitchDataWithTime(random.nextInt(100, 250).toFloat(), 0.5f, timeStep, idCounter++))
            timeStep += 1000
        }
    }

    fun stop() {
        running = false
    }


}