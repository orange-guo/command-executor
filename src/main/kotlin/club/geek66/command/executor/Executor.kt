package club.geek66.command.executor

import arrow.core.Either

fun interface Executor<T : RequestSpecific> {

	fun execute(specific: T): Either<ExecutionError, ResponseSpecification<T>>

}