package kernitus.plugin.Hotels.events;

import kernitus.plugin.Hotels.trade.HotelBuyer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HotelSaleEvent extends Event implements Cancellable {
	
	private HotelBuyer hb;
	private static final HandlerList handlers = new HandlerList();
	private boolean cancel;
	double revenue;

	public HotelSaleEvent(HotelBuyer hb, double revenue){
		this.hb = hb;
		this.revenue = revenue;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HotelBuyer getHotelBuyer(){
		return hb;
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
		hb.setPrice(price);
	}
	public void setRevenue(double revenue){
		this.revenue = revenue;
	}
}
