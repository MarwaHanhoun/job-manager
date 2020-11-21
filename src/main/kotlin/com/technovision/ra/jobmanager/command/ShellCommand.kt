package com.technovision.ra.jobmanager.command

import java.io.File

class ShellCommand(private val config: CommandConfig.ShellConfig) : Command {
    override var success: Boolean = true
    override fun exec(): String = ProcessBuilder().command(config.script)
                    .directory(File(config.workingDirectory)).start().let {
                        it.inputStream.reader().use { res ->
                            res.readText()
                        }.apply {
                            success = it.waitFor() == 0
                        }
                    }
}