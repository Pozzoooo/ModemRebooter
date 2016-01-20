package pozzo.apps.modemrebooter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

/**
 * Basicaly everything =].
 */
public class MainActivity extends AppCompatActivity {
	private WebView webView;
	private LoaderQueue loaderQueue;

	/**
	 * This is what is going on.
	 */
	private void script() {
		String url = "http://192.168.1.1/";

		loaderQueue.add(url)//Load the entry page
				//Click on login
				.add("javascript:document.getElementsByName(\"login\")[0].click();")
				//Reboot page
				.add(url + "tools_system.htm")
				.add("restart");

		loaderQueue.start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_main);

		setupWebview();
		script();
	}

	/**
	 * Create the webview.
	 */
	private void setupWebview() {
		webView = (WebView) findViewById(R.id.webView);
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		loaderQueue = new LoaderQueue();
		webView.setWebViewClient(loaderQueue);
	}

	/**
	 * Loads a page and request something to run after loaded.
	 *
	 * @param toLoad To be loaded now,
	 * @param afterLoad To be loaded after fully loaded.
	 */
	private void load(String toLoad, WebViewClient afterLoad) {
		webView.loadUrl(toLoad);
		webView.setWebViewClient(afterLoad);
	}

	/**
	 * Chain page loads.
	 * Make sure to start the execution with #start().
	 */
	private class LoaderQueue extends WebViewClient {
		private ArrayList<String> tasks = new ArrayList<>();

		/**
		 * Add an entry to the execution queue.
		 */
		public LoaderQueue add(String task) {
			tasks.add(task);
			return this;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			executeSingleValidCommand();
		}

		/**
		 * Start executing tasks.
		 * They will chain if they all load things.
		 */
		public void start() {
			executeSingleValidCommand();
		}

		private void executeSingleValidCommand() {
			//Make sure it is not empty and there is no null element
			while(!tasks.isEmpty()) {
				String next = tasks.remove(0);

				if(next != null) {
					webView.loadUrl(next);
					break;
				}
			}
		}
	}
}
