package club.geek66.command.executor

import arrow.core.Either
import arrow.core.Nel
import arrow.core.filterOrElse
import club.geek66.command.executor.local.LocalRequestSpecific
import java.nio.file.Files
import java.nio.file.Path

fun interface Validator<T, E> {

	fun validate(t: T): Either<E, T>

}

val commandsValidator: Validator<Nel<String>, SpecificValidationError> = Validator {
	TODO()
}

