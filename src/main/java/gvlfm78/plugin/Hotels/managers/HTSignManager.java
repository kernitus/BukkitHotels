package kernitus.plugin.Hotels.managers;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import kernitus.plugin.Hotels.Hotel;
import kernitus.plugin.Hotels.HotelsAPI;
import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.Room;
import kernitus.plugin.Hotels.exceptions.EventCancelledException;
import kernitus.plugin.Hotels.exceptions.RoomNotSetupException;
import kernitus.plugin.Hotels.exceptions.RoomSignInRoomException;
import kernitus.plugin.Hotels.handlers.HTConfigHandler;
import kernitus.plugin.Hotels.handlers.HTMessageQueue;
import kernitus.plugin.Hotels.handlers.MessageType;
import kernitus.plugin.Hotels.signs.ReceptionSign;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTSignManager {

	public static void placeReceptionSign(SignChangeEvent e) {
		Player p = e.getPlayer();
		//Sign Lines
		String hotelName = ChatColor.stripColor(e.getLine(1)).trim();
		if (hotelName.isEmpty()) {
			e.setLine(0, ChatColor.DARK_RED + "]Hotels[");
			Mes.mes(p, "chat.sign.place.emptySign");
			return;
		}
		World world = p.getWorld();
		Hotel hotel = new Hotel(world, hotelName);
		if (!hotel.exists()) { //Hotel exists
			e.setLine(0, ChatColor.DARK_RED + "]Hotels[");
			Mes.mes(p, "chat.sign.place.noHotel");
			return;
		}

		String tot = String.valueOf(hotel.getTotalRoomCount()); //Getting total amount of rooms in hotel
		String free = String.valueOf(hotel.getFreeRoomCount()); //Getting amount of free rooms in hotel

		//Setting all sign lines
		e.setLine(0, (ChatColor.GREEN + Mes.getStringNoPrefix("sign.reception.reception")));
		e.setLine(1, (ChatColor.DARK_BLUE + hotelName + " Hotel"));
		e.setLine(2, (ChatColor.DARK_BLUE + String.valueOf(tot) + ChatColor.BLACK + " " + Mes.getStringNoPrefix("sign.room.total")));
		e.setLine(3, (ChatColor.GREEN + String.valueOf(free) + ChatColor.BLACK + " " + Mes.getStringNoPrefix("sign.room.free")));

		//Loop will stop once a non-existent file is found
		File receptionFile = HTConfigHandler.getReceptionFile(hotelName, 1);
		for (int i = 1; receptionFile.exists(); i++)
			receptionFile = HTConfigHandler.getReceptionFile(hotelName, i);

		try {
			receptionFile.getParentFile().mkdirs();
			receptionFile.createNewFile();
		} catch (IOException e1) {
			Mes.mes(p, "chat.sign.place.fileFail");
			e1.printStackTrace();
			return;
		}

		YamlConfiguration config = YamlConfiguration.loadConfiguration(receptionFile);
		config.addDefault("hotel", hotelName);
		config.addDefault("location.world", e.getBlock().getWorld().getUID().toString());
		config.addDefault("location.x", e.getBlock().getX());
		config.addDefault("location.y", e.getBlock().getY());
		config.addDefault("location.z", e.getBlock().getZ());
		config.options().copyDefaults(true);

		try {
			config.save(receptionFile);
		} catch (IOException e1) {
			Mes.mes(p, "chat.sign.place.fileFail");
			e1.printStackTrace();
		}
	}

	public static void placeRoomSign(SignChangeEvent e) throws RoomSignInRoomException {
		Player p = e.getPlayer();

		//Sign Lines
		String Line2 = ChatColor.stripColor(e.getLine(1)).trim();
		String Line3 = ChatColor.stripColor(e.getLine(2)).trim();
		String Line4 = ChatColor.stripColor(e.getLine(3)).trim();

		Sign sign = (Sign) e.getBlock().getState();

		if (!Line3.contains(":")) {
			Mes.mes(p, "chat.sign.place.noSeparator");
			e.setLine(0, ChatColor.DARK_RED + "]Hotels[");
			return;
		}

		String[] Line3parts = Line3.split(":");

		if (createRoomSign(sign, p, Line2, Line3parts[0], Line4, Line3parts[1]))
			Mes.mes(p, "chat.sign.place.success");
		else {
			sign.setLine(0, ChatColor.DARK_RED + "]Hotels[");
			sign.update();
		}
	}

	public static boolean createRoomSign(Sign sign, Player p, String hotelName, String roomNum, String time, String price) throws RoomSignInRoomException {
		World world = p.getWorld();

		Hotel hotel = new Hotel(world, hotelName);

		if (!hotel.exists()) {
			Mes.mes(p, "chat.sign.place.noRegion");
			return false;
		}
		if (!hotel.isOwner(p.getName()) && !hotel.isOwner(p.getUniqueId()) && !Mes.hasPerm(p, "hotels.sign.create.admin")) {
			Mes.mes(p, "chat.commands.youDoNotOwnThat");
			return false;
		}

		try {
			Integer.parseInt(roomNum); //Room Number
		} catch (NumberFormatException e1) {
			Mes.mes(p, "chat.commands.room.roomNumInvalid");
			return false;
		}

		if ((roomNum.length() + price.length() + 9) > 21) {
			Mes.mes(p, "chat.sign.place.tooLong");
			return false;
		}

		try {
			Integer.parseInt(price); //Cost
		} catch (NumberFormatException e1) {
			Mes.mes(p, "chat.commands.sellhotel.invalidPrice");
			return false;
		}

		Room room = new Room(hotel, roomNum);

		if (room.doesSignFileExist()) {
			Mes.mes(p, "chat.sign.place.alreadyExists");
			return false;
		}

		//If sign is within hotel region
		int x = sign.getX();
		int y = sign.getY();
		int z = sign.getZ();
		if (!hotel.getRegion().contains(x, y, z)) {
			Mes.mes(p, "chat.sign.place.outOfRegion");
			return false;
		}

		if (!room.exists()) {
			Mes.mes(p, "chat.sign.place.noRegion");
			return false;
		}

		//Checking sign is not inside any resettable room
		for (Room tempRoom : hotel.getRooms())
			if (tempRoom.shouldReset() && tempRoom.getRegion().contains(x, y, z)) throw new RoomSignInRoomException();

		//Successful Sign

		long timeInMins = TimeConverter(time);
		double accCost = CostConverter(price);

		try {
			room.createSignConfig(p, timeInMins, accCost, sign.getLocation());
		} catch (IOException e1) {
			Mes.mes(p, "chat.sign.place.fileFail");
			e1.printStackTrace();
			return false;
		}

		//Room reset setup
		try {
			room.setShouldReset(HTConfigHandler.getconfigYML().getBoolean("defaultReset", false));
		} catch (DataException | IOException | WorldEditException e1) {
			Mes.mes(p, "chat.commands.somethingWentWrong");
			e1.printStackTrace();
		} catch (RoomNotSetupException e1) {
			Mes.mes(p, "chat.commands.resetroom.notSetup");
			return false;
		} catch (RoomSignInRoomException e1) {
			Mes.mes(p, "chat.sign.place.inRoomRegion");
			return false;
		}

		sign.setLine(0, ChatColor.DARK_BLUE + hotelName); //Hotel Name
		sign.setLine(1, ChatColor.DARK_GREEN +
				Mes.getStringNoPrefix("sign.room.name") + " " +
				roomNum + " - " + price.toUpperCase() + "$"); //Room Number + Cost

		if (time.matches("0")) sign.setLine(2, Mes.getStringNoPrefix("sign.permanent"));
		else sign.setLine(2, TimeFormatter(timeInMins));

		sign.setLine(3, ChatColor.GREEN + Mes.getStringNoPrefix("sign.vacant"));
		sign.update();

		Mes.mes(p, "chat.sign.place.success");

		return true; //(Supposedly) everything went fine
	}

	public static boolean isReceptionSign(Sign s) {
		return ChatColor.stripColor(s.getLine(0)).equalsIgnoreCase(ChatColor.stripColor(Mes.getStringNoPrefix("sign.reception.reception")));
	}

	public static void useRoomSign(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		World world = p.getWorld();

		//Sign lines
		Sign s = (Sign) e.getClickedBlock().getState();
		String Line2 = ChatColor.stripColor(s.getLine(1)); //Line2
		String hotelName = ChatColor.stripColor(s.getLine(0)); //Hotel name

		Hotel hotel = new Hotel(world, hotelName);

		//If Hotel exists
		if (!hotel.exists()) return;

		int x = s.getX();
		int y = s.getY();
		int z = s.getZ();

		//If sign is within region
		if (!hotel.getRegion().contains(x, y, z)) {
			Mes.mes(p, "chat.sign.use.signOutOfRegion");
			return;
		}

		String[] Line2parts = Line2.split("\\s"); //Splitting Line2 into room num + cost
		String roomNum = Line2parts[1]; //Room Number
		Room room = new Room(hotel, roomNum);

		if (!room.doesSignFileExist()) {
			Mes.mes(p, "chat.sign.use.fileNonExistent");
			return;
		}

		if (!hotelName.equalsIgnoreCase(room.getHotelNameFromConfig())) {
			Mes.mes(p, "chat.sign.use.differentHotelNames");
			return;
		}

		if (roomNum.equals(room.getRoomNumFromConfig()))//If room nums match
			rentRoom(p, room); //This will also check if rent should be extended and not new
		else Mes.mes(p, "chat.sign.use.differentRoomNums");
	}

	public static void rentRoom(Player p, Room room) {
		String hotelName = room.getHotel().getName();

		if (!room.exists()) {
			Mes.mes(p, "chat.sign.use.nonExistentRoom");
			return;
		}

		OfflinePlayer renter = room.getRenter();

		if (room.isFree()) {
			if (isPlayerOverRoomLimitPerHotel(hotelName, p)) {//If player is under per-hotel limit
				Mes.mes(p, "chat.sign.use.overRoomsPerHotelLimit" ,
						"%limit%", HTConfigHandler.getconfigYML().getString("maxRoomsOwnedPerHotel", "2"));
				return;
			}

			double account = HotelsMain.economy.getBalance(p);
			double price = room.getCost();
			if (account >= price) {//If player has enough money
				//If player is under max owned rooms limit
				int maxRoomsOwned = HTConfigHandler.getconfigYML().getInt("maxRoomsOwned", 3);

				if (HotelsAPI.getRoomsRentedBy(p.getUniqueId()).size() >= maxRoomsOwned) {
					Mes.mes(p, "chat.sign.use.maxRoomsReached",
							"%max%", String.valueOf(maxRoomsOwned));
					return;
				}

				//Renter has passed all conditions and is able to rent this room
				HotelsMain.economy.withdrawPlayer(p, price);//Taking money from renter
				payOwners(price, room, false); //Pay the hotel owners the net profit

				try {
					room.rent(p);
				} catch (EventCancelledException e) {
				} catch (IOException e) {
					Mes.mes(p, "chat.commands.somethingWentWrong");
					e.printStackTrace();
				}

				DecimalFormat df = new DecimalFormat("#.00");

				Mes.mes(p, "chat.sign.use.success",
						"%room%", String.valueOf(room.getNum()),
						"%hotel%", hotelName,
						"%price%", df.format(price));
				//Successfully rented room	
			} else
				Mes.mes(p, "chat.sign.use.notEnoughMoney",
						"%missingmoney%", String.valueOf(price - account));
		} else if (renter.getUniqueId().equals(p.getUniqueId()))
			//Renter is same player that right clicked
			rentExtend(p, room);
		else Mes.mes(p, "chat.sign.use.taken");
	}

	public static void payOwners(double price, Room room, boolean isRentExtend) {
		Hotel hotel = room.getHotel();
		String tax = HTConfigHandler.getconfigYML().getString("tax", "20%");
		double revenue = price;
		double taxValue;

		if (tax.matches("\\d+%")) {//If it's a percentage
			tax = tax.replace("%", "");
			try {
				taxValue = Double.parseDouble(tax);
			} catch (NumberFormatException e) {
				//Tax value is invalid, assuming it is 0%
				taxValue = 0;
			}
			if (taxValue >= 0 && taxValue <= 100)
				revenue -= revenue * (taxValue / 100);
		} else if (tax.matches("\\d+")) {//If it's a set amount
			try {
				taxValue = Double.parseDouble(tax);
			} catch (NumberFormatException e) {
				//Tax value is invalid, assuming it is 0%
				taxValue = 0;
			}
			revenue -= taxValue;
		}

		//Giving to all owners the revenue
		for (UUID uuid : hotel.getOwners().getUniqueIds()) {
			OfflinePlayer owner = Bukkit.getOfflinePlayer(uuid);
			String chatMessage;

			if (isRentExtend) chatMessage = "chat.moneyEarnedExtend";
			else chatMessage = "chat.moneyEarned";

			if (revenue < 0) revenue = 0;

			HotelsMain.economy.depositPlayer(owner, revenue);

			if (!owner.isOnline()) {
				HTMessageQueue.addMessage(MessageType.revenue, owner.getUniqueId(), Mes.getString(chatMessage,
						"%revenue%", new DecimalFormat("#.00").format(revenue),
						"%room%", String.valueOf(room.getNum()),
						"%hotel%", hotel.getName()));
				return;
			}

			Player player = (Player) owner;
			Mes.mes(player, chatMessage, "%revenue%", new DecimalFormat("#.00").format(revenue),
					"%hotel%", hotel.getName(),
					"%room%", String.valueOf(room.getNum()));
			Mes.debug("Payed owner");
		}
	}

	/**
	 * @return Whether the event should be cancelled
	 */
	public static boolean breakSign(Sign s, Player p) {
		//Delegates to correct sign breaking method
		if (isReceptionSign(s))
			breakReceptionSign(s);
		else return breakRoomSign(s, p);

		return false;
	}

	public static void breakReceptionSign(Sign s) {
		Hotel hotel = new Hotel(s.getWorld(), ChatColor.stripColor(s.getLine(1)).split(" ")[0]);

		for (ReceptionSign rs : hotel.getAllReceptionSigns())
			if (rs.getLocation().equals(s.getLocation()))
				rs.deleteSignAndConfig();
	}

	/**
	 * @param s Sign that was broken
	 * @param p Player that broke the sign
	 * @return Whether the event should be cancelled or not
	 */
	public static boolean breakRoomSign(Sign s, Player p) {
		World w = s.getWorld();
		String hotelName = ChatColor.stripColor(s.getLine(0));

		Hotel hotel = new Hotel(s.getWorld(), hotelName);
		if (!hotel.exists()) return false;

		//Room sign has been broken?
		if (!hotel.getRegion().contains(s.getX(), s.getY(), s.getZ())) return false;

		String roomNum = ChatColor.stripColor(s.getLine(1)).split(" ")[1];

		try {
			Integer.parseInt(roomNum);
		} catch (Exception e1) {
			return false;
		}

		Room room = new Room(hotel, String.valueOf(roomNum));

		if (!room.exists() || !room.doesSignFileExist()) return false;

		if (!room.getHotelNameFromConfig().equalsIgnoreCase(hotelName))
			return false; //If sign and config hotel names match

		if (!room.getRoomNumFromConfig().equalsIgnoreCase(roomNum)) return false; //If sign and config room nums match

		World cWorld = room.getWorldFromConfig();
		if (cWorld == null || cWorld != w) return false; //If sign and config worlds match

		if (!room.getSignLocation().equals(s.getLocation())) return false; //If sign and config location match

		if (room.isFree() || Mes.hasPerm(p, "hotels.delete.rooms.admin")) {
			room.deleteSignFile();
			room.deleteSchematic();
			room.deleteRegion();
		} else {
			Mes.mes(p, "sign.room.breakDenied");
			return true;
		}
		return false;
	}

	public static void rentExtend(Player p, Room room) {

		if (!room.isBlockAtSignLocationSign()) {
			Mes.mes(p, "chat.commands.rent.invalidLocation");
			return;
		}

		if (room.getTime() <= 0) return;

		int extended = room.getTimesExtended();
		int max = HTConfigHandler.getconfigYML().getInt("maxRentExtend", 3);

		if (extended >= max) {
			Mes.mes(p, "chat.sign.use.maxEntendReached","%max%", String.valueOf(max));
			return;
		}

		double account = HotelsMain.economy.getBalance(p);
		double price = room.getCost();

		if (account < price) {
			double topay = price - account;
			Mes.mes(p, "chat.sign.use.notEnoughMoney", "%missingmoney%", String.valueOf(topay));
			return;
		}

		HotelsMain.economy.withdrawPlayer(p, price);
		payOwners(price, room, true);

		ProtectedRegion region = room.getRegion();

		room.setOwnerEditing(region, true);
		room.incrementTimesExtended();

		try {
			room.setNewExpiryDate();
			room.saveSignConfig();
			room.updateSign();
		} catch (IOException e) {
			Mes.mes(p, "chat.commands.somethingWentWrong");
			e.printStackTrace();
		} catch (EventCancelledException e) {
		}

		extended++;

		if (max - extended > 0)
			Mes.mes(p, "chat.sign.use.extensionSuccess",
					"%tot%", String.valueOf(extended),
					"%left%", String.valueOf(max - extended));
		else
			Mes.mes(p, "chat.sign.use.extensionSuccessNoMore",
					"%tot%", String.valueOf(extended));
	}

	public static int howManyRoomsPlayerHasRentedInHotel(String hotelName, Player player){
		World world = player.getWorld();
		Hotel hotel = new Hotel(world,hotelName);
		ArrayList<Room> rooms = hotel.getRooms();
		int rented = 0;

		for(Room room : rooms){
			OfflinePlayer renter = room.getRenter();
			if(renter!=null && room.getRenter().equals(player))
				rented++;
		}
		return rented;
	}

	public static boolean isPlayerOverRoomLimitPerHotel(String hotelName, Player player){
		int limit = HTConfigHandler.getconfigYML().getInt("maxRoomsOwnedPerHotel", 2);
		int rented = howManyRoomsPlayerHasRentedInHotel(hotelName, player);
		return rented >= limit;
	}

	public static long TimeConverter(final String immutedtime){
		if(immutedtime.equals("0")) return 0;

		final Pattern p = Pattern.compile("(\\d+)([hmd])");
		final Matcher m = p.matcher(immutedtime);

		long l = 0;
		long totalMins = 0;

		for(totalMins = 0; m.find(); totalMins += l ){
			final int duration = Integer.parseInt(m.group(1));
			final TimeUnit interval = toTimeUnit(m.group(2));
			l = interval.toMinutes(duration);
		}

		return totalMins;
	}

	public static TimeUnit toTimeUnit(@Nonnull final String c){
		switch (c){
			case "m": return TimeUnit.MINUTES;
			case "h": return TimeUnit.HOURS;
			case "d": return TimeUnit.DAYS;
			default: throw new IllegalArgumentException(String.format("%s is not a valid time code [mhd]", c));
		}
	}
	public static double CostConverter(String immutedcost){
		final Pattern p = Pattern.compile("(\\d+)([thkmb]||)");
		final Matcher m = p.matcher(immutedcost);

		double l = 0;
		double totalCost = 0;

		for(totalCost = 0; m.find(); totalCost += l){
			final double duration = Double.parseDouble(m.group(1));
			final double interval = toCost(m.group(2));
			l = interval * duration;
		}
		return totalCost;
	}

	public static double toCost(@Nonnull final String c){
		switch (c){
			case "t": return 10;
			case "h": return 100;
			case "k": return 1000;
			case "m": return 1000000;
			case "b": return 1000000000;
			case "": return 1;

			default: throw new IllegalArgumentException(String.format("%s is not a valid cost code [thkmb]", c));
		}
	}
	public static String TimeFormatter(final long input){
		if(input <= 0) return Mes.getStringNoPrefix("sign.permanent");

		//Formats time in minutes to days, hours and minutes
		long[] ftime = new long[3];
		ftime[0] = TimeUnit.MINUTES.toDays(input); //Days
		ftime[1] = TimeUnit.MINUTES.toHours(input) - TimeUnit.DAYS.toHours(ftime[0]); //Hours
		ftime[2] = input - TimeUnit.DAYS.toMinutes(ftime[0]) - TimeUnit.HOURS.toMinutes(ftime[1]); //Minutes

		String line2 = "";

		if(ftime[0] > 0)
			line2 += ftime[0] + "d";
		if(ftime[1] > 0)
			line2 += ftime[1] + "h";
		if(ftime[2] > 0)
			line2 += ftime[2] + "m";
		return line2;
	}
}
