plugins {
	java
	id("org.springframework.boot") version "4.0.5"         // https://plugins.gradle.org/plugin/org.springframework.boot
	id("io.spring.dependency-management") version "1.1.7"  // https://plugins.gradle.org/plugin/io.spring.dependency-management
}

group = "com.jordanpaille"
version = "0.0.1-SNAPSHOT"
description = "project for housing-coop"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")

	// Logging
	implementation("ch.qos.logback:logback-classic:1.5.32")                            // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic

	// Lombok
	compileOnly("org.projectlombok:lombok:1.18.44")
	annotationProcessor("org.projectlombok:lombok:1.18.44")


	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("ch.qos.logback:logback-classic:1.5.32")
	testCompileOnly("org.projectlombok:lombok:1.18.44")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.44")

}

tasks.withType<Test> {
	useJUnitPlatform()
}
