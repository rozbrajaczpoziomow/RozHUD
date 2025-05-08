package futbol.rozbrajacz.rozutils.net

import com.sun.management.OperatingSystemMXBean
import futbol.rozbrajacz.rozutils.ConfigHandler
import net.minecraft.server.MinecraftServer
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import java.lang.management.ManagementFactory
import java.text.DecimalFormat

class ServerHandler : IMessageHandler<ArrayPacket, ArrayPacket> {
	private lateinit var server: MinecraftServer

	private val formats = HashMap<String, Format>().apply {
		val allowedFormats = ConfigHandler.server.hud.formats.split(',')
		val add = add@ { key: String, func: Context.() -> String ->
			var op = false
			if(!allowedFormats.contains(key)) {
				if(ConfigHandler.server.hud.opBypass)
					op = true
				else
					return@add
			}
			put(key, Format(op, func))
		}

		// Server-wide
		add("tick") { server.tickCounter.toString() }

		val formatter = DecimalFormat("########0.00")
		val singleFormatter = DecimalFormat("########0.0")

		val getServerMSPT = { server.tickTimeArray.average() * 1e-6 }
		add("mspt") { formatter.format(getServerMSPT()) }
		add("tps") { formatter.format((1000 / getServerMSPT()).coerceAtMost(20.0)) }

		val runtime = Runtime.getRuntime()
		add("ram_used") { formatter.format((runtime.maxMemory() - runtime.freeMemory()) * 1e-9) }
		add("ram_max") { formatter.format(runtime.maxMemory() * 1e-9) }

		val bean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean::class.java)
		add("cpu_usage") { formatter.format(bean.systemCpuLoad * 100) }

		add("ping") { singleFormatter.format((System.nanoTime() - pkt.nanos) * 1e-6) }

		// Current dimension
		val getDimensionId = { ctx: MessageContext -> ctx.serverHandler.player.dimension }
		add("dim_id") { getDimensionId(ctx).toString() }

		val getDimensionMSPT = { ctx: MessageContext -> server.worldTickTimes[getDimensionId(ctx)]!!.average() * 1e-6 }
		add("dim_mspt") { formatter.format(getDimensionMSPT(ctx)) }
		add("dim_tps") { formatter.format((1000 / getDimensionMSPT(ctx)).coerceAtMost(20.0)) }

		val getDimension = { ctx: MessageContext -> DimensionManager.getWorld(getDimensionId(ctx)) }
		add("dim_entity_count") { getDimension(ctx).loadedEntityList.size.toString() }
		add("dim_tile_entity_count") { getDimension(ctx).loadedTileEntityList.size.toString() }
		add("dim_chunk_count") { getDimension(ctx).chunkProvider.loadedChunkCount.toString() }

		// Current chunk
		val getChunk = { ctx: MessageContext -> getDimension(ctx).getChunk(ctx.serverHandler.player.position) }
		add("ch_entity_count") { getChunk(ctx).entityLists.sumOf { it.size }.toString() }
		add("ch_tile_entity_count") { getChunk(ctx).tileEntityMap.size.toString() }
	}

	override fun onMessage(message: ArrayPacket, ctx: MessageContext): ArrayPacket? {
		if(!ConfigHandler.server.hud.enabled)
			return null

		server = FMLCommonHandler.instance().sidedDelegate.server ?: return null

		val text = message.arr
		val opped = server.playerList.oppedPlayers.getEntry(ctx.serverHandler.player.gameProfile) != null
		val resp = Array(text.size) { format(text[it], opped, Context(message, ctx)) }

		return ArrayPacket(resp)
	}

	private fun format(arg: String, opped: Boolean, ctx: Context): String {
		var str = arg
		formats.forEach { (fmt, out) ->
			if(str.contains("{$fmt}") && (!out.requiresOp || opped))
				str = str.replace("{$fmt}", out.func(ctx))
		}
		return str
	}

	private data class Format(val requiresOp: Boolean, val func: Context.() -> String)
	private class Context(val pkt: ArrayPacket, val ctx: MessageContext)
}
