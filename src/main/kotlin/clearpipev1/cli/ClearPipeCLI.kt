package clearpipev1.cli

import clearpipev1.cli.commands.ParseExportAafCommand
import java.io.File

fun main(args : Array<String>) {
    ParseExportAafCommand.execute(
        listOf(File("S:\\Fauna\\Res\\SRF")),
        File("E:\\Bucket\\sif\\export.csv") )

}
