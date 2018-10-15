import com.yarlungsoft.iot.mqttv3.MqttConnectOptions;
import com.yarlungsoft.iot.mqttv3.MqttException;
import com.yarlungsoft.iot.mqttv3.simple.ISimpleMqttClient;
import com.yarlungsoft.iot.mqttv3.simple.SimpleMqttClient;
import com.yarlungsoft.iot.mqttv3.simple.SimpleMqttClientID;

import jp.co.cmcc.event.Applet;
import jp.co.cmcc.event.Event;


public class NetApp extends Applet {
	
	private SimpleMqttClient mqtt;
	private final String PROTOCOL = "tcp";
	private final String HOST = "47.96.64.38";
	private final int PORT = 1883;
	private boolean allowRunning = true;
	
	public NetApp() {}

	public void cleanup() {
		allowRunning = false;
		notifyDestroyed();
	}

	public void processEvent(Event arg0) {}

	public void startup() {
		System.out.println("- - - - - - - Dthing Network Function Display Ser47- - - - - - -");
		while(allowRunning){
			try{
				connect();
				Thread.sleep(1000*30);
			}catch (Exception e) {
			}
		}
	}

	private void connect(){
		int res = ISimpleMqttClient.FAILURE;
		if(mqtt !=null){
			try {
				mqtt.disconnect();
				mqtt.close();
				mqtt = null;
				System.out.println("disconnecting "+ HOST +" success.");
			} catch (MqttException e) {
				e.printStackTrace();
			}			
		}		
		mqtt = SimpleMqttClient.getInstance();		
		mqtt.setClientIdentify(SimpleMqttClientID.CLIENT_ID_DEFAULT);		
		String Id = "dthing-net-app";
		MqttConnectOptions opt = new MqttConnectOptions(Id);
		opt.setPassword("wmstest1234");
		opt.setUserName("wmstest");
		while(true){
			try{
				res = mqtt.connect(PROTOCOL, HOST, PORT, opt);			
				if(res == ISimpleMqttClient.SUCCESS){
					System.out.println("connecting " + HOST +" success.");
					break;
				} else {
					Thread.sleep(1000);
				}				
			} catch (Exception e){				
				try {
					System.out.println("app connecting exception happens.");
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}			
		}		
	}
}
