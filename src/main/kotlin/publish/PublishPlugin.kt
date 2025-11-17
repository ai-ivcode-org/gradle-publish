package org.ivcode.gradle.publish

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.credentials.AwsCredentials
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import java.net.URI

class PublishPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        applyJavaPlugin(project)
        applyPublishPlugin(project)
    }

    private fun applyJavaPlugin(project: Project) {
        project.plugins.apply("java")

        // ensure a sources JAR is produced and attached to the java component
        project.extensions.configure(JavaPluginExtension::class.java) {
            withSourcesJar()
        }
    }

    private fun applyPublishPlugin(project: Project) {
        project.plugins.apply("maven-publish")

        project.afterEvaluate {
            // ensure a sources JAR is produced and attached to the java component
            project.extensions.configure(JavaPluginExtension::class.java) {
                withSourcesJar()
            }

            val awsKey = System.getenv("AWS_ACCESS_KEY_ID")
            val awsSecret = System.getenv("AWS_SECRET_ACCESS_KEY")

            project.extensions.configure(PublishingExtension::class.java) {
                publications {
                    create("org.ivcode", MavenPublication::class.java) {
                        groupId = project.group.toString()
                        artifactId = project.name
                        version = project.version.toString()

                        val componentName = if (project.plugins.hasPlugin("java")) "java" else "kotlin"
                        from(project.components.getByName(componentName))
                    }
                }

                repositories {
                    maven {
                        this.name = "org.ivcode"
                        this.url = getUri(project)

                        if(awsKey != null && awsSecret != null) {
                            credentials (AwsCredentials::class.java) {
                                accessKey = awsKey
                                secretKey = awsSecret
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getUri(project: Project): URI {
        val directory = if (project.version.toString().endsWith("-SNAPSHOT")) "snapshot" else "release"
        return URI("s3://maven.ivcode.org/$directory/")
    }
}