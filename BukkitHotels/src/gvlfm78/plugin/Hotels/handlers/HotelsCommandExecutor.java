package kernitus.plugin.Hotels.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.HotelsCreationMode;
import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.managers.HotelsLoop;
import kernitus.plugin.Hotels.managers.HotelsFileFinder;
import kernitus.plugin.Hotels.managers.HotelsMessageManager;
import kernitus.plugin.Hotels.managers.SignManager;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

public class HotelsCommandExecutor {

	private HotelsMain plugin;
	public HotelsCommandExecutor(HotelsMain instance)
	{
		this.plugin = instance;
	}

	HotelsMessageManager HMM = new HotelsMessageManager(plugin);
	SignManager SM = new SignManager(plugin);
	HotelsCreationMode HCM = new HotelsCreationMode(plugin);
	WorldGuardManager WGM = new WorldGuardManager(plugin);
	HotelsConfigHandler HConH = new HotelsConfigHandler(plugin);
	HotelsFileFinder HFF = new HotelsFileFinder(plugin);

	public void cmdCreate(Plugin plugin, Player p,String hotelName){//Hotel creation command{
		UUID playerUUID = p.getUniqueId();
		File file = HConH.getFile("Inventories"+File.separator+playerUUID+".yml");
		if(file.exists()){
			HCM.hotelSetup(hotelName, p, plugin);
		}
		else
			p.sendMessage(HMM.mes("chat.commands.create.fail"));
	}
	public void cmdCommandsAll(CommandSender s){
		s.sendMessage(HMM.mesnopre("chat.commands.commands.header"));
		s.sendMessage(HMM.mesnopre("chat.commands.commands.subheader"));
		s.sendMessage(HMM.mesnopre("chat.commands.commands.help"));

		s.sendMessage(HMM.mesnopre("chat.commands.commands.creationMode"));
		s.sendMessage(HMM.mesnopre("chat.commands.commands.create"));
		s.sendMessage(HMM.mesnopre("chat.commands.commands.room"));
		s.sendMessage(HMM.mesnopre("chat.commands.commands.renum"));
		s.sendMessage(HMM.mesnopre("chat.commands.commands.rename"));

		s.sendMessage(HMM.mesnopre("chat.commands.commands.sethome"));
		s.sendMessage(HMM.mesnopre("chat.commands.commands.home"));

		s.sendMessage(HMM.mesnopre("chat.commands.commands.check"));
		s.sendMessage(HMM.mesnopre("chat.commands.commands.list"));
		s.sendMessage(HMM.mesnopre("chat.commands.commands.rlist"));

		s.sendMessage(HMM.mesnopre("chat.commands.commands.friend"));
		s.sendMessage(HMM.mesnopre("chat.commands.commands.friendList"));
		
		s.sendMessage(HMM.mesnopre("chat.commands.commands.sellh"));
		s.sendMessage(HMM.mesnopre("chat.commands.commands.buyh"));

		s.sendMessage(HMM.mesnopre("chat.commands.commands.reload"));
		s.sendMessage(HMM.mesnopre("chat.commands.commands.remove"));
		s.sendMessage(HMM.mesnopre("chat.commands.commands.delete"));
		s.sendMessage(HMM.mesnopre("chat.commands.commands.delr"));

		s.sendMessage(HMM.mesnopre("chat.commands.commands.footer"));
	}
	public void cmdCommandsOnly(CommandSender s){
		s.sendMessage(HMM.mesnopre("chat.commands.commands.header"));
		s.sendMessage(HMM.mesnopre("chat.commands.commands.subheader"));
		s.sendMessage(HMM.mesnopre("chat.commands.commands.help"));

		if(HMM.hasPerm(s,"hotels.createmode"))
			s.sendMessage(HMM.mesnopre("chat.commands.commands.creationMode"));

		if(HMM.hasPerm(s,"hotels.create")){
			s.sendMessage(HMM.mesnopre("chat.commands.commands.create"));
			s.sendMessage(HMM.mesnopre("chat.commands.commands.room"));}

		if(HMM.hasPerm(s,"hotels.renumber"))
			s.sendMessage(HMM.mesnopre("chat.commands.commands.renum"));
		if(HMM.hasPerm(s,"hotels.rename"))
			s.sendMessage(HMM.mesnopre("chat.commands.commands.rename"));

		if(HMM.hasPerm(s, "hotels.sethome"))
			s.sendMessage(HMM.mesnopre("chat.commands.commands.sethome"));
		if(HMM.hasPerm(s, "hotels.home"))
			s.sendMessage(HMM.mesnopre("chat.commands.commands.home"));

		if(HMM.hasPerm(s,"hotels.check"))
			s.sendMessage(HMM.mesnopre("chat.commands.commands.check"));
		if(HMM.hasPerm(s,"hotels.list.hotels"))
			s.sendMessage(HMM.mesnopre("chat.commands.commands.list"));
		if(HMM.hasPerm(s,"hotels.list.rooms"))
			s.sendMessage(HMM.mesnopre("chat.commands.commands.rlist"));

		if(HMM.hasPerm(s,"hotels.friend")){
			s.sendMessage(HMM.mesnopre("chat.commands.commands.friend"));
			s.sendMessage(HMM.mesnopre("chat.commands.commands.friendList"));}

		if(HMM.hasPerm(s, "hotels.sell.room")){
			s.sendMessage(HMM.mesnopre("chat.commands.commands.sellh"));
			s.sendMessage(HMM.mesnopre("chat.commands.commands.buyh"));
		}
		
		if(HMM.hasPerm(s,"hotels.reload"))
			s.sendMessage(HMM.mesnopre("chat.commands.commands.reload"));

		if(HMM.hasPerm(s,"hotels.remove"))
			s.sendMessage(HMM.mesnopre("chat.commands.commands.remove"));
		if(HMM.hasPerm(s,"hotels.delete.rooms"))
			s.sendMessage(HMM.mesnopre("chat.commands.commands.delr"));
		if(HMM.hasPerm(s,"hotels.delete"))
			s.sendMessage(HMM.mesnopre("chat.commands.commands.delete"));

		s.sendMessage(HMM.mesnopre("chat.commands.commands.footer"));
	}
	public void cmdHelp1(CommandSender s){
		s.sendMessage(HMM.mesnopre("chat.commands.help.header"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.subheader"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page1.1"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page1.2"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page1.3"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page1.4"));
		s.sendMessage((HMM.mesnopre("chat.commands.help.prefooter")).replaceAll("%num%", "2"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.footer"));
	}
	public void cmdHelp2(CommandSender s){
		s.sendMessage(HMM.mesnopre("chat.commands.help.header"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.subheader"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page2.1"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page2.2"));
		s.sendMessage((HMM.mesnopre("chat.commands.help.prefooter")).replaceAll("%num%", "3"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.footer"));
	}
	public void cmdHelp3(CommandSender s){
		s.sendMessage(HMM.mesnopre("chat.commands.help.header"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.subheader"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page3.1"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page3.2"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page3.3"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page3.4"));
		s.sendMessage((HMM.mesnopre("chat.commands.help.prefooter")).replaceAll("%num%", "4"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.footer"));
	}
	public void cmdHelp4(CommandSender s){
		s.sendMessage(HMM.mesnopre("chat.commands.help.header"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.subheader"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page4.1"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page4.2"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page4.3"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page4.4"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page4.5"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page4.6"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page4.7"));
		s.sendMessage((HMM.mesnopre("chat.commands.help.prefooter")).replaceAll("%num%", "5"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.footer"));
	}
	public void cmdHelp5(CommandSender s){
		s.sendMessage(HMM.mesnopre("chat.commands.help.header"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.subheader"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page5.1"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page5.2"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page5.3"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page5.4"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page5.5"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page5.6"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page5.7"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.page5.8"));
		s.sendMessage((HMM.mesnopre("chat.commands.help.prefooter")).replaceAll("%num%", "1"));
		s.sendMessage(HMM.mesnopre("chat.commands.help.footer"));
	}

	public void cmdCreateModeEnter(Player p){
		HCM.checkFolder();
		if(!HCM.isInCreationMode(p.getUniqueId().toString())){
			HCM.saveInventory(p);
			HCM.giveItems(p);
			p.sendMessage(HMM.mes("chat.commands.creationMode.enter"));
		}
		else
			p.sendMessage(HMM.mes("chat.commands.creationMode.alreadyIn"));
	}
	public void cmdCreateModeExit(Player p){
		if(HCM.isInCreationMode(p.getUniqueId().toString())){
			p.sendMessage(HMM.mes("chat.commands.creationMode.exit"));
			HCM.loadInventory(p);
		}
		else
			p.sendMessage(HMM.mes("chat.commands.creationMode.notAlreadyIn"));
	}
	public void cmdCreateModeReset(Player p){
		HCM.resetInventoryFiles(p);
		p.sendMessage(HMM.mes("chat.commands.creationMode.reset"));
	}
	public void cmdReload(CommandSender s,Plugin pluginstance){
		HConH.reloadConfigs(pluginstance);
		s.sendMessage(HMM.mes("chat.commands.reload.success"));
	}
	public void cmdRent(CommandSender s,String hotelName, String roomNum){
		File signFile = HConH.getFile("Signs"+File.separator+hotelName+"-"+roomNum+".yml");
		if(signFile.exists()){
			YamlConfiguration signConfig = HConH.getyml(signFile);
			if(s instanceof Player){
				Player p = (Player) s;
				SM.rentRoom(signConfig,signFile,p,hotelName,roomNum);
			}
			else
				s.sendMessage(HMM.mes("chat.commands.rent.consoleRejected"));
		}
		else
			s.sendMessage(HMM.mes("chat.commands.rent.invalidData"));
	}
	public void cmdFriendAdd(CommandSender s, String hotel, String room, String friendName){
		File signFile = HConH.getFile("Signs"+File.separator+hotel+"-"+room+".yml");
		if(signFile.exists()){
			YamlConfiguration signConfig = YamlConfiguration.loadConfiguration(signFile);
			String renterUUID = signConfig.getString("Sign.renter");
			if(renterUUID!=null){
				Player pl = (Player) s;
				if(pl.getUniqueId().equals(UUID.fromString(renterUUID))){
					@SuppressWarnings("deprecation")
					OfflinePlayer friend = Bukkit.getServer().getOfflinePlayer(friendName);
					if(friend.hasPlayedBefore()){
						if(!pl.getUniqueId().equals(friend.getUniqueId())){
							//Adding player as region member
							World fromConfigWorld = Bukkit.getWorld(signConfig.getString("Sign.location.world"));
							String fromConfigRegionName = signConfig.getString("Sign.region");
							ProtectedRegion r = WGM.getRegion(fromConfigWorld, fromConfigRegionName);
							WGM.addMember(friend, r);
							//Adding player to config under friends list
							List<String> stringList = signConfig.getStringList("Sign.friends");
							stringList.add(friend.getUniqueId().toString());
							signConfig.set("Sign.friends", stringList);

							try {
								signConfig.save(signFile);
							} catch (IOException e) {
								e.printStackTrace();
							}
							//Friend /name/ added successfully
							s.sendMessage(HMM.mes("chat.commands.friend.addSuccess").replaceAll("%friend%", friend.getName()));
						}
						else
							s.sendMessage(HMM.mes("chat.commands.friend.addYourself"));
					}
					else
						s.sendMessage(HMM.mes("chat.commands.friend.nonExistant"));
				}
				else
					s.sendMessage(HMM.mes("chat.commands.friend.notRenter"));
			}
			else
				s.sendMessage(HMM.mes("chat.commands.friend.noRenter"));	
		}
		else
			s.sendMessage(HMM.mes("chat.commands.friend.wrongData"));
	}
	public void cmdFriendRemove(CommandSender s, String hotel, String room, String friendName){
		File signFile = HConH.getFile("Signs"+File.separator+hotel+"-"+room+".yml");
		if(signFile.exists()){
			YamlConfiguration signConfig = YamlConfiguration.loadConfiguration(signFile);
			String renterUUID = signConfig.getString("Sign.renter");
			Player pl = (Player) s;
			if(renterUUID!=null){
				if(pl.getUniqueId().equals(UUID.fromString(renterUUID))){
					@SuppressWarnings("deprecation")
					OfflinePlayer friend = Bukkit.getServer().getOfflinePlayer(friendName);
					if(signConfig.getStringList("Sign.friends").contains(friend.getUniqueId().toString())){
						//Removing player as region member
						World fromConfigWorld = Bukkit.getWorld(signConfig.getString("Sign.location.world"));
						String fromConfigRegionName = signConfig.getString("Sign.region");
						ProtectedRegion r = WGM.getRegion(fromConfigWorld, fromConfigRegionName);
						WGM.removeMember(friend, r);
						//Removing player from config under friends list
						List<String> stringList = signConfig.getStringList("Sign.friends");
						stringList.remove(friend.getUniqueId().toString());
						signConfig.set("Sign.friends", stringList);

						try {
							signConfig.save(signFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
						//Friend /name/ removed successfully
						s.sendMessage(HMM.mes("chat.commands.friend.removeSuccess").replaceAll("%friend%", friend.getName()));
					}
					else
						s.sendMessage(HMM.mes("chat.commands.friend.friendNotInList"));
				}
				else
					s.sendMessage(HMM.mes("chat.commands.friend.notRenter"));
			}
			else
				s.sendMessage(HMM.mes("chat.commands.friend.noRenter"));
		}
		else
			s.sendMessage(HMM.mes("chat.commands.friend.wrongData"));
	}
	public void cmdFriendList(CommandSender s, String hotel, String room){
		File signFile = HConH.getFile("Signs"+File.separator+hotel+"-"+room+".yml");
		if(signFile.exists()){
			YamlConfiguration signConfig = YamlConfiguration.loadConfiguration(signFile);
			String renterUUID = signConfig.getString("Sign.renter");
			Player pl = (Player) s;
			if(renterUUID!=null){
				if(pl.getUniqueId().equals(UUID.fromString(renterUUID))){
					List<String> stringList = signConfig.getStringList("Sign.friends");
					if(!stringList.isEmpty()){
						hotel = hotel.substring(0, 1).toUpperCase() + hotel.substring(1).toLowerCase();
						s.sendMessage(HMM.mes("chat.commands.friend.list.heading").replaceAll("%room%", room).replaceAll("%hotel%", hotel));
						for(String currentFriend : stringList){
							OfflinePlayer friend = Bukkit.getServer().getOfflinePlayer(UUID.fromString(currentFriend));
							String friendName = friend.getName();
							s.sendMessage(HMM.mes("chat.commands.friend.list.line").replaceAll("%name%", friendName));
						}
						s.sendMessage(HMM.mes("chat.commands.friend.list.footer"));
					}
					else
						s.sendMessage(HMM.mes("chat.commands.friend.noFriends"));	
				}
				else
					s.sendMessage(HMM.mes("chat.commands.friend.notRenter"));	
			}
			else
				s.sendMessage(HMM.mes("chat.commands.friend.noRenter"));
		}
		else
			s.sendMessage(HMM.mes("chat.commands.friend.wrongData"));
	}
	public void cmdRoomListPlayer(Player p, String hotel, World w){
		if(WGM.hasRegion(w, "hotel-"+hotel))
			listRooms(hotel,w,p);
		else
			p.sendMessage(HMM.mes("chat.commands.hotelNonExistant"));
	}
	public void cmdRoomListPlayer(CommandSender s, String hotel, World w){
		if(WGM.hasRegion(w, "hotel-"+hotel)){
			listRooms(hotel,w,s);
		}
		else
			s.sendMessage(HMM.mes("chat.commands.hotelNonExistant").replaceAll("(?i)&([a-fk-r0-9])", ""));
	}
	public void renumber(Plugin pluginstance, String hotel,String oldnum,String newnum, World world,CommandSender sender){
		hotel = hotel.toLowerCase();
		if(Integer.parseInt(newnum)<100000){
			if(WGM.hasRegion(world, "Hotel-"+hotel)){
				if(WGM.hasRegion(world, "Hotel-"+hotel+"-"+oldnum)){
					if(sender instanceof Player){
						Player p = (Player) sender;
						if(!WGM.isOwner(p, "hotel-"+hotel, p.getWorld()))
							if(!HMM.hasPerm(p, "hotels.renumber.admin")){
								p.sendMessage(HMM.mes("chat.commands.youDoNotOwnThat"));
								return;
							}
					}
					File file = HConH.getFile("Signs"+File.separator+hotel+"-"+oldnum+".yml");
					if(file.exists()){
						YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
						World signworld = Bukkit.getWorld(config.getString("Sign.location.world"));
						int signx = config.getInt("Sign.location.coords.x");
						int signy = config.getInt("Sign.location.coords.y");
						int signz = config.getInt("Sign.location.coords.z");
						Block b = signworld.getBlockAt(signx,signy,signz);

						if(world==signworld){
							if(b.getType().equals(Material.SIGN)||b.getType().equals(Material.SIGN_POST)||b.getType().equals(Material.WALL_SIGN)){
								Sign s = (Sign) b.getState();
								String Line1 = ChatColor.stripColor(s.getLine(0));
								String Line2 = ChatColor.stripColor(s.getLine(1));
								String signroom = Line2.split(" ")[1];
								if(Line1.toLowerCase().matches(hotel.toLowerCase())){
									if(WGM.hasRegion(signworld, "hotel-"+hotel)){
										if(WGM.getRegion(signworld, "hotel-"+hotel).contains(signx, signy, signz)){
											if(signroom.trim().toLowerCase().matches(oldnum.toLowerCase())){
												s.setLine(1, HMM.mesnopre("sign.room.name")+" "+newnum+" - "+Line2.split(" ")[3]);
												s.update();
												config.set("Sign.room", Integer.valueOf(newnum));
												config.set("Sign.region", "hotel-"+hotel+"-"+newnum);
												try {
													config.save(file);
												} catch (IOException e) {
													e.printStackTrace();
												}
												File newfile = HConH.getFile("Signs"+File.separator+hotel+"-"+newnum+".yml");
												file.renameTo(newfile);
											}
											else{
												b.setType(Material.AIR);
												file.delete();
											}
										}
										else{
											b.setType(Material.AIR);
											file.delete();
										}
									}
									else{
										b.setType(Material.AIR);
										file.delete();
									}
								}
								else{
									b.setType(Material.AIR);
									file.delete();
								}
							}
							else{
								b.setType(Material.AIR);
								file.delete();
							}
						}
						else{
							b.setType(Material.AIR);
							file.delete();
						}
					}
					ProtectedRegion r = WGM.getRegion(world, "hotel-"+hotel+"-"+oldnum);
					String idHotelName = r.getId();
					String[] partsofhotelName = idHotelName.split("-");
					String fromIdhotelName = partsofhotelName[1].substring(0, 1).toUpperCase() + partsofhotelName[1].substring(1).toLowerCase();
					if(HMM.flagValue("room.map-making.GREETING").equalsIgnoreCase("true"))
						r.setFlag(DefaultFlag.GREET_MESSAGE, (HMM.mesnopre("message.room.enter").replaceAll("%room%", String.valueOf(newnum))));
					if(HMM.flagValue("room.map-making.FAREWELL").equalsIgnoreCase("true"))
						r.setFlag(DefaultFlag.FAREWELL_MESSAGE, (HMM.mesnopre("message.room.exit").replaceAll("%room%", String.valueOf(newnum))));
					WGM.renameRegion("Hotel-"+hotel+"-"+oldnum, "Hotel-"+hotel+"-"+newnum, world);
					try {
						WGM.getRM(world).save();
						sender.sendMessage(HMM.mes("chat.commands.renumber.success").replaceAll("%oldnum%", oldnum).replaceAll("%newnum%", newnum).replaceAll("%hotel%", fromIdhotelName));
					} catch (StorageException e) {
						sender.sendMessage(HMM.mes("chat.commands.renumber.fail").replaceAll("%oldnum%", oldnum));
						e.printStackTrace();
					}
				}
				else
					sender.sendMessage(HMM.mes("chat.commands.roomNonExistant"));
			}
			else
				sender.sendMessage(HMM.mes("chat.commands.hotelNonExistant"));
		}
		else
			sender.sendMessage(HMM.mes("chat.commands.renumber.newNumTooBig"));
	}

	public void renameHotel(String oldname,String newname, World world,CommandSender sender){
		oldname = oldname.toLowerCase();
		newname = newname.toLowerCase();
		if(WGM.hasRegion(world, "hotel-"+oldname)){
			if(sender instanceof Player){
				Player p = (Player) sender;
				if(!WGM.isOwner(p, "hotel-"+oldname, p.getWorld()))
					if(!HMM.hasPerm(p, "hotels.rename.admin")){
						p.sendMessage(HMM.mes("chat.commands.youDoNotOwnThat"));
						return;
					}
			}
			WGM.renameRegion("hotel-"+oldname, "hotel-"+newname, world);
			ProtectedRegion r = WGM.getRegion(world, "hotel-"+newname);
			String idHotelName = r.getId();
			String[] partsofhotelName = idHotelName.split("-");
			String fromIdhotelName = partsofhotelName[1].substring(0, 1).toUpperCase() + partsofhotelName[1].substring(1).toLowerCase();
			if(HMM.flagValue("hotel.map-making.GREETING").equalsIgnoreCase("true"))
				r.setFlag(DefaultFlag.GREET_MESSAGE, (HMM.mesnopre("message.hotel.enter").replaceAll("%hotel%", fromIdhotelName)));
			if(HMM.flagValue("hotel.map-making.FAREWELL")!=null)
				r.setFlag(DefaultFlag.FAREWELL_MESSAGE, (HMM.mesnopre("message.hotel.exit").replaceAll("%hotel%", fromIdhotelName)));
			sender.sendMessage(HMM.mes("chat.commands.rename.success").replaceAll("%hotel%" , fromIdhotelName));
			//Rename rooms
			Map<String, ProtectedRegion> regionlist = WGM.getRM(world).getRegions();

			for(ProtectedRegion region : regionlist.values()){
				String regionId = region.getId();
				if(regionId.matches("hotel-"+oldname+"-"+"[0-9]+")){
					String regionIdparts[] = regionId.split("-");
					WGM.renameRegion(regionId, "Hotel-"+newname+"-"+regionIdparts[2], world);
					//Rename sign file
					File file = HConH.getFile("Signs"+File.separator+regionIdparts[1]+"-"+regionIdparts[2]+".yml");
					if(file.exists()){
						YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
						World signworld = Bukkit.getWorld(config.getString("Sign.location.world").trim());
						int signx = config.getInt("Sign.location.coords.x");
						int signy = config.getInt("Sign.location.coords.y");
						int signz = config.getInt("Sign.location.coords.z");
						Block b = signworld.getBlockAt(signx,signy,signz);
						if(b.getType().equals(Material.SIGN)||b.getType().equals(Material.SIGN_POST)||b.getType().equals(Material.WALL_SIGN)){
							Sign s = (Sign) b.getState();
							String Line1 = ChatColor.stripColor(s.getLine(0));
							if(Line1.toLowerCase().matches(oldname.toLowerCase())){
								if(WGM.getRegion(signworld, "hotel-"+newname).contains(signx, signy, signz)){
									s.setLine(0, ChatColor.DARK_BLUE+newname);
									s.update();
									config.set("Sign.hotel", newname);
									config.set("Sign.region", "hotel-"+newname+"-"+regionIdparts[2]);
									try {
										config.save(file);
									} catch (IOException e) {
										e.printStackTrace();
									}
									File newfile = HConH.getFile("Signs"+File.separator+newname.toLowerCase()+"-"+regionIdparts[2]+".yml");
									file.renameTo(newfile);

									//Renaming
									File hotelsFile = HConH.getFile("Hotels"+File.separator+oldname.toLowerCase()+".yml");
									File newHotelsfile = HConH.getFile("Hotels"+File.separator+newname.toLowerCase()+".yml");
									hotelsFile.renameTo(newHotelsfile);
								}
							}
						}
					}
				}
				try {
					WGM.getRM(world).save();
				} catch (StorageException e) {
					sender.sendMessage(HMM.mes("chat.commands.rename.failRooms"));
					e.printStackTrace();
				}
			}
		}
		else
			sender.sendMessage(HMM.mes("chat.commands.hotelNonExistant"));
	}
	public void removeRoom(String hotelName,String roomNum,World world,CommandSender sender){
		if(WGM.hasRegion(world, "Hotel-"+hotelName+"-"+roomNum)){//If region exists
			WGM.getRM(world).removeRegion("Hotel-"+hotelName+"-"+roomNum);//Delete region
			try {
				WGM.getRM(world).save();
				File file = HConH.getFile("Signs"+File.separator+hotelName+"-"+roomNum+".yml");
				if(file.exists())
					file.delete();
				sender.sendMessage(HMM.mes("chat.commands.removeRoom.success"));
			} catch (StorageException e) {
				sender.sendMessage(HMM.mes("chat.commands.removeRoom.fail"));
				e.printStackTrace();
			}

		}
	}
	public void removeRegions(String hotelName,World world,CommandSender sender){
		if(WGM.hasRegion(world, "Hotel-"+hotelName)){
			WGM.getRM(world).removeRegion("Hotel-"+hotelName);
			Map<String, ProtectedRegion> regionlist = WGM.getRM(world).getRegions();

			for(ProtectedRegion values : regionlist.values()){
				if(values.getId().matches("hotel-"+hotelName+"-"+"[0-9]+")){
					ProtectedRegion goodregion = values;
					WGM.getRM(world).removeRegion(goodregion.getId());
				}
			}

			try {
				WGM.getRM(world).save();
				sender.sendMessage(HMM.mes("chat.commands.removeRegions.success"));
			} catch (StorageException e) {
				sender.sendMessage(HMM.mes("chat.commands.removeRegions.fail"));
				e.printStackTrace();
			}
		}
		else{
			if(sender instanceof Player)
				sender.sendMessage(HMM.mes("chat.commands.hotelNonExistant"));
			else
				sender.sendMessage(HMM.mes("chat.commands.hotelNonExistant").replaceAll("(?i)&([a-fk-r0-9])", ""));
		}
	}
	public void removePlayer(World w, String hotel, String room,String toRemovePlayer,CommandSender sender){
		if(w!=null){
			if(WGM.hasRegion(w, "hotel-"+hotel)){
				if(WGM.hasRegion(w, "hotel-"+hotel+"-"+room)){
					@SuppressWarnings("deprecation")
					Player player = Bukkit.getOfflinePlayer(toRemovePlayer).getPlayer();
					if(player!=null){
						File file = HConH.getFile("Signs"+File.separator+hotel+"-"+room+".yml");
						YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
						String renter = config.getString("Sign.renter");
						if(renter!=null){
							Player pfromfile = Bukkit.getOfflinePlayer(UUID.fromString(renter)).getPlayer();
							if(player.equals(pfromfile)){
								ProtectedRegion r = WGM.getRM(w).getRegion("hotel-"+hotel+"-"+room);
								WGM.removeMember(player, r);
								r.setPriority(1);
								//Config stuff
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
								//Hotelsloop
								HotelsLoop hotelsloop = new HotelsLoop(plugin);
								hotelsloop.run();
								//Make free room accessible to all players if set in config
								WGM.makeRoomAccessible(r);
								sender.sendMessage(HMM.mes("chat.commands.remove.success").replaceAll("%player%", player.getName()).replaceAll("%room%", room).replaceAll("%hotel%", hotel));
							}
							else
								sender.sendMessage(HMM.mes("chat.commands.remove.playerNotRenter"));	
						}
						else
							sender.sendMessage(HMM.mes("chat.commands.remove.noRenter"));
					}
					else
						sender.sendMessage(HMM.mes("chat.commands.userNonExistant"));
				}
				else
					sender.sendMessage(HMM.mes("chat.commands.roomNonExistant"));
			}
			else
				sender.sendMessage(HMM.mes("chat.commands.hotelNonExistant"));
		}
		else
			sender.sendMessage(HMM.mes("chat.commands.worldNonExistant"));
	}
	public void check(String playername, CommandSender sender){
		Map<String, ProtectedRegion> regions = new HashMap<String, ProtectedRegion>();
		List<World> worlds = Bukkit.getWorlds();
		Map<ProtectedRegion,World> hotels = new HashMap<ProtectedRegion,World>();
		List<ProtectedRegion> rooms = new ArrayList<ProtectedRegion>();
		@SuppressWarnings("deprecation")
		OfflinePlayer p = Bukkit.getOfflinePlayer(playername);
		if(p!=null&&p.hasPlayedBefore()){

			for(World w:worlds){//Looping through all the regions in all the worlds & separating rooms from hotels
				regions = WGM.getRM(w).getRegions();
				ProtectedRegion[] rlist = regions.values().toArray(new ProtectedRegion[regions.size()]);
				if(rlist.length>0){
					for(ProtectedRegion r:rlist){
						if(r.getId().toLowerCase().startsWith("hotel-")){ //If it's a hotel
							if(r.getId().toLowerCase().matches("^hotel-.+-.+")){//If it's a room
								if(r.getMembers().contains(WGM.getWorldGuard().wrapOfflinePlayer(p)))//They are the renter
									rooms.add(r);//Add to hotels list
							}
							else{
								if(r.getOwners().contains(WGM.getWorldGuard().wrapOfflinePlayer(p)))//They are the owner
									hotels.put(r,w);//Add to rooms list
							}
						}
					}
				}
			}
			//Printing out owned hotels first
			sender.sendMessage(HMM.mes("chat.commands.check.headerHotels").replaceAll("%player%", playername));
			if(hotels.size()>0){
				for(ProtectedRegion hr:hotels.keySet()){
					String[] rId = hr.getId().toLowerCase().split("-");
					String hotelName = rId[1];

					//String hotelName = hr.getId().replaceFirst("hotel-", "");
					//hotelName = hotelName.replaceFirst("-\\d+", "");
					World world = hotels.get(hr);
					int total = SM.totalRooms(hotelName, world);
					int free = SM.freeRooms(hotelName, world);
					sender.sendMessage(HMM.mes("chat.commands.check.lineHotels")
							.replaceAll("%player%", playername)
							.replaceAll("%hotel%", hotelName)
							.replaceAll("%total%", String.valueOf(total))
							.replaceAll("%free%", String.valueOf(free))
							);
				}
			}
			else
				sender.sendMessage(HMM.mes("chat.commands.check.noHotels"));

			//And printing out rented rooms
			sender.sendMessage(HMM.mes("chat.commands.check.headerRooms").replaceAll("%player%", playername));
			if(rooms.size()>0){
				for(ProtectedRegion r:rooms){//looping through rented rooms
					String[] rId = r.getId().toLowerCase().split("-");
					String hotelName = rId[1];
					String roomNum = rId[2];

					File file = HConH.getFile("Signs"+File.separator+hotelName+"-"+roomNum+".yml");
					YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
					long expiryDate = config.getLong("Sign.expiryDate");

					if(expiryDate>0){
						long currentmins = System.currentTimeMillis()/1000/60;
						String timeleft = SM.TimeFormatter(expiryDate-currentmins);
						sender.sendMessage(HMM.mes("chat.commands.check.lineRooms")
								.replaceAll("%hotel%", hotelName).replaceAll("%room%", roomNum).replaceAll("%timeleft%", String.valueOf(timeleft)));
					}
					else//Room is permanently rented
						sender.sendMessage(HMM.mes("chat.commands.check.lineRooms")
								.replaceAll("%hotel%", hotelName).replaceAll("%room%", roomNum).replaceAll("%timeleft%", HMM.mesnopre("sign.permanent")));
				}
			}
			else
				sender.sendMessage(HMM.mes("chat.commands.check.noRooms"));
		}
		else
			sender.sendMessage(HMM.mes("chat.commands.userNonExistant"));
	}
	public void listHotels(World w, CommandSender sender){
		sender.sendMessage(HMM.mes("chat.commands.listHotels.heading"));
		Map<String, ProtectedRegion> regions = new HashMap<String, ProtectedRegion>();
		regions = WGM.getRM(w).getRegions();
		ProtectedRegion[] rlist = regions.values().toArray(new ProtectedRegion[regions.size()]);
		for(ProtectedRegion r:rlist){
			String id = r.getId();
			if(id.startsWith("hotel-")){ //If it's a hotel
				if(!id.matches("^hotel-.+-.+")){ //if it's not a room
					String hotelName = (id.replaceFirst("hotel-", "")).toLowerCase();
					hotelName = hotelName.substring(0, 1).toUpperCase() + hotelName.substring(1).toLowerCase();
					int spaceamount = 10-hotelName.length();
					String space = " ";
					String rep = StringUtils.repeat(space, spaceamount);
					sender.sendMessage(HMM.mes("chat.commands.listHotels.line").replaceAll("%hotel%", hotelName)
							.replaceAll("%total%", String.valueOf(SM.totalRooms(hotelName, w)))
							.replaceAll("%free%", String.valueOf(SM.freeRooms(hotelName, w)))
							.replaceAll("%space%", rep)
							);
				}
			}
		}
	}
	public void listRooms(String hotel, World w, CommandSender sender){
		String hotelName = hotel.substring(0, 1).toUpperCase() + hotel.substring(1).toLowerCase();
		sender.sendMessage(HMM.mes("chat.commands.listRooms.heading").replaceAll("%hotel%", hotelName));
		Map<String, ProtectedRegion> regions = WGM.getRM(w).getRegions();
		boolean roomsFound = false;
		for(ProtectedRegion r : regions.values()){
			String id = r.getId();
			if(id.startsWith("hotel-")){ //If it's a hotel
				if(id.matches("^hotel-"+hotel.toLowerCase()+"-.+")){ //If it's a room of the specified hotel
					String roomnum = (id.replaceAll("hotel-.+-", ""));
					int spaceamount = 10-roomnum.length();
					String space = " ";
					String rep = StringUtils.repeat(space, spaceamount);
					File file = HConH.getFile("Signs"+File.separator+hotel.toLowerCase()+"-"+roomnum+".yml");
					YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
					String state = "";
					if(config!=null){
						String renter = config.getString("Sign.renter");
						if(renter==null){
							//Vacant
							state = ChatColor.GREEN+HMM.mesnopre("sign.vacant");
						}
						else{
							//Occupied
							state = ChatColor.BLUE+HMM.mesnopre("sign.occupied");
						}
						sender.sendMessage(HMM.mes("chat.commands.listRooms.line")
								.replaceAll("%room%", roomnum)
								.replaceAll("%state%", state)
								.replaceAll("%space%", rep)
								);
						roomsFound = true;
					}
				}
			}
		}
		if(roomsFound==false)
			sender.sendMessage(HMM.mes("chat.commands.listRooms.noRooms"));
	}
	public void removeSigns(String hotelName,World world,CommandSender sender){
		if(WGM.hasRegion(world, "Hotel-"+hotelName)){
			ArrayList<String> fileslist = HFF.listFiles("plugins//Hotels//Signs");
			for(String x: fileslist){
				File file = HConH.getFile("Signs"+File.separator+x);
				String receptionLoc = HMM.mesnopre("sign.reception");
				if(file.getName().matches("^"+receptionLoc+"-.+-.+")){
					YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
					World worldsign = Bukkit.getWorld(config.getString("Reception.location.world").trim());
					int locx = config.getInt("Reception.location.x");
					int locy = config.getInt("Reception.location.y");
					int locz = config.getInt("Reception.location.z");
					Block b = worldsign.getBlockAt(locx,locy,locz);
					if(world==worldsign){
						if(b.getType().equals(Material.SIGN)||b.getType().equals(Material.SIGN_POST)||b.getType().equals(Material.WALL_SIGN)){
							Sign s = (Sign) b.getState();
							String Line1 = ChatColor.stripColor(s.getLine(0));
							String Line2 = ChatColor.stripColor(s.getLine(1));
							if(Line1.matches("Reception")){
								String[] Line1split = Line2.split(" ");
								String hotelname = Line1split[0];
								if(WGM.hasRegion(worldsign, "Hotel-"+hotelname)){
									if(WGM.getRM(worldsign).getRegion("Hotel-"+hotelname).contains(locx, locy, locz)){
										b.setType(Material.AIR);
										file.delete();
									}
									else{
										b.setType(Material.AIR);
										file.delete();
									}
								}
								else{
									b.setType(Material.AIR);
									file.delete();
								}
							}
							else
								file.delete();
						}
						else
							file.delete();
					}
				}else{
					String[] parts = x.split("-");
					String chotelName = parts[0];
					if(chotelName.equalsIgnoreCase(hotelName)){
						YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
						int locx = config.getInt("Sign.location.coords.x");
						int locy = config.getInt("Sign.location.coords.y");
						int locz = config.getInt("Sign.location.coords.z");
						Block signblock = world.getBlockAt(locx, locy, locz);
						signblock.setType(Material.AIR);
						signblock.breakNaturally();
						file.delete();
					}
				}
			}
			sender.sendMessage(HMM.mes("chat.commands.removeSigns.success"));
		}
	}
	public int nextNewRoom(World w, String hotel){
		if(WGM.hasRegion(w, "Hotel-"+hotel)){
			Map<String, ProtectedRegion> regions = new HashMap<String, ProtectedRegion>();
			regions = WGM.getRM(w).getRegions();
			for(int i=0; i<regions.size(); i++){
				if(!WGM.hasRegion(w, "Hotel-"+hotel+"-"+(i+1)))
					return i+1;
			}
		}
		else
			return 0;
		return 0;
	}
}
