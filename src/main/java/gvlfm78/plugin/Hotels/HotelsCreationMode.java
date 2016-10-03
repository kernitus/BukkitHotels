package kernitus.plugin.Hotels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

public class HotelsCreationMode {

	private HotelsMain plugin;
	public HotelsCreationMode(HotelsMain plugin){
		this.plugin = plugin;
	}
	WorldGuardManager WGM = new WorldGuardManager();
	HotelsConfigHandler HCH = new HotelsConfigHandler(plugin);

	public boolean isInCreationMode(String uuid){
		return HCH.getInventoryFile(UUID.fromString(uuid)).exists();
	}
	public boolean isInCreationMode(UUID uuid){
		return HCH.getInventoryFile(uuid).exists();
	}

	public void hotelSetup(String hotelName, CommandSender s){
		Player p = (Player) s;

		if(hotelName.contains("-")){ p.sendMessage(Mes.mes("chat.creationMode.invalidChar")); return; }

		if(Mes.hasPerm(p, "hotels.create")){ p.sendMessage(Mes.mes("chat.noPermission")); return; }

		Selection sel = getWorldEdit().getSelection(p);
		Hotel hotel = new Hotel(p.getWorld(), hotelName);

		if(hotel.exists()){	p.sendMessage(Mes.mes("chat.creationMode.hotelCreationFailed")); return; }

		if(sel==null){ p.sendMessage(Mes.mes("chat.creationMode.noSelection")); return; }

		int ownedHotels = HotelsAPI.getHotelsOwnedBy(p.getUniqueId()).size();
		int maxHotels = plugin.getConfig().getInt("settings.max_hotels_owned");
		if(ownedHotels>maxHotels && !Mes.hasPerm(p, "hotels.create.admin")){
			p.sendMessage((Mes.mes("chat.commands.create.maxHotelsReached")).replaceAll("%max%", String.valueOf(maxHotels))); return;
		}
		//Creating hotel region
		if(sel instanceof CuboidSelection){
			ProtectedRegion r = new ProtectedCuboidRegion(
					"Hotel-"+hotelName, 
					new BlockVector(sel.getNativeMinimumPoint()), 
					new BlockVector(sel.getNativeMaximumPoint())
					);
			hotel.create(r, p);
		}
		else if(sel instanceof Polygonal2DSelection){
			int minY = sel.getMinimumPoint().getBlockY();
			int maxY = sel.getMaximumPoint().getBlockY();
			List<BlockVector2D> points = ((Polygonal2DSelection) sel).getNativePoints();
			ProtectedRegion r = new ProtectedPolygonalRegion("Hotel-"+hotelName, points, minY, maxY);
			hotel.create(r, p);
		}
		else
			p.sendMessage(Mes.mes("chat.creationMode.selectionInvalid"));
	}

	public void roomSetup(String hotelName,int roomNum,Player p){
		Selection sel = getWorldEdit().getSelection(p);
		World world = p.getWorld();
		Hotel hotel = new Hotel(world, hotelName);
		if(!hotel.exists()){ p.sendMessage(Mes.mes("chat.creationMode.rooms.fail")); return; }
		Room room = new Room(hotel, roomNum);
		if(room.exists()){ p.sendMessage(Mes.mes("chat.creationMode.rooms.alreadyExists")); return; }
		ProtectedRegion pr = hotel.getRegion();
		if(sel==null){ p.sendMessage(Mes.mes("chat.creationMode.noSelection")); return; }
		if((sel instanceof Polygonal2DSelection) && (pr.containsAny(((Polygonal2DSelection) sel).getNativePoints()))||
				((sel instanceof CuboidSelection) && (pr.contains(sel.getNativeMinimumPoint())&&pr.contains(sel.getNativeMaximumPoint())))){
			//Creating room region
			if(sel instanceof CuboidSelection){
				ProtectedRegion r = new ProtectedCuboidRegion(
						"Hotel-"+hotelName+"-"+room, 
						new BlockVector(sel.getNativeMinimumPoint()), 
						new BlockVector(sel.getNativeMaximumPoint())
						);
				createRoomRegion(p,r,hotelName,roomNum);
			}
			else if(sel instanceof Polygonal2DSelection){
				int minY = sel.getMinimumPoint().getBlockY();
				int maxY = sel.getMaximumPoint().getBlockY();
				List<BlockVector2D> points = ((Polygonal2DSelection) sel).getNativePoints();
				ProtectedRegion r = new ProtectedPolygonalRegion("Hotel-"+hotelName+"-"+room, points, minY, maxY);
				createRoomRegion(p,r,hotelName,roomNum);
			}
			else
				p.sendMessage(Mes.mes("chat.creationMode.selectionInvalid"));
		}
		else
			p.sendMessage(Mes.mes("chat.creationMode.rooms.notInHotel"));
	}

	public void resetInventoryFiles(CommandSender s){
		Player p = ((Player) s);
		UUID playerUUID = p.getUniqueId();
		File invFile = HotelsConfigHandler.getFile("Inventories"+File.separator+playerUUID+".yml");
		if(invFile.exists())
			invFile.delete();
	}

	public void saveInventory(CommandSender s){
		Player p = ((Player) s);
		UUID playerUUID = p.getUniqueId();
		PlayerInventory pinv = p.getInventory();
		File file = HotelsConfigHandler.getFile("Inventories"+File.separator+playerUUID+".yml");

		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e){
				p.sendMessage(Mes.mes("chat.creationMode.inventory.storeFail"));
			}

