/**
 * 
 */
package kernitus.plugin.Hotels;

import java.util.Locale;

/**
 * @author kernitus
 * Converts between language codes and names
 */

public enum Language {
	English (new String[] {"enGB", "en"}),
	Italian (new String[] {"itIT", "it"}),
	Simplified_Chinese (new String[] {"zhCN", "zh"}),
	Traditional_Chinese (new String[] {"zhTW"}),
	French (new String[] {"frFR", "fr"}),
	Russian (new String[] {"ruRU", "ru"}),
	Spanish (new String[] {"esES", "es"});

	private final String[] codes = new String[2];       

	private Language(String[] codes) {
		for(int i = 0; i < codes.length; i++)
			this.codes[i] = codes[i];
	}

	public String[] getCodes(){
		return codes;
	}

	public String getStandardCode(){
		return codes[0];
	}

	public String getHumanName(){
		return name().replaceAll("_", " ");
	}

	public static Language getFromCode(String code){
		
		//If "auto" choose language from current system, if language unavailable default to english
		if(code.equalsIgnoreCase("auto"))
			code = Locale.getDefault().getLanguage();
		
		for(Language lang : values()){
			for(String currentCode: lang.getCodes()){
				if(currentCode != null && currentCode.equalsIgnoreCase(code))
					return lang;
			}
		}
		return Language.English;
	}
}