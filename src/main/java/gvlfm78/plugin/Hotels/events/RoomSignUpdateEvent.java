package kernitus.plugin.Hotels.events;

import org.bukkit.block.Sign;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import kernitus.plugin.Hotels.Room;

public class RoomSignUpdateEvent extends Event implements Cancellable{

	private static final HandlerList handlers = new HandlerList();
	private final Room room;
	private final Sign sign;
	private final long remainingTime;
	
	private boolean isCancelled = false;
	private String formattedRemainingTime;

	public RoomSignUpdateEvent(Room room, Sign sign, long remainingTime, String formattedRemainingTime){
		this.room = room;
		this.sign = sign;
		this.remainingTime = remainingTime;
		this.formattedRemainingTime = formattedRemainingTime;
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
	public Sign getSign(){
		return sign;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}
	public long getRemainingTime(){
		return remainingTime;
	}
	public String getFormattedRemainingTime(){
		return formattedRemainingTime;
	}
	public void setFormattedRemainingTime(String formattedRemainingTime){
		this.formattedRemainingTime = formattedRemainingTime;
	}
}