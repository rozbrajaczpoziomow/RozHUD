package futbol.rozbrajacz.rozhud.client

import futbol.rozbrajacz.rozhud.ConfigHandler
import futbol.rozbrajacz.rozhud.ConfigHelper
import futbol.rozbrajacz.rozhud.RozHUD
import futbol.rozbrajacz.rozhud.net.ArrayPacket
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent
import java.awt.Color

class OverlayHandler {
	val coordinates = ConfigHelper({ ConfigHandler.position }) {
		val split = it.split(',')
		split[0].toInt() to split[1].toInt()
	}

	val colourParser = { it: String ->
		Color(it.substring(1, 3).toInt(16), it.substring(3, 5).toInt(16), it.substring(5, 7).toInt(16), it.substring(7, 9).toInt(16)).rgb
	}

	val backgroundColour = ConfigHelper({ ConfigHandler.backgroundColour }, colourParser)
	val textColour = ConfigHelper({ ConfigHandler.textColour }, colourParser)

	@SubscribeEvent(priority = EventPriority.LOWEST)
	fun drawOverlay(ev: RenderGameOverlayEvent.Post) {
		if(!ConfigHandler.enabled || ev.type != RenderGameOverlayEvent.ElementType.ALL || ConfigHandler.renderF3 != Minecraft.getMinecraft().gameSettings.showDebugInfo)
			return

		val renderer = Minecraft.getMinecraft().fontRenderer

		GlStateManager.pushMatrix()

		val (left, top) = coordinates.value
		val textOffset = (renderer.FONT_HEIGHT + 1)
		val messages = MessageHandler.getMessage()

		GuiScreen.drawRect(left, top, left + messages.maxOf { renderer.getStringWidth(it) } + 1, top + textOffset * messages.size, backgroundColour.value)
		GlStateManager.color(1f, 1f, 1f, 1f)

		messages.forEachIndexed { idx, str ->
			renderer.drawString(str, left + 1, top + 1 + textOffset * idx, textColour.value)
		}

		GlStateManager.popMatrix()
	}

	@SubscribeEvent
	fun serverJoin(ev: FMLNetworkEvent.ClientConnectedToServerEvent) {
		MessageHandler.reset()
	}

	companion object {
		fun handlePacket(packet: ArrayPacket) = MessageHandler.handlePacket(packet)
	}

	object MessageHandler {
		private var last = 0L
		private var lastMessage = ConfigHandler.text
		private var waiting = false

		fun getMessage(): Array<String> {
			val current = Minecraft.getSystemTime()

			if(waiting && current > last + ConfigHandler.refreshInterval * 3)
				return if(lastMessage === ConfigHandler.text)
					arrayOf("RozHUD is not installed or is disabled on this server")
				else
					arrayOf(*lastMessage, "Server did not respond to our last request,", "Relog to refresh")

			if(waiting || current <= last + ConfigHandler.refreshInterval)
				return lastMessage

			waiting = true
			RozHUD.networkChannel.sendToServer(ArrayPacket(ConfigHandler.text))
			return lastMessage
		}

		fun handlePacket(packet: ArrayPacket) {
			last = Minecraft.getSystemTime()
			lastMessage = packet.arr
			waiting = false
		}

		fun reset() {
			last = 0L
			lastMessage = ConfigHandler.text
			waiting = false
		}
	}
}
