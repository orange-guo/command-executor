package club.geek66.command.executor.ssh

sealed class SshCredential {

	data class PasswordCredential(val password: String) : SshCredential()

	data class PrivateKeyCredential(val privateKey: String) : SshCredential()

}