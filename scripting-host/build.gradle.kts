plugins {
    kotlin("jvm")
    application
    id("com.google.cloud.tools.jib") version "3.3.2"
}

val kotlinVersion: String by rootProject.extra

dependencies {
    implementation(project(":script-definition"))
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-main-kts:$kotlinVersion")
    testImplementation("junit:junit:4.12")
    testRuntimeOnly("org.slf4j:slf4j-nop:1.7.28")
}

application {
    mainClass.set("nm.HostKt")
}


jib {
    from {
        image = "azul/zulu-openjdk-alpine:17"
        platforms {
            platform {
                architecture = "arm64"
                os = "linux"
            }
            platform {
                architecture = "amd64"
                os = "linux"
            }
        }
    }
    to {
        image = "ghcr.io/magnayn-mns/infradsl"
    }
    container {
        jvmFlags = listOf(
            "-Xms512m",
            "-Xdebug",
            "-Dmicronaut.config.files=/version.properties")

        mainClass = "nm.HostKt"
      //  ports = listOf("8080")
      //  creationTime = java.time.Instant.now().toString()
        labels = mapOf("org.opencontainers.image.source" to "https://github.com/magnayn-mns/infradsl")
        user = "1000"
    }

    extraDirectories {
        paths {
            path {
                setFrom("src/main/scripts")
                into = "/"

            }
        }
    }
}


