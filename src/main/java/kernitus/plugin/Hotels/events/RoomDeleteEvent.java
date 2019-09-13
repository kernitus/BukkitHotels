package kernitus.plugin.Hotels.events;

import kernitus.plugin.Hotels.Room;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RoomDeleteEvent extends Event implements Cancellable {

	private Room room;
	private static final HandlerList handlers = new HandlerList();
	private boolean cancel;

	public RoomDeleteEvent(Room room){
		this.room = room;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Room getRoom(){
		return room;
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
