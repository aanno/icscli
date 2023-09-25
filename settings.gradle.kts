pluginManagement {
    plugins {
        kotlin("jvm") version "1.9.0"
    }
    // val mavenUrl1 = extra["mavenUrl1"]
    val mavenUrl1: String by settings
    repositories {
        // mavenCentral()
        maven {
            url = uri(mavenUrl1!!)
        }
    }
}
rootProject.name = "icscli"

