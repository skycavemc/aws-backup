package de.skycave.awsbackup.uploading

import com.amazonaws.services.glacier.AmazonGlacier
import com.amazonaws.services.glacier.model.UploadArchiveRequest
import com.amazonaws.util.BinaryUtils
import de.skycave.awsbackup.SkyCaveAWSBackup
import de.skycave.awsbackup.utils.Utils
import java.io.File
import java.io.FileNotFoundException

class Uploader(
    private val glacier: AmazonGlacier
) {

    /**
     * Uploads content to the given glacier vault.
     */
    fun uploadContent(file: File, vaultName: String = SkyCaveAWSBackup.DEFAULT_VAULT): String {
        // Get an SHA-256 tree hash value.
        if (!file.isFile) {
            throw FileNotFoundException("File ${file.absolutePath} not found.")
        }
        val chunkSHA256Hashes = Utils.getChunkSHA256Hashes(file)
        val treeHash = Utils.computeSHA256TreeHash(chunkSHA256Hashes) ?: throw RuntimeException("treeHash is null")
        val checkVal = BinaryUtils.toHex(treeHash)
        println("SHA-256 tree hash = $checkVal")

        val uploadRequest = UploadArchiveRequest()
        uploadRequest.vaultName = vaultName
        uploadRequest.accountId = "-"
        uploadRequest.checksum = checkVal
        uploadRequest.body = file.inputStream()
        uploadRequest.contentLength = file.length()
        val res = glacier.uploadArchive(uploadRequest)
        return res.archiveId
    }

}