package music.dreamer.useful;

import Xinyue.all.activity.VideoPlayerActivity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class VideoService  extends Service{
	public static  TelephonyManager phone;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
   
	public void onCreate() {
		super.onCreate();
		phone = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
		
		IntentFilter phone = new IntentFilter();
		phone.addAction("android.intent.action.NEW_OUTGOING_CALL");
		phone.addAction("android.intent.action.PHONE_STATE");
		registerReceiver(phoneReceiver, phone);
	}
	/* ʵ����һ���㲥�����߶���phoneReceiver����ʵ�ֶԵ绰ͨ��״̬���¼����� */
	private BroadcastReceiver phoneReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			if (VideoPlayerActivity.uri != null) {
				VideoPlayerActivity.vv.pause();
		
			}
		
			phone.listen(new PhoneStateListener() {

				@Override
				public void onCallStateChanged(int state, String incomingNumber) {

					switch (state) {
					case TelephonyManager.CALL_STATE_OFFHOOK:
						// ����
				
						break;

					case TelephonyManager.CALL_STATE_IDLE:
						
						// �Ҷ�
						break;
			
					}
				}
			}, PhoneStateListener.LISTEN_CALL_STATE);
		}

	};
	public void onDestroy() {
		super.onDestroy();
		System.out.println("Service destroy!");
	
	}
}
