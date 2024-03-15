/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.3/userguide/building_java_projects.html in the Gradle documentation.
 */

plugins {
    id("overcooked.java-conventions")
}

version = "0.0.1"

dependencies {
    // This dependency is used by the application.
    implementation("com.google.guava:guava:32.1.1-jre")

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.10.1")

    // https://mvnrepository.com/artifact/org.jgrapht/jgrapht-core
    implementation("org.jgrapht:jgrapht-core:1.5.2")

    // https://mvnrepository.com/artifact/org.jgrapht/jgrapht-io
    implementation("org.jgrapht:jgrapht-io:1.5.2")

    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.9")

    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")

    // https://mvnrepository.com/artifact/org.assertj/assertj-core
    testImplementation("org.assertj:assertj-core:3.6.1")

    // https://mvnrepository.com/artifact/org.mockito/mockito-core
    testImplementation("org.mockito:mockito-core:2.1.0")

    // https://mvnrepository.com/artifact/org.slf4j/slf4j-jdk14
    testImplementation("org.slf4j:slf4j-jdk14:2.0.9")

    testImplementation(project(":sample"))
    testImplementation(project(":analysis"))

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
