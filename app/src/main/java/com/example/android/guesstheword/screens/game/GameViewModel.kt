package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 1000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

class GameViewModel : ViewModel() {

    enum class BuzzType(val pattern: LongArray) {
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

    companion object {
        const val DONE = 0L
        const val ONE_SECOND = 1000L
        const val COUNTDOWN_TIME = 20000L
        const val COUNTDOWN_PANIC_SECONDS = 10L
    }

    private val timer: CountDownTimer

    private val _currentTime = MutableLiveData<Long>()
    private val currentTime: LiveData<Long>
        get() = _currentTime

    val currentTimeString = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time)
    }

    private val _word = MutableLiveData("")
    val word: LiveData<String>
        get() = _word

    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private val _eventGameFinished = MutableLiveData<Boolean>(false)
    val eventGameFinished: LiveData<Boolean>
        get() = _eventGameFinished

    private val _eventBuzz = MutableLiveData<BuzzType>()
    val eventBuzz: LiveData<BuzzType>
        get() = _eventBuzz


    private lateinit var wordList: MutableList<String>

    init {
        resetList()
        nextWord()

        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = (millisUntilFinished / ONE_SECOND)
                if (millisUntilFinished / ONE_SECOND <= COUNTDOWN_PANIC_SECONDS) {
                    _eventBuzz.value = BuzzType.COUNTDOWN_PANIC
                }
            }

            override fun onFinish() {
                _currentTime.value = DONE
                _eventBuzz.value = BuzzType.GAME_OVER
                _eventGameFinished.value = true
            }
        }

        timer.start()

    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }

    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }

    private fun nextWord() {
        if (wordList.isEmpty()) {
            _eventGameFinished.value = true
        } else {
            _word.value = wordList.removeAt(0)
        }
    }

    fun onSkip() {
        if (_score.value == 0) {
            nextWord()
        } else {
            _score.value = (_score.value)?.minus(1)
            nextWord()
        }
    }

    fun onCorrect() {
        _score.value = (_score.value)?.plus(1)
        nextWord()
    }

    fun onGameFinishComplete() {
        _eventBuzz.value = BuzzType.GAME_OVER
        _eventGameFinished.value = false
        onCleared()
    }

    fun onBuzzComplete() {
        _eventBuzz.value = BuzzType.NO_BUZZ
    }
}