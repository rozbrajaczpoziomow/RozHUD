package futbol.rozbrajacz.rozhud.net

import net.minecraft.server.MinecraftServer
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import java.text.DecimalFormat

class ServerHandler : IMessageHandler<ArrayPacket, ArrayPacket> {
	private lateinit var server: MinecraftServer
	private val formatter = DecimalFormat("########0.000")
	// {tps}, {mspt}, {tick}, {entity_count}, {tile_entity_count}, {chunk_count}, {ram_used}, {ram_max}, {cpu_usage}
	private val formats = HashMap<String, (ctx: MessageContext) -> String>().apply {
		put("tick") { server.tickCounter.toString() }
		val getMSPT = { server.tickTimeArray.average() * 1e-6 }
		put("mspt") { formatter.format(getMSPT()) }
		put("tps") { formatter.format((1000 / getMSPT()).coerceAtMost(20.0)) }
	}

	override fun onMessage(message: ArrayPacket, ctx: MessageContext): ArrayPacket? {
		server = FMLCommonHandler.instance().sidedDelegate.server ?: return null

		val text = message.arr
		val resp = Array(text.size) { format(text[it], ctx) }

		return ArrayPacket(resp)
	}

	private fun format(arg: String, ctx: MessageContext): String {
		var str = arg
		formats.forEach { (fmt, replacer) ->
			if(str.contains("{$fmt}"))
				str = str.replace("{$fmt}", replacer(ctx))
		}
		return str
	}
}
