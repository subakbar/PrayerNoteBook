/*****
 * 
 * 		SetPasswordActivity�� ��й�ȣ�� �����ϴ� ��Ƽ��Ƽ�̴�.
 * 		�� 3�ܰ踦 ���� ��й�ȣ�� �����Ѵ�.
 * 		1. ���� ��й�ȣ�� �Է� �޴´�.
 * 		2. ������ ��й�ȣ�� �Է� �޴´�.
 * 		3. ������ ��й�ȣ�� Ȯ���ϱ����� ���Է� �޴´�.
 * 
 * 		�̷��� �Էµ� ����й�ȣ�� prereference�� �����Ѵ�.
 * 
 *****/

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
import android.widget.Toast;

public class SetPasswordActivity extends Activity {
	int m_currentStep;
	String m_password;
	String m_newPassword;
	String m_inputPassword;
	boolean m_isSetReInput;
	Vibrator m_vibrator;
	
	TextView textView;
	
	ImageView m_password_box_1;
	ImageView m_password_box_2;
	ImageView m_password_box_3;
	ImageView m_password_box_4;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_password);
		
		SharedPreferences pref = getSharedPreferences("Setting", 0);
		
		// ������� �ʱ�ȭ
		m_password = pref.getString("Password", "none");
		m_currentStep = 1;
		m_inputPassword = "";
		m_isSetReInput = false;
		m_vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		textView = (TextView)findViewById(R.id.password_activity_text);
		
		m_password_box_1 = (ImageView)findViewById(R.id.password_activity_password_box_1);
		m_password_box_2 = (ImageView)findViewById(R.id.password_activity_password_box_2);
		m_password_box_3 = (ImageView)findViewById(R.id.password_activity_password_box_3);
		m_password_box_4 = (ImageView)findViewById(R.id.password_activity_password_box_4);
		
		// ���� �н����尡 none�� ���, 2�ܰ���� �����Ͽ� ����й�ȣ�� �Է� �޴´�.
		if (m_password.equals("none")) {
			m_currentStep = 2;
			textView.setText(R.string.set_password_key_input_text_step_2);
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
			
			// 1�ܰ� : ��й�ȣ�� �Է� �޴´�.
			if (m_currentStep == 1) {
				// ��й�ȣ�� ������ 2�ܰ�� �Ѿ��.
				if (m_inputPassword.equals(m_password)) {
					m_currentStep = 2;
					m_isSetReInput = false;
					textView.setText(R.string.set_password_key_input_text_step_2);
				}
				else {
					// 200ms�� �����߻�
					m_vibrator.vibrate(200);
					
					// ó�� ��й�ȣ�� Ʋ������, �ȳ����� �����Ѵ�.
					if (m_isSetReInput == false) {
						textView.setText(R.string.set_password_key_re_input_text_step_1);
						
						m_isSetReInput = true;
					}
				}
				// �н����� �ڽ� �ʱ�ȭ
				m_password_box_1.setImageResource(R.drawable.password_box_normal);
				m_password_box_2.setImageResource(R.drawable.password_box_normal);
				m_password_box_3.setImageResource(R.drawable.password_box_normal);
				m_password_box_4.setImageResource(R.drawable.password_box_normal);
				
				// �Է� ���ڿ� �ʱ�ȭ
				m_inputPassword = "";
			}
			// 2�ܰ� : ���ο� ��й�ȣ�� �Է� �޴´�.
			else if (m_currentStep == 2) {
				m_currentStep = 3;
				m_newPassword = m_inputPassword;
				textView.setText(R.string.set_password_key_input_text_step_3);

				// �н����� �ڽ� �ʱ�ȭ
				m_password_box_1.setImageResource(R.drawable.password_box_normal);
				m_password_box_2.setImageResource(R.drawable.password_box_normal);
				m_password_box_3.setImageResource(R.drawable.password_box_normal);
				m_password_box_4.setImageResource(R.drawable.password_box_normal);
				
				// �Է� ���ڿ� �ʱ�ȭ
				m_inputPassword = "";
			}
			// 3�ܰ� : ���ο� ��й�ȣ�� ���Է� �޴´�.
			else if (m_currentStep == 3) {
				// ��й�ȣ�� ������ 2�ܰ�� �Ѿ��.
				if (m_inputPassword.equals(m_newPassword)) {
					// ���ο� ��й�ȣ�� �����Ѵ�.
					SharedPreferences pref = getSharedPreferences("Setting", 0);
			    	SharedPreferences.Editor edit = pref.edit();
			    	edit.putString("Password", m_newPassword);
		    		edit.commit();
					
					// �佺Ʈ�� ��й�ȣ������ �˸���.
		    		Toast.makeText(this, "'��й�ȣ�� ����Ǿ����ϴ�.", Toast.LENGTH_SHORT).show();
					
					// ����ȭ������ ���ư���.
					finish();
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
				else {
					// 200ms�� �����߻�
					m_vibrator.vibrate(200);
					
					// ó�� ��й�ȣ�� Ʋ������, �ȳ����� �����Ѵ�.
					if (m_isSetReInput == false) {
						textView.setText(R.string.set_password_key_re_input_text_step_3);
						
						m_isSetReInput = true;
					}
				}
				// �н����� �ڽ� �ʱ�ȭ
				m_password_box_1.setImageResource(R.drawable.password_box_normal);
				m_password_box_2.setImageResource(R.drawable.password_box_normal);
				m_password_box_3.setImageResource(R.drawable.password_box_normal);
				m_password_box_4.setImageResource(R.drawable.password_box_normal);
				
				// �Է� ���ڿ� �ʱ�ȭ
				m_inputPassword = "";
			}
		}
	}
	
	
	// ���̾��ư�� ���ư
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