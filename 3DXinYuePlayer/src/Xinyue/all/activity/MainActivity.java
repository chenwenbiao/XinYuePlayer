package Xinyue.all.activity;



import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import music.dreamer.adapter.AlbumListAdapter;
import music.dreamer.adapter.ArtistListAdapter;
import music.dreamer.adapter.MusicListAdapter;
import music.dreamer.useful.DBHelper;
import music.dreamer.useful.MusicService;
import music.dreamer.useful.ScanSdReceiver;
import music.dreamer.useful.SetTimeDialog;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;


/**���и�����ʾ��**/
/*This work comes from DreamerؼTeam. The main programmer is LinShaoHan.
 * QQ:752280466   Welcome to join with us.
 */

public class MainActivity extends TabActivity implements TabHost.TabContentFactory{


	protected void onStart() {
		IntentFilter filter = new IntentFilter();
        filter.addAction(MUSIC_LIST);
		registerReceiver(changeItem, filter);
		super.onStart();
	}
	
	static SharedPreferences sharedpreferences;
	private static final String MUSIC_LIST = "music.dreamer.list";
	private ListView listview;
	private int[] _ids;
	private String[] _titles;
	private String[] _artists;
	private String[] albums;
	private String[] artists;
	private String[] _path;	   //�����ļ���·��
	private int pos;		   //���ڲ������ֵ�λ��
	private int num;		   //ѡ��ĸ�����λ��
	private  MusicListAdapter adapter;
	public  static int skin=0;
	private static final String MEMORIES_SKIN="MEMORIES_SKIN";
	protected final static int MENU_ABOUT = Menu.FIRST ;
	protected final static int MENU_MORE = Menu.FIRST + 1;
	protected final static int MENU_EXIT = Menu.FIRST+2;
	public static final int RingtongButton=0;
	//private ScanSdReceiver scanSdReceiver = null;
	public static final String TAG = "TestMain" ; 
	public static ArrayList<Activity> activityList = new ArrayList<Activity>();
    public Intent intent_SERVICE;
	static LinearLayout list;
	private AlertDialog ad = null;
	private AlertDialog.Builder  builder = null;
	private Cursor c;
	private String tag;
	/*�����Ĳ˵���*/
	private static final int PLAY_ITEM = Menu.FIRST;
	private static final int DELETE_ITEM = Menu.FIRST+1;
	private static final int SONG_SHARE = Menu.FIRST+2;
	private static final int SET_AS = Menu.FIRST+3;
	//���������ı���
	private AudioManager mAudioManager = null;
	private int maxVolume;//�������
	private int currentVolume;//��ǰ����
	public static int time;
	public static String search_music;
	public int id;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityList.add(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		TabHost th = getTabHost();
		th.addTab(th.newTabSpec("list").setIndicator(
				"�����б�",getResources().getDrawable(R.drawable.item))
				.setContent(this));
		th.addTab(th.newTabSpec("artists").setIndicator(
				"������",getResources().getDrawable(R.drawable.artist))
				.setContent(this));
		th.addTab(th.newTabSpec("albums").setIndicator(
				"ר  ��",getResources().getDrawable(R.drawable.album))
				.setContent(this));
        //Intentһ�����ֺ�̨���񣬲������������
       /* intent_SERVICE = new Intent();
        intent_SERVICE.setAction("music.dreamer.media.MUSIC_SERVICE");
        intent_SERVICE.putExtra("list", 1);
        startService(intent_SERVICE);*/
      
	}
	//TabHost��ǩ��Ƭ��ť�ļ���������ʵ��
	public View createTabContent(String tag) {
		this.tag = tag;
		if (tag.equals("list")){
			listview = new ListView(this);
			setListData();
		    listview.setOnItemClickListener(new ListItemClickListener());
		    listview.setOnCreateContextMenuListener(new ContextMenuListener());
		} else if (tag.equals("artists")){
			c.moveToFirst();
			int num = c.getCount();
			HashSet set = new HashSet();
			for (int i = 0; i < num; i++){
				String szArtist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
				if (szArtist.equals("<unknown>")){
					set.add("δ֪������");
				}else{
					set.add(szArtist);
				}
				c.moveToNext();
			}
			num = set.size();
			Iterator it = set.iterator();
			artists = new String[num];
			int i = 0;
			while(it.hasNext()){
				artists[i] = it.next().toString();
				i++;
			}
			/*����ÿ������ӵ�еĸ�����*/
			int counts[] = new int[num];

			for(int j = 0; j<num; j++){
				c.moveToFirst();
     		for(int k = 0; k < c.getCount(); k++){
					String szArtist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST)); 
					if (szArtist.equals("<unknown>"))
					{
						szArtist = "δ֪������";
					}
					if (artists[j].equals(szArtist)){
						counts[j]++;
						//n++;
					}
					c.moveToNext();
				}
			}
			
			listview = new ListView(this);
			listview.setAdapter(new ArtistListAdapter(this, artists,counts));
			listview.setOnItemClickListener(new ArtistsItemClickListener());
		} else if (tag.equals("albums")){
			Cursor c = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					new String[]{MediaStore.Audio.Media.TITLE,
					MediaStore.Audio.Media.DURATION,
					MediaStore.Audio.Media.ARTIST,
					MediaStore.Audio.Media.ALBUM,
					MediaStore.Audio.Media._ID,
					MediaStore.Audio.Media.DISPLAY_NAME}, 
					null, 
					null,
					MediaStore.Audio.Media.ALBUM);
			c.moveToFirst();
			int num = c.getCount();
			HashSet set = new HashSet();
			for (int i = 0; i < num; i++){
				String szAlbum = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM));
				set.add(szAlbum);
				c.moveToNext();
			}
			num = set.size();
			Iterator it = set.iterator();
			albums = new String[num];
			int i = 0;
			while(it.hasNext()){
				albums[i] = it.next().toString();
				i++;
			}
			String album="";
			for (int j=0;j<num; j++){
				if (j<num-1){
					album = album + "'" + albums[j] + "',"; 
				} else{
					album = album + "'" + albums[j] + "'";
				}
			}
			
			Cursor c1 = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					new String[]{
					MediaStore.Audio.Media.TITLE,
					MediaStore.Audio.Media.DURATION,
					MediaStore.Audio.Media.ARTIST,
					MediaStore.Audio.Media.ALBUM,
					MediaStore.Audio.Media._ID,
					MediaStore.Audio.Media.DISPLAY_NAME,
					}, 
					null,
					null,
					MediaStore.Audio.Media.ALBUM);
			c1.moveToFirst();
			HashMap<String, String> map = new HashMap<String, String>();
			int num1 = c1.getCount();
			for (int j=0;j<num1;j++){
				map.put(c1.getString(3), c1.getString(2));
				c1.moveToNext();
			}
			listview = new ListView(this);
			listview.setAdapter(new AlbumListAdapter(this, albums,map));
			listview.setOnItemClickListener(new AlbumsItemClickListener());
		}else if (tag.equals("recent")){
			DBHelper dbHelper = new DBHelper(this, "music.db", null, 2);
			Cursor cursor = dbHelper.queryRecently();
			cursor.moveToFirst();
			int num = 0;
			int[] music_id;
			if (cursor!=null){
				num = cursor.getCount();
			} else{
				return null;
			}
			String idString ="";
			if (num>=10){
				for(int i=0;i<10;i++){
					music_id = new int[10];
					music_id[i]=cursor.getInt(cursor.getColumnIndex("music_id"));
					if (i<9){
						idString = idString+music_id[i]+",";
					} else{
						idString = idString+music_id[i];
					}
					cursor.moveToNext();
				} 
			}else if(num>0){
				for(int i=0;i<num;i++){
					music_id = new int[num];
					music_id[i]=cursor.getInt(cursor.getColumnIndex("music_id"));
					if (i<num-1){
						idString = idString+music_id[i]+",";
					} else{
						idString = idString+music_id[i];
					}
					cursor.moveToNext();
				}
			}
			if (cursor!=null){
				cursor.close();
				cursor=null;
			}
			if (dbHelper!=null){
				dbHelper.close();
				dbHelper = null;
			}
			listview = new ListView(this);
			listview.setCacheColorHint(00000000);
			Cursor c = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
	        			new String[]{MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media._ID,
						MediaStore.Audio.Media.DISPLAY_NAME,
						MediaStore.Audio.Media.ALBUM_ID,} , MediaStore.Audio.Media._ID + " in ("+ idString + ")", null,null);
			
			  c.moveToFirst();
		      _ids = new int[c.getCount()];
		      _titles = new String[c.getCount()];
		      for(int i=0;i<c.getCount();i++){
		    	  _ids[i] = c.getInt(3);
		          _titles[i] = c.getString(0);
		        	
		          c.moveToNext();
		      }
		      listview.setAdapter(new MusicListAdapter(this, c));
		      listview.setOnItemClickListener(new ListItemClickListener());
		}
		listview.setCacheColorHint(00000000);
        
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE); 
		maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//����������  
		currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//��õ�ǰ���� 
	    list = new LinearLayout(this);

		  sharedpreferences=getSharedPreferences(MEMORIES_SKIN,0);	
			skin=sharedpreferences.getInt("skin_value",0);
			try{
				//Ƥ����¼  
				 if(skin==0){
				    	System.gc();
				    	list.setBackgroundResource(R.drawable.listbg);  
				    }
				 else if(skin==1){
				    	System.gc();	
				    	
				    	list.setBackgroundResource(R.drawable.back);  
				    }
				    else if(skin==2){
				    	System.gc();
				    	list.setBackgroundResource(R.drawable.back0);  
				    					
				    }
				    else if(skin==3){
				    	System.gc();		  
				    	list.setBackgroundResource(R.drawable.back1);  
				    }
				    else if(skin==4){
				    	System.gc();
				    	list.setBackgroundResource(R.drawable.back2); 	    	
				    }
				
				    else if(skin==5){
				    	System.gc();
				    	list.setBackgroundResource(R.drawable.back3);  				   
				    }

			      }catch(Exception e){System.out.println(e);}
			
	        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        list.removeAllViews();
        list.addView(listview,params);
		return list;
	}
	//���������ʵ�ַ���
	public static void Theme(){
		System.gc();
		skin++;
		if(skin>=6){skin=0;}
	 
		sharedpreferences.edit().putInt("skin_value",skin).commit();	
		skin=sharedpreferences.getInt("skin_value",0);
		 if(skin==0){
		    	System.gc();		    	
		    	list.setBackgroundResource(R.drawable.listbg);  			  
		    }
		 else if(skin==1){
		    	System.gc();
		    	list.setBackgroundResource(R.drawable.back);  
		    }
		    else if(skin==2){
		    	System.gc();
		    	list.setBackgroundResource(R.drawable.back0);  

		    }
		    else if(skin==3){
		    	System.gc();
		    	list.setBackgroundResource(R.drawable.back1);     
		    }
		    else if(skin==4){
		    	System.gc();
		    	list.setBackgroundResource(R.drawable.back2);     
		    }
		
		    else if(skin==5){
		    	System.gc();
		    	list.setBackgroundResource(R.drawable.back3);     
		    }
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_ABOUT://������������
			 MoreSetting();						
			break;	
			
		case MENU_MORE://������ز���	
			setMore();
			break;
			
		case MENU_EXIT://�˳�
			/*android.os.Process.killProcess(android.os.Process.myPid());*/
			/*���ﱻע�͵������ΪϵͳĬ�ϵĻ����������*/
			System.gc();
			exitApp();
			
			break;
		}
		return true;
	}
	/*����ײ���menu�˵�ʵ��*/
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		setMenuBackground();
		menu.add(Menu.NONE, MENU_ABOUT, 0, "��  ��").setIcon(R.drawable.common_account);
		menu.add(Menu.NONE, MENU_MORE, 0, "��  ��").setIcon(R.drawable.menu_more);
		menu.add(Menu.NONE, MENU_EXIT, 0, "��  ��").setIcon(R.drawable.menu_exit);
		//return true;
		return super.onCreateOptionsMenu(menu); 
	}
	
	protected void setMenuBackground() {
		this.getLayoutInflater().setFactory(
		new android.view.LayoutInflater.Factory() {
		    public View onCreateView(String name, Context context,AttributeSet attrs)
		    {
		// ָ���Զ���inflate�Ķ���
	 	           if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")
		          | name.equalsIgnoreCase("com.android.internal.view.menu.ListMenuItemView")) {
		        try {
		              LayoutInflater f = getLayoutInflater();
	                  final View view = f.createView(name, null,attrs);
		              new Handler().post(new Runnable() {
		            public void run() {
		           // ���ñ���ͼƬ
	                	view.setBackgroundResource(R.drawable.button);	}});
		                return view;
		              } catch (InflateException e){e.printStackTrace();
		           } catch (ClassNotFoundException e) {
		          e.printStackTrace();
	 	          }
		    }
		return null;
		          }
                              });
		                              }
	/*�����ʵ������˯��ʱ���Handler*/
	static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
		//Ҫ��������
		super.handleMessage(msg);
		android.os.Process.killProcess(android.os.Process.myPid());
		   }
		};
		
	public static class MyThread implements Runnable{
		
		public void run() {
		// TODO Auto-generated method stub
		while (true) {
		try {
		Thread.sleep(time*60000);//�߳���ͣʱ��XX����
		Message message=new Message();
		message.what=1;
		handler.sendMessage(message);//������Ϣ
		} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		      }
		    }
		  }
		}

	
		/*������������*/
	private void MoreSetting() {
			
			 String[] items={
					 "˯�߶�ʱ",
					 "���÷�ɫ",
					 "ҡ˦����"
				}; 
			 
			 AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)  
			           .setIcon(R.drawable.music)  
			           .setTitle("�������")
			           .setItems(items, onMoreSetting).create();  
			 dialog.show();  
		}
 
	  OnClickListener onMoreSetting = new OnClickListener() {  
		    public void onClick(DialogInterface dialog, int which) {  
		          
		      switch(which)
		      {
		        /*˯�߶�ʱ*/
		        case 0:
		        	
		        	SetTimeDialog.getCreatePlaylistDialog(
		        			MainActivity.this).create().show();     	
	        	
					break;
			  case 1://����
					Intent intent = new Intent();
		    		intent.setClass(MainActivity.this, ChangeNew.class);
		    		startActivity(intent);
		    		finish();	
					break;
					
			  case 2://ҡһҡ������
				  moveDialog();			
					break;
		      }
		    
		   }
	    };	
		
		
	//�������ز���
	private void setMore() {
		
		 String[] items={			
				 "��������",
				 "ˢ�¸���",
				 "ת��Ӱ�Ӳ���" 
			}; 
		 
		 AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)  
		           .setIcon(R.drawable.music)  
		           .setTitle("�������")
		           .setItems(items, onSetMoreSelect).create();  
		 dialog.show();  
	}

 
	OnClickListener onSetMoreSelect = new OnClickListener() {  
	    public void onClick(DialogInterface dialog, int which) {  
	          
	      switch(which)
	      {
	       
				
	        case 0:/*��������*/
	        	Intent search = new Intent();
				search.setClass(MainActivity.this, SearchActivity.class);
				startActivity(search);
				finish();
				break;
				
	        case 1:/*����ˢ��*/
	        	ScanSdReceiver scanSdReceiver = null;
	        	IntentFilter intentfilter = new IntentFilter( Intent.ACTION_MEDIA_SCANNER_STARTED);
		    	intentfilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		    	intentfilter.addDataScheme("file");
		    	scanSdReceiver = new ScanSdReceiver();
		    	registerReceiver(scanSdReceiver, intentfilter);
		    	sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
		    	Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath())));

	        	Intent song = new Intent();
	 			song.setClass(MainActivity.this, NewSongs.class);
	 			startActivity(song);
	 			finish();
	    		break;
	    		
	        case 2:/*ת��Ӱ�Ӳ���*/
	        	
	     /*��ת����Ƶ������֮ǰ������Ҫ�жϲ��������Ƿ�Ϊ�գ���Ϊ�������ں�̨���в��ŵĸ�������ǿ��ֹͣ*/
	        	if (MusicService.mp != null) {
	        		MusicService.mp.stop();	        		
	    		}	        	
	        	Intent intent = new Intent();
				intent.setClass(MainActivity.this, VideoPlayerActivity.class);
				startActivity(intent);			
				Toast.makeText(MainActivity.this,"���л���Ӱ�Ӳ�������",Toast.LENGTH_SHORT).show();
				finish();				
	        	break;

	      }
	    
	   }
    };
    
	/*��������*/
	private void playMusic(int position){
		Intent intent = new Intent(MainActivity.this,MusicActivity.class);
		intent.putExtra("_ids", _ids);
		intent.putExtra("_titles", _titles);
		intent.putExtra("_artists", _artists);
		intent.putExtra("position", position);
		startActivity(intent);
		finish();
	}
	
	/*���б���ɾ��ѡ�е�����*/
	private void deleteMusic(int position){
		this.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 
				MediaStore.Audio.Media._ID + "=" + _ids[position], 
				null);
	}
	
	/*��sdcard��ɾ��ѡ�е�����*/
	private void deleteMusicFile(int position){
		File file = new File(_path[position]);
		System.out.println(_path[position]);
		try {
			deleteFile(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//ɾ�������ļ��ķ���
	public static void deleteFile(File f) throws Exception {
		if (f.isFile()) {
			if (f.canWrite()) {
				f.delete();
			} else {
				throw new Exception("�ļ���" + f.getName() + "ֻ�����޷�ɾ�������ֶ�ɾ��");
			}
		} else {
			File[] fs = f.listFiles();
			if (fs.length != 0) {
				for (int i = 0; i < fs.length; i++) {
					deleteFile(fs[i]);
				}
			}
		}
	}
	class ListItemClickListener implements OnItemClickListener{

    	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
    		// TODO Auto-generated method stub
    		playMusic(position);
    	}
    	
    }
	/*���������������б��intent�����ʵ��*/
	class ArtistsItemClickListener implements OnItemClickListener{

    	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
    		Intent intent = new Intent();
    		intent.setClass(MainActivity.this, ArtistActivity.class);
    		intent.putExtra("artist", artists[position]);
    		startActivity(intent);
    	}
    }
	
	/*����ר�������б��intent�����ʵ��*/
	class AlbumsItemClickListener implements OnItemClickListener{
	
    	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
    		Intent intent = new Intent();
    		intent.setClass(MainActivity.this, AlbumActivity.class);
    		intent.putExtra("albums", albums[position]);
    		startActivity(intent);
    	}
    }
	/*ҡһҡ����������*/
	private  void moveDialog(){
		LayoutInflater in = MainActivity.this.getLayoutInflater();
		final View alarm = in.inflate(R.layout.share, null);
		
		new AlertDialog.Builder(this)
				.setTitle("ҡ˦������")
				.setIcon(R.drawable.moving)
				.setView(alarm)
				.setPositiveButton("����",
						new DialogInterface.OnClickListener() {

						
							public void onClick(DialogInterface dialog,
									int which) {	
								  MusicService.moveshare=true;
									Toast.makeText(MainActivity.this,
											"�ѿ���ҡ˦����������,����ת������ҳ����м��", Toast.LENGTH_LONG)
											.show();
							} 
					
						}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

							
							public void onClick(DialogInterface dialog,
									int which) {		
								    MusicService.moveshare=false;
								    MusicService.sd.stop();
									Toast.makeText(MainActivity.this,
											"��ȡ��ҡ˦���������ܣ�", Toast.LENGTH_LONG)
											.show();
							} 
					
						}).show();

	                            }

	 /*��ȫ�˳�Ӧ�ó��� */
	public void exitApp() { 
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
		builder.setTitle("��ܰ��ʾ"); 
		builder.setIcon(R.drawable.dialog_alert_icon);
		builder.setMessage("��ȷ��Ҫ�˳�����Ӱ���е����ֲ���ģʽ��?"); 
		builder.setPositiveButton("ȷ��", new OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) { 
		         if (activityList.size() > 0) { 
		         for (Activity activity : activityList) { 
		               System.gc();
		               activity.finish(); 
		              
		               } 
		         try {
		        	 if(MusicService.mp!=null){
		        		 MusicService.nm.cancelAll();
				 		 MainActivity.this.stopService(intent_SERVICE); 
		        	 }
		        	
		 		} catch (Exception e) {
		 			// TODO Auto-generated catch block
		 			e.printStackTrace();
		 		}
		      android.os.Process.killProcess(android.os.Process.myPid()); 
		          } 
		      } 
		 });
		builder.setNegativeButton("ȡ��", null); 
		builder.show(); 
	}

	@Override 
	protected void onDestroy() { 
	        super.onDestroy(); 
	        activityList.remove(this); 
	       Log.i(TAG, activityList.toString()) ; 
	}
     /*�����ĵ���ʵ��*/
	public boolean dispatchKeyEvent(KeyEvent event) { 
		int action = event.getAction(); 
		int keyCode = event.getKeyCode(); 
		switch (keyCode) { 
			case KeyEvent.KEYCODE_VOLUME_UP: 
			if (action == KeyEvent.ACTION_UP) {
				if (currentVolume<maxVolume){
					currentVolume = currentVolume + 1;
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
				} else {
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
				}
			} 
			return false; 
			case KeyEvent.KEYCODE_VOLUME_DOWN: 
			if (action == KeyEvent.ACTION_UP) { 
				if (currentVolume>0){
					currentVolume = currentVolume - 1;
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume , 0);
				} else {
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
				}
			} 
			return false; 
			default: 
			return super.dispatchKeyEvent(event); 
		} 
	}
    private BroadcastReceiver changeItem = new BroadcastReceiver() {
		
		
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(MUSIC_LIST)){
				pos = intent.getExtras().getInt("position");
				adapter.setItemIcon(pos);//�ı��б���ͼ��
				adapter.notifyDataSetChanged();//֪ͨUI����
				System.out.println("List Update...");
			}
			
		}
	};
	
	private void setListData(){
		c = this.getContentResolver()
		.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[]{
				MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.DURATION,
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.ALBUM_ID},
				null, null, null);
	    if (c==null || c.getCount()==0){
	    	builder = new AlertDialog.Builder(this);
			builder.setMessage("�ܱ�Ǹ�����ֿ�����Ϊ�գ�").setPositiveButton("ȷ��", null);
			ad = builder.create();
			ad.show();
			return;
	    }
	    c.moveToFirst();
	    _ids = new int[c.getCount()];
	    _titles = new String[c.getCount()];
	    _artists = new String[c.getCount()];
	    _path = new String[c.getCount()];
	    for(int i=0;i<c.getCount();i++){
	    	_ids[i] = c.getInt(3);
	    	_titles[i] = c.getString(0);
	    	_artists[i] = c.getString(2);
	    	_path[i] = c.getString(5).substring(4);
	    	c.moveToNext();
	    }
	    adapter = new MusicListAdapter(this, c);
	    listview.setAdapter(adapter);
	}
	
	
	protected void onStop() {
		super.onStop();
		unregisterReceiver(changeItem);
	}
	
	/*���������Ĳ˵�������*/
	class ContextMenuListener implements OnCreateContextMenuListener{
		
		public void onCreateContextMenu(ContextMenu menu, View view,
				ContextMenuInfo info) {
			if (tag.equals("list")){
				menu.setHeaderTitle("��ز���");
				menu.setHeaderIcon(R.drawable.music);
				menu.add(0, PLAY_ITEM, 0, "����");
				menu.add(0, DELETE_ITEM, 0, "ɾ��");
				menu.add(0, SONG_SHARE, 0, "����");
				menu.add(0, SET_AS, 0, "�����趨����");
				final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) info;
				num = menuInfo.position;
			}
		}
	}
	
	//ָ���������ò���
	private void setEffects() {
		
		String[] items = { "����Ϊ��������", "����Ϊ֪ͨ����", "����Ϊ��������" };
		 AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)  
		           .setIcon(R.drawable.music)  
		           .setTitle("�����趨����")
		           .setItems(items, onSetEffectsSelect).create();  
		 dialog.show();  
	}
	
	OnClickListener onSetEffectsSelect = new OnClickListener() {  
	    public void onClick(DialogInterface dialog, int which) {  
	          
	      switch(which)
	      {
	        /*����---����*/
	        case 0:
	        	try{	
	        		setMyRingtone(num);
	        	}
	        	catch(Exception e)
	        	{
	        		Log.v(TAG, "This is my ringset!");
	        	}
	    		break;	    	
	    		/*����---��ʾ��*/
	        case 1:
	        	
	        	setMyNotification(num);
				break;
             /*����---������*/
	        case 2:
	        	setMyAlarm(num);
	    		break;
	      }
	    
	   }
    };
	//����--�����ľ��巽��
    public void setMyRingtone(int position)  
    {  

      File sdfile = new File(_path[position]);
      ContentValues values = new ContentValues();  
      values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());  
      values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
      values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");   
      values.put(MediaStore.Audio.Media.IS_RINGTONE, true);  
      values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);  
      values.put(MediaStore.Audio.Media.IS_ALARM, false);  
      values.put(MediaStore.Audio.Media.IS_MUSIC, false);  
      
      Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
      //Uri uri = MediaStore.Audio.Media.getContentUriForPath(_path[position]);
      Uri newUri = this.getContentResolver().insert(uri, values);
      RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, newUri); 
      Toast.makeText( getApplicationContext (),"�������������ɹ���", Toast.LENGTH_SHORT ).show(); 
      System.out.println("setMyRingtone()-----����");
    }  

  //����--��ʾ���ľ���ʵ�ַ���
    public void setMyNotification(int position)  
    { 
   
      File sdfile = new File(_path[position]);   
      ContentValues values = new ContentValues();  
      values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());  
      values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
      values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");   
      values.put(MediaStore.Audio.Media.IS_RINGTONE, false);  
      values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);  
      values.put(MediaStore.Audio.Media.IS_ALARM, false);  
      values.put(MediaStore.Audio.Media.IS_MUSIC, false);  
      
      Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
      Uri newUri = this.getContentResolver().insert(uri, values);
      
      RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION, newUri); 
      Toast.makeText( getApplicationContext (),"����֪ͨ�����ɹ���", Toast.LENGTH_SHORT ).show();
      System.out.println("setMyNOTIFICATION-----��ʾ��");
    } 
  //����--�������ľ���ʵ�ַ���
  public void setMyAlarm(int position)  
    {  
      File sdfile = new File(_path[position]);   
      ContentValues values = new ContentValues();  
      values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());  
      values.put(MediaStore.MediaColumns.TITLE, sdfile.getName());
      values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");   
      values.put(MediaStore.Audio.Media.IS_RINGTONE, false);  
      values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);  
      values.put(MediaStore.Audio.Media.IS_ALARM, true);  
      values.put(MediaStore.Audio.Media.IS_MUSIC, false);  
      
      Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
      Uri newUri = this.getContentResolver().insert(uri, values);
      RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM, newUri); 
      Toast.makeText( getApplicationContext (),"�������������ɹ���", Toast.LENGTH_SHORT ).show();
      System.out.println("setMyNOTIFICATION------������");
    }  

	/*����ѡ�е�����*/
	private void ShareMusicFile(int position){

		 Intent intent=new Intent(Intent.ACTION_SEND);  
			//intent.setType("text/plain");  
			 intent.setType("audio/*");
			 File file = new File(_path[position]);
			 Uri u = Uri.fromFile(file);
			 intent.putExtra(Intent.EXTRA_STREAM, u);
			 System.out.println(_path[position]);
			 intent.putExtra(Intent.EXTRA_SUBJECT, "����");  
			 intent.putExtra(Intent.EXTRA_TEXT, "��������    (����Dreamer����С�飬��ӭʹ������Ӱ��������)");  
			 startActivity(Intent.createChooser(intent, getTitle()));  
	   
	}
	/*�����Ĳ˵���ĳһ����ʱ�ص��÷���*/
	
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case PLAY_ITEM:									//��ʼ����
			playMusic(num);
			break;

		case DELETE_ITEM:								//ɾ��һ�׸���
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("��ȷ��Ҫ�����ֿ���ɾ�����׸�����?")
			.setPositiveButton("��", new DialogInterface.OnClickListener() {
				
				
				public void onClick(DialogInterface dialog, int which) {
					deleteMusic(num);					//���б���ɾ������
					deleteMusicFile(num);				//��sdcard��ɾ������
					setListData();						//���»���б���ҩ��ʾ������
					adapter.notifyDataSetChanged();		//�����б�UI
				}
			})
			.setNegativeButton("��", null);
			AlertDialog ad = builder.create();
			ad.show();
			break;
			
		case SONG_SHARE://����ѡ�еĸ���
			ShareMusicFile(num);
			break;
		case SET_AS: //��ѡ�еĸ�������Ϊ....
			setEffects();
			break;
		}
		return true;
	}

}
