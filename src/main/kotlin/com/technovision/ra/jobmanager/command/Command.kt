package com.technovision.ra.jobmanager.command

interface Command {
    var success: Boolean
    fun exec():String
}