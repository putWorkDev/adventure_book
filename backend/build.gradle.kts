plugins {
	java
	id("org.springframework.boot") version "4.1.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.openapi.generator") version "7.25.0" // Generate API client code from OpenAPI specification
}

group = "com.game"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	runtimeOnly("org.flywaydb:flyway-database-postgresql")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.testcontainers:junit-jupiter:1.21.3")
	testImplementation("org.testcontainers:postgresql:1.21.3")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// Generates Spring controller interfaces + DTOs from the API spec (API-first).
openApiGenerate {
	generatorName.set("spring")
	inputSpec.set("$projectDir/src/main/resources/openapi/adventure-book-api.yaml")
	outputDir.set(layout.buildDirectory.dir("generated/openapi"))
	apiPackage.set("com.game.adventure_book.api")
	modelPackage.set("com.game.adventure_book.api.model")
	modelNameSuffix.set("Dto")
	cleanupOutput.set(true)
	configOptions.set(
		mapOf(
			"useSpringBoot4" to "true",
			"useJackson3" to "true",
			"interfaceOnly" to "true",
			"skipDefaultInterface" to "true",
			"openApiNullable" to "false",
			"documentationProvider" to "none",
			"annotationLibrary" to "none",
		),
	)
}

// Wiring the task output (not a plain path) makes compileJava depend on openApiGenerate automatically.
sourceSets.main {
	java.srcDir(tasks.openApiGenerate.map { "${it.outputDir.get()}/src/main/java" })
}
