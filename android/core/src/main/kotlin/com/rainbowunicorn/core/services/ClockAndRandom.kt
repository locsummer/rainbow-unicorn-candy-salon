package com.rainbowunicorn.core.services

import java.util.Random

interface Clock {
    fun nowMillis(): Long
}

class SystemClock : Clock {
    override fun nowMillis(): Long = System.currentTimeMillis()
}

class TestClock(var currentMillis: Long = 1000000L) : Clock {
    override fun nowMillis(): Long = currentMillis

    fun advanceSeconds(seconds: Long) {
        currentMillis += seconds * 1000L
    }
}

interface RandomSource {
    fun nextInt(bound: Int): Int
    fun nextInt(from: Int, to: Int): Int
    fun nextDouble(): Double
}

class SeededRandomSource(seed: Long = 42L) : RandomSource {
    private val random = Random(seed)

    override fun nextInt(bound: Int): Int = random.nextInt(bound)

    override fun nextInt(from: Int, to: Int): Int {
        require(to >= from) { "to must be >= from" }
        return from + random.nextInt(to - from + 1)
    }

    override fun nextDouble(): Double = random.nextDouble()
}
