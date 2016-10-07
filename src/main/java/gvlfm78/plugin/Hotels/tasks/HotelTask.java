package kernitus.plugin.Hotels.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import kernitus.plugin.Hotels.Hotel;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.HotelsFileFinder;
import kernitus.plugin.Hotels.managers.Mes;

public class HotelTask extends BukkitRunnable{

	@Override
	public void run(){
		//Hotel files
		
		ArrayList<String> fileList = HotelsFileFinder.listFiles("plugins" + File.separator + "Hotels" + File.separator + "Hotels");
		for(String fileName : fileList){
			File file = HotelsConfigHandler.getFile("Hotels" + File.separator + fileName);
			YamlConfiguration hconf = YamlConfiguration.loadConfiguration(file);
			String buyeruuid = hconf.getString("Hotel.sell.buyer");
			if(buyeruuid != null){
				String hotelName = file.getName().replaceFirst(".yml", "");
				Hotel hotel = new Hotel(hotelName);

				//Messaging the buyer
				OfflinePlayer buyer = Bukkit.getOfflinePlayer(UUID.fromString(buyeruuid));
				if(buyer.isOnline()){
					Player onlineBuyer = (Player) buyer;
					onlineBuyer.sendMessage(Mes.mes("hotels.commands.buyhotel.expired").replaceAll("%hotel%", hotelName));
				}

				//Messaging the seller
				for(UUID ownerUUID : hotel.getOwners().getUniqueIds()){
					OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerUUID);
					if(owner.isOnline()){
						Player player = (Player) owner;
						player.sendMessage(Mes.mes("hotels.commands.sellhotel.expired").replaceAll("%hotel%", hotelName));
					}
				}
				hotel.setBuyer(null);
				hotel.removePrice();
				hotel.saveHotelConfig();
			}
		}
	}

}
