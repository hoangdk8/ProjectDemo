package com.example.projectdemo.untils

fun convertDurationToTimeString(duration: Int): Array<String> {
    val minute: String
    val second: String

    when {
        duration < 10000 -> {
            minute = "0"
            second = "0${duration / 1000}"
        }
        duration in 10000..59999 -> {
            minute = "0"
            second = "${duration / 1000}"
        }
        duration in 60000..60999 -> {
            minute = "1"
            second = "00"
        }
        duration in 61000..119999 -> {
            minute = "1"
            second = "${(duration - 60000) / 1000}"
        }
        else -> {
            // Handle other cases if needed
            minute = ""
            second = ""
        }
    }
    return arrayOf(minute, second)
}
