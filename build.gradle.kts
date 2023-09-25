import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    // https://kotlinlang.org/docs/gradle-configure-project.html#set-jdk-version-with-the-task-dsl
    // id("org.gradle.toolchains.foojay-resolver-convention") version("0.6.0")
    // id("org.javamodularity.moduleplugin") version "1.8.12"
    application
    distribution
}

group = "org.github.aanno.ics"
version = "1.0-SNAPSHOT"

val mavenUrl1 by properties

repositories {
    // mavenCentral()
    maven {
        url = uri(mavenUrl1!!)
    }
}

val kotlinLoggingVersion by properties
val slf4jSimpleVersion by properties
val junitVersion by properties
val cliktVersion by properties
val moduleName by properties
val ical4jVersion by properties

kotlin {
    jvmToolchain(17)
}

val service = project.extensions.getByType<JavaToolchainService>()
val customLauncher = service.launcherFor {
    languageVersion.set(JavaLanguageVersion.of(17))
}
/*
project.tasks.withType<UsesKotlinJavaToolchain>().configureEach {
    kotlinJavaToolchain.toolchain.use(customLauncher)
}
 */

dependencies {
    // https://www.baeldung.com/junit-5-gradle
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")

    // https://www.ical4j.org/getting-started/
    implementation("org.mnode.ical4j:ical4j:${ical4jVersion}")

    // https://www.baeldung.com/kotlin/kotlin-logging-library
    // https://github.com/oshai/kotlin-logging
    implementation("io.github.oshai:kotlin-logging-jvm:${kotlinLoggingVersion}")
    implementation("org.slf4j:slf4j-simple:${slf4jSimpleVersion}")
    // implementation("ch.qos.logback:logback-classic:1.4.8")

    // https://ajalt.github.io/clikt/
    implementation("com.github.ajalt.clikt:clikt:${cliktVersion}")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    named("compileJava", JavaCompile::class.java) {
        options.compilerArgumentProviders.add(CommandLineArgumentProvider {
            // Provide compiled Kotlin classes to javac – needed for Java/Kotlin mixed sources to work
            listOf("--patch-module", "${moduleName}=${sourceSets["main"].output.asPath}")
        })
    }

    test {
        useJUnitPlatform()
    }

    wrapper {
        distributionType = Wrapper.DistributionType.ALL
    }
}

application {
    mainClass.set("com.github.aanno.ics.MainKt")
}
