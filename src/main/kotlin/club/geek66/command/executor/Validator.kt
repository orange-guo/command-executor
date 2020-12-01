package club.geek66.command.executor

import arrow.core.Either
import arrow.core.flatMap

fun interface Validator<T, E> {

	fun validate(t: T): Either<E, T>

}

fun <T : RequestSpecific<T>> baseValidator(): Validator<T, SpecificValidationError> = Validator { spec ->
	Either.right(spec)
		.flatMap {
			when {
				it.commands.isNotEmpty() -> Either.right(it)
				it.timeout < 0 -> Either.left(SpecificValidationError.BadTimeout(it.timeout))
				else -> Either.left(SpecificValidationError.EmptyCommand)
			}
		}
}

