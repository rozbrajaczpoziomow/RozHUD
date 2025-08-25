package futbol.rozbrajacz.rozutils

import futbol.rozbrajacz.rozutils.client.GamemodeSwitcherHandler
import futbol.rozbrajacz.rozutils.client.HUDHandler
import futbol.rozbrajacz.rozutils.commands.RozUtilsCommand
import futbol.rozbrajacz.rozutils.net.ArrayPacket
import futbol.rozbrajacz.rozutils.net.ClientHandler
import futbol.rozbrajacz.rozutils.net.ServerHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side

@Mod(
	modid = Reference.MODID,
	name = Reference.MOD_NAME,
	version = Reference.VERSION,
	dependencies = RozUtils.DEPENDENCIES,
	modLanguageAdapter = "io.github.chaosunity.forgelin.KotlinAdapter",
	acceptableRemoteVersions = "*"
)
object RozUtils {
	const val DEPENDENCIES = "required-after:forgelin_continuous@[${Reference.KOTLIN_VERSION},);"

	val networkChannel: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID)

	@Suppress("unused")
	@Mod.EventHandler
	fun preInit(e: FMLPreInitializationEvent) {
		if(FMLCommonHandler.instance().effectiveSide.isClient) {
			MinecraftForge.EVENT_BUS.register(HUDHandler.instance)
			MinecraftForge.EVENT_BUS.register(GamemodeSwitcherHandler.instance)
		}
		networkChannel.registerMessage(ServerHandler::class.java, ArrayPacket::class.java, 0, Side.SERVER)
		networkChannel.registerMessage(ClientHandler::class.java, ArrayPacket::class.java, 0, Side.CLIENT)
	}

	@Mod.EventHandler
	fun serverStarting(e: FMLServerStartingEvent) {
		if(ConfigHandler.server.command.enabled)
			e.registerServerCommand(RozUtilsCommand())
	}
}
