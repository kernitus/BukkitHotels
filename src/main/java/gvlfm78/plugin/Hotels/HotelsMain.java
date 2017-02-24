package kernitus.plugin.Hotels;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import kernitus.plugin.Hotels.Metrics.Graph;
import kernitus.plugin.Hotels.handlers.HotelsCommandHandler;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.tasks.RoomTask;
import kernitus.plugin.Hotels.updateChecker.HotelsUpdateChecker;
import kernitus.plugin.Hotels.updateChecker.HotelsUpdateListener;
import net.milkbowl.vault.economy.Economy;

public class HotelsMain extends JavaPlugin{

	public static Economy economy = null; //Creating economy variable

	//Task loops
	RoomTask roomTask;

	Logger log = getServer().getLogger();

	@Override
	public void onEnable(){
		HotelsConfigHandler.initialise(this);

		PluginDescriptionFile pdfFile = this.getDescription();
		//Listeners and stuff
		getServer().getPluginManager().registerEvents((new HotelsListener(this)), this);//Firing event listener
		getCommand("Hotels").setExecutor(new HotelsCommandHandler(this));//Firing commands listener
		setupEconomy();
		//Economy and stuff
		if (!setupEconomy())
			//If economy is turned on
			//but no vault is found it will warn the user
			getLogger().severe(Mes.getStringNoPrefix("main.enable.noVault"));

		//Room sign checker and updater
		roomTask = new RoomTask(this);
		int roomMins = getConfig().getInt("settings.roomTaskTimerMinutes");

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

		//Logging to console the correct enabling of Hotels
		getLogger().info(Mes.getStringNoPrefix("main.enable.success").replaceAll("%pluginname%", pdfFile.getName()).replaceAll("%version%", pdfFile.getVersion()));
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
			switch(HotelsConfigHandler.getLanguage()){
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
			getServer().getPluginManager().registerEvents((new HotelsUpdateListener(this, this.getFile())), this);

			final HotelsMain plugin = this;

			Bukkit.getScheduler().runTaskLaterAsynchronously(this, new Runnable (){
				public void run(){
					HotelsUpdateChecker HUC = new HotelsUpdateChecker(plugin, plugin.getFile());
					HUC.sendUpdateMessages(getLogger());
				}
			},20L);
		}
	}
	@Override
	public void onDisable(){
		roomTask.cancel();

		PluginDescriptionFile pdfFile = this.getDescription();
		//Logging to console the disabling of Hotels
		getLogger().info(Mes.getStringNoPrefix("main.disable.success").replaceAll("%pluginname%", pdfFile.getName()).replaceAll("%version%", pdfFile.getVersion()));
	}

	@Override
	public void onLoad(){
		HotelsConfigHandler.initialise(this);
		setupEconomy();
	}

	//Setting up the economy
	private boolean setupEconomy(){
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
			economy = economyProvider.getProvider();
		return (economy != null);
	}
}
