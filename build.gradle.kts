plugins {
    kotlin("multiplatform") version "1.4.0" apply false
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
