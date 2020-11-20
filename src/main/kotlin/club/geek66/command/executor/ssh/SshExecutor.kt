package club.geek66.command.executor.ssh

import arrow.core.Either
import club.geek66.command.executor.CommandResponse
import club.geek66.command.executor.DefaultResponseSpecification
import club.geek66.command.executor.ExecutionError
import club.geek66.command.executor.Executor
import club.geek66.command.executor.ResponseSpecification
import club.geek66.command.executor.ssh.SshCredential.PasswordCredential
import club.geek66.command.executor.ssh.SshCredential.PrivateKeyCredential
import com.jcabi.aspects.Tv
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import io.vavr.concurrent.Future
import org.cactoos.io.LengthOf
import org.cactoos.io.TeeInput
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.TimeUnit

object SshExecutor : Executor<SshRequestSpecific> {

	override fun execute(specific: SshRequestSpecific): Either<ExecutionError, ResponseSpecification<SshRequestSpecific>> =
		specific.run {
			JSch.setConfig("StrictHostKeyChecking", "no")
			val session: Session = when (val credential: SshCredential = credential) {
				is PrivateKeyCredential -> {
					val file: File = File.createTempFile("jcabi-ssh", ".key")
					val jSch = JSch()
					LengthOf(
						TeeInput(
							credential.privateKey
								.replace("\r".toRegex(), "")
								.replace("\n\\s+|\n{2,}".toRegex(), "\n")
								.trim { it <= ' ' },
							file
						)
					).value()

					jSch.addIdentity(file.absolutePath)

					val session: Session = jSch.getSession(
						user, host, port
					)
					session.serverAliveInterval = TimeUnit.SECONDS.toMillis(Tv.TEN.toLong()).toInt()
					session.serverAliveCountMax = Tv.MILLION
					session
				}

				is PasswordCredential -> {
					val jSch = JSch()
					val session: Session = jSch.getSession(
						user, host, port
					)
					session.setPassword(credential.password)
					session
				}
			}

			session.connect()

			val channelExec: ChannelExec = session.openChannel("exec") as ChannelExec

			val out = ByteArrayOutputStream()
			val err = ByteArrayOutputStream()
			channelExec.inputStream = stdIn()
			channelExec.outputStream = out
			channelExec.setErrStream(err)
			specific.environments.forEach(channelExec::setEnv)
			channelExec.setCommand(commands.joinToString(" "))
			channelExec.connect()
			channelExec.start()

			val waitExit: Future<Int> = Future.of {
				while (!channelExec.isClosed) {
					session.sendKeepAliveMsg()
					TimeUnit.SECONDS.sleep(1L)
				}
			}.map {
				channelExec.exitStatus
			}.apply {
				get()
			}

			if (waitExit.isSuccess) {
				CommandResponse(
					stdOut = out.toByteArray().decodeToString(),
					stdErr = err.toByteArray().decodeToString(),
					exitCode = waitExit.get(),
					timeSpend = 0
				).let {
					DefaultResponseSpecification(it, this)
				}.let { Either.right(it) }
			} else {
				Either.left(ExecutionError.ProcessTimeout)
			}
		}

}