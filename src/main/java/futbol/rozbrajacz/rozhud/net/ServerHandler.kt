package futbol.rozbrajacz.rozhud.net

import com.sun.management.OperatingSystemMXBean
import futbol.rozbrajacz.rozhud.ConfigHandler
import net.minecraft.server.MinecraftServer
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import java.lang.management.ManagementFactory
import java.text.DecimalFormat

class ServerHandler : IMessageHandler<ArrayPacket, ArrayPacket> {
	private lateinit var server: MinecraftServer

	private val formats = HashMap<String, (ctx: MessageContext) -> String>().apply {
		// Server-wide
		put("tick") { server.tickCounter.toString() }

		val formatter = DecimalFormat("########0.00")

		val getServerMSPT = { server.tickTimeArray.average() * 1e-6 }
		put("mspt") { formatter.format(getServerMSPT()) }
		put("tps") { formatter.format((1000 / getServerMSPT()).coerceAtMost(20.0)) }

		val runtime = Runtime.getRuntime()
		put("ram_used") { formatter.format((runtime.maxMemory() - runtime.freeMemory()) * 1e-9) }
		put("ram_max") { formatter.format(runtime.maxMemory() * 1e-9) }

		val bean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean::class.java)
		put("cpu_usage") { formatter.format(bean.systemCpuLoad * 100) }

		// Current dimension
		val getDimensionId = { ctx: MessageContext -> ctx.serverHandler.player.dimension }
		put("dim_id") { getDimensionId(it).toString() }

		val getDimensionMSPT = { ctx: MessageContext -> server.worldTickTimes[getDimensionId(ctx)]!!.average() * 1e-6 }
		put("dim_mspt") { formatter.format(getDimensionMSPT(it)) }
		put("dim_tps") { formatter.format((1000 / getDimensionMSPT(it)).coerceAtMost(20.0)) }

		val getDimension = { ctx: MessageContext -> DimensionManager.getWorld(getDimensionId(ctx)) }
		put("dim_entity_count") { getDimension(it).loadedEntityList.size.toString() }
		put("dim_tile_entity_count") { getDimension(it).loadedTileEntityList.size.toString() }
		put("dim_chunk_count") { getDimension(it).chunkProvider.loadedChunkCount.toString() }
	}

	override fun onMessage(message: ArrayPacket, ctx: MessageContext): ArrayPacket? {
		if(!ConfigHandler.enabled)
			return null // thought of sending a "RozHUD is disabled" message, but the way the client is made, if we don't reply, the client doesn't send any more requests

		server = FMLCommonHandler.instance().sidedDelegate.server ?: return null

		if(ConfigHandler.server.op && server.playerList.oppedPlayers.getPermissionLevel(ctx.serverHandler.player.gameProfile) == 0)
			return null

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
