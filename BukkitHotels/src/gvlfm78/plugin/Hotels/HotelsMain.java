package kernitus.plugin.Hotels;

import java.io.File;
import java.io.IOException;

import kernitus.plugin.Hotels.handlers.HotelsCommandHandler;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.GameLoop;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HotelsMain extends JavaPlugin{

	public static Economy economy = null; //Creating economy variable
	HotelsConfigHandler hconfigh = HotelsConfigHandler.getInstance();
	GameLoop gameloop;
	protected HotelsUpdateChecker updateChecker;

	@Override
	public void onEnable(){
		
		this.updateChecker = new HotelsUpdateChecker(this, "http://dev.bukkit.org/bukkit-plugins/hotels/files.rss");
		this.updateChecker.updateNeeded();

		if(this.updateChecker.updateNeeded()&&getConfig().getBoolean("settings.checkForUpdates")){
			this.getLogger().info(getConfig().getString("main.updateAvailable").replaceAll("%version%", this.updateChecker.getVersion()));
			this.getLogger().info(getConfig().getString("main.updateAvailableLink").replaceAll("%link%", this.updateChecker.getLink()));
		}
		
		setupConfig();
		PluginDescriptionFile pdfFile = this.getDescription();
		//Listeners and stuff
		getServer().getPluginManager().registerEvents((new HotelsListener(this)), this);//Firing event listener
		getCommand("Hotels").setExecutor(new HotelsCommandHandler(this));//Firing commands listener
		setupEconomy();
		File lfile = new File("plugins//Hotels//locale.yml");
		YamlConfiguration locale = YamlConfiguration.loadConfiguration(lfile);
		//Economy and stuff
		if (!setupEconomy()){
			//If economy is turned on
			//but no vault is found it will warn the user
			String message = locale.getString("main.enable.noVault");
			if(message!=null){
				getLogger().severe(message);
			}
			else
				getLogger().severe("No Vault dependency found!");
		}

		//GameLoop stuff
		gameloop = new GameLoop(this);
		gameloop.runTaskTimer(this, 200, 2*60*20);

		//Logging to console the correct enabling of Hotels
		String message = locale.getString("main.enable.success");
		if(message!=null){
			getLogger().info(locale.getString("main.enable.success").replaceAll("%pluginname%", pdfFile.getName()).replaceAll("%version%", pdfFile.getVersion()));
		}
		else
			getLogger().info(pdfFile.getName()+" v"+pdfFile.getVersion()+ " has been enabled correctly");
		//Metrics
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}
	}
	@Override
	public void onDisable(){
		gameloop.cancel();

		PluginDescriptionFile pdfFile = this.getDescription();
		File lfile = new File("plugins//Hotels//locale.yml");
		YamlConfiguration locale = YamlConfiguration.loadConfiguration(lfile);
		//Logging to console the disabling of Hotels
		String message = locale.getString("main.disable.success");
		if(message!=null){
			getLogger().info(locale.getString("main.disable.success").replaceAll("%version%", pdfFile.getVersion()));
		}
		else
			getLogger().info(pdfFile.getName() + " v" + pdfFile.getVersion() + " has been disabled");
	}

	@Override
	public void onLoad(){
		setupConfig();
		setupEconomy();
		PluginDescriptionFile pdfFile = this.getDescription();
		File lfile = new File("plugins//Hotels//locale.yml");
		YamlConfiguration locale = YamlConfiguration.loadConfiguration(lfile);
		//Economy and stuff
		if (!setupEconomy()){
			//If economy is turned on
			//but no vault is found it will warn the user
			String message = locale.getString("main.enable.noVault");
			if(message!=null){
				getLogger().severe(message.replaceAll("%pluginname%", pdfFile.getName()));
			}
			else
				getLogger().severe(pdfFile.getName() + " No Vault dependency found!");
		}
	}

	//Setting up config files
	private void setupConfig(){
		hconfigh.setupConfigs(this);//Creates config file
	}

	//Setting up the economy
	private boolean setupEconomy()
	{RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	if (economyProvider != null) {
		economy = economyProvider.getProvider();
	}
	return (economy != null);
	}
}
