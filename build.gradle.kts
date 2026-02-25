plugins {
    id("java")
    id("jacoco")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.jacocoTestReport {
    reports {
        html.required.set(true)
    }
}

javafx {
    version = "23"
    modules = listOf("javafx.controls", "javafx.graphics", "javafx.fxml")
}

application {
    mainClass.set("view.MazeApplication")
}

dependencies {
    implementation("org.openjfx:javafx-controls:23")
    implementation("org.openjfx:javafx-graphics:23")
    implementation("org.openjfx:javafx-base:23")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}