// https://www.baeldung.com/java-9-modularity
module com.github.aanno.ics {
    // error: module not found: clikt.jvm
    // requires transitive clikt.jvm;
    requires transitive kotlin.stdlib;
    requires transitive io.github.oshai.kotlinlogging;
    requires transitive org.mnode.ical4j.core;
}
