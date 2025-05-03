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
		"Available formats: {tps}, {mspt}, {tick}, {entity_count}, {tile_entity_count}, {chunk_count}, {ram_used}, {ram_max}, {cpu_usage}"
	)
	var text = arrayOf(
		"TPS {tps}, {mspt} ms",
		"E {entity_count}; TE {tile_entity_count}; C {chunk_count}",
		"RAM {ram_used} / {ram_max} CPU {cpu_usage}"
	)

	@JvmField
	@Config.Name("Refresh Interval")
	@Config.Comment("Refresh the data that gets shown on the client every x (ms)")
	@Config.RangeInt(min = 100, max = 5000)
	var refreshInterval = 500

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
