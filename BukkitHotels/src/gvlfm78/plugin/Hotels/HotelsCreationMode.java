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
import kernitus.plugin.Hotels.managers.HotelsRegionManager;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

public class HotelsCreationMode {

	private HotelsMain plugin;
	private HotelsRegionManager RM;
	public HotelsCreationMode(HotelsMain plugin){
		this.plugin = plugin;
		RM = new HotelsRegionManager(plugin);
	}
	WorldGuardManager WGM = new WorldGuardManager();
	HotelsConfigHandler HConH = new HotelsConfigHandler(plugin);

	public void checkFolder(){
		File file = HConH.getFile("Inventories");
		if(!file.exists()){
			file.mkdir();
		}
	}
	public boolean isInCreationMode(String uuid){
		File file = HConH.getFile("Inventories"+File.separator+uuid+".yml");
		if(file.exists())
			return true;
		else
			return false;
	}
	public int ownedHotels(Player p){
		ArrayList<Hotel> hotels = HotelsAPI.getHotelsInWorld(p.getWorld());
		int count = 0;
		for(Hotel hotel : hotels){
			if(hotel.isOwner(p.getUniqueId()))
				count++;
		}
		return count;
	}
	public void hotelSetup(String hotelName, CommandSender s){
		Player p = (Player) s;
		if(!hotelName.contains("-")){
			if(Mes.hasPerm(p, "hotels.create")){
				Selection sel = getWorldEdit().getSelection(p);
				if(WGM.hasRegion(p.getWorld(), "Hotel-"+hotelName)){
					p.sendMessage(Mes.mes("chat.creationMode.hotelCreationFailed"));
					return;}
				else if(sel!=null){
					int ownedHotels = ownedHotels(p);
					int maxHotels = HConH.getconfigyml().getInt("settings.max_hotels_owned");
					if(ownedHotels<maxHotels||Mes.hasPerm(p, "hotels.create.admin")){
						//Creating hotel region
						if(sel instanceof CuboidSelection){
							ProtectedRegion r = new ProtectedCuboidRegion(
									"Hotel-"+hotelName, 
									new BlockVector(sel.getNativeMinimumPoint()), 
									new BlockVector(sel.getNativeMaximumPoint())
									);
							createHotelRegion(p,r,hotelName);
						}
						else if(sel instanceof Polygonal2DSelection){
							int minY = sel.getMinimumPoint().getBlockY();
							int maxY = sel.getMaximumPoint().getBlockY();
							List<BlockVector2D> points = ((Polygonal2DSelection) sel).getNativePoints();
							ProtectedRegion r = new ProtectedPolygonalRegion("Hotel-"+hotelName, points, minY, maxY);
							createHotelRegion(p,r,hotelName);
						}
						else
							p.sendMessage(Mes.mes("chat.creationMode.selectionInvalid"));
					}
					else
						p.sendMessage((Mes.mes("chat.commands.create.maxHotelsReached")).replaceAll("%max%", String.valueOf(maxHotels)));
				}
				else
					p.sendMessage(Mes.mes("chat.creationMode.noSelection"));
			}
			else
				p.sendMessage(Mes.mes("chat.noPermission"));
		}
		else
			p.sendMessage(Mes.mes("chat.creationMode.invalidChar"));
	}

	public boolean createHotelRegion(Player p, ProtectedRegion region, String hotelName){
		World world = p.getWorld();
		if(!WGM.doHotelRegionsOverlap(region, world)){

			WGM.addRegion(world, region);
			WGM.hotelFlags(region,hotelName);
			WGM.addOwner(p, region);
			region.setPriority(5);
			WGM.saveRegions(world);
			String idHotelName =region.getId();
			String[] partsofhotelName = idHotelName.split("-");
			p.sendMessage(Mes.mes("chat.creationMode.hotelCreationSuccessful").replaceAll("%hotel%", partsofhotelName[1]));
			int ownedHotels = ownedHotels(p);
			int maxHotels = HConH.getconfigyml().getInt("settings.max_hotels_owned");

			String hotelsLeft = String.valueOf(maxHotels-ownedHotels);

			if(!Mes.hasPerm(p, "hotels.create.admin"))//If the player has hotel limit display message
			p.sendMessage(Mes.mes("chat.commands.create.creationSuccess").replaceAll("%tot%", String.valueOf(ownedHotels)).replaceAll("%left%", String.valueOf(hotelsLeft)));
			
			return true;
		}
		else{
			p.sendMessage(Mes.mes("chat.commands.create.hotelAlreadyPresent"));
			return false;
		}
	}

	public void createRoomRegion(Player p, ProtectedRegion region, String hotelName, int room){
		World world = p.getWorld();
		ProtectedRegion hotelRegion = WGM.getRegion(world, "hotel-"+hotelName);
		if(Mes.hasPerm(p, "hotels.create")){
			if(!WGM.doesRoomRegionOverlap(region, world)){
				if(WGM.isOwner(p, hotelRegion)||Mes.hasPerm(p, "hotels.create.admin")){
					WGM.addRegion(world, region);
					WGM.roomFlags(region,room);
					if(HConH.getconfigyml().getBoolean("settings.stopOwnersEditingRentedRooms"))
						region.setPriority(1);
					else
						region.setPriority(10);
					RM.makeRoomAccessible(region);
					WGM.saveRegions(p.getWorld());
					p.sendMessage(Mes.mes("chat.commands.room.success").replaceAll("%room%", String.valueOf(room)).replaceAll("%hotel%", hotelName));
				}
				else
					p.sendMessage(Mes.mes("chat.commands.youDoNotOwnThat"));
			}
			else
				p.sendMessage(Mes.mes("chat.commands.room.alreadyPresent"));
		}
		else
			p.sendMessage(Mes.mes("chat.noPermission"));
	}
	public void roomSetup(String hotelName,int roomNum,Player p){
		Selection sel = getWorldEdit().getSelection(p);
		World world = p.getWorld();
		Hotel hotel = new Hotel(world,hotelName);
		if(hotel.exists()){
			Room room = new Room(hotel,roomNum);
			if(!room.exists()){//If room doesn't already exist
				ProtectedRegion pr = hotel.getRegion();
				if(sel!=null){
					if((sel instanceof Polygonal2DSelection)&&(pr.containsAny(((Polygonal2DSelection) sel).getNativePoints()))||
							((sel instanceof CuboidSelection)&&(pr.contains(sel.getNativeMinimumPoint())&&pr.contains(sel.getNativeMaximumPoint())))){
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
				else
					p.sendMessage(Mes.mes("chat.creationMode.noSelection"));
			}
			else
				p.sendMessage(Mes.mes("chat.creationMode.rooms.alreadyExists"));
		}
		else
			p.sendMessage(Mes.mes("chat.creationMode.rooms.fail"));
	}

	public void resetInventoryFiles(CommandSender s){
		Player p = ((Player) s);
		UUID playerUUID = p.getUniqueId();
		File invFile = HConH.getFile("Inventories"+File.separator+playerUUID+".yml");
		if(invFile.exists())
			invFile.delete();
	}

	public void saveInventory(CommandSender s){
		Player p = ((Player) s);
		UUID playerUUID = p.getUniqueId();
		PlayerInventory pinv = p.getInventory();
		File file = HConH.getFile("Inventories"+File.separator+playerUUID+".yml");

		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e){
				p.sendMessage(Mes.mes("chat.creationMode.inventory.storeFail"));
			}

			YamlConfiguration inv = HConH.getconfig("Inventories"+File.separator+playerUUID+".yml");

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
		File file = HConH.getFile("Inventories"+File.separator+playerUUID+".yml");

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
