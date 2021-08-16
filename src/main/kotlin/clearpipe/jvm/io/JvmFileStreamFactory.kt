package clearpipe.jvm.io

import clearpipe.core.IFileSteamFactory
import rb.file.BufferedReadStream
import rb.file.IReadStream
import rbJvm.file.JvmRandomAccessFileBinaryReadStream
import java.io.File
import java.io.RandomAccessFile

object JvmFileStreamFactory : IFileSteamFactory {
    override fun getReadStream(file: String): IReadStream {
        val f = File(file)
        val ra = RandomAccessFile(f, "r")
        return BufferedReadStream(JvmRandomAccessFileBinaryReadStream(ra))
    }
}