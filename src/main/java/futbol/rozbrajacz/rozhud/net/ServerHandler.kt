package futbol.rozbrajacz.rozhud.net

import com.sun.management.OperatingSystemMXBean
import futbol.rozbrajacz.rozhud.ConfigHandler
import futbol.rozbrajacz.rozhud.Reference
import net.minecraft.server.MinecraftServer
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import java.lang.management.ManagementFactory
import java.text.DecimalFormat

class ServerHandler : IMessageHandler<ArrayPacket, ArrayPacket> {
	private lateinit var server: MinecraftServer

	private val formats = HashMap<String, Context.() -> String>().apply {
		// Server-wide
		put("tick") { server.tickCounter.toString() }

		val formatter = DecimalFormat("########0.00")
		val singleFormatter = DecimalFormat("########0.0")

		val getServerMSPT = { server.tickTimeArray.average() * 1e-6 }
		put("mspt") { formatter.format(getServerMSPT()) }
		put("tps") { formatter.format((1000 / getServerMSPT()).coerceAtMost(20.0)) }

		val runtime = Runtime.getRuntime()
		put("ram_used") { formatter.format((runtime.maxMemory() - runtime.freeMemory()) * 1e-9) }
		put("ram_max") { formatter.format(runtime.maxMemory() * 1e-9) }

		val bean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean::class.java)
		put("cpu_usage") { formatter.format(bean.systemCpuLoad * 100) }

		put("ping") { singleFormatter.format((System.nanoTime() - pkt.nanos) * 1e-6) }

		// Current dimension
		val getDimensionId = { ctx: MessageContext -> ctx.serverHandler.player.dimension }
		put("dim_id") { getDimensionId(ctx).toString() }

		val getDimensionMSPT = { ctx: MessageContext -> server.worldTickTimes[getDimensionId(ctx)]!!.average() * 1e-6 }
		put("dim_mspt") { formatter.format(getDimensionMSPT(ctx)) }
		put("dim_tps") { formatter.format((1000 / getDimensionMSPT(ctx)).coerceAtMost(20.0)) }

		val getDimension = { ctx: MessageContext -> DimensionManager.getWorld(getDimensionId(ctx)) }
		put("dim_entity_count") { getDimension(ctx).loadedEntityList.size.toString() }
		put("dim_tile_entity_count") { getDimension(ctx).loadedTileEntityList.size.toString() }
		put("dim_chunk_count") { getDimension(ctx).chunkProvider.loadedChunkCount.toString() }
	}

	private val disabledPacket = ArrayPacket(arrayOf("${Reference.MOD_NAME} has been disabled on this server"))
	private val requiresOpPacket = ArrayPacket(arrayOf("${Reference.MOD_NAME} requires OP on this server"))

	override fun onMessage(message: ArrayPacket, ctx: MessageContext): ArrayPacket? {
		if(!ConfigHandler.enabled)
			return if(ConfigHandler.server.ignore) null else disabledPacket

		server = FMLCommonHandler.instance().sidedDelegate.server ?: return null

		if(ConfigHandler.server.op && server.playerList.oppedPlayers.getPermissionLevel(ctx.serverHandler.player.gameProfile) == 0)
			return if(ConfigHandler.server.ignore) null else requiresOpPacket

		val text = message.arr
		val resp = Array(text.size) { format(text[it], Context(message, ctx)) }

		return ArrayPacket(resp)
	}

	private fun format(arg: String, ctx: Context): String {
		var str = arg
		formats.forEach { (fmt, replacer) ->
			if(str.contains("{$fmt}"))
				str = str.replace("{$fmt}", replacer(ctx))
		}
		return str
	}

	private class Context(val pkt: ArrayPacket, val ctx: MessageContext)
}
