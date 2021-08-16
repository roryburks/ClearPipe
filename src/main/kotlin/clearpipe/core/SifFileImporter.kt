package clearpipe.core

import clearpipe.core.dependencyInjection.disetPlatform
import spirite.core.file.contracts.SifFile
import spirite.core.file.load.SifFileReader

interface ISifFileImporter {
    fun import(file: String) : SifFile
}

class SifFileImporter(
    private val _fileFactory : IFileSteamFactory
) :ISifFileImporter {
    override fun import(file: String): SifFile {
        val read = _fileFactory.getReadStream(file)
        return SifFileReader.read(read)
    }
}

object SifFileImporterProvider {
    val importer by lazy { SifFileImporter(disetPlatform.fileFactory) }
}