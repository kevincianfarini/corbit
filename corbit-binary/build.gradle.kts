plugins {
    kotlin("multiplatform")
}

group = "com.corbit"
version = "unspecified"

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    linuxX64("linux")

    sourceSets {

        all {
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
        }

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val linuxMain by getting {
            dependencies { }
        }
    }
}
