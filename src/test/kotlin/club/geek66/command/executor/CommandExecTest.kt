package club.geek66.command.executor

import arrow.core.Either
import arrow.core.Nel
import arrow.core.k
import club.geek66.command.executor.local.LocalRequestSpecific
import club.geek66.command.executor.ssh.SshConnectionInfo
import club.geek66.command.executor.ssh.SshCredential
import club.geek66.command.executor.ssh.SshRequestSpecific
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


/**
 * @author: orange
 * @date: 2020/11/20
 * @time: 下午6:52
 * @copyright: Copyright 2020 by orange
 */
class CommandExecTest {

	@Test
	fun testLocal() {
		// test echo env
		CommandExec.local.exec {
			LocalRequestSpecific(
				environments = mapOf("NAME" to "TEST").k(),
				commands = Nel("bash", "-c", "echo ${'$'}NAME"),
				workDir = "/tmp"
			)
		}.let {
			when (it) {
				is Either.Right -> {
					assertEquals("TEST\n", it.b.response.stdOut)
				}
				else -> throw IllegalArgumentException(it.toString())
			}
		}

		Files.deleteIfExists(Path.of("/tmp/123"))
		// test touch file
		CommandExec.local.exec {
			LocalRequestSpecific(
				environments = emptyMap<String, String>().k(),
				commands = Nel("touch", "123"),
				workDir = "/tmp"
			)
		}.let {
			when (it) {
				is Either.Right -> assertEquals(0, it.b.response.exitCode)
				else -> throw IllegalArgumentException(it.toString())
			}
		}
		assertTrue(Files.exists(Path.of("/tmp/123")))
	}

	@Test
	fun testSsh() {
		// only support environment that name start with LC(ssh security config)
		// see https://stackoverflow.com/questions/9366914/setting-environment-variables-on-a-jsch-channelexec?rq=1 Paŭlo Ebermann's answer
		CommandExec.ssh.exec {
			SshRequestSpecific(
				environments = mapOf("LC_TEST" to "Jack").k(),
				commands = Nel("echo", "${'$'}LC_TEST"),
				connectionInfo = SshConnectionInfo(user = "orange"),
				credential = SshCredential.PrivateKeyCredential(
					privateKey = File("/home/orange/.ssh/id_rsa").readText()
				)
			)
		}.let {
			when (it) {
				is Either.Right -> {
					// end with \r\n
					assertEquals("Jack\r\n", it.b.response.stdOut)
				}

				else -> throw IllegalArgumentException()
			}
		}
	}

}