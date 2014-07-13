package kernitus.plugin.Hotels;

import kernitus.plugin.Hotels.HotelsConfigHandler;

import org.bukkit.plugin.Plugin;

public class HotelsConfigHandler {
		
		static HotelsConfigHandler instance = new HotelsConfigHandler();
		
		public static HotelsConfigHandler getInstance() {
		        return instance;
		}
	    //Anything from here can be modified, this was copied and pasted from my other plugin to save time
	    public void setupConfig(Plugin plugin){
	      plugin.getLogger().info("[Hotels] Generating config file...");
	      plugin.getConfig().options().header("Hotels Plugin by kernitus");
	      plugin.getConfig().addDefault("settings.language", String.valueOf("en"));
	      plugin.getConfig().options().copyDefaults(true);
	      plugin.saveConfig();
	      plugin.getLogger().info("[Hotels] Config file generated");
	    }
	    
	    public void setupLanguageEnglish(Plugin plugin){
	      plugin.getConfig().addDefault("settingsChat.firstLine", String.valueOf("Hotels"));
	      plugin.getConfig().addDefault("settingsChat.prefix", String.valueOf("[Hotels]"));
	      
	      plugin.getConfig().addDefault("HCM.bossBar", Boolean.valueOf(false));
	      
	      plugin.getConfig().addDefault("onCommand.messageReloadConfig", String.valueOf("Configuration file reloaded"));
	      plugin.getConfig().addDefault("onCommand.messageVersion", String.valueOf("version"));
	      plugin.getConfig().addDefault("onCommand.messageNoPermission", String.valueOf("You are not allowed to use that command"));
	      
	      plugin.getConfig().options().copyDefaults(true);
	      plugin.saveConfig();
	      plugin.getLogger().info("[Hotels] Language strings generated");
	    }
	    
	    public void setLanguageEnglish(Plugin plugin){
	        plugin.getConfig().set("settingsChat.firstLine", String.valueOf("Private"));
	        plugin.getConfig().set("settingsChat.prefix", String.valueOf("[Hotels]"));
	        
	        plugin.getConfig().set("onCommand.messageReloadConfig", String.valueOf("Configuration file reloaded"));
	        plugin.getConfig().set("onCommand.messageVersion", String.valueOf("version"));
	        plugin.getConfig().set("onCommand.messageNoPermission", String.valueOf("You are not allowed to use that command"));

	        plugin.saveConfig();
	        plugin.getLogger().info("[Hotels] Language strings generated");
	      }
}