package music.dreamer.useful;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import Xinyue.all.activity.MainActivity;
import Xinyue.all.activity.MainActivity.MyThread;
import Xinyue.all.activity.R;

/**˯�߶�ʱ��**/
/*This work comes from DreamerؼTeam. The main programmer is LinShaoHan.
 * QQ:752280466   Welcome to join with us.
 */

public class SetTimeDialog extends DialogBuilder {

	public final static int RUNNING_BG = 0;
	public final static int EXIT_APP = 1;
	public final static int DIALOG_CANCEL = 2;
    public static EditText edittext1; 
  
	public static Builder getCreatePlaylistDialog(final Context context) {

		AlertDialog.Builder builder = getInstance(context);
		edittext1 = new EditText(context);
		edittext1.setText(context
				.getString(R.string.set_time));
		edittext1.setSelectAllOnFocus(true);
		builder.setView(edittext1);
		builder.setPositiveButton(context.getString(R.string.Yes),
				new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
				/*���ı��������String���͵ķ�����ת��Ϊint���Ͳ���ֵ��time*/
				if(SetTimeDialog.edittext1.getText().toString()!=null)
				{
				MainActivity.time= Integer.valueOf(SetTimeDialog.edittext1.getText().toString()).intValue();
				dialog.cancel();
				}
				else {
				Toast.makeText(context, "�ܱ�Ǹ�����õ�ʱ�䲻��Ϊ�գ�", Toast.LENGTH_LONG).show();
				}
				new Thread(new MyThread()).start();
				if(MainActivity.time>=120){
					Toast.makeText(context, "�ܱ�Ǹ�����õ�ʱ�䲻�ܳ���120����", Toast.LENGTH_LONG)
							.show();
				}
				else {
					Toast.makeText(context, "����˯��ʱ��ɹ���"+edittext1.getText().toString()
						+"���Ӻ�ر����ֲ��ų���", Toast.LENGTH_LONG)
						.show();}
			}
		});			 
				
		builder.setNeutralButton(context.getString(R.string.cancel),
				null);
		builder.setIcon(ImageScale.getImage(context));
		builder.setTitle("˯�߶�ʱ");
		return builder;
		  
	}
			
}
