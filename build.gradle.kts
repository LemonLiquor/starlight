plugins {
    val kotlinVersion = "1.4.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.9.2"
}

group = "zhu.moon"
//非覆盖打包会花费额外时间，于是决定不在命名上改动
version = "0.0.0"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

val gsonVersion = "2.8.9"
val quartzVersion = "2.3.2"
val jsoupVersion = "1.14.3"

dependencies {
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("org.quartz-scheduler:quartz:$quartzVersion")
    implementation("org.jsoup:jsoup:$jsoupVersion")
    implementation(fileTree("src/main/resources/libs/webp-imageio-core-0.1.3.jar"))
}