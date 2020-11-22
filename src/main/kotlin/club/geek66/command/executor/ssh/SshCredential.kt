package club.geek66.command.executor.ssh

sealed class SshCredential {

	data class PasswordCredential(
		val password: String
	) : SshCredential()

	data class PrivateKeyCredential(
		val privateKey: String,
		val passphrase: String? = null,
	) : SshCredential()

}

data class SshConnectionInfo(
	val host: String = "localhost",
	val user: String,
	val port: Int = 22,
)