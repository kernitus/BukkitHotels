package kernitus.plugin.Hotels;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import managers.WorldGuardManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class HotelsListener implements Listener {

	public HotelsMain plugin;

	public HotelsListener(HotelsMain plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onSignPlace(SignChangeEvent e){
		File lfile = new File("plugins//Hotels//locale.yml");
		YamlConfiguration locale = YamlConfiguration.loadConfiguration(lfile);
		Player p = e.getPlayer();
		if(e.getLine(0).toLowerCase().contains("[hotels]")) {
			if(p.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(p.hasPermission("hotels.sign.create")||p.hasPermission("hotels.*")))){
				String Line2 = ChatColor.stripColor(e.getLine(1)).trim();
				String Line3 = ChatColor.stripColor(e.getLine(2)).trim();
				String Line4 = ChatColor.stripColor(e.getLine(3)).trim();

				if(Line3.isEmpty()&&Line4.isEmpty()){
					//Reception sign?
					if(!Line2.isEmpty()){
						if ((!(Line2.isEmpty()))&&(WorldGuardManager.getWorldGuard().getRegionManager(e.getPlayer().getWorld()).hasRegion("Hotel-"+Line2))){ //Hotel region exists
							if(WorldGuardManager.getWorldGuard().getRegionManager(e.getPlayer().getWorld()).getRegion("Hotel-"+Line2).contains(e.getBlock().getX(),e.getBlock().getY(),e.getBlock().getZ())){
								//Sign is within hotel region
								int tot = totalRooms(Line2,p.getWorld());
								int free = freeRooms(Line2,p.getWorld());
								String hotelName = Line2.substring(0, 1).toUpperCase() + Line2.substring(1);
								e.setLine(0, "§a"+locale.getString("sign.reception"));
								e.setLine(1, "§1"+hotelName+" Hotel");
								e.setLine(2, "§1"+tot+" §0"+locale.getString("sign.total"));
								e.setLine(2, "§a"+free+" §0"+locale.getString("sign.free"));
								File signFile = new File("plugins//Hotels//Signs//Reception-"+Line2+"-1.yml");
								for(int i = 1; signFile.exists(); i++){
									signFile = new File("plugins//Hotels//Signs//Reception-"+Line2+"-"+i+".yml");
								}
								if(!signFile.exists()){
									try {
										signFile.createNewFile();
									} catch (IOException e1){
										p.sendMessage(locale.getString("chat.sign.place.fileFail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
										e1.printStackTrace();
									}
									new YamlConfiguration();
									YamlConfiguration config = YamlConfiguration.loadConfiguration(signFile);
									config.addDefault("Reception.hotel", hotelName);
									config.addDefault("Reception.location.world", e.getBlock().getWorld().getName());
									config.addDefault("Reception.location.x", e.getBlock().getX());
									config.addDefault("Reception.location.y", e.getBlock().getY());
									config.addDefault("Reception.location.z", e.getBlock().getZ());
									config.options().copyDefaults(true);
									try {
										config.save(signFile);
									} catch (IOException e1) {
										p.sendMessage(locale.getString("chat.sign.place.fileFail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
										e1.printStackTrace();
									}
								}		
							}
							else{
								e.setLine(0, "§4[Hotels]");
								p.sendMessage(locale.getString("chat.sign.place.outOfRegion").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
							}
						}
						else{
							e.setLine(0, "§4[Hotels]");
							p.sendMessage(locale.getString("chat.sign.place.noHotel").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						}
					}
					else{
						e.setLine(0, "§4[Hotels]");
						p.sendMessage(locale.getString("chat.sign.place.emptySign").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					}
					return;
				}
				//Room sign?
				File directory = new File("plugins//Hotels//Signs");
				if(!directory.exists()){
					directory.mkdir();}
				if(Line3.contains(":")){
					String[] Line3parts = Line3.split(":");
					int roomnum = Integer.parseInt(Line3parts[0]); //Room Number
					String roomnumb = String.valueOf(roomnum);
					String cost = Line3parts[1]; //Cost
					if((roomnumb.length()+cost.length()+9)<22){
						File signFile = new File("plugins//Hotels//Signs//"+Line2+"-"+roomnum+".yml");
						if(!signFile.exists()){ //Sign for room doesn't already exist
							if ((!(Line2.isEmpty()))&&(WorldGuardManager.getWorldGuard().getRegionManager(e.getPlayer().getWorld()).hasRegion("Hotel-"+Line2))&& //Hotel region exists
									(WorldGuardManager.getWorldGuard().getRegionManager(e.getPlayer().getWorld()).getRegion("Hotel-"+Line2).contains(e.getBlock().getX(),e.getBlock().getY(),e.getBlock().getZ()))){
								//Sign is within hotel region
								if((WorldGuardManager.getWorldGuard().getRegionManager(e.getPlayer().getWorld()).hasRegion("Hotel-"+Line2+"-"+roomnum))){ //Room region exists
									//Successful Sign
									if(!signFile.exists()){
										try {
											signFile.createNewFile();
										} catch (IOException e2){
											p.sendMessage(locale.getString("chat.sign.place.fileFail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
										}
									}

									//Creating sign config file:
									YamlConfiguration signConfig = YamlConfiguration.loadConfiguration(signFile);

									String immutedtime = Line4.trim(); //Time								
									long timeinminutes = TimeConverter(immutedtime);
									signConfig.set("Sign.time", Long.valueOf(timeinminutes));

									signConfig.set("Sign.hotel", Line2.toLowerCase());
									signConfig.set("Sign.room", roomnum);
									signConfig.set("Sign.cost", Double.valueOf(cost));

									signConfig.set("Sign.region", WorldGuardManager.getWorldGuard().getRegionManager(e.getPlayer().getWorld()).getRegion("Hotel-"+Line2+"-"+roomnum).getId().toString());
									signConfig.set("Sign.location.world", String.valueOf(e.getBlock().getWorld().getName()));
									signConfig.set("Sign.location.coords.x", Integer.valueOf(e.getBlock().getLocation().getBlockX()));
									signConfig.set("Sign.location.coords.y", Integer.valueOf(e.getBlock().getLocation().getBlockY()));
									signConfig.set("Sign.location.coords.z", Integer.valueOf(e.getBlock().getLocation().getBlockZ()));
									try {
										signConfig.save(signFile);
									} catch (IOException e1) {
										p.sendMessage(locale.getString("chat.sign.place.fileFail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
										e1.printStackTrace();}

									Line2 = Line2.toLowerCase();
									String output = Line2.substring(0, 1).toUpperCase() + Line2.substring(1);
									e.setLine(0, ChatColor.DARK_BLUE+output); //Hotel Name
									e.setLine(1, ChatColor.DARK_GREEN+"Room " + roomnum+" - "+cost+"$"); //Room Number + Cost
									e.setLine(2,immutedtime);  //Time
									e.setLine(3,ChatColor.GREEN+"Vacant"); //Renter
									p.sendMessage(locale.getString("chat.sign.place.success").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));

								} else{
									p.sendMessage(locale.getString("chat.sign.place.noRegion").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
									//Specified hotel does not exist
								}
							} else{
								p.sendMessage(locale.getString("chat.sign.place.outOfRegion").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));       		
								e.setLine(0, "§4[Hotels]");
								//Sign not in hotel borders
							}
						}else{
							p.sendMessage(locale.getString("chat.sign.place.alreadyExists").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
							e.setLine(0, "§4[Hotels]");
							//Sign for specified room already exists
						}
					}
					else{
						p.sendMessage(locale.getString("chat.sign.place.tooLong").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));				
						e.setLine(0, "§4[Hotels]");
						//Room num of price too big
					}
				}else{
					p.sendMessage(locale.getString("chat.sign.place.noSeparator").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));  				
					e.setLine(0, "§4[Hotels]");
					//Line 3 does not contain separator
				}
			}
			else{
				p.sendMessage(locale.getString("chat.noPermission").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
				e.setLine(0, "§4[Hotels]");
				//No permissions
			}
		}
	}

	@EventHandler
	public void onSignUse(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN){//If block is sign
				Player p = e.getPlayer();
				File lfile = new File("plugins//Hotels//locale.yml");
				YamlConfiguration locale = YamlConfiguration.loadConfiguration(lfile);

				//Permission check
				if(p.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(p.hasPermission("hotels.sign.use")||p.hasPermission("hotels.*")))){
					Sign s = (Sign) e.getClickedBlock().getState();
					String Line1 = ChatColor.stripColor(s.getLine(0)); //Line1
					String Line2 = ChatColor.stripColor(s.getLine(1)); //Line2
					String hotelName = ChatColor.stripColor(Line1); //Hotel name

					//If Hotel region exists
					if(WorldGuardManager.getWorldGuard().getRegionManager(p.getWorld()).hasRegion("Hotel-"+hotelName)){
						int x = e.getClickedBlock().getX();
						int y = e.getClickedBlock().getY();
						int z = e.getClickedBlock().getZ();
						//If sign is within region
						if(WorldGuardManager.getWorldGuard().getRegionManager(p.getWorld()).getRegion("Hotel-"+hotelName).contains(x, y, z)){

							String[] Line2parts = Line2.split("\\s"); //Splitting Line2 into room num + cost
							int roomNum = Integer.valueOf(Line2parts[1].trim()); //Room Number
							File signFile = new File("plugins//Hotels//Signs//"+hotelName+"-"+roomNum+".yml");

							if(signFile.exists()){
								YamlConfiguration signConfig = YamlConfiguration.loadConfiguration(signFile);
								String cHotelName = signConfig.getString("Sign.hotel");
								int cRoomNum = signConfig.getInt("Sign.room");
								if(hotelName.equalsIgnoreCase(cHotelName)){ //If hotel names match
									if(roomNum==cRoomNum){ //If room nums match
										//If region exists
										if(WorldGuardManager.getWorldGuard().getRegionManager(p.getWorld()).hasRegion(signConfig.getString("Sign.region"))){

											String cRenter = signConfig.getString("Sign.renter");
											if(cRenter==null){ //If there is a renter
												if(HotelsMain.economy.hasAccount(p)){
													double account = HotelsMain.economy.getBalance(p);
													double price = signConfig.getDouble("Sign.cost");
													if(account>=price){//If player has enough money
														HotelsMain.economy.withdrawPlayer(p, price);

														//Setting time rented at
														signConfig.set("Sign.renter", p.getUniqueId().toString());
														long currentmins = System.currentTimeMillis()/1000/60;
														signConfig.set("Sign.timeRentedAt", currentmins);

														//Setting expiry time
														long minstoexpire = signConfig.getLong("Sign.time");
														long expirydate = currentmins+minstoexpire;
														signConfig.set("Sign.expiryDate", expirydate);

														try {//Saving config file
															signConfig.save(signFile);
														} catch (IOException e1) {
															e1.printStackTrace();
														}

														//Adding renter as region member
														ProtectedRegion r = WorldGuardManager.getWorldGuard().getRegionManager(p.getWorld()).getRegion("Hotel-"+cHotelName+"-"+cRoomNum);
														WorldGuardManager.addMember(p, (ProtectedCuboidRegion) r);

														try {//Saving WG regions
															WorldGuardManager.getWorldGuard().getRegionManager(p.getWorld()).save();
														} catch (StorageException e1) {
															e1.printStackTrace();
														}

														s.setLine(3, "§c"+p.getName());//Writing renter name on sign
														s.update();
														p.sendMessage(locale.getString("chat.sign.use.success").replaceAll("%roomnum%", String.valueOf(roomNum)).replaceAll("%hotelname%", hotelName)
																.replaceAll("price", String.valueOf(price)).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
														//Successfully rented room
													}
													else{
														double topay = price-account;
														p.sendMessage(locale.getString("chat.sign.use.notEnoughMoney").replaceAll("%missingmoney%", String.valueOf(topay)).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
													}
												}
												else
													p.sendMessage(locale.getString("chat.sign.use.noAccount").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
											}
											else
												p.sendMessage(locale.getString("chat.sign.use.taken").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
										}
										else
											p.sendMessage(locale.getString("chat.sign.use.nonExistantRoom").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
									}
									else
										p.sendMessage(locale.getString("chat.sign.use.differentRoomNums").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
								}
								else
									p.sendMessage(locale.getString("chat.sign.use.differentHotelNames").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
							}
							else
								p.sendMessage(locale.getString("chat.sign.use.fileNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
						}
						else
							p.sendMessage(locale.getString("chat.sign.use.signOutOfRegion").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
					}
					/*else
						p.sendMessage("§4Hotel region doesn't exist!");
						If this is enabled any non-hotel sign in a hotel would display this error message
					 */
				}
				else
					p.sendMessage(locale.getString("chat.noPermission").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
			}
		}
	}

	@EventHandler
	public void onSignBreak(BlockBreakEvent e) {
		Block b = e.getBlock();
		if(b.getType().equals(Material.SIGN)||b.getType().equals(Material.SIGN_POST)||b.getType().equals(Material.WALL_SIGN)){
			Sign s = (Sign) b.getState();
			String Line1 = ChatColor.stripColor(s.getLine(0));
			World w = b.getWorld();
			if(WorldGuardManager.hasRegion(w, "Hotel-"+Line1)){
				//Room sign has been broken?
				if(WorldGuardManager.getRegion(w, "Hotel-"+Line1).contains(b.getX(), b.getY(), b.getZ())){
					String Line2 = ChatColor.stripColor(s.getLine(1));
					String[] Line2split = Line2.split(" ");
					int roomnum = Integer.parseInt(Line2split[1]);
					if(WorldGuardManager.hasRegion(w, "Hotel-"+Line1+"-"+roomnum)){
						File signFile = new File("plugins//Hotels//Signs//"+Line1+"-"+roomnum+".yml");
						if(signFile.exists()){
							YamlConfiguration config = YamlConfiguration.loadConfiguration(signFile);
							if(config.getString("Sign.hotel").equalsIgnoreCase(Line1)){
								if(config.getInt("Sign.room")==roomnum){
									World locw = Bukkit.getWorld(config.getString("Sign.location.world"));
									if(locw!=null){
										int locx = config.getInt("Sign.location.coords.x");
										int locy = config.getInt("Sign.location.coords.y");
										int locz = config.getInt("Sign.location.coords.z");
										int bx = b.getX();
										int by = b.getY();
										int bz = b.getZ();
										if(locx==bx&&locy==by&&locz==bz){
											signFile.delete();
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

	public static int totalRooms(String hotelName,World w){
		int tot = 0;
		Map<String, ProtectedRegion> regions = new HashMap<String, ProtectedRegion>();
		regions = WorldGuardManager.getWorldGuard().getRegionManager(w).getRegions();
		ProtectedRegion[] rlist = regions.values().toArray(new ProtectedRegion[regions.size()]);
		int i;
		for(i=0; i<rlist.length; i++){
			ProtectedRegion r = rlist[i];
			if(r.getId().startsWith("hotel-"+hotelName)){
				if(r.getId().matches("^hotel-"+hotelName+"-.+")){
					tot++;
				}
			}
		}
		return tot;
	}

	public static int freeRooms(String hotelName,World w){
		int free = 0;
		Map<String, ProtectedRegion> regions = new HashMap<String, ProtectedRegion>();
		regions = WorldGuardManager.getWorldGuard().getRegionManager(w).getRegions();
		ProtectedRegion[] rlist = regions.values().toArray(new ProtectedRegion[regions.size()]);
		int i;
		for(i=0; i<rlist.length; i++){
			ProtectedRegion r = rlist[i];
			if(r.getId().startsWith("hotel-"+hotelName)){
				if(r.getId().matches("^hotel-"+hotelName+"-.+")){
					int roomNum = Integer.parseInt(r.getId().replaceAll("^hotel-.+-", ""));
					File signFile = new File("plugins//Hotels//Signs//"+hotelName+"-"+roomNum+".yml");
					if(signFile.exists()){
						new YamlConfiguration();
						YamlConfiguration config = YamlConfiguration.loadConfiguration(signFile);
						if(config.get("Sign.renter")==null){
							free++;
						}
					}
				}
			}
		}
		return free;
	}

	public static boolean updateReceptionSign(Location l){
		Block b = l.getBlock();
		if(b.getType().equals(Material.WALL_SIGN)||b.getType().equals(Material.SIGN)||l.getBlock().getType().equals(Material.SIGN_POST)){
			Sign s = (Sign) b.getState();
			String Line1 = ChatColor.stripColor(s.getLine(0));
			String Line2 = ChatColor.stripColor(s.getLine(1));
			File lfile = new File("plugins//Hotels//locale.yml");
			YamlConfiguration locale = YamlConfiguration.loadConfiguration(lfile);
			if(Line1.equals("Reception")){ //First line is "Reception"
				if(Line2!=null){
					String[] Line2split = Line2.split(" ");
					String hotelname = Line2split[0].toLowerCase();
					if(WorldGuardManager.getWorldGuard().getRegionManager(b.getWorld()).hasRegion("hotel-"+hotelname)){ //Hotel region exists
						int tot = totalRooms(hotelname,b.getWorld());
						int free = freeRooms(hotelname,b.getWorld());
						s.setLine(2, locale.getString("chat.sign.reception.total").replaceAll("%tot%", String.valueOf(tot)).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						s.setLine(3, locale.getString("chat.sign.reception.free").replaceAll("%tot%", String.valueOf(free)).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
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

	//When a player tries to drop an item/block
	@EventHandler
	public void avoidDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		UUID playerUUID = p.getUniqueId();
		File file = new File("plugins//Hotels//Inventories//"+"Inventory-"+playerUUID+".yml");

		if(file.exists())
			e.setCancelled(true);
	}

	//When a player tries to pickup an item/block
	@EventHandler
	public void avoidPickup(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		UUID playerUUID = p.getUniqueId();
		File file = new File("plugins//Hotels//Inventories//"+"Inventory-"+playerUUID+".yml");

		if(file.exists())
			e.setCancelled(true);
	}
	public static long TimeConverter(String immutedtime)
	{
		final Pattern p = Pattern.compile("(\\d+)([hmd])");
		final Matcher m = p.matcher(immutedtime);
		long totalMins = 0;
		while (m.find())
		{
			final int duration = Integer.parseInt(m.group(1));
			final TimeUnit interval = toTimeUnit(m.group(2));
			final long l = interval.toMinutes(duration);
			totalMins = totalMins + l;
		}
		return totalMins;
	}

	public static TimeUnit toTimeUnit(@Nonnull final String c)
	{
		switch (c)
		{
		case "m": return TimeUnit.MINUTES;
		case "h": return TimeUnit.HOURS;
		case "d": return TimeUnit.DAYS;
		default: throw new IllegalArgumentException(String.format("%s is not a valid time code [smhd]", c));
		}
	}
}