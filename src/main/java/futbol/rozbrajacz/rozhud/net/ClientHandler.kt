package futbol.rozbrajacz.rozhud.net

import futbol.rozbrajacz.rozhud.client.OverlayHandler
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class ClientHandler : IMessageHandler<ArrayPacket, IMessage> {
	override fun onMessage(message: ArrayPacket, ctx: MessageContext): IMessage? {
		OverlayHandler.handlePacket(message)
		return null
	}
}
