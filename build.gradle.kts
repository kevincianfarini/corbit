plugins {
    kotlin("multiplatform") version "1.3.72"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
}

allprojects {
    repositories {
        jcenter()
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}
