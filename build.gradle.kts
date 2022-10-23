import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "de.skycave"
version = "2.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.322")
    implementation("org.slf4j:slf4j-simple:2.0.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("de.skycave.awsbackup.MainKt")
}

tasks.withType<ShadowJar> {
    archiveFileName.set("${project.name}-${project.version}.jar")
    manifest {
        attributes["Main-Class"] = "de.skycave.awsbackup.MainKt"
    }
}
