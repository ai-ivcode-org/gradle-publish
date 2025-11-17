group = "org.ivcode"
version = "0.1-SNAPSHOT"

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
    `maven-publish`
}

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
}

publishing {
    val directory = if (version.toString().endsWith("-SNAPSHOT")) "snapshot" else "release"
    val mvnUri = uri("s3://maven.ivcode.org/$directory/")

    repositories {
        maven {
            url = mvnUri
            if(System.getenv("AWS_ACCESS_KEY_ID") != null && System.getenv("AWS_SECRET_ACCESS_KEY") != null) {
                credentials(AwsCredentials::class.java) {
                    accessKey = System.getenv("AWS_ACCESS_KEY_ID")
                    secretKey = System.getenv("AWS_SECRET_ACCESS_KEY")
                }
            }
        }
    }
}

gradlePlugin {
    plugins {
        create("gradle-publish") {
            id = "org.ivcode.gradle-publish"
            implementationClass = "org.ivcode.gradle.publish.PublishPlugin"
        }
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.0")
}

tasks.test {
    useJUnitPlatform()
}