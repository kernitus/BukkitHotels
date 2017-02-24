package kernitus.plugin.Hotels.managers;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.Hotel;
import kernitus.plugin.Hotels.HotelsAPI;
import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.Room;
import kernitus.plugin.Hotels.exceptions.EventCancelledException;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.handlers.HotelsMessageQueue;
import kernitus.plugin.Hotels.handlers.MessageType;

public class SignManager {

	private HotelsMain plugin;

	public SignManager(HotelsMain plugin){
		this.plugin = plugin;
	}

	public void placeReceptionSign(SignChangeEvent e){
		Player p = e.getPlayer();
		//Sign Lines
		String hotelName = ChatColor.stripColor(e.getLine(1)).trim();
		if(hotelName.isEmpty()){
			e.setLine(0, (ChatColor.DARK_RED+"[Hotels]"));
			Mes.mes(p, "chat.sign.place.emptySign");
			return;
		}
		World world = p.getWorld();
		Hotel hotel = new Hotel(world,hotelName);
		if (!hotel.exists()){ //Hotel exists
			e.setLine(0, (ChatColor.DARK_RED+"[Hotels]"));
			Mes.mes(p, "chat.sign.place.noHotel");
			return;
		}

		String tot = String.valueOf(hotel.getTotalRoomCount()); //Getting total amount of rooms in hotel
		String free = String.valueOf(hotel.getFreeRoomCount()); //Getting amount of free rooms in hotel

		//Setting all sign lines
		e.setLine(0, (ChatColor.GREEN + Mes.getStringNoPrefix("sign.reception")));
		e.setLine(1, (ChatColor.DARK_BLUE + hotelName + " Hotel"));
		e.setLine(2, (ChatColor.DARK_BLUE + String.valueOf(tot) + ChatColor.BLACK + " " + Mes.getStringNoPrefix("sign.room.total")));
		e.setLine(3, (ChatColor.GREEN + String.valueOf(free) + ChatColor.BLACK + " " + Mes.getStringNoPrefix("sign.room.free")));

		int i = 1;
		File receptionFile = HotelsConfigHandler.getReceptionFile(hotelName, i);
		while(receptionFile.exists()){
			i++;
			receptionFile = HotelsConfigHandler.getReceptionFile(hotelName, i);
		}//Loop will stop once a non-existant file is found

		try {
			receptionFile.createNewFile();
		} catch (IOException e1){
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
			return;
		}
	}

