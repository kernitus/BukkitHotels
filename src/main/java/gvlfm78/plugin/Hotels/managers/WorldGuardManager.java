package kernitus.plugin.Hotels.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;


public class WorldGuardManager {

	public static WorldGuardPlugin getWorldGuard(){
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		if (p instanceof WorldGuardPlugin) return (WorldGuardPlugin) p;
		else return null;
	}

	public ProtectedRegion getRegion(World world, String string) {
		return getRM(world).getRegion(string);
	}

	public void addOwner(OfflinePlayer p, ProtectedRegion r){
		DefaultDomain owners = r.getOwners();
		owners.addPlayer(p.getUniqueId());
		r.setOwners(owners);
	}

	public void addMember(OfflinePlayer p, ProtectedRegion r){
		DefaultDomain members = r.getMembers();
		members.addPlayer(p.getUniqueId());
		r.setMembers(members);
	}
	public void setMember(UUID uuid, ProtectedRegion r){
		DefaultDomain member = new DefaultDomain();
		member.addPlayer(uuid);
		r.setMembers(member);
	}
	public void setMembers(ArrayList<UUID> uuids, ProtectedRegion r){
		DefaultDomain members = new DefaultDomain();
		for(UUID uuid : uuids)
			members.addPlayer(uuid);
		r.setMembers(members);
	}
	public void setOwners(ArrayList<UUID> uuids, ProtectedRegion r){
		DefaultDomain owners = new DefaultDomain();
		for(UUID uuid : uuids)
			owners.addPlayer(uuid);
		r.setOwners(owners);
	}

	public void removeOwner(OfflinePlayer p, ProtectedRegion r){
		DefaultDomain owners = r.getOwners();
		owners.removePlayer(p.getUniqueId());
		r.setOwners(owners);
	}

	public void removeMember(OfflinePlayer p, ProtectedRegion r){
		DefaultDomain members = r.getMembers();
		members.removePlayer(p.getUniqueId());
		r.setMembers(members);
	}

	public void addRegion(World w, ProtectedRegion r){
		getRM(w).addRegion(r);
	}
	public void removeRegion(World w, String r){
		getRM(w).removeRegion(r);
	}
	public void removeRegion(World w, ProtectedRegion r){
		getRM(w).removeRegion(r.getId());
	}

	public void saveRegions(World world){
		try {
			getRM(world).save();
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}
	public static RegionManager getRM(World world){
		return getWorldGuard().getRegionManager(world);
	}
	public boolean hasRegion(World world, String regionName){
		return getRM(world).hasRegion(regionName);
	}
	public ProtectedRegion getHotelRegion(World world, String name){
		return getRegion(world, "hotel-"+name);
	}
	public ProtectedRegion getRoomRegion(World world, String hotelName, String num){
		return getRegion(world, "hotel-"+hotelName+"-"+num);
	}
	public ProtectedRegion getRoomRegion(World world, String hotelName, int num){
		return getRegion(world, "hotel-"+hotelName+"-"+num);
	}
	public void renameRegion(String oldname, String newname,World world){
		if(hasRegion(world, oldname)){//If old region exists
			ProtectedRegion oldr = getRegion(world, oldname);//Get old region
			ProtectedRegion newr2;
			if(oldr instanceof ProtectedCuboidRegion)
				newr2 = new ProtectedCuboidRegion(newname, oldr.getMinimumPoint(), oldr.getMaximumPoint());
			else if(oldr instanceof ProtectedPolygonalRegion)
				newr2 = new ProtectedPolygonalRegion(newname, oldr.getPoints(), oldr.getMinimumPoint().getBlockY(), oldr.getMaximumPoint().getBlockY());
			else{
				System.out.println("There was a problem renaming the region "+oldname); return; }

			addRegion(world, newr2);
			ProtectedRegion newr = getRegion(world, newname);
			Map<Flag<?>, Object> flags = oldr.getFlags();
			newr.setFlags(flags);
			DefaultDomain owners = oldr.getOwners();
			DefaultDomain members = oldr.getMembers();
			newr.setOwners(owners);
			newr.setMembers(members);
			removeRegion(world, oldr.getId());
			saveRegions(world);
		}
	}
	public static Collection<ProtectedRegion> getRegions(World world){
		return getRM(world).getRegions().values();
	}
	public boolean isOwner(Player p,ProtectedRegion r){
		return r.getOwners().contains(p.getName())||r.getOwners().contains(p.getUniqueId());
	}
	public boolean isOwner(Player p, String id,World w){
		return hasRegion(w,id) && isOwner(p, getRegion(w, id));
	}
	public boolean doTwoRegionsOverlap(ProtectedRegion r1, ProtectedRegion r2){
		return r2.containsAny(r1.getPoints());
	}
	public boolean doHotelRegionsOverlap(ProtectedRegion region, World world){
		Collection<ProtectedRegion> regions = getRegions(world);
		List<ProtectedRegion> inter = region.getIntersectingRegions(regions);
		for(ProtectedRegion reg : inter){
			if(reg.getId().startsWith("hotel-"))
				return true;
		}
		return false;
	}
	public boolean doesRoomRegionOverlap(ProtectedRegion region, World world){
		Collection<ProtectedRegion> regions = getRegions(world);
		List<ProtectedRegion> inter = region.getIntersectingRegions(regions);
		for(ProtectedRegion reg : inter){
			if(reg.getId().matches("hotel-\\w+-\\d+"))//It's a room region
				return true;
		}
		return false;
	}
	public void setFlags(ConfigurationSection section, ProtectedRegion r, String name, World world){

		boolean isHotel = r.getId().matches("hotel-.+-\\d+");

		Map <Flag<?>, Object> flags = new HashMap<Flag<?>, Object>();
		Map <Flag<?>, Object> groupFlags = new HashMap<Flag<?>, Object>();
		Map <Flag<?>, String> groupFlagValues = new HashMap<Flag<?>, String>();

		for(String key : section.getKeys(true)){
			String pureKey = key.replaceAll(".+\\.", "");
			String keyValue = section.getString(key);
			if(keyValue==null || keyValue.equalsIgnoreCase("none") || keyValue.startsWith("MemorySection"))
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
			case "GREETING":
				if(Boolean.valueOf(keyValue)){
					String message;
					if(isHotel)
						message = Mes.mesnopre("message.hotel.enter").replaceAll("%hotel%", name);
					else
						message = Mes.mesnopre("message.room.enter").replaceAll("%room%", name);
					flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), message);
				}
				break;
			case "FAREWELL":
				String message;
				if(isHotel)
					message = Mes.mesnopre("message.hotel.exit").replaceAll("%hotel%", name);
				else
					message = Mes.mesnopre("message.room.exit").replaceAll("%room%", name);
				flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), message);
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
				List<String> entityList = section.getStringList(key);
				Set<EntityType> entitySet = new HashSet<EntityType>();
				for(String entity : entityList)
					entitySet.add(EntityType.valueOf(entity));

				flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), entitySet);
				break;
			case "BLOCKED-CMDS": case "ALLOWED-CMDS":
				String[] cmdsValues = keyValue.split(",");
				Set<String> cmdsSet = new HashSet<String>();
				for(String cmd: cmdsValues)
					cmdsSet.add("/"+cmd);

				flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), cmdsSet);
				break;
			case "TELEPORT": case "SPAWN":
				int x = section.getInt(key + ".x");
				int y = section.getInt(key + ".y");
				int z = section.getInt(key + ".z");
				int yaw = 0;
				int pitch = 0;
				Location locationFlag = new Location(world, x, y, z, yaw, pitch);
				if(locationFlag!=null)
					flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), locationFlag);
				break;
			default:
				if(keyValue.equalsIgnoreCase("ALLOW"))
					flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), State.ALLOW);
				else if(keyValue.equalsIgnoreCase("DENY"))
					flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), State.DENY);
				else
					System.out.println("REJECTED: " + pureKey + " veleue: " + keyValue);
				break;
			}
		}
		r.setFlags(flags);
		for(Flag<?> flag : groupFlags.keySet()){
			String groupFlagValue = groupFlagValues.get(flag);
			groupFlags(r, flag, groupFlagValue);
		}
	}
	public void groupFlags(ProtectedRegion region,Flag<?> flag, String group){
		RegionGroupFlag regionGroupFlag = flag.getRegionGroupFlag();
		RegionGroup regionGroup = RegionGroup.valueOf(group.toUpperCase());
		region.setFlag(regionGroupFlag, regionGroup);
	}
	public void hotelFlags(ProtectedRegion region, String hotelName, World world){
		YamlConfiguration flagsConfig = HotelsConfigHandler.getFlags();
		ConfigurationSection section = flagsConfig.getConfigurationSection("hotel");
		setFlags(section, region, hotelName, world);
	}
	public void roomFlags(ProtectedRegion region, int roomNum, World world){
		YamlConfiguration flagsConfig = HotelsConfigHandler.getFlags();
		ConfigurationSection section = flagsConfig.getConfigurationSection("room");
		setFlags(section, region, String.valueOf(roomNum), world);
	}
	public static void makeRoomAccessible(ProtectedRegion region){
		if(HotelsConfigHandler.getconfigyml().getBoolean("settings.allowPlayersIntoFreeRooms")){
			region.setFlag(DefaultFlag.INTERACT, null);
			region.setFlag(DefaultFlag.USE, null);
			makeRoomContainersAccessible(region);
		}
	}
	public static void makeRoomContainersAccessible(ProtectedRegion region){
		if(HotelsConfigHandler.getconfigyml().getBoolean("settings.allowPlayersToOpenContainersInFreeRooms"))
			region.setFlag(DefaultFlag.CHEST_ACCESS, null);
	}
}
