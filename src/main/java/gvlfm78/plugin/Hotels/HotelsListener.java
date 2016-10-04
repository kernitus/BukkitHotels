package kernitus.plugin.Hotels;

import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.SignManager;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

public class HotelsListener implements Listener {

	private HotelsMain plugin;
	public HotelsListener(HotelsMain plugin){
		this.plugin = plugin;
	}
	HotelsCreationMode HCM = new HotelsCreationMode(plugin);
	SignManager SM = new SignManager(plugin);
	WorldGuardManager WGM = new WorldGuardManager();
	HotelsConfigHandler HConH = new HotelsConfigHandler(plugin);

	@EventHandler
	public void onSignPlace(SignChangeEvent e){
		//Player places sign, checking if it's a hotel sign
		Player p = e.getPlayer();
		//If sign is a hotels sign
		if(e.getLine(0).toLowerCase().contains("[hotels]")) {
			if(Mes.hasPerm(p,"hotels.sign.create")){
				//Sign lines
				String Line3 = ChatColor.stripColor(e.getLine(2)).trim();
				String Line4 = ChatColor.stripColor(e.getLine(3)).trim();

				if(Line3.isEmpty() && Line4.isEmpty()) //Reception sign?
					SM.placeReceptionSign(e);
				else //Room sign?
					SM.placeRoomSign(e);
			}
			else{
				//No permission
				p.sendMessage(Mes.mes("chat.noPermission")); 
				e.setLine(0, ChatColor.DARK_RED + e.getLine(0));
			}
		}
	}

	@EventHandler
	public void onSignUse(PlayerInteractEvent e){
		//Player right clicks sign, checking if it's a hotel sign
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) { //They Right clicked
			if (e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN){//If block is sign
				Player p = e.getPlayer();
				//Permission check
				if(Mes.hasPerm(p, "hotels.sign.use")){
					Sign s = (Sign) e.getClickedBlock().getState();//Getting sign object
					if(SM.isReceptionSign(s))//If it's a reception sign
						SM.useReceptionSign(e);//Update the reception sign, as they right clicked on it
					else
						SM.useRoomSign(e);
				}
				else
					p.sendMessage(Mes.mes("chat.noPermission")); 
			}
		}
	}

	@EventHandler
	public void onSignBreak(BlockBreakEvent e){
		//Player broke a sign, checking if it's a hotel sign
		Block b = e.getBlock();
		if(b.getType().equals(Material.SIGN) || b.getType().equals(Material.SIGN_POST) || b.getType().equals(Material.WALL_SIGN))
			SM.breakRoomSign(e);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		//Player joined the server, send update notification to ops
		Player p = e.getPlayer();
		if(p.hasPermission("hotel.*")){
			String ava = HConH.getupdateAvailable();
			String lin = HConH.getupdateString();
			if(ava != null)
				p.sendMessage(ChatColor.BLUE + ava);
			if(lin != null)
				p.sendMessage(ChatColor.BLUE + lin);
		}
		//Notifying players if any of their rooms have expired while they were offline
		UUID playerUUID = p.getUniqueId();
		YamlConfiguration queue = HConH.getMessageQueue();
		ConfigurationSection allExpiryMessages = queue.getConfigurationSection("messages.expiry");
		if(allExpiryMessages != null){
			Set<String> keys = allExpiryMessages.getKeys(false);
			if(keys != null){
				for(String key : keys){
					UUID configUUID = UUID.fromString(queue.getString("messages.expiry." + key + ".UUID"));
					if(playerUUID.equals(configUUID)){
						p.sendMessage(queue.getString("messages.expiry." + key + ".message"));
						queue.set("messages.expiry." + key, null);
						HConH.saveMessageQueue(queue);
					}
				}
			}
		}
		//Notifying hotel owners of any revenue they made while offline
		ConfigurationSection allRevenueMessages = queue.getConfigurationSection("messages.revenue");
		if(allRevenueMessages != null){
			Set<String> keys = allRevenueMessages.getKeys(false);
			if(keys != null){
				for(String key : keys){
					UUID configUUID = UUID.fromString(queue.getString("messages.revenue." + key + ".UUID"));
					if(playerUUID.equals(configUUID)){
						p.sendMessage(queue.getString("messages.revenue." + key + ".message"));
						queue.set("messages.revenue." + key, null);
						HConH.saveMessageQueue(queue);
					}
				}
			}
		}
	}
	//When a player tries to drop an item/block
	@EventHandler
	public void avoidDrop(PlayerDropItemEvent e) {
		String UUID = e.getPlayer().getUniqueId().toString();
		if(HCM.isInCreationMode(UUID))
			e.setCancelled(true);
	}
	//When a player tries to pickup an item/block
	@EventHandler
	public void avoidPickup(PlayerPickupItemEvent e) {
		String UUID  = e.getPlayer().getUniqueId().toString();
		if(HCM.isInCreationMode(UUID))
			e.setCancelled(true);
	}
	@EventHandler
	public void avoidChestInteraction(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		if(Mes.hasPerm(p, "hotels.cratemode.admin")){
			String UUID = p.getUniqueId().toString();
			if(HCM.isInCreationMode(UUID))
				e.setCancelled(true);
		}
	}
}