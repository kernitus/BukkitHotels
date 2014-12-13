package kernitus.plugin.Hotels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import managers.WorldGuardManager;
import me.confuser.barapi.BarAPI;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class HotelsListener implements Listener {
	public final HashMap<Player, ArrayList<Block>> hashmapPlayerName = new HashMap<Player, ArrayList<Block>>();

	public HotelsMain plugin;

	public HotelsListener(HotelsMain plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onSignPlace(SignChangeEvent e) {
		Player p = e.getPlayer();
		if(e.getLine(0).toLowerCase().contains("[hotels]")) {
			if(p.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(p.hasPermission("hotels.sign.create")||p.hasPermission("hotels.*")))){
				String Line2 = ChatColor.stripColor(e.getLine(1)).trim();
				String Line3 = ChatColor.stripColor(e.getLine(2)).trim();
				String Line4 = ChatColor.stripColor(e.getLine(3)).trim();

				File directory = new File("plugins//Hotels//Signs");
				if(!directory.exists()){
					directory.mkdir();}
				if(Line3.contains(":")){
					String[] Line3parts = Line3.split(":");
					int roomnum = Integer.parseInt(Line3parts[0]); //Room Number
					String cost = Line3parts[1]; //Cost
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
										p.sendMessage(ChatColor.DARK_RED + "Could not save sign");
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
									p.sendMessage("§4Could not save sign file");
									e1.printStackTrace();}

								Line2 = Line2.toLowerCase();
								String output = Line2.substring(0, 1).toUpperCase() + Line2.substring(1);
								e.setLine(0, ChatColor.DARK_BLUE+output); //Hotel Name
								e.setLine(1, ChatColor.DARK_GREEN+"Room " + roomnum+" - "+cost+"$"); //Room Number + Cost
								e.setLine(2,immutedtime);  //Time
								e.setLine(3,ChatColor.GREEN+"Vacant"); //Renter
								p.sendMessage(ChatColor.DARK_GREEN + "Hotel sign has been successfully created!");

							} else {
								p.sendMessage("§4The specified hotel or room does not exist!");  
								//Specified hotel does not exist
							}
						} else {
							p.sendMessage("§4Sign was not placed within hotel borders!");        		
							e.setLine(0, "§4[Hotels]");
							//Sign not in hotel borders
						}
					}else {
						p.sendMessage("§4Sign for this hotel room already exists!");
						e.setLine(0, "§4[Hotels]");
						//Sign for specified room already exists
					}
				}else{
					p.sendMessage(ChatColor.DARK_RED + "Line 3 must contain the separator §3:");    				
					e.setLine(0, "§4[Hotels]");
					//Line 3 does not contain separator
				}
			}
			else{
				p.sendMessage("§4You don't have permission!");
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
														p.sendMessage("§aYou have rented room "+roomNum+" of the "+hotelName+" hotel for "+price);
														//Successfully rented room
													}
													else{
														double topay = price-account;
														p.sendMessage("§4You do not have enough money! You need another "+topay);
													}
												}
												else
													p.sendMessage("§4You do not have an economy account!");
											}
											else
												p.sendMessage("§4This room has already been rented");
										}
										else
											p.sendMessage("§4This room does not exist!");
									}
									else
										p.sendMessage("§4Room numbers don't match!");
								}
								else
									p.sendMessage("§4Hotel names don't match!");
							}
							else
								p.sendMessage("§4Sign file doesn't exist!");
						}
						else
							p.sendMessage("§4Sign is not inside specified hotel region!");
					}
					else
						p.sendMessage("§4Hotel region doesn't exist!");
				}
				else
					p.sendMessage("§4You don't have permission!");
			}
		}
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

	//Upon login check if player was in HCM mode, if yes, display boss bar
	@EventHandler
	public void bossBarCheck(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if(plugin.getConfig().getBoolean("HCM.bossBar")==true){
			UUID playerUUID = p.getUniqueId();
			File file = new File("plugins//Hotels//Inventories//"+"Inventory-"+playerUUID+".yml");
			if(file.exists()){
				BarAPI.setMessage(p, "§2Hotel Creation Mode");
			}
		}

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
		default: throw new IllegalArgumentException(String.format("%s is not a valid code [smhd]", c));
		}
	}
}