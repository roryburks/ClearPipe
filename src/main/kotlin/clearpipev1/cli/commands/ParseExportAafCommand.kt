package clearpipev1.cli.commands

import rb.animo.io.aaf.AafFile
import rb.animo.io.aaf.reader.AafReaderFactory
import rb.file.BufferedReadStream
import rbJvm.file.JvmRandomAccessFileBinaryReadStream
import rbJvm.file.util.toBufferedRead
import java.io.File
import java.io.RandomAccessFile

object ParseExportAafCommand {
    enum class ParseExportFormat {
        Csv
    }

    data class ParseExportAafParams(
        val format : ParseExportFormat = ParseExportFormat.Csv ,
        val includeSubfolders : Boolean = true)

    data class ParsedAafFile(
        val filename: String,
        val aafFile: AafFile)

    fun execute( filesToParse : List<File>, outFile: File, params: ParseExportAafParams = ParseExportAafParams())
    {
        val aafs = getAafs(filesToParse, params)
        export(aafs, outFile, params)
    }

    fun getAafs(filesToParse : List<File>, params: ParseExportAafParams = ParseExportAafParams()) : List<ParsedAafFile>{
        val aafs = mutableListOf<ParsedAafFile>()

        fun runOnFile(file: File) {
            if( !file.exists()) {
                println("File/Folder ${file.absolutePath} does not exist.")
            }
            else if( file.isFile){
                val aaf = parseFile(file) ?: return
                aafs.add(ParsedAafFile(file.name, aaf))
            }
            else if( file.isDirectory) {
                val baseFiles = file.listFiles()?.asList() ?: emptyList()
                val filesToRun =
                    if( params.includeSubfolders) baseFiles
                    else baseFiles.filter { !it.isDirectory }
                filesToRun.forEach { runOnFile(it) }
            }
        }
        filesToParse.forEach { runOnFile(it) }

        return aafs
    }

    fun export( aafs: List<ParsedAafFile>, outFile: File, params: ParseExportAafParams = ParseExportAafParams()) {
        val sb = StringBuilder()
        sb.appendln("FileName,Version,AnimName,Ox,Oy,FrameCt,Groups")
        for (aaf in aafs) {
            for( anim in aaf.aafFile.animations) {
                val groups = anim.frames
                    .flatMap { frame -> frame.chunks.map { it.group } }
                    .distinct()
                    .joinToString("")

                sb.appendln("${aaf.filename},${aaf.aafFile.version},${anim.name},${anim.ox},${anim.oy},${anim.frames.count()},${groups}")
            }
        }
        if( outFile.exists())
            outFile.delete()
        outFile.writeText(sb.toString())
    }

    fun parseFile( file: File) : AafFile? {
        return try {
            val reader = file.toBufferedRead()
            try {
                val aafReader = AafReaderFactory.readVersionAndGetReader(reader)
                aafReader.read(reader)
            }finally {
                reader.close()
            }
        }catch (e: Throwable) {
            null
        }
    }
}