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

	public void setupFlagsFile(Plugin plugin){

		File flagsFile = new File(plugin.getDataFolder()+"flags.yml");
		if(!flagsFile.exists())
			try {
				flagsFile.createNewFile();
			} catch (IOException e) {
				plugin.getLogger().severe("Could not create new flags.yml file");
				e.printStackTrace();
			}

		YamlConfiguration flagsConfig = YamlConfiguration.loadConfiguration(flagsFile);

		
		flagsConfig.addDefault("PASSTHROUGH", Boolean.valueOf(false));
		flagsConfig.addDefault("BUILD", Boolean.valueOf(false));
		flagsConfig.addDefault("CONSTRUCT", Boolean.valueOf(false));
		flagsConfig.addDefault("PVP", Boolean.valueOf(false));
		flagsConfig.addDefault("CHEST_ACCESS", Boolean.valueOf(false));
		flagsConfig.addDefault("PISTONS", Boolean.valueOf(false));
		flagsConfig.addDefault("TNT", Boolean.valueOf(false));
		flagsConfig.addDefault("LIGHTER", Boolean.valueOf(false));
		flagsConfig.addDefault("USE", Boolean.valueOf(false));
		flagsConfig.addDefault("PLACE_VEHICLE", Boolean.valueOf(false));
		flagsConfig.addDefault("DESTROY_VEHICLE", Boolean.valueOf(false));
		flagsConfig.addDefault("SLEEP", Boolean.valueOf(false));
		flagsConfig.addDefault("MOB_DAMAGE", Boolean.valueOf(false));
		flagsConfig.addDefault("MOB_SPAWNING", Boolean.valueOf(false));
		flagsConfig.addDefault("DENY_SPAWN", Boolean.valueOf(false));
		flagsConfig.addDefault("INVINCIBILITY", Boolean.valueOf(false));
		flagsConfig.addDefault("EXP_DROPS", Boolean.valueOf(false));
		flagsConfig.addDefault("CREEPER_EXPLOSION", Boolean.valueOf(false));
		flagsConfig.addDefault("OTHER_EXPLOSION", Boolean.valueOf(false));
		flagsConfig.addDefault("ENDERDRAGON_BLOCK_DAMAGE", Boolean.valueOf(false));
		flagsConfig.addDefault("GHAST_FIREBALL", Boolean.valueOf(false));
		flagsConfig.addDefault("ENDER_BUILD", Boolean.valueOf(false));
		flagsConfig.addDefault("GREET_MESSAGE", Boolean.valueOf(false));
		flagsConfig.addDefault("FAREWELL_MESSAGE", Boolean.valueOf(false));
		flagsConfig.addDefault("NOTIFY_ENTER", Boolean.valueOf(false));
		flagsConfig.addDefault("NOTIFY_LEAVE", Boolean.valueOf(false));
		flagsConfig.addDefault("EXIT", Boolean.valueOf(false));
		flagsConfig.addDefault("ENTRY", Boolean.valueOf(false));
		flagsConfig.addDefault("LIGHTNING", Boolean.valueOf(false));
		flagsConfig.addDefault("ENTITY_PAINTING_DESTROY", Boolean.valueOf(false));
		flagsConfig.addDefault("ENDERPEARL", Boolean.valueOf(false));
		flagsConfig.addDefault("ENTITY_ITEM_FRAME_DESTROY", Boolean.valueOf(false));
		flagsConfig.addDefault("ITEM_DROP", Boolean.valueOf(false));
		flagsConfig.addDefault("HEAL_AMOUNT", Boolean.valueOf(false));
		flagsConfig.addDefault("HEAL_DELAY", Boolean.valueOf(false));
		flagsConfig.addDefault("MIN_HEAL", Boolean.valueOf(false));
		flagsConfig.addDefault("MAX_HEAL", Boolean.valueOf(false));
		flagsConfig.addDefault("FEED_DELAY", Boolean.valueOf(false));
		flagsConfig.addDefault("FEED_AMOUNT", Boolean.valueOf(false));
		flagsConfig.addDefault("MIN_FOOD", Boolean.valueOf(false));
		flagsConfig.addDefault("MAX_FOOD", Boolean.valueOf(false));
		flagsConfig.addDefault("SNOW_FALL", Boolean.valueOf(false));
		flagsConfig.addDefault("SNOW_MELT", Boolean.valueOf(false));
		flagsConfig.addDefault("ICE_FORM", Boolean.valueOf(false));
		flagsConfig.addDefault("ICE_MELT", Boolean.valueOf(false));
		flagsConfig.addDefault("SOIL_DRY", Boolean.valueOf(false));
		flagsConfig.addDefault("GAME_MODE", Boolean.valueOf(false));
		flagsConfig.addDefault("MUSHROOMS", Boolean.valueOf(false));
		flagsConfig.addDefault("LEAF_DECAY", Boolean.valueOf(false));
		flagsConfig.addDefault("GRASS_SPREAD", Boolean.valueOf(false));
		flagsConfig.addDefault("MYCELIUM_SPREAD", Boolean.valueOf(false));
		flagsConfig.addDefault("VINE_GROWTH", Boolean.valueOf(false));
		flagsConfig.addDefault("SEND_CHAT", Boolean.valueOf(false));
		flagsConfig.addDefault("RECEIVE_CHAT", Boolean.valueOf(false));
		flagsConfig.addDefault("FIRE_SPREAD", Boolean.valueOf(false));
		flagsConfig.addDefault("LAVA_FIRE", Boolean.valueOf(false));
		flagsConfig.addDefault("LAVA_FLOW", Boolean.valueOf(false));
		flagsConfig.addDefault("WATER_FLOW", Boolean.valueOf(false));
		flagsConfig.addDefault("TELE_LOC", Boolean.valueOf(false));
		flagsConfig.addDefault("SPAWN_LOC", Boolean.valueOf(false));
		flagsConfig.addDefault("POTION_SPLASH", Boolean.valueOf(false));
		flagsConfig.addDefault("BLOCKED_CMDS", Boolean.valueOf(false));
		flagsConfig.addDefault("ALLOWED_CMDS", Boolean.valueOf(false));
		flagsConfig.addDefault("PRICE", Boolean.valueOf(false));
		flagsConfig.addDefault("BUYABLE", Boolean.valueOf(false));

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