	public void placeRoomSign(SignChangeEvent e){
		Player p = e.getPlayer();
		World world = p.getWorld();
		//Sign Lines
		String Line2 = ChatColor.stripColor(e.getLine(1)).trim();
		String Line3 = ChatColor.stripColor(e.getLine(2)).trim();
		String Line4 = ChatColor.stripColor(e.getLine(3)).trim();

		Hotel hotel = new Hotel(world,Line2);

		if(hotel.exists()){
			if(hotel.isOwner(p.getName())||hotel.isOwner(p.getUniqueId())||Mes.hasPerm(p, "hotels.sign.create.admin")){

				if(Line3.contains(":")){
					String[] Line3parts = Line3.split(":");
					int roomNum = Integer.parseInt(Line3parts[0]); //Room Number
					String roomnumb = String.valueOf(roomNum);
					String cost = Line3parts[1]; //Cost
					if((roomnumb.length()+cost.length()+9)<22){
						Room room = new Room(hotel,roomNum);

						if(!room.doesSignFileExist()){ //Sign for room doesn't already exist
							if(hotel.getRegion().contains(e.getBlock().getX(),e.getBlock().getY(),e.getBlock().getZ())){
								//Sign is within hotel region
								if(room.exists()){ //Room region exists

									//Successful Sign

									//Creating sign config file:

									String immutedTime = Line4.trim(); //Time
									long timeInMins = immutedTime.equals(0) ? 0 : TimeConverter(immutedTime);

									//Calculating accurate cost
									double accCost = CostConverter(cost);									

									try {
										room.createSignConfig(p, timeInMins, accCost, e.getBlock().getLocation());
									} catch (IOException e1) {
										Mes.mes(p, "chat.sign.place.fileFail");
										e.setLine(0, ChatColor.DARK_RED+"[Hotels]");
										e1.printStackTrace();
										return;
									}

									e.setLine(0, ChatColor.DARK_BLUE + Line2); //Hotel Name
									e.setLine(1, ChatColor.DARK_GREEN + Mes.getStringNoPrefix("sign.room.name") + " " + roomNum + " - " + cost.toUpperCase() + "$"); //Room Number + Cost

									if(immutedTime.matches("0"))
										e.setLine(2,Mes.getStringNoPrefix("sign.permanent"));
									else
										e.setLine(2, TimeFormatter(timeInMins));

									e.setLine(3,ChatColor.GREEN+Mes.getStringNoPrefix("sign.vacant"));
									Mes.mes(p, "chat.sign.place.success");

								} else{
									Mes.mes(p, "chat.sign.place.noRegion"); 
									//Specified hotel does not exist
								}
							} else{
								Mes.mes(p, "chat.sign.place.outOfRegion");       		
								e.setLine(0, ChatColor.DARK_RED+"[Hotels]");
								//Sign not in hotel borders
							}
						}else{
							Mes.mes(p, "chat.sign.place.alreadyExists");
							e.setLine(0, ChatColor.DARK_RED+"[Hotels]");
							//Sign for specified room already exists
						}
					}
					else{
						Mes.mes(p, "chat.sign.place.tooLong");				
						e.setLine(0, ChatColor.DARK_RED+"[Hotels]");
						//Room num of price too big
					}
				}else{
					Mes.mes(p, "chat.sign.place.noSeparator");  				
					e.setLine(0, ChatColor.DARK_RED+"[Hotels]");
					//Line 3 does not contain separator
				}
			}
			else{
				Mes.mes(p, "chat.commands.youDoNotOwnThat");  
				e.setCancelled(true);
			}
		}
		else{
			Mes.mes(p, "chat.sign.place.noRegion");  
			e.setCancelled(true);
		}
	}
	public boolean isReceptionSign(Sign s){
		return ChatColor.stripColor(s.getLine(0)).equalsIgnoreCase(Mes.getStringNoPrefix("sign.reception"));
	}
	public void useRoomSign(PlayerInteractEvent e){
		Player p = e.getPlayer();
		World world = p.getWorld();
		//Sign lines
		Sign s = (Sign) e.getClickedBlock().getState();
		String Line1 = ChatColor.stripColor(s.getLine(0)); //Line1
		String Line2 = ChatColor.stripColor(s.getLine(1)); //Line2
		String hotelName = (ChatColor.stripColor(Line1)); //Hotel name

		Hotel hotel = new Hotel(world, hotelName);

		//If Hotel exists
		if(hotel.exists()){
			int x = e.getClickedBlock().getX();
			int y = e.getClickedBlock().getY();
			int z = e.getClickedBlock().getZ();
			//If sign is within region
			if(hotel.getRegion().contains(x, y, z)){

				String[] Line2parts = Line2.split("\\s"); //Splitting Line2 into room num + cost
				int roomNum = Integer.valueOf(Line2parts[1]); //Room Number
				Room room = new Room(hotel, roomNum);

				if(room.doesSignFileExist()){

					if(hotelName.equalsIgnoreCase(room.getHotelNameFromConfig())){ //If hotel names match
						if(roomNum==room.getRoomNumFromConfig())//If room nums match
							rentRoom(p, room); //This will also check if rent should be extended and not new
						else
							Mes.mes(p, "chat.sign.use.differentRoomNums");
					}
					else
						Mes.mes(p, "chat.sign.use.differentHotelNames"); 
				}
				else
					Mes.mes(p, "chat.sign.use.fileNonExistent"); 
			}
			else
				Mes.mes(p, "chat.sign.use.signOutOfRegion"); 
		}
	}
	public void rentRoom(Player p, Room room){

		String hotelName = room.getHotel().getName();

		if(!room.exists()){ Mes.mes(p, "chat.sign.use.nonExistentRoom"); return; }

		OfflinePlayer renter = room.getRenter();

		if(room.isFree()){
			if(!isPlayerOverRoomLimitPerHotel(hotelName, p)){//If player is under per-hotel limit
				double account = HotelsMain.economy.getBalance(p);
				double price = room.getCost();
				if(account>=price){//If player has enough money
					//If player is under max owned rooms limit
					if(HotelsAPI.getRoomsRentedBy(p.getUniqueId()).size() < plugin.getConfig().getInt("max_rooms_owned")){
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
						p.sendMessage(Mes.getString("chat.sign.use.success").replaceAll("%room%", String.valueOf(room.getNum())).replaceAll("%hotel%", hotelName)
								.replaceAll("%price%", df.format(price)));
						//Successfully rented room
					}
					else
						p.sendMessage(Mes.getString("chat.sign.use.maxRoomsReached").replaceAll("%max%", String.valueOf(plugin.getConfig().getInt("max_rooms_owned"))));
				}
				else{
					double topay = price-account;
					p.sendMessage(Mes.getString("chat.sign.use.notEnoughMoney").replaceAll("%missingmoney%", String.valueOf(topay))); 
				}
			}
			else
				p.sendMessage(Mes.getString("chat.sign.use.overRoomsPerHotelLimit").replaceAll("%limit%", plugin.getConfig().getString("max_rooms_owned_per_hotel")));
		}
		else if(renter.getUniqueId().equals(p.getUniqueId()))
			//Renter is same player that right clicked
			rentExtend(p, room);
		else
			Mes.mes(p, "chat.sign.use.taken"); 
	}
	public void payOwners(double price, Room room, boolean isRentExtend){
		Hotel hotel = room.getHotel();
		String tax = plugin.getConfig().getString("tax");
		double revenue = price;
		double taxValue;
		if(tax.matches("\\d+%")){//If it's a percentage
			tax = tax.replace("%", "");
			try{
				taxValue = Double.parseDouble(tax);
			}
			catch(NumberFormatException e){
				//Tax value is invalid, assuming it is 0%
				taxValue = 0;
			}
			if(taxValue>=0&&taxValue<=100){
				revenue = revenue-(revenue*(taxValue/100));
			}
		}
		else if(tax.matches("\\d+")){//If it's a set amount
			try{
				taxValue = Double.parseDouble(tax);
			}
			catch(NumberFormatException e){
				//Tax value is invalid, assuming it is 0%
				taxValue = 0;
			}
			revenue = revenue - taxValue;
		}
		//Giving to all owners the revenue
		Set<String> hotelOwners = hotel.getOwners().getPlayers();
		for(String ownerName : hotelOwners){
			@SuppressWarnings("deprecation")
			OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerName);
			HotelsMain.economy.depositPlayer(owner, revenue);
			String chatMessage;
			if(isRentExtend==true)
				chatMessage = "chat.moneyEarnedExtend";
			else
				chatMessage = "chat.moneyEarned";
			if(revenue<0)
				revenue = 0;

			if(owner.isOnline()){//Telling player they earned some munniez
				Player player = (Player) owner;
				player.sendMessage(Mes.getString(chatMessage)
						.replaceAll("%revenue%",  new DecimalFormat("#.00").format(revenue))
						.replaceAll("%hotel%", hotel.getName())
						.replaceAll("%room%", String.valueOf(room.getNum()))
						);
			}
			else//Placing message in message queue
				HotelsMessageQueue.addMessage(MessageType.revenue, owner.getUniqueId(), Mes.getString(chatMessage).replaceAll("%revenue%", new DecimalFormat("#.00").format(revenue)).replaceAll("%room%", String.valueOf(room.getNum())).replaceAll("%hotel%", hotel.getName()));
		}
	}
	public void breakRoomSign(BlockBreakEvent e){
		Block b = e.getBlock();
		Sign s = (Sign) b.getState();
		String Line1 = (ChatColor.stripColor(s.getLine(0)));
		World w = b.getWorld();
		Player p = e.getPlayer();

		Hotel hotel = new Hotel(w,"hotel-"+Line1);

		if(hotel.exists()){
			//Room sign has been broken?
			if(hotel.getRegion().contains(b.getX(), b.getY(), b.getZ())){//If sign is in hotel
				String Line2 = ChatColor.stripColor(s.getLine(1));
				String[] Line2split = Line2.split(" ");
				int roomNum = Integer.parseInt(Line2split[1]);

				Room room = new Room(hotel,roomNum);

				if(room.exists()){//If room exists
					ProtectedRegion roomRegion = room.getRegion();

					if(room.doesSignFileExist()){//If signfile is present

						if(room.getHotelNameFromConfig().equalsIgnoreCase(Line1)){//If sign and config hotel names match
							if(room.getRoomNumFromConfig()==roomNum){//If sign and config room nums match
								World cWorld = room.getWorldFromConfig();
								if(cWorld!=null && cWorld==w){//If sign and config worlds match
									if(room.getSignLocation().equals(b.getLocation())){//If sign and config location match
										if(room.isFree() || Mes.hasPerm(p, "hotels.delete.rooms.admin")){
											WorldGuardManager.removeRegion(w,roomRegion.getId());
											room.deleteSignFile();
										}
										else{
											Mes.mes(p, "sign.room.breakDenied");
											e.setCancelled(true);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void rentExtend(Player p, Room room){

		if(!room.isBlockAtSignLocationSign()){ Mes.mes(p, "chat.commands.rent.invalidLocation"); return; }

		if(room.getTime()<=0) return;

		int extended = room.getTimesExtended();
		int max = plugin.getConfig().getInt("max_rent_extend");

		if(extended >= max){ Mes.getString("chat.sign.use.maxEntendReached").replaceAll("%max%", String.valueOf(max)); }

		double account = HotelsMain.economy.getBalance(p);
		double price = room.getCost();

		if(account >= price){//If player has enough money
			HotelsMain.economy.withdrawPlayer(p, price);
			payOwners(price, room, true);

			ProtectedRegion region = room.getRegion();

			if(plugin.getConfig().getBoolean("stopOwnersEditingRentedRooms")){

				region.setFlag(DefaultFlag.BLOCK_BREAK, State.DENY);
				region.setFlag(DefaultFlag.BLOCK_PLACE, State.DENY);
			}

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

			if(max-extended>0)
				p.sendMessage(Mes.getString("chat.sign.use.extensionSuccess").replaceAll("%tot%", String.valueOf(extended)).replaceAll("%left%", String.valueOf(max-extended)));
			else
				p.sendMessage(Mes.getString("chat.sign.use.extensionSuccessNoMore").replaceAll("%tot%", String.valueOf(extended)));
		}
		else{
			double topay = price-account;
			p.sendMessage(Mes.getString("chat.sign.use.notEnoughMoney").replaceAll("%missingmoney%", String.valueOf(topay)));
		}
	}

	public int howManyRoomsPlayerHasRentedInHotel(String hotelName, Player player){
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

	public boolean isPlayerOverRoomLimitPerHotel(String hotelName, Player player){
		int limit = plugin.getConfig().getInt("max_rooms_owned_per_hotel");
		int rented = howManyRoomsPlayerHasRentedInHotel(hotelName,player);
		return rented>=limit;
	}

	public long getRemainingTime(String hotelName, String roomNum){
		File file = HotelsConfigHandler.getFile("Signs"+File.separator+hotelName+"-"+roomNum+".yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		long expiryDate = config.getLong("Sign.expiryDate");
		long currentMins = System.currentTimeMillis()/1000/60;
		return expiryDate-currentMins;
	}
	public static long TimeConverter(String immutedtime){
		final Pattern p = Pattern.compile("(\\d+)([hmd])");
		final Matcher m = p.matcher(immutedtime);
		long totalMins = 0;

		while (m.find()){
			final int duration = Integer.parseInt(m.group(1));
			final TimeUnit interval = toTimeUnit(m.group(2));
			final long l = interval.toMinutes(duration);
			totalMins += l;
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
		double totalCost = 0;
		while (m.find()){
			final double duration = Double.parseDouble(m.group(1));
			final double interval = toCost(m.group(2));
			final double l = interval*duration;
			totalCost += l;
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
	public static String TimeFormatter(long input){
		if(input>0){
			//Formats time in minutes to days, hours and minutes
			long[] ftime = new long[3];
			ftime[0] = TimeUnit.MINUTES.toDays(input); //Days
			ftime[1] = TimeUnit.MINUTES.toHours(input) - TimeUnit.DAYS.toHours(ftime[0]); //Hours
			ftime[2] = input - TimeUnit.DAYS.toMinutes(ftime[0]) - TimeUnit.HOURS.toMinutes(ftime[1]); //Minutes
			String line2 = "";
			if(ftime[0]>0)
				line2 = line2+ftime[0]+"d";
			if(ftime[1]>0)
				line2 = line2+ftime[1]+"h";
			if(ftime[2]>0)
				line2 = line2+ftime[2]+"m";
			return line2;
		}
		else
			return Mes.getStringNoPrefix("sign.permanent");
	}
}
