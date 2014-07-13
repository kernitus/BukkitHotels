package kernitus.plugin.Hotels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class HotelsCreationMode {	
	
	public static void saveInventory(CommandSender s){
		Player p = ((Player) s);
		ArrayList<ItemStack> list = new ArrayList<>();
		UUID playerUUID = p.getUniqueId();
		File file = new File("plugins//Hotels//Inventories//"+playerUUID+".yml");
		
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
	
	public static void checkFolder(){
		File file = new File("plugins//Hotels//Inventories");
		if(!file.exists()){
			file.mkdir();
		}
	}
	
	public static void loadInventory(CommandSender s){
		Player p = ((Player) s);
		UUID playerUUID = p.getUniqueId();
		File file = new File("plugins//Hotels//Inventories//"+playerUUID+".yml");
		
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
	
	public static void getSelection(CommandSender s){
		Player p = ((Player) s);
		Selection sel = getWorldEdit().getSelection(p);
	}
	
	public static void giveWand(CommandSender s){
		Player p = ((Player) s);
		File file = new File("plugins//Worldedit//config.yml");
		
		if(file.exists()){
			YamlConfiguration inv = YamlConfiguration.loadConfiguration(file);
			if(!(inv == null)&&(inv.contains("wand-item"))&&!(inv.get("wand-item") == null)){
				inv.get("wand-item");
			}
		}
	}
	
}
