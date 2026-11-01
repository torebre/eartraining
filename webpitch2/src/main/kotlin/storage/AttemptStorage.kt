package storage

import com.kjipo.attemptprocessor.InputAttempt

interface AttemptStorage {
    fun saveAttempt(attempt: InputAttempt)
    fun getAttempts(): List<InputAttempt>
    fun clearAttempts()
}
