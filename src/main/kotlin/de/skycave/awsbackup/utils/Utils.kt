package de.skycave.awsbackup.utils

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest

object Utils {
    private const val ONE_MB = 1024 * 1024

    /**
     * Computes an SHA256 checksum for each 1 MB chunk of the input file. This
     * includes the checksum for the last chunk, even if it's smaller than 1 MB.
     */
    fun getChunkSHA256Hashes(file: File): Array<ByteArray?> {
        val md = MessageDigest.getInstance("SHA-256")
        var numChunks: Long = file.length() / ONE_MB
        if (file.length() % ONE_MB > 0) {
            numChunks++
        }
        if (numChunks == 0L) {
            return arrayOf(md.digest())
        }
        val chunkSHA256Hashes = arrayOfNulls<ByteArray>(numChunks.toInt())
        var fileStream: FileInputStream? = null
        return try {
            fileStream = FileInputStream(file)
            val buff = ByteArray(ONE_MB)
            var bytesRead: Int
            var idx = 0
            while (fileStream.read(buff, 0, ONE_MB).also { bytesRead = it } > 0) {
                md.reset()
                md.update(buff, 0, bytesRead)
                chunkSHA256Hashes[idx++] = md.digest()
            }
            chunkSHA256Hashes
        } finally {
            if (fileStream != null) {
                try {
                    fileStream.close()
                } catch (ioe: IOException) {
                    System.err.printf(
                        "Exception while closing %s.\n %s", file.name,
                        ioe.message
                    )
                }
            }
        }
    }

    /**
     * Computes the SHA-256 tree hash for the passed array of 1 MB chunk
     * checksums.
     */
    fun computeSHA256TreeHash(chunkSHA256Hashes: Array<ByteArray?>): ByteArray? {
        val md = MessageDigest.getInstance("SHA-256")
        var prevLvlHashes = chunkSHA256Hashes
        while (prevLvlHashes.size > 1) {
            var len = prevLvlHashes.size / 2
            if (prevLvlHashes.size % 2 != 0) {
                len++
            }
            val currLvlHashes = arrayOfNulls<ByteArray>(len)
            var j = 0
            var i = 0
            while (i < prevLvlHashes.size) {


                // If there are at least two elements remaining.
                if (prevLvlHashes.size - i > 1) {

                    // Calculate a digest of the concatenated nodes.
                    md.reset()
                    md.update(prevLvlHashes[i])
                    md.update(prevLvlHashes[i + 1])
                    currLvlHashes[j] = md.digest()
                } else { // Take care of the remaining odd chunk
                    currLvlHashes[j] = prevLvlHashes[i]
                }
                i += 2
                j++
            }
            prevLvlHashes = currLvlHashes
        }
        return prevLvlHashes[0]
    }
}