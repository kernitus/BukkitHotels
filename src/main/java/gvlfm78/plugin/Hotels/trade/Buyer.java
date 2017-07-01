package kernitus.plugin.Hotels.trade;

import org.bukkit.entity.Player;

public interface Buyer {
	
	Player getPlayer();
	double getPrice();
	void setPrice(double price);
}
