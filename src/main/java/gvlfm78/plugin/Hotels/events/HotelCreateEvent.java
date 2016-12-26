package kernitus.plugin.Hotels.events;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import kernitus.plugin.Hotels.Hotel;
import kernitus.plugin.Hotels.managers.WorldGuardManager;

public class HotelCreateEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private Hotel hotel;
	private boolean cancel;
	private ProtectedRegion region;
	private String name;
	private World world;
	
	public HotelCreateEvent(Hotel hotel){
		this.hotel = hotel;
		this.region = hotel.getRegion();
		this.name = hotel.getName();
		this.world = hotel.getWorld();
	}
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public Hotel getHotel(){
		return hotel;
	}
	
	public ProtectedRegion getRegion(){
		return region;
	}
	public String getName(){
		return name;
	}
	public World getWorld(){
		return world;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	public void setRegion(ProtectedRegion region){
		this.region = region;
	}
	public void setName(String name){
		this.name = name;
		ProtectedRegion temp = WorldGuardManager.getHotelRegion(world, name);
		temp.copyFrom(region);
		this.region = temp; //TODO check if this correctly copies over everything but the name
	}
	public void setWorld(World world){
		this.world = world;
	}
}
