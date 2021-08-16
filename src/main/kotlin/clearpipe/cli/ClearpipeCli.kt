package clearpipe.cli

import clearpipe.core.SifFileImporterProvider
import clearpipe.core.dependencyInjection.disetPlatform
import clearpipe.jvm.JvmPlatformDi

fun main(args: Array<String>) {
    init()

    val file = "S:\\Fauna\\Art\\Doe\\Taur\\move\\trot.sif"
    val importer = SifFileImporterProvider.importer
    val sif = importer.import(file)
    println("breakpoint here")
}

fun init(){
    disetPlatform = JvmPlatformDi
}