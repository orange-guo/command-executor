package club.geek66.command.executor

abstract class CommandError(
	val message: String
) {

	override fun toString(): String = message

}

sealed class SpecificValidationError(msg: String) : CommandError(msg) {

	object EmptyCommand : SpecificValidationError("Empty commands")

	object BadWorkDir : SpecificValidationError("Bad workDir")

	object BadSshPort : SpecificValidationError("Port must range in 1-65535")

}

sealed class ExecutionError(msg: String) : CommandError(msg) {

	object ProcessTimeout : ExecutionError("process time out")

	class ReadOutPutError : ExecutionError("read out error")

	class ExecError(msg: String) : ExecutionError("exec error, msg$msg")

}