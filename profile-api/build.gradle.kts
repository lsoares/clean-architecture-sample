plugins {
    kotlin("jvm") version "1.4.30"
}

group = "com.luissoares"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.javalin:javalin:3.+")
    implementation("org.slf4j:slf4j-simple:1.+")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.+")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.+")
    testImplementation("au.com.dius.pact.provider:junit5:4.+")
}

tasks.withType<Test> {
    useJUnitPlatform()
}