/**
 * 
 */
package kernitus.plugin.Hotels;

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
	Spanish (new String[] {"esES", "es"}),
	Custom (new String[] {"custom"});

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
		for(Language lang : values()){
			for(String currentCode: lang.getCodes()){
				if(currentCode != null && currentCode.equalsIgnoreCase(code))
					return lang;
			}
		}
		return Language.Custom;
	}
}