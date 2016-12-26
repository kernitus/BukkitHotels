package kernitus.plugin.Hotels.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import kernitus.plugin.Hotels.trade.RoomBuyer;

public class RoomSaleEvent extends Event implements Cancellable {

	private RoomBuyer rb;
	private static final HandlerList handlers = new HandlerList();
	private boolean cancel;
	double revenue;

	public RoomSaleEvent(RoomBuyer rb, double revenue){
		this.rb = rb;
		this.revenue = revenue;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public RoomBuyer getRoomBuyer(){
		return rb;
	}
	public double getRevenue(){
		return revenue;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	public void setPrice(double price){
		rb.setPrice(price);
	}
	public void setRevenue(double revenue){
		this.revenue = revenue;
	}
}
