package managers;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class HotelsFileFinder {

	public static ArrayList<String> listFiles(String dir) {
		 
	    File directory = new File(dir);
	 
	    if (!directory.isDirectory()) {
	    	
	      //No Directory
	      return null;
	    }
	 
	    //create a FilenameFilter and override its accept-method
	    FilenameFilter filefilter = new FilenameFilter() {
	 
	    	@Override
	      public boolean accept(File dir, String name) {
	        //if the file extension is .yml return true, else false
	        return name.endsWith(".yml");
	      }
	    };
	    
	    String[] filenames = directory.list(filefilter);
	 
	    ArrayList<String> nameslist = new ArrayList<>();
	    
	    for (String name : filenames) {
	    	nameslist.add(name);
	    	
	    }
	    return nameslist;
	  }
}
