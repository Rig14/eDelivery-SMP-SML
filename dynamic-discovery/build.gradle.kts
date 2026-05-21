plugins {
  kotlin("jvm") version "2.3.0"
}

repositories {
  mavenCentral()
  maven { url = uri("https://jitpack.io") }
}

dependencies {
  fun klite(module: String) = "com.github.keksworks.klite:klite-$module:1.8.2"
  implementation(klite("server"))
  implementation(klite("slf4j"))

  implementation("dnsjava:dnsjava:3.6.4")

  testImplementation("org.junit.jupiter:junit-jupiter:6.1.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.1.0")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.1.0")
  testImplementation("ch.tutteli.atrium:atrium-fluent:1.3.0-alpha-2")
  testImplementation("io.mockk:mockk:1.14.9")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
}

kotlin {
  jvmToolchain(25)
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
  workingDir(rootDir)
  useJUnitPlatform()
  jvmArgs("-DENV=test", "--add-opens=java.base/java.lang=ALL-UNNAMED", "-XX:-OmitStackTraceInFastThrow")
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
      attributes(
        mapOf(
          "Main-Class" to mainClassName,
          "Class-Path" to File("$buildDir/libs/deps").listFiles()?.joinToString(" ") { "deps/${it.name}" }
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