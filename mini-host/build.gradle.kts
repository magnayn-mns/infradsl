plugins {
    kotlin("jvm")
    application

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
    mainClass.set("com.nirima.infradsl.HostKt")
}



