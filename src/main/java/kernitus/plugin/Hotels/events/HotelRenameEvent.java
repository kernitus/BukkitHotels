package kernitus.plugin.Hotels.events;

import kernitus.plugin.Hotels.Hotel;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HotelRenameEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private Hotel hotel;
	private String newName;
	private boolean cancel;
	

	public HotelRenameEvent(Hotel hotel, String newName){
		this.hotel = hotel;
		this.newName = newName;
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
	
	public String getNewName(){
		return newName;
	}
	
	public void setNewName(String name){
		newName = name;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
