package kernitus.plugin.Hotels.managers;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

public class HTFileFinder {

	public static ArrayList<String> listFiles(String dir) {

		File directory = new File(dir);

		if(!directory.exists() || !directory.isDirectory())
			directory.mkdirs();

		//create a FilenameFilter and override its accept-method
		FilenameFilter filefilter = (dir1, name) -> {
            //if the file extension is .yml return true
            return name.endsWith(".yml");
        };

		String[] filenames = directory.list(filefilter);

		ArrayList<String> nameslist = new ArrayList<>();

		nameslist.addAll(Arrays.asList(filenames));

		return nameslist;
	}
}