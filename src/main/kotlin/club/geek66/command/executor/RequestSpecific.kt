package club.geek66.command.executor

import arrow.core.MapK
import arrow.core.Nel
import java.io.InputStream

interface RequestSpecific<T : RequestSpecific<T>> {

	val environments: MapK<String, String>

	val timeout: Long

	val stdIn: () -> InputStream

	val commands: Nel<String>

	@Suppress(names = ["UNCHECKED_CAST"])
	fun fork(apply: T.() -> T): T =
		(this as T).apply()

}