plugins {
    val kotlinVersion = "1.4.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.9.2"
}

group = "zhu.moon"
version = "0.1.3"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

val gsonVersion = "2.8.9"

dependencies {
    implementation("com.google.code.gson:gson:$gsonVersion")
}