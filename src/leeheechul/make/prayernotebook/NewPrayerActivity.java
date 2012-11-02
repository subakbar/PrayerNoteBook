package leeheechul.make.prayernotebook;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class NewPrayerActivity extends Activity implements OnDateSetListener {
	
	int m_select_group;
	int m_year, m_month, m_day;
	long m_startTime;							// ���Ḧ ���� ���۽ð��� �����ϴ� ����
	long m_endTime;								// ���Ḧ ���� ����ð��� �����ϴ� ����
	boolean m_isShowGroup;
	boolean m_isPressedBackButton;
	ArrayAdapter<String> m_groupList;
	
	EditText m_target;
	Button m_dateButton;
	Button m_groupButton;
	EditText m_title;
	EditText m_answer;
	EditText m_word;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_prayer);
        
        // ���� �ʱ�ȭ
        m_target = (EditText)findViewById(R.id.new_prayer_target);
        m_dateButton = (Button)findViewById(R.id.new_prayer_date);
        m_groupButton = (Button)findViewById(R.id.new_prayer_group);
        m_title = (EditText)findViewById(R.id.new_prayer_title);
        m_answer = (EditText)findViewById(R.id.new_prayer_answer);
        m_word = (EditText)findViewById(R.id.new_prayer_word);
        registerForContextMenu(m_groupButton);
        
        // ���� ��¥ ���ϱ�
        Calendar cal = new GregorianCalendar();
        m_year = cal.get(Calendar.YEAR);
        m_month = cal.get(Calendar.MONTH) + 1;
        m_day = cal.get(Calendar.DAY_OF_MONTH);
        
        // ������ �ʱ�ȭ
        m_isShowGroup = false;
        DecimalFormat df  = new DecimalFormat("00");
        String month = df.format(m_month);
        String day = df.format(m_day);
        m_dateButton.setText(m_year + "/" + month + "/" + day);
        m_groupList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        m_startTime = System.currentTimeMillis();
        
        // ����Ʈ���� ������������
        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        
        // GroupListActivity�� 'All'�̳� 'Ungroup'�ϰ��, 'Ungroup'���� ���Ͻ�Ų��.
        if ((groupName.toLowerCase().equals("all")) || (groupName.toLowerCase().equals("ungroup")))
        	m_groupButton.setText("Ungroup");
        else
        	m_groupButton.setText(groupName);
	}
	
	
	@Override
    public void onStart() {
    	super.onStart();
    	
    	// ��й�ȣ������ ture�̰�, ��׶��� ���ͽ� ��й�ȣ��Ƽ��Ƽ�� �����Ѵ�.
    	PrayerApplication app = (PrayerApplication)getApplication();
    	if (app.getIsSetPassword() == true) {
			if (app.getPreActivity().equals("NewPrayerActivity")) {
				Intent intent = new Intent(this, PasswordActivity.class);
	    		
	    		startActivity(intent);
			}
			
			// ���� ��Ƽ��Ƽ�� �����Ѵ�.
			app.setPreActivity("NewPrayerActivity");
    	}
    }
	
	
	/*****
	 * 
	 * 					Button Method
	 * 
	 *****/
	// cancelButton
	public void cancelButton(View view) {
		finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}
	
	// okButton
	public void okButton(View view) {
		// DB���׷���� �����Ѵ�.
		PrayerSQLite prayerSqlite = new PrayerSQLite(NewPrayerActivity.this);
    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
    	
    	String groupName = m_groupButton.getText().toString();
    	
		Cursor cursor;
        cursor = db.rawQuery("SELECT _id FROM groupInfo where name = '" + groupName + "';", null);
        
        // 1) ungroup�� ���
        if (groupName.toLowerCase().equals("ungroup")) {
			cursor.close();
			
			// prayer �߰��ϱ�
			db.execSQL("INSERT INTO prayer (target, date, title, answer, word) VALUES('" +
			                 m_target.getText().toString() + "', '" +
			                 m_dateButton.getText().toString() + "', '" +
			                 m_title.getText().toString() + "', '" +
			                 m_answer.getText().toString() + "', '" +
			                 m_word.getText().toString() + "');");
        	
        }
        // 2) ���׷��� ������ ���׷��� �߰��ϰ�, prayer�� �߰��Ѵ�.
        else if (cursor.getCount() == 0) {
			// ���׷� �߰��ϱ�
			db.execSQL("INSERT INTO groupInfo (name) VALUES ('" + groupName + "');");
			
			// ���׷� ID���ϱ�
			cursor.close();
			cursor = db.rawQuery("SELECT _id FROM groupInfo where name = '" + groupName + "';", null);
			cursor.moveToNext();
			int groupId = cursor.getInt(0);
			cursor.close();
			
			// prayer�� �߰��ϱ�
			db.execSQL("INSERT INTO prayer (target, date, group_id, title, answer, word) VALUES('" +
			                 m_target.getText().toString() + "', '" +
			                 m_dateButton.getText().toString() + "', " +
			                 groupId + ", '" +
			                 m_title.getText().toString() + "', '" +
			                 m_answer.getText().toString() + "', '" +
			                 m_word.getText().toString() + "');");
		}
		// 3) ���׷��� ������ prayer�� �߰��Ѵ�.
		else {
			cursor.moveToNext();
			int groupId = cursor.getInt(0);
			cursor.close();
			
			// prayer �߰��ϱ�
			db.execSQL("INSERT INTO prayer (target, date, group_id, title, answer, word) VALUES('" +
			                 m_target.getText().toString() + "', '" +
			                 m_dateButton.getText().toString() + "', " +
			                 groupId + ", '" +
			                 m_title.getText().toString() + "', '" +
			                 m_answer.getText().toString() + "', '" +
			                 m_word.getText().toString() + "');");
		}
		db.close();
		
		finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}
	
	// dateButton
	public void setDate(View view) {
		new DatePickerDialog(this, this, m_year, m_month - 1, m_day).show();
	}
	
	public void onDateSet (DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		m_year = year;
		m_month = monthOfYear + 1;
		m_day = dayOfMonth;
		
		DecimalFormat df  = new DecimalFormat("00");
        String month = df.format(m_month);
        String day = df.format(m_day);
		
		m_dateButton.setText(m_year + "/" + month + "/" + day);
	}
	
	// groupButton
	public void setGroup(View view) {
		m_isShowGroup = true;
		
		openContextMenu(view);
	}
	
	/*****
	 * 
	 * 					ContextMenu
	 * 
	 *****/
    @Override
    public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);   
    	
    	if (m_isShowGroup == true) {
    		// Ű���� ������
    		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
    		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

	    	// DB���� �׷�� ��������
			PrayerSQLite prayerSqlite = new PrayerSQLite(this);
	    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
	    	Cursor cursor;
	    	
	    	cursor = db.rawQuery("SELECT name FROM groupInfo;", null);
	    	
	    	m_groupList.clear();
	    	m_groupList.add("Ungroup");
	    	m_groupList.add("New Group");
	    	while(cursor.moveToNext())
	    		m_groupList.add(cursor.getString(0));
	    	cursor.close();
	
		    menu.setHeaderTitle(R.string.prayer_group);
		    
		    for (int i = 0; i < m_groupList.getCount(); i++)
		    	menu.add(0, i, 0, m_groupList.getItem(i).toString());
		    
		    m_isShowGroup = false;
    	}
    }
    
    
    public boolean onContextItemSelected (MenuItem item) {
    	int position = item.getItemId();
    	
    	if (position == 1) {
    		// ��ȭ���ڿ� ���� ���̾ƿ��� �����Ѵ�.
        	final LinearLayout linear = (LinearLayout) View.inflate(this, R.layout.alert_dialog_add_group, null);
    		
        	// ��ȭ���� ���� �� ����ϱ�
        	AlertDialog alertDialog = new AlertDialog.Builder(this)
        	.setTitle(R.string.dialog_add_group_title)
        	.setView(linear)
        	.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				EditText editText = (EditText)linear.findViewById(R.id.add_group_name_edit_text);	
    				String newGroupName = editText.getText().toString();
    				
    				// DB�� �˻��Ͽ� all, ungroup, ������ �׷���� ������ �佺Ʈ�� ����.
    				PrayerSQLite prayerSqlite = new PrayerSQLite(NewPrayerActivity.this);
    		    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
    				Cursor cursor;
    		        cursor = db.rawQuery("SELECT name FROM groupInfo where name = '" + newGroupName + "';", null);
    		        
    		        // DB�� ���׷��� ����, all, ungroup�� �ƴ� ��� ����
    				if (cursor.getCount() == 0 && !(newGroupName.toLowerCase().equals("all")) && !(newGroupName.toLowerCase().equals("ungroup"))) {
    					m_groupButton.setText(newGroupName);
    				}
    				// DB�� ���׷��� ������ �佺Ʈ�� ���и� �˸���.
    				else
    					Toast.makeText(NewPrayerActivity.this, R.string.dialog_add_fail, Toast.LENGTH_SHORT).show();
    				
    				cursor.close();
    				db.close();
    			}
    		})
    		.setNegativeButton(R.string.button_cancel, null)
    		.show();
        	
        	// EditText Ű���� ����ϱ�
        	alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    	}
    	else 
    		m_groupButton.setText(m_groupList.getItem(position));
    	
    	return false;
    }
    
    
    /*****
     * 
     * 					Listener Method
     * 
     *****/
    // �ϵ���� ���ư
    @Override
    public void onBackPressed() {
		m_endTime = System.currentTimeMillis();
		
		if (m_endTime - m_startTime > 2000)
			m_isPressedBackButton = false;
		
		if (m_isPressedBackButton == false) {
			m_isPressedBackButton = true;
			
			m_startTime = System.currentTimeMillis();
			
			Toast.makeText(this, "'�ڷ�'��ư�� �ѹ� �� �����ø� ��� �˴ϴ�.", Toast.LENGTH_SHORT).show();
		}
		else {
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
    }
}