package kernitus.plugin.Hotels;

import handlers.HotelsCommandHandler;
import handlers.HotelsConfigHandler;

import java.io.File;

import managers.GameLoop;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HotelsMain extends JavaPlugin{

	public static Economy economy = null; //Creating economy variable
	HotelsConfigHandler hconfigh = HotelsConfigHandler.getInstance();
	GameLoop gameloop;

	@Override
	public void onEnable(){
		//Config file stuff
		PluginDescriptionFile pdfFile = this.getDescription();
		if (!new File(getDataFolder(), "config.yml").exists()) { //Checking if config file exists
			hconfigh.setupConfig(this);//Creates config file
			hconfigh.setupLanguageEnglish(this);//Adds language strings
		}
		//Listeners and stuff
		getServer().getPluginManager().registerEvents((new HotelsListener(this)), this);//Firing event listener
		getCommand("Hotels").setExecutor(new HotelsCommandHandler(this));//Firing commands listener
		setupEconomy();

		//Economy and stuff
		if (!setupEconomy()) {
			//If economy is turned on
			//but no vault is found it will warn the user
			getLogger().severe(String.format("[%s] - No Vault dependency found!", getDescription().getName()));}

		//hconfigh.setupFlagsFile(this);
		//getLogger().info(pdfFile.getName() + " " + pdfFile.getVersion() + " has setup the flags correctly ");

		//GameLoop stuff
		//gameloop = new GameLoop(this);
		gameloop = new GameLoop(this);
		gameloop.runTaskTimer(this, 200, 2*60*20);

		//Logging to console the correct enabling of Hotels
		getLogger().info(pdfFile.getName() + " " + pdfFile.getVersion() + " has been enabled correctly");
	}
	@Override
	public void onDisable(){

		reloadConfig();
		saveConfig();

		gameloop.cancel();

		PluginDescriptionFile pdfFile = this.getDescription();//Logging to console the disabling of Hotels
		getLogger().info(pdfFile.getName() + " " + pdfFile.getVersion() + " has been disabled");
	}

	//Setting up the economy
	private boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}
}
