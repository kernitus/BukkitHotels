package kernitus.plugin.Hotels.updates;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import sun.misc.IOUtils;

import java.net.URL;

public class SpigetUpdateChecker {

    private final String VERSIONS_URL = "https://api.spiget.org/v2/resources/2047/versions?size=15000";
    private final String UPDATES_URL = "https://api.spiget.org/v2/resources/2047/updates?size=15000";
    private String latestVersion = "";

    public boolean getNewUpdateAvailable(){
        try {
            JSONArray versionsArray = (JSONArray) JSONValue.parseWithException((new URL(String.valueOf(VERSIONS_URL))).toString());
            latestVersion = ((JSONObject) versionsArray.get(versionsArray.size() -1)).get("name").toString();
            return HTUpdateChecker.shouldUpdate(latestVersion);

        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public String getLatestVersion(){
        return latestVersion;
    }

    public String getUpdateURL(){
        try {
            JSONArray updatesArray = (JSONArray) JSONValue.parseWithException((new URL(String.valueOf(UPDATES_URL))).toString());
            String updateId = ((JSONObject) updatesArray.get(updatesArray.size() - 1)).get("id").toString();
            String latestUpdateLink = "https://www.spigotmc.org/resources/hotels.2047/update?update=" + updateId;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "Error getting update URL";
    }
}
