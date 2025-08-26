package futbol.rozbrajacz.rozutils.client

import futbol.rozbrajacz.rozutils.RozUtils
import futbol.rozbrajacz.rozutils.commands.RozUtilsCommand
import futbol.rozbrajacz.rozutils.commands.reply
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.ClientChatEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class ClientEventHandler {
	companion object {
		val instance = ClientEventHandler()
	}
	private constructor()

	@SubscribeEvent
	fun runCommand(ev: ClientChatEvent) {
		val msg = ev.message.trim()
		if(msg[0] != '/')
			return

		if(msg == "/${RozUtilsCommand.instance.name} version" || RozUtilsCommand.instance.aliases.any { msg == "/$it version" })
			Minecraft.getMinecraft().player.reply(RozUtils.formatVersion("Client"))
	}
}
