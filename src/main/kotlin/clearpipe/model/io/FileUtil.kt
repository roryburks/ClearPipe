package clearpipe.model.io

import rb.vectrix.mathUtil.i
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.RandomAccessFile
import java.nio.charset.Charset

internal object FileConsts {
    val ColKind_Point = 0
    val ColKind_RigidRect = 1
    val ColKind_Circle = 2
    val ColKind_Arc = 3
    val ColKind_LineSegment = 4
    val ColKind_RayRect = 5
    val ColKind_Polygon = 6
}

fun RandomAccessFile.readUTF8nt() : String {
    val bos = ByteArrayOutputStream()
    var b = this.readByte()
    while( b != 0x00.toByte()) {
        bos.write(b.i)
        b = this.readByte()
    }

    return bos.toString("UTF-8")
}


fun getAafFiles(file: File) : Pair<File, File> {
    val filename = file.absolutePath
    return when(val ext = file.extension.toLowerCase()) {
        "png" -> Pair(file, File(filename.substring(0, filename.length-3)+"aaf"))
        else -> Pair(File(filename.substring(0, filename.length - ext.length) + "png"), file)
    }
}

fun RandomAccessFile.writeUTF8nt(str: String) {
    val b = (str + 0.toChar()).toByteArray(Charset.forName("UTF-8"))

    // Convert non-terminating null characters to whitespace
    val nil : Byte = 0
    for( i in 0 until b.size-1) {
        if( b[i] == nil)
            b[i] = 0x20
    }

    write(b)
}