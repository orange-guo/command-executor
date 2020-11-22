package club.geek66.command.executor.local

import arrow.core.Either
import arrow.core.filterOrElse
import club.geek66.command.executor.SpecificValidationError
import club.geek66.command.executor.Validator
import java.nio.file.Files
import java.nio.file.Path

val localValidator: Validator<LocalRequestSpecific, SpecificValidationError> = Validator { specific ->
	Either.right(specific)
		.filterOrElse({
			it.commands.isNotEmpty()
		}) {
			SpecificValidationError.EmptyCommand
		}.filterOrElse({
			Files.isDirectory(Path.of(it.workDir))
		}) {
			SpecificValidationError.BadWorkDir
		}
}