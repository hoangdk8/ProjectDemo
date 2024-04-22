package com.example.projectdemo.untils

fun convertDurationToTimeString(duration: Int): Array<String> {
    val hour: String
    val minute: String

    when {
        duration < 10000 -> {
            hour = "0"
            minute = "0${duration / 1000}"
        }
        duration in 10000..59999 -> {
            hour = "0"
            minute = "${duration / 1000}"
        }
        duration in 60000..60999 -> {
            hour = "1"
            minute = "00"
        }
        duration in 61000..119999 -> {
            hour = "1"
            minute = "${(duration - 60000) / 1000}"
        }
        else -> {
            // Handle other cases if needed
            hour = ""
            minute = ""
        }
    }
    return arrayOf(hour, minute)
}
