package kernitus.plugin.Hotels.events;

import kernitus.plugin.Hotels.Hotel;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HotelDeleteEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private Hotel hotel;
	private boolean cancel;

	public HotelDeleteEvent(Hotel hotel){
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

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
	this.cancel = cancel;
	}
}
