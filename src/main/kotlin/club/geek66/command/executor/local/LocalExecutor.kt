package club.geek66.command.executor.local

import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.flatMap
import arrow.core.toTuple2
import arrow.typeclasses.Show
import club.geek66.command.executor.CommandResponse
import club.geek66.command.executor.DefaultResponseSpecification
import club.geek66.command.executor.ExecutionError
import club.geek66.command.executor.Executor
import io.vavr.concurrent.Future
import io.vavr.control.Try
import java.io.File
import java.util.concurrent.TimeUnit

object EnvPairShow : Show<Tuple2<String, String>> {

	override fun Tuple2<String, String>.show(): String =
		"$a=$b"

}

val localExecutor: Executor<LocalRequestSpecific> = Executor { specific ->
	Try.of {
		with(specific) {
			Runtime.getRuntime().exec(
				commands.joinToString(" "),
				environments.toList()
					.map(kotlin.Pair<String, String>::toTuple2)
					.map { EnvPairShow.run { it.show() } }.toTypedArray(),
				File(workDir)
			)
		}
	}.map { process ->
		Tuple3(
			Future.of { process.waitFor(specific.timeout, TimeUnit.MILLISECONDS) }.map { process.exitValue() },
			Future.of { process.inputStream.readAllBytes().decodeToString() },
			Future.of { process.errorStream.readAllBytes().decodeToString() }
		)
	}.map { (readExitCode: Future<Int>, readOut: Future<String>, readErr: Future<String>) ->
		Either.conditionally(readExitCode.isCompleted, {
			ExecutionError.ProcessTimeout
		}) {
			readExitCode
		}.flatMap {
			Either.conditionally(readOut.isFailure || readErr.isFailure, {
				ExecutionError.ReadOutPutError()
			}) {
				DefaultResponseSpecification(CommandResponse(
					stdOut = readOut.get(),
					stdErr = readErr.get(),
					exitCode = it.get(),
					timeSpend = 0
				), specific)
			}
		}
	}.getOrElseGet {
		Either.left(ExecutionError.ExecError(it.message!!))
	}
}