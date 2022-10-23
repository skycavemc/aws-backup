package de.skycave.awsbackup

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import de.skycave.awsbackup.uploading.Uploader
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SkyCaveAWSBackup {

    companion object {
        const val DEFAULT_VAULT = "skycave-backup"
    }

    lateinit var client: AmazonS3
        private set
    private lateinit var uploader: Uploader

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun onStart() {
        val credentials = BasicAWSCredentials(
            System.getenv("SC_AWS_ACCESS_KEY"), System.getenv("SC_AWS_SECRET_KEY")
        )
        client = AmazonS3ClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.EU_CENTRAL_1)
            .build()

        if (!client.doesBucketExistV2(DEFAULT_VAULT)) {
            client.createBucket(DEFAULT_VAULT)
        }

        uploader = Uploader(this)
    }

    fun accept(args: Array<String>) {
        if (args.isNotEmpty()) {
            logger.info("Trying to upload ${args[0]} now.")
            uploader.upload(args[0])
        }
    }

    fun onExit() {
        client.shutdown()
    }

}