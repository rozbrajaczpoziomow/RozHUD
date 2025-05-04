package futbol.rozbrajacz.rozutils.net

import futbol.rozbrajacz.rozutils.client.HUDHandler
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class ClientHandler : IMessageHandler<ArrayPacket, IMessage> {
	override fun onMessage(message: ArrayPacket, ctx: MessageContext): IMessage? {
		HUDHandler.handlePacket(message)
		return null
	}
}
