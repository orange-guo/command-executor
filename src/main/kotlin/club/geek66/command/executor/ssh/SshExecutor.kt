package club.geek66.command.executor.ssh

import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.flatMap
import club.geek66.command.executor.CommandResponse
import club.geek66.command.executor.DefaultResponseSpecification
import club.geek66.command.executor.ExecutionError
import club.geek66.command.executor.Executor
import club.geek66.command.executor.ssh.SshCredential.PasswordCredential
import club.geek66.command.executor.ssh.SshCredential.PrivateKeyCredential
import com.jcabi.aspects.Tv
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import io.vavr.concurrent.Future
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit


val sshExecutor = Executor<SshRequestSpecific> { specific ->
	with(specific) {
		val out = ByteArrayOutputStream()
		val err = ByteArrayOutputStream()
		JSch.setConfig("StrictHostKeyChecking", "no")
		when (credential) {
			is PrivateKeyCredential -> privateKeyCredential.create(credential, connectionInfo)
			is PasswordCredential -> passwordCredential.create(credential, connectionInfo)
		}.map {
			it.serverAliveInterval = TimeUnit.SECONDS.toMillis(Tv.TEN.toLong()).toInt()
			it.serverAliveCountMax = Tv.MILLION
			it.connect()
			it
		}.mapLeft {
			ExecutionError.OpenSessionFailed("")
		}.map {
			val channelExec: ChannelExec = it.openChannel("exec") as ChannelExec
			channelExec.setPty(true)
			channelExec.inputStream = stdIn()
			channelExec.outputStream = out
			channelExec.setErrStream(err)
			specific.environments.forEach(channelExec::setEnv)
			channelExec.setCommand(commands.joinToString(" "))
			channelExec.connect()
			channelExec.start()

			Tuple2(it, channelExec)
		}.flatMap { (session: Session, channelExec: ChannelExec) ->
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
					stdOut = out.toByteArray().toString(Charsets.UTF_8),
					stdErr = err.toByteArray().toString(Charsets.UTF_8),
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
}