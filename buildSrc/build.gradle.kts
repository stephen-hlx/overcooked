plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal() // so that external plugins can be resolved in dependencies section
}

dependencies {
    // https://mvnrepository.com/artifact/com.github.spotbugs.snom/spotbugs-gradle-plugin
    implementation("com.github.spotbugs.snom:spotbugs-gradle-plugin:5.2.1")

    // https://mvnrepository.com/artifact/io.freefair.lombok/io.freefair.lombok.gradle.plugin
    implementation("io.freefair.lombok:io.freefair.lombok.gradle.plugin:8.4")
}
