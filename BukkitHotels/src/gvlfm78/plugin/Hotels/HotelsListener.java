package kernitus.plugin.Hotels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

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

import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class HotelsListener implements Listener {
	public final HashMap<Player, ArrayList<Block>> hashmapPlayerName = new HashMap<Player, ArrayList<Block>>();

	public HotelsMain plugin;

	public HotelsListener(HotelsMain plugin){
		this.plugin = plugin;
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		Player p = e.getPlayer();
		if(e.getLine(0).contains("[Hotels]")||e.getLine(0).contains("[hotels]")) {
			String Line2 = e.getLine(1);
			String Line3 = e.getLine(2);
			String Line4 = e.getLine(3);

			File directory = new File("plugins//Hotels//Signs");
			if(directory.exists()){}
			else
				directory.mkdir();

			File signFile = new File("plugins//Hotels//Signs//"+Line2+"-"+Line3+".yml");
			if(!signFile.exists()){
				if ((!(Line2.isEmpty()))&&(WorldGuardManager.getWorldGuard().getRegionManager(e.getPlayer().getWorld()).hasRegion("Hotel-"+Line2))&&(WorldGuardManager.getWorldGuard().getRegionManager(e.getPlayer().getWorld()).getRegion("Hotel-"+Line2).contains(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()))) {
					if((Integer.valueOf(Line3).equals(Integer.valueOf(Line3)))&&(WorldGuardManager.getWorldGuard().getRegionManager(e.getPlayer().getWorld()).hasRegion("Hotel-"+Line2+"-"+Line3))) {
						if(Line4.contains(":")) {
							//Successful Sign
							if(!signFile.exists()){
								try {
									signFile.createNewFile();
								} catch (IOException e2){
									p.sendMessage(ChatColor.DARK_RED + "Could not save sign");
								}
							}

							YamlConfiguration signConfig = YamlConfiguration.loadConfiguration(signFile);
							String[] parts = Line4.split(":");
							String cost = parts[0]; //Cost
							String time = parts[1]; //Time

							signConfig.set("Sign.hotel", Line2);
							signConfig.set("Sign.room", Integer.valueOf(Line3));
							signConfig.set("Sign.cost", Double.valueOf(cost));
							signConfig.set("Sign.time", Integer.valueOf(time));
							signConfig.set("Sign.region", WorldGuardManager.getWorldGuard().getRegionManager(e.getPlayer().getWorld()).getRegion("Hotel-"+Line2+"-"+Line3).getId().toString());
							signConfig.set("Sign.location.world", String.valueOf(e.getBlock().getWorld().getName()));
							signConfig.set("Sign.location.coords.x", Integer.valueOf(e.getBlock().getLocation().getBlockX()));
							signConfig.set("Sign.location.coords.y", Integer.valueOf(e.getBlock().getLocation().getBlockY()));
							signConfig.set("Sign.location.coords.z", Integer.valueOf(e.getBlock().getLocation().getBlockZ()));
							try {
								signConfig.save(signFile);
							} catch (IOException e1) {
								p.sendMessage("§4Could not save sign file");
								e1.printStackTrace();
							}
							String output = Line2.substring(0, 1).toUpperCase() + Line2.substring(1);
							e.setLine(0, "§1"+output); //Hotel Name
							e.setLine(1, "§2Room " + Line3+" - "+cost+"$"); //Room Number + Cost
							e.setLine(2,time+" mins");  //Time
							e.setLine(3,"§aVacant"); //Renter
							p.sendMessage(ChatColor.DARK_GREEN + "Hotel sign has been successfully created!");

						} else {
							p.sendMessage(ChatColor.DARK_RED + "Line 4 must contain the separator §3:");    				
							e.setLine(0, "§4[Hotels]");
						}
					} else {
						p.sendMessage(ChatColor.DARK_RED + "Line 3 must be the number of a room!");        			
						e.setLine(0, "§4[Hotels]");
					}
				} else {
					p.sendMessage(ChatColor.DARK_RED + "Sign was not placed within hotel borders");        		
					e.setLine(0, "§4[Hotels]");
				}
			}
			else{
				p.sendMessage("§4Sign for this hotel room already exists!");
				e.setLine(0, "§4[Hotels]");
				//Sign file already exists
			}
		}
	}

	@EventHandler
	public void onSignUse(PlayerInteractEvent e) {

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN) {

				Sign s = (Sign) e.getClickedBlock().getState();
				String Line1 = s.getLine(0);
				String Line2 = s.getLine(1);
				String hotelName = Line1.replace("§1", "");
				if(WorldGuardManager.getWorldGuard().getRegionManager(e.getPlayer().getWorld()).hasRegion("Hotel-"+hotelName)){
					int x = e.getClickedBlock().getX();
					int y = e.getClickedBlock().getY();
					int z = e.getClickedBlock().getZ();
					if(WorldGuardManager.getWorldGuard().getRegionManager(e.getPlayer().getWorld()).getRegion("Hotel-"+hotelName).contains(x, y, z)){

						String[] Line2parts = Line2.split("\\s");
						int roomNum = Integer.valueOf(Line2parts[1].trim()); //Room Number
						File signFile = new File("plugins//Hotels//Signs//"+hotelName+"-"+roomNum+".yml");

						if(signFile.exists()){
							YamlConfiguration signConfig = YamlConfiguration.loadConfiguration(signFile);
							String cHotelName = signConfig.getString("Sign.hotel");
							int cRoomNum = signConfig.getInt("Sign.room");
							Player p = e.getPlayer();
							if(hotelName.equalsIgnoreCase(cHotelName)){
								if(roomNum==cRoomNum){
									if(WorldGuardManager.getWorldGuard().getRegionManager(p.getWorld()).hasRegion(signConfig.getString("Sign.region"))){

										String cRenter = signConfig.getString("Sign.renter");
										if(cRenter==null){
											if(HotelsMain.economy.hasAccount(p)){
												double account = HotelsMain.economy.getBalance(p);
												double price = signConfig.getDouble("Sign.cost");
												if(account>=price){
													HotelsMain.economy.withdrawPlayer(p, price);

											signConfig.set("Sign.renter", p.getUniqueId().toString());
											signConfig.set("Sign.timeRentedAt", System.currentTimeMillis()/1000/60);
											/*int days = signConfig.getInt("Sign.time.days");
						int hours = signConfig.getInt("Sign.time.hours");
						int mins = signConfig.getInt("Sign.time.mins");

						int daysinmillis = days*24*60*60*1000;
						int hoursinmillis = hours*60*60*1000;
						int minsinmillis = mins*60*1000;

						long expirydate = System.currentTimeMillis()+daysinmillis+hoursinmillis+minsinmillis;*/

											int minutes = signConfig.getInt("Sign.time");
											int millistoexpire = minutes;
											long expirydate = System.currentTimeMillis()/1000/60+millistoexpire;


											signConfig.set("Sign.expiryDate", expirydate);

											try {
												signConfig.save(signFile);
											} catch (IOException e1) {
												e1.printStackTrace();
											}
											ProtectedRegion r = WorldGuardManager.getWorldGuard().getRegionManager(p.getWorld()).getRegion("Hotel-"+cHotelName+"-"+cRoomNum);
											WorldGuardManager.addMember(p, (ProtectedCuboidRegion) r);
											try {
												WorldGuardManager.getWorldGuard().getRegionManager(p.getWorld()).save();
											} catch (ProtectionDatabaseException e1) {
												e1.printStackTrace();
											}
											s.setLine(3, "§c"+p.getName());
											s.update();
													p.sendMessage("§aYou have rented room "+roomNum+" of the "+hotelName+" hotel for "+price);
												}
											else{
												double topay = price-account;
												p.sendMessage("§4You do not have enough money! You need another "+topay);
											}
											}
										}
										else
											p.sendMessage("§4This room has already been rented");
										}
										else
											p.sendMessage("§4You cannot rent this room");
									}
								}
							}
						}
					}
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
}