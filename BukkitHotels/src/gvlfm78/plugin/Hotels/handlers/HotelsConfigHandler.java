package kernitus.plugin.Hotels.handlers;

import kernitus.plugin.Hotels.HotelsMain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.protection.flags.StateFlag.State;

public class HotelsConfigHandler {
	@SuppressWarnings("unused")
	private HotelsMain plugin;
	public HotelsConfigHandler(HotelsMain instance){
		this.plugin = instance;
	}

	public void setupConfigs(Plugin plugin){
		//Message Queue
		if(!getMessageQueueFile().exists())
			setupMessageQueue();
		//Config.yml
		if (!getconfigymlFile().exists())
			setupConfigyml(plugin);

		//Flags.yml
		if(!getFlagsFile().exists())
			setupFlags(plugin);

		//Locale
		localeLanguageSelector(plugin);
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
		YamlConfiguration config = new YamlConfiguration();
		FileInputStream fileinputstream;

		try {
			fileinputstream = new FileInputStream(file);
			config.load(new InputStreamReader(fileinputstream, Charset.forName("UTF-8")));
		} catch (FileNotFoundException e){
			System.out.print("");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return config;
	}

	public YamlConfiguration getyml(String filepath){
		return getyml(getFile(filepath));
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

	public File getFlagsFile(){
		return new File("plugins"+File.separator+"Hotels"+File.separator+"flags.yml");
	}

	public YamlConfiguration getconfig(String configName){
		File file = getconfigFile(configName);
		return getyml(file);
	}

	public YamlConfiguration getconfigyml(){
		File file = getconfigymlFile();
		return getyml(file);
	}

	public YamlConfiguration getLocale(){
		File file = getLocaleFile();
		return getyml(file);
	}

	public YamlConfiguration getMessageQueue(){
		File file = getMessageQueueFile();
		return getyml(file);
	}

	public YamlConfiguration getFlags(){
		File file = getFlagsFile();
		return getyml(file);
	}

	public void saveConfiguration(File file, YamlConfiguration config){

		try{
			Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8")));
			fileWriter.write(config.saveToString());
			fileWriter.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}

	}

	public void saveLocale(YamlConfiguration config){
		File file = getLocaleFile();
		saveConfiguration(file,config);
	}

	public void saveMessageQueue(YamlConfiguration config){
		File file = getMessageQueueFile();
		saveConfiguration(file,config);
	}

	public void saveFlags(YamlConfiguration config){
		File file = getFlagsFile();
		saveConfiguration(file,config);
	}

	public void saveconfigyml(YamlConfiguration config){
		File file = getconfigymlFile();
		saveConfiguration(file,config);
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

	public void reloadFlags(Plugin plugin){
		if(!getFlagsFile().exists()){
			setupFlags(plugin);//Setup message queue
		}
		else{
			YamlConfiguration f = getFlags();
			saveFlags(f);
			getFlags();
		}
	}

	public void reloadConfigs(Plugin plugin){
		//Reload config.yml
		plugin.reloadConfig();
		//Reload locale.yml
		reloadLocale(plugin);
		//Reload queuedMessages.yml
		reloadMessageQueue();
		//Reload flags.yml
		reloadFlags(plugin);
	}

	public void setupLanguage(String langCode, Plugin plugin){
		plugin.saveResource("locale-"+langCode+".yml", false);
		File loc = getLocaleFile();
		File itLoc = getFile("locale-"+langCode+".yml");
		loc.delete();
		itLoc.renameTo(loc);
		plugin.getLogger().info(langCode+" Language strings generated");
	}

	public void setupFlags(Plugin plugin){

		File flagsFile = getFlagsFile();
		if(!flagsFile.exists())
			try {
				flagsFile.createNewFile();
			} catch (IOException e) {
				plugin.getLogger().severe("Could not create new flags.yml file");
				e.printStackTrace();
			}

		YamlConfiguration flagsConfig = getyml(flagsFile);

		//Hotel flags
		//Ovverrides
		flagsConfig.addDefault("hotel.overrides.PASSTHROUGH", "none");
		//Protection-Related
		flagsConfig.addDefault("hotel.protection.BUILD", "none");
		flagsConfig.addDefault("hotel.protection.INTERACT", "none");
		flagsConfig.addDefault("hotel.protection.BLOCK-BREAK", "none");
		flagsConfig.addDefault("hotel.protection.BLOCK-PLACE", "none");
		flagsConfig.addDefault("hotel.protection.USE", "none");
		flagsConfig.addDefault("hotel.protection.DAMAGE_ANIMALS", "none");
		flagsConfig.addDefault("hotel.protection.CHEST_ACCESS", "none");
		flagsConfig.addDefault("hotel.protection.RIDE", "none");
		flagsConfig.addDefault("hotel.protection.PVP", State.DENY);
		flagsConfig.addDefault("hotel.protection.SLEEP", "none");
		flagsConfig.addDefault("hotel.protection.TNT", State.DENY);
		flagsConfig.addDefault("hotel.protection.VEHICLE_PLACE", "none");
		flagsConfig.addDefault("hotel.protection.VEHICLE_DESTROY", "none");
		flagsConfig.addDefault("hotel.protection.LIGHTER", State.DENY);
		//Mobs, fire, explosions
		flagsConfig.addDefault("hotel.mobs.CREEPER_EXPLOSION", State.DENY);
		flagsConfig.addDefault("hotel.mobs.ENDERDRAGON_BLOCK_DAMAGE", State.DENY);
		flagsConfig.addDefault("hotel.mobs.GHAST_FIREBALL", State.DENY);
		flagsConfig.addDefault("hotel.mobs.OTHER_EXPLOSION", State.DENY);
		flagsConfig.addDefault("hotel.mobs.FIRE_SPREAD", State.DENY);
		flagsConfig.addDefault("hotel.mobs.ENDERMAN_GRIEF", State.DENY);
		flagsConfig.addDefault("hotel.mobs.MOB_DAMAGE", State.DENY);
		flagsConfig.addDefault("hotel.mobs.MOB_SPAWNING", State.DENY);
		flagsConfig.addDefault("hotel.mobs.DENY_SPAWN", "none");
		flagsConfig.addDefault("hotel.mobs.ENTITY_PAINTING_DESTROY", State.DENY);
		flagsConfig.addDefault("hotel.mobs.ENTITY_ITEM_FRAME_DESTROY", State.DENY);
		//Natural Events
		flagsConfig.addDefault("hotel.nature.LAVA_FIRE", State.DENY);
		flagsConfig.addDefault("hotel.nature.LIGHTNING", State.DENY);
		flagsConfig.addDefault("hotel.nature.WATER_FLOW", "none");
		flagsConfig.addDefault("hotel.nature.LAVA_FLOW", "none");
		flagsConfig.addDefault("hotel.nature.SNOW_FALL", State.DENY);
		flagsConfig.addDefault("hotel.nature.SNOW_MELT", State.DENY);
		flagsConfig.addDefault("hotel.nature.ICE_FORM", State.DENY);
		flagsConfig.addDefault("hotel.nature.ICE_MELT", State.DENY);
		flagsConfig.addDefault("hotel.nature.MUSHROOM_GROWTH", State.DENY);
		flagsConfig.addDefault("hotel.nature.LEAF_DECAY", State.DENY);
		flagsConfig.addDefault("hotel.nature.GRASS_GROWTH", State.DENY);
		flagsConfig.addDefault("hotel.nature.MYCELIUM_SPREAD", State.DENY);
		flagsConfig.addDefault("hotel.nature.VINE_GROWTH", State.DENY);
		flagsConfig.addDefault("hotel.nature.SOIL_DRY", State.DENY);
		//Map-making
		flagsConfig.addDefault("hotel.map-making.ITEM_PICKUP", "none");
		flagsConfig.addDefault("hotel.map-making.ITEM_DROP", "none");
		flagsConfig.addDefault("hotel.map-making.EXP_DROPS", "none");
		flagsConfig.addDefault("hotel.map-making.DENY_MESSAGE", "You aren't allowed!");
		flagsConfig.addDefault("hotel.map-making.ENTRY", "none");
		flagsConfig.addDefault("hotel.map-making.EXIT", "none");
		flagsConfig.addDefault("hotel.map-making.GREETING", "&bWelcome to the %hotel% hotel");
		flagsConfig.addDefault("hotel.map-making.FAREWELL", "&aCome back soon to the %hotel% hotel");
		flagsConfig.addDefault("hotel.map-making.ENDERPEARL", State.DENY);
		flagsConfig.addDefault("hotel.map-making.INVICIBLE", "none");
		flagsConfig.addDefault("hotel.map-making.GAME_MODE", "none");
		flagsConfig.addDefault("hotel.map-making.TIME_LOCK", "none");
		flagsConfig.addDefault("hotel.map-making.WEATHER_LOCK", "none");
		flagsConfig.addDefault("hotel.map-making.HEAL_DELAY", "none");
		flagsConfig.addDefault("hotel.map-making.HEAL_AMOUNT", "none");
		flagsConfig.addDefault("hotel.map-making.HEAL_MIN_HEALTH", "none");
		flagsConfig.addDefault("hotel.map-making.HEAL_MAX_HEALTH", "none");
		flagsConfig.addDefault("hotel.map-making.FEED_DELAY", "none");
		flagsConfig.addDefault("hotel.map-making.FEED_AMOUNT", "none");
		flagsConfig.addDefault("hotel.map-making.FEED_MIN_HUNGER", "none");
		flagsConfig.addDefault("hotel.map-making.FEED_MAX_HUNGER", "none");
		flagsConfig.addDefault("hotel.map-making.TELEPORT", "none");
		flagsConfig.addDefault("hotel.map-making.SPAWN", "none");
		flagsConfig.addDefault("hotel.map-making.BLOCKED_CMDS", "none");
		flagsConfig.addDefault("hotel.map-making.ALLOWED_CMDS", "none");
		//Miscellaneous
		flagsConfig.addDefault("hotel.miscellaneous.PISTONS", "none");
		flagsConfig.addDefault("hotel.miscellaneous.SEND_CHAT", "none");
		flagsConfig.addDefault("hotel.miscellaneous.RECEIVE_CHAT", "none");
		flagsConfig.addDefault("hotel.miscellaneous.POTION_SPLASH", State.DENY);
		flagsConfig.addDefault("hotel.miscellaneous.NOTIFY_ENTER", "none");
		flagsConfig.addDefault("hotel.miscellaneous.NOTIFY_LEAVE", "none");
		//Unused (by WorldGuard)
		flagsConfig.addDefault("hotel.unused.ALLOW-SHOP", "none");
		flagsConfig.addDefault("hotel.unused.BUYABLE", "none");
		flagsConfig.addDefault("hotel.unused.PRICE", "none");

		//Room flags
		//Ovverrides
		flagsConfig.addDefault("room.overrides.PASSTHROUGH", "none");
		//Protection-Related
		flagsConfig.addDefault("room.protection.BUILD", "none");
		flagsConfig.addDefault("room.protection.INTERACT", State.ALLOW);
		flagsConfig.addDefault("room.protection.BLOCK-BREAK", "none");
		flagsConfig.addDefault("room.protection.BLOCK-PLACE", "none");
		flagsConfig.addDefault("room.protection.USE", State.ALLOW);
		flagsConfig.addDefault("room.protection.DAMAGE_ANIMALS", "none");
		flagsConfig.addDefault("room.protection.CHEST_ACCESS", State.ALLOW);
		flagsConfig.addDefault("room.protection.RIDE", State.ALLOW);
		flagsConfig.addDefault("room.protection.PVP", State.DENY);
		flagsConfig.addDefault("room.protection.SLEEP", State.ALLOW);
		flagsConfig.addDefault("room.protection.TNT", State.DENY);
		flagsConfig.addDefault("room.protection.VEHICLE_PLACE", State.ALLOW);
		flagsConfig.addDefault("room.protection.VEHICLE_DESTROY", State.ALLOW);
		flagsConfig.addDefault("room.protection.LIGHTER", State.DENY);
		//Mobs, fire, explosions
		flagsConfig.addDefault("room.mobs.CREEPER_EXPLOSION", State.DENY);
		flagsConfig.addDefault("room.mobs.ENDERDRAGON_BLOCK_DAMAGE", State.DENY);
		flagsConfig.addDefault("room.mobs.GHAST_FIREBALL", State.DENY);
		flagsConfig.addDefault("room.mobs.OTHER_EXPLOSION", State.DENY);
		flagsConfig.addDefault("room.mobs.FIRE_SPREAD", State.DENY);
		flagsConfig.addDefault("room.mobs.ENDERMAN_GRIEF", State.DENY);
		flagsConfig.addDefault("room.mobs.MOB_DAMAGE", State.DENY);
		flagsConfig.addDefault("room.mobs.MOB_SPAWNING", State.DENY);
		flagsConfig.addDefault("room.mobs.DENY_SPAWN", "none");
		flagsConfig.addDefault("room.mobs.ENTITY_PAINTING_DESTROY", State.DENY);
		flagsConfig.addDefault("room.mobs.ENTITY_ITEM_FRAME_DESTROY", State.DENY);
		//Natural Events
		flagsConfig.addDefault("room.nature.LAVA_FIRE", State.DENY);
		flagsConfig.addDefault("room.nature.LIGHTNING", State.DENY);
		flagsConfig.addDefault("room.nature.WATER_FLOW", "none");
		flagsConfig.addDefault("room.nature.LAVA_FLOW", "none");
		flagsConfig.addDefault("room.nature.SNOW_FALL", State.DENY);
		flagsConfig.addDefault("room.nature.SNOW_MELT", State.DENY);
		flagsConfig.addDefault("room.nature.ICE_FORM", State.DENY);
		flagsConfig.addDefault("room.nature.ICE_MELT", State.DENY);
		flagsConfig.addDefault("room.nature.MUSHROOM_GROWTH", State.DENY);
		flagsConfig.addDefault("room.nature.LEAF_DECAY", State.DENY);
		flagsConfig.addDefault("room.nature.GRASS_GROWTH", State.DENY);
		flagsConfig.addDefault("room.nature.MYCELIUM_SPREAD", State.DENY);
		flagsConfig.addDefault("room.nature.VINE_GROWTH", State.DENY);
		flagsConfig.addDefault("room.nature.SOIL_DRY", State.DENY);
		//Map-making
		flagsConfig.addDefault("room.map-making.ITEM_PICKUP", "none");
		flagsConfig.addDefault("room.map-making.ITEM_DROP", "none");
		flagsConfig.addDefault("room.map-making.EXP_DROPS", "none");
		flagsConfig.addDefault("room.map-making.DENY_MESSAGE", "You aren't allowed!");
		flagsConfig.addDefault("room.map-making.ENTRY", "none");
		flagsConfig.addDefault("room.map-making.EXIT", "none");
		flagsConfig.addDefault("room.map-making.GREETING", "&&bWelcome to room %room%");
		flagsConfig.addDefault("room.map-making.FAREWELL", "&aCome back soon to Room %room%");
		flagsConfig.addDefault("room.map-making.ENDERPEARL", State.DENY);
		flagsConfig.addDefault("room.map-making.INVICIBLE", "none");
		flagsConfig.addDefault("room.map-making.GAME_MODE", "none");
		flagsConfig.addDefault("room.map-making.TIME_LOCK", "none");
		flagsConfig.addDefault("room.map-making.WEATHER_LOCK", "none");
		flagsConfig.addDefault("room.map-making.HEAL_DELAY", "none");
		flagsConfig.addDefault("room.map-making.HEAL_AMOUNT", "none");
		flagsConfig.addDefault("room.map-making.HEAL_MIN_HEALTH", "none");
		flagsConfig.addDefault("room.map-making.HEAL_MAX_HEALTH", "none");
		flagsConfig.addDefault("room.map-making.FEED_DELAY", "none");
		flagsConfig.addDefault("room.map-making.FEED_AMOUNT", "none");
		flagsConfig.addDefault("room.map-making.FEED_MIN_HUNGER", "none");
		flagsConfig.addDefault("room.map-making.FEED_MAX_HUNGER", "none");
		flagsConfig.addDefault("room.map-making.TELEPORT", "none");
		flagsConfig.addDefault("room.map-making.SPAWN", "none");
		flagsConfig.addDefault("room.map-making.BLOCKED_CMDS", "none");
		flagsConfig.addDefault("room.map-making.ALLOWED_CMDS", "none");
		//Miscellaneous
		flagsConfig.addDefault("room.miscellaneous.PISTONS", "none");
		flagsConfig.addDefault("room.miscellaneous.SEND_CHAT", "none");
		flagsConfig.addDefault("room.miscellaneous.RECEIVE_CHAT", "none");
		flagsConfig.addDefault("room.miscellaneous.POTION_SPLASH", State.ALLOW);
		flagsConfig.addDefault("room.miscellaneous.NOTIFY_ENTER", "none");
		flagsConfig.addDefault("room.miscellaneous.NOTIFY_LEAVE", "none");
		//Unused (by WorldGuard)
		flagsConfig.addDefault("room.unused.ALLOW-SHOP", "none");
		flagsConfig.addDefault("room.unused.BUYABLE", "none");
		flagsConfig.addDefault("room.unused.PRICE", "none");

		flagsConfig.options().copyDefaults(true);

		try {
			flagsConfig.save(flagsFile);
			plugin.getLogger().info("&2Flags file has been created");
		} catch (IOException e) {
			plugin.getLogger().severe("&4Could not save flags file");
			e.printStackTrace();
		}
	}
}
