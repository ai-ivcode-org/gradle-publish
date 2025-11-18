package org.ivcode.gradle.actions

import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import com.github.mustachejava.DefaultMustacheFactory

class ActionsWriter(
    private val file: File
) {

    /**
     * Render the template at [templatePath] with the provided [model] and write the result to [file].
     *
     * The templatePath is resolved in the following order:
     * 1) If a file exists at the given path on the filesystem, it is used.
     * 2) Otherwise the path is treated as a classpath resource and looked up with the classloader.
     *
     * The [model] is passed directly to the Mustache engine (a Map or a POJO are supported).
     */
    fun writeFromTemplate(templatePath: String, model: Map<String, Any?> = emptyMap()) {
        val rendered = renderTemplate(templatePath, model)
        file.parentFile?.mkdirs()
        file.writeText(rendered)
    }

    private fun renderTemplate(templatePath: String, model: Any): String {
        val mustacheFactory = DefaultMustacheFactory()

        val reader = when {
            File(templatePath).exists() -> FileReader(File(templatePath), StandardCharsets.UTF_8)
            else -> {
                val stream = this::class.java.classLoader.getResourceAsStream(templatePath)
                    ?: this::class.java.classLoader.getResourceAsStream("/$templatePath")
                    ?: throw IllegalArgumentException("Template not found on classpath or filesystem: $templatePath")
                InputStreamReader(stream, StandardCharsets.UTF_8)
            }
        }

        reader.use {
            val mustache = mustacheFactory.compile(it, templatePath)
            val writer = StringWriter()
            mustache.execute(writer, model).flush()
            return writer.toString()
        }
    }
}

internal fun main() {
    ActionsWriter(File(OUTPUT_PATH)).writeFromTemplate(TEMPLATE_PATH, emptyMap())
}
