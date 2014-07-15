package handlers;

import handlers.HotelsConfigHandler;

import org.bukkit.plugin.Plugin;

public class HotelsConfigHandler {

	static HotelsConfigHandler instance = new HotelsConfigHandler();

	public static HotelsConfigHandler getInstance() {
		return instance;
	}
	public void setupConfig(Plugin plugin){
		plugin.getLogger().info("[Hotels] Generating config file...");
		plugin.getConfig().options().header("Hotels Plugin by kernitus");
		plugin.getConfig().addDefault("settings.language", String.valueOf("en"));
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
		plugin.getLogger().info("[Hotels] Config file generated");
	}

	public void setupLanguageEnglish(Plugin plugin){
		plugin.getConfig().addDefault("settingsChat.firstLine", String.valueOf("Hotels"));
		plugin.getConfig().addDefault("settingsChat.prefix", String.valueOf("[Hotels]"));

		plugin.getConfig().addDefault("HCM.bossBar", Boolean.valueOf(false));

		plugin.getConfig().addDefault("onCommand.messageReloadConfig", String.valueOf("Configuration file reloaded"));
		plugin.getConfig().addDefault("onCommand.messageVersion", String.valueOf("version"));
		plugin.getConfig().addDefault("onCommand.messageNoPermission", String.valueOf("You are not allowed to use that command"));

		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
		plugin.getLogger().info("[Hotels] Language strings generated");
	}

	public void setLanguageEnglish(Plugin plugin){
		plugin.getConfig().set("settingsChat.firstLine", String.valueOf("Private"));
		plugin.getConfig().set("settingsChat.prefix", String.valueOf("[Hotels]"));

		plugin.getConfig().set("onCommand.messageReloadConfig", String.valueOf("Configuration file reloaded"));
		plugin.getConfig().set("onCommand.messageVersion", String.valueOf("version"));
		plugin.getConfig().set("onCommand.messageNoPermission", String.valueOf("You are not allowed to use that command"));

		plugin.saveConfig();
		plugin.getLogger().info("[Hotels] Language strings generated");
	}

	/*public void setupFlagsFile(Plugin plugin){
		Map<String, String> flags = new HashMap<String, String>(66);

		File configFile = new File("plugins//Hotels//flags.yml");
		if(!configFile.exists())
			try {
				configFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
				plugin.getLogger().severe("Could not create new flags.yml file");
			}
		
		YamlConfiguration cf = YamlConfiguration.loadConfiguration(configFile);

		flags.put("PASSTHROUGH", "DENY");
		flags.put("BUILD", "DENY");
		flags.put("CONSTRUCT", "DENY");
		flags.put("PVP", "DENY");
		flags.put("CHEST_ACCESS", "DENY");
		flags.put("PISTONS", "DENY");
		flags.put("TNT", "DENY");
		flags.put("LIGHTER", "DENY");
		flags.put("USE", "DENY");
		flags.put("PLACE_VEHICLE", "DENY");
		flags.put("DESTROY_VEHICLE", "DENY");
		flags.put("SLEEP", "DENY");
		flags.put("MOB_DAMAGE", "DENY");
		flags.put("MOB_SPAWNING", "DENY");
		flags.put("DENY_SPAWN", "DENY");
		flags.put("INVINCIBILITY", "DENY");
		flags.put("EXP_DROPS", "DENY");
		flags.put("CREEPER_EXPLOSION", "DENY");
		flags.put("OTHER_EXPLOSION", "DENY");
		flags.put("ENDERDRAGON_BLOCK_DAMAGE", "DENY");
		flags.put("GHAST_FIREBALL", "DENY");
		flags.put("ENDER_BUILD", "DENY");
		flags.put("GREET_MESSAGE", "DENY");
		flags.put("FAREWELL_MESSAGE", "DENY");
		flags.put("NOTIFY_ENTER", "DENY");
		flags.put("NOTIFY_LEAVE", "DENY");
		flags.put("EXIT", "DENY");
		flags.put("ENTRY", "DENY");
		flags.put("LIGHTNING", "DENY");
		flags.put("ENTITY_PAINTING_DESTROY", "DENY");
		flags.put("ENDERPEARL", "DENY");
		flags.put("ENTITY_ITEM_FRAME_DESTROY", "DENY");
		flags.put("ITEM_DROP", "DENY");
		flags.put("HEAL_AMOUNT", "DENY");
		flags.put("HEAL_DELAY", "DENY");
		flags.put("MIN_HEAL", "DENY");
		flags.put("MAX_HEAL", "DENY");
		flags.put("FEED_DELAY", "DENY");
		flags.put("FEED_AMOUNT", "DENY");
		flags.put("MIN_FOOD", "DENY");
		flags.put("MAX_FOOD", "DENY");
		flags.put("SNOW_FALL", "DENY");
		flags.put("SNOW_MELT", "DENY");
		flags.put("ICE_FORM", "DENY");
		flags.put("ICE_MELT", "DENY");
		flags.put("SOIL_DRY", "DENY");
		flags.put("GAME_MODE", "DENY");
		flags.put("MUSHROOMS", "DENY");
		flags.put("LEAF_DECAY", "DENY");
		flags.put("GRASS_SPREAD", "DENY");
		flags.put("MYCELIUM_SPREAD", "DENY");
		flags.put("VINE_GROWTH", "DENY");
		flags.put("SEND_CHAT", "DENY");
		flags.put("RECEIVE_CHAT", "DENY");
		flags.put("FIRE_SPREAD", "DENY");
		flags.put("LAVA_FIRE", "DENY");
		flags.put("LAVA_FLOW", "DENY");
		flags.put("WATER_FLOW", "DENY");
		flags.put("TELE_LOC", "DENY");
		flags.put("SPAWN_LOC", "DENY");
		flags.put("POTION_SPLASH", "DENY");
		flags.put("BLOCKED_CMDS", "DENY");
		flags.put("ALLOWED_CMDS", "DENY");
		flags.put("PRICE", "DENY");
		flags.put("BUYABLE", "DENY");
		//flags.put(");
		//flags.put("DefaultFlag);

		cf.set("Flags", flags);

		try {
			cf.save(configFile);
			plugin.getLogger().info("§2Flags file has been created");
		} catch (IOException e) {
			e.printStackTrace();
			plugin.getLogger().severe("§4Could not save Flags file");
		}
	}*/
}