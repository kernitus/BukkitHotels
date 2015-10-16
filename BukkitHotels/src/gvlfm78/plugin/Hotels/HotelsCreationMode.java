package kernitus.plugin.Hotels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
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
import kernitus.plugin.Hotels.managers.HotelsMessageManager;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

public class HotelsCreationMode {
	private HotelsMain plugin;
	public HotelsCreationMode(HotelsMain instance)
	{
		this.plugin = instance;
	}
	HotelsMessageManager HMM = new HotelsMessageManager(plugin);
	WorldGuardManager WGM = new WorldGuardManager(plugin);
	HotelsConfigHandler HConH = new HotelsConfigHandler(plugin);

	//Prefix
	YamlConfiguration locale = HConH.getLocale();
	String prefix = (locale.getString("chat.prefix").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")+" ");

	public void checkFolder(){
		File file = HConH.getFile("Inventories");
		if(!file.exists()){
			file.mkdir();
		}
	}

	public void hotelSetup(String hotelName, CommandSender s,Plugin plugin){
		hotelName = hotelName.toLowerCase();
		Player p = (Player) s;
		if(!hotelName.contains("-")){
		if(p.isOp()||(plugin.getConfig().getBoolean("settings.use-permissions")&&(p.hasPermission("hotels.commands")||p.hasPermission("hotels.*")))){
			Selection sel = getWorldEdit().getSelection(p);
			if(WGM.hasRegion(p.getWorld(), "Hotel-"+hotelName)){
				p.sendMessage(HMM.mes("chat.creationMode.hotelCreationFailed").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				return;}
			else if(sel!=null){
				//Creating hotel region
				if(sel instanceof CuboidSelection){
					ProtectedRegion r = new ProtectedCuboidRegion(
							"Hotel-"+hotelName, 
							new BlockVector(sel.getNativeMinimumPoint()), 
							new BlockVector(sel.getNativeMaximumPoint())
							);
					createHotelRegion(plugin, p,r,hotelName);
				}
				else if(sel instanceof Polygonal2DSelection){
					int minY = sel.getMinimumPoint().getBlockY();
					int maxY = sel.getMaximumPoint().getBlockY();
					List<BlockVector2D> points = ((Polygonal2DSelection) sel).getNativePoints();
					ProtectedRegion r = new ProtectedPolygonalRegion("Hotel-"+hotelName, points, minY, maxY);
					createHotelRegion(plugin, p,r,hotelName);
				}
				else
					p.sendMessage(HMM.mes("chat.creationMode.selectionInvalid").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
			}
			else
				p.sendMessage(HMM.mes("chat.creationMode.noSelection").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
		}
		else
			p.sendMessage(HMM.mes("chat.noPermission").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
	}
		else
			p.sendMessage(HMM.mes("chat.creationMode.invalidChar").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
}

	public void createHotelRegion(Plugin plugin, Player p, ProtectedRegion region, String hotelName){
		World world = p.getWorld();
		WGM.addRegion(world, region);
		WGM.hotelFlags(region,hotelName,plugin);
		WGM.saveRegions(world);
		String idHotelName =region.getId();
		String[] partsofhotelName = idHotelName.split("-");
		String fromIdhotelName = partsofhotelName[1].substring(0, 1).toUpperCase() + partsofhotelName[1].substring(1);
		p.sendMessage(HMM.mes("chat.creationMode.hotelCreationSuccessful").replaceAll("%hotel%", fromIdhotelName).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
	}

	public void createRoomRegion(Plugin plugin, Player p, ProtectedRegion region, String hotelName, int roomNum){
		WGM.addRegion(p.getWorld(), region);
		WGM.roomFlags(region,hotelName, p, roomNum,plugin);
		region.setPriority(10);
		WGM.saveRegions(p.getWorld());
	}

	public void roomSetup(String hotelName,int roomNum,CommandSender s,Plugin plugin){
		hotelName = hotelName.toLowerCase();
		Player p = (Player) s;
		Selection sel = getWorldEdit().getSelection(p);
		World world = p.getWorld();
		if(WGM.getWorldGuard().getRegionManager(p.getWorld()).hasRegion("hotel-"+hotelName)){
			ProtectedRegion pr = WGM.getWorldGuard().getRegionManager(world).getRegion("hotel-"+hotelName);
			if(sel!=null){
				if((sel instanceof Polygonal2DSelection)&&(pr.containsAny(((Polygonal2DSelection) sel).getNativePoints()))||
				((sel instanceof CuboidSelection)&&(pr.contains(sel.getNativeMinimumPoint())&&pr.contains(sel.getNativeMaximumPoint())))){
					//Creating room region
					if(sel instanceof CuboidSelection){
						ProtectedRegion r = new ProtectedCuboidRegion(
								"Hotel-"+hotelName+"-"+roomNum, 
								new BlockVector(sel.getNativeMinimumPoint()), 
								new BlockVector(sel.getNativeMaximumPoint())
								);
						createRoomRegion(plugin,p,r,hotelName,roomNum);
					}
					else if(sel instanceof Polygonal2DSelection){
						int minY = sel.getMinimumPoint().getBlockY();
						int maxY = sel.getMaximumPoint().getBlockY();
						List<BlockVector2D> points = ((Polygonal2DSelection) sel).getNativePoints();
						ProtectedRegion r = new ProtectedPolygonalRegion("Hotel-"+hotelName+"-"+roomNum, points, minY, maxY);
						createRoomRegion(plugin,p,r,hotelName,roomNum);
					}
					else
						p.sendMessage(HMM.mes("chat.creationMode.selectionInvalid").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				}
				else
					p.sendMessage(HMM.mes("chat.creationMode.rooms.notInHotel").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
			}
			else
				p.sendMessage(HMM.mes("chat.creationMode.noSelection").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
		}
		else
			p.sendMessage(HMM.mes("chat.creationMode.rooms.fail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
	}

	public void resetInventoryFiles(CommandSender s){
		Player p = ((Player) s);
		UUID playerUUID = p.getUniqueId();
		File invFile = HConH.getFile("Inventories"+File.separator+"Inventory-"+playerUUID+".yml");
		File armFile = HConH.getFile("Inventories"+File.separator+"Armour-"+playerUUID+".yml");
		if(invFile.exists()){
			invFile.delete();
		}
		else return;
		if(armFile.exists()){
			armFile.delete();
		}
		else return;
	}

	public void saveInventory(CommandSender s){
		Player p = ((Player) s);
		ArrayList<ItemStack> list = new ArrayList<>();
		UUID playerUUID = p.getUniqueId();
		File file = HConH.getFile("Inventories"+File.separator+"Inventory-"+playerUUID+".yml");

		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e){
				p.sendMessage(HMM.mes("chat.creationMode.inventory.storeFail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
			}
			YamlConfiguration inv = YamlConfiguration.loadConfiguration(file);
			ItemStack[] contents = p.getInventory().getContents();
			for(int i = 0; i < contents.length; i++){
				ItemStack item = contents[i];
				if(!(item == null)){
					list.add(item);
				}
			}
			inv.set("Inventory", list);
			try {
				inv.save(file);
			} catch (IOException e) {
				p.sendMessage(HMM.mes("chat.creationMode.inventory.storeFail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
			}
			p.getInventory().clear();
			p.sendMessage(HMM.mes("chat.creationMode.inventory.storeSuccess").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
		}else{
			p.sendMessage(prefix+HMM.mes("chat.creationMode.inventory.storeFail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
		}
	}

	public void saveArmour(CommandSender s){
		Player p = ((Player) s);
		ArrayList<ItemStack> list = new ArrayList<>();
		UUID playerUUID = p.getUniqueId();
		File file = HConH.getFile("Inventories"+File.separator+"Armour-"+playerUUID+".yml");

		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e){
				p.sendMessage(HMM.mes("chat.creationMode.armour.storeFail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
			}
			YamlConfiguration inv = YamlConfiguration.loadConfiguration(file);
			ItemStack[] contents = p.getInventory().getArmorContents();
			for(int i = 0; i < contents.length; i++){
				ItemStack item = contents[i];
				if(!(item == null)){
					list.add(item);
				}
			}
			inv.set("Armour", list);
			try {
				inv.save(file);
			} catch (IOException e) {
				p.sendMessage(HMM.mes("chat.creationMode.armour.storeFail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
			}
			p.getInventory().setArmorContents(null);;
			p.sendMessage(HMM.mes("chat.creationMode.armour.storeSuccess").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
		}else{
			p.sendMessage(HMM.mes("chat.creationMode.armour.storeFail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
		}
	}

	public void loadArmour(CommandSender s){
		Player p = ((Player) s);
		UUID playerUUID = p.getUniqueId();
		File file = HConH.getFile("Inventories"+File.separator+"Armour-"+playerUUID+".yml");

		if(file.exists()){
			YamlConfiguration inv = YamlConfiguration.loadConfiguration(file);
			p.getInventory().setArmorContents(null);
			ItemStack[] contents = p.getInventory().getArmorContents();
			List<?> list = inv.getList("Armour");

			for(int i = 0; i < list.size(); i++){
				contents[i] = (ItemStack) list.get(i);
			}
			p.getInventory().setArmorContents(contents);
			p.sendMessage(HMM.mes("chat.creationMode.armour.restoreSuccess").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
			file.delete();

		}else{
			p.sendMessage(HMM.mes("chat.creationMode.armour.restoreFail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
		}
	}

	public void loadInventory(CommandSender s){
		Player p = ((Player) s);
		UUID playerUUID = p.getUniqueId();
		File file = HConH.getFile("Inventories"+File.separator+"Inventory-"+playerUUID+".yml");

		if(file.exists()){
			YamlConfiguration inv = YamlConfiguration.loadConfiguration(file);
			p.getInventory().clear();
			ItemStack[] contents = p.getInventory().getContents();
			List<?> list = inv.getList("Inventory");

			for(int i = 0; i < list.size(); i++){
				contents[i] = (ItemStack) list.get(i);
			}
			p.getInventory().setContents(contents);
			p.sendMessage(HMM.mes("chat.creationMode.inventory.restoreSuccess").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
			file.delete();

		}else{
			p.sendMessage(HMM.mes("chat.creationMode.inventory.restoreFail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
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

		if(file.exists()){
			YamlConfiguration weconfig = YamlConfiguration.loadConfiguration(file);
			if(!(weconfig == null)&&(weconfig.contains("wand-item"))&&!(weconfig.get("wand-item") == null)){
				int wanditem = (int) weconfig.get("wand-item");
				@SuppressWarnings("deprecation")
				ItemStack wand = new ItemStack(wanditem, 1);
				ItemMeta im = wand.getItemMeta();
				im.setDisplayName(locale.getString("chat.creationMode.items.wand.name").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				List<String> loreList = new ArrayList<String>();
				loreList.add(locale.getString("chat.creationMode.items.wand.lore1").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				loreList.add(locale.getString("chat.creationMode.items.wand.lore2").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				im.setLore(loreList);
				wand.setItemMeta(im);
				pi.setItem(1, wand);

			}
		}
		ItemStack compass = new ItemStack(Material.COMPASS, 1);
		ItemMeta cim = compass.getItemMeta();
		cim.setDisplayName(locale.getString("chat.creationMode.items.compass.name").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
		List<String> compassLoreList = new ArrayList<String>();
		compassLoreList.add(locale.getString("chat.creationMode.items.compass.lore1").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
		compassLoreList.add(locale.getString("chat.creationMode.items.compass.lore2").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
		cim.setLore(compassLoreList);
		compass.setItemMeta(cim);
		pi.setItem(0, compass);
		ItemStack sign = new ItemStack(Material.SIGN, 1);
		ItemMeta sim = sign.getItemMeta();
		sim.setDisplayName(locale.getString("chat.creationMode.items.sign.name").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
		List<String> signLoreList = new ArrayList<String>();
		signLoreList.add(locale.getString("chat.creationMode.items.sign.lore1").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
		signLoreList.add(locale.getString("chat.creationMode.items.sign.lore2").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
		sim.setLore(signLoreList);
		sign.setItemMeta(sim);
		pi.setItem(2, sign);
	}
}
