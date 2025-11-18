package org.ivcode.gradle.actions

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.Action
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile

const val TEMPLATE_PATH = "gradle-actions/publish.yml.mustache"
const val OUTPUT_PATH = ".github/workflows/publish.yml"

class ActionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("generate-actions", ActionsTask::class.java, object : Action<ActionsTask> {
            override fun execute(task: ActionsTask) {
                task.group = "org.ivcode"
                task.description = "generates github actions file"

                // configure the output file inside registration (relative to project root)
                val regFile = project.file(OUTPUT_PATH)
                task.outputFile.set(regFile)
            }
        })
    }
}

abstract class ActionsTask : DefaultTask() {
    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun executeAction() {
        val out = outputFile.get().asFile
        val model = mapOf(
            "projectName" to project.name,
            "version" to project.version.toString()
        )
        val writer = ActionsWriter(out)
        writer.writeFromTemplate(TEMPLATE_PATH, model)
        logger.lifecycle("Wrote generated file: ${out.absolutePath}")
    }
}
