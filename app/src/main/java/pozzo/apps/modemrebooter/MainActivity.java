package pozzo.apps.modemrebooter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
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
				//Login button
				.add(JavascriptUtil.clickByName("login"))
				//Reboot page
				.add(url + "tools_system.htm")
				//Reboot button
				.add(JavascriptUtil.clickByName("restart"));
				//Reboot command - It seems to not always work =/, not sure yet
//				.add("form6.submit();");

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
		webView.setWebChromeClient(loaderQueue);
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
	private class LoaderQueue extends WebChromeClient {
		private ArrayList<String> tasks;
		private Handler uiHandler;
		//Prevents double 100% page load
		private Runnable current;

		{
			uiHandler = new Handler(Looper.getMainLooper());
			tasks = new ArrayList<>();
		}

		/**
		 * Add an entry to the execution queue.
		 */
		public LoaderQueue add(String task) {
			tasks.add(task);
			return this;
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if(newProgress == 100) {
				System.out.println("executing: 100");
				executeSingleValidCommand();
			}
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
			result.confirm();//We confirm, for any "do you really want" dialog.
			return true;
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
			while(!tasks.isEmpty() && current == null) {
				final String next = tasks.remove(0);

				if(next != null) {
					current = new Runnable() {
						@Override
						public void run() {
							System.out.println("executing: " + next);
							webView.loadUrl(next);
							current = null;
						}
					};
					//300 is good enougth to block dual 100% and is enough to flash on screen.
					uiHandler.postDelayed(current, 300);
					break;
				}
			}
		}
	}
}
