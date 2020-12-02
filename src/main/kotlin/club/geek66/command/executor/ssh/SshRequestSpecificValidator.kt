package club.geek66.command.executor.ssh

import arrow.core.Either
import arrow.core.flatMap
import club.geek66.command.executor.SpecificValidationError
import club.geek66.command.executor.SpecificValidationError.BadSshHost
import club.geek66.command.executor.SpecificValidationError.BadSshPassword
import club.geek66.command.executor.SpecificValidationError.BadSshPort
import club.geek66.command.executor.SpecificValidationError.BadSshPrivateKey
import club.geek66.command.executor.SpecificValidationError.BadSshUser
import club.geek66.command.executor.Validator
import club.geek66.command.executor.specValidator
import club.geek66.command.executor.ssh.SshCredential.PasswordCredential
import club.geek66.command.executor.ssh.SshCredential.PrivateKeyCredential

val sshValidator = Validator<SshRequestSpecific, SpecificValidationError> { specific ->
	specValidator<SshRequestSpecific>()
		.validate(specific)
		.flatMap {
			connectionInfoValidator
				.validate(it.connectionInfo)
				.map { specific }
		}.flatMap {
			sshCredentialValidator(it.credential).map {
				specific
			}
		}
}

val connectionInfoValidator = Validator<SshConnectionInfo, SpecificValidationError> { info ->
	Either.right(info)
		.flatMap {
			when {
				it.host.isEmpty() -> Either.left(BadSshHost)
				it.user.isEmpty() -> Either.left(BadSshUser)
				it.port < 0 || it.port > 65535 -> Either.left(BadSshPort)
				else -> Either.right(it)
			}
		}
}

fun sshCredentialValidator(credential: SshCredential): Either<SpecificValidationError, SshCredential> =
	when (credential) {
		is PasswordCredential -> passwordCredentialValidator.validate(credential)
		is PrivateKeyCredential -> privateKeyCredentialValidator.validate(credential)
	}

val passwordCredentialValidator = Validator<PasswordCredential, SpecificValidationError> {
	when {
		it.password.isEmpty() -> Either.left(BadSshPassword)
		else -> Either.right(it)
	}
}

val privateKeyCredentialValidator = Validator<PrivateKeyCredential, SpecificValidationError> {
	when {
		it.privateKey.isEmpty() -> Either.left(BadSshPrivateKey)
		else -> Either.right(it)
	}
}