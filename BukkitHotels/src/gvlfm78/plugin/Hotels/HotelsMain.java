package kernitus.plugin.Hotels;

import kernitus.plugin.Hotels.HotelsCommandHandler;
import kernitus.plugin.Hotels.HotelsListener;
import kernitus.plugin.Hotels.Metrics;

import java.io.File;
import java.io.IOException;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class HotelsMain extends JavaPlugin{

	public static Economy econ = null; //Creating economy variable
	HotelsConfigHandler hconfigh = HotelsConfigHandler.getInstance();

	@Override
	public void onEnable(){
		if (!new File(getDataFolder(), "config.yml").exists()) { //Checking if config file exists
	        hconfigh.setupConfig(this);//Creates config file
	        hconfigh.setupLanguageEnglish(this);//Adds language strings
	      }
		    getServer().getPluginManager().registerEvents(new HotelsListener(), this);//Firing event listener
		    getCommand("Hotels").setExecutor(new HotelsCommandHandler(this));//Firing commands listener
		    setupEconomy();//Setting up the economy
		    if (!setupEconomy() && getConfig().getBoolean("useEconomy")) {//If economy is turned on
		    	//But no vault is found it will warn the user
	            getLogger().severe(String.format("[%s] - No Vault dependency found!", getDescription().getName()));
	            
	            getLogger().info(this.getDescription().getName() + " " + getDescription().getVersion() + " has been enabled");
	        
	        try {
	            Metrics metrics = new Metrics(this);
	            metrics.start();
	        } catch (IOException e) {
	            // Failed to submit the stats :-(
	        }
	        }
	}
	 @Override
	 public void onDisable(){
	         
	        reloadConfig();
	        saveConfig();
	         
	        PluginDescriptionFile pdfFile = this.getDescription();//Logging to console the disabling of IL
	        getLogger().info(pdfFile.getName() + " " + pdfFile.getVersion() + " has been disabled");
	        }

	 public WorldEditPlugin getWorldEdit(){
		 Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		 
		 if (p instanceof WorldEditPlugin) return (WorldEditPlugin) p;
		 else return null;
	 }
	 
	 private boolean setupEconomy() {//Setting up the economy
		 if (getServer().getPluginManager().getPlugin("Vault") == null) {
	        return false;
	    }
		 RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		 if (rsp == null) {
			 return false;
		 }
		 econ = rsp.getProvider();
		 return econ != null;
	
}
}
