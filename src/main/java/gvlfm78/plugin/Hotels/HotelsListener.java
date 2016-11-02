package kernitus.plugin.Hotels;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import org.bukkit.event.player.PlayerQuitEvent;

import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.handlers.HotelsMessageQueue;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.SignManager;
import kernitus.plugin.Hotels.trade.TradesHolder;

public class HotelsListener implements Listener {

	private HotelsCreationMode HCM;
	private SignManager SM;
	private HotelsConfigHandler HCH;


	public HotelsListener(HotelsMain plugin){	
		HCM = new HotelsCreationMode(plugin);
		SM = new SignManager(plugin);
		HCH = new HotelsConfigHandler(plugin);
	}

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
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) { //They Right clicked
			Block b = e.getClickedBlock();
			Material mat = b.getType();
			if(mat.equals(Material.SIGN_POST) || mat.equals(Material.WALL_SIGN)){//If block is sign
				Player p = e.getPlayer();
				//Permission check
				if(Mes.hasPerm(p, "hotels.sign.use")){
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
		Material mat = b.getType();
		if(mat.equals(Material.SIGN_POST) || mat.equals(Material.WALL_SIGN))
			SM.breakRoomSign(e);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		//Player joined the server, send update notification to OPs
		Player p = e.getPlayer();
		
		if(p.hasPermission("hotel.*")){
			String ava = HCH.getupdateAvailable();
			String lin = HCH.getupdateString();
			if(ava != null)
				p.sendMessage(ChatColor.BLUE + ava);
			if(lin != null)
				p.sendMessage(ChatColor.BLUE + lin);
		}
		//Send player all queued up messages for them
		HotelsMessageQueue.sendPlayerAllMessages(p);
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		//Removing them from Trades hashmap if they were in it
		Player p = e.getPlayer();
		TradesHolder.removeFromAll(p);
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
		if(Mes.hasPerm(p, "hotels.createmode.admin")){
			String UUID = p.getUniqueId().toString();
			if(HCM.isInCreationMode(UUID))
				e.setCancelled(true);
		}
	}
}