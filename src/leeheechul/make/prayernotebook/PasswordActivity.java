package leeheechul.make.prayernotebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PasswordActivity extends Activity {
	String m_password;
	String m_inputPassword;
	boolean m_isSetReInput;
	Vibrator m_vibrator;
	
	ImageView m_password_box_1;
	ImageView m_password_box_2;
	ImageView m_password_box_3;
	ImageView m_password_box_4;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password);
		
		SharedPreferences pref = getSharedPreferences("Setting", 0);
		
		// ������� �ʱ�ȭ
		m_password = pref.getString("Password", "none");
		m_inputPassword = "";
		m_isSetReInput = false;
		m_vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		m_password_box_1 = (ImageView)findViewById(R.id.password_activity_password_box_1);
		m_password_box_2 = (ImageView)findViewById(R.id.password_activity_password_box_2);
		m_password_box_3 = (ImageView)findViewById(R.id.password_activity_password_box_3);
		m_password_box_4 = (ImageView)findViewById(R.id.password_activity_password_box_4);
		
		// ���� �н����尡 none�� ���, ��Ƽ��Ƽ�� ���� �����Ѵ�.
		if (m_password.equals("none")) {
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		PrayerApplication app = (PrayerApplication)getApplication();
		app.setPreActivity("PasswordActivity");
	}
	
	public void keyButton(View view) {
		switch (view.getId()) {
		// 0 ~ 9 ���ڸ� �߰��Ѵ�.
		case R.id.password_activity_key_1 :
			m_inputPassword += "1";
			break;
		case R.id.password_activity_key_2 :
			m_inputPassword += "2";
			break;
		case R.id.password_activity_key_3 :
			m_inputPassword += "3";
			break;
		case R.id.password_activity_key_4 :
			m_inputPassword += "4";
			break;
		case R.id.password_activity_key_5 :
			m_inputPassword += "5";
			break;
		case R.id.password_activity_key_6 :
			m_inputPassword += "6";
			break;
		case R.id.password_activity_key_7 :
			m_inputPassword += "7";
			break;
		case R.id.password_activity_key_8 :
			m_inputPassword += "8";
			break;
		case R.id.password_activity_key_9 :
			m_inputPassword += "9";
			break;
		case R.id.password_activity_key_0 :
			m_inputPassword += "0";
			break;
		
		// Ȩ��ưŰ : Ȩ���� �̵��Ѵ�.
		case R.id.password_activity_key_exit :
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.HOME");
			intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
			  | Intent.FLAG_ACTIVITY_FORWARD_RESULT
			  | Intent.FLAG_ACTIVITY_NEW_TASK
			  | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
			  | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			startActivity(intent);
			break;
			
		// ���ưŰ : �Է��� ���ڸ� �ϳ� �����. 
		case R.id.password_activity_key_back :
			if (m_inputPassword.length() > 0)
				m_inputPassword = m_inputPassword.substring(0, m_inputPassword.length()-1);
			break;
		}
		
		// ������ �н����� �ڽ��� �����.
		if (view.getId() == R.id.password_activity_key_back) {
			switch (m_inputPassword.length()) {
			case 0 :
				m_password_box_1.setImageResource(R.drawable.password_box_normal);
				break;
			case 1 :
				m_password_box_2.setImageResource(R.drawable.password_box_normal);
				break;
			case 2 :
				m_password_box_3.setImageResource(R.drawable.password_box_normal);
				break;
			}
		}
		// ������ �н����� �ڽ��� üũ�Ѵ�.
		else {
			switch (m_inputPassword.length()) {
			case 1 :
				m_password_box_1.setImageResource(R.drawable.password_box_check);
				break;
			case 2 :
				m_password_box_2.setImageResource(R.drawable.password_box_check);
				break;
			case 3 :
				m_password_box_3.setImageResource(R.drawable.password_box_check);
				break;
			case 4 :
				m_password_box_4.setImageResource(R.drawable.password_box_check);
				break;
			}
		}
		
		// �Էµ� ���ڿ��� 4�ڸ���, ��й�ȣ�� Ȯ���Ѵ�.
		if (m_inputPassword.length() >= 4) {
			// 4�ڸ��� �����Ѵ�.
			m_inputPassword = m_inputPassword.substring(0, 4);
			
			if (m_inputPassword.equals(m_password)) {
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
			else {
				// �н����� �ڽ� �ʱ�ȭ
				m_password_box_1.setImageResource(R.drawable.password_box_normal);
				m_password_box_2.setImageResource(R.drawable.password_box_normal);
				m_password_box_3.setImageResource(R.drawable.password_box_normal);
				m_password_box_4.setImageResource(R.drawable.password_box_normal);
				
				// �Է� ���ڿ� �ʱ�ȭ
				m_inputPassword = "";
				
				// 200ms�� �����߻�
				m_vibrator.vibrate(200);
				
				// ó�� ��й�ȣ�� Ʋ������, �ȳ����� �����Ѵ�.
				if (m_isSetReInput == false) {
					TextView textView = (TextView)findViewById(R.id.password_activity_text);
					textView.setText(R.string.password_key_re_input_text);
					
					m_isSetReInput = true;
				}
			}
		}
	}
	
	// ���ư�� Ȩ���� �̵��ϰ� �Ѵ�.
	@Override
    public void onBackPressed() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
		  | Intent.FLAG_ACTIVITY_FORWARD_RESULT
		  | Intent.FLAG_ACTIVITY_NEW_TASK
		  | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
		  | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		startActivity(intent);
    }
}