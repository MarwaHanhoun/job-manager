package com.technovision.ra.jobmanager.command

object NOOP : Command {
    override var success: Boolean = true
    override fun exec(): String = ""
}