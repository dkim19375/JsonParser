import me.dkim19375.dkimgradle.enums.mavenAll
import me.dkim19375.dkimgradle.util.setupJava

plugins {
    application
    kotlin("jvm") version "1.9.20"
    id("me.champeau.jmh") version "0.7.2"
    id("org.cadixdev.licenser") version "0.6.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.github.dkim19375.dkim-gradle") version "1.3.7"
}

group = "me.dkim19375"
version = "1.0.0"

setupJava(
    mainClassName = "me.dkim19375.jsonparser.JsonPrinterKt",
    javaVersion = JavaVersion.VERSION_1_8,
)

repositories {
    mavenAll()
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.1.0")

    implementation("io.github.dkim19375:dkimcore:1.4.2")

    jmh("com.google.code.gson:gson:2.10.1")
    jmh("com.fasterxml.jackson.core:jackson-databind:2.16.0")

    testImplementation(kotlin("test"))
}

jmh {
    resultFormat.set("JSON")

    warmupIterations.set(2)
    iterations.set(2)
    fork.set(2)
}

tasks {
    named<JavaExec>("run") {
        standardInput = System.`in`
    }

    build {
        dependsOn(licenseFormat)
    }

    test {
        useJUnitPlatform()
    }
}