package Xinyue.all.activity;




import java.io.File;
import java.util.ArrayList;





import music.dreamer.useful.BaseActivity;
import music.dreamer.useful.InfoHelper;
import music.dreamer.useful.MusicService;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**����ѡ���3D��תҳ��*/
public class RotateActivity extends BaseActivity implements OnTouchListener  {
	
	
	protected static ArrayList<Activity> All_activityList = new ArrayList<Activity>();
	public static  int REQUEST_CODE_GETIMAGE_BYCAMERA = 1;
    public static  String thisLarge = null, theSmall = null;
	private ViewGroup layoutmain;
	private ViewGroup layoutnext;
	private ViewGroup layoutlast;
	
	private Rotate3D rotate3d;
	private Rotate3D rotate3d2;
	private Rotate3D rotate3d3;
	private int mCenterX ;		
	private int mCenterY ;		
	private float degree = (float) 0.0;
	private int currentTab = 0;
	private float perDegree;
	private VelocityTracker mVelocityTracker;
	
	private boolean areButtonsShowing;
	private RelativeLayout composerButtonsWrapper;
	private ImageView composerButtonsShowHideButtonIcon;
	private RelativeLayout composerButtonsShowHideButton;
	
	//�������ؿؼ�����
	private ImageButton ZuozheButton;
	private ImageButton AboutButton;
	private ImageButton CameraButton;
	private ImageButton ExitButton;
	private ImageButton music;
	private ImageButton video;
	private ImageView MusicButton;
	private ImageView VideoButton;

	public Intent intent_SERVICE;
	public Intent video_intent;
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	All_activityList.add(this);
        initMain();
        MyAnimations.initOffset(this);
 
		// �ӺŵĶ���
		composerButtonsShowHideButton.startAnimation(MyAnimations.getRotateAnimation(0, 360, 200));
        DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		mCenterX = dm.widthPixels / 2;
		mCenterY = dm.heightPixels / 2;
		perDegree = (float) (90.0 / dm.widthPixels);
		
		//ΪImageButton�ؼ�����¼�����
		MusicButton.setOnClickListener(new MusicButtonListener());
		VideoButton.setOnClickListener(new VideoButtonListener());
		//ImageButton�ĳ�ʼ���Լ��¼������ĸ���
		CameraButton = (ImageButton)findViewById(R.id.composer_button_photo);
		ZuozheButton = (ImageButton)findViewById(R.id.composer_button_people);
		AboutButton = (ImageButton)findViewById(R.id.composer_button_place);
		music= (ImageButton)findViewById(R.id.composer_button_music);
		video=(ImageButton)findViewById(R.id.composer_button_thought);
		ExitButton = (ImageButton)findViewById(R.id.composer_button_sleep);
		CameraButton.setOnClickListener(new CameraButtonListener());
		ZuozheButton.setOnClickListener(new ZuozheButtonListener());
		AboutButton.setOnClickListener(new AboutButtonListener());
		music.setOnClickListener(new MusicButtonListener());
		video.setOnClickListener(new VideoButtonListener());
		ExitButton.setOnClickListener(new ExitButtonListener());
	}
	

	class MusicButtonListener implements OnClickListener{
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			 //Intentһ�����ֺ�̨���񣬲������������
	        intent_SERVICE = new Intent();
	        intent_SERVICE.setAction("music.dreamer.media.MUSIC_SERVICE");
	        intent_SERVICE.putExtra("list", 1);
	        startService(intent_SERVICE);
			
			Intent music1 = new Intent();
		    music1.setClass(RotateActivity.this, MainActivity.class);
			startActivity(music1);			
			overridePendingTransition(R.anim.s,R.anim.a); 
			Toast.makeText(RotateActivity.this,"���л������ֲ�������",Toast.LENGTH_SHORT).show();	
		}
	}
	class VideoButtonListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			//Intentһ����̨���񣬲������������
			video_intent = new Intent();
	        video_intent.setAction("music.dreamer.media.VIDEO_SERVICE");
	        startService(video_intent);
			
			Intent video1 = new Intent();
			video1.setClass(RotateActivity.this, VideoPlayerActivity.class);
			startActivity(video1);
		    Toast.makeText(RotateActivity.this,"���л���Ӱ�Ӳ�������",Toast.LENGTH_SHORT).show();		
		}
	}
	
	class ZuozheButtonListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			openDialog();				
			Toast.makeText(RotateActivity.this,"Dreamer�ŶӼ��",Toast.LENGTH_SHORT).show();
           
		}
	}
	class CameraButtonListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent takephoto = new Intent("android.media.action.IMAGE_CAPTURE");	
			
			String camerName = InfoHelper.getFileName();
			String fileName = "Share" + camerName + ".tmp";	
			
			File camerFile = new File( InfoHelper.getCamerPath(), fileName );
					
			theSmall = InfoHelper.getCamerPath() + fileName;
			thisLarge = getLatestImage();
			
			Uri originalUri = Uri.fromFile( camerFile );
			takephoto.putExtra(MediaStore.EXTRA_OUTPUT, originalUri); 	
			startActivityForResult(takephoto, REQUEST_CODE_GETIMAGE_BYCAMERA);
           
		}
	}
	class AboutButtonListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			openDatilDialog();
			Toast.makeText(RotateActivity.this,"����Ӱ������",Toast.LENGTH_SHORT).show();
		}
	}
	class ExitButtonListener implements OnClickListener{

		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (VideoPlayerActivity.uri != null) {
				RotateActivity.this.stopService(video_intent); 
			}
			if(MusicService.mp!=null){
				MusicService.nm.cancelAll();
				RotateActivity.this.stopService(intent_SERVICE); 
	 			onDestroy();		 			
				System.exit(0);
        	}
			else{
				Toast.makeText(RotateActivity.this,"�ף��´��ټ�����",Toast.LENGTH_SHORT).show();
				RotateActivity.this.finish();
				onDestroy();
				System.exit(0);
			}
			
		}
	}
	private void setListener() {
		// ����ť���õ���¼�
		composerButtonsShowHideButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!areButtonsShowing) {
					// ͼ��Ķ���
					MyAnimations.startAnimationsIn(composerButtonsWrapper, 300);
					// �ӺŵĶ���
					composerButtonsShowHideButtonIcon.startAnimation(MyAnimations.getRotateAnimation(0, -225, 300));
				} else {
					// ͼ��Ķ���
					MyAnimations.startAnimationsOut(composerButtonsWrapper, 300);
					// �ӺŵĶ���
					composerButtonsShowHideButtonIcon.startAnimation(MyAnimations.getRotateAnimation(-225, 0, 300));
				}
				areButtonsShowing = !areButtonsShowing;
			}
		});

		// ��Сͼ�����õ���¼�
		for (int i = 0; i < composerButtonsWrapper.getChildCount(); i++) {
			final ImageView smallIcon = (ImageView) composerButtonsWrapper.getChildAt(i);
			final int position = i;
			smallIcon.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					// ����д����item�ĵ���¼�
					// 1.�ӺŰ�ť��С����ʧ ��С��animation
					// 2.������ť��С����ʧ ��С��animation
					// 3.�������ť�Ŵ����ʧ ͸���Ƚ��� �Ŵ󽥱��animation
					if(areButtonsShowing){
						composerButtonsShowHideButtonIcon.startAnimation(MyAnimations.getRotateAnimation(-225, 0, 300));
						smallIcon.startAnimation(MyAnimations.getMaxAnimation(400));
						for (int j = 0; j < composerButtonsWrapper.getChildCount(); j++) {
							if (j != position) {
								final ImageView smallIcon = (ImageView) composerButtonsWrapper.getChildAt(j);
								smallIcon.startAnimation(MyAnimations.getMiniAnimation(300));
								//MyAnimations.getMiniAnimation(300).setFillAfter(true);
							}
						}
						areButtonsShowing = !areButtonsShowing;
					}
					
					
				}
			});
		}
	}
	//��ʼ3Dҳ�����ʾ����
	private void initMain(){
        setContentView(R.layout.rotate);
        
        composerButtonsWrapper = (RelativeLayout) findViewById(R.id.composer_buttons_wrapper);
		composerButtonsShowHideButton = (RelativeLayout) findViewById(R.id.composer_buttons_show_hide_button);
		composerButtonsShowHideButtonIcon = (ImageView) findViewById(R.id.composer_buttons_show_hide_button_icon);
		
        layoutnext = (ViewGroup) findViewById(R.id.layout_next);
        layoutnext.setOnTouchListener(this);
		MusicButton = (ImageView)findViewById(R.id.image);
		
		
        layoutlast = (ViewGroup) findViewById(R.id.layout_last);
        layoutlast.setOnTouchListener(this);
        VideoButton= (ImageView)findViewById(R.id.image1);
        
		layoutmain = (ViewGroup)findViewById(R.id.layout_main);
		layoutmain.setOnTouchListener(this);
		 setListener();
	}
	
	/*���������*/
	private  void openDatilDialog(){
		LayoutInflater in = RotateActivity.this.getLayoutInflater();
		final View datil = in.inflate(R.layout.datil, null);
		
		new AlertDialog.Builder(this)
				.setTitle("��Զְҵ����ѧԺ")
				.setIcon(R.drawable.alarm)
				.setView(datil)
				.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {

							
							public void onClick(DialogInterface dialog,
									int which) {														
									Toast.makeText(RotateActivity.this,
											"������ϣ���л���Ա������ʹ�ã�", Toast.LENGTH_SHORT)
											.show();
							} 
					
						}).setNegativeButton("ȡ��", null).show();

	                            }
	
	/*��Dreamer�Ŷӽ���*/
	private  void openDialog(){
		LayoutInflater in = RotateActivity.this.getLayoutInflater();
		final View alarm = in.inflate(R.layout.alarm, null);
		
		new AlertDialog.Builder(this)
				.setTitle("��Զְҵ����ѧԺ")
				.setIcon(R.drawable.alarm)
				.setView(alarm)
				.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {

							
							public void onClick(DialogInterface dialog,
									int which) {														
									Toast.makeText(RotateActivity.this,
											"������ϣ���л���Ա��Ŷӵ�֧�֣�", Toast.LENGTH_SHORT)
											.show();
							} 
					
						}).setNegativeButton("ȡ��", null).show();

	                            }
	


	private int mLastMotionX;
	public boolean onTouch(View arg0, MotionEvent event) {
		int x = (int) event.getX();
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();//���VelocityTracker��ʵ��
			}
			mVelocityTracker.addMovement(event);//���¼����뵽VelocityTracker��ʵ����
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			mVelocityTracker.computeCurrentVelocity(1000, 1000); 
			Log.i("test","velocityTraker :"+mVelocityTracker.getXVelocity());
			int dx = x - mLastMotionX;
			if(dx != 0){
				doRotate(dx);
				if(degree > 90){			
					degree = 0;		
					break;
				}
			}else{
				return false;
			}
			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_UP:
			//����units��ֵΪ1000����˼Ϊһ��ʱ�����˶��˶��ٸ�����
			mVelocityTracker.computeCurrentVelocity(1000); 
			float VelocityX = mVelocityTracker.getXVelocity();
			Log.i("test","velocityTraker2:"+mVelocityTracker.getXVelocity());
			if(VelocityX > 500 || VelocityX < -500 ){
				endRotateByVelocity();
			}else{
				endRotate();
			}
			   releaseVelocityTracker();  
               break;  
 
           case MotionEvent.ACTION_CANCEL:  
               releaseVelocityTracker();  
               break;  
		}
		return true;
	}
	private void releaseVelocityTracker() {
		if(null != mVelocityTracker) {  
            mVelocityTracker.clear();  
            mVelocityTracker.recycle();  
            mVelocityTracker = null;  
        }  
		
	}
	//3Dҳ���ѭ��
	private void endRotateByVelocity(){
		if(degree > 0){
			rotate3d = new Rotate3D(degree , 90 , 0, mCenterX, mCenterY);
			rotate3d3 = new Rotate3D( - 90 + degree,0,0, mCenterX, mCenterY);
			rotate3d.setDuration(300);
			rotate3d3.setDuration(300);
			if(currentTab == 0){
				layoutmain.startAnimation(rotate3d);
				layoutlast.startAnimation(rotate3d3);	
			}else if(currentTab == 2){
				layoutlast.startAnimation(rotate3d);
				layoutnext.startAnimation(rotate3d3);
			}else if(currentTab == 1){
				layoutnext.startAnimation(rotate3d);
				layoutmain.startAnimation(rotate3d3);
			}
			
			currentTab =(currentTab - 1)%3;
			if(currentTab < 0){
				currentTab = 2;
			}
		}else if(degree < 0){
			rotate3d = new Rotate3D(degree , -90 , 0, mCenterX, mCenterY);
			rotate3d2 = new Rotate3D( 90 + degree,0,0, mCenterX, mCenterY);
			rotate3d.setDuration(300);
			rotate3d2.setDuration(300);
			if(currentTab == 0){
				layoutmain.startAnimation(rotate3d);
				layoutnext.startAnimation(rotate3d2);	
			}else if(currentTab == 1){
				layoutnext.startAnimation(rotate3d);
				layoutlast.startAnimation(rotate3d2);
			}else if(currentTab == 2){
				layoutlast.startAnimation(rotate3d);
				layoutmain.startAnimation(rotate3d2);
			}
			
			currentTab = (currentTab + 1)%3;
		}
		
		
		System.out.println(">>>>>>>>degree:"+degree +" currentTab:" + currentTab);
		setViewVisibile();
		
		degree = 0;
	
	}
	private void endRotate() {
		if(degree > 45){
			rotate3d = new Rotate3D(degree , 90 , 0, mCenterX, mCenterY);
			rotate3d3 = new Rotate3D( - 90 + degree,0,0, mCenterX, mCenterY);
			rotate3d.setDuration(300);
			rotate3d3.setDuration(300);
			if(currentTab == 0){
				layoutmain.startAnimation(rotate3d);
				layoutlast.startAnimation(rotate3d3);	
			}else if(currentTab == 2){
				layoutlast.startAnimation(rotate3d);
				layoutnext.startAnimation(rotate3d3);
			}else if(currentTab == 1){
				layoutnext.startAnimation(rotate3d);
				layoutmain.startAnimation(rotate3d3);
			}
			
			currentTab =(currentTab - 1)%3;
			if(currentTab < 0){
				currentTab = 2;
			}
		}else if(degree < -45){
			rotate3d = new Rotate3D(degree , -90 , 0, mCenterX, mCenterY);
			rotate3d2 = new Rotate3D( 90 + degree,0,0, mCenterX, mCenterY);
			rotate3d.setDuration(300);
			rotate3d2.setDuration(300);
			if(currentTab == 0){
				layoutmain.startAnimation(rotate3d);
				layoutnext.startAnimation(rotate3d2);	
			}else if(currentTab == 1){
				layoutnext.startAnimation(rotate3d);
				layoutlast.startAnimation(rotate3d2);
			}else if(currentTab == 2){
				layoutlast.startAnimation(rotate3d);
				layoutmain.startAnimation(rotate3d2);
			}
			
			currentTab = (currentTab + 1)%3;
		}else{
			rotate3d = new Rotate3D( degree , 0 , 0, mCenterX, mCenterY);
			rotate3d2 = new Rotate3D(  90 + degree,90,0, mCenterX, mCenterY);
			rotate3d3 = new Rotate3D(  - 90 + degree,- 90,0, mCenterX, mCenterY);
			rotate3d.setDuration(500);
			rotate3d2.setDuration(500);
			rotate3d3.setDuration(500);
			if(currentTab == 0){
				layoutmain.startAnimation(rotate3d);
				layoutnext.startAnimation(rotate3d2);
				layoutlast.startAnimation(rotate3d3);
			}else if(currentTab == 1){
				layoutnext.startAnimation(rotate3d);
				layoutlast.startAnimation(rotate3d2);
				layoutmain.startAnimation(rotate3d3);
			}else if(currentTab == 2){
				layoutlast.startAnimation(rotate3d);
				layoutmain.startAnimation(rotate3d2);
				layoutnext.startAnimation(rotate3d3);
			}
		}
		
		
		System.out.println(">>>>>>>>degree:"+degree +" currentTab:" + currentTab);
		setViewVisibile();
		
		degree = 0;
	}
	private void setViewVisibile() {
		if(currentTab == 0){
			layoutmain.setVisibility(View.VISIBLE);
			layoutnext.setVisibility(View.GONE);
			layoutlast.setVisibility(View.GONE);
		}else if(currentTab == 1){
			layoutmain.setVisibility(View.GONE);
			layoutnext.setVisibility(View.VISIBLE);
			layoutlast.setVisibility(View.GONE);
		}else if(currentTab == 2){
			layoutmain.setVisibility(View.GONE);
			layoutnext.setVisibility(View.GONE);
			layoutlast.setVisibility(View.VISIBLE);
		}
	}
	private void doRotate(int dx) {
		float xd = degree;
		layoutnext.setVisibility(View.VISIBLE);
		layoutmain.setVisibility(View.VISIBLE);
		layoutlast.setVisibility(View.VISIBLE);
		
		degree += perDegree*dx;
		System.out.println(">>>>>>>>>degree:" + degree );
		rotate3d = new Rotate3D(xd , degree , 0, mCenterX, mCenterY);
		rotate3d2 = new Rotate3D( 90 + xd,  90+degree,0, mCenterX, mCenterY);
		rotate3d3 = new Rotate3D(-90+xd, -90+degree,0, mCenterX, mCenterY);	
		if(currentTab == 0){
			layoutmain.startAnimation(rotate3d);
			layoutnext.startAnimation(rotate3d2);
			layoutlast.startAnimation(rotate3d3);
		}else if(currentTab == 1){
			layoutmain.startAnimation(rotate3d3);
			layoutnext.startAnimation(rotate3d);
			layoutlast.startAnimation(rotate3d2);
		}else if(currentTab == 2){
			layoutmain.startAnimation(rotate3d2);
			layoutnext.startAnimation(rotate3d3);
			layoutlast.startAnimation(rotate3d);
		}
		rotate3d.setFillAfter(true);
		rotate3d2.setFillAfter(true);
		rotate3d3.setFillAfter(true);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("��ܰ��ʾ");
			builder.setIcon(R.drawable.dialog_alert_icon);
			builder.setMessage("��ȷ��Ҫ�ر�����Ӱ����������")
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog,int which) {
					if (All_activityList.size() > 0) { 
				         for (Activity activity : All_activityList) { 
				               System.gc();
				               activity.finish(); 
				               } 
				         try {
				        	 if (VideoPlayerActivity.uri != null) {
				 				RotateActivity.this.stopService(video_intent); 
				 			}
				        	if(MusicService.mp!=null){
				        		MusicService.nm.cancelAll();
				        		RotateActivity.this.stopService(intent_SERVICE); 

					 			onDestroy();		 			
								System.exit(0);
								
				        	}
				        	else{
				        		onDestroy();
								System.exit(0);		
				        	}
				        } catch (Exception e) {e.printStackTrace();}
				 		}	
					}
							}).setNegativeButton("ȡ��", null).show();

		}
		return false;
	}

    @Override 
    protected void onDestroy() { 
               super.onDestroy(); 
               All_activityList.remove(this); 	                
           }
}
