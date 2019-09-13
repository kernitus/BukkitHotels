package kernitus.plugin.Hotels.trade;

import org.bukkit.entity.Player;

public class RoomBuyer implements Buyer {

	private final Player p;
	private double price;
	private final String hotelName;
	private final String roomNum;

	public RoomBuyer(String hotelName, String roomNum, Player p, double price) {
		this.hotelName = hotelName;
		this.roomNum = roomNum;
		this.p = p;
		this.price = price;
	}

	public String getRoomNum(){
		return roomNum;
	}

	@Override
	public Player getPlayer() {
		return p;
	}
	@Override
	public double getPrice() {
		return price;
	}
	@Override
	public String getHotelName(){
		return hotelName;
	}

	@Override
	public void setPrice(double price) {
		this.price = price;
	}
}