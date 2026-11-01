package storage

import com.kjipo.attemptprocessor.InputAttempt

class InMemoryAttemptStorage : AttemptStorage {
    private val attempts = mutableListOf<InputAttempt>()

    override fun saveAttempt(attempt: InputAttempt) {
        attempts.add(attempt)
    }

    override fun getAttempts(): List<InputAttempt> {
        return attempts.toList()
    }

    override fun clearAttempts() {
        attempts.clear()
    }
}
