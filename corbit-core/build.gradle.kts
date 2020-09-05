plugins {
    kotlin("multiplatform")
}

group = "com.corbit.core"
version = "0.0.1"

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(project(":corbit-binary"))
                implementation(project(":corbit-bencoding"))
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
                implementation("com.squareup.okio:okio:2.8.0")
                implementation("com.squareup.okhttp3:okhttp:4.8.1")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
    }
}
