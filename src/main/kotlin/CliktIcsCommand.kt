package com.github.aanno.ics

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import io.github.oshai.kotlinlogging.KotlinLogging
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.data.CalendarOutputter
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Period
import net.fortuna.ical4j.model.component.CalendarComponent
import net.fortuna.ical4j.model.component.VEvent
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

private val log = KotlinLogging.logger {}

class CliktIcsCommand : CliktCommand(
    help = """
    Usage: icscli <in> <out>
    Strip ics <in> to events in the next 4 months (and from 2 months ago) and write that to <out>
""".trimIndent()
) {
    private val source by argument().file(mustExist = true)
    private val dest by argument().file()

    override fun run() {
        // Try adding program arguments via Run/Debug configuration.
        // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
        // log.info { "Program arguments: ${args.joinToString()}" }

        // https://www.ical4j.org/examples/parsing/
        val fin = BufferedInputStream(FileInputStream(ICS_FILE))
        val builder = CalendarBuilder()
        val inCalendar: Calendar = builder.build(fin)
        val valid = inCalendar.validate()
        log.info { "parsed ${ICS_FILE}, result: ${valid}" }

        log.info { "in content: ${summary<CalendarComponent>(inCalendar)}" }

        val instant = LocalDateTime.now()
        val startMonth = instant.minus(2L, ChronoUnit.MONTHS)
        val endMonth = instant.plus(4L, ChronoUnit.MONTHS)
        /*
        val startMonth = YearMonth.of(2023, 1)
        val endMonth = YearMonth.of(2023, 7)
         */
        val start = LocalDateTime.of(startMonth.year, startMonth.month, 1, 0, 0, 0)
        val end = LocalDateTime.of(endMonth.year, endMonth.month, 1, 0, 0, 0)
        val period = Period<LocalDateTime>(start, end)
        log.info { "events in period: ${period}" }

        val outCalendar = inCalendar // inCalendar.copy()
        outCalendar.getComponents<CalendarComponent>().forEach { c ->
            when (c::class.java as Class<in CalendarComponent>) {
                VEvent::class.java -> {
                    val event = c as VEvent
                    try {
                        val consumed: List<Period<LocalDateTime>> = event.getConsumedTime(period)
                        if (consumed.isEmpty()) {
                            outCalendar.remove(event)
                        } else {
                            log.debug { "consumed: ${consumed}" }
                        }
                    } catch (e: DateTimeException) {
                        log.error { "failed to handle ${event}" }
                    }
                }
            }
        }
        log.info { "out content: ${summary<CalendarComponent>(outCalendar)}" }

        // https://www.ical4j.org/examples/output/
        val fout = BufferedOutputStream(FileOutputStream("mycalendar.ics"))
        val outputter = CalendarOutputter()
        outputter.output(outCalendar, fout)
    }
}