			YamlConfiguration inv = HCH.getconfig("Inventories"+File.separator+playerUUID+".yml");

			inv.set("inventory", pinv.getContents());
			inv.set("armour", pinv.getArmorContents());

			try{//Only if in 1.9 try getting this
				inv.set("extra", pinv.getExtraContents());
			}
			catch(NoSuchMethodError er){
				//We must be in a pre-1.9 version
			}

			try {
				inv.save(file);
			} catch (IOException e) {
				p.sendMessage(Mes.mes("chat.creationMode.inventory.storeFail"));
			}

			pinv.clear();
			pinv.setArmorContents(new ItemStack[4]);
			p.sendMessage(Mes.mes("chat.creationMode.inventory.storeSuccess"));
		}else{
			p.sendMessage(Mes.mes("chat.creationMode.inventory.storeFail"));
		}
	}

	@SuppressWarnings("unchecked")
	public void loadInventory(CommandSender s){
		Player p = (Player) s;
		UUID playerUUID = p.getUniqueId();
		PlayerInventory pinv = p.getInventory();
		File file = HotelsConfigHandler.getFile("Inventories"+File.separator+playerUUID+".yml");

		if(file.exists()){
			YamlConfiguration inv = HotelsConfigHandler.getyml(file);

			List<ItemStack> inventoryItems = (List<ItemStack>) inv.getList("inventory");
			pinv.setContents(inventoryItems.toArray(new ItemStack[inventoryItems.size()]));

			List<ItemStack> armourItems = (List<ItemStack>) inv.getList("armour");
			pinv.setArmorContents(armourItems.toArray(new ItemStack[armourItems.size()]));

			try{
				List<ItemStack> extraItems = (List<ItemStack>) inv.getList("extra");
				pinv.setExtraContents(extraItems.toArray(new ItemStack[extraItems.size()]));
			}
			catch(Exception et){
				//Must be in a pre-1.9 version
			}

			p.sendMessage(Mes.mes("chat.creationMode.inventory.restoreSuccess"));
			file.delete();
		}

		else{
			p.sendMessage(Mes.mes("chat.creationMode.inventory.restoreFail"));
		}
	}

	public WorldEditPlugin getWorldEdit(){
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

		if (p instanceof WorldEditPlugin) return (WorldEditPlugin) p;
		else return null;
	}

	public Selection getSelection(CommandSender s){
		Player p = ((Player) s);
		return getWorldEdit().getSelection(p);
	}

	public void giveItems(CommandSender s){
		Player p = ((Player) s);
		File file = new File("plugins"+File.separator+"Worldedit"+File.separator+"config.yml");
		PlayerInventory pi = p.getInventory();

		//Wand
		if(file.exists()){
			YamlConfiguration weconfig = YamlConfiguration.loadConfiguration(file);
			if(!(weconfig == null)&&(weconfig.contains("wand-item"))&&!(weconfig.get("wand-item") == null)){
				int wanditem = (int) weconfig.get("wand-item");
				@SuppressWarnings("deprecation")
				ItemStack wand = new ItemStack(wanditem, 1);
				ItemMeta im = wand.getItemMeta();
				im.setDisplayName(Mes.mesnopre("chat.creationMode.items.wand.name"));
				List<String> loreList = new ArrayList<String>();
				loreList.add(Mes.mesnopre("chat.creationMode.items.wand.lore1"));
				loreList.add(Mes.mesnopre("chat.creationMode.items.wand.lore2"));
				im.setLore(loreList);
				wand.setItemMeta(im);
				pi.setItem(0, wand);

			}
		}
		//Sign
		if(p.getGameMode().equals(GameMode.CREATIVE)){
			ItemStack sign = new ItemStack(Material.SIGN, 1);
			ItemMeta sim = sign.getItemMeta();
			sim.setDisplayName(Mes.mesnopre("chat.creationMode.items.sign.name"));
			List<String> signLoreList = new ArrayList<String>();
			signLoreList.add(Mes.mesnopre("chat.creationMode.items.sign.lore1"));
			signLoreList.add(Mes.mesnopre("chat.creationMode.items.sign.lore2"));
			sim.setLore(signLoreList);
			sign.setItemMeta(sim);
			pi.setItem(1, sign);
		}
		//Compass
		if(Mes.hasPerm(p,"worldedit.navigation")){
			ItemStack compass = new ItemStack(Material.COMPASS, 1);
			ItemMeta cim = compass.getItemMeta();
			cim.setDisplayName(Mes.mesnopre("chat.creationMode.items.compass.name"));
			List<String> compassLoreList = new ArrayList<String>();
			compassLoreList.add(Mes.mesnopre("chat.creationMode.items.compass.lore1"));
			compassLoreList.add(Mes.mesnopre("chat.creationMode.items.compass.lore2"));
			cim.setLore(compassLoreList);
			compass.setItemMeta(cim);
			pi.setItem(2, compass);
		}
		//Leather
		if(Mes.hasPerm(p,"worldguard.region.wand")){
			ItemStack leather = new ItemStack(Material.LEATHER, 1);
			ItemMeta lim = leather.getItemMeta();
			lim.setDisplayName(Mes.mesnopre("chat.creationMode.items.leather.name"));
			List<String> leatherLoreList = new ArrayList<String>();
			leatherLoreList.add(Mes.mesnopre("chat.creationMode.items.leather.lore1"));
			leatherLoreList.add(Mes.mesnopre("chat.creationMode.items.leather.lore2"));
			lim.setLore(leatherLoreList);
			leather.setItemMeta(lim);
			pi.setItem(3, leather);
		}
	}
}
