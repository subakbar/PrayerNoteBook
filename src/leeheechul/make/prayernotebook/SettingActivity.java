package leeheechul.make.prayernotebook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SettingActivity extends Activity {
	Button m_usePasswordButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		m_usePasswordButton = (Button)findViewById(R.id.setting_activity_use_password_button);
	}
	
	
	@Override
    public void onStart() {
    	super.onStart();
    	
    	// ��й�ȣ������ ture�̰�, ��׶��� ���ͽ� ��й�ȣ��Ƽ��Ƽ�� �����Ѵ�.
    	PrayerApplication app = (PrayerApplication)getApplication();
    	if (app.getIsSetPassword() == true) {
			if (app.getPreActivity().equals("SettingActivity")) {
				Intent intent = new Intent(this, PasswordActivity.class);
	    		
	    		startActivity(intent);
			}
    	}
    	
    	// ���� ��Ƽ��Ƽ�� �����Ѵ�.
    	// SettingActivity�� isSetPassword���� ����� �� ������
    	// ��й�ȣ������ false�϶����� setPreActivity�� �����ؾ�
    	// isSetPassword���� ����Ǹ� �ٷ� ��й�ȣ����� Ȱ��ȭ�ȴ�.
		app.setPreActivity("SettingActivity");
    }
	
	@Override
	public void onResume() {
		super.onResume();
		
		// ��й�ȣ��� ���� ��������
		SharedPreferences pref = getSharedPreferences("Setting", 0);
		SharedPreferences.Editor edit = pref.edit();
		String password = pref.getString("Password", "none");
        boolean isSetPassword = pref.getBoolean("isSetPassword", false);
        
        // ���� �н����尡 'none'�̰� isSetPassword�� 'true'�ΰ�츦 �����Ѵ�.
        if (password.equals("none") && isSetPassword == true) {
        	edit.putBoolean("isSetPassword", false);
        	edit.commit();
        	isSetPassword = false;
        }
        
        // ���ڿ� ���� ��������
        String usePasswordStr = getString(R.string.setting_use_password_button);
        String onStr = getString(R.string.setting_on);
        String offStr = getString(R.string.setting_off);
        
        // ��й�ȣ ����, On�� ����Ѵ�.
        if (isSetPassword == true)
        	m_usePasswordButton.setText(usePasswordStr + " " + onStr);
        // ��й�ȣ �̻���, Off�� ����Ѵ�.
        else
        	m_usePasswordButton.setText(usePasswordStr + " " + offStr);
	}
	
	
	/*****
	 * 
	 * 					Button Method
	 *	
	 *****/
	public void cafeButton(View view) {
		// Web�� �������ؼ� intent����, web����Ʈ���� �����ͷ� �Ѱ��ش�.
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://cafe.naver.com/subakbar"));
		
		startActivity(intent);
	}
	
	
	// ��й�ȣ��� ��ư
	public void usePasswordButton(View view) {
		// ��й�ȣ��� ���� ��������
		SharedPreferences pref = getSharedPreferences("Setting", 0);
    	SharedPreferences.Editor edit = pref.edit();
    	String password = pref.getString("Password", "none");
    	boolean isSetPassword = pref.getBoolean("isSetPassword", false);
    	
    	// ���ڿ� ���� ��������
    	String usePasswordStr = getString(R.string.setting_use_password_button);
        String onStr = getString(R.string.setting_on);
        String offStr = getString(R.string.setting_off);
        
        // ������ ��й�ȣ�� ������̿��ٸ�, off�� �����Ѵ�.
        if (isSetPassword == true) {
        	m_usePasswordButton.setText(usePasswordStr + " " + offStr);
        	edit.putBoolean("isSetPassword", false);
        	edit.commit();
        }
        // ������ ��й�ȣ�� �̻�����̿��ٸ�, on���� �����Ѵ�.
        else {
        	m_usePasswordButton.setText(usePasswordStr + " " + onStr);
        	edit.putBoolean("isSetPassword", true);
        	edit.commit();
        }
        
        // ��й�ȣ�� 'none'�ΰ��, ��й�ȣ���� ��Ƽ��Ƽ�� �̵��Ѵ�.
        if (password.equals("none")) {
    		Intent intent = new Intent(this, SetPasswordActivity.class);
    		startActivity(intent);
        }
	}
	
	
	// ��й�ȣ���� ��ư
	public void setPasswordButton(View view) {
		// ��й�ȣ�����Ƽ��Ƽ�� �����Ѵ�.
		Intent intent = new Intent(this, SetPasswordActivity.class);
		startActivity(intent);
	}
	
	
	// ��� ��ư
	public void backupButton(View view) {
		// SDcard�� ����Ǿ� �ִ��� Ȯ��
		String state = Environment.getExternalStorageState();
		if (state.equals("mounted")) {
			try {
				// sdcard ��ġ ������ �����´�.
				File sdDir = Environment.getExternalStorageDirectory();
	
				if (sdDir.canWrite()) {
					// ����� �ʿ��� ������ �����Ѵ�.
					String dbFile = "data/leeheechul.make.prayernotebook/databases/PrayerDB.db";
					String backupDbFile = "PrayerDB.db";
	
					// �޴��� ������ ��ġ������ �����´�.
					File dataDir = Environment.getDataDirectory();
					
					// ������ ���纻 ��ġ������ �����Ѵ�.
					File originalDB = new File(dataDir, dbFile);         
					File backupDB = new File(sdDir, backupDbFile);   
					
					// �����͸� �����Ѵ�.
					if (originalDB.exists()) {
						FileChannel src = new FileInputStream(originalDB).getChannel();          
						FileChannel dst = new FileOutputStream(backupDB).getChannel();          
						dst.transferFrom(src, 0, src.size());          
						src.close();          
						dst.close();
					}
				}
				Toast.makeText(SettingActivity.this, "��� ���� :)", Toast.LENGTH_SHORT).show();
			}
			catch (Exception e) {
				Toast.makeText(SettingActivity.this, "��� ���� :(", Toast.LENGTH_SHORT).show();
			}
		}
		else {
			Toast.makeText(SettingActivity.this, "SDCARD �ν� ���� :(", Toast.LENGTH_SHORT).show();
		} 
	}
	
	
	// ���� ��ư
	public void restoreButton(View view) {
		// ��ȭ���� ���� �� ����ϱ�
    	new AlertDialog.Builder(this)
    	.setTitle(R.string.setting_restore_button)
    	.setMessage(R.string.setting_restore_message)
    	.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// SDcard�� ����Ǿ� �ִ��� Ȯ��
				String state = Environment.getExternalStorageState();
				if (state.equals("mounted")) {
					try {
						// ����� �ʿ��� ������ �����Ѵ�.
						String restoreDbFile = "data/leeheechul.make.prayernotebook/databases/PrayerDB.db";
						String backupDbFile = "PrayerDB.db";
		
						// �޴��� ������ ��ġ������ �����´�.
						File dataDir = Environment.getDataDirectory();
						
						// sdcard ��ġ ������ �����´�.
						File sdDir = Environment.getExternalStorageDirectory();
						
						// ������ ���纻 ��ġ������ �����Ѵ�.
						File backupDB = new File(sdDir, backupDbFile);         
						File restoreDB = new File(dataDir, restoreDbFile);   
						
						// �����͸� �����Ѵ�.
						if (backupDB.exists()) {
							FileChannel src = new FileInputStream(backupDB).getChannel();          
							FileChannel dst = new FileOutputStream(restoreDB).getChannel();          
							dst.transferFrom(src, 0, src.size());          
							src.close();          
							dst.close();
						}
						Toast.makeText(SettingActivity.this, "���� ���� :)", Toast.LENGTH_SHORT).show();
					}
					catch (Exception e) {
						Toast.makeText(SettingActivity.this, "���� ���� :(", Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Toast.makeText(SettingActivity.this, "SDCARD �ν� ���� :(", Toast.LENGTH_SHORT).show();
				} 
			}
		})
		.setNegativeButton(R.string.button_cancel, null)
		.show();
	}
	
	
	// �ʱ�ȭ ��ư
	public void resetButton(View view) {
		// ��ȭ���� ���� �� ����ϱ�
    	new AlertDialog.Builder(this)
    	.setTitle(R.string.setting_reset_botton)
    	.setMessage(R.string.setting_reset_message)
    	.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				File dataDir = Environment.getDataDirectory();
				String dbFile = "data/leeheechul.make.prayernotebook/databases/PrayerDB.db";
				
		        File file = new File(dataDir, dbFile);
		        file.delete();
		        
		        Toast.makeText(SettingActivity.this, "������ �ʱ�ȭ �Ϸ� :P", Toast.LENGTH_SHORT).show();
			}
		})
		.setNegativeButton(R.string.button_cancel, null)
		.show();
	}
	
	
	// �׼ǹ� ���ư
	public void backButton(View view) {
		finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}
	
	
	// �ϵ���� ���ư
    @Override
    public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
}
