package kernitus.plugin.Hotels.handlers;

import java.io.File;
import java.io.IOException;

import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class HotelsConfigHandler {

	private final static HotelsConfigHandler instance = new HotelsConfigHandler();

	public final static HotelsConfigHandler getInstance() {
		return instance;
	}

	public void setupConfigs(Plugin plugin){
		if (!new File(plugin.getDataFolder(), "config.yml").exists()) { //Checking if config file exists
			setupConfig(plugin);
		}
		String lang = plugin.getConfig().getString("settings.language"); //From config.yml
		File locale = new File("plugins//Hotels//locale.yml");
		if(locale.exists()){
			/*YamlConfiguration config = YamlConfiguration.loadConfiguration(locale);
			String loclang = config.getString("language"); //From already-generated locale.yml
			if(!lang.equalsIgnoreCase(loclang)){
			This is for future support of embedded locales
			}*/
		}
		else{
			if(lang.equalsIgnoreCase("en")){
				setupLanguageEnglish(plugin);
			}
		}
	}

	public void setupConfig(Plugin plugin){
		FileConfiguration config = plugin.getConfig();
		plugin.getLogger().info("[Hotels] Generating config file...");
		config.options().header("Hotels Plugin by kernitus");
		config.addDefault("settings.language", String.valueOf("en"));
		config.addDefault("settings.use-permissions", Boolean.valueOf(true));
		config.addDefault("settings.use-hotel_enter_message", Boolean.valueOf(true));
		config.addDefault("settings.use-hotel_exit_message", Boolean.valueOf(true));
		config.addDefault("settings.use-room_enter_message", Boolean.valueOf(true));
		config.addDefault("settings.use-room_exit_message", Boolean.valueOf(true));

		config.options().copyDefaults(true);
		plugin.saveConfig();
		plugin.getLogger().info("[Hotels] Config file generated");
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

	public void setupLanguageEnglish(Plugin plugin){
		File file = new File("plugins//Hotels//locale.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.addDefault("language", String.valueOf("en"));
		config.addDefault("main.enable.noVault", String.valueOf("No Vault dependency found!"));
		config.addDefault("main.enable.success", String.valueOf("%pluginname% v%version% has been enabled correctly"));
		config.addDefault("main.disable.success", String.valueOf("%pluginname% v%version% has been disabled"));

		config.addDefault("message.hotel.enter", String.valueOf("&bWelcome to the %hotel% hotel"));
		config.addDefault("message.hotel.exit", String.valueOf("&aCome back soon to the %hotel% hotel"));
		config.addDefault("message.room.enter", String.valueOf("&bWelcome to room %room%"));
		config.addDefault("message.room.exit", String.valueOf("&aCome back soon to Room %room%"));

		config.addDefault("sign.permanent", String.valueOf("Permanent"));
		config.addDefault("sign.room", String.valueOf("&aRoom"));
		config.addDefault("sign.room.free", String.valueOf("Free Rooms"));
		config.addDefault("sign.room.total", String.valueOf("Total Rooms"));
		config.addDefault("sign.reception", String.valueOf("Reception"));
		config.addDefault("sign.vacant", String.valueOf("Vacant"));
		config.addDefault("sign.rentExpiredConsole", String.valueOf("%player%'s rent of room %room% of the %hotel% hotel has expired"));
		config.addDefault("sign.rentExpiredPlayer", String.valueOf("&9Your rent of room %room% of the %hotel% hotel has expired"));
		config.addDefault("sign.delete.reception", String.valueOf("Reception file %filename% did not match in-game characteristics and has been deleted"));
		config.addDefault("sign.delete.roomNum", String.valueOf("Sign file %filename% did not match in-game roomNum and has been deleted"));
		config.addDefault("sign.delete.hotelName", String.valueOf("Sign file %filename% did not match in-game hotelname and has been deleted"));
		config.addDefault("sign.delete.location", String.valueOf("Sign file %filename% did not match in-game location and has been deleted"));

		config.addDefault("chat.prefix", String.valueOf("&9[Hotels]&r"));
		config.addDefault("chat.noPermission", String.valueOf("&4You do not have permission!"));

		config.addDefault("chat.sign.place.fileFail", String.valueOf("&4Could not save sign file"));
		config.addDefault("chat.sign.place.outOfRegion", String.valueOf("&4Sign is not within hotel region!"));
		config.addDefault("chat.sign.place.noHotel", String.valueOf("&4Hotel does not exist"));
		config.addDefault("chat.sign.place.emptySign", String.valueOf("&4Empty sign"));
		config.addDefault("chat.sign.place.success", String.valueOf("&2Hotel sign has been successfully created!"));
		config.addDefault("chat.sign.place.noRegion", String.valueOf("&4The specified hotel or room does not exist!"));
		config.addDefault("chat.sign.place.alreadyExists", String.valueOf("&4Sign for this hotel room already exists!"));
		config.addDefault("chat.sign.place.tooLong", String.valueOf("&4The room number or the price is too big!"));
		config.addDefault("chat.sign.place.noSeparator", String.valueOf("&4Line 3 must contain the separator &3:"));
		config.addDefault("chat.sign.use.success", String.valueOf("&aYou have rented room %room% of the %hotel% hotel for $%price%"));
		config.addDefault("chat.sign.use.notEnoughMoney", String.valueOf("&4You do not have enough money! You need another %missingmoney%"));
		config.addDefault("chat.sign.use.noAccount", String.valueOf("&4You do not have an economy account!"));
		config.addDefault("chat.sign.use.taken", String.valueOf("&4This room has already been rented"));
		config.addDefault("chat.sign.use.nonExistantRoom", String.valueOf("&4This room does not exist!"));
		config.addDefault("chat.sign.use.differentRoomNums", String.valueOf("&4Room numbers don't match!"));
		config.addDefault("chat.sign.use.differentHotelNames", String.valueOf("&4Hotel names don't match!"));
		config.addDefault("chat.sign.use.fileNonExistant", String.valueOf("&4Sign file does not exist!"));
		config.addDefault("chat.sign.use.signOutOfRegion", String.valueOf("&4Sign is not inside specified hotel region"));
		config.addDefault("chat.sign.use.differentRoomNums", String.valueOf("&4Room numbers don't match!"));


		config.addDefault("chat.creationMode.hotelCreationFailed", String.valueOf("&4Could not create Hotel, hotel already exists"));
		config.addDefault("chat.creationMode.hotelCreationSuccessful", String.valueOf("&2You have successfully created the %hotel% hotel"));
		config.addDefault("chat.creationMode.noSelection", String.valueOf("&4Please select an area using the WE wand"));
		config.addDefault("chat.creationMode.rooms.notInHotel", String.valueOf("&4The room is not in the specified hotel!"));
		config.addDefault("chat.creationMode.rooms.notInHotel", String.valueOf("&4Could not create room!"));
		config.addDefault("chat.creationMode.inventory.storeFail", String.valueOf("&4Could not store your inventory"));
		config.addDefault("chat.creationMode.inventory.storeSuccess", String.valueOf("&aSuccessfully stored your inventory"));
		config.addDefault("chat.creationMode.inventory.restoreFail", String.valueOf("&4Failed to find your stored inventory"));
		config.addDefault("chat.creationMode.inventory.restoreSuccess", String.valueOf("&aSuccessfully restored your inventory"));
		config.addDefault("chat.creationMode.armour.storeFail", String.valueOf("&4Could not store your armour"));
		config.addDefault("chat.creationMode.armour.storeSuccess", String.valueOf("&aSuccessfully stored your armour"));
		config.addDefault("chat.creationMode.armour.restoreFail", String.valueOf("&4Failed to find your stored armour"));
		config.addDefault("chat.creationMode.armour.restoreSuccess", String.valueOf("&aSuccessfully restored your armour"));
		config.addDefault("chat.creationMode.items.wand.name", String.valueOf("&bWorldEdit Wand"));
		config.addDefault("chat.creationMode.items.wand.lore1", String.valueOf("L-click one corner"));
		config.addDefault("chat.creationMode.items.wand.lore2", String.valueOf("R-click opposite corner"));
		config.addDefault("chat.creationMode.items.compass.name", String.valueOf("&bWorldEdit Compass"));
		config.addDefault("chat.creationMode.items.compass.lore1", String.valueOf("L-click to tp to"));
		config.addDefault("chat.creationMode.items.compass.lore2", String.valueOf("R-click to pass through"));
		config.addDefault("chat.creationMode.items.sign.name", String.valueOf("&bEpic Sign"));
		config.addDefault("chat.creationMode.items.sign.lore1", String.valueOf("R-click to place"));
		config.addDefault("chat.creationMode.items.sign.lore2", String.valueOf("First Line: �9[Hotels]"));

		config.addDefault("chat.commands.unknownArg", String.valueOf("&4Unknown argument. Try &3&o/hotels"));
		config.addDefault("chat.commands.noWorld", String.valueOf("&cPlease specify world"));
		config.addDefault("chat.commands.noHotel", String.valueOf("&cPlease specify hotel"));
		config.addDefault("chat.commands.noPlayer", String.valueOf("&cPlease specify player"));
		config.addDefault("chat.commands.userNonExistant", String.valueOf("&4Specified user does not exist"));
		config.addDefault("chat.commands.roomNonExistant", String.valueOf("&4Specified room does not exist"));
		config.addDefault("chat.commands.hotelNonExistant", String.valueOf("&4Specified hotel does not exist"));
		config.addDefault("chat.commands.worldNonExistant", String.valueOf("&4Specified world does not exist"));
		config.addDefault("chat.commands.creationMode.enter", String.valueOf("&aYou have entered hotel creation mode."));
		config.addDefault("chat.commands.creationMode.exit", String.valueOf("&aYou have exited hotel creation mode."));
		config.addDefault("chat.commands.creationMode.reset", String.valueOf("&2The inventory files have been reset."));
		config.addDefault("chat.commands.creationMode.noarg", String.valueOf("&4Please specify &6&oenter &4or &6&oexit &4mode"));
		config.addDefault("chat.commands.creationMode.consoleRejected", String.valueOf("The console can't use hotel creation mode!"));
		config.addDefault("chat.commands.create.fail", String.valueOf("&4Could not create hotel. Did you enter Hotel Creation Mode? (&3&o/hotels cm enter&r&4)"));
		config.addDefault("chat.commands.create.consoleRejected", String.valueOf("The console can't create a hotel!"));
		config.addDefault("chat.commands.create.noName", String.valueOf("&4Give a name to your hotel!"));
		config.addDefault("chat.commands.room.success", String.valueOf("&aYou have created room %room% of the %hotel% hotel"));
		config.addDefault("chat.commands.room.roomNumInvalid", String.valueOf("&4The room number is not a valid integer!"));
		config.addDefault("chat.commands.room.nextNewRoomFail", String.valueOf("&4Could not find next available room! Try specifying the room number manually"));
		config.addDefault("chat.commands.room.usage", String.valueOf("&4Correct Usage: &6&o/hotels room hotelname roomnum"));
		config.addDefault("chat.commands.deleteRoom.usage", String.valueOf("&4Correct usage: /hotels delr [hotelName] [roomNum] <world>"));
		config.addDefault("chat.commands.renumber.usage", String.valueOf("&4Correct usage: /hotels renum [hotelName] [oldNum] [newNum] <world>"));
		config.addDefault("chat.commands.rename.usage", String.valueOf("&4Correct usage: /hotels ren [oldName] [newName] <world>"));
		config.addDefault("chat.commands.renumber.success", String.valueOf("&2You have successfully changed room %oldnum% to room %newnum% of the %hotel% hotel"));
		config.addDefault("chat.commands.renumber.fail", String.valueOf("&4Could not renumber room %oldnum%"));
		config.addDefault("chat.commands.renumber.newNumTooBig", String.valueOf("&4New number is too big!"));
		config.addDefault("chat.commands.rename.success", String.valueOf("&2You have successfully renamed the %hotel% hotel"));
		config.addDefault("chat.commands.rename.failRooms", String.valueOf("&4Could not rename rooms"));
		config.addDefault("chat.commands.removeRoom.success", String.valueOf("&aSuccessfully deleted room"));
		config.addDefault("chat.commands.removeRoom.fail", String.valueOf("&4Could not delete room"));
		config.addDefault("chat.commands.removeRegions.success", String.valueOf("&aSuccessfully deleted hotel regions"));
		config.addDefault("chat.commands.removeRegions.fail", String.valueOf("&4Could not delete hotel regions"));
		config.addDefault("chat.commands.remove.playerNotRenter", String.valueOf("&4Specified player did not rent specified room!"));
		config.addDefault("chat.commands.remove.noRenter", String.valueOf("&4The specified room has not been rented!"));
		config.addDefault("chat.commands.remove.success", String.valueOf("&aSuccessfully removed %player% from room %room% of the %hotel% hotel"));
		config.addDefault("chat.commands.remove.usage", String.valueOf("&4Correct usage: /hotels remove [player] [hotel] [room] <world>"));
		config.addDefault("chat.commands.check.heading", String.valueOf("&a==Rented rooms list for %player%=="));
		config.addDefault("chat.commands.check.footer", String.valueOf("&c==End of rented rooms list for %player%=="));
		config.addDefault("chat.commands.check.line", String.valueOf("&6Hotel: &c%hotel%    &6Room: &c%room%    &6Expires in: %expirydate% minutes"));
		config.addDefault("chat.commands.listHotels.heading", String.valueOf("&a==Hotel list=="));
		config.addDefault("chat.commands.listHotels.footer", String.valueOf("&c==End of hotel list=="));
		config.addDefault("chat.commands.listHotels.line", String.valueOf("&6Hotel: &c%hotel%"));
		config.addDefault("chat.commands.listRooms.heading", String.valueOf("&a==Room list for %hotel% hotel=="));
		config.addDefault("chat.commands.listRooms.footer", String.valueOf("&c==End of room list for %hotel% hotel=="));
		config.addDefault("chat.commands.listRooms.line", String.valueOf("&6Room n: &c%room%"));
		config.addDefault("chat.commands.listRooms.noRooms", String.valueOf("&cThere are no rooms in that hotel"));
		config.addDefault("chat.commands.removeSigns.success", String.valueOf("&aSuccessfully removed all signs"));

		config.options().copyDefaults(true);
		try {
			config.save(file);
		} catch (IOException e) {
			System.out.println("Could not save locale config");
			e.printStackTrace();
		}
		plugin.getLogger().info("[Hotels] Language strings generated");
	}

	public void setupLanguageItalian(Plugin plugin){
		File file = new File("plugins//Hotels//locale.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.addDefault("language", String.valueOf("en"));
		config.addDefault("settings.chat.firstLine", String.valueOf("hotels"));
		config.addDefault("settings.chat.prefix", String.valueOf("[Hotels]"));
		config.addDefault("settings.chat.creationMode.hotelCreationFailed", String.valueOf("�4Could not create Hotel, hotel already exists"));

		config.options().copyDefaults(true);
		try {
			config.save(file);
		} catch (IOException e) {
			System.out.println("Could not save locale config");
			e.printStackTrace();
		}
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
			plugin.getLogger().info("�2Flags file has been created");
		} catch (IOException e) {
			e.printStackTrace();
			plugin.getLogger().severe("�4Could not save Flags file");
		}
	}*/
}
