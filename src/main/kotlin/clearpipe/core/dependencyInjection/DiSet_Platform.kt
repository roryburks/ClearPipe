package clearpipe.core.dependencyInjection

import clearpipe.core.IFileSteamFactory

lateinit var disetPlatform: IDiSet_Platform

interface IDiSet_Platform {
    val fileFactory : IFileSteamFactory
}