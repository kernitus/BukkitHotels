package kernitus.plugin.Hotels.managers;

import kernitus.plugin.Hotels.HotelsMain;
import kernitus.plugin.Hotels.handlers.HotelsConfigHandler;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
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
	public void hotelFlags(ProtectedRegion r,String hotelName,Plugin plugin){
		//r.setFlag(DefaultFlag.PASSTHROUGH, State.ALLOW);
		//r.setFlag(DefaultFlag.BUILD, State.DENY);
		r.setFlag(DefaultFlag.PVP, State.DENY);
		//r.setFlag(DefaultFlag.CHEST_ACCESS, State.DENY);
		//r.setFlag(DefaultFlag.PISTONS, State.DENY);
		r.setFlag(DefaultFlag.TNT, State.DENY);
		r.setFlag(DefaultFlag.LIGHTER, State.DENY);
		//r.setFlag(DefaultFlag.USE, State.DENY);
		r.setFlag(DefaultFlag.PLACE_VEHICLE, State.DENY);
		r.setFlag(DefaultFlag.DESTROY_VEHICLE, State.DENY);
		//r.setFlag(DefaultFlag.SLEEP, State.DENY);
		r.setFlag(DefaultFlag.MOB_DAMAGE, State.DENY);
		r.setFlag(DefaultFlag.MOB_SPAWNING, State.DENY);
		//r.setFlag(DefaultFlag.DENY_SPAWN, State.DENY);
		//r.setFlag(DefaultFlag.INVINCIBILITY, State.DENY);
		//r.setFlag(DefaultFlag.EXP_DROPS, State.DENY);
		r.setFlag(DefaultFlag.CREEPER_EXPLOSION, State.DENY);
		r.setFlag(DefaultFlag.OTHER_EXPLOSION, State.DENY);
		r.setFlag(DefaultFlag.ENDERDRAGON_BLOCK_DAMAGE, State.DENY);
		r.setFlag(DefaultFlag.GHAST_FIREBALL, State.DENY);
		r.setFlag(DefaultFlag.ENDER_BUILD, State.DENY);
		if(plugin.getConfig().getBoolean("settings.use-hotel_enter_message"))
			r.setFlag(DefaultFlag.GREET_MESSAGE, (locale.getString("message.hotel.enter").replaceAll("%hotel%", hotelName)));
		if(plugin.getConfig().getBoolean("settings.use-hotel_exit_message"))
			r.setFlag(DefaultFlag.FAREWELL_MESSAGE, (locale.getString("message.hotel.exit").replaceAll("%hotel%", hotelName)));

		//r.setFlag(DefaultFlag.NOTIFY_ENTER, Boolean.FALSE);
		//r.setFlag(DefaultFlag.NOTIFY_LEAVE, Boolean.FALSE);
		//r.setFlag(DefaultFlag.EXIT, State.ALLOW);
		//r.setFlag(DefaultFlag.ENTRY, State.ALLOW);
		r.setFlag(DefaultFlag.LIGHTNING, State.DENY);
		r.setFlag(DefaultFlag.ENTITY_PAINTING_DESTROY, State.DENY);
		r.setFlag(DefaultFlag.ENDERPEARL, State.DENY);
		r.setFlag(DefaultFlag.ENTITY_ITEM_FRAME_DESTROY, State.DENY);
		/*r.setFlag(DefaultFlag.ITEM_DROP, State.ALLOW);
		r.setFlag(DefaultFlag.HEAL_AMOUNT, 0);
		r.setFlag(DefaultFlag.HEAL_DELAY, 0);
		r.setFlag(DefaultFlag.MIN_HEAL, 0);
		r.setFlag(DefaultFlag.MAX_HEAL, 20);
		r.setFlag(DefaultFlag.FEED_DELAY, 0);
		r.setFlag(DefaultFlag.FEED_AMOUNT, 20);
		r.setFlag(DefaultFlag.MIN_FOOD, 0);
		r.setFlag(DefaultFlag.MAX_FOOD, 20);*/
		r.setFlag(DefaultFlag.SNOW_FALL, State.DENY);
		r.setFlag(DefaultFlag.SNOW_MELT, State.DENY);
		r.setFlag(DefaultFlag.ICE_FORM, State.DENY);
		r.setFlag(DefaultFlag.ICE_MELT, State.DENY);
		r.setFlag(DefaultFlag.SOIL_DRY, State.DENY);
		//r.setFlag(DefaultFlag.GAME_MODE, State.DENY);
		r.setFlag(DefaultFlag.MUSHROOMS, State.DENY);
		r.setFlag(DefaultFlag.LEAF_DECAY, State.DENY);
		r.setFlag(DefaultFlag.GRASS_SPREAD, State.DENY);
		r.setFlag(DefaultFlag.MYCELIUM_SPREAD, State.DENY);
		r.setFlag(DefaultFlag.VINE_GROWTH, State.DENY);
		//r.setFlag(DefaultFlag.SEND_CHAT, State.ALLOW);
		//r.setFlag(DefaultFlag.RECEIVE_CHAT, State.ALLOW);
		r.setFlag(DefaultFlag.FIRE_SPREAD, State.DENY);
		r.setFlag(DefaultFlag.LAVA_FIRE, State.DENY);
		//r.setFlag(DefaultFlag.LAVA_FLOW, State.DENY);
		//r.setFlag(DefaultFlag.WATER_FLOW, State.DENY);
		//r.setFlag(DefaultFlag.TELE_LOC, State.DENY);
		//r.setFlag(DefaultFlag.SPAWN_LOC, State.DENY);
		//r.setFlag(DefaultFlag.POTION_SPLASH, State.DENY);
		//r.setFlag(DefaultFlag.BLOCKED_CMDS, SetFlag<T>);
		//r.setFlag(DefaultFlag.ALLOWED_CMDS, State.DENY);
		//Double price = 0.0;
		//r.setFlag(DefaultFlag.PRICE, price);
		//r.setFlag(DefaultFlag.BUYABLE, Boolean.FALSE);
	}	
	public void roomFlags(ProtectedRegion region,String hotelName,Player p,int roomNum,Plugin plugin){

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
