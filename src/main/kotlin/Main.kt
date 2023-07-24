package com.github.aanno.ics

import io.github.oshai.kotlinlogging.KotlinLogging
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.component.CalendarComponent
import net.fortuna.ical4j.model.component.VAlarm
import net.fortuna.ical4j.model.component.VEvent


val ICS_FILE = "U:/Daten/Downloads/7W6YDyKTWjD2sx2Q-2021-12-03.ics"

private val log = KotlinLogging.logger {}

fun main(args: Array<String>) {
    CliktIcsCommand().main(args)
}

fun <T : CalendarComponent> summary(c: Calendar): Map<Class<in T>, Int> {
    val content: MutableMap<Class<in T>, Int> = mutableMapOf()
    c.getComponents<CalendarComponent>().forEach { c ->
        val clazz: Class<in T> = c::class.java as Class<in T>
        incrementCount(content, clazz, 1)
        when (c::class.java as Class<in CalendarComponent>) {
            VEvent::class.java -> {
                val event = c as VEvent
                // event.alarms
                incrementCount(content, VAlarm::class.java as Class<in T>, event.alarms.size)
            }
        }
    }
    return content.toMap()
}

fun <K> incrementCount(m: MutableMap<K, Int>, key: K, increment: Int): Int {
    val v = m[key]
    m[key] = if (v == null) {
        increment
    } else {
        v + increment
    }
    return m[key] as Int
}
