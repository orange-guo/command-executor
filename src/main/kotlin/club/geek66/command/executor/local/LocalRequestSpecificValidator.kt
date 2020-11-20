package club.geek66.command.executor.local

import arrow.core.Either
import arrow.core.filterOrElse
import club.geek66.command.executor.RequestSpecificValidator
import club.geek66.command.executor.SpecificValidationError
import java.nio.file.Files
import java.nio.file.Path

object LocalRequestSpecificValidator : RequestSpecificValidator<LocalRequestSpecific> {

	override fun validate(specification: LocalRequestSpecific): Either<SpecificValidationError, LocalRequestSpecific> =
		Either.right(specification).filterOrElse({
			it.commands.isNotEmpty()
		}) {
			SpecificValidationError.EmptyCommand
		}.filterOrElse({
			Files.isDirectory(Path.of(it.workDir))
		}) {
			SpecificValidationError.BadWorkDir
		}

}