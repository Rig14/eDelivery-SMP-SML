plugins {
    kotlin("jvm") version "2.3.0"
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    fun klite(module: String) = "com.github.keksworks.klite:klite-$module:1.7.2"
    implementation(klite("server"))
    implementation(klite("slf4j"))
}

kotlin {
    jvmToolchain(21)
}

sourceSets {
    main {
        kotlin.setSrcDirs(listOf("src"))
        resources.setSrcDirs(listOf("src")).exclude("**/*.kt")
    }
    test {
        kotlin.setSrcDirs(listOf("test"))
        resources.setSrcDirs(listOf("test")).exclude("**/*.kt")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Copy>("deps") {
    into("$buildDir/libs/deps")
    from(configurations.runtimeClasspath)
}

val mainClassName = "LauncherKt"

tasks.jar {
    dependsOn("deps")
    doFirst {
        manifest {
            attributes(mapOf(
                "Main-Class" to mainClassName,
                "Class-Path" to File("$buildDir/libs/deps").listFiles()?.joinToString(" ") { "deps/${it.name}"}
            ))
        }
    }
}

tasks.register<JavaExec>("run") {
    workingDir(rootDir)
    jvmArgs("--add-exports=java.base/sun.net.www=ALL-UNNAMED")
    mainClass.set(mainClassName)
    classpath = sourceSets.main.get().runtimeClasspath
}