package futbol.rozbrajacz.rozhud

import net.minecraftforge.common.config.Config
import net.minecraftforge.common.config.ConfigManager
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Config(modid = Reference.MODID, name = Reference.MODID)
object ConfigHandler {
	@JvmField
	@Config.Name("A Enable ${Reference.MOD_NAME}")
	@Config.Comment(
		"On client-side, this enables the HUD",
		"On server-side, this enables replying to requests"
	)
	var enabled = false

	@JvmField
	@Config.Name("HUD Position")
	@Config.Comment("Coordinates for the top-left of the HUD (x,y)")
	var position = "0,0"

	@JvmField
	@Config.Name("HUD Background Colour")
	@Config.Comment("Colour of the background behind the HUD (#RRGGBBAA)")
	var backgroundColour = "#7F7F7F7F"

	@JvmField
	@Config.Name("HUD Text Colour")
	@Config.Comment("Colour of the text (#RRGGBBAA)")
	var textColour = "#F0F0F0FF"

	@JvmField
	@Config.Name("HUD Text")
	@Config.Comment(
		"Text displayed in the HUD",
		"Available formats:",
		"- Server-wide: {tps}, {mspt}, {tick}, {ram_used}, {ram_max}, {cpu_usage}, {ping}",
		"- Current dimension: {dim_id}, {dim_tps}, {dim_mspt}, {dim_entity_count}, {dim_tile_entity_count}, {dim_chunk_count}",
		"- Current chunk: {ch_entity_count}, {ch_tile_entity_count}"
	)
	var text = arrayOf(
		"TPS {tps}, {mspt} ms",
		"E {dim_entity_count} TE {dim_tile_entity_count} Ch {dim_chunk_count}",
		"RAM {ram_used} / {ram_max} GB; CPU {cpu_usage}%"
	)

	@JvmField
	@Config.Name("Show HUD with F3")
	@Config.Comment(
		"If enabled, the HUD is only shown when the F3 menu is open, if disabled, the HUD is hidden when the F3 menu is open",
		"If you choose to enable this, I'd recommend changing the default HUD position to not interfere with the F3 menu"
	)
	var renderF3 = false

	@JvmField
	@Config.Name("Refresh Interval")
	@Config.Comment("Refresh the data that gets shown on the client every x (ms)")
	@Config.RangeInt(min = 100, max = 5000)
	var refreshInterval = 500

	@JvmField
	@Config.Name("Server-only settings")
	val server = Server()

	class Server {
		@JvmField
		@Config.Name("Require operator permissions")
		@Config.Comment("Only reply to requests from operators")
		var op = false

		@JvmField
		@Config.Name("Ignore blocked requests")
		@Config.Comment(
			"Data requests can be blocked because enabled is false or op is true and the player is not opped",
			"Whether to ignore (true) requests, or reply (false) to them with a message saying why the request has been blocked",
			"Ignoring has the benefit of the player not sending any more requests until they relog",
			"Replying has the benefit of the player knowing why their request has been blocked, and if they get unblocked, sending back proper data will not require a relog on the player's side"
		)
		var ignore = true
	}

	@Mod.EventBusSubscriber(modid = Reference.MODID)
	object ConfigEventHandler {
		@SubscribeEvent
		@JvmStatic
		fun onConfigChangedEvent(event: ConfigChangedEvent.OnConfigChangedEvent) {
			if(event.modID == Reference.MODID)
				ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE)
		}
	}
}
