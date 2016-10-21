package kernitus.plugin.Hotels.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import kernitus.plugin.Hotels.Hotel;

public class HotelRenameEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	private Hotel hotel;
	private String oldName;
	

	public HotelRenameEvent(Hotel hotel, String oldName){
		this.hotel = hotel;
		this.oldName = oldName;
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
	
	public String getOldName(){
		return oldName;
	}
}
