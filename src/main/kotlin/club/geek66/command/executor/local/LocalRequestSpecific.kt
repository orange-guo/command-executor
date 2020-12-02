package club.geek66.command.executor.local

import arrow.core.MapK
import arrow.core.Nel
import arrow.core.k
import club.geek66.command.executor.RequestSpecific
import java.io.InputStream

data class LocalRequestSpecific(
	override val environments: MapK<String, String> = emptyMap<String, String>().k(),
	override val timeout: Long = 10000,
	override val stdIn: () -> InputStream = InputStream::nullInputStream,
	override val commands: Nel<String>,
	val workDir: String,
) : RequestSpecific