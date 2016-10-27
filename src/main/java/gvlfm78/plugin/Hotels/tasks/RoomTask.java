package kernitus.plugin.Hotels.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.Room;
import kernitus.plugin.Hotels.events.RentExpiryEvent;
import kernitus.plugin.Hotels.events.RoomSignUpdateEvent;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;
import kernitus.plugin.Hotels.managers.HotelsFileFinder;
import kernitus.plugin.Hotels.managers.Mes;
import kernitus.plugin.Hotels.managers.SignManager;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

public class RoomTask extends BukkitRunnable {

	private HotelsMain plugin;
	private WorldGuardManager WGM;
	private HotelsConfigHandler HCH;

	public RoomTask(HotelsMain plugin){
		this.plugin = plugin;
		
		WGM = new WorldGuardManager();
		HCH = new HotelsConfigHandler(plugin);
	}

	@Override
	public void run() {

		ArrayList<String> fileslist = HotelsFileFinder.listFiles("plugins" + File.separator + "Hotels" + File.separator + "Signs");
		Room room = null;

		for(String fileName : fileslist){
			File file = HotelsConfigHandler.getFile("Signs" + File.separator + fileName);

			if(!fileName.matches("\\w+-\\d+.yml")){ file.delete(); }//Delete all non-room signs in folder

			Mes.debugConsole("Room sign getting checked: " + file.getName() + " Path: " + file.getAbsolutePath());

			//Room sign
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			String hotelName = config.getString("Sign.hotel");
			World world = Bukkit.getWorld(config.getString("Sign.location.world").toLowerCase());
			int roomNum = config.getInt("Sign.room");

			room = new Room(world, hotelName, roomNum); //Creating room object

			Block signBlock = room.getBlockAtSignLocation(); //Getting block at location where sign should be

			if(!room.isBlockAtSignLocationSign()){
				file.delete();//If block is not a sign, delete it
				plugin.getLogger().info(Mes.mesnopre("sign.delete.location").replaceAll("%filename%", file.getName())); 
				return; }

			Sign sign = (Sign) signBlock.getState(); //Getting sign object
			if(!hotelName.equalsIgnoreCase(ChatColor.stripColor(sign.getLine(0)))){//If hotelName on sign doesn't match that in config
				file.delete();
				plugin.getLogger().info(Mes.mesnopre("sign.delete.hotelName").replaceAll("%filename%", file.getName()));
				return; }

			String[] Line2parts = ChatColor.stripColor(sign.getLine(1)).split("\\s");
			int roomNumfromSign = Integer.valueOf(Line2parts[1].trim()); //Room Number
			if(roomNum!=roomNumfromSign){ //If roomNum on sign doesn't match that in config
				file.delete();
				plugin.getLogger().info(Mes.mesnopre("sign.delete.roomNum").replaceAll("%filename%", file.getName()));
				return; }

			//Room numbers match
			if(config.get("Sign.expiryDate")!=null){
				long expiryDate = room.getExpiryMinute();
				if(expiryDate==0){ return; }//Rent is permanent

				if(expiryDate > (System.currentTimeMillis()/1000/60)){//If rent has not expired, update time remaining on sign
					//Updating time remaining till expiry
					long currentMins = System.currentTimeMillis()/1000/60;
					//Time remaining
					long remainingTime = expiryDate-currentMins;
					String formattedRemainingTime = SignManager.TimeFormatter(remainingTime);
					RoomSignUpdateEvent rsue = new RoomSignUpdateEvent(room, sign, remainingTime, formattedRemainingTime);
					Bukkit.getPluginManager().callEvent(rsue); //Call Room Sign Update event
					if(rsue.isCancelled()){ return; } //If event has been cancelled return
					formattedRemainingTime = rsue.getFormattedRemainingTime(); //Getting FRT from event in case another plugin modified it

					sign.setLine(2, formattedRemainingTime);

					sign.update();
				}
				else{//Rent has expired
					ProtectedRegion region = room.getRegion();
					if(!room.isRented()){ return; } //Rent expired but there's no renter, something went wrong, just quit

					OfflinePlayer p = room.getRenter(); //Getting renter
					WGM.removeMember(p, region); //Removing renter as member of room region

					//Removing friends
					List<String> friendList = room.getFriendsList();
					for(String currentFriend : friendList){
						OfflinePlayer cf = Bukkit.getServer().getOfflinePlayer(UUID.fromString(currentFriend));
						WGM.removeMember(cf, region);
					}
					//If set in config, make room accessible to all players now that it is not rented
					WorldGuardManager.makeRoomAccessible(region);
					if(HotelsConfigHandler.getconfigyml().getBoolean("settings.stopOwnersEditingRentedRooms")){
						region.setFlag(DefaultFlag.BLOCK_BREAK, null);
						region.setFlag(DefaultFlag.BLOCK_PLACE, null);
						region.setPriority(1);
					}

					sign.setLine(3, ChatColor.GREEN + Mes.mesnopre("sign.vacant"));
					sign.update();
					
					plugin.getLogger().info(Mes.mesnopre("sign.rentExpiredConsole").replaceAll("%room%", String.valueOf(roomNum)).replaceAll("%hotel%", hotelName).replaceAll("%player%", p.getName()));
					if(p.isOnline()){
						Player op = (Player) p;
						op.sendMessage(Mes.mes("sign.rentExpiredPlayer").replaceAll("%room%", String.valueOf(roomNum)).replaceAll("%hotel%", hotelName));
					}
					else{ //Player is offline, place their expiry message in the message queue //TODO Make class for easy usage of message queue
						YamlConfiguration queue = HCH.getMessageQueue();
						if(!queue.contains("messages.expiry")){
							queue.createSection("messages.expiry");
							HCH.saveMessageQueue(queue);
						}
						Set<String> expiryMessages = queue.getConfigurationSection("messages.expiry").getKeys(false);
						int expiryMessagesSize = expiryMessages.size();
						String pathToPlace = "messages.expiry." + (expiryMessagesSize + 1);
						queue.set(pathToPlace + ".UUID", p.getUniqueId().toString());
						queue.set(pathToPlace  +".message", Mes.mes("sign.rentExpiredPlayer").replaceAll("%room%", String.valueOf(roomNum)).replaceAll("%hotel%", hotelName));
						HCH.saveMessageQueue(queue);
					}
					config.set("Sign.renter", null);
					config.set("Sign.timeRentedAt", null);
					config.set("Sign.expiryDate", null);
					config.set("Sign.friends", null);
					config.set("Sign.extended", null);
					config.set("Sign.userHome.x", null);
					config.set("Sign.userHome.y", null);
					config.set("Sign.userHome.z", null);
					try {
						config.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					//Resetting time on sign to default
					sign.setLine(2, SignManager.TimeFormatter(config.getLong("Sign.time")));
					sign.update();
					
					Bukkit.getPluginManager().callEvent(new RentExpiryEvent(room, p, friendList));
					
				}
			}
			else{
				config.set("Sign.renter", null);
				config.set("Sign.timeRentedAt", null);
				config.set("Sign.expiryDate", null);
				config.set("Sign.friends", null);
				config.set("Sign.extended", null);
				try {
					config.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				sign.setLine(3, ChatColor.GREEN + Mes.mesnopre("sign.vacant"));
				sign.setLine(2, SignManager.TimeFormatter(config.getLong("Sign.time")));
				sign.update();
			}
		}
		if(room!=null) room.getHotel().updateReceptionSigns();
	}
}