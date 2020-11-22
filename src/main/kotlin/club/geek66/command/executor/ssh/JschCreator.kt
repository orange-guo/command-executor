package club.geek66.command.executor.ssh

import arrow.core.Either
import club.geek66.command.executor.CommandError
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.cactoos.io.LengthOf
import org.cactoos.io.TeeInput
import java.io.File

/**
 *
 * @author: orange
 * @date: 2020/11/22
 * @time: 下午4:21
 * @copyright: Copyright 2020 by orange
 */
fun interface JschCreator<T : SshCredential> {

	fun create(request: T, connectionInfo: SshConnectionInfo): Either<CommandError, Session>

}

val privateKeyCredential = JschCreator<SshCredential.PrivateKeyCredential> { credential, connectionInfo ->
	val file: File = File.createTempFile("ssh", ".key")
	val jsch = JSch()
	LengthOf(
		TeeInput(
			credential.privateKey
				.replace("\r".toRegex(), "")
				.replace("\n\\s+|\n{2,}".toRegex(), "\n")
				.trim { it <= ' ' },
			file
		)
	).value()

	if (credential.passphrase == null) {
		jsch.addIdentity(file.absolutePath)
	} else {
		jsch.addIdentity(
			connectionInfo.user,
			credential.privateKey.toByteArray(),
			null,
			credential.passphrase.toByteArray()
		)
	}

	val session: Session = jsch.getSession(connectionInfo.user, connectionInfo.host, connectionInfo.port)
	Either.right(session)
}

val passwordCredential = JschCreator<SshCredential.PasswordCredential> { credential, connectionInfo ->
	val jSch = JSch()
	val session: Session = jSch.getSession(
		connectionInfo.user, connectionInfo.host, connectionInfo.port
	)
	session.setPassword(credential.password)
	Either.right(session)
}