package club.geek66.command.executor

import arrow.core.Either

interface Executor<T : RequestSpecific<T>> {

	fun execute(specific: T): Either<ExecutionError, ResponseSpecification<T>>

}