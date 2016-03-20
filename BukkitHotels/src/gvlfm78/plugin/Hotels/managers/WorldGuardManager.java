package kernitus.plugin.Hotels.managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;


public class WorldGuardManager {
	private HotelsMain plugin;
	public WorldGuardManager(HotelsMain instance){
		this.plugin = instance;
	}
	HotelsConfigHandler HConH = new HotelsConfigHandler(plugin);

	YamlConfiguration locale = HConH.getLocale();

	public WorldGuardPlugin getWorldGuard(){
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		if (p instanceof WorldGuardPlugin) return (WorldGuardPlugin) p;
		else return null;
	}

	public ProtectedRegion getRegion(World world, String string) {
		return getWorldGuard().getRegionManager(world).getRegion(string);
	}

	public void addOwner(OfflinePlayer p, ProtectedRegion r){
		DefaultDomain owners = r.getOwners();
		owners.addPlayer(p.getName());
		r.setOwners(owners);
	}

	public void addMember(OfflinePlayer p, ProtectedRegion r){
		DefaultDomain members = r.getMembers();
		members.addPlayer(p.getName());
		r.setMembers(members);
	}

	public void removeOwner(OfflinePlayer p, ProtectedRegion r){
		DefaultDomain owners = r.getOwners();
		owners.removePlayer(p.getName());
		r.setOwners(owners);
	}

	public void removeMember(OfflinePlayer p, ProtectedRegion r){
		DefaultDomain members = r.getMembers();
		members.removePlayer(p.getName());
		r.setMembers(members);
	}

	public void addRegion(World w, ProtectedRegion r){
		getWorldGuard().getRegionManager(w).addRegion(r);
	}
	public void removeRegion(World w, String r){
		getWorldGuard().getRegionManager(w).removeRegion(r);
	}
	public boolean hasRegion(World w, String r){
		if(getWorldGuard().getRegionManager(w).hasRegion(r))
			return true;
		else
			return false;
	}

	public void saveRegions(World world){
		RegionManager regionManager = getWorldGuard().getRegionManager(world);
		try {
			regionManager.save();
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}
	public void renameRegion(String oldname, String newname,World world){
		if(hasRegion(world, oldname)){//If old hotel exists
			ProtectedRegion oldr = getRegion(world, oldname);//Get old region
			ProtectedCuboidRegion newr2 = new ProtectedCuboidRegion(
					newname, 
					new BlockVector(oldr.getMinimumPoint()), 
					new BlockVector(oldr.getMaximumPoint())
					);
			addRegion(world, newr2);
			ProtectedRegion newr = getRegion(world, newname);
			Map<Flag<?>, Object> flags = oldr.getFlags();
			newr.setFlags(flags);
			removeRegion(world, oldr.getId());
			saveRegions(world);
		}
	}
	public void setFlags(ConfigurationSection section, ProtectedRegion r,String namenum){

		Map <Flag<?>, Object> flags = new HashMap<Flag<?>, Object>();
		Map <Flag<?>, Object> groupFlags = new HashMap<Flag<?>, Object>();
		Map <Flag<?>, String> groupFlagValues = new HashMap<Flag<?>, String>();

		for(String key : section.getKeys(true)){
			String pureKey = key.replaceAll(".+\\.", "");
			String keyValue = section.getString(key);
			if(keyValue==null||keyValue.equalsIgnoreCase("none")||keyValue.startsWith("MemorySection"))
				continue;
			if(keyValue.contains("-g ")){		
				final Pattern pattern = Pattern.compile("(\\s?)(-g\\s)(\\w+)(\\s?)");
				final Matcher matcher = pattern.matcher(keyValue);

				while (matcher.find()){
					String pureGroupFlag = matcher.group(3);
					groupFlags.put(DefaultFlag.fuzzyMatchFlag(pureKey), keyValue);
					groupFlagValues.put(DefaultFlag.fuzzyMatchFlag(pureKey), pureGroupFlag);
				}
				keyValue = keyValue.replaceAll("\\s?-g\\s\\w+\\s?", "");
			}
			switch(pureKey){
			case "GREETING": case "FAREWELL":
				keyValue = keyValue.replaceAll("%hotel%", namenum).replaceAll("%room%", namenum);
				flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), keyValue);
				break;
				//String
			case "DENY-MESSAGE": case "ENTRY-DENY-MESSAGE": case "EXIT-DENY-MESSAGE": case "TIME-LOCK":
				flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), keyValue);
				break;
				//Integer
			case "HEAL-DELAY": case "HEAL-AMOUNT": case "FEED-DELAY": case "FEED-AMOUNT": case "FEED-MIN-HUNGER": case "FEED-MAX-HUNGER": 
				Integer intFlag = Integer.valueOf(keyValue);
				if(intFlag!=null)
					flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), intFlag);
				break;
				//Double
			case "HEAL-MIN-HEALTH": case "HEAL-MAX-HEALTH": case "PRICE":
				Double doubleFlag = Double.valueOf(keyValue);
				if(doubleFlag!=null)
					flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), doubleFlag);
				break;
				//Boolean
			case "NOTIFY-ENTER": case "NOTIFY-LEAVE": case "BUYABLE": case "EXIT-OVERRIDE":
				Boolean booleanFlag = Boolean.valueOf(keyValue);
				flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), booleanFlag);
				break;
				//Weather Type (Clear or downfall)
			case "WEATHER-LOCK":
				WeatherType weatherFlag = WeatherType.valueOf(keyValue.toUpperCase());
				if(weatherFlag!=null)
					flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), weatherFlag);
				break;
				//GameMode (Adventure, Creative, Spectator, Survival)
			case "GAME-MODE":
				GameMode gamemodeFlag = GameMode.valueOf(keyValue.toUpperCase());
				if(gamemodeFlag!=null)
					flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), gamemodeFlag);
				break;
				//Set of entities
			case "DENY-SPAWN":
				/*List<String> entityList = section.getStringList(key);
				Set<EntityType> entitySet = new HashSet<EntityType>(entityList);
				r.setFlag(DefaultFlag.DENY_SPAWN, entitySet);
				flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), entitySet);*/
				break;
			case "BLOCKED-CMDS": case "ALLOWED-CMDS":
				String[] cmdsValues = keyValue.split(",");
				Set<String> cmdsSet = new HashSet<String>();
				for(String cmd: cmdsValues){
					cmd = "/"+cmd;
					cmdsSet.add(cmd);
				}
				flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), cmdsSet);
				break;
			case "TELEPORT": case "SPAWN":
				/*int x = section.getInt(key+".x");
				int y = section.getInt(key+".y");
				int z = section.getInt(key+".z");
				int yaw = 0;
				int pitch = 0;
				Location locationFlag = new Location(world,x,y,z,yaw,pitch);
				r.setFlag(DefaultFlag.SPAWN_LOC, locationFlag);*/
				break;
			default:
				if(keyValue.equalsIgnoreCase("ALLOW"))
					flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), State.ALLOW);
				else if(keyValue.equalsIgnoreCase("DENY"))
					flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), State.DENY);
				else{
					System.out.println("REJECTED: "+pureKey+" veleue: "+keyValue);
				}
				break;
			}
		}
		r.setFlags(flags);
		for(Flag<?> flag:groupFlags.keySet()){
			String groupFlagValue = groupFlagValues.get(flag);
			groupFlags(r,flag,groupFlagValue);
		}
	}
	public void hotelFlags(ProtectedRegion region,String hotelName){
		YamlConfiguration flagsConfig = HConH.getFlags();
		ConfigurationSection section = flagsConfig.getConfigurationSection("hotel");
		setFlags(section,region,hotelName);
	}
	public void roomFlags(ProtectedRegion region,String roomNum){
		YamlConfiguration flagsConfig = HConH.getFlags();
		ConfigurationSection section = flagsConfig.getConfigurationSection("room");
		setFlags(section,region,roomNum);
	}
	public void groupFlags(ProtectedRegion region,Flag<?> flag,String group){
		RegionGroupFlag regionGroupFlag = flag.getRegionGroupFlag();
		RegionGroup regionGroup = RegionGroup.valueOf(group.toUpperCase());
		region.setFlag(regionGroupFlag, regionGroup);
	}
	public void makeRoomAccessible(ProtectedRegion region){
		if(HConH.getconfigyml().getBoolean("settings.allowPlayersIntoFreeRooms")){
			region.setFlag(DefaultFlag.INTERACT, null);
			region.setFlag(DefaultFlag.USE, null);
			makeRoomContainersAccessible(region);
		}
	}
	public void makeRoomContainersAccessible(ProtectedRegion region){
		if(HConH.getconfigyml().getBoolean("settings.allowPlayersToOpenContainersInFreeRooms")){
			region.setFlag(DefaultFlag.CHEST_ACCESS, null);
		}
	}
}
