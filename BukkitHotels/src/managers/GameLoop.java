package managers;

import kernitus.plugin.Hotels.HotelsMain;
import handlers.HotelsCommandHandler;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

public class GameLoop extends BukkitRunnable {

	FilenameFilter SignFileFilter;
	HotelsMain plugin;

	public GameLoop(HotelsMain plugin) {
		this.plugin = plugin;
	}
	//Prefix
	static File lfile = new File("plugins//Hotels//locale.yml");
	static YamlConfiguration locale = YamlConfiguration.loadConfiguration(lfile);
	static String prefix = (locale.getString("chat.prefix").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")+" ");

	public GameLoop(HotelsCommandHandler hotelsCommandHandler) {}

	@Override
	public void run() {
		//int list = new File("plugins//Hotels//Signs").listFiles().length;
		File dir = new File("plugins//Hotels//Signs");
		if(!(dir.exists()))
			dir.mkdir();

		ArrayList<String> fileslist = HotelsFileFinder.listFiles("plugins//Hotels//Signs");

		for(String x: fileslist){
			File file = new File("plugins//Hotels//Signs//"+x);
			if(file.getName().matches("^"+locale.getString("sign.reception")+"-.+-.+")){
				YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
				World world = Bukkit.getWorld(config.getString("Reception.location.world").trim());
				int locx = config.getInt("Reception.location.x");
				int locy = config.getInt("Reception.location.y");
				int locz = config.getInt("Reception.location.z");
				Block b = world.getBlockAt(locx,locy,locz);
				Location l = b.getLocation();
				if(SignManager.updateReceptionSign(l)==true){//TODO There's some bug here
					file.delete();
					b.setType(Material.AIR);
					plugin.getLogger().info(prefix+locale.getString("sign.delete.reception").replaceAll("%filename%", file.getName()));
				}
			}
			else{
				YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
				String hotelName = config.getString("Sign.hotel");
				World world = Bukkit.getWorld(config.getString("Sign.location.world").trim());
				int roomNum = config.getInt("Sign.room");
				int locx = config.getInt("Sign.location.coords.x");
				int locy = config.getInt("Sign.location.coords.y");
				int locz = config.getInt("Sign.location.coords.z");
				Block signblock = world.getBlockAt(locx, locy, locz);

				if((signblock.getType().equals(Material.WALL_SIGN))||(signblock.getType().equals(Material.SIGN_POST))||(signblock.getType().equals(Material.SIGN))){ 
					Sign sign = (Sign) signblock.getState();
					if(hotelName.equalsIgnoreCase(ChatColor.stripColor(sign.getLine(0)))){
						String[] Line2parts = ChatColor.stripColor(sign.getLine(1)).split("\\s");
						int roomNumfromSign = Integer.valueOf(Line2parts[1].trim()); //Room Number
						if(roomNum==roomNumfromSign){				

							if(config.get("Sign.expiryDate")!=null){
								long expirydate = config.getLong("Sign.expiryDate");
								if(expirydate!=0){
									if(expirydate<System.currentTimeMillis()/1000/60){
										String r = config.getString("Sign.region");
										ProtectedCuboidRegion region = (ProtectedCuboidRegion) WorldGuardManager.getWorldGuard().getRegionManager(world).getRegion(r);
										if(config.getString("Sign.renter")!=null){
											Player p = Bukkit.getOfflinePlayer(UUID.fromString(config.getString("Sign.renter"))).getPlayer();
											WorldGuardManager.removeMember(p, region);
											config.set("Sign.renter", null);
											config.set("Sign.timeRentedAt", null);
											config.set("Sign.expiryDate", null);
											try {
												config.save(file);
											} catch (IOException e) {
												e.printStackTrace();
											}
											sign.setLine(3, "§a"+locale.getString("sign.vacant"));
											sign.update();
											plugin.getLogger().info(prefix+locale.getString("sign.rentExpiredConsole").replaceAll("%room%", String.valueOf(roomNum)).replaceAll("%hotel%", hotelName).replaceAll("%player%", p.getName()));
											if(p.isOnline())
												p.sendMessage(prefix+locale.getString("sign.rentExpiredPlayer").replaceAll("%room%", String.valueOf(roomNum)).replaceAll("%hotel%", hotelName).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
										}
									}
								}
							}
							else{
								config.set("Sign.renter", null);
								config.set("Sign.timeRentedAt", null);
								config.set("Sign.expiryDate", null);
								try {
									config.save(file);
								} catch (IOException e) {
									e.printStackTrace();
								}
								sign.setLine(3, "§a"+locale.getString("sign.vacant"));
								sign.update();
							}
						}
						else{
							file.delete();
							plugin.getLogger().info(prefix+locale.getString("sign.delete.roomNum").replaceAll("%filename%", file.getName()));}
					}
					else{
						file.delete();
						plugin.getLogger().info(prefix+locale.getString("sign.delete.hotelName").replaceAll("%filename%", file.getName()));}
				}
				else{
					file.delete();
					plugin.getLogger().info(prefix+locale.getString("sign.delete.location").replaceAll("%filename%", file.getName()));}
			}
		}
	}
}
