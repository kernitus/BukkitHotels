package kernitus.plugin.Hotels;

import kernitus.plugin.Hotels.Metrics.Graph;
import kernitus.plugin.Hotels.handlers.HotelsCommandExecutor;
import kernitus.plugin.Hotels.handlers.HotelsCommandHandler;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.HotelsLoop;
import kernitus.plugin.Hotels.managers.HotelsMessageManager;
import kernitus.plugin.Hotels.managers.HotelsUpdateLoop;

import java.io.IOException;
import java.util.Collection;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HotelsMain extends JavaPlugin{

	public static Economy economy = null; //Creating economy variable

	HotelsConfigHandler HConH = new HotelsConfigHandler(this);
	HotelsCommandExecutor HCE = new HotelsCommandExecutor(this);
	HotelsMessageManager HMM = new HotelsMessageManager(this);
	HotelsLoop hotelsloop;
	protected HotelsUpdateChecker updateChecker;

	YamlConfiguration queue = HConH.getMessageQueue();

	@Override
	public void onEnable(){
		setupConfig();
		this.updateChecker = new HotelsUpdateChecker(this, "http://dev.bukkit.org/bukkit-plugins/hotels/files.rss");
		this.updateChecker.updateNeeded();
		if(getConfig().getBoolean("settings.checkForUpdates")){
			if(this.updateChecker.updateNeeded()){
				String updateAvailable = HMM.mesnopre("main.updateAvailable").replaceAll("%version%", this.updateChecker.getVersion());
				String updateLink = HMM.mesnopre("main.updateAvailableLink").replaceAll("%link%", this.updateChecker.getLink());
				getLogger().info(updateAvailable);
				getLogger().info(updateLink);

				queue.set("messages.update.available", updateAvailable);
				queue.set("messages.update.link", updateLink);
				HConH.saveMessageQueue(queue);

				Collection<? extends Player> players = getServer().getOnlinePlayers();
				for(Player p:players){
					if(p.isOp()||p.hasPermission("hotels.*")){
						p.sendMessage(ChatColor.BLUE+updateAvailable);
						p.sendMessage(ChatColor.BLUE+updateLink);
					}
				}
			}
		}
		PluginDescriptionFile pdfFile = this.getDescription();
		//Listeners and stuff
		getServer().getPluginManager().registerEvents((new HotelsListener(this)), this);//Firing event listener
		getCommand("Hotels").setExecutor(new HotelsCommandHandler(this));//Firing commands listener
		setupEconomy();
		//Economy and stuff
		if (!setupEconomy()){
			//If economy is turned on
			//but no vault is found it will warn the user
			String message = HMM.mesnopre("main.enable.noVault");
			if(message!=null){
				getLogger().severe(message);
			}
			else
				getLogger().severe("No Vault dependency found!");
		}

		//HotelsLoop stuff
		hotelsloop = new HotelsLoop(this);
		int minutes = this.getConfig().getInt("settings.hotelsLoopTimerMinutes");

		boolean isLoopRunning;
		try{
			isLoopRunning = Bukkit.getScheduler().isCurrentlyRunning(hotelsloop.getTaskId());
		}
		catch(Exception e5){
			isLoopRunning = false;
		}
		if(!isLoopRunning){
			if(minutes>0)
				hotelsloop.runTaskTimer(this, 200, minutes*60*20);
			else
				hotelsloop.runTaskTimer(this, 200, 2*60*20);
		}

		//HotelsUpdateLoop stuff
		HotelsUpdateLoop HUL = new HotelsUpdateLoop(this);
		int hours = this.getConfig().getInt("settings.updateTime");
		boolean isUpdateCheckRunning;
		try{
			isUpdateCheckRunning = Bukkit.getScheduler().isCurrentlyRunning(HUL.getTaskId());
		}
		catch(Exception e5){
			isUpdateCheckRunning = false;
		}

		if(!isUpdateCheckRunning){//If the update checker is not running
			if(hours>0)
				HUL.runTaskTimerAsynchronously(this, 3*60*60*20, hours*60*60*20);
			else
				HUL.runTaskTimerAsynchronously(this, 3*60*60*20, 6*60*60*20);
		}
		//Logging to console the correct enabling of Hotels
		String message = HMM.mesnopre("main.enable.success");
		if(message!=null){
			getLogger().info(HMM.mesnopre("main.enable.success").replaceAll("%pluginname%", pdfFile.getName()).replaceAll("%version%", pdfFile.getVersion()));
		}
		else
			getLogger().info(pdfFile.getName()+" v"+pdfFile.getVersion()+ " has been enabled correctly");
		//Metrics
		try {
			Metrics metrics = new Metrics(this);

			Graph hotelAmount = metrics.createGraph("Amount of Hotels");

			hotelAmount.addPlotter(new Metrics.Plotter("Hotel Count") {

				@Override
				public int getValue() {
					return HCE.getHotelCount(); // Number of players who used a diamond sword
				}

			});

			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}
	}
	@Override
	public void onDisable(){
		hotelsloop.cancel();

		PluginDescriptionFile pdfFile = this.getDescription();
		//Logging to console the disabling of Hotels
		String message = HMM.mesnopre("main.disable.success");
		if(message!=null){
			getLogger().info(HMM.mesnopre("main.disable.success").replaceAll("%pluginname%", pdfFile.getName()).replaceAll("%version%", pdfFile.getVersion()));
		}
		else
			getLogger().info(pdfFile.getName() + " v" + pdfFile.getVersion() + " has been disabled");
	}

	@Override
	public void onLoad(){
		setupConfig();
		setupEconomy();
	}

	//Setting up config files
	private void setupConfig(){
		HConH.setupConfigs(this);//Creates config file
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
