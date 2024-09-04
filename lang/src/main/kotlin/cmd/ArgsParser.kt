package cmd

class ArgsParser(private val args: Array<String>) {
    fun getFileToRun(): String? {
        return args.firstOrNull { it[0] != '-' || it[1] != '-' }
    }

    fun hasOption(option: String): Boolean {
        return args.any { it == "--$option" }
    }

    fun getOptionValue(option: String): String? {
        return args.filter { it.startsWith("--$option=") }.map { it.split("=").last() }.firstOrNull()
    }

    companion object {
        const val OPTION_HELP = "help"
        const val OPTION_ERROR_LOG = "error-log"
    }
}