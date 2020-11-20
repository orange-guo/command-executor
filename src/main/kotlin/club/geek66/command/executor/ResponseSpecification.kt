package club.geek66.command.executor

interface ResponseSpecification<T : RequestSpecific<T>> {

	val request: RequestSpecific<T>

	val response: CommandResponse

}

data class DefaultResponseSpecification<T : RequestSpecific<T>>(
	override val response: CommandResponse,
	override val request: RequestSpecific<T>,
) : ResponseSpecification<T>

data class CommandResponse(
	val stdOut: String,
	val stdErr: String,
	val exitCode: Int,
	val timeSpend: Long
)