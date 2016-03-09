package kernitus.plugin.Hotels.managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
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
	public void hotelFlags(World world, ProtectedRegion r,String hotelName,Plugin plugin){
		YamlConfiguration flagsConfig = HConH.getFlags();
		ConfigurationSection section = flagsConfig.getConfigurationSection("hotel");
		Map <Flag<?>, Object> flags = new HashMap<Flag<?>, Object>();
		for(String key : section.getKeys(true)){
			String pureKey = key.replaceAll(".+\\.", "");
			if(section.get(key)==null||section.getString(key).equalsIgnoreCase("none")||section.getString(key).startsWith("MemorySection"))
				continue;
			switch(pureKey){
			
			case "GREET_MESSAGE": case "FAREWELL_MESSAGE":
				r.setFlag(DefaultFlag.GREET_MESSAGE, section.getString(key));
				Flag<?> flag = DefaultFlag.fuzzyMatchFlag("ITEM-PICKUP");
				System.out.println("Fuzzy: "+flag+" Purekey: "+pureKey+" key: "+key+" value: "+section.getString(key));
				flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), section.getString(key).replaceAll("%hotel%", hotelName));
				break;
			//String
			case "DENY_MESSAGE": case "ENTRY_DENY_MESSAGE": case "EXIT_DENY_MESSAGE": case "TIME_LOCK":
				flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), section.getString(key));
				break;
			//Integer
			case "HEAL_DELAY": case "HEAL_AMOUNT": case "FEED_DELAY": case "FEED_AMOUNT": case "MIN_FOOD": case "MAX_FOOD": 
				Integer intFlag = section.getInt(key);
				if(intFlag!=null)
				flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), intFlag);
				break;
			//Double
			case "MIN_HEAL": case "MAX_HEAL": case "PRICE":
				Double doubleFlag = section.getDouble(key);
				if(doubleFlag!=null)
				flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), doubleFlag);
				break;
			//Boolean
			case "NOTIFY_ENTER": case "NOTIFY_LEAVE": case "BUYABLE":
				flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), section.getBoolean(key));
				break;
			//Weather Type (Clear or downfall)
			case "WEATHER_LOCK":
				WeatherType weatherFlag = WeatherType.valueOf(section.getString(key).toUpperCase());
				if(weatherFlag!=null)
					flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), weatherFlag);
				break;
			//GameMode (Adventure, Creative, Spectator, Survival)
			case "GAME_MODE":
				GameMode gamemodeFlag = GameMode.valueOf(section.getString(key).toUpperCase());
				if(gamemodeFlag!=null)
					flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), gamemodeFlag);
				break;
				//Set of entities
			case "DENY_SPAWN":
				/*Set<EntityType> entitySet = new HashSet<EntityType>();
				entitySet.add(new EntityType(section.getString(key)));
				r.setFlag(DefaultFlag.DENY_SPAWN, entitySet);
				flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), entitySet);*/
				break;
			case "BLOCKED_CMDS": case "ALLOWED_CMDS":
				List<String> cmdsList = section.getStringList(key);
				Set<String> cmdsSet = new HashSet<String>(cmdsList);
				//Letschekkdisaut
				flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), cmdsSet);
				break;
			case "TELE_LOC": case "SPAWN_LOC":
				/*int x = section.getInt(key+".x");
				int y = section.getInt(key+".y");
				int z = section.getInt(key+".z");
				int yaw = 0;
				int pitch = 0;
				Location locationFlag = new Location(world,x,y,z,yaw,pitch);
				r.setFlag(DefaultFlag.SPAWN_LOC, locationFlag);*/
				break;
			default:
				String flagValue = section.getString(key);
				System.out.println("FLEGGGG V: "+flagValue);
				if(flagValue.equalsIgnoreCase("ALLOW"))
					flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), State.ALLOW);
				else if(flagValue.equalsIgnoreCase("DENY"))
					flags.put(DefaultFlag.fuzzyMatchFlag(pureKey), State.DENY);
				break;
			}
		}
	}	
	public void roomFlags(World world, ProtectedRegion region,String hotelName,Player p,int roomNum,Plugin plugin){

		groupFlags(region,DefaultFlag.CHEST_ACCESS);
		groupFlags(region,DefaultFlag.USE);
		groupFlags(region,DefaultFlag.SLEEP);
		groupFlags(region,DefaultFlag.POTION_SPLASH);
		groupFlags(region,DefaultFlag.ITEM_DROP);
		groupFlags(region,DefaultFlag.EXP_DROPS);

		region.setFlag(DefaultFlag.BLOCK_BREAK, State.DENY);
		region.setFlag(DefaultFlag.BLOCK_PLACE, State.DENY);
		region.setFlag(DefaultFlag.PVP, State.DENY);
		region.setFlag(DefaultFlag.PISTONS, State.DENY);
		region.setFlag(DefaultFlag.TNT, State.DENY);
		region.setFlag(DefaultFlag.LIGHTER, State.DENY);
		region.setFlag(DefaultFlag.MOB_SPAWNING, State.DENY);
		if(plugin.getConfig().getBoolean("settings.use-room_enter_message"))
			region.setFlag(DefaultFlag.GREET_MESSAGE, (locale.getString("message.room.enter").replaceAll("%room%", String.valueOf(roomNum))));
		if(plugin.getConfig().getBoolean("settings.use-room_exit_message"))
			region.setFlag(DefaultFlag.FAREWELL_MESSAGE, (locale.getString("message.room.exit").replaceAll("%room%", String.valueOf(roomNum))));
	}
	public void groupFlags(ProtectedRegion region,StateFlag f){
		region.setFlag(f, State.DENY);
		RegionGroupFlag gf = f.getRegionGroupFlag();
		try {
			RegionGroup groupValue = gf.parseInput(getWorldGuard(), null, "non_members");
			region.setFlag(gf, groupValue);
		} catch (InvalidFlagFormat e) {
			e.printStackTrace();
		}
	}
}
