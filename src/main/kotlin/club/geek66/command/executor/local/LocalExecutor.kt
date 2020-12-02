package club.geek66.command.executor.local

import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.toTuple2
import arrow.typeclasses.Show
import club.geek66.command.executor.CommandResponse
import club.geek66.command.executor.DefaultResponseSpecification
import club.geek66.command.executor.ExecutionError
import club.geek66.command.executor.Executor
import io.vavr.concurrent.Future
import java.io.File
import java.util.concurrent.TimeUnit

object EnvPairShow : Show<Tuple2<String, String>> {

	override fun Tuple2<String, String>.show(): String =
		"$a=$b"

}

val localExecutor: Executor<LocalRequestSpecific> = Executor { specific ->
	val process = with(specific) {
		Runtime.getRuntime().exec(
			commands.toTypedArray(),
			environments.toList()
				.map(kotlin.Pair<String, String>::toTuple2)
				.map { EnvPairShow.run { it.show() } }.toTypedArray(),
			File(workDir)
		)
	}

	val waitExit = Future.of { process.waitFor(specific.timeout, TimeUnit.MILLISECONDS) }
	val readStdOut = Future.of { process.inputStream.readAllBytes().decodeToString() }
	val readStdErr = Future.of { process.errorStream.readAllBytes().decodeToString() }

	when {
		!waitExit.get() -> Either.left(ExecutionError.ProcessTimeout)
		readStdOut.isFailure -> Either.left(ExecutionError.ReadStdOutError(readStdOut.cause.map(Throwable::message).getOrElse("Unknown")!!))
		readStdErr.isFailure -> Either.left(ExecutionError.ReadStdErrError(readStdErr.cause.map(Throwable::message).getOrElse("Unknown")!!))
		else -> Either.right(
			DefaultResponseSpecification(CommandResponse(
				stdOut = readStdOut.get(),
				stdErr = readStdErr.get(),
				exitCode = process.exitValue(),
				timeSpend = 0
			), specific)
		)
	}

}