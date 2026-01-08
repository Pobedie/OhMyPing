package com.pobedie.ohmyping.entity

enum class VibationPattern(
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
            250, 0,
            250, 0,
            250, 0,
            250, 0,
        )
    ),
    Bach(
        patternName = "Bach",
        timings = longArrayOf(

        ),
        amplitudes = intArrayOf(

        )
    ),
    BzzBzz(
        patternName = "Bzz Bzz",
        timings = longArrayOf(

        ),
        amplitudes = intArrayOf(

        )
    ),
    BzzBzzBzz(
        patternName = "Bzz Bzz Bzz",
        timings = longArrayOf(

        ),
        amplitudes = intArrayOf(

        )
    );

    companion object {
        fun VibationPattern.getName(): String {
            return this.patternName
        }
        fun VibationPattern.getTimings(): LongArray {
            return this.timings
        }
        fun VibationPattern.getAmplitudes(): IntArray {
            return this.amplitudes
        }
    }
}

