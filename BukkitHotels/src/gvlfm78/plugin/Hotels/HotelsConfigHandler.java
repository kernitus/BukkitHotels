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
	      plugin.getConfig().addDefault("settingsChat.firstLine", String.valueOf("Private"));
	      plugin.getConfig().addDefault("settingsChat.prefix", String.valueOf("[Hotels]"));
	      
	      plugin.getConfig().addDefault("onCommand.messageReloadConfig", String.valueOf("Configuration file reloaded"));
	      plugin.getConfig().addDefault("onCommand.messageVersion", String.valueOf("version"));
	      plugin.getConfig().addDefault("onCommand.messageNoPermission", String.valueOf("You are not allowed to use that command"));
	      
	      plugin.getConfig().addDefault("onPunch.messageActive", String.valueOf("The owner of this lock is still active"));
	      plugin.getConfig().addDefault("onPunch.messageInactive", String.valueOf("The owner of this lock has been inactive for %inactivedays% days"));
	      plugin.getConfig().addDefault("onPunch.messageDaysToWait", String.valueOf("You still have to wait %daystowait% days to open this lock"));
	      
	      plugin.getConfig().addDefault("onUnlock.messageChest", String.valueOf("The chest locked is going to get emptied"));
	      plugin.getConfig().addDefault("onUnlock.messageFurnace", String.valueOf("The furnace locked is going to get emptied"));
	      plugin.getConfig().addDefault("onUnlock.messageDispenser", String.valueOf("The dispenser locked is going to get emptied"));
	      plugin.getConfig().addDefault("onUnlock.messageHopper", String.valueOf("The hopper locked is going to get emptied"));
	      plugin.getConfig().addDefault("onUnlock.messageDropper", String.valueOf("The dropper locked is going to get emptied"));
	      
	      plugin.getConfig().addDefault("massages.messageBroadcast", String.valueOf("A %block% owned by %owner% was unlocked at coordinates %coordinates%"));
	      plugin.getConfig().addDefault("massages.messageMoneyWithdraw", String.valueOf("You have removed this lock for %cost%. New balance: %balance%"));
	      plugin.getConfig().addDefault("massages.messageMoneyTransactionFailed1", String.valueOf("You do not have enough money to remove this lock."));
	      plugin.getConfig().addDefault("massages.messageMoneyTransactionFailed2", String.valueOf("You need %cost% to open a lock."));
	      plugin.getConfig().addDefault("massages.messageMoneyTransactionFailed3", String.valueOf("You have %balance% and require another %moneyneeded%."));
	      
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
	        
	        plugin.getConfig().set("onPunch.messageActive", String.valueOf("The owner of this lock is still active"));
	        plugin.getConfig().set("onPunch.messageInactive", String.valueOf("The owner of this lock has been inactive for %inactivedays% days"));
	        plugin.getConfig().set("onPunch.messageDaysToWait", String.valueOf("You still have to wait %daystowait% days to open this lock"));
	        
	        plugin.getConfig().set("onUnlock.messageChest", String.valueOf("The chest locked is going to get emptied"));
	        plugin.getConfig().set("onUnlock.messageFurnace", String.valueOf("The furnace locked is going to get emptied"));
	        plugin.getConfig().set("onUnlock.messageDispenser", String.valueOf("The dispenser locked is going to get emptied"));
	        plugin.getConfig().set("onUnlock.messageHopper", String.valueOf("The hopper locked is going to get emptied"));
	        plugin.getConfig().set("onUnlock.messageDropper", String.valueOf("The dropper locked is going to get emptied"));
	        
	        plugin.getConfig().set("massages.messageBroadcast", String.valueOf("A %block% owned by %owner% was unlocked at coordinates %coordinates%"));
	        plugin.getConfig().set("massages.messageMoneyWithdraw", String.valueOf("You have removed this lock for %cost%. New balance: %balance%"));
	        plugin.getConfig().set("massages.messageMoneyTransactionFailed1", String.valueOf("You do not have enough money to remove this lock."));
	        plugin.getConfig().set("massages.messageMoneyTransactionFailed2", String.valueOf("You need %cost% to open a lock."));
	        plugin.getConfig().set("massages.messageMoneyTransactionFailed3", String.valueOf("You have %balance% and require another %moneyneeded%."));
	        
	        plugin.saveConfig();
	        plugin.getLogger().info("[Hotels] Language strings generated");
	      }
	    
	    public void setLanguageItalian(Plugin plugin){
	        plugin.getConfig().set("settingsChat.firstLine", String.valueOf("Privato"));
	        plugin.getConfig().set("settingsChat.prefix", String.valueOf("[Hotels]"));
	        
	        plugin.getConfig().set("onCommand.messageReloadConfig", String.valueOf("File di configurazione ricaricato"));
	        plugin.getConfig().set("onCommand.messageVersion", String.valueOf("versione"));
	        plugin.getConfig().set("onCommand.messageNoPermission", String.valueOf("Non hai il permesso di utilizzare quel comando"));
	        
	        plugin.getConfig().set("onPunch.messageActive", String.valueOf("Il proprietario di questo blocco e' ancora attivo"));
	        plugin.getConfig().set("onPunch.messageInactive", String.valueOf("Il proprietario di questo blocco e' stato inattivo per %inactivedays% giorni"));
	        plugin.getConfig().set("onPunch.messageDaysToWait", String.valueOf("Devi ancora aspettare %daystowait% giorni per sbloccare questo blocco"));
	        
	        plugin.getConfig().set("onUnlock.messageChest", String.valueOf("La chest sbloccata sara' svuotata"));
	        plugin.getConfig().set("onUnlock.messageFurnace", String.valueOf("La fornace sbloccata sara' svuotata"));
	        plugin.getConfig().set("onUnlock.messageDispenser", String.valueOf("Il distributore sbloccato sara' svuotato"));
	        plugin.getConfig().set("onUnlock.messageHopper", String.valueOf("La tramoggia sbloccata sara' svuotata"));
	        plugin.getConfig().set("onUnlock.messageDropper", String.valueOf("Il gettatore sbloccato sara' svuotato"));
	        
	        plugin.getConfig().set("massages.messageBroadcast", String.valueOf("Un %block% di proprieta' di %owner% e' stato/a sbloccato/a alle coordinate %coordinates%"));
	        plugin.getConfig().set("massages.messageMoneyWithdraw", String.valueOf("Hai rimosso questo blocco al costo di %cost%. Nuovo bilancio: %balance%"));
	        plugin.getConfig().set("massages.messageMoneyTransactionFailed1", String.valueOf("Nnon hai abbastanza soldi per rimuovere questo blocco."));
	        plugin.getConfig().set("massages.messageMoneyTransactionFailed2", String.valueOf("Ti servono %cost% per rimuovere un blocco."));
	        plugin.getConfig().set("massages.messageMoneyTransactionFailed3", String.valueOf("Hai %balance% e necessiti altri %moneyneeded%."));
	        
	        plugin.saveConfig();
	        plugin.getLogger().info("[Hotels] Stringhe di lingua generate");
	      }
}