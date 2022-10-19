package de.skycave.awsbackup

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.glacier.AmazonGlacier
import com.amazonaws.services.glacier.AmazonGlacierClientBuilder
import de.skycave.awsbackup.uploading.Uploader
import java.io.File

class SkyCaveAWSBackup {

    companion object {
        const val DEFAULT_VAULT = "sc_backup"
    }

    private val client: AmazonGlacier
    private val uploader: Uploader

    init {
        val credentials = BasicAWSCredentials(
            System.getenv("SC_AWS_ACCESS_KEY"), System.getenv("SC_AWS_SECRET_KEY")
        )
        client = AmazonGlacierClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.EU_CENTRAL_1)
            .build()
        uploader = Uploader(client)

        // testing
        testUpload()
        client.shutdown()
    }

    private fun testUpload() {
        println(uploader.uploadContent(File("largefile")))
    }

}