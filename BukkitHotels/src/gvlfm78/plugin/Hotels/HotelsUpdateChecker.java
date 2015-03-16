package kernitus.plugin.Hotels;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class HotelsUpdateChecker {

    public HotelsUpdateChecker(HotelsMain Huc)
    {
        this.plugin = Huc;
    }

    private HotelsMain plugin;
    private URL filesFeed;

    private String version;
    private String link;

    public HotelsUpdateChecker(HotelsMain plugin, String url){
        this.plugin = plugin;

        try{
        this.filesFeed = new URL(url);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    }

    public boolean updateNeeded(){
        try {
            InputStream input = this.filesFeed.openConnection().getInputStream();
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);

            Node latestFile = document.getElementsByTagName("item").item(0);
            NodeList children = latestFile.getChildNodes();

            this.version = children.item(1).getTextContent().replaceAll("[a-zA-Z ]", "");
            this.link = children.item(3).getTextContent();

            if(!plugin.getDescription().getVersion().equals(this.version)){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    public String getVersion(){
        return this.version;
    }

    public String getLink(){
        return this.link;
    }

}

