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
    	
    	// 비밀번호설정이 ture이고, 백그라운드 복귀시 비밀번호액티비티를 실행한다.
    	PrayerApplication app = (PrayerApplication)getApplication();
    	if (app.getIsSetPassword() == true) {
			if (app.getPreActivity().equals("SettingActivity")) {
				Intent intent = new Intent(this, PasswordActivity.class);
	    		
	    		startActivity(intent);
			}
    	}
    	
    	// 현재 액티비티를 저장한다.
    	// SettingActivity는 isSetPassword값이 변경될 수 있으니
    	// 비밀번호설정이 false일때에도 setPreActivity를 설정해야
    	// isSetPassword값이 변경되면 바로 비밀번호기능이 활성화된다.
		app.setPreActivity("SettingActivity");
    }
	
	@Override
	public void onResume() {
		super.onResume();
		
		// 비밀번호사용 정보 가져오기
		SharedPreferences pref = getSharedPreferences("Setting", 0);
		SharedPreferences.Editor edit = pref.edit();
		String password = pref.getString("Password", "none");
        boolean isSetPassword = pref.getBoolean("isSetPassword", false);
        
        // 만일 패스워드가 'none'이고 isSetPassword가 'true'인경우를 방지한다.
        if (password.equals("none") && isSetPassword == true) {
        	edit.putBoolean("isSetPassword", false);
        	edit.commit();
        	isSetPassword = false;
        }
        
        // 문자열 정보 가져오기
        String usePasswordStr = getString(R.string.setting_use_password_button);
        String onStr = getString(R.string.setting_on);
        String offStr = getString(R.string.setting_off);
        
        // 비밀번호 사용시, On을 출력한다.
        if (isSetPassword == true)
        	m_usePasswordButton.setText(usePasswordStr + " " + onStr);
        // 비밀번호 미사용시, Off를 출력한다.
        else
        	m_usePasswordButton.setText(usePasswordStr + " " + offStr);
	}
	
	
	/*****
	 * 
	 * 					Button Method
	 *	
	 *****/
	public void cafeButton(View view) {
		// Web을 열기위해서 intent생성, web사이트를를 데이터로 넘겨준다.
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://cafe.naver.com/subakbar"));
		
		startActivity(intent);
	}
	
	
	// 비밀번호사용 버튼
	public void usePasswordButton(View view) {
		// 비밀번호사용 정보 가져오기
		SharedPreferences pref = getSharedPreferences("Setting", 0);
    	SharedPreferences.Editor edit = pref.edit();
    	String password = pref.getString("Password", "none");
    	boolean isSetPassword = pref.getBoolean("isSetPassword", false);
    	
    	// 문자열 정보 가져오기
    	String usePasswordStr = getString(R.string.setting_use_password_button);
        String onStr = getString(R.string.setting_on);
        String offStr = getString(R.string.setting_off);
        
        // 이전에 비밀번호를 사용중이였다면, off로 변경한다.
        if (isSetPassword == true) {
        	m_usePasswordButton.setText(usePasswordStr + " " + offStr);
        	edit.putBoolean("isSetPassword", false);
        	edit.commit();
        }
        // 이전에 비밀번호를 미사용중이였다면, on으로 변경한다.
        else {
        	m_usePasswordButton.setText(usePasswordStr + " " + onStr);
        	edit.putBoolean("isSetPassword", true);
        	edit.commit();
        }
        
        // 비밀번호가 'none'인경우, 비밀번호변경 액티비티로 이동한다.
        if (password.equals("none")) {
    		Intent intent = new Intent(this, SetPasswordActivity.class);
    		startActivity(intent);
        }
	}
	
	
	// 비밀번호변경 버튼
	public void setPasswordButton(View view) {
		// 비밀번호변경액티비티를 실행한다.
		Intent intent = new Intent(this, SetPasswordActivity.class);
		startActivity(intent);
	}
	
	
	// 백업 버튼
	public void backupButton(View view) {
		// SDcard가 연결되어 있는지 확인
		String state = Environment.getExternalStorageState();
		if (state.equals("mounted")) {
			try {
				// sdcard 위치 정보를 가져온다.
				File sdDir = Environment.getExternalStorageDirectory();
	
				if (sdDir.canWrite()) {
					// 백업에 필요한 정보를 정의한다.
					String dbFile = "data/leeheechul.make.prayernotebook/databases/PrayerDB.db";
					String backupDbFile = "PrayerDB.db";
	
					// 휴대폰 데이터 위치정보를 가져온다.
					File dataDir = Environment.getDataDirectory();
					
					// 원본과 복사본 위치정보를 설정한다.
					File originalDB = new File(dataDir, dbFile);         
					File backupDB = new File(sdDir, backupDbFile);   
					
					// 데이터를 복사한다.
					if (originalDB.exists()) {
						FileChannel src = new FileInputStream(originalDB).getChannel();          
						FileChannel dst = new FileOutputStream(backupDB).getChannel();          
						dst.transferFrom(src, 0, src.size());          
						src.close();          
						dst.close();
					}
				}
				Toast.makeText(SettingActivity.this, "백업 성공 :)", Toast.LENGTH_SHORT).show();
			}
			catch (Exception e) {
				Toast.makeText(SettingActivity.this, "백업 실패 :(", Toast.LENGTH_SHORT).show();
			}
		}
		else {
			Toast.makeText(SettingActivity.this, "SDCARD 인식 실패 :(", Toast.LENGTH_SHORT).show();
		} 
	}
	
	
	// 복원 버튼
	public void restoreButton(View view) {
		// 대화상자 생성 및 출력하기
    	new AlertDialog.Builder(this)
    	.setTitle(R.string.setting_restore_button)
    	.setMessage(R.string.setting_restore_message)
    	.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// SDcard가 연결되어 있는지 확인
				String state = Environment.getExternalStorageState();
				if (state.equals("mounted")) {
					try {
						// 백업에 필요한 정보를 정의한다.
						String restoreDbFile = "data/leeheechul.make.prayernotebook/databases/PrayerDB.db";
						String backupDbFile = "PrayerDB.db";
		
						// 휴대폰 데이터 위치정보를 가져온다.
						File dataDir = Environment.getDataDirectory();
						
						// sdcard 위치 정보를 가져온다.
						File sdDir = Environment.getExternalStorageDirectory();
						
						// 원본과 복사본 위치정보를 설정한다.
						File backupDB = new File(sdDir, backupDbFile);         
						File restoreDB = new File(dataDir, restoreDbFile);   
						
						// 데이터를 복사한다.
						if (backupDB.exists()) {
							FileChannel src = new FileInputStream(backupDB).getChannel();          
							FileChannel dst = new FileOutputStream(restoreDB).getChannel();          
							dst.transferFrom(src, 0, src.size());          
							src.close();          
							dst.close();
						}
						Toast.makeText(SettingActivity.this, "복원 성공 :)", Toast.LENGTH_SHORT).show();
					}
					catch (Exception e) {
						Toast.makeText(SettingActivity.this, "복원 실패 :(", Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Toast.makeText(SettingActivity.this, "SDCARD 인식 실패 :(", Toast.LENGTH_SHORT).show();
				} 
			}
		})
		.setNegativeButton(R.string.button_cancel, null)
		.show();
	}
	
	
	// 초기화 버튼
	public void resetButton(View view) {
		// 대화상자 생성 및 출력하기
    	new AlertDialog.Builder(this)
    	.setTitle(R.string.setting_reset_botton)
    	.setMessage(R.string.setting_reset_message)
    	.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				File dataDir = Environment.getDataDirectory();
				String dbFile = "data/leeheechul.make.prayernotebook/databases/PrayerDB.db";
				
		        File file = new File(dataDir, dbFile);
		        file.delete();
		        
		        Toast.makeText(SettingActivity.this, "데이터 초기화 완료 :P", Toast.LENGTH_SHORT).show();
			}
		})
		.setNegativeButton(R.string.button_cancel, null)
		.show();
	}
	
	
	// 액션바 백버튼
	public void backButton(View view) {
		finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}
	
	
	// 하드웨어 백버튼
    @Override
    public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
}
