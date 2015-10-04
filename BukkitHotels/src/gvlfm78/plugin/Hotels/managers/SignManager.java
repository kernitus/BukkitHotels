package kernitus.plugin.Hotels.managers;

import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class SignManager {

	FilenameFilter SignFileFilter;
	public HotelsMain plugin;
	public SignManager(HotelsMain instance){
		this.plugin = instance;
	}
	HotelsFileFinder HFF = new HotelsFileFinder(plugin);
	WorldGuardManager WGM = new WorldGuardManager(plugin);
	HotelsConfigHandler HConH = new HotelsConfigHandler(plugin);

	//Prefix
	YamlConfiguration locale = HConH.getLocale();
	String prefix = (locale.getString("chat.prefix").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")+" ");

	public void placeReceptionSign(SignChangeEvent e){
		Player p = e.getPlayer();
		//Sign Lines
		String Line2 = ChatColor.stripColor(e.getLine(1)).trim();
		if(!Line2.isEmpty()){
			if ((!(Line2.isEmpty()))&&(WGM.getWorldGuard().getRegionManager(e.getPlayer().getWorld()).hasRegion("Hotel-"+Line2))){ //Hotel region exists
				int tot = totalRooms(Line2,p.getWorld()); //Getting total amount of rooms in hotel
				int free = freeRooms(Line2,p.getWorld()); //Getting amount of free rooms in hotel
				Line2 = Line2.toLowerCase();
				String hotelName = Line2.substring(0, 1).toUpperCase() + Line2.substring(1); //Beautifying hotel name
				//Setting all sign lines
				e.setLine(0, ("&a"+locale.getString("sign.reception")).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				e.setLine(1, ("&1"+hotelName+" Hotel").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				e.setLine(2, ("&1"+tot+"&0 "+locale.getString("sign.room.total")).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				e.setLine(3, ("&a"+free+"&0 "+locale.getString("sign.room.free")).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
				//Updating sign file
				File signFile = HConH.getFile("Signs"+File.separator+"Reception-"+Line2+"-1.yml");
				for(int i = 1; signFile.exists(); i++){
					signFile = HConH.getFile("Signs"+File.separator+"Reception-"+Line2+"-"+i+".yml");
				}
				if(!signFile.exists()){
					try {
						signFile.createNewFile();
					} catch (IOException e1){
						p.sendMessage(prefix+locale.getString("chat.sign.place.fileFail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						e1.printStackTrace();
					}
					new YamlConfiguration();
					YamlConfiguration config = YamlConfiguration.loadConfiguration(signFile);
					config.addDefault("Reception.hotel", hotelName);
					config.addDefault("Reception.location.world", e.getBlock().getWorld().getName());
					config.addDefault("Reception.location.x", e.getBlock().getX());
					config.addDefault("Reception.location.y", e.getBlock().getY());
					config.addDefault("Reception.location.z", e.getBlock().getZ());
					config.options().copyDefaults(true);
					try {
						config.save(signFile);
					} catch (IOException e1) {
						p.sendMessage(prefix+locale.getString("chat.sign.place.fileFail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						e1.printStackTrace();
					}
				}		
			}
			else{
				e.setLine(0, "&4[Hotels]");
				p.sendMessage(prefix+locale.getString("chat.sign.place.noHotel").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
			}
		}
		else{
			e.setLine(0, "&4[Hotels]");
			p.sendMessage(prefix+locale.getString("chat.sign.place.emptySign").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
		}
		return;
	}

	public void placeRoomSign(SignChangeEvent e){
		Player p = e.getPlayer();
		//Sign Lines
		String Line2 = (ChatColor.stripColor(e.getLine(1)).trim()).toLowerCase();
		String Line3 = ChatColor.stripColor(e.getLine(2)).trim();
		String Line4 = ChatColor.stripColor(e.getLine(3)).trim();

		File directory = HConH.getFile("Signs");
		if(!directory.exists()){
			directory.mkdir();}
		if(Line3.contains(":")){
			String[] Line3parts = Line3.split(":");
			int roomnum = Integer.parseInt(Line3parts[0]); //Room Number
			String roomnumb = String.valueOf(roomnum);
			String cost = Line3parts[1]; //Cost
			if((roomnumb.length()+cost.length()+9)<22){
				File signFile = HConH.getFile("Signs"+File.separator+Line2+"-"+roomnum+".yml");
				if(!signFile.exists()){ //Sign for room doesn't already exist
					if ((!(Line2.isEmpty()))&&(WGM.getWorldGuard().getRegionManager(e.getPlayer().getWorld()).hasRegion("Hotel-"+Line2))&& //Hotel region exists
							(WGM.getWorldGuard().getRegionManager(e.getPlayer().getWorld()).getRegion("Hotel-"+Line2).contains(e.getBlock().getX(),e.getBlock().getY(),e.getBlock().getZ()))){
						//Sign is within hotel region
						if((WGM.getWorldGuard().getRegionManager(e.getPlayer().getWorld()).hasRegion("Hotel-"+Line2+"-"+roomnum))){ //Room region exists
							//Successful Sign
							if(!signFile.exists()){
								try {
									signFile.createNewFile();
								} catch (IOException e2){
									p.sendMessage(prefix+locale.getString("chat.sign.place.fileFail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
								}
							}

							//Creating sign config file:
							YamlConfiguration signConfig = YamlConfiguration.loadConfiguration(signFile);

							String immutedtime = Line4.trim(); //Time

							if(immutedtime.equals(0)){//Checking if time is set to infinite
								signConfig.set("Sign.time", 0);
							}
							else{
								long timeinminutes = TimeConverter(immutedtime);
								signConfig.set("Sign.time", Long.valueOf(timeinminutes));
							}

							//Calculating accurate cost
							double acccost = CostConverter(cost);

							signConfig.set("Sign.hotel", Line2.toLowerCase());
							signConfig.set("Sign.room", roomnum);
							signConfig.set("Sign.cost", acccost);

							signConfig.set("Sign.region", WGM.getWorldGuard().getRegionManager(e.getPlayer().getWorld()).getRegion("Hotel-"+Line2+"-"+roomnum).getId().toString());
							signConfig.set("Sign.location.world", String.valueOf(e.getBlock().getWorld().getName()));
							signConfig.set("Sign.location.coords.x", Integer.valueOf(e.getBlock().getLocation().getBlockX()));
							signConfig.set("Sign.location.coords.y", Integer.valueOf(e.getBlock().getLocation().getBlockY()));
							signConfig.set("Sign.location.coords.z", Integer.valueOf(e.getBlock().getLocation().getBlockZ()));
							try {
								signConfig.save(signFile);
							} catch (IOException e1) {
								p.sendMessage(prefix+locale.getString("chat.sign.place.fileFail").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
								e1.printStackTrace();}

							Line2 = Line2.toLowerCase();
							String output = Line2.substring(0, 1).toUpperCase() + Line2.substring(1);
							e.setLine(0, ChatColor.DARK_BLUE+output); //Hotel Name
							e.setLine(1, ChatColor.DARK_GREEN+"Room " + roomnum+" - "+cost.toUpperCase()+"$"); //Room Number + Cost
							if(immutedtime.matches("0"))
								e.setLine(2,locale.getString("sign.permanent").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
							else{
								long ktimeinminutes = TimeConverter(immutedtime);
								//Time
								e.setLine(2, TimeFormatter(ktimeinminutes));
							}
							e.setLine(3,ChatColor.GREEN+"Vacant"); //Renter
							p.sendMessage(prefix+locale.getString("chat.sign.place.success").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));

						} else{
							p.sendMessage(prefix+locale.getString("chat.sign.place.noRegion").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
							//Specified hotel does not exist
						}
					} else{
						p.sendMessage(prefix+locale.getString("chat.sign.place.outOfRegion").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));       		
						e.setLine(0, "&4[Hotels]");
						//Sign not in hotel borders
					}
				}else{
					p.sendMessage(prefix+locale.getString("chat.sign.place.alreadyExists").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					e.setLine(0, "&4[Hotels]");
					//Sign for specified room already exists
				}
			}
			else{
				p.sendMessage(prefix+locale.getString("chat.sign.place.tooLong").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));				
				e.setLine(0, "&4[Hotels]");
				//Room num of price too big
			}
		}else{
			p.sendMessage(prefix+locale.getString("chat.sign.place.noSeparator").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));  				
			e.setLine(0, "&4[Hotels]");
			//Line 3 does not contain separator
		}
	}
	public void useRoomSign(PlayerInteractEvent e, HotelsMain pluginstance){
		Player p = e.getPlayer();
		//Sign lines
		Sign s = (Sign) e.getClickedBlock().getState();
		String Line1 = ChatColor.stripColor(s.getLine(0)); //Line1
		String Line2 = ChatColor.stripColor(s.getLine(1)); //Line2
		String hotelName = (ChatColor.stripColor(Line1)).toLowerCase(); //Hotel name

		//If Hotel region exists
		if(WGM.getWorldGuard().getRegionManager(p.getWorld()).hasRegion("Hotel-"+hotelName)){
			int x = e.getClickedBlock().getX();
			int y = e.getClickedBlock().getY();
			int z = e.getClickedBlock().getZ();
			//If sign is within region
			if(WGM.getWorldGuard().getRegionManager(p.getWorld()).getRegion("Hotel-"+hotelName).contains(x, y, z)){

				String[] Line2parts = Line2.split("\\s"); //Splitting Line2 into room num + cost
				int roomNum = Integer.valueOf(Line2parts[1].trim()); //Room Number

				File signFile = HConH.getFile("Signs"+File.separator+hotelName.toLowerCase()+"-"+roomNum+".yml");
				if(signFile.exists()){
					YamlConfiguration signConfig = YamlConfiguration.loadConfiguration(signFile);
					String cHotelName = signConfig.getString("Sign.hotel");
					int cRoomNum = signConfig.getInt("Sign.room");
					if(hotelName.equalsIgnoreCase(cHotelName)){ //If hotel names match
						if(roomNum==cRoomNum){ //If room nums match
							String stringroomNum = String.valueOf(roomNum);
							rentRoom(signConfig,pluginstance,signFile,p,hotelName,stringroomNum);
						}
						else
							p.sendMessage(prefix+locale.getString("chat.sign.use.differentRoomNums").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					}
					else
						p.sendMessage(prefix+locale.getString("chat.sign.use.differentHotelNames").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
				}
				else
					p.sendMessage(prefix+locale.getString("chat.sign.use.fileNonExistant").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
			}
			else
				p.sendMessage(prefix+locale.getString("chat.sign.use.signOutOfRegion").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
		}
	}
	public void rentRoom(YamlConfiguration signConfig,HotelsMain pluginstance,File signFile,Player p,String hotelName,String roomNum){
		//If region exists
		System.out.println("Region: "+signConfig.getString("Sign.region"));
		if(WGM.getWorldGuard().getRegionManager(p.getWorld()).hasRegion(signConfig.getString("Sign.region"))){
			String cRenter = signConfig.getString("Sign.renter");
			if(cRenter==null){ //If there is no renter
				if(HotelsMain.economy.hasAccount(p)){
					double account = HotelsMain.economy.getBalance(p);
					double price = signConfig.getDouble("Sign.cost");
					if(account>=price){//If player has enough money
						//If player is under max owned rooms limit
						if(getTimesRented(p.getUniqueId(),pluginstance)<pluginstance.getConfig().getInt("settings.max_rooms_owned")){
							//Renter has passed all conditions and is able to rent this room
							HotelsMain.economy.withdrawPlayer(p, price);
							//Setting time rented at
							signConfig.set("Sign.renter", p.getUniqueId().toString());
							long currentmins = System.currentTimeMillis()/1000/60;
							signConfig.set("Sign.timeRentedAt", currentmins);

							//Setting expiry time
							long minstoexpire = signConfig.getLong("Sign.time");
							if(minstoexpire==0){
								signConfig.set("Sign.expiryDate", 0);
							}
							else{
								long expirydate = currentmins+minstoexpire;
								signConfig.set("Sign.expiryDate", expirydate);
							}

							try {//Saving config file
								signConfig.save(signFile);
							} catch (IOException e1) {
								e1.printStackTrace();
							}

							//Adding renter as region member
							ProtectedRegion r = WGM.getWorldGuard().getRegionManager(p.getWorld()).getRegion("Hotel-"+hotelName+"-"+roomNum);
							WGM.addMember(p, (ProtectedCuboidRegion) r);

							try {//Saving WG regions
								WGM.getWorldGuard().getRegionManager(p.getWorld()).save();
							} catch (StorageException e1) {
								e1.printStackTrace();
							}
							World w = Bukkit.getWorld(signConfig.getString("Sign.location.world"));
							int x = signConfig.getInt("Sign.location.coords.x");
							int y = signConfig.getInt("Sign.location.coords.y");
							int z = signConfig.getInt("Sign.location.coords.z");
							Location loc = new Location(w,x,y,z);
							Block block = loc.getBlock();
							if(block.getType()==Material.SIGN||block.getType()==Material.WALL_SIGN||block.getType()==Material.SIGN_POST){
								Sign s = (Sign) block.getState();
								s.setLine(3, ChatColor.RED+p.getName());//Writing renter name on sign
								s.update();
								DecimalFormat df = new DecimalFormat("#.##");
								p.sendMessage(prefix+locale.getString("chat.sign.use.success").replaceAll("%room%", String.valueOf(roomNum)).replaceAll("%hotel%", hotelName)
										.replaceAll("%price%", String.valueOf(df.format(price))).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
								//Successfully rented room
							}
							else
								p.sendMessage(prefix+locale.getString("chat.commands.rent.invalidLocation").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
						}
						else
							p.sendMessage(prefix+locale.getString("chat.sign.use.maxRoomsReached").replaceAll("%max%", String.valueOf(pluginstance.getConfig().getInt("settings.max_rooms_owned"))).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					}
					else{
						double topay = price-account;
						p.sendMessage(prefix+locale.getString("chat.sign.use.notEnoughMoney").replaceAll("%missingmoney%", String.valueOf(topay)).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
					}
				}
				else
					p.sendMessage(prefix+locale.getString("chat.sign.use.noAccount").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
			}
			else if(Bukkit.getServer().getOfflinePlayer(UUID.fromString(cRenter)).equals(p)){
				//Renter is same player that right clicked
				rentExtend(p,signConfig,signFile,pluginstance);
			}
			else
				p.sendMessage(prefix+locale.getString("chat.sign.use.taken").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
		}
		else
			p.sendMessage(prefix+locale.getString("chat.sign.use.nonExistantRoom").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
	}
	public void breakRoomSign(BlockBreakEvent e){
		Block b = e.getBlock();
		Sign s = (Sign) b.getState();
		String Line1 = (ChatColor.stripColor(s.getLine(0))).toLowerCase();
		World w = b.getWorld();
		if(WGM.hasRegion(w, "Hotel-"+Line1)){
			//Room sign has been broken?
			if(WGM.getRegion(w, "Hotel-"+Line1).contains(b.getX(), b.getY(), b.getZ())){
				String Line2 = ChatColor.stripColor(s.getLine(1));
				String[] Line2split = Line2.split(" ");
				int roomnum = Integer.parseInt(Line2split[1]);
				if(WGM.hasRegion(w, "Hotel-"+Line1+"-"+roomnum)){
					File signFile = HConH.getFile("Signs"+File.separator+Line1+"-"+roomnum+".yml");
					if(signFile.exists()){
						YamlConfiguration config = YamlConfiguration.loadConfiguration(signFile);
						if(config.getString("Sign.hotel").equalsIgnoreCase(Line1)){
							if(config.getInt("Sign.room")==roomnum){
								World locw = Bukkit.getWorld(config.getString("Sign.location.world"));
								if(locw!=null){
									int locx = config.getInt("Sign.location.coords.x");
									int locy = config.getInt("Sign.location.coords.y");
									int locz = config.getInt("Sign.location.coords.z");
									int bx = b.getX();
									int by = b.getY();
									int bz = b.getZ();
									if(locx==bx&&locy==by&&locz==bz){
										signFile.delete();
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public int getTimesRented(UUID ptocheck, HotelsMain pluginstance){
		File dir = HConH.getFile("Signs");
		if(!(dir.exists()))
			dir.mkdir();

		ArrayList<String> fileslist = HFF.listFiles("plugins"+File.separator+"Hotels"+File.separator+"Signs");
		int rents = 0;
		for(String x: fileslist){
			File file = HConH.getFile("Signs"+File.separator+x);
			if(!file.getName().matches("^"+locale.getString("sign.reception")+"-.+-.+")){
				//Not a reception sign, therefore a room sign
				YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
				String renterUUIDfromConfigString = config.getString("Sign.renter");
				if(renterUUIDfromConfigString!=null){
					UUID renterUUIDfromConfig = UUID.fromString(renterUUIDfromConfigString);
					if(ptocheck.equals(renterUUIDfromConfig))
						rents++;	
				}
				//There is no renter
			}
		}
		return rents;
	}

	public void rentExtend(Player p,YamlConfiguration signConfig,File signFile,HotelsMain pluginstance){
		FileConfiguration ymlConfig = pluginstance.getConfig();
		World w = Bukkit.getWorld(signConfig.getString("Sign.location.world"));
		int x = signConfig.getInt("Sign.location.coords.x");
		int y = signConfig.getInt("Sign.location.coords.y");
		int z = signConfig.getInt("Sign.location.coords.z");
		Location loc = new Location(w,x,y,z);
		Block block = loc.getBlock();
		if(block.getType()==Material.SIGN||block.getType()==Material.WALL_SIGN||block.getType()==Material.SIGN_POST){
			Sign s = (Sign) block.getState();

			if(signConfig.getInt("Sign.time")>0){
				int extended = signConfig.getInt("Sign.extended");
				int max = ymlConfig.getInt("settings.max_rent_extend");
				if(extended<max){
					double account = HotelsMain.economy.getBalance(p);
					double price = signConfig.getDouble("Sign.cost");
					if(account>=price){//If player has enough money
						HotelsMain.economy.withdrawPlayer(p, price);
						signConfig.set("Sign.extended", extended+1);
						try {
							signConfig.save(signFile);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						long expiryDate = signConfig.getLong("Sign.expiryDate");
						long deftime = signConfig.getLong("Sign.time");
						signConfig.set("Sign.expiryDate", expiryDate+deftime);
						try {
							signConfig.save(signFile);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						s.setLine(2, TimeFormatter(signConfig.getLong("Sign.expiryDate")-(System.currentTimeMillis()/1000/60) ));
						s.update();
						extended+=1;
						if(max-extended>0)
							p.sendMessage(prefix+locale.getString("chat.sign.use.extensionSuccess").replaceAll("%tot%", String.valueOf(extended)).replaceAll("%left%", String.valueOf(max-extended)).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
						else
							p.sendMessage(prefix+locale.getString("chat.sign.use.extensionSuccessNoMore").replaceAll("%tot%", String.valueOf(extended)).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					}
					else{
						double topay = price-account;
						p.sendMessage(prefix+locale.getString("chat.sign.use.notEnoughMoney").replaceAll("%missingmoney%", String.valueOf(topay)).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					}
				}
				else
					p.sendMessage(prefix+locale.getString("chat.sign.use.maxEntendReached").replaceAll("%max%", String.valueOf(max)).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
			}
	}
	else
		p.sendMessage(prefix+locale.getString("chat.commands.rent.invalidLocation").replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1")); 
}

public int totalRooms(String hotelName,World w){
	hotelName = hotelName.toLowerCase();
	//Finds total amount of rooms in given hotel
	int tot = 0;
	Map<String, ProtectedRegion> regions = new HashMap<String, ProtectedRegion>();
	regions = WGM.getWorldGuard().getRegionManager(w).getRegions();
	ProtectedRegion[] rlist = regions.values().toArray(new ProtectedRegion[regions.size()]);
	for(ProtectedRegion r:rlist){
		if(r.getId().startsWith("hotel-"+hotelName)){
			if(r.getId().matches("^hotel-"+hotelName+"-.+")){
				tot++;
			}
		}
	}
	return tot;
}

public int freeRooms(String hotelName,World w){
	hotelName = hotelName.toLowerCase();
	//Finds total amount of free rooms in given hotel
	int free = 0;
	Map<String, ProtectedRegion> regions = new HashMap<String, ProtectedRegion>();
	regions = WGM.getWorldGuard().getRegionManager(w).getRegions();
	ProtectedRegion[] rlist = regions.values().toArray(new ProtectedRegion[regions.size()]);
	for(ProtectedRegion r:rlist){
		if(r.getId().startsWith("hotel-"+hotelName)){
			if(r.getId().matches("^hotel-"+hotelName+"-.+")){
				int roomNum = Integer.parseInt(r.getId().replaceAll("^hotel-.+-", ""));
				File signFile = HConH.getFile("Signs"+File.separator+hotelName+"-"+roomNum+".yml");
				if(signFile.exists()){
					new YamlConfiguration();
					YamlConfiguration config = YamlConfiguration.loadConfiguration(signFile);
					if(config.get("Sign.renter")==null){
						free++;
					}
				}
			}
		}
	}
	return free;
}

public boolean updateReceptionSign(Location l){
	//Updates the reception sign at given location
	Block b = l.getBlock();
	if(b.getType().equals(Material.WALL_SIGN)||b.getType().equals(Material.SIGN)||l.getBlock().getType().equals(Material.SIGN_POST)){
		Sign s = (Sign) b.getState();
		String Line1 = ChatColor.stripColor(s.getLine(0));
		String Line2 = ChatColor.stripColor(s.getLine(1));
		if(Line1.equals("Reception")){ //First line is "Reception"
			if(Line2!=null){
				String[] Line2split = Line2.split(" ");
				String hotelname = Line2split[0].toLowerCase();
				if(WGM.getWorldGuard().getRegionManager(b.getWorld()).hasRegion("hotel-"+hotelname)){ //Hotel region exists
					int tot = totalRooms(hotelname,b.getWorld());
					int free = freeRooms(hotelname,b.getWorld());
					s.setLine(2, ("&1"+tot+"&0 "+locale.getString("sign.room.total")).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					s.setLine(3, ("&a"+free+"&0 "+locale.getString("sign.room.free")).replaceAll("(?i)&([a-fk-r0-9])", "\u00A7$1"));
					s.update();
					return false;
				}
				return true;
			}
			return true;
		}
		return true;
	}
	return true;
}
public long TimeConverter(String immutedtime){
	final Pattern p = Pattern.compile("(\\d+)([hmd])");
	final Matcher m = p.matcher(immutedtime);
	long totalMins = 0;
	while (m.find())
	{
		final int duration = Integer.parseInt(m.group(1));
		final TimeUnit interval = toTimeUnit(m.group(2));
		final long l = interval.toMinutes(duration);
		totalMins = totalMins + l;
	}
	return totalMins;
}

public TimeUnit toTimeUnit(@Nonnull final String c){
	switch (c)
	{
	case "m": return TimeUnit.MINUTES;
	case "h": return TimeUnit.HOURS;
	case "d": return TimeUnit.DAYS;
	default: throw new IllegalArgumentException(String.format("%s is not a valid time code [mhd]", c));
	}
}
public double CostConverter(String immutedcost)
{
	final Pattern p = Pattern.compile("(\\d+)([thkmb]||)");
	final Matcher m = p.matcher(immutedcost);
	double totalCost = 0;
	while (m.find())
	{
		final double duration = Double.parseDouble(m.group(1));
		final double interval = toCost(m.group(2));
		final double l = interval*duration;
		totalCost += l;
	}
	return totalCost;
}

public double toCost(@Nonnull final String c){
	switch (c){
	case "t": return 10;
	case "h": return 100;
	case "k": return 1000;
	case "m": return 1000000;
	case "b": return 1000000000;
	case "": return 1;

	default: throw new IllegalArgumentException(String.format("%s is not a valid cost code [thkmb]", c));
	}
}
public String TimeFormatter(long input){
	if(input>0){
		//Formats time in minutes to days, hours and minutes
		long[] ftime = new long[3];
		ftime[0] = TimeUnit.MINUTES.toDays(input); //Days
		ftime[1] = TimeUnit.MINUTES.toHours(input) - TimeUnit.DAYS.toHours(ftime[0]); //Hours
		ftime[2] = input - TimeUnit.DAYS.toMinutes(ftime[0]) - TimeUnit.HOURS.toMinutes(ftime[1]); //Minutes
		String line2 = "";
		if(ftime[0]>0)
			line2 = line2+ftime[0]+"d";
		if(ftime[1]>0)
			line2 = line2+ftime[1]+"h";
		if(ftime[2]>0)
			line2 = line2+ftime[2]+"m";
		return line2;
	}
	else
		return locale.getString("sign.permanent");
}
}
