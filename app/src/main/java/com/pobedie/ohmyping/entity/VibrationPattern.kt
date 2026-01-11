package com.pobedie.ohmyping.entity

enum class VibrationPattern(
    val patternName: String,
    val timings: LongArray,
    val amplitudes: IntArray
) {
    BeeHive(
        patternName = "Bee hive",
        timings = longArrayOf(
            50, 60,
            50, 60,
            50, 60,
            50, 60,
        ),
        amplitudes = intArrayOf(
            255, 0,
            255, 0,
            255, 0,
            255, 0,
        )
    ),
    Bach(
        patternName = "Bach",
        timings = longArrayOf(
            100, 100,
            100, 100,
            200, 100,
            600, 0,
        ),
        amplitudes = intArrayOf(
            200, 0,
            230, 0,
            255, 0,
            200, 0,
        )
    ),
    BzzBzz(
        patternName = "Bzz Bzz",
        timings = longArrayOf(
            500, 100,
            500, 100,
        ),
        amplitudes = intArrayOf(
            255, 0,
            255, 0,
        )
    ),
    BzzBzzBzz(
        patternName = "Bzz Bzz Bzz",
        timings = longArrayOf(
            500, 100,
            500, 100,
            500, 100,
        ),
        amplitudes = intArrayOf(
            255, 0,
            255, 0,
            255, 0,
        )
    ),
    Wheeehooo(
        patternName = "Wheee-hooo",
        timings = longArrayOf(
            50, 50, 50, 50, 50, 50, 100,
            100, 50, 50, 50, 150, 100, 150
        ),
        amplitudes = intArrayOf(
            10, 60, 100, 140, 180, 220, 255,
            255, 220, 180, 140, 100, 60, 10
            )
    ),
    Trrrrrrrrrr(
        patternName = "Trrrrrrrrrrrr",
        timings = longArrayOf(
            50, 70, 50, 70, 50, 70, 50,
            70, 50, 70, 50, 70, 50, 50,
            50, 70, 50, 70, 50, 70, 50,
            70, 50, 70, 50, 70, 50, 50,
        ),
        amplitudes = intArrayOf(
            250, 0, 250, 0, 250, 0, 250,
            0, 250, 0, 250, 0, 250, 0,
            250, 0, 250, 0, 250, 0, 250,
            0, 250, 0, 250, 0, 250, 0,
        )
    )
}

