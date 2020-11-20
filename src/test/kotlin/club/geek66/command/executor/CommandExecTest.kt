package club.geek66.command.executor

import arrow.core.Either
import arrow.core.Nel
import arrow.core.k
import club.geek66.command.executor.local.LocalRequestSpecific
import club.geek66.command.executor.ssh.SshCredential
import club.geek66.command.executor.ssh.SshRequestSpecific
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 * @author: orange
 * @date: 2020/11/20
 * @time: 下午6:52
 * @copyright: Copyright 2020 by orange
 */
class CommandExecTest {

	@Test
	fun exec() {
		CommandExec.local.exec {
			LocalRequestSpecific(
				environments = mapOf("name" to "Jack").k(),
				commands = Nel("/bin/bash", "-c", "echo ${'$'}HOME"),
				workDir = "/home/orange"
			)
		}.let {
			when (it) {
				is Either.Right -> {
					assertEquals("/home/orange\n", it.b.response.stdOut)
				}

				else -> throw IllegalArgumentException()
			}
		}

		CommandExec.ssh.exec {
			SshRequestSpecific(
				environments = mapOf("name" to "Jack").k(),
				commands = Nel("echo", "${'$'}HOME"),
				user = "orange",
				credential = SshCredential.PrivateKeyCredential(privateKey = File("/home/orange/.ssh/id_rsa").readText())
			)
		}.let {
			when (it) {
				is Either.Right -> {
					assertEquals("/home/orange\n", it.b.response.stdOut)
				}

				else -> throw IllegalArgumentException()
			}
		}
	}

}