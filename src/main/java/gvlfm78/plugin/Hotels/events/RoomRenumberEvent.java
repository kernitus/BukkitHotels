package kernitus.plugin.Hotels.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import kernitus.plugin.Hotels.Room;

public class RoomRenumberEvent extends Event{
	
	private Room room;
	private static final HandlerList handlers = new HandlerList();
	private int oldNum;

	public RoomRenumberEvent(Room room, int oldNum){
		this.room = room;
		this.oldNum = oldNum;
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
	public int getOldNum(){
		return oldNum;
	}
}
