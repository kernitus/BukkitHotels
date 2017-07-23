package kernitus.plugin.Hotels.events;

import kernitus.plugin.Hotels.Room;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RoomRenumberEvent extends Event implements Cancellable {
	
	private Room room;
	private static final HandlerList handlers = new HandlerList();
	private String oldNum;
	private boolean cancel;

	public RoomRenumberEvent(Room room, String oldNum2){
		this.room = room;
		this.oldNum = oldNum2;
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
	public String getOldNum(){
		return oldNum;
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
