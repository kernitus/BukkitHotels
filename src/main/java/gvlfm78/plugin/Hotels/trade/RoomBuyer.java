package kernitus.plugin.Hotels.trade;

import org.bukkit.entity.Player;

import kernitus.plugin.Hotels.Room;

public class RoomBuyer extends Room implements Buyer {

	private final Player p;
	private final double price;

	public RoomBuyer(Room room, Player p, double price) {
		super(room.getHotel(), room.getNum());
		this.p = p;
		this.price = price;
	}
	
	@Override
	public Player getPlayer() {
		return p;
	}
	@Override
	public double getPrice() {
		return price;
	}
}