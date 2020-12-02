package club.geek66.command.executor

import arrow.core.MapK
import arrow.core.Nel
import java.io.InputStream

interface RequestSpecific {

	val environments: MapK<String, String>

	val timeout: Long

	val stdIn: () -> InputStream

	val commands: Nel<String>

	fun <T : RequestSpecific> T.fork(apply: T.() -> T): T = apply()

}