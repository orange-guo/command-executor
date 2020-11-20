package club.geek66.command.executor

import arrow.core.Either

interface RequestSpecificValidator<T : RequestSpecific<T>> {

	fun validate(specification: T): Either<SpecificValidationError, T>

}