import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
	extra["vavrVersion"] = "0.10.3"
	extra["jcabiVersion"] = "1.6.1"
	extra["arrowVersion"] = "0.11.0"
	extra["jcabiVersion"] = "1.6.1"
	extra["kotlinVersion"] = "1.4.30"
	repositories {
		maven { setUrl("http://maven.aliyun.com/nexus/content/groups/public/") }
		maven { setUrl("https://kotlin.bintray.com/kotlinx") }
		jcenter()
	}
}

repositories {
	maven { setUrl("http://maven.aliyun.com/nexus/content/groups/public/") }
	maven { setUrl("https://kotlin.bintray.com/kotlinx") }
	jcenter()
}

val vavrVersion: String by extra
val jcabiVersion: String by extra
val arrowVersion: String by extra
var kotlinVersion: String by extra

dependencies {
	implementation("io.vavr:vavr:$vavrVersion")

	implementation("org.jetbrains.kotlin:kotlin-stdlib")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("com.jcabi:jcabi-ssh:$jcabiVersion")

	implementation("io.arrow-kt:arrow-core:$arrowVersion")
	implementation("io.arrow-kt:arrow-syntax:$arrowVersion")
	kapt("io.arrow-kt:arrow-meta:0.11.0:$arrowVersion")

	testImplementation("org.jetbrains.kotlin:kotlin-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
}

plugins {
	kotlin("jvm") version "1.4.21"
	kotlin("kapt") version "1.4.21"
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()