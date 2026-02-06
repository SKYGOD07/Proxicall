plugins {
    id("com.android.application") version "8.3.2" apply false
    id("com.android.library") version "8.3.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
}

subprojects {
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    configurations.all {
        resolutionStrategy {
            force(
                "org.jetbrains.kotlin:kotlin-stdlib:1.9.23",
                "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.23",
                "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.23",
                "org.jetbrains.kotlin:kotlin-reflect:1.9.23"
            )
        }
    }
}
