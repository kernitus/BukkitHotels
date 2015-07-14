package kernitus.plugin.Hotels;

import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.SignManager;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class HotelsListener implements Listener {

	private HotelsMain plugin;
	public HotelsListener(HotelsMain instance){
		this.plugin = instance;
	}
	SignManager SM = new SignManager(plugin);
	WorldGuardManager WGM = new WorldGuardManager(plugin);
	HotelsConfigHandler HConH = new HotelsConfigHandler(plugin);

	//Prefix
	YamlConfiguration locale = HConH.getLocale();
	String prefix = (locale.getString("chat.prefix").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")+" ");

	@EventHandler
	public void onSignPlace(SignChangeEvent e){
		//Player places sign, checking if it's a hotel sign
		Player p = e.getPlayer();
		//If sign is a hotels sign
		if(e.getLine(0).toLowerCase().contains("[hotels]")) {
			if(p.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(p.hasPermission("hotels.sign.create")||p.hasPermission("hotels.*")))){
				//Sign lines
				String Line3 = ChatColor.stripColor(e.getLine(2)).trim();
				String Line4 = ChatColor.stripColor(e.getLine(3)).trim();

				if(Line3.isEmpty()&&Line4.isEmpty()){
					//Reception sign?
					SM.placeReceptionSign(e);
				}
				else{
					//Room sign?
					SM.placeRoomSign(e);
				}
			}
			else{
				p.sendMessage(prefix+locale.getString("chat.noPermission").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
				e.setLine(0, "§4[Hotels]");
				//No permissions
			}
		}
	}

	@EventHandler
	public void onSignUse(PlayerInteractEvent e){
		//Player right clicks sign, checking if it's a hotel sign
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN){//If block is sign
				Player p = e.getPlayer();
				//Permission check
				if(p.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(p.hasPermission("hotels.sign.use")||p.hasPermission("hotels.*")))){
					SM.useRoomSign(e,plugin);
				}
				else
					p.sendMessage(prefix+locale.getString("chat.noPermission").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
			}
		}
	}

	@EventHandler
	public void onSignBreak(BlockBreakEvent e){
		//Player broke a sign, checking if it's a hotel sign
		Block b = e.getBlock();
		if(b.getType().equals(Material.SIGN)||b.getType().equals(Material.SIGN_POST)||b.getType().equals(Material.WALL_SIGN)){
			SM.breakRoomSign(e);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		//Player joined the server, update notification to admins:
		Player p = e.getPlayer();
		if(p.hasPermission("hotel.*")||p.isOp()){
			File qfile = new File("plugins//Hotels//queuedMessages.yml");
			YamlConfiguration queue = YamlConfiguration.loadConfiguration(qfile);
			String ava = queue.getString("messages.update.available");
			String lin = queue.getString("messages.update.link");
			if(ava!=null)
				p.sendMessage(ChatColor.BLUE+ava);
			if(lin!=null)
				p.sendMessage(ChatColor.BLUE+lin);
		}
		//Notifying players if any of their rooms has expired while they were offline
		UUID playerUUID = p.getUniqueId();
		YamlConfiguration queue = HConH.getMessageQueue();
		ConfigurationSection allExpiryMessages = queue.getConfigurationSection("messages.expiry");
		for(String key:allExpiryMessages.getKeys(false)){
			UUID configUUID = UUID.fromString(queue.getString("messages.expiry."+key+".UUID"));
			if(playerUUID.equals(configUUID)){
				p.sendMessage(queue.getString("messages.expiry."+key+".message"));
				queue.set("messages.expiry."+key, null);
				HConH.saveMessageQueue(queue);
			}
		}
	}

	public int totalRooms(String hotelName,World w){
		int tot = 0;
		Map<String, ProtectedRegion> regions = new HashMap<String, ProtectedRegion>();
		regions = WGM.getWorldGuard().getRegionManager(w).getRegions();
		ProtectedRegion[] rlist = regions.values().toArray(new ProtectedRegion[regions.size()]);
		for(int i=0; i<rlist.length; i++){
			ProtectedRegion r = rlist[i];
			if(r.getId().startsWith("hotel-"+hotelName)){
				if(r.getId().matches("^hotel-"+hotelName+"-.+")){
					tot++;
				}
			}
		}
		return tot;
	}

	public int freeRooms(String hotelName,World w){
		int free = 0;
		Map<String, ProtectedRegion> regions = new HashMap<String, ProtectedRegion>();
		regions = WGM.getWorldGuard().getRegionManager(w).getRegions();
		ProtectedRegion[] rlist = regions.values().toArray(new ProtectedRegion[regions.size()]);
		for(int i=0; i<rlist.length; i++){
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

	public boolean updateReceptionSign(Location l){
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
					if(WGM.getWorldGuard().getRegionManager(b.getWorld()).hasRegion("hotel-"+hotelname)){ //Hotel region exists
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
}
