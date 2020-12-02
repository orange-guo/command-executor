package club.geek66.command.executor

interface ResponseSpecification<T : RequestSpecific> {

	val request: T

	val response: CommandResponse

}

data class DefaultResponseSpecification<T : RequestSpecific>(
	override val response: CommandResponse,
	override val request: T,
) : ResponseSpecification<T>

data class CommandResponse(
	val stdOut: String,
	val stdErr: String,
	val exitCode: Int,
	val timeSpend: Long
)