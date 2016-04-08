package kernitus.plugin.Hotels.handlers;

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

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import kernitus.plugin.Hotels.HotelsMain;

public class HotelsConfigHandler {
	@SuppressWarnings("unused")
	private HotelsMain plugin;
	public HotelsConfigHandler(HotelsMain instance){
		this.plugin = instance;
	}
	//TODO Make static variable
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
		plugin.saveResource("config.yml", false);
		plugin.getLogger().info("Config file generated");
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

	public void reloadConfigs(Plugin plugin){
		//Reload config.yml
		if(getconfigymlFile().exists())
		plugin.reloadConfig();
		else
			setupConfigyml(plugin);
		
		//Message Queue
		if(!getMessageQueueFile().exists())
			setupMessageQueue();
		//Flags.yml
		if(!getFlagsFile().exists())
			setupFlags(plugin);

		//Locale
		localeLanguageSelector(plugin);
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

		flagsConfig.options().header("These are the flag values the hotel and room regions will have set to upon creation. Refer here for what each does and the data type http://docs.enginehub.org/manual/worldguard/latest/regions/flags/");
		//Hotel flags
		//Overrides
		flagsConfig.addDefault("hotel.overrides.PASSTHROUGH", "none");
		flagsConfig.addDefault("hotel.overrides.EXIT-OVERRIDE", "none");
		//Protection-Related
		flagsConfig.addDefault("hotel.protection.BUILD", "none");
		flagsConfig.addDefault("hotel.protection.INTERACT", "allow");
		flagsConfig.addDefault("hotel.protection.BLOCK-BREAK", "none");
		flagsConfig.addDefault("hotel.protection.BLOCK-PLACE", "none");
		flagsConfig.addDefault("hotel.protection.USE", "allow");
		flagsConfig.addDefault("hotel.protection.DAMAGE-ANIMALS", "none");
		flagsConfig.addDefault("hotel.protection.CHEST-ACCESS", "allow");
		flagsConfig.addDefault("hotel.protection.RIDE", "allow");
		flagsConfig.addDefault("hotel.protection.PVP", "deny");
		flagsConfig.addDefault("hotel.protection.SLEEP", "allow");
		flagsConfig.addDefault("hotel.protection.TNT", "deny");
		flagsConfig.addDefault("hotel.protection.VEHICLE-PLACE", "none");
		flagsConfig.addDefault("hotel.protection.VEHICLE-DESTROY", "none");
		flagsConfig.addDefault("hotel.protection.LIGHTER", "deny");
		//Mobs, fire, explosions
		flagsConfig.addDefault("hotel.mobs.CREEPER-EXPLOSION", "deny");
		flagsConfig.addDefault("hotel.mobs.ENDERDRAGON-BLOCK-DAMAGE", "deny");
		flagsConfig.addDefault("hotel.mobs.GHAST-FIREBALL", "deny");
		flagsConfig.addDefault("hotel.mobs.OTHER-EXPLOSION", "deny");
		flagsConfig.addDefault("hotel.mobs.FIRE-SPREAD", "deny");
		flagsConfig.addDefault("hotel.mobs.ENDERMAN-GRIEF", "deny");
		flagsConfig.addDefault("hotel.mobs.MOB-DAMAGE", "deny");
		flagsConfig.addDefault("hotel.mobs.MOB-SPAWNING", "deny");
		flagsConfig.addDefault("hotel.mobs.DENY-SPAWN", "none");
		flagsConfig.addDefault("hotel.mobs.ENTITY-PAINTING-DESTROY", "deny");
		flagsConfig.addDefault("hotel.mobs.ENTITY-ITEM-FRAME-DESTROY", "deny");
		//Natural Events
		flagsConfig.addDefault("hotel.nature.LAVA-FIRE", "deny");
		flagsConfig.addDefault("hotel.nature.LIGHTNING", "deny");
		flagsConfig.addDefault("hotel.nature.WATER-FLOW", "none");
		flagsConfig.addDefault("hotel.nature.LAVA-FLOW", "none");
		flagsConfig.addDefault("hotel.nature.SNOW-FALL", "deny");
		flagsConfig.addDefault("hotel.nature.SNOW-MELT", "deny");
		flagsConfig.addDefault("hotel.nature.ICE-FORM", "deny");
		flagsConfig.addDefault("hotel.nature.ICE-MELT", "deny");
		flagsConfig.addDefault("hotel.nature.MUSHROOM-GROWTH", "deny");
		flagsConfig.addDefault("hotel.nature.LEAF-DECAY", "deny");
		flagsConfig.addDefault("hotel.nature.GRASS-GROWTH", "deny");
		flagsConfig.addDefault("hotel.nature.MYCELIUM-SPREAD", "deny");
		flagsConfig.addDefault("hotel.nature.VINE-GROWTH", "deny");
		flagsConfig.addDefault("hotel.nature.SOIL-DRY", "deny");
		//Map-making
		flagsConfig.addDefault("hotel.map-making.ITEM-PICKUP", "none");
		flagsConfig.addDefault("hotel.map-making.ITEM-DROP", "none");
		flagsConfig.addDefault("hotel.map-making.EXP-DROPS", "none");
		flagsConfig.addDefault("hotel.map-making.DENY-MESSAGE", "none");
		flagsConfig.addDefault("hotel.map-making.ENTRY", "none");
		flagsConfig.addDefault("hotel.map-making.ENTRY-DENY-MESSAGE", "none");
		flagsConfig.addDefault("hotel.map-making.EXIT", "none");
		flagsConfig.addDefault("hotel.map-making.EXIT-DENY-MESSAGE", "none");
		flagsConfig.addDefault("hotel.map-making.EXIT-VIA-TELEPORT", "none");
		flagsConfig.addDefault("hotel.map-making.GREETING", "true");
		flagsConfig.addDefault("hotel.map-making.FAREWELL", "true");
		flagsConfig.addDefault("hotel.map-making.ENDERPEARL", "deny");
		flagsConfig.addDefault("hotel.map-making.INVICIBLE", "none");
		flagsConfig.addDefault("hotel.map-making.GAME-MODE", "none");
		flagsConfig.addDefault("hotel.map-making.TIME-LOCK", "none");
		flagsConfig.addDefault("hotel.map-making.WEATHER-LOCK", "none");
		flagsConfig.addDefault("hotel.map-making.HEAL-DELAY", "none");
		flagsConfig.addDefault("hotel.map-making.HEAL-AMOUNT", "none");
		flagsConfig.addDefault("hotel.map-making.HEAL-MIN-HEALTH", "none");
		flagsConfig.addDefault("hotel.map-making.HEAL-MAX-HEALTH", "none");
		flagsConfig.addDefault("hotel.map-making.FEED-DELAY", "none");
		flagsConfig.addDefault("hotel.map-making.FEED-AMOUNT", "none");
		flagsConfig.addDefault("hotel.map-making.FEED-MIN-HUNGER", "none");
		flagsConfig.addDefault("hotel.map-making.FEED-MAX-HUNGER", "none");
		flagsConfig.addDefault("hotel.map-making.TELEPORT", "none");
		flagsConfig.addDefault("hotel.map-making.SPAWN", "none");
		flagsConfig.addDefault("hotel.map-making.BLOCKED-CMDS", "none");
		flagsConfig.addDefault("hotel.map-making.ALLOWED-CMDS", "none");
		//Miscellaneous
		flagsConfig.addDefault("hotel.miscellaneous.PISTONS", "none");
		flagsConfig.addDefault("hotel.miscellaneous.SEND-CHAT", "none");
		flagsConfig.addDefault("hotel.miscellaneous.RECEIVE-CHAT", "none");
		flagsConfig.addDefault("hotel.miscellaneous.POTION-SPLASH", "none");
		flagsConfig.addDefault("hotel.miscellaneous.NOTIFY-ENTER", "none");
		flagsConfig.addDefault("hotel.miscellaneous.NOTIFY-LEAVE", "none");
		//Unused (by WorldGuard)
		flagsConfig.addDefault("hotel.unused.ALLOW-SHOP", "none");
		flagsConfig.addDefault("hotel.unused.BUYABLE", "none");
		flagsConfig.addDefault("hotel.unused.PRICE", "none");

		//Room flags
		//Overrides
		flagsConfig.addDefault("room.overrides.PASSTHROUGH", "none");
		flagsConfig.addDefault("room.overrides.EXIT-OVERRIDE", "none");
		//Protection-Related
		flagsConfig.addDefault("room.protection.BUILD", "none");
		flagsConfig.addDefault("room.protection.INTERACT", "-g non_members deny");
		flagsConfig.addDefault("room.protection.BLOCK-BREAK", "none");
		flagsConfig.addDefault("room.protection.BLOCK-PLACE", "none");
		flagsConfig.addDefault("room.protection.USE", "-g non_members deny");
		flagsConfig.addDefault("room.protection.DAMAGE-ANIMALS", "none");
		flagsConfig.addDefault("room.protection.CHEST-ACCESS", "-g non_members deny");
		flagsConfig.addDefault("room.protection.RIDE", "-g non_members deny");
		flagsConfig.addDefault("room.protection.PVP", "deny");
		flagsConfig.addDefault("room.protection.SLEEP", "-g non_members deny");
		flagsConfig.addDefault("room.protection.TNT", "deny");
		flagsConfig.addDefault("room.protection.VEHICLE-PLACE", "-g non_members deny");
		flagsConfig.addDefault("room.protection.VEHICLE-DESTROY", "-g non_members deny");
		flagsConfig.addDefault("room.protection.LIGHTER", "deny");
		//Mobs, fire, explosions
		flagsConfig.addDefault("room.mobs.CREEPER-EXPLOSION", "deny");
		flagsConfig.addDefault("room.mobs.ENDERDRAGON-BLOCK-DAMAGE", "deny");
		flagsConfig.addDefault("room.mobs.GHAST-FIREBALL", "deny");
		flagsConfig.addDefault("room.mobs.OTHER-EXPLOSION", "deny");
		flagsConfig.addDefault("room.mobs.FIRE-SPREAD", "deny");
		flagsConfig.addDefault("room.mobs.ENDERMAN-GRIEF", "deny");
		flagsConfig.addDefault("room.mobs.MOB-DAMAGE", "deny");
		flagsConfig.addDefault("room.mobs.MOB-SPAWNING", "deny");
		flagsConfig.addDefault("room.mobs.DENY-SPAWN", "none");
		flagsConfig.addDefault("room.mobs.ENTITY-PAINTING-DESTROY", "deny");
		flagsConfig.addDefault("room.mobs.ENTITY-ITEM-FRAME-DESTROY", "deny");
		//Natural Events
		flagsConfig.addDefault("room.nature.LAVA-FIRE", "deny");
		flagsConfig.addDefault("room.nature.LIGHTNING", "deny");
		flagsConfig.addDefault("room.nature.WATER-FLOW", "none");
		flagsConfig.addDefault("room.nature.LAVA-FLOW", "none");
		flagsConfig.addDefault("room.nature.SNOW-FALL", "deny");
		flagsConfig.addDefault("room.nature.SNOW-MELT", "deny");
		flagsConfig.addDefault("room.nature.ICE-FORM", "deny");
		flagsConfig.addDefault("room.nature.ICE-MELT", "deny");
		flagsConfig.addDefault("room.nature.MUSHROOM-GROWTH", "deny");
		flagsConfig.addDefault("room.nature.LEAF-DECAY", "deny");
		flagsConfig.addDefault("room.nature.GRASS-GROWTH", "deny");
		flagsConfig.addDefault("room.nature.MYCELIUM-SPREAD", "deny");
		flagsConfig.addDefault("room.nature.VINE-GROWTH", "deny");
		flagsConfig.addDefault("room.nature.SOIL-DRY", "deny");
		//Map-making
		flagsConfig.addDefault("room.map-making.ITEM-PICKUP", "none");
		flagsConfig.addDefault("room.map-making.ITEM-DROP", "none");
		flagsConfig.addDefault("room.map-making.EXP-DROPS", "none");
		flagsConfig.addDefault("room.map-making.DENY-MESSAGE", "none");
		flagsConfig.addDefault("room.map-making.ENTRY", "none");
		flagsConfig.addDefault("room.map-making.ENTRY-DENY-MESSAGE", "none");
		flagsConfig.addDefault("room.map-making.EXIT", "none");
		flagsConfig.addDefault("room.map-making.EXIT-DENY-MESSAGE", "none");
		flagsConfig.addDefault("room.map-making.EXIT-VIA-TELEPORT", "none");
		flagsConfig.addDefault("room.map-making.GREETING", "true");
		flagsConfig.addDefault("room.map-making.FAREWELL", "true");
		flagsConfig.addDefault("room.map-making.ENDERPEARL", "deny");
		flagsConfig.addDefault("room.map-making.INVICIBLE", "none");
		flagsConfig.addDefault("room.map-making.GAME-MODE", "none");
		flagsConfig.addDefault("room.map-making.TIME-LOCK", "none");
		flagsConfig.addDefault("room.map-making.WEATHER-LOCK", "none");
		flagsConfig.addDefault("room.map-making.HEAL-DELAY", "none");
		flagsConfig.addDefault("room.map-making.HEAL-AMOUNT", "none");
		flagsConfig.addDefault("room.map-making.HEAL-MIN-HEALTH", "none");
		flagsConfig.addDefault("room.map-making.HEAL-MAX-HEALTH", "none");
		flagsConfig.addDefault("room.map-making.FEED-DELAY", "none");
		flagsConfig.addDefault("room.map-making.FEED-AMOUNT", "none");
		flagsConfig.addDefault("room.map-making.FEED-MIN-HUNGER", "none");
		flagsConfig.addDefault("room.map-making.FEED-MAX-HUNGER", "none");
		flagsConfig.addDefault("room.map-making.TELEPORT", "none");
		flagsConfig.addDefault("room.map-making.SPAWN", "none");
		flagsConfig.addDefault("room.map-making.BLOCKED-CMDS", "none");
		flagsConfig.addDefault("room.map-making.ALLOWED-CMDS", "none");
		//Miscellaneous
		flagsConfig.addDefault("room.miscellaneous.PISTONS", "none");
		flagsConfig.addDefault("room.miscellaneous.SEND-CHAT", "none");
		flagsConfig.addDefault("room.miscellaneous.RECEIVE-CHAT", "none");
		flagsConfig.addDefault("room.miscellaneous.POTION-SPLASH", "-g non_members deny");
		flagsConfig.addDefault("room.miscellaneous.NOTIFY-ENTER", "none");
		flagsConfig.addDefault("room.miscellaneous.NOTIFY-LEAVE", "none");
		//Unused (by WorldGuard)
		flagsConfig.addDefault("room.unused.ALLOW-SHOP", "none");
		flagsConfig.addDefault("room.unused.BUYABLE", "none");
		flagsConfig.addDefault("room.unused.PRICE", "none");

		flagsConfig.options().copyDefaults(true);

		try {
			flagsConfig.save(flagsFile);
			plugin.getLogger().info(ChatColor.GREEN+"Flags file has been created");
		} catch (IOException e) {
			plugin.getLogger().severe(ChatColor.DARK_RED+"Could not save flags file");
			e.printStackTrace();
		}
	}
}
