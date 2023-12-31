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

    implementation("com.pulumi:pulumi:(,1.0]")
    implementation("com.pulumi:docker:4.2.4")
}

application {
    mainClass.set("nm.HostKt")
}

tasks.withType<Jar> {

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes("Main-Class" to "nm.HostKt")
    }

    from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
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
        workingDirectory = "/github/workspace"
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


