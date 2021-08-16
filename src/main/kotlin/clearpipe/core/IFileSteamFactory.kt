package clearpipe.core

import rb.file.IReadStream

interface IFileSteamFactory {
    fun getReadStream(file: String) : IReadStream
}