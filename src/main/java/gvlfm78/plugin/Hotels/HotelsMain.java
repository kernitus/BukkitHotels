package kernitus.plugin.Hotels;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import kernitus.plugin.Hotels.Metrics.Graph;
import kernitus.plugin.Hotels.handlers.HotelsCommandExecutor;
import kernitus.plugin.Hotels.handlers.HotelsCommandHandler;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.tasks.HotelTask;
import kernitus.plugin.Hotels.tasks.RoomTask;
import net.milkbowl.vault.economy.Economy;

public class HotelsMain extends JavaPlugin{

	public static Economy economy = null; //Creating economy variable

	HotelsConfigHandler HCH = new HotelsConfigHandler(this);
	HotelsCommandExecutor HCE = new HotelsCommandExecutor(this);
	FileConfiguration config = getConfig();
	Logger log = getServer().getLogger();

	YamlConfiguration queue = HCH.getMessageQueue();

	@Override
	public void onEnable(){
		setupConfig();

		PluginDescriptionFile pdfFile = this.getDescription();
		//Listeners and stuff
		getServer().getPluginManager().registerEvents((new HotelsListener(this)), this);//Firing event listener
		getCommand("Hotels").setExecutor(new HotelsCommandHandler(this));//Firing commands listener
		setupEconomy();
		//Economy and stuff
		if (!setupEconomy()){
			//If economy is turned on
			//but no vault is found it will warn the user
			String message = Mes.mesnopre("main.enable.noVault");
			if(message != null)
				getLogger().severe(message);
			else
				getLogger().severe("No Vault dependency found!");
		}
		
		//HotelsLoop stuff
		RoomTask roomTask = new RoomTask(this);
		int roomMins = this.getConfig().getInt("settings.roomTaskTimerMinutes");//TODO Add this to config

		boolean isRoomRunning;
		try{
			isRoomRunning = Bukkit.getScheduler().isCurrentlyRunning(roomTask.getTaskId());
		}
		catch(Exception e5){
			isRoomRunning = false;
		}
		if(!isRoomRunning){
			if(roomMins<=0)
				roomMins = 2;

			roomTask.runTaskTimer(this, 200, roomMins*60*20);
		}
		
		HotelTask hotelTask = new HotelTask();
		int hotelMins = this.getConfig().getInt("settings.hotelTaskTimerMinutes");//TODO Add this to config

		boolean isLoopRunning;
		try{
			isLoopRunning = Bukkit.getScheduler().isCurrentlyRunning(roomTask.getTaskId());
		}
		catch(Exception e5){
			isLoopRunning = false;
		}
		if(!isLoopRunning){
			if(hotelMins<=0)
				hotelMins = 2;

			roomTask.runTaskTimer(this, 200, hotelMins*60*20);
		}


		//Logging to console the correct enabling of Hotels
		String message = Mes.mesnopre("main.enable.success");
		if(message!=null)
			getLogger().info(Mes.mesnopre("main.enable.success").replaceAll("%pluginname%", pdfFile.getName()).replaceAll("%version%", pdfFile.getVersion()));
		else
			getLogger().info(pdfFile.getName()+" v"+pdfFile.getVersion()+ " has been enabled correctly");
		//Metrics
		try {
			Metrics metrics = new Metrics(this);

			Graph hotelAmount = metrics.createGraph("Amount of Hotels");
			Graph language = metrics.createGraph("Language");
			int count = HotelsAPI.getHotelCount();

			//Hotel amount
			switch(count) {
			case 0:
				hotelAmount.addPlotter(new Metrics.Plotter("1-3") {
					@Override
					public int getValue() {
						return 0;
					}
				}); break;
			case 1: case 2: case 3:
				hotelAmount.addPlotter(new Metrics.Plotter("1-3") {
					@Override
					public int getValue() {
						return 1;
					}
				}); break;
			case 4: case 5:
				hotelAmount.addPlotter(new Metrics.Plotter("4-5") {
					@Override
					public int getValue() {
						return 2;
					}
				}); break;
			case 6: case 7: case 8: case 9: case 10:
				hotelAmount.addPlotter(new Metrics.Plotter("6-10") {
					@Override
					public int getValue() {
						return 3;
					}
				}); break;
			case 11: case 12: case 13: case 14: case 15:
				hotelAmount.addPlotter(new Metrics.Plotter("11-15") {
					@Override
					public int getValue() {
						return 4;
					}
				});
			case 16: case 17: case 18: case 19: case 20:
				hotelAmount.addPlotter(new Metrics.Plotter("16-20") {
					@Override
					public int getValue() {
						return 5;
					}
				}); break;
			default:
				hotelAmount.addPlotter(new Metrics.Plotter(">20") {
					@Override
					public int getValue() {
						return 6;
					}
				}); break;
			}

			//Languages
			switch(HCH.getLanguage()){
			case "en": case "enGB":
				language.addPlotter(new Metrics.Plotter("English") {
					@Override
					public int getValue() {
						return 1;
					}
				}); break;

			case "it": case "itIT":
				language.addPlotter(new Metrics.Plotter("Italian") {
					@Override
					public int getValue() {
						return 2;
					}
				}); break;
			case "fr": case "frFR":
				language.addPlotter(new Metrics.Plotter("French") {
					@Override
					public int getValue() {
						return 3;
					}
				}); break;
			case "ru": case "ruRU":
				language.addPlotter(new Metrics.Plotter("Russian") {
					@Override
					public int getValue() {
						return 4;
					}
				}); break;
			case "zhCN":
				language.addPlotter(new Metrics.Plotter("Simplified Chinese") {
					@Override
					public int getValue() {
						return 5;
					}
				}); break;
			case "zhTW":
				language.addPlotter(new Metrics.Plotter("Traditional Cninese") {
					@Override
					public int getValue() {
						return 6;
					}
				}); break;
			default:
				language.addPlotter(new Metrics.Plotter("Custom") {
					@Override
					public int getValue() {
						return 7;
					}
				}); break;
			}

			metrics.start();
		} catch (IOException e) { /*Failed to submit stats */ }

		//Checking for updates
		if(getConfig().getBoolean("checkForUpdates")){
			getServer().getPluginManager().registerEvents((new HotelsUpdateListener(this)), this);

			final HotelsMain plugin = this;
			
			Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable (){
				public void run(){
					HotelsUpdateChecker HUC = new HotelsUpdateChecker(plugin);
					HUC.sendUpdateMessages(getLogger());
				}
			},20L);
		}
	}
	@Override
	public void onDisable(){
		hotelsloop.cancel();

		PluginDescriptionFile pdfFile = this.getDescription();
		//Logging to console the disabling of Hotels
		String message = Mes.mesnopre("main.disable.success");
		if(message!=null)
			getLogger().info(Mes.mesnopre("main.disable.success").replaceAll("%pluginname%", pdfFile.getName()).replaceAll("%version%", pdfFile.getVersion()));
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
		HCH.setupConfigs();//Creates config file
	}

	//Setting up the economy
	private boolean setupEconomy(){
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
			economy = economyProvider.getProvider();
		return (economy != null);
	}
}
