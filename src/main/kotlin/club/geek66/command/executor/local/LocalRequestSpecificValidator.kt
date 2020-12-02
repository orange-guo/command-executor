package club.geek66.command.executor.local

import arrow.core.Either
import arrow.core.flatMap
import club.geek66.command.executor.SpecificValidationError
import club.geek66.command.executor.Validator
import club.geek66.command.executor.specValidator
import java.nio.file.Files
import java.nio.file.Path

val localValidator: Validator<LocalRequestSpecific, SpecificValidationError> = Validator { specific ->
	specValidator<LocalRequestSpecific>()
		.validate(specific)
		.flatMap {
			when {
				Files.isDirectory(Path.of(it.workDir)) -> Either.right(it)
				else -> Either.left(SpecificValidationError.BadWorkDir)
			}
		}
}