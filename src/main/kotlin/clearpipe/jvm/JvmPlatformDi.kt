package clearpipe.jvm

import clearpipe.core.IFileSteamFactory
import clearpipe.core.dependencyInjection.IDiSet_Platform
import clearpipe.jvm.io.JvmFileStreamFactory

object JvmPlatformDi :IDiSet_Platform {
    override val fileFactory by lazy {  JvmFileStreamFactory }
}