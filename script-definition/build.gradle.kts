plugins {
    kotlin("jvm")
}
/*
group = "org.example"
version = "1.0-SNAPSHOT"
*/


dependencies {
 //   testImplementation(kotlin("test"))
 //   implementation("org.jetbrains.kotlin:kotlin-scripting-common")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
 //   implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies")
 //   implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies-maven")
    // coroutines dependency is required for this particular definition
 //   implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

}

/*
tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

*/