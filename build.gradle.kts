import org.gradle.internal.os.OperatingSystem

plugins {
    java
    application
}

// Project variables.
group = "com.ustudents"
version = "1.0"

repositories {
    // Maven repository.
    mavenCentral()
}

val os: OperatingSystem = OperatingSystem.current()

dependencies {
    val lwjglVersion: String by project
    val imGuiVersion: String by project
    val jomlVersion: String by project
    val jUnitVersion: String by project

    var osType = ""
    when {
        os.isWindows -> {
            osType = "natives-windows"
        }
        os.isLinux -> {
            osType = "natives-linux"
        }
        os.isMacOsX -> {
            osType = "natives-macos"
        }
    }

    // LWJGL libraries.
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl:lwjgl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-openal:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
    implementation("org.lwjgl:lwjgl-stb:$lwjglVersion")
    runtimeOnly("org.lwjgl:lwjgl::$osType")
    runtimeOnly("org.lwjgl:lwjgl-glfw::$osType")
    runtimeOnly("org.lwjgl:lwjgl-openal::$osType")
    runtimeOnly("org.lwjgl:lwjgl-opengl::$osType")
    runtimeOnly("org.lwjgl:lwjgl-stb::$osType")

    // ImGui
    implementation("io.github.spair:imgui-java-binding:$imGuiVersion")
    implementation("io.github.spair:imgui-java-lwjgl3:$imGuiVersion")
    implementation("io.github.spair:imgui-java-$osType:$imGuiVersion")

    // JOML library (math computations).
    implementation("org.joml:joml:$jomlVersion")

    // JUnit libraries.
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")
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

    // Set main class to run.
    jar {
        manifest {
            attributes(
                    "Main-Class" to "com.ustudents.farmland.Main"
            )
        }
    }
}

// Changes the standard input (useful because Gradle can hide the input in some cases).
val run by tasks.getting(JavaExec::class) {
    standardInput = System.`in`
}

// Set main class.
application.mainClass.set("com.ustudents.farmland.Main")

// List of JVM options to pass.
var jvmOptions = mutableListOf<String>()
// Gradle has an issue supporting unicode within Powershell or cmd.exe,
// you need to use `chcp 65001` to enable unicode characters
// (this is not an issue in distributed builds, only within gradle commands output).
jvmOptions.add("-Dfile.encoding=utf-8")
// Pass an IDE name information to know within the engine's code if we are debugging within an IDE.
if (project.gradle.startParameter.taskNames.contains("run") && System.getProperty("idea.vendor.name") == "JetBrains") {
    jvmOptions.add("-Dide=JetBrains")
}
// Needed by LWJGL to create the GLFW window.
if (os.isMacOsX) {
    jvmOptions.add("-XstartOnFirstThread")
}
application.applicationDefaultJvmArgs = jvmOptions

// Set minimal JDK version.
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}