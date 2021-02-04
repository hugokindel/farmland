import org.gradle.internal.os.OperatingSystem

plugins {
    java
    application
}

plugins.apply("java")

// Project variables.
group = "com.ustudents"
version = "1.0"

// LWJGL variables (version and operating system type for dependencies).
var os = OperatingSystem.current()
var lwjglVersion = "3.2.3"
var lwjglNatives = "natives-windows"
if (os.isLinux) {
    lwjglNatives = "natives-linux"
} else if (os.isMacOsX) {
    lwjglNatives = "natives-macos"
}

// Set minimal JDK version.
java.sourceCompatibility = JavaVersion.VERSION_11

// Set main class.
application.mainClass.set("com.ustudents.farmland.Main")

repositories {
    // Maven repository.
    mavenCentral()
}

dependencies {
    // LWJGL libraries.
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-openal")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-stb")
    runtimeOnly("org.lwjgl:lwjgl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-openal::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-stb::$lwjglNatives")

    // JOML library (math computations).
    implementation("org.joml:joml:1.10.0")

    // JUnit libraries.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks {
    // Force unicode support.
    compileJava {
        options.encoding = "UTF-8"
    }
    compileTestJava {
        options.encoding = "UTF-8"
    }

    // JUnit support.
    "test"(Test::class) {
        useJUnitPlatform()
    }
}

// Gradle has an issue supporting unicode within Powershell or cmd.exe,
// you need to use `chcp 65001` to enable unicode characters
// (this is not an issue in distributed builds, only within gradle commands output).
application.applicationDefaultJvmArgs = listOf("-Dfile.encoding=utf-8")

if (project.gradle.startParameter.taskNames.contains("run") && System.getProperty("idea.vendor.name") == "JetBrains") {
    application.applicationDefaultJvmArgs = listOf("-Dfile.encoding=utf-8", "-Dide=JetBrains")
}
