package kernitus.plugin.Hotels.trade;

import org.bukkit.entity.Player;

public class HotelBuyer implements Buyer {
	
	private final Player p;
	private double price;
	private final String hotelName;

	public HotelBuyer(String hotelName, Player p, double price){
		this.hotelName = hotelName;
		this.p = p;
		this.price = price;
	}

	@Override
	public double getPrice(){
		return price;
	}
	@Override
	public Player getPlayer() {
		return p;
	}
	@Override
	public String getHotelName() {
		return hotelName;
	}

	@Override
	public void setPrice(double price){
		this.price = price;
	}
}
