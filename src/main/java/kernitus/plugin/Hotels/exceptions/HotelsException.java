package kernitus.plugin.Hotels.exceptions;

public class HotelsException extends Exception {

	private static final long serialVersionUID = 1L;

	public HotelsException() {
	}
	
	@Override
	public String getMessage(){
		return "Error: " + getClass().getSimpleName();
	}
}