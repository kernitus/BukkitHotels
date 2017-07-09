package kernitus.plugin.Hotels.managers;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.flags.registry.SimpleFlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import kernitus.plugin.Hotels.handlers.HTConfigHandler;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HTWorldGuardManager {

	public static WorldGuardPlugin getWorldGuard(){
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		if (p instanceof WorldGuardPlugin) return (WorldGuardPlugin) p;
		else return null;
	}

	public static ProtectedRegion getRegion(World world, String string) {
		return getRM(world).getRegion(string);
	}

	public static void addOwner(OfflinePlayer p, ProtectedRegion r){
		DefaultDomain owners = r.getOwners();
		owners.addPlayer(p.getUniqueId());
		r.setOwners(owners);
	}

	public static void addOwners(DefaultDomain dd, ProtectedRegion r){
		DefaultDomain owners = r.getOwners();
		owners.addAll(dd);
		r.setOwners(owners);
	}

	public static void addMember(OfflinePlayer p, ProtectedRegion r){
		DefaultDomain members = r.getMembers();
		members.addPlayer(p.getUniqueId());
		r.setMembers(members);
	}
	public static void addMembers(DefaultDomain dd, ProtectedRegion r){
		DefaultDomain members = r.getMembers();
		members.addAll(dd);
		r.setMembers(members);
	}
	public static void setMember(UUID uuid, ProtectedRegion r){
		DefaultDomain member = new DefaultDomain();
		member.addPlayer(uuid);
		r.setMembers(member);
	}
	public static void setMember(OfflinePlayer p, ProtectedRegion r){
		DefaultDomain members = new DefaultDomain();
		members.addPlayer(p.getUniqueId());
		r.setMembers(members);
	}
	public static void setOwner(OfflinePlayer p, ProtectedRegion r){
		DefaultDomain owners = new DefaultDomain();
		owners.addPlayer(p.getUniqueId());
		r.setOwners(owners);
	}
	public static void setMembers(ArrayList<UUID> uuids, ProtectedRegion r){
		DefaultDomain members = new DefaultDomain();
		for(UUID uuid : uuids)
			members.addPlayer(uuid);
		r.setMembers(members);
	}
	public static void setOwners(ArrayList<UUID> uuids, ProtectedRegion r){
		DefaultDomain owners = new DefaultDomain();
		for(UUID uuid : uuids)
			owners.addPlayer(uuid);
		r.setOwners(owners);
	}

	public static void removeOwner(OfflinePlayer p, ProtectedRegion r){
		DefaultDomain owners = r.getOwners();
		owners.removePlayer(p.getUniqueId());
		r.setOwners(owners);
	}
	public static void removeOwners(DefaultDomain dd, ProtectedRegion r){
		DefaultDomain owners = r.getOwners();
		owners.removeAll(dd);
		r.setOwners(owners);
	}

	public static void removeMember(UUID id, ProtectedRegion r){
		DefaultDomain members = r.getMembers();
		members.removePlayer(id);
		System.out.println("Members: " + members.size() + " id: " + id.toString());
		r.setMembers(members);
	}
	public static void removeMembers(DefaultDomain dd, ProtectedRegion r){
		DefaultDomain members = r.getMembers();
		members.removeAll(dd);
		r.setMembers(members);
	}

	public static void addRegion(World w, ProtectedRegion r){
		getRM(w).addRegion(r);
	}
	public static void removeRegion(World w, String r){
		getRM(w).removeRegion(r);
	}
	public static void removeRegion(World w, ProtectedRegion r){
		getRM(w).removeRegion(r.getId());
	}

	public static void saveRegions(World world){
		try {
			getRM(world).save();
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}
	public static RegionManager getRM(World world){
		return getWorldGuard().getRegionManager(world);
	}
	public static boolean hasRegion(World world, String regionName){
		return getRM(world).hasRegion(regionName);
	}
	public static ProtectedRegion getHotelRegion(World world, String name){
		return getRegion(world, "hotel-" + name);
	}
	public static ProtectedRegion getRoomRegion(World world, String hotelName, String num){
		return getRegion(world, "hotel-" + hotelName + "-" + num);
	}
	public static void renameRegion(String oldname, String newname, World world){
		if(!hasRegion(world, oldname)) return; //If old region exists
		ProtectedRegion oldRegion = getRegion(world, oldname);//Get old region

		ProtectedRegion newRegion;

		if(oldRegion instanceof ProtectedCuboidRegion)
			newRegion = new ProtectedCuboidRegion(newname, oldRegion.getMinimumPoint(), oldRegion.getMaximumPoint());
		else if(oldRegion instanceof ProtectedPolygonalRegion)
			newRegion = new ProtectedPolygonalRegion(newname, oldRegion.getPoints(), oldRegion.getMinimumPoint().getBlockY(), oldRegion.getMaximumPoint().getBlockY());
		else return; //Not the correct type of region

		getRM(world).addRegion(newRegion);

		newRegion.copyFrom(oldRegion);

		removeRegion(world, oldRegion);
		saveRegions(world);
	}
	public static Collection<ProtectedRegion> getRegions(World world){
		return getRM(world).getRegions().values();
	}
	public static boolean isOwner(Player p, ProtectedRegion r){
		return r.getOwners().contains(p.getName()) || r.getOwners().contains(p.getUniqueId());
	}
	public static boolean isOwner(Player p, String id, World w){
		return hasRegion(w, id) && isOwner(p, getRegion(w, id));
	}
	public static boolean doTwoRegionsOverlap(ProtectedRegion r1, ProtectedRegion r2){
		return r2.containsAny(r1.getPoints());
	}
	public static boolean doHotelRegionsOverlap(ProtectedRegion region, World world){
		Collection<ProtectedRegion> regions = getRegions(world);
		List<ProtectedRegion> inter = region.getIntersectingRegions(regions);
		for(ProtectedRegion reg : inter)
			if(reg.getId().startsWith("hotel-")) return true;
		return false;
	}
	public static boolean doesRoomRegionOverlap(ProtectedRegion region, World world){
		Collection<ProtectedRegion> regions = getRegions(world);
		List<ProtectedRegion> inter = region.getIntersectingRegions(regions);
		for(ProtectedRegion reg : inter)
			if(reg.getId().matches("hotel-\\w+-\\d+")) return true; //It's a room region
		return false;
	}
	public static void setFlags(ConfigurationSection section, ProtectedRegion r, String name, World world){
		FlagRegistry registry = new SimpleFlagRegistry();
		registry.registerAll(DefaultFlag.getDefaultFlags());

		boolean isHotel = !r.getId().matches("hotel-.+-\\d+");

		Map <Flag<?>, Object> flags = new HashMap<Flag<?>, Object>();
		Map <Flag<?>, Object> groupFlags = new HashMap<Flag<?>, Object>();
		Map <Flag<?>, String> groupFlagValues = new HashMap<Flag<?>, String>();

		for(String key : section.getKeys(true)){
			String pureKey = key.replaceAll(".+\\.", "");
			String keyValue = section.getString(key);
			if(keyValue == null || keyValue.equalsIgnoreCase("none") || keyValue.startsWith("MemorySection")) continue;

			if(keyValue.contains("-g ")){	
				final Pattern pattern = Pattern.compile("(\\s?)(-g\\s)(\\w+)(\\s?)");
				final Matcher matcher = pattern.matcher(keyValue);

				while (matcher.find()){
					String pureGroupFlag = matcher.group(3);
					groupFlags.put(DefaultFlag.fuzzyMatchFlag(registry, pureKey), keyValue);
					groupFlagValues.put(DefaultFlag.fuzzyMatchFlag(registry, pureKey), pureGroupFlag);
				}

				keyValue = keyValue.replaceAll("\\s?-g\\s\\w+\\s?", "");
			}

			switch(pureKey){
			case "GREETING":
				if(Boolean.valueOf(keyValue)){
					String message;
					if(isHotel) message = Mes.getStringNoPrefix("message.hotel.enter").replaceAll("%hotel%", name);
					else message = Mes.getStringNoPrefix("message.room.enter").replaceAll("%room%", name);
					flags.put(DefaultFlag.fuzzyMatchFlag(registry, pureKey), message);
				} break;
			case "FAREWELL":
				if(Boolean.valueOf(keyValue)){
					String message;
					if(isHotel)	message = Mes.getStringNoPrefix("message.hotel.exit").replaceAll("%hotel%", name);
					else message = Mes.getStringNoPrefix("message.room.exit").replaceAll("%room%", name);
					flags.put(DefaultFlag.fuzzyMatchFlag(registry, pureKey), message);
				} break;
				//String
			case "DENY-MESSAGE": case "ENTRY-DENY-MESSAGE": case "EXIT-DENY-MESSAGE": case "TIME-LOCK":
				flags.put(DefaultFlag.fuzzyMatchFlag(registry, pureKey), keyValue); break;
				//Integer
			case "HEAL-DELAY": case "HEAL-AMOUNT": case "FEED-DELAY": case "FEED-AMOUNT": case "FEED-MIN-HUNGER": case "FEED-MAX-HUNGER": 
				Integer intFlag = Integer.valueOf(keyValue);
				if(intFlag!=null) flags.put(DefaultFlag.fuzzyMatchFlag(registry, pureKey), intFlag); break;
				//Double
			case "HEAL-MIN-HEALTH": case "HEAL-MAX-HEALTH": case "PRICE":
				Double doubleFlag = Double.valueOf(keyValue);
				if(doubleFlag!=null) flags.put(DefaultFlag.fuzzyMatchFlag(registry, pureKey), doubleFlag); break;
				//Boolean
			case "NOTIFY-ENTER": case "NOTIFY-LEAVE": case "BUYABLE": case "EXIT-OVERRIDE":
				Boolean booleanFlag = Boolean.valueOf(keyValue);
				flags.put(DefaultFlag.fuzzyMatchFlag(registry, pureKey), booleanFlag); break;
				//Weather Type (Clear or downfall)
			case "WEATHER-LOCK":
				WeatherType weatherFlag = WeatherType.valueOf(keyValue.toUpperCase());
				if(weatherFlag!=null) flags.put(DefaultFlag.fuzzyMatchFlag(registry, pureKey), weatherFlag); break;
				//GameMode (Adventure, Creative, Spectator, Survival)
			case "GAME-MODE":
				GameMode gamemodeFlag = GameMode.valueOf(keyValue.toUpperCase());
				if(gamemodeFlag!=null) flags.put(DefaultFlag.fuzzyMatchFlag(registry, pureKey), gamemodeFlag); break;
				//Set of entities
			case "DENY-SPAWN":
				List<String> entityList = section.getStringList(key);
				Set<EntityType> entitySet = new HashSet<EntityType>();
				entityList.forEach(entity -> entitySet.add(EntityType.valueOf(entity)));
				flags.put(DefaultFlag.fuzzyMatchFlag(registry, pureKey), entitySet); break;
			case "BLOCKED-CMDS": case "ALLOWED-CMDS":
				String[] cmdsValues = keyValue.split(",");
				Set<String> cmdsSet = new HashSet<String>();
				for(String cmd: cmdsValues)
					cmdsSet.add("/"+cmd);

				flags.put(DefaultFlag.fuzzyMatchFlag(registry, pureKey), cmdsSet); break;
			case "TELEPORT": case "SPAWN":
				int x = section.getInt(key + ".x");
				int y = section.getInt(key + ".y");
				int z = section.getInt(key + ".z");
				int yaw = 0;
				int pitch = 0;
				Location locationFlag = new Location(world, x, y, z, yaw, pitch);
				if(locationFlag!=null) flags.put(DefaultFlag.fuzzyMatchFlag(registry, pureKey), locationFlag); break;
			default:
				if(keyValue.equalsIgnoreCase("ALLOW"))
					flags.put(DefaultFlag.fuzzyMatchFlag(registry, pureKey), State.ALLOW);
				else if(keyValue.equalsIgnoreCase("DENY"))
					flags.put(DefaultFlag.fuzzyMatchFlag(registry, pureKey), State.DENY);
				else Mes.debug("Could not match flag: " + pureKey + " with value: " + keyValue); break;
			}
		}
		r.setFlags(flags);
		for(Flag<?> flag : groupFlags.keySet()){
			String groupFlagValue = groupFlagValues.get(flag);
			groupFlags(r, flag, groupFlagValue);
		}
	}
	public static void groupFlags(ProtectedRegion region,Flag<?> flag, String group){
		RegionGroupFlag regionGroupFlag = flag.getRegionGroupFlag();
		RegionGroup regionGroup = RegionGroup.valueOf(group.toUpperCase());
		region.setFlag(regionGroupFlag, regionGroup);
	}
	public static void hotelFlags(ProtectedRegion region, String hotelName, World world){
		YamlConfiguration flagsConfig = HTConfigHandler.getFlags();
		ConfigurationSection section = flagsConfig.getConfigurationSection("hotel");
		setFlags(section, region, hotelName, world);
	}
	public static void roomFlags(ProtectedRegion region, String num, World world){
		YamlConfiguration flagsConfig = HTConfigHandler.getFlags();
		ConfigurationSection section = flagsConfig.getConfigurationSection("room");
		setFlags(section, region, String.valueOf(num), world);
	}
	public static void makeRoomAccessible(ProtectedRegion region){
		if(HTConfigHandler.getconfigYML().getBoolean("allowPlayersIntoFreeRooms", true)){
			region.setFlag(DefaultFlag.INTERACT, null);
			region.setFlag(DefaultFlag.USE, null);
			makeRoomContainersAccessible(region);
		}
	}
	public static void makeRoomContainersAccessible(ProtectedRegion region){
		if(HTConfigHandler.getconfigYML().getBoolean("allowPlayersToOpenContainersInFreeRooms", false))
			region.setFlag(DefaultFlag.CHEST_ACCESS, null);
	}
}
