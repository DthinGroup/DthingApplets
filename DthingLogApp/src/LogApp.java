import com.yarlungsoft.util.Log;

import jp.co.cmcc.event.Applet;
import jp.co.cmcc.event.Event;


public class LogApp extends Applet {
	
	private boolean allowRunning = true;
	private final String LOG_INFO = "hi, i'm a dthing log info...";
	private final String TAG = "LogApp";

	public LogApp() {}

	public void cleanup() {
		System.out.println("clean up");
		allowRunning = false;
		notifyDestroyed();
	}

	public void processEvent(Event arg0) {}

	public void startup() {
		System.out.println("- - - - - - - Dthing Log Function Display - - - - - - -");
		while(allowRunning){
			allowRunning = false;
			Log.log(TAG, LOG_INFO);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}

}
