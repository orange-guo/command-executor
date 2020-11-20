import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
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

dependencies {
	implementation("io.vavr:vavr:0.10.3")

	implementation("org.jetbrains.kotlin:kotlin-stdlib")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("com.jcabi:jcabi-ssh:1.6.1")

	implementation("io.arrow-kt:arrow-core:0.11.0")
	implementation("io.arrow-kt:arrow-syntax:0.11.0")
	kapt("io.arrow-kt:arrow-meta:0.11.0")

	testImplementation("org.jetbrains.kotlin:kotlin-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
}

plugins {
	kotlin("jvm") version "1.4.20"
	kotlin("kapt") version "1.4.20"
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()