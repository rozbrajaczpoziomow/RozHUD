package futbol.rozbrajacz.rozhud

import futbol.rozbrajacz.rozhud.client.OverlayHandler
import futbol.rozbrajacz.rozhud.net.ArrayPacket
import futbol.rozbrajacz.rozhud.net.ClientHandler
import futbol.rozbrajacz.rozhud.net.ServerHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side

@Mod(
	modid = Reference.MODID,
	name = Reference.MOD_NAME,
	version = Reference.VERSION,
	dependencies = RozHUD.DEPENDENCIES,
	modLanguageAdapter = "io.github.chaosunity.forgelin.KotlinAdapter",
	acceptableRemoteVersions = "*"
)
object RozHUD {
	const val DEPENDENCIES = "required-after:forgelin_continuous@[${Reference.KOTLIN_VERSION},);"

	val networkChannel: SimpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID)

	@Suppress("unused")
	@Mod.EventHandler
	fun preInit(e: FMLPreInitializationEvent) {
		if(FMLCommonHandler.instance().effectiveSide.isClient) {
			val inst = OverlayHandler()
			MinecraftForge.EVENT_BUS.register(inst)
		}
		networkChannel.registerMessage(ServerHandler::class.java, ArrayPacket::class.java, 0, Side.SERVER)
		networkChannel.registerMessage(ClientHandler::class.java, ArrayPacket::class.java, 0, Side.CLIENT)
	}
}
