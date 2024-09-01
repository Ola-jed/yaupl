package io

import java.io.File
import java.util.*

class FilePathResolver(scriptPath: String) {
    private val scriptAbsolutePath: String = File(scriptPath).absolutePath

    fun inferFileAbsolutePath(relativePath: String): String? {

        try {
            val pathStack = Stack<String>()
            pathStack.addAll(scriptAbsolutePath.split(File.separator))
            if (!pathStack.empty()) {
                pathStack.pop()
            }

            val relativePathComponents = relativePath.split(File.separator)
            for (relativePathComponent in relativePathComponents) {
                if (relativePathComponent == ".") {
                    continue
                }

                if (relativePathComponent == "..") {
                    pathStack.pop()
                } else {
                    pathStack.push(relativePathComponent)
                }
            }

            return pathStack.joinToString(File.separator)
        } catch (_: EmptyStackException) {
            return null
        }
    }
}