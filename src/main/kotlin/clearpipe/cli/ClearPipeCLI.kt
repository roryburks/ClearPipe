package clearpipe.cli

import clearpipe.cli.commands.ParseExportAafCommand
import java.io.File

fun main(args : Array<String>) {
    ParseExportAafCommand.execute(
        listOf(File("S:\\Fauna\\Res\\AAF\\enemies")),
        File("E:\\Bucket\\sif\\export.csv") )

}
