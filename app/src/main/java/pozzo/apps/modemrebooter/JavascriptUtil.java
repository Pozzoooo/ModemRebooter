package pozzo.apps.modemrebooter;

/**
 * Created by sarge on 19/01/16.
 */
public class JavascriptUtil {

	public static String clickByName(String name) {
		return "javascript:document.getElementsByName(\"" + name + "\")[0].click();";
	}
}
