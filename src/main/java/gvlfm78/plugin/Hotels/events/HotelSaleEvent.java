package kernitus.plugin.Hotels.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import kernitus.plugin.Hotels.Hotel;

public class HotelSaleEvent extends Event{
	
	private Hotel hotel;
	private static final HandlerList handlers = new HandlerList();

	public HotelSaleEvent(Hotel hotel){
		this.hotel = hotel;
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
}
