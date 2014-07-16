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
							new YamlConfiguration();
							//Successful Sign
							YamlConfiguration signConfig = YamlConfiguration.loadConfiguration(signFile);
							String[] parts = Line4.split(":");
							String cost = parts[0]; //Cost
							String time = parts[1]; //Time

							signConfig.addDefault("Sign.hotel", Line2);
							signConfig.addDefault("Sign.room", Line3);
							signConfig.addDefault("Sign.cost", cost);
							signConfig.addDefault("Sign.time", time);
							signConfig.addDefault("Sign.region", WorldGuardManager.getWorldGuard().getRegionManager(e.getPlayer().getWorld()).getRegion("Hotel-"+Line2+"-"+Line3).toString());
							signConfig.addDefault("Sign.location.world", e.getBlock().getWorld().getName());
							signConfig.addDefault("Sign.location.coords.x", e.getBlock().getLocation().getBlockX());
							signConfig.addDefault("Sign.location.coords.y", e.getBlock().getLocation().getBlockY());
							signConfig.addDefault("Sign.location.coords.z", e.getBlock().getLocation().getBlockZ());
							try {
								signConfig.save(signFile);
							} catch (IOException e1) {
								p.sendMessage("§4Could not save sign file");
								e1.printStackTrace();
							}

							e.setLine(0, "§1"+Line2); //Hotel Name
							e.setLine(1, "§2Room " + Line3); //Room Number
							e.setLine(2,cost+"$");  //Cost
							e.setLine(3,"§f"+time);      //Time
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
				String[] parts = Line2.split(" ");
				String roomNum = parts[1]; //Room Number
				File signFile = new File("plugins//Hotels//Signs//"+Line1+"-"+roomNum+".yml");

				if(signFile.exists()){
					YamlConfiguration signConfig = YamlConfiguration.loadConfiguration(signFile);
					String cHotelName = signConfig.getString("Sign.hotel");
					int cRoomNum = signConfig.getInt("Sign.room");
					Player p = e.getPlayer();
					if ((s.getLine(0).equalsIgnoreCase(cHotelName)) && (s.getLine(1).equalsIgnoreCase(Integer.toString(cRoomNum)) && (WorldGuardManager.getWorldGuard().getRegionManager(p.getWorld()).hasRegion("Hotel-"+cHotelName+"-"+cRoomNum)))) {

						signConfig.addDefault("Sign.renter", p.getUniqueId().toString());
						signConfig.addDefault("Sign.timeRentedAt", System.currentTimeMillis());
						int days = signConfig.getInt("Sign.time.days");
						int hours = signConfig.getInt("Sign.time.hours");
						int mins = signConfig.getInt("Sign.time.mins");

						int daysinmillis = days*24*60*60*1000;
						int hoursinmillis = hours*60*60*1000;
						int minsinmillis = mins*60*1000;

						long expirydate = System.currentTimeMillis()+daysinmillis+hoursinmillis+minsinmillis;


						signConfig.addDefault("Sign.expiryDate", expirydate);

						try {
							signConfig.save(signFile);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						ProtectedRegion r = WorldGuardManager.getWorldGuard().getRegionManager(p.getWorld()).getRegion("Hotel-"+cHotelName+"-"+cRoomNum);
						WorldGuardManager.addMember(p, (ProtectedCuboidRegion) r);

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