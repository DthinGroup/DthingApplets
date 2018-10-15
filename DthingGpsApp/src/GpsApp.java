import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import iot.oem.comm.CommConnectionImpl;

import com.yarlungsoft.iot.mqttv3.MqttConnectOptions;
import com.yarlungsoft.iot.mqttv3.MqttException;
import com.yarlungsoft.iot.mqttv3.simple.ISimpleMqttClient;
import com.yarlungsoft.iot.mqttv3.simple.SimpleMqttClient;
import com.yarlungsoft.iot.mqttv3.simple.SimpleMqttClientID;

import jp.co.cmcc.event.Applet;
import jp.co.cmcc.event.Event;


public class GpsApp extends Applet {
	
	private final int SERIAL_PORT_1 = 1;
	private final int BAUD_RATE_57600 = 57600;
	private CommConnectionImpl comm;
	private static SimpleMqttClient mqtt;
	private final static String PROTOCOL = "tcp";
	private final static String HOST = "47.96.64.38";
	private final static int PORT = 1883;
	private final String TOPIC = "dthing/up/yarlung/app/data";
	private boolean allowRunning = true;
	
	public GpsApp() {
		// TODO Auto-generated constructor stub
	}

	public void cleanup() {
		allowRunning = false;
		notifyDestroyed();

	}

	public void processEvent(Event arg0) {
		// TODO Auto-generated method stub

	}

	public void startup() {
		System.out.println("- - - - - - - Dthing GPS Function Display App Ser47 - - - - - - -");
		startMqtt();
		handleSensor();

	}
	private static void connect(){
		int res = ISimpleMqttClient.FAILURE;
		if(mqtt !=null){
			try {
				mqtt.disconnect();
				mqtt.close();
				mqtt = null;
			} catch (MqttException e) {
				e.printStackTrace();
			}			
		}		
		mqtt = SimpleMqttClient.getInstance();
		mqtt.setClientIdentify(SimpleMqttClientID.CLIENT_ID_DEFAULT);		
		String Id = "dthing-net-app"+new Random().nextInt(100);
		MqttConnectOptions opt = new MqttConnectOptions(Id);	
		while(true){
			try{
				opt.setPassword("wmstest1234");
				opt.setUserName("wmstest");
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
	
	private void handleSensor(){
		comm = CommConnectionImpl.getComInstance(SERIAL_PORT_1, BAUD_RATE_57600);
		try {					
			InputStream is = comm.openInputStream();
			byte[] buff = new byte[25];
			do {		
//				System.out.println("comm is waiting data......");
				int readSize= is.read(buff);	
				if (readSize > 0) {					
					parser(buff);
				}
				Thread.sleep(1);
			} while (allowRunning);
		} catch (IOException e) {			
			e.printStackTrace();
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}
	
	private static String encodeGps(int value){
		int inte = value / 10000000;
		int deci = value % 10000000;
		
		String sdeci = "" + deci;
		int len = sdeci.length();
		for(int i=0; i<7-len;i++){
			sdeci = "0" + sdeci;
		}	
		return "" + inte + "." + sdeci;
	}
	
	public void parser(byte[] data){
		if(data == null){
			System.out.println("data is null");
			return;
		}
		int len = data.length;
		if(len < 23){
			return;
		}	
		if((data[0]&0xFF)!=170 ||
		   (data[1]&0xFF)!=255 ||
		   (data[22]&0xFF) != 238 ||
		   (data[21]&0xFF) != 221){
			return;
		}
		if(mqtt != null){
			if(mqtt.isConnected()){
				String ip = byte4ToIp(data[2],data[3],data[4], data[5]);
				String gpsStatus = data[7]== 0?"error":"ok";
				String longti = encodeGps(byte4ToInt(data[8],data[9],data[10], data[11]));
				String lati = encodeGps(byte4ToInt(data[12],data[13],data[14], data[15]));
				
				
				System.out.println("device["+ip+"] is sending info["+"GPS Status:"+gpsStatus
						+", GPS Longtitude"+longti
						+", GPS Latitude:"+lati+"] to host: "+HOST+", topic: "+TOPIC);
				try {
					mqtt.publish(TOPIC, ("device["+ip+"] gps info [status:"+gpsStatus+",lo:"+longti+",la:"+lati+"]").getBytes(), 0, false);
				} catch (MqttException e) {
					e.printStackTrace();
				}
			}else{
				startMqtt();
			}
		}
	}
	
	private void startMqtt(){
		new Thread(new Runnable() {		
			public void run() {
				connect();		
			}
		}).start();
	}


	public static int byte2ToInt(byte val1, byte val2){
		return (int)(((val1 & 0xff) << 8) | 
					  (val2 & 0xff)
					);
	}
	
	public static String byte4ToIp(byte val1, byte val2, byte val3, byte val4){
		return (val1 & 0xff )+"." +
					 (val2 & 0xff )+"." +
					 (val3 & 0xff )+"." +
					  (val4 & 0xff );
	}

	public static int byte4ToInt(byte val1, byte val2, byte val3, byte val4){
		return (int)(((val1 & 0xff )<< 24) | 
					 ((val2 & 0xff )<< 16) |
					 ((val3 & 0xff )<<  8) | 
					  (val4 & 0xff ) 
					 );
	}

	public static String BytesToHexStringEx(byte[] b,int type) {
	    String result = null;
	    int datalen = b.length;
	    char[] bc = new char[datalen * 2];
	    for (int i = 0; i < datalen; i++) {
	    	bc[i*2] = ByteToChar((b[i]&0xf0)>>4);
	    	bc[i*2 + 1] = ByteToChar(b[i]&0xf);
	    }
	    result = new String(bc);
	    if(type == 0){
	    	 return result;
	    }else{
	    	 return result.substring(datalen * 2-2,datalen * 2);	
	    }
	          
	}
	public static char ByteToChar(int b) {
		char ch = 0;
		switch(b) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
			ch = (char)(b + '0');
			break;
		case 10:
			ch = (char)'A';
			break;
		case 11:
			ch = (char)'B';
			break;
		case 12:
			ch = (char)'C';
			break;
		case 13:
			ch = (char)'D';
			break;
		case 14:
			ch = (char)'E';
			break;
		case 15:
			ch = (char)'F';
			break;
		default:
			break;
		}
		return ch;
	} 
}
