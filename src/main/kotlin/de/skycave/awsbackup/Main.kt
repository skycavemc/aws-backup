package de.skycave.awsbackup

import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val scBackup = SkyCaveAWSBackup()
    if (args.isEmpty()) {
        scBackup.logger.info("No arguments specified, exiting.")
        exitProcess(0)
    }

    scBackup.onStart()
    scBackup.accept(args)
    scBackup.onExit()
}