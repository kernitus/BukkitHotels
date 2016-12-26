package kernitus.plugin.Hotels.trade;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import kernitus.plugin.Hotels.Hotel;

public class HotelBuyer extends Hotel implements Buyer {
	
	private final Player p;
	private double price;

	public HotelBuyer(World world, String hotelName, Player p, double price){
		super(world, hotelName);
		this.p = p;
		this.price = price;
	}
	public HotelBuyer(Hotel hotel, Player p, double price){
		super(hotel.getWorld(), hotel.getName());
		this.p = p;
		this.price = price;
	}
	public HotelBuyer(String hotelName, Player p, double price){
		super(hotelName);
		this.p = p;
		this.price = price;
	}
	
	public HotelBuyer(World world, String hotelName, UUID id, double price){
		super(world, hotelName);
		this.p = Bukkit.getPlayer(id);
		this.price = price;
	}
	public HotelBuyer(Hotel hotel, UUID id, double price){
		super(hotel.getWorld(), hotel.getName());
		this.p = Bukkit.getPlayer(id);
		this.price = price;
	}
	public HotelBuyer(String hotelName, UUID id, double price){
		super(hotelName);
		this.p = Bukkit.getPlayer(id);
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
	public void setPrice(double price){
		this.price = price;
	}
}
