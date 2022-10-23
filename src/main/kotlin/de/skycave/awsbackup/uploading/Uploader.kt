package de.skycave.awsbackup.uploading

import de.skycave.awsbackup.SkyCaveAWSBackup
import java.io.File

class Uploader(
    private val scBackup: SkyCaveAWSBackup
) {

    /**
     * Uploads content to the given S3 bucket.
     */
    fun upload(fileName: String, vaultName: String = SkyCaveAWSBackup.DEFAULT_VAULT) {
        val file = File(fileName)
        if (!file.isFile) {
            scBackup.logger.error("File ${file.absolutePath} not found.")
            return
        }

        scBackup.client.putObject(
            vaultName,
            file.name,
            file
        )
        scBackup.logger.info("Upload complete.")
    }

}