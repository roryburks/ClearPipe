package clearpipe.model.io

import rb.vectrix.mathUtil.i
import java.io.ByteArrayOutputStream
import java.io.RandomAccessFile

fun RandomAccessFile.readUTF8nt() : String {
    val bos = ByteArrayOutputStream()
    var b = this.readByte()
    while( b != 0x00.toByte()) {
        bos.write(b.i)
        b = this.readByte()
    }

    return bos.toString("UTF-8")
}