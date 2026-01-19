package com.robote.onlinestore

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.util.Calendar

class DayTImeTest {
    @Test
    fun dayTime() {
        val dayTime = Calendar.HOUR_OF_DAY
        var greeting = ""

        greeting = when (dayTime) {
            in 0..12 -> {
                "Good morning"
            }

            in 13..22 -> {
                "Good afternoon"
            }

            else -> {
                "Goog night"
            }
        }

        assertEquals("Good morning",greeting)

    }
}