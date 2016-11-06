package kernitus.plugin.Hotels.Signs;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class AbstractSign {

	abstract void update();
	
	abstract Block getBlock();
	
	abstract Sign getSign();
	
	abstract Location getLocation();
	
	abstract File getFile();
	
	abstract void removeSign();
	
	abstract World getWorldFromConfig();
	
	public YamlConfiguration getConfig(){
		return YamlConfiguration.loadConfiguration(getFile());
	}
	
	public void deleteConfig(){
		getFile().delete();
	}
	
	public void deleteSignAndConfig(){
		removeSign();
		deleteConfig();
	}
}
