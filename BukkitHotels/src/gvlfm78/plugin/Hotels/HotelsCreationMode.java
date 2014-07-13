package kernitus.plugin.Hotels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class HotelsCreationMode {	
	
	public static void checkFolder(){
		File file = new File("plugins//Hotels//Inventories");
		if(!file.exists()){
			file.mkdir();
		}
	}
	
	public static void resetInventoryFiles(CommandSender s){
		Player p = ((Player) s);
		UUID playerUUID = p.getUniqueId();
		File invFile = new File("plugins//Hotels//Inventories//"+"Inventory-"+playerUUID+".yml");
		File armFile = new File("plugins//Hotels//Inventories//"+"Armour-"+playerUUID+".yml");
		if(invFile.exists()){
			invFile.delete();
		}
		else return;
		if(armFile.exists()){
			armFile.delete();
		}
		else return;
	}
	
	public static void saveInventory(CommandSender s){
		Player p = ((Player) s);
		ArrayList<ItemStack> list = new ArrayList<>();
		UUID playerUUID = p.getUniqueId();
		File file = new File("plugins//Hotels//Inventories//"+"Inventory-"+playerUUID+".yml");
		
		if(!file.exists()){
			try {
			file.createNewFile();
			} catch (IOException e){
				p.sendMessage(ChatColor.DARK_RED + "Could not store your inventory");
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
				p.sendMessage(ChatColor.DARK_RED + "Could not store your inventory");
			}
			p.getInventory().clear();
			p.sendMessage(ChatColor.GREEN+"Your inventory has been stored");
		}else{
			p.sendMessage(ChatColor.DARK_RED + "Could not store your inventory");
		}
	}
	
	public static void saveArmour(CommandSender s){
		Player p = ((Player) s);
		ArrayList<ItemStack> list = new ArrayList<>();
		UUID playerUUID = p.getUniqueId();
		File file = new File("plugins//Hotels//Inventories//"+"Armour-"+playerUUID+".yml");
		
		if(!file.exists()){
			try {
			file.createNewFile();
			} catch (IOException e){
				p.sendMessage(ChatColor.DARK_RED + "Could not store your armour");
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
				p.sendMessage(ChatColor.DARK_RED + "Could not store your armour");
			}
			p.getInventory().setArmorContents(null);;
			p.sendMessage(ChatColor.GREEN+"Your armour has been stored");
		}else{
			p.sendMessage(ChatColor.DARK_RED + "Could not store your armour");
		}
	}
	
	public static void loadArmour(CommandSender s){
		Player p = ((Player) s);
		UUID playerUUID = p.getUniqueId();
		File file = new File("plugins//Hotels//Inventories//"+"Armour-"+playerUUID+".yml");
		
		if(file.exists()){
			YamlConfiguration inv = YamlConfiguration.loadConfiguration(file);
			p.getInventory().setArmorContents(null);
			ItemStack[] contents = p.getInventory().getArmorContents();
			List<?> list = inv.getList("Armour");
			
			for(int i = 0; i < list.size(); i++){
				contents[i] = (ItemStack) list.get(i);
			}
			p.getInventory().setArmorContents(contents);
			p.sendMessage(ChatColor.GREEN + "Your armour has been restored");
			file.delete();
			
		}else{
			p.sendMessage(ChatColor.DARK_RED + "Your armour could not be found!");
		}
	}
	
	public static void loadInventory(CommandSender s){
		Player p = ((Player) s);
		UUID playerUUID = p.getUniqueId();
		File file = new File("plugins//Hotels//Inventories//"+"Inventory-"+playerUUID+".yml");
		
		if(file.exists()){
			YamlConfiguration inv = YamlConfiguration.loadConfiguration(file);
			p.getInventory().clear();
			ItemStack[] contents = p.getInventory().getContents();
			List<?> list = inv.getList("Inventory");
			
			for(int i = 0; i < list.size(); i++){
				contents[i] = (ItemStack) list.get(i);
			}
			p.getInventory().setContents(contents);
			p.sendMessage(ChatColor.GREEN + "Your inventory has been restored");
			file.delete();
			
		}else{
			p.sendMessage(ChatColor.DARK_RED + "Your inventory could not be found!");
		}
	}
	
	 public static WorldEditPlugin getWorldEdit(){
		 Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		 
		 if (p instanceof WorldEditPlugin) return (WorldEditPlugin) p;
		 else return null;
	 }
	 
	 public static WorldGuardPlugin getWorldGuard(){
		 Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		 
		 if (p instanceof WorldGuardPlugin) return (WorldGuardPlugin) p;
		 else return null;
	 }
	
	public static void getSelection(CommandSender s){
		Player p = ((Player) s);
		Selection sel = getWorldEdit().getSelection(p);
	}
	
	public static void giveItems(CommandSender s){
		Player p = ((Player) s);
		File file = new File("plugins//Worldedit//config.yml");
		PlayerInventory pi = p.getInventory();
		
		if(file.exists()){
			YamlConfiguration weconfig = YamlConfiguration.loadConfiguration(file);
			if(!(weconfig == null)&&(weconfig.contains("wand-item"))&&!(weconfig.get("wand-item") == null)){
				int wanditem = (int) weconfig.get("wand-item");
				ItemStack wand = new ItemStack(wanditem, 1);
				ItemMeta im = wand.getItemMeta();
				im.setDisplayName("§bWorldEdit Wand");
				List<String> loreList = new ArrayList<String>();
				loreList.add("L-click one corner");//This is the first line of lore
				loreList.add("R-click opposite corner");//This is the second line of lore
				im.setLore(loreList);
				wand.setItemMeta(im);
				pi.setItem(1, wand);
				
			}
		}
		ItemStack compass = new ItemStack(Material.COMPASS, 1);
		ItemMeta cim = compass.getItemMeta();
		cim.setDisplayName("§bWorldEdit Compass");
		List<String> compassLoreList = new ArrayList<String>();
		compassLoreList.add("L-click to tp to");//This is the first line of lore
		compassLoreList.add("R-click to pass through");//This is the second line of lore
		cim.setLore(compassLoreList);
		compass.setItemMeta(cim);
		pi.setItem(0, compass);
		ItemStack sign = new ItemStack(Material.SIGN, 1);
		ItemMeta sim = sign.getItemMeta();
		sim.setDisplayName("§bEpic Sign");
		List<String> signLoreList = new ArrayList<String>();
		signLoreList.add("R-click to place");//This is the first line of lore
		signLoreList.add("First Line: [Hotels]");//This is the second line of lore
		sim.setLore(signLoreList);
		sign.setItemMeta(sim);
		pi.setItem(2, sign);
	}
	
}
