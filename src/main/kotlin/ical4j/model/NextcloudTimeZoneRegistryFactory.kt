package com.github.aanno.ics.ical4j.model

import net.fortuna.ical4j.model.TimeZoneRegistry
import net.fortuna.ical4j.model.TimeZoneRegistryFactory
import net.fortuna.ical4j.model.TimeZoneRegistryImpl
import java.time.DateTimeException
import java.time.ZoneId
import java.time.ZoneOffset

class NextcloudTimeZoneRegistryFactory : TimeZoneRegistryFactory() {
    override fun createRegistry(): TimeZoneRegistry {
        return NextcloudTimeZoneRegistry()
    }
}

private class NextcloudTimeZoneRegistry : TimeZoneRegistryImpl() {

    override fun getZoneId(tzId: String): ZoneId {
        return try {
            super.getZoneId(tzId)
        } catch (e: DateTimeException) {
            val id = if (tzId.startsWith("GMT")) {
                tzId.substring(3)
            } else {
                tzId
            }
            ZoneOffset.of(id)
        }
    }

}
