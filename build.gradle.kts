import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"
}

group = "com.luissoares"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("io.javalin:javalin:3.+")
    implementation("org.slf4j:slf4j-simple:1.+")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.+")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.+")

    implementation("org.jetbrains.exposed:exposed:0.17.+")
    implementation("mysql:mysql-connector-java:8.+")
    implementation("org.litote.kmongo:kmongo:3.+")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.+")
    testImplementation("io.mockk:mockk:1.+")
    testImplementation("org.skyscreamer:jsonassert:1.+")

    testImplementation("com.wix:wix-embedded-mysql:4.+")
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:2.+")
}

tasks.withType<Test> {
    useJUnitPlatform {
    }
}
