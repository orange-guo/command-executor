package club.geek66.command.executor.ssh

import arrow.core.Either
import arrow.core.filterOrElse
import club.geek66.command.executor.SpecificValidationError
import club.geek66.command.executor.Validator

val sshValidator = Validator<SshRequestSpecific, SpecificValidationError> { specific ->
	Either.right(specific)
		.filterOrElse({
			it.commands.isNotEmpty()
		}) {
			SpecificValidationError.EmptyCommand
		}.filterOrElse({
			it.port > 0
		}) {
			SpecificValidationError.BadSshPort
		}
}