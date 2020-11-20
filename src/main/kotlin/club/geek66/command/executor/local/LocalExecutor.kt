package club.geek66.command.executor.local

import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.toTuple2
import arrow.typeclasses.Show
import club.geek66.command.executor.CommandResponse
import club.geek66.command.executor.DefaultResponseSpecification
import club.geek66.command.executor.ExecutionError
import club.geek66.command.executor.Executor
import club.geek66.command.executor.ResponseSpecification
import io.vavr.concurrent.Future
import io.vavr.control.Try
import java.io.File
import java.util.concurrent.TimeUnit

object EnvPairShow : Show<Tuple2<String, String>> {

	override fun Tuple2<String, String>.show(): String =
		"$a=$b"

}

object LocalExecutor : Executor<LocalRequestSpecific> {

	override fun execute(specific: LocalRequestSpecific): Either<ExecutionError, ResponseSpecification<LocalRequestSpecific>> =
		Try.of {
			with(specific) {
				Runtime.getRuntime().exec(
					commands.toTypedArray(),
					environments.toList()
						.map(Pair<String, String>::toTuple2)
						.map { EnvPairShow.run { it.show() } }.toTypedArray(),
					File(workDir)
				)
			}
		}.map {
			val readOut: Future<String> = Future.of { it.inputStream.readAllBytes().decodeToString() }
			val readErr: Future<String> = Future.of { it.errorStream.readAllBytes().decodeToString() }

			if (!it.waitFor(specific.timeout, TimeUnit.MILLISECONDS)) {
				// timeout
				Either.left(ExecutionError.ProcessTimeout)
			} else if (readOut.isFailure || readErr.isFailure) {
				Either.left(ExecutionError.ReadOutPutError())
			} else {
				Either.right(
					DefaultResponseSpecification(CommandResponse(
						stdOut = readOut.get(),
						stdErr = readErr.get(),
						exitCode = it.exitValue(),
						timeSpend = 0
					), specific)
				)
			}
		}.getOrElseGet {
			Either.left(ExecutionError.ExecError(it.message!!))
		}

}