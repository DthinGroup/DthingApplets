import com.yarlungsoft.util.Log;
import jp.co.cmcc.event.Applet;
import jp.co.cmcc.event.Event;


public class HelloDthingApp extends Applet {

	private static final String TAG = "HelloDthingApp";
	private static final String ALIVE = "I'm alive.....";
	private static final String DESTROY = "I'v been destroyed.....";
	
	public HelloDthingApp() {
		// TODO Auto-generated constructor stub
	}

	public void cleanup() {
		Log.log(TAG, DESTROY);
		notifyDestroyed();
	}

	public void processEvent(Event arg0) {
		// TODO Auto-generated method stub

	}

	public void startup() {
		Log.log(TAG, ALIVE);
	}
	
	

}
