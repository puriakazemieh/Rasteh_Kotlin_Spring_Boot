import java.math.BigDecimal
import java.math.RoundingMode
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element

plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.2.21"
    jacoco
}

group = "com.kazemieh.rasteh"
version = "0.0.1-SNAPSHOT"
description = "Rasteh"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    sourceCompatibility = JavaVersion.VERSION_17
}

val jwtVersion = "0.12.5"
val openApiWebMvc = "2.5.0"
val jupiterVersion = "5.10.2"
val assertjVersion = "3.25.3"
val mockkVersion = "1.13.11"
val testcontainersVersion = "2.0.5"

dependencies {
    // for actual implementation
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openApiWebMvc")
    implementation("io.jsonwebtoken:jjwt-api:$jwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jwtVersion")

    // databases
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.postgresql:postgresql")

    // testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.junit.jupiter:junit-jupiter:$jupiterVersion")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:testcontainers-postgresql:$testcontainersVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

val criticalPackageCoverage = mapOf(
    "com/kazemieh/rasteh/identity" to BigDecimal("0.30"),
    "com/kazemieh/rasteh/order" to BigDecimal("0.18"),
    "com/kazemieh/rasteh/payment" to BigDecimal("0.70"),
    "com/kazemieh/rasteh/wallet" to BigDecimal("0.45"),
)

val verifyCriticalPackageCoverage by tasks.registering {
    group = "verification"
    description = "Verifies aggregate line coverage for critical server package groups."
    dependsOn(tasks.jacocoTestReport)

    val reportFile = layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml")
    inputs.file(reportFile)

    doLast {
        val report = reportFile.get().asFile
        check(report.isFile) { "JaCoCo XML report was not generated: ${report.absolutePath}" }

        val documentBuilderFactory = DocumentBuilderFactory.newInstance().apply {
            setFeature("http://xml.org/sax/features/external-general-entities", false)
            setFeature("http://xml.org/sax/features/external-parameter-entities", false)
            setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        }
        val document = documentBuilderFactory.newDocumentBuilder().parse(report)
        val packages = document.getElementsByTagName("package")

        criticalPackageCoverage.forEach { (packagePrefix, minimum) ->
            var matchedPackages = 0
            var missedLines = 0
            var coveredLines = 0

            for (index in 0 until packages.length) {
                val packageElement = packages.item(index) as Element
                if (!packageElement.getAttribute("name").startsWith(packagePrefix)) continue

                matchedPackages++
                val children = packageElement.childNodes
                for (childIndex in 0 until children.length) {
                    val child = children.item(childIndex)
                    if (child is Element && child.tagName == "counter" && child.getAttribute("type") == "LINE") {
                        missedLines += child.getAttribute("missed").toInt()
                        coveredLines += child.getAttribute("covered").toInt()
                    }
                }
            }

            check(matchedPackages > 0) { "No JaCoCo packages matched $packagePrefix" }
            val totalLines = missedLines + coveredLines
            check(totalLines > 0) { "No executable lines found for $packagePrefix" }
            val ratio = coveredLines.toBigDecimal().divide(
                totalLines.toBigDecimal(),
                4,
                RoundingMode.HALF_UP,
            )
            check(ratio >= minimum) {
                "Aggregate line coverage for $packagePrefix is $ratio; required at least $minimum"
            }
            logger.lifecycle(
                "Aggregate line coverage for $packagePrefix: $ratio (minimum $minimum)",
            )
        }
    }
}

tasks.check {
    dependsOn(verifyCriticalPackageCoverage)
}
