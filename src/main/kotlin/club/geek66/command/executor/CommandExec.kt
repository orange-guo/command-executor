package club.geek66.command.executor

import arrow.core.Either
import arrow.core.flatMap
import club.geek66.command.executor.local.LocalRequestSpecific
import club.geek66.command.executor.local.localExecutor
import club.geek66.command.executor.local.localValidator
import club.geek66.command.executor.ssh.SshRequestSpecific
import club.geek66.command.executor.ssh.sshExecutor
import club.geek66.command.executor.ssh.sshValidator


class CommandExec<T : RequestSpecific>(
	private val validator: Validator<T, SpecificValidationError>,
	private val executor: Executor<T>
) {

	fun exec(supplier: () -> T): Either<CommandError, ResponseSpecification<T>> =
		supplier().let(validator::validate).flatMap(executor::execute)

	companion object {

		val local: CommandExec<LocalRequestSpecific> = CommandExec(localValidator, localExecutor)

		val ssh: CommandExec<SshRequestSpecific> = CommandExec(sshValidator, sshExecutor)

	}

}