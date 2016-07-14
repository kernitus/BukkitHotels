package kernitus.plugin.Hotels.managers;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.Room;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;

public class SignManager {

	FilenameFilter SignFileFilter;
	private HotelsMain plugin;
	public SignManager(HotelsMain plugin){
		this.plugin = plugin;
	}
	HotelsFileFinder HFF = new HotelsFileFinder();
	WorldGuardManager WGM = new WorldGuardManager();
	HotelsConfigHandler HCH = new HotelsConfigHandler(plugin);
	HotelsRegionManager HRM = new HotelsRegionManager(plugin);

	public void placeReceptionSign(SignChangeEvent e){
		Player p = e.getPlayer();
		//Sign Lines
		String hotelName = ChatColor.stripColor(e.getLine(1)).trim();
		if(!hotelName.isEmpty()){
			World world = p.getWorld();
			Hotel hotel = new Hotel(world,hotelName);
			if (hotel.exists()){ //Hotel exists
				String tot = String.valueOf(hotel.getTotalRoomCount()); //Getting total amount of rooms in hotel
				String free = String.valueOf(hotel.getFreeRoomCount()); //Getting amount of free rooms in hotel

				//Setting all sign lines
				e.setLine(0, (ChatColor.GREEN + Mes.mesnopre("sign.reception")));
				e.setLine(1, (ChatColor.DARK_BLUE + hotelName + " Hotel"));
				e.setLine(2, (ChatColor.DARK_BLUE + String.valueOf(tot) + ChatColor.BLACK + " " + Mes.mesnopre("sign.room.total")));
				e.setLine(3, (ChatColor.GREEN + String.valueOf(free) + ChatColor.BLACK + " " + Mes.mesnopre("sign.room.free")));

				//Updating sign file
				File receptionFile = null;
				for(int i = 1; receptionFile.exists(); i++){
					receptionFile = HCH.getReceptionFile(hotelName,i);
				}
				if(!receptionFile.exists()){
					try {
						receptionFile.createNewFile();
					} catch (IOException e1){
						p.sendMessage(Mes.mes("chat.sign.place.fileFail"));
						e1.printStackTrace();
					}
					YamlConfiguration config = YamlConfiguration.loadConfiguration(receptionFile);
					config.addDefault("Reception.hotel", hotelName);
					config.addDefault("Reception.location.world", e.getBlock().getWorld().getName());
					config.addDefault("Reception.location.x", e.getBlock().getX());
					config.addDefault("Reception.location.y", e.getBlock().getY());
					config.addDefault("Reception.location.z", e.getBlock().getZ());
					config.options().copyDefaults(true);
					try {
						config.save(receptionFile);
					} catch (IOException e1) {
						p.sendMessage(Mes.mes("chat.sign.place.fileFail"));
						e1.printStackTrace();
					}
				}		
			}
			else{
				e.setLine(0, ("&4[Hotels]").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				p.sendMessage(Mes.mes("chat.sign.place.noHotel"));
			}
		}
		else{
			e.setLine(0, ("&4[Hotels]").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
			p.sendMessage(Mes.mes("chat.sign.place.emptySign"));
		}
		return;
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
									
									room.createSignConfig(p, timeInMins, accCost, e.getBlock().getLocation());

									e.setLine(0, ChatColor.DARK_BLUE + Line2); //Hotel Name
									e.setLine(1, ChatColor.DARK_GREEN + Mes.mesnopre("sign.room.name") + " " + roomNum + " - " + cost.toUpperCase() + "$"); //Room Number + Cost
									
									if(immutedTime.matches("0"))
										e.setLine(2,Mes.mesnopre("sign.permanent"));
									else
										e.setLine(2, TimeFormatter(timeInMins));

									e.setLine(3,ChatColor.GREEN+Mes.mesnopre("sign.vacant"));
									p.sendMessage(Mes.mes("chat.sign.place.success"));

								} else{
									p.sendMessage(Mes.mes("chat.sign.place.noRegion")); 
									//Specified hotel does not exist
								}
							} else{
								p.sendMessage(Mes.mes("chat.sign.place.outOfRegion"));       		
								e.setLine(0, ChatColor.DARK_RED+"[Hotels]");
								//Sign not in hotel borders
							}
						}else{
							p.sendMessage(Mes.mes("chat.sign.place.alreadyExists"));
							e.setLine(0, ChatColor.DARK_RED+"[Hotels]");
							//Sign for specified room already exists
						}
					}
					else{
						p.sendMessage(Mes.mes("chat.sign.place.tooLong"));				
						e.setLine(0, ChatColor.DARK_RED+"[Hotels]");
						//Room num of price too big
					}
				}else{
					p.sendMessage(Mes.mes("chat.sign.place.noSeparator"));  				
					e.setLine(0, ChatColor.DARK_RED+"[Hotels]");
					//Line 3 does not contain separator
				}
			}
			else{
				p.sendMessage(Mes.mes("chat.commands.youDoNotOwnThat"));  
				e.setCancelled(true);
			}
		}
		else{
			p.sendMessage(Mes.mes("chat.sign.place.noRegion"));  
			e.setCancelled(true);
		}
	}
	public boolean isReceptionSign(Sign s){
		return ChatColor.stripColor(s.getLine(0)).equalsIgnoreCase(Mes.mesnopre("sign.reception"));
	}
	public void useReceptionSign(PlayerInteractEvent e){
		updateReceptionSign(e.getClickedBlock().getLocation());
	}
	public void useRoomSign(PlayerInteractEvent e){
		Player p = e.getPlayer();
		World world = p.getWorld();
		//Sign lines
		Sign s = (Sign) e.getClickedBlock().getState();
		String Line1 = ChatColor.stripColor(s.getLine(0)); //Line1
		String Line2 = ChatColor.stripColor(s.getLine(1)); //Line2
		String hotelName = (ChatColor.stripColor(Line1)); //Hotel name
		
		Hotel hotel = new Hotel(world,hotelName);

		//If Hotel exists
		if(hotel.exists()){
			int x = e.getClickedBlock().getX();
			int y = e.getClickedBlock().getY();
			int z = e.getClickedBlock().getZ();
			//If sign is within region
			if(hotel.getRegion().contains(x, y, z)){

				String[] Line2parts = Line2.split("\\s"); //Splitting Line2 into room num + cost
				int roomNum = Integer.valueOf(Line2parts[1]); //Room Number
				Room room = new Room(hotel,roomNum);

				if(room.doesSignFileExist()){

					String configHotelName = room.sconfig.getString("Sign.hotel");
					int configRoomNum = room.sconfig.getInt("Sign.room");
					if(hotelName.equalsIgnoreCase(configHotelName)){ //If hotel names match
						if(roomNum==configRoomNum)//If room nums match
							rentRoom(p,hotelName,roomNum);
						else
							p.sendMessage(Mes.mes("chat.sign.use.differentRoomNums"));
					}
					else
						p.sendMessage(Mes.mes("chat.sign.use.differentHotelNames")); 
				}
				else
					p.sendMessage(Mes.mes("chat.sign.use.fileNonExistant")); 
			}
			else
				p.sendMessage(Mes.mes("chat.sign.use.signOutOfRegion")); 
		}
	}
	public void rentRoom(Player p, String hotelName, int roomNum){

		//If region exists
		World world = p.getWorld();
		Hotel hotel = new Hotel(world,hotelName);
		Room room = new Room(hotel,roomNum);
		
		if(room.exists()){
			OfflinePlayer renter = room.getRenter();
			if(room.isFree()){
				if(!isPlayerOverRoomLimitPerHotel(hotelName, p)){//If player is under per-hotel limit
					if(HotelsMain.economy.hasAccount(p)){
						double account = HotelsMain.economy.getBalance(p);
						double price = room.getCost();
						if(account>=price){//If player has enough money
							//If player is under max owned rooms limit
							if(getTimesRented(p.getUniqueId())<plugin.getConfig().getInt("settings.max_rooms_owned")){
								
								//Renter has passed all conditions and is able to rent this room
								HotelsMain.economy.withdrawPlayer(p, price);//Taking money from renter
								payOwners(price,room,false); //Pay the hotel owners the net profit

								ProtectedRegion region = room.getRegion();
								
								room.rent(p);
								room.saveSignConfig();

								if(plugin.getConfig().getBoolean("settings.stopOwnersEditingRentedRooms")){
									region.setPriority(10);
									region.setFlag(DefaultFlag.BLOCK_BREAK, State.DENY);
									region.setFlag(DefaultFlag.BLOCK_PLACE, State.DENY);
								}

								WGM.saveRegions(world);//Saving WG regions

								Location loc = room.getSignLocation();
								Block block = loc.getBlock();
								if(block.getType()==Material.SIGN||block.getType()==Material.WALL_SIGN||block.getType()==Material.SIGN_POST){
									Sign s = (Sign) block.getState();
									s.setLine(3, ChatColor.RED + p.getName()); //Writing renter name on sign
									s.update();
									DecimalFormat df = new DecimalFormat("#.00");
									p.sendMessage(Mes.mes("chat.sign.use.success").replaceAll("%room%", String.valueOf(roomNum)).replaceAll("%hotel%", hotelName)
											.replaceAll("%price%", df.format(price)));
									//Successfully rented room
								}
								else
									p.sendMessage(Mes.mes("chat.commands.rent.invalidLocation")); 
							}
							else
								p.sendMessage(Mes.mes("chat.sign.use.maxRoomsReached").replaceAll("%max%", String.valueOf(plugin.getConfig().getInt("settings.max_rooms_owned"))));
						}
						else{
							double topay = price-account;
							p.sendMessage(Mes.mes("chat.sign.use.notEnoughMoney").replaceAll("%missingmoney%", String.valueOf(topay))); 
						}
					}
					else
						p.sendMessage(Mes.mes("chat.sign.use.noAccount"));
				}
				else
					p.sendMessage(Mes.mes("chat.sign.use.overRoomsPerHotelLimit").replaceAll("%limit%", plugin.getConfig().getString("settings.max_rooms_owned_per_hotel")));
			}
			else if(renter.equals(p)){
				//Renter is same player that right clicked
				rentExtend(p,room);
			}
			else
				p.sendMessage(Mes.mes("chat.sign.use.taken")); 
		}
		else
			p.sendMessage(Mes.mes("chat.sign.use.nonExistantRoom")); 
	}
	public void payOwners(double price, Room room, boolean isRentExtend){
		Hotel hotel = room.getHotel();
		String tax = plugin.getConfig().getString("settings.tax");
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
				player.sendMessage(Mes.mes(chatMessage)
						.replaceAll("%revenue%",  new DecimalFormat("#.00").format(revenue))
						.replaceAll("%hotel%", hotel.getName())
						.replaceAll("%room%", String.valueOf(room.getNum()))
						);
			}
			else{//Placing message in message queue
				YamlConfiguration queue = HCH.getMessageQueue();
				if(!queue.contains("messages.revenue")){
					queue.createSection("messages.revenue");
					HCH.saveMessageQueue(queue);
				}
				Set<String> revenueMessages = queue.getConfigurationSection("messages.revenue").getKeys(false);
				int expiryMessagesSize = revenueMessages.size();
				String pathToPlace = "messages.revenue."+(expiryMessagesSize+1);
				queue.set(pathToPlace+".UUID", owner.getUniqueId().toString());
				queue.set(pathToPlace+".message", Mes.mes(chatMessage)
						.replaceAll("%revenue%", new DecimalFormat("#.00").format(revenue))
						.replaceAll("%room%", String.valueOf(room.getNum()))
						.replaceAll("%hotel%", hotel.getName())
						);
				HCH.saveMessageQueue(queue);
			}
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
											WGM.removeRegion(w,roomRegion.getId());
											room.deleteSignFile();
										}
										else{
											p.sendMessage(Mes.mes("sign.room.breakDenied"));
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

	public int getTimesRented(UUID ptocheck){
		File dir = HCH.getFile("Signs");
		if(!(dir.exists()))
			dir.mkdir();

		ArrayList<String> filesList = HFF.listFiles("plugins"+File.separator+"Hotels"+File.separator+"Signs");
		int rents = 0;
		for(String x: filesList){
			File file = HCH.getFile("Signs"+File.separator+x);
			if(!file.getName().matches("^"+Mes.mesnopre("sign.reception")+"-.+-.+")){
				//Not a reception sign, therefore a room sign
				YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
				String renterUUIDfromConfigString = config.getString("Sign.renter");
				if(renterUUIDfromConfigString!=null){
					UUID renterUUIDfromConfig = UUID.fromString(renterUUIDfromConfigString);
					if(ptocheck.equals(renterUUIDfromConfig))
						rents++;	
				}
				//There is no renter
			}
		}
		return rents;
	}

	public void rentExtend(Player p, Room room){

		Location loc = room.getSignLocation();
		Block block = loc.getBlock();
		
		if(block.getType()==Material.SIGN||block.getType()==Material.WALL_SIGN||block.getType()==Material.SIGN_POST){
			Sign s = (Sign) block.getState();

			if(room.getTime()>0){
				int extended = room.getTimesExtended();
				int max = plugin.getConfig().getInt("settings.max_rent_extend");
				
				if(extended < max){
					double account = HotelsMain.economy.getBalance(p);
					double price = room.getCost();
					if(account >= price){//If player has enough money
						HotelsMain.economy.withdrawPlayer(p, price);
						payOwners(price, room, true);

						ProtectedRegion region = room.getRegion();

						if(plugin.getConfig().getBoolean("settings.stopOwnersEditingRentedRooms")){

							region.setFlag(DefaultFlag.BLOCK_BREAK, State.DENY);
							region.setFlag(DefaultFlag.BLOCK_PLACE, State.DENY);
						}

						room.incrementTimesExtended();
						room.saveSignConfig();

						room.setNewExpiryDate();
						room.saveSignConfig();
						
						s.setLine(2, TimeFormatter(room.getExpiryMinute()-(System.currentTimeMillis()/1000/60)));
						s.update();
						extended+=1;
						if(max-extended>0)
							p.sendMessage(Mes.mes("chat.sign.use.extensionSuccess").replaceAll("%tot%", String.valueOf(extended)).replaceAll("%left%", String.valueOf(max-extended)));
						else
							p.sendMessage(Mes.mes("chat.sign.use.extensionSuccessNoMore").replaceAll("%tot%", String.valueOf(extended)));
					}
					else{
						double topay = price-account;
						p.sendMessage(Mes.mes("chat.sign.use.notEnoughMoney").replaceAll("%missingmoney%", String.valueOf(topay)));
					}
				}
				else
					p.sendMessage(Mes.mes("chat.sign.use.maxEntendReached").replaceAll("%max%", String.valueOf(max)));
			}
		}
		else
			p.sendMessage(Mes.mes("chat.commands.rent.invalidLocation")); 
	}

	public int howManyRoomsPlayerHasRentedInHotel(String hotelName, Player player){
		World world = player.getWorld();
		Hotel hotel = new Hotel(world,hotelName);
		ArrayList<Room> rooms = hotel.getRooms();
		int rented = 0;

		for(Room room : rooms){
			if(room.getRenter().equals(player))
				rented++;
		}
		return rented;
	}

	public boolean isPlayerOverRoomLimitPerHotel(String hotelName, Player player){
		int limit = plugin.getConfig().getInt("settings.max_rooms_owned_per_hotel");
		int rented = howManyRoomsPlayerHasRentedInHotel(hotelName,player);
		if(rented>=limit)
			return true;
		return false;
	}

	public long getRemainingTime(String hotelName, String roomNum){
		File file = HCH.getFile("Signs"+File.separator+hotelName+"-"+roomNum+".yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		long expiryDate = config.getLong("Sign.expiryDate");
		long currentMins = System.currentTimeMillis()/1000/60;
		return expiryDate-currentMins;
	}

	public boolean updateReceptionSign(Location l){
		//Updates the reception sign at given location
		Block b = l.getBlock();
		if(b.getType().equals(Material.WALL_SIGN)||b.getType().equals(Material.SIGN)||l.getBlock().getType().equals(Material.SIGN_POST)){
			Sign s = (Sign) b.getState();
			String Line1 = ChatColor.stripColor(s.getLine(0));
			String Line2 = ChatColor.stripColor(s.getLine(1));
			if(Line1.equals(Mes.mesnopre("sign.reception"))){ //First line is "Reception"
				if(Line2!=null){
					String[] Line2split = Line2.split(" ");
					String hotelName = Line2split[0];
					Hotel hotel = new Hotel(b.getWorld(),hotelName);
					if(hotel.exists()){ //Hotel region exists
						int tot = hotel.getTotalRoomCount();
						int free = hotel.getFreeRoomCount();
						s.setLine(2, (ChatColor.DARK_BLUE+String.valueOf(tot)+ChatColor.BLACK+" "+Mes.mesnopre("sign.room.total")));
						s.setLine(3, (ChatColor.GREEN+String.valueOf(free)+ChatColor.BLACK+" "+Mes.mesnopre("sign.room.free")));
						s.update();
						return false;
					}
					return true;
				}
				return true;
			}
			return true;
		}
		return true;
	}
	public long TimeConverter(String immutedtime){
		final Pattern p = Pattern.compile("(\\d+)([hmd])");
		final Matcher m = p.matcher(immutedtime);
		long totalMins = 0;
		
		while (m.find()){
			final int duration = Integer.parseInt(m.group(1));
			final TimeUnit interval = toTimeUnit(m.group(2));
			final long l = interval.toMinutes(duration);
			totalMins+=l;
		}
		return totalMins;
	}

	public TimeUnit toTimeUnit(@Nonnull final String c){
		switch (c){
		case "m": return TimeUnit.MINUTES;
		case "h": return TimeUnit.HOURS;
		case "d": return TimeUnit.DAYS;
		default: throw new IllegalArgumentException(String.format("%s is not a valid time code [mhd]", c));
		}
	}
	public double CostConverter(String immutedcost){
		
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

	public double toCost(@Nonnull final String c){
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
	public String TimeFormatter(long input){
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
			return Mes.mesnopre("sign.permanent");
	}
}
