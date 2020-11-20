package club.geek66.command.executor

import arrow.core.Either
import arrow.core.flatMap
import club.geek66.command.executor.local.LocalExecutor
import club.geek66.command.executor.local.LocalRequestSpecific
import club.geek66.command.executor.local.LocalRequestSpecificValidator
import club.geek66.command.executor.ssh.SshExecutor
import club.geek66.command.executor.ssh.SshRequestSpecific
import club.geek66.command.executor.ssh.SshRequestSpecificValidator

class CommandExec<T : RequestSpecific<T>>(
	private val executor: Executor<T>,
	private val validator: RequestSpecificValidator<T>
) {

	fun exec(supplier: () -> T): Either<CommandError, ResponseSpecification<T>> =
		supplier().let(validator::validate).flatMap(executor::execute)

	companion object {

		val local: CommandExec<LocalRequestSpecific> = CommandExec(LocalExecutor, LocalRequestSpecificValidator)

		val ssh: CommandExec<SshRequestSpecific> = CommandExec(SshExecutor, SshRequestSpecificValidator)

	}

}