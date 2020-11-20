package club.geek66.command.executor.ssh

import arrow.core.Either
import arrow.core.filterOrElse
import club.geek66.command.executor.RequestSpecificValidator
import club.geek66.command.executor.SpecificValidationError

object SshRequestSpecificValidator : RequestSpecificValidator<SshRequestSpecific> {

	override fun validate(specification: SshRequestSpecific): Either<SpecificValidationError, SshRequestSpecific> =
		Either.right(specification)
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