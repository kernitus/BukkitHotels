package kernitus.plugin.Hotels.handlers;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import kernitus.plugin.Hotels.HotelsMain;

public class HotelsConfigHandler {
	@SuppressWarnings("unused")
	private HotelsMain plugin;
	public HotelsConfigHandler(HotelsMain instance){
		this.plugin = instance;
	}

	public void setupConfigs(Plugin pluginstance){
		//Message Queue
		if(!getMessageQueueFile().exists())
			setupMessageQueue();
		//Config.yml
		if (!getconfigymlFile().exists())
			setupConfigyml(pluginstance);

		//Locale
		localeLanguageSelector(pluginstance);
	}

	public void setupConfigyml(Plugin plugin){
		FileConfiguration config = plugin.getConfig();
		plugin.getLogger().info("[Hotels] Generating config file...");
		config.options().header("Hotels Plugin by kernitus\nAvailable languages: enGB, itIT, zhCN, znTW");
		config.addDefault("settings.language", String.valueOf("enGB"));
		config.addDefault("settings.commands.onlyDisplayAllowed", Boolean.TRUE);
		config.addDefault("settings.max_rooms_owned", Integer.valueOf(3));
		config.addDefault("settings.max_rent_extend", Integer.valueOf(3));
		config.addDefault("settings.use-permissions", Boolean.TRUE);
		config.addDefault("settings.checkForUpdates", Boolean.TRUE);
		config.addDefault("settings.use-hotel_enter_message", Boolean.TRUE);
		config.addDefault("settings.use-hotel_exit_message", Boolean.TRUE);
		config.addDefault("settings.use-room_enter_message", Boolean.TRUE);
		config.addDefault("settings.use-room_exit_message", Boolean.TRUE);

		config.options().copyDefaults(true);
		plugin.saveConfig();
		plugin.getLogger().info("[Hotels] Config file generated");
	}

	public void setupMessageQueue(){
		saveMessageQueue(getMessageQueue());
	}

	public void localeLanguageSelector(Plugin plugin){
		String lang = plugin.getConfig().getString("settings.language");
		YamlConfiguration locale = getLocale();
		String loclang = locale.getString("language"); //From already-generated locale.yml
		if(loclang==null){
			if(lang.equalsIgnoreCase("en")|lang.equalsIgnoreCase("enGB"))
				setupLanguage("enGB",plugin);
			else if(lang.equalsIgnoreCase("it")|lang.equalsIgnoreCase("itIT"))
				setupLanguage("itIT",plugin);
			else if(lang.equalsIgnoreCase("zhCN")|lang.equalsIgnoreCase("zh"))
				setupLanguage("zhCN",plugin);
			else if(lang.equalsIgnoreCase("zhTW"))
				setupLanguage("zhTW",plugin);
			else if(lang.equalsIgnoreCase("custom"))
				return;
			else
				setupLanguage("enGB",plugin);
		}
	}

	public File getFile(String filepath){
		return new File("plugins"+File.separator+"Hotels"+File.separator+filepath);
	}

	public YamlConfiguration getyml(File file){
		return YamlConfiguration.loadConfiguration(file);
	}

	public YamlConfiguration getyml(String filepath){
		return YamlConfiguration.loadConfiguration(getFile(filepath));
	}

	public File getconfigFile(String configName){
		return new File("plugins"+File.separator+"Hotels"+File.separator+configName+".yml");
	}

	public File getconfigymlFile(){
		return new File("plugins"+File.separator+"Hotels"+File.separator+"config.yml");
	}

	public File getLocaleFile(){
		return new File("plugins"+File.separator+"Hotels"+File.separator+"locale.yml");
	}

	public File getMessageQueueFile(){
		return new File("plugins"+File.separator+"Hotels"+File.separator+"queuedMessages.yml");
	}

	public YamlConfiguration getconfig(String configName){
		File file = getconfigFile(configName);
		return YamlConfiguration.loadConfiguration(file);
	}

	public YamlConfiguration getconfigyml(){
		File file = getconfigymlFile();
		return YamlConfiguration.loadConfiguration(file);
	}

	public YamlConfiguration getLocale(){
		File file = getLocaleFile();
		return YamlConfiguration.loadConfiguration(file);
	}

	public YamlConfiguration getMessageQueue(){
		File file = getMessageQueueFile();
		return YamlConfiguration.loadConfiguration(file);
	}

	public void saveConfiguration(File file, YamlConfiguration config){
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveLocale(YamlConfiguration config){
		File file = getLocaleFile();
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveMessageQueue(YamlConfiguration config){
		File file = getMessageQueueFile();
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveconfigyml(YamlConfiguration config){
		File file = getconfigymlFile();
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reloadLocale(Plugin pluginstance){
		if(!getLocaleFile().exists()){
			localeLanguageSelector(pluginstance);//Setup locale file from scratch
		}
		else{
			YamlConfiguration locale = getLocale();
			saveLocale(locale);
			getLocale();
		}
	}

	public void reloadMessageQueue(){
		if(!getMessageQueueFile().exists()){
			setupMessageQueue();//Setup message queue
		}
		else{
			YamlConfiguration mq = getMessageQueue();
			saveMessageQueue(mq);
			getMessageQueue();
		}
	}

	public void reloadConfigs(Plugin pluginstance){
		//Reload config.yml
		pluginstance.reloadConfig();
		//Reload locale.yml
		reloadLocale(pluginstance);
		//Reload queuedMessages.yml
		reloadMessageQueue();
	}

	public void setupLanguage(String langCode, Plugin plugin){
		plugin.saveResource("locale-"+langCode+".yml", false);
		File loc = getLocaleFile();
		File itLoc = getFile("locale-"+langCode+".yml");
		loc.delete();
		itLoc.renameTo(loc);
		plugin.getLogger().info(langCode+" Language strings generated");
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
			plugin.getLogger().info("&2Flags file has been created");
		} catch (IOException e) {
			e.printStackTrace();
			plugin.getLogger().severe("&4Could not save Flags file");
		}
	}*/
}
