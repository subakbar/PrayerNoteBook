/*****
 * 
 * 		SetPasswordActivity는 비밀번호를 설정하는 액티비티이다.
 * 		총 3단계를 걸쳐 비밀번호를 설정한다.
 * 		1. 현재 비밀번호를 입력 받는다.
 * 		2. 변경할 비밀번호를 입력 받는다.
 * 		3. 변결할 비밀번호를 확인하기위해 재입력 받는다.
 * 
 * 		이렇게 입력된 새비밀번호는 prereference에 저장한다.
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
		
		// 멤버변수 초기화
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
		
		// 만일 패스워드가 none인 경우, 2단계부터 시작하여 새비밀번호를 입력 받는다.
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
		// 0 ~ 9 숫자를 추가한다.
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
		
		// 홈버튼키 : 홈으로 이동한다.
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
			
		// 백버튼키 : 입력한 문자를 하나 지운다. 
		case R.id.password_activity_key_back :
			if (m_inputPassword.length() > 0)
				m_inputPassword = m_inputPassword.substring(0, m_inputPassword.length()-1);
			break;
		}
		
		// 마지막 패스워드 박스를 지운다.
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
		// 마지막 패스워드 박스를 체크한다.
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
		
		// 입력된 문자열이 4자리면, 비밀번호를 확인한다.
		if (m_inputPassword.length() >= 4) {
			// 4자리로 수정한다.
			m_inputPassword = m_inputPassword.substring(0, 4);
			
			// 1단계 : 비밀번호를 입력 받는다.
			if (m_currentStep == 1) {
				// 비밀번호가 맞으면 2단계로 넘어간다.
				if (m_inputPassword.equals(m_password)) {
					m_currentStep = 2;
					m_isSetReInput = false;
					textView.setText(R.string.set_password_key_input_text_step_2);
				}
				else {
					// 200ms간 진동발생
					m_vibrator.vibrate(200);
					
					// 처음 비밀번호를 틀렸으면, 안내문을 수정한다.
					if (m_isSetReInput == false) {
						textView.setText(R.string.set_password_key_re_input_text_step_1);
						
						m_isSetReInput = true;
					}
				}
				// 패스워드 박스 초기화
				m_password_box_1.setImageResource(R.drawable.password_box_normal);
				m_password_box_2.setImageResource(R.drawable.password_box_normal);
				m_password_box_3.setImageResource(R.drawable.password_box_normal);
				m_password_box_4.setImageResource(R.drawable.password_box_normal);
				
				// 입력 문자열 초기화
				m_inputPassword = "";
			}
			// 2단계 : 새로운 비밀번호를 입력 받는다.
			else if (m_currentStep == 2) {
				m_currentStep = 3;
				m_newPassword = m_inputPassword;
				textView.setText(R.string.set_password_key_input_text_step_3);

				// 패스워드 박스 초기화
				m_password_box_1.setImageResource(R.drawable.password_box_normal);
				m_password_box_2.setImageResource(R.drawable.password_box_normal);
				m_password_box_3.setImageResource(R.drawable.password_box_normal);
				m_password_box_4.setImageResource(R.drawable.password_box_normal);
				
				// 입력 문자열 초기화
				m_inputPassword = "";
			}
			// 3단계 : 새로운 비밀번호를 재입력 받는다.
			else if (m_currentStep == 3) {
				// 비밀번호가 맞으면 2단계로 넘어간다.
				if (m_inputPassword.equals(m_newPassword)) {
					// 새로운 비밀번호를 저장한다.
					SharedPreferences pref = getSharedPreferences("Setting", 0);
			    	SharedPreferences.Editor edit = pref.edit();
			    	edit.putString("Password", m_newPassword);
		    		edit.commit();
					
					// 토스트로 비밀번호변경을 알린다.
		    		Toast.makeText(this, "'비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
					
					// 이전화면으로 돌아간다.
					finish();
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
				else {
					// 200ms간 진동발생
					m_vibrator.vibrate(200);
					
					// 처음 비밀번호를 틀렸으면, 안내문을 수정한다.
					if (m_isSetReInput == false) {
						textView.setText(R.string.set_password_key_re_input_text_step_3);
						
						m_isSetReInput = true;
					}
				}
				// 패스워드 박스 초기화
				m_password_box_1.setImageResource(R.drawable.password_box_normal);
				m_password_box_2.setImageResource(R.drawable.password_box_normal);
				m_password_box_3.setImageResource(R.drawable.password_box_normal);
				m_password_box_4.setImageResource(R.drawable.password_box_normal);
				
				// 입력 문자열 초기화
				m_inputPassword = "";
			}
		}
	}
	
	
	// 다이얼버튼의 백버튼
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