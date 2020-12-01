package club.geek66.command.executor

import arrow.typeclasses.Show

abstract class CommandError(private val message: String) : Show<CommandError> {

	override fun CommandError.show(): String = message

}

sealed class SpecificValidationError(msg: String) : CommandError(msg) {

	object EmptyCommand : SpecificValidationError("Empty commands")

	class BadTimeout(timeout: Long) : SpecificValidationError("Bad timeout $timeout")

	object BadWorkDir : SpecificValidationError("Bad workDir")

	object BadSshHost : SpecificValidationError("Bad ssh host")

	object BadSshUser : SpecificValidationError("Bad ssh user")

	object BadSshPort : SpecificValidationError("Bad ssh port that must range in 1-65535")

	object BadSshPassword : SpecificValidationError("Bad ssh password")

	object BadSshPrivateKey : SpecificValidationError("Bad ssh privateKey")

}

sealed class ExecutionError(msg: String) : CommandError(msg) {

	object ProcessTimeout : ExecutionError("process time out")

	class ReadOutPutError : ExecutionError("read out error")

	class ExecError(msg: String) : ExecutionError("exec error, msg$msg")

	class OpenSessionFailed(msg: String) : ExecutionError(msg)

}