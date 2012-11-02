package leeheechul.make.prayernotebook;

import android.app.Application;
import android.content.SharedPreferences;

public class PrayerApplication extends Application {
	boolean m_isSetPassword;
	SharedPreferences m_pref;
	String m_preActivity;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		m_preActivity = "AllGroupListActivity";
		m_pref = getSharedPreferences("Setting", 0);
		
		resetIsSetPassword();
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	public boolean getIsSetPassword() {
		resetIsSetPassword();
		
		return m_isSetPassword;
	}
	
	public void resetIsSetPassword() {
        m_isSetPassword = m_pref.getBoolean("isSetPassword", false);
	}
	
	public String getPreActivity() {
		return m_preActivity;
	}
	
	public void setPreActivity(String preActivity) {
		m_preActivity = preActivity;
	}
}
