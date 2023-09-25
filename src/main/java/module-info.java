// https://www.baeldung.com/java-9-modularity
module com.github.aanno.ics {
    // error: module not found: clikt.jvm
    // requires transitive clikt.jvm;
    requires kotlin.stdlib;
    requires io.github.oshai.kotlinlogging;
    requires org.mnode.ical4j.core;
}
