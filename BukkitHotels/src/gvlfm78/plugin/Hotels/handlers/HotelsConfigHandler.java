package kernitus.plugin.Hotels.handlers;

import java.io.File;
import java.io.IOException;

import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class HotelsConfigHandler {
	@SuppressWarnings("unused")
	private HotelsMain plugin;
	public HotelsConfigHandler(HotelsMain instance){
		this.plugin = instance;
	}
	YamlConfiguration locale = getLocale();

	public void setupConfigs(Plugin plugin){
		if(!getMessageQueueFile().exists()){
			setupQueuedMessages(plugin);
		}
		if (!new File(plugin.getDataFolder(), "config.yml").exists()) { //Checking if config file exists
			setupConfig(plugin);
		}
		String lang = plugin.getConfig().getString("settings.language"); //From config.yml
			if(getLocale().contains("language")){
			/*YamlConfiguration config = YamlConfiguration.loadConfiguration(locale);
			String loclang = config.getString("language"); //From already-generated locale.yml
			if(!lang.equalsIgnoreCase(loclang)){
			This is for future support of embedded locales
			}*/
		}
		else{
			if(lang.equalsIgnoreCase("en")|lang.isEmpty()|lang==null){
				setupLanguageEnglish(plugin);
			}
			else{
				//Fallback language
				setupLanguageEnglish(plugin);
			}
		}
	}

	public void setupConfig(Plugin plugin){
		FileConfiguration config = plugin.getConfig();
		plugin.getLogger().info("[Hotels] Generating config file...");
		config.options().header("Hotels Plugin by kernitus");
		config.addDefault("settings.language", String.valueOf("en"));
		config.addDefault("settings.max_rooms_owned", Integer.valueOf(3));
		config.addDefault("settings.max_rent_extend", Integer.valueOf(3));
		config.addDefault("settings.use-permissions", Boolean.valueOf(true));
		config.addDefault("settings.checkForUpdates", Boolean.valueOf(true));
		config.addDefault("settings.use-hotel_enter_message", Boolean.valueOf(true));
		config.addDefault("settings.use-hotel_exit_message", Boolean.valueOf(true));
		config.addDefault("settings.use-room_enter_message", Boolean.valueOf(true));
		config.addDefault("settings.use-room_exit_message", Boolean.valueOf(true));

		config.options().copyDefaults(true);
		plugin.saveConfig();
		plugin.getLogger().info("[Hotels] Config file generated");
	}
	
	public void setupQueuedMessages(Plugin plugin){
		saveMessageQueue(getMessageQueue());
	}

	public void localeLanguageSelector(Plugin plugin){
		String lang = plugin.getConfig().getString("settings.language");
		if(lang.equalsIgnoreCase("en"))
			setupLanguageEnglish(plugin);
		else if(lang.equalsIgnoreCase("it"))
			setupLanguageItalian(plugin);
		else if(lang.equalsIgnoreCase("custom")){}
		else
			setupLanguageEnglish(plugin);

	}
	
	public File getFile(String filepath){
		return new File("plugins"+File.separator+"Hotels"+File.separator+filepath);
	}
	
	public YamlConfiguration getyml(String filepath){
		return YamlConfiguration.loadConfiguration(getFile(filepath));
	}
	
	public File getLocaleFile(){
		return new File("plugins//Hotels//locale.yml");
	}
	
	public File getMessageQueueFile(){
		return new File("plugins//Hotels//queuedMessages.yml");
	}
	
	public YamlConfiguration getLocale(){
		File file = getLocaleFile();
		return YamlConfiguration.loadConfiguration(file);
	}
	
	public YamlConfiguration getMessageQueue(){
		File file = getMessageQueueFile();
		return YamlConfiguration.loadConfiguration(file);
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
	
	public void reloadConfigs(Plugin plugin){
		//Reload config.yml
		plugin.reloadConfig();
		//Reload locale.yml
		getLocale();
		//Reload queuedMessages.yml
		getMessageQueue();
	}

	public void setupLanguageEnglish(Plugin plugin){
		locale.addDefault("language", String.valueOf("en"));
		locale.addDefault("main.enable.success", String.valueOf("%pluginname% v%version% has been enabled correctly"));
		locale.addDefault("main.disable.success", String.valueOf("%pluginname% v%version% has been disabled"));
		locale.addDefault("main.updateAvailable", String.valueOf("Hotels v%version% is available for download!"));
		locale.addDefault("main.updateAvailableLink", String.valueOf("To download it, go to %link%"));
		locale.addDefault("main.noConnection", String.valueOf("Could not connect to update site!"));

		locale.addDefault("message.hotel.enter", String.valueOf("&bWelcome to the %hotel% hotel"));
		locale.addDefault("message.hotel.exit", String.valueOf("&aCome back soon to the %hotel% hotel"));
		locale.addDefault("message.room.enter", String.valueOf("&bWelcome to room %room%"));
		locale.addDefault("message.room.exit", String.valueOf("&aCome back soon to Room %room%"));

		locale.addDefault("sign.permanent", String.valueOf("Permanent"));
		locale.addDefault("sign.room", String.valueOf("&aRoom"));
		locale.addDefault("sign.room.free", String.valueOf("Free Rooms"));
		locale.addDefault("sign.room.total", String.valueOf("Total Rooms"));
		locale.addDefault("sign.reception", String.valueOf("Reception"));
		locale.addDefault("sign.vacant", String.valueOf("Vacant"));
		locale.addDefault("sign.occupied", String.valueOf("Occupied"));
		locale.addDefault("sign.rentExpiredConsole", String.valueOf("%player%'s rent of room %room% of the %hotel% hotel has expired"));
		locale.addDefault("sign.rentExpiredPlayer", String.valueOf("&9Your rent of room %room% of the %hotel% hotel has expired"));
		locale.addDefault("sign.delete.reception", String.valueOf("Reception file %filename% did not match in-game characteristics and has been deleted"));
		locale.addDefault("sign.delete.roomNum", String.valueOf("Sign file %filename% did not match in-game roomNum and has been deleted"));
		locale.addDefault("sign.delete.hotelName", String.valueOf("Sign file %filename% did not match in-game hotelname and has been deleted"));
		locale.addDefault("sign.delete.location", String.valueOf("Sign file %filename% did not match in-game location and has been deleted"));

		locale.addDefault("chat.prefix", String.valueOf("&9[Hotels]&r"));
		locale.addDefault("chat.noPermission", String.valueOf("&4You do not have permission!"));

		locale.addDefault("chat.sign.place.fileFail", String.valueOf("&4Could not save sign file"));
		locale.addDefault("chat.sign.place.outOfRegion", String.valueOf("&4Sign is not within hotel region!"));
		locale.addDefault("chat.sign.place.noHotel", String.valueOf("&4Hotel does not exist"));
		locale.addDefault("chat.sign.place.emptySign", String.valueOf("&4Empty sign"));
		locale.addDefault("chat.sign.place.success", String.valueOf("&2Hotel sign has been successfully created!"));
		locale.addDefault("chat.sign.place.noRegion", String.valueOf("&4The specified hotel or room does not exist!"));
		locale.addDefault("chat.sign.place.alreadyExists", String.valueOf("&4Sign for this hotel room already exists!"));
		locale.addDefault("chat.sign.place.tooLong", String.valueOf("&4The room number or the price is too big!"));
		locale.addDefault("chat.sign.place.noSeparator", String.valueOf("&4Line 3 must contain the separator &3:"));
		locale.addDefault("chat.sign.use.success", String.valueOf("&aYou have rented room %room% of the %hotel% hotel for $%price%"));
		locale.addDefault("chat.sign.use.notEnoughMoney", String.valueOf("&4You do not have enough money! You need another %missingmoney%"));
		locale.addDefault("chat.sign.use.noAccount", String.valueOf("&4You do not have an economy account!"));
		locale.addDefault("chat.sign.use.taken", String.valueOf("&4This room has already been rented"));
		locale.addDefault("chat.sign.use.nonExistantRoom", String.valueOf("&4This room does not exist!"));
		locale.addDefault("chat.sign.use.differentRoomNums", String.valueOf("&4Room numbers don't match!"));
		locale.addDefault("chat.sign.use.differentHotelNames", String.valueOf("&4Hotel names don't match!"));
		locale.addDefault("chat.sign.use.fileNonExistant", String.valueOf("&4Sign file does not exist!"));
		locale.addDefault("chat.sign.use.signOutOfRegion", String.valueOf("&4Sign is not inside specified hotel region"));
		locale.addDefault("chat.sign.use.differentRoomNums", String.valueOf("&4Room numbers don't match!"));
		locale.addDefault("chat.sign.use.maxEntendReached", String.valueOf("&4You have reached the limit of rent extention of %max% times!"));
		locale.addDefault("chat.sign.use.maxRoomsReached", String.valueOf("&4You have reached the limit of %max% rooms you can own"));
		locale.addDefault("chat.sign.use.extensionSuccess", String.valueOf("&aRent has been extended %tot% times. You can extend it another %left% times."));
		locale.addDefault("chat.sign.use.extensionSuccessNoMore", String.valueOf("&aRent has been extended %tot% times. You can't extend it any more."));

		locale.addDefault("chat.creationMode.hotelCreationFailed", String.valueOf("&4Could not create Hotel, hotel already exists"));
		locale.addDefault("chat.creationMode.hotelCreationSuccessful", String.valueOf("&2You have successfully created the %hotel% hotel"));
		locale.addDefault("chat.creationMode.noSelection", String.valueOf("&4Please select an area using the WE wand"));
		locale.addDefault("chat.creationMode.rooms.notInHotel", String.valueOf("&4The room is not in the specified hotel!"));
		locale.addDefault("chat.creationMode.rooms.notInHotel", String.valueOf("&4Could not create room!"));
		locale.addDefault("chat.creationMode.inventory.storeFail", String.valueOf("&4Could not store your inventory"));
		locale.addDefault("chat.creationMode.inventory.storeSuccess", String.valueOf("&aSuccessfully stored your inventory"));
		locale.addDefault("chat.creationMode.inventory.restoreFail", String.valueOf("&4Failed to find your stored inventory"));
		locale.addDefault("chat.creationMode.inventory.restoreSuccess", String.valueOf("&aSuccessfully restored your inventory"));
		locale.addDefault("chat.creationMode.armour.storeFail", String.valueOf("&4Could not store your armour"));
		locale.addDefault("chat.creationMode.armour.storeSuccess", String.valueOf("&aSuccessfully stored your armour"));
		locale.addDefault("chat.creationMode.armour.restoreFail", String.valueOf("&4Failed to find your stored armour"));
		locale.addDefault("chat.creationMode.armour.restoreSuccess", String.valueOf("&aSuccessfully restored your armour"));
		locale.addDefault("chat.creationMode.items.wand.name", String.valueOf("&bWorldEdit Wand"));
		locale.addDefault("chat.creationMode.items.wand.lore1", String.valueOf("L-click one corner"));
		locale.addDefault("chat.creationMode.items.wand.lore2", String.valueOf("R-click opposite corner"));
		locale.addDefault("chat.creationMode.items.compass.name", String.valueOf("&bWorldEdit Compass"));
		locale.addDefault("chat.creationMode.items.compass.lore1", String.valueOf("L-click to tp to"));
		locale.addDefault("chat.creationMode.items.compass.lore2", String.valueOf("R-click to pass through"));
		locale.addDefault("chat.creationMode.items.sign.name", String.valueOf("&bEpic Sign"));
		locale.addDefault("chat.creationMode.items.sign.lore1", String.valueOf("R-click to place"));
		locale.addDefault("chat.creationMode.items.sign.lore2", String.valueOf("First Line: &9[Hotels]"));

		locale.addDefault("chat.commands.unknownArg", String.valueOf("&4Unknown argument. Try &3&o/hotels"));
		locale.addDefault("chat.commands.noWorld", String.valueOf("&cPlease specify world"));
		locale.addDefault("chat.commands.noHotel", String.valueOf("&cPlease specify hotel"));
		locale.addDefault("chat.commands.noPlayer", String.valueOf("&cPlease specify player"));
		locale.addDefault("chat.commands.userNonExistant", String.valueOf("&4Specified user does not exist"));
		locale.addDefault("chat.commands.roomNonExistant", String.valueOf("&4Specified room does not exist"));
		locale.addDefault("chat.commands.hotelNonExistant", String.valueOf("&4Specified hotel does not exist"));
		locale.addDefault("chat.commands.worldNonExistant", String.valueOf("&4Specified world does not exist"));
		locale.addDefault("chat.commands.reload.success", String.valueOf("&aConfiguration files successfully reloaded!"));
		locale.addDefault("chat.commands.creationMode.enter", String.valueOf("&aYou have entered hotel creation mode."));
		locale.addDefault("chat.commands.creationMode.exit", String.valueOf("&aYou have exited hotel creation mode."));
		locale.addDefault("chat.commands.creationMode.reset", String.valueOf("&2The inventory files have been reset."));
		locale.addDefault("chat.commands.creationMode.noarg", String.valueOf("&4Please specify &6&oenter &4or &6&oexit &4mode"));
		locale.addDefault("chat.commands.creationMode.consoleRejected", String.valueOf("The console can't use hotel creation mode!"));
		locale.addDefault("chat.commands.create.fail", String.valueOf("&4Could not create hotel. Did you enter Hotel Creation Mode? (&3&o/hotels cm enter&r&4)"));
		locale.addDefault("chat.commands.create.consoleRejected", String.valueOf("The console can't create a hotel!"));
		locale.addDefault("chat.commands.create.noName", String.valueOf("&4Give a name to your hotel!"));
		locale.addDefault("chat.commands.room.success", String.valueOf("&aYou have created room %room% of the %hotel% hotel"));
		locale.addDefault("chat.commands.room.roomNumInvalid", String.valueOf("&4The room number is not a valid integer!"));
		locale.addDefault("chat.commands.room.nextNewRoomFail", String.valueOf("&4Could not find next available room! Try specifying the room number manually"));
		locale.addDefault("chat.commands.room.usage", String.valueOf("&4Correct Usage: &6&o/hotels room hotelname roomnum"));
		locale.addDefault("chat.commands.deleteRoom.usage", String.valueOf("&4Correct usage: /hotels delr [hotelName] [roomNum] <world>"));
		locale.addDefault("chat.commands.renumber.usage", String.valueOf("&4Correct usage: /hotels renum [hotelName] [oldNum] [newNum] <world>"));
		locale.addDefault("chat.commands.rename.usage", String.valueOf("&4Correct usage: /hotels ren [oldName] [newName] <world>"));
		locale.addDefault("chat.commands.renumber.success", String.valueOf("&2You have successfully changed room %oldnum% to room %newnum% of the %hotel% hotel"));
		locale.addDefault("chat.commands.renumber.fail", String.valueOf("&4Could not renumber room %oldnum%"));
		locale.addDefault("chat.commands.renumber.newNumTooBig", String.valueOf("&4New number is too big!"));
		locale.addDefault("chat.commands.rename.success", String.valueOf("&2You have successfully renamed the %hotel% hotel"));
		locale.addDefault("chat.commands.rename.failRooms", String.valueOf("&4Could not rename rooms"));
		locale.addDefault("chat.commands.removeRoom.success", String.valueOf("&aSuccessfully deleted room"));
		locale.addDefault("chat.commands.removeRoom.fail", String.valueOf("&4Could not delete room"));
		locale.addDefault("chat.commands.removeRegions.success", String.valueOf("&aSuccessfully deleted hotel regions"));
		locale.addDefault("chat.commands.removeRegions.fail", String.valueOf("&4Could not delete hotel regions"));
		locale.addDefault("chat.commands.remove.playerNotRenter", String.valueOf("&4Specified player did not rent specified room!"));
		locale.addDefault("chat.commands.remove.noRenter", String.valueOf("&4The specified room has not been rented!"));
		locale.addDefault("chat.commands.remove.success", String.valueOf("&aSuccessfully removed %player% from room %room% of the %hotel% hotel"));
		locale.addDefault("chat.commands.remove.usage", String.valueOf("&4Correct usage: /hotels remove [player] [hotel] [room] <world>"));
		locale.addDefault("chat.commands.check.heading", String.valueOf("&a==Rented rooms list for %player%=="));
		locale.addDefault("chat.commands.check.footer", String.valueOf("&c==End of rented rooms list for %player%=="));
		locale.addDefault("chat.commands.check.line", String.valueOf("&6Hotel: &c%hotel%    &6Room: &c%room%    &6Expires in: &c%timeleft%"));
		locale.addDefault("chat.commands.listHotels.heading", String.valueOf("&a==Hotel list=="));
		locale.addDefault("chat.commands.listHotels.footer", String.valueOf("&c==End of hotel list=="));
		locale.addDefault("chat.commands.listHotels.line", String.valueOf("&6Hotel: &c%hotel%%space%&9Total: &r%total%   &aFree: &r%free%"));
		locale.addDefault("chat.commands.listRooms.heading", String.valueOf("&a==Room list for %hotel% hotel=="));
		locale.addDefault("chat.commands.listRooms.footer", String.valueOf("&c==End of room list for %hotel% hotel=="));
		locale.addDefault("chat.commands.listRooms.line", String.valueOf("&6Room n: &c%room%%space%%state%"));
		locale.addDefault("chat.commands.listRooms.noRooms", String.valueOf("&cThere are no rooms in that hotel"));
		locale.addDefault("chat.commands.removeSigns.success", String.valueOf("&aSuccessfully removed all signs"));
		locale.addDefault("chat.commands.friend.usage", String.valueOf("&4Correct usage: /hotels friend [add/remove/list] [hotel] [room] <friendname>"));
		locale.addDefault("chat.commands.friend.wrongData", String.valueOf("&4The hotel or room number entered do not match any existing location"));
		locale.addDefault("chat.commands.friend.noRenter", String.valueOf("&4The room you specified has no renter!"));
		locale.addDefault("chat.commands.friend.notRenter", String.valueOf("&4You are not the renter of the specified room!"));
		locale.addDefault("chat.commands.friend.consoleRejected", String.valueOf("The console can't add/remove friend from a room!"));
		locale.addDefault("chat.commands.friend.addYourself", String.valueOf("&4You can't add yourself to the friend list!"));
		locale.addDefault("chat.commands.friend.nonExistant", String.valueOf("&4You can't add imaginary friends to the friend list!"));
		locale.addDefault("chat.commands.friend.friendNotInList", String.valueOf("&4The user you specified is not in the friend list!"));
		locale.addDefault("chat.commands.friend.addSuccess", String.valueOf("&aSuccessfully added %friend% to the friend list"));
		locale.addDefault("chat.commands.friend.removeSuccess", String.valueOf("&aSuccessfully removed %friend% to the friend list"));
		locale.addDefault("chat.commands.friend.noFriends", String.valueOf("&2You have not added any friends to the specified room"));
		locale.addDefault("chat.commands.friend.list.heading", String.valueOf("&a==Friend list for room n. %room% of the %hotel% hotel=="));
		locale.addDefault("chat.commands.friend.list.footer", String.valueOf("&c==End of friend list=="));
		locale.addDefault("chat.commands.friend.list.line", String.valueOf("&6Friend: &c%name%"));
		
		locale.options().copyDefaults(true);
		saveLocale(locale);
		plugin.getLogger().info("[Hotels] Language strings generated");
	}

	public void setupLanguageItalian(Plugin plugin){
		locale.addDefault("language", String.valueOf("en"));
		locale.addDefault("settings.chat.firstLine", String.valueOf("hotels"));
		locale.addDefault("settings.chat.prefix", String.valueOf("[Hotels]"));
		locale.addDefault("settings.chat.creationMode.hotelCreationFailed", String.valueOf("&4Could not create Hotel, hotel already exists"));

		locale.options().copyDefaults(true);
		saveLocale(locale);
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
			plugin.getLogger().info("&2Flags file has been created");
		} catch (IOException e) {
			e.printStackTrace();
			plugin.getLogger().severe("&4Could not save Flags file");
		}
	}*/
}
