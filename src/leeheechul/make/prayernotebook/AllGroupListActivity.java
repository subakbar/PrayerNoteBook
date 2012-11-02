package leeheechul.make.prayernotebook;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AllGroupListActivity extends Activity implements OnItemClickListener {
	
	int m_position;								// ListView�� ���õ� �������� ��ġ�� �����ϴ� ����
	long m_startTime;							// ���Ḧ ���� ���۽ð��� �����ϴ� ����
	long m_endTime;								// ���Ḧ ���� ����ð��� �����ϴ� ����
	boolean m_isPressedBackButton;		// ���ư�� ���������� Ȯ���ϴ� ����
	AllGroupListAdapter m_adapter;			// ListView�� ���� Adapter����
	
	// ���� ����
	Button m_cancelButton;
	Button m_okButton;
	Button m_addButton;
	TextView m_title;
	TextView m_selectTitle;
	ListView m_listView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_group_list);
        
        // ���� �ʱ�ȭ
        m_cancelButton = (Button)findViewById(R.id.all_group_list_activity_cancel_button);
        m_okButton = (Button)findViewById(R.id.all_group_list_activity_ok_botton);
        m_addButton = (Button)findViewById(R.id.all_group_list_activity_add_group_botton);
        m_title = (TextView)findViewById(R.id.all_group_list_activity_title);
        m_selectTitle = (TextView)findViewById(R.id.all_group_list_activity_title_select_mode);
        m_listView = (ListView)findViewById(R.id.all_group_list_activity_list);
        
        // �׼ǹ� '���', 'Ȯ��'��ư �����
        m_cancelButton.setVisibility(View.GONE);
        m_okButton.setVisibility(View.GONE);
        
        // �׼ǹ��� Ÿ��Ʋ�� ��ġ�� �����Ѵ�.
        m_selectTitle.setVisibility(View.GONE);
        
        // ����Ʈ ���ý� ������ �������� �����Ѵ�.
        m_listView.setSelector(R.drawable.my_selector);
        
        // ����Ʈ ������ �ʱ�ȭ
        m_listView.setOnItemClickListener(this);
        registerForContextMenu(m_listView);
        
        // ������� �ʱ�ȭ
        m_adapter = new AllGroupListAdapter(this);
        m_isPressedBackButton = false;
        m_startTime = System.currentTimeMillis();
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	
    	// ��й�ȣ������ ture�̰�, ��׶��� ���ͽ� ��й�ȣ��Ƽ��Ƽ�� �����Ѵ�.
    	PrayerApplication app = (PrayerApplication)getApplication();
    	if (app.getIsSetPassword() == true) {
			if (app.getPreActivity().equals("AllGroupListActivity")) {
				Intent intent = new Intent(this, PasswordActivity.class);
	    		
	    		startActivity(intent);
			}
			
			// ���� ��Ƽ��Ƽ�� �����Ѵ�.
			app.setPreActivity("AllGroupListActivity");
    	}
    }
 
    
    // ��Ƽ��Ƽ�� ���׶��ɶ����� setAdapter�� ���� �����͸� �ʱ�ȭ�Ѵ�.
    @Override
    public void onResume() {
    	super.onResume();

    	setAdapter();
    }
    
    
    // DB���� adapter������ �ʱ�ȭ�Ѵ�.
    public void setAdapter() {
    	PrayerSQLite prayerSqlite = new PrayerSQLite(this);
    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
    	ArrayList<AllGroupListItem> arrayListItem = new ArrayList<AllGroupListItem>();
        Cursor cursor;
        AllGroupListItem listItem;
        
        // DB���� All_count�� ���Ѵ�.
        cursor = db.rawQuery("SELECT count(_id) FROM prayer;", null);
        cursor.moveToNext();
        listItem = new AllGroupListItem("All", cursor.getString(0));
        arrayListItem.add(listItem);
        cursor.close();
        
        // DB���� Ungroup_count�� ���Ѵ�.
        cursor = db.rawQuery("SELECT count(_id) FROM prayer WHERE group_id IS NULL;", null);
        cursor.moveToNext();
        listItem = new AllGroupListItem("Ungroup", cursor.getString(0));
        arrayListItem.add(listItem);
        cursor.close();
    	
        // DB���� �׷������� �����´�.
        cursor = db.rawQuery("SELECT name, (SELECT count(group_id) FROM prayer where group_id = groupInfo._id) count " +
        		                      "FROM groupInfo " +
        		                      "ORDER BY name;", null);
        
        // cursor�� �׷������� arrayList�� �ű��.
        while (cursor.moveToNext()) {
        	listItem = new AllGroupListItem(cursor.getString(0), cursor.getString(1));
        	arrayListItem.add(listItem);
        }
        cursor.close();
        db.close();

        // adapter�� �����ϱ�
        m_adapter.setArrayList(arrayListItem);
        
        // ListView�� adapter �����ϱ�
        m_listView.setAdapter(m_adapter);

        m_adapter.notifyDataSetChanged();
    }
    
    
    /*****
     * 
     * 					Button Method
     * 
     *****/
    // �߰���ư Ŭ���� ȣ��Ǵ� �޼���
    public void addButton(View view) {
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
				
				// DB���� �ߺ��Ǵ� ���׷���� �����ϴ��� Ȯ���Ѵ�.
				PrayerSQLite prayerSqlite = new PrayerSQLite(AllGroupListActivity.this);
		    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
				Cursor cursor;
		        cursor = db.rawQuery("SELECT name FROM groupInfo where name = '" + newGroupName + "';", null);
		        
		        // DB�� ���׷��� ����, all, ungroup�� �ƴҰ�� ����
		        if (cursor.getCount() == 0 && !(newGroupName.toLowerCase().equals("all")) && !(newGroupName.toLowerCase().equals("ungroup"))) {
					db.execSQL("INSERT INTO groupInfo (name) VALUES ('" + newGroupName + "');");
					
					// �����ͺ��̽� ����
					setAdapter();
				}
				// DB�� ���׷��� ������ �佺Ʈ�� ���и� �˸���.
				else
					Toast.makeText(AllGroupListActivity.this, R.string.dialog_add_fail, Toast.LENGTH_SHORT).show();
				cursor.close();
				db.close();
			}
		})
		.setNegativeButton(R.string.button_cancel, null)
		.show();
    	
    	// EditText Ű���� ����ϱ�
    	alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
    
    
    public void cancelButton(View view) {
    	// �׼ǹٿ� '���', 'Ȯ��', '�߰�' ��ư�� Ŭ���ڵ鷯�� ���´�.
    	m_cancelButton.setClickable(false);
    	m_okButton.setClickable(false);
    	m_addButton.setClickable(false);
        
        // �������� checked�� �����Ѵ�.
        m_adapter.setAllCheckedFalse();
        
        // ListView�� ���ø��� �����Ѵ�.
		m_adapter.setSelectMode(false);
		m_adapter.notifyDataSetChanged();
		
		// �׼ǹ��� ��ư ������� �ٲٰ�, Ŭ���ڵ鷯�� ����Ѵ�.
		m_cancelButton.setVisibility(View.GONE);
		m_cancelButton.setClickable(true);
		m_okButton.setVisibility(View.GONE);
		m_okButton.setClickable(true);
		m_addButton.setVisibility(View.VISIBLE);
		m_addButton.setClickable(true);
		
		// �׼ǹ��� Ÿ��Ʋ�� ��ġ�� �����Ѵ�.
        m_title.setVisibility(View.VISIBLE);
        m_selectTitle.setVisibility(View.GONE);
    }
    
    
    public void okButton(View view) {
    	// �׼ǹٿ� '���', 'Ȯ��', '�߰�' ��ư�� Ŭ���ڵ鷯�� ���´�.
    	m_cancelButton.setClickable(false);
    	m_okButton.setClickable(false);
    	m_addButton.setClickable(false);
        
        // ���õ� �������� �����Ѵ�.
        m_adapter.removeAllCheckListItem();
        
        // ListView�� ���ø��� �����Ѵ�.
 		m_adapter.setSelectMode(false);
 		
 		// adapter�� ������Ʈ�Ѵ�.
 		setAdapter();
 		
 		// �׼ǹ��� ��ư ������� �ٲٰ�, Ŭ���ڵ鷯�� ����Ѵ�.
 		m_cancelButton.setVisibility(View.GONE);
 		m_cancelButton.setClickable(true);
 		m_okButton.setVisibility(View.GONE);
 		m_okButton.setClickable(true);
 		m_addButton.setVisibility(View.VISIBLE);
 		m_addButton.setClickable(true);
 		
 		// �׼ǹ��� Ÿ��Ʋ�� ��ġ�� �����Ѵ�.
 		m_title.setVisibility(View.VISIBLE);
 		m_selectTitle.setVisibility(View.GONE);
    }
    
    
    /******
     * 
     *					Listener Method 
     * 
     ******/
    
    // OnItemClickListener.onItemClick : Listview �ǽ� ȣ��Ǵ� �޼���
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	// ���� ������ �Ʒ� �ڵ带 Ȱ��ȭ�Ѵ�.
    	if (m_adapter.getIsSelectMode() == true) {
    		m_adapter.setCheckedChange(position);
    		m_adapter.notifyDataSetChanged();
    	}
    	// ������ ���ý� �ش� �׷����� �̵��Ѵ�.
    	else {
    		AllGroupListItem listItem = (AllGroupListItem)parent.getItemAtPosition(position);
    		
    		Intent intent = new Intent(this, GroupListActivity.class);
    		intent.putExtra("groupName", listItem.m_leftText);
    		
    		startActivity(intent);
    		overridePendingTransition(R.anim.left_in, R.anim.left_out);
    	}
    }
    
    // �ϵ���� ���ư
    @Override
    public void onBackPressed() {
    	if (m_adapter.getIsSelectMode() == true)
    		cancelButton(null);
    	else {
    		m_endTime = System.currentTimeMillis();
    		
    		if (m_endTime - m_startTime > 2000)
    			m_isPressedBackButton = false;
    		
    		if (m_isPressedBackButton == false) {
    			m_isPressedBackButton = true;
    			
    			m_startTime = System.currentTimeMillis();
    			
    			Toast.makeText(this, "'�ڷ�'��ư�� �ѹ� �� �����ø� ���� �˴ϴ�.", Toast.LENGTH_SHORT).show();
    		}
    		else {
    			finish();
	    		System.exit(0);
	    		android.os.Process.killProcess(android.os.Process.myPid());
    		}
    	}
    }
    

    
    /******
     * 
     * 					Menu Method
     * 
     ******/
    
    // ��Ƽ��Ƽ������ ó�� �ѹ�����Ǵ� �ݹ�޼���
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, 101, 0, R.string.menu_settings);
    	menu.add(0, 102, 0, R.string.menu_select_delete);
    	
        return true;
    }
    
    // menuȣ��� �� ȣ��Ǵ� �޼���
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	// ���ø���϶� �޴��� �����.
    	if (m_adapter.getIsSelectMode() == true)
    		menu.setGroupVisible(0, false);
    	else
    		menu.setGroupVisible(0, true);
    	
    	return true;
    }
    
    
    // menu�� context menu ������ �ڵ鷯 �޼���
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	// ȯ�漳��
    	case 101 :
    		Intent intent = new Intent(this, SettingActivity.class);
    		
    		startActivity(intent);
    		overridePendingTransition(R.anim.left_in, R.anim.left_out);
    		
    		return true;
    	// ���û���
    	case 102 :
    		// ListView�� ���ø��� �����Ѵ�.
    		m_adapter.setSelectMode(true);
    		m_adapter.notifyDataSetChanged();
    		
    		// �׼ǹٿ� '���', 'Ȯ��'��ư�� ���̰��ϰ�, '�׷��߰�'��ư�� �����.
    		m_cancelButton.setVisibility(View.VISIBLE);
    		m_okButton.setVisibility(View.VISIBLE);
    		m_addButton.setVisibility(View.GONE);
            // �׼ǹ��� Ÿ��Ʋ�� ��ġ�� �����Ѵ�.
            m_title.setVisibility(View.GONE);
            m_selectTitle.setVisibility(View.VISIBLE);
    		
    		return true;
    	}
    	
    	return false;
    }
    
    
    /******
     * 
     * 					ContextMenu Method
     * 
     ******/
    // ContextMenuȣ��� �ݹ��Լ��� ��� ȣ��ȴ�.
    // menu�� �޸� Preapare �ݹ��Լ��� �������� �ʴ´�.
    @Override
    public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);    	
    	
    	// ���ø�尡 �ƴҶ� ���ؽ�Ʈ�並 �����Ѵ�.
    	if (m_adapter.getIsSelectMode() == false) {
	    	int position = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
	    	AllGroupListItem listItem = (AllGroupListItem)m_adapter.getItem(position);
	    	menu.setHeaderTitle(listItem.m_leftText);
	    	
	    	// ListView�� ������ 0, 1�� �ƴҶ� ����ȴ�.
	    	if (position == 0 || position == 1) {
	        	menu.add(0, 201, 0, R.string.context_select);
	        	menu.add(0, 204, 0, R.string.Context_cancel);
	    	}
	    	else {
		    	menu.add(0, 201, 0, R.string.context_select);
		    	menu.add(0, 202, 0, R.string.Context_edit);
		    	menu.add(0, 203, 0, R.string.Context_delete);
		    	menu.add(0, 204, 0, R.string.Context_cancel);
			}
    	}
    }
    
    
    public boolean onContextItemSelected (MenuItem item) {
    	// m_position ������ editGorupName()�� deleteGroup()���� ���ȴ�.
    	AdapterView.AdapterContextMenuInfo adapterInfo;
    	adapterInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    	m_position = adapterInfo.position;

    	switch (item.getItemId()) {
    	// ����
    	case 201 :
    		// gorupActivity�� �̵�
    		return true;
    	// ����
    	case 202 :
    		editGroupName();
    		return true;
    	// ����
    	case 203 :
    		deleteGroup();
    		return true;
    	// ���
    	case 204 :
    		// �� ������ 
    		return true;
    	}
    	
    	return false;
    }
    
    // ContextMenu�� edit������ ���ý� ȣ��Ǵ� �޼���
    public void editGroupName() {
    	// ��ȭ���ڿ� ���� ���̾ƿ��� �����Ѵ�.
    	final LinearLayout linear = (LinearLayout) View.inflate(this, R.layout.alert_dialog_add_group, null);
    	EditText editText = (EditText)linear.findViewById(R.id.add_group_name_edit_text);
    	AllGroupListItem listItem = (AllGroupListItem)m_adapter.getItem(m_position);
		editText.setHint(listItem.m_leftText);
    	// ��ȭ���� ���� �� ����ϱ�
    	AlertDialog alertDialog = new AlertDialog.Builder(this)
    	.setTitle(getString(R.string.dialog_edit_group_title))
    	.setView(linear)
    	.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				EditText editText = (EditText)linear.findViewById(R.id.add_group_name_edit_text);
				String newGroupName = editText.getText().toString();
				
				PrayerSQLite prayerSqlite = new PrayerSQLite(AllGroupListActivity.this);
		    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();				
		    	Cursor cursor;
		        
		    	cursor = db.rawQuery("SELECT name FROM groupInfo where name = '" + newGroupName + "';", null);
		    	
		        // DB�� ���׷���� ������ ���׷������ �����Ѵ�.
				if (cursor.getCount() == 0) {
					// DB���� gorup_id�� ã�´�.
					cursor.close();
					AllGroupListItem listItem = (AllGroupListItem)m_adapter.getItem(m_position);
					cursor = db.rawQuery("SELECT _id FROM groupInfo WHERE name = '" + listItem.m_leftText + "';", null);
					cursor.moveToNext();
					int id = cursor.getInt(0);
					cursor.close();
					
					// DB���� �׷���� �����Ѵ�.
					db.execSQL("UPDATE groupInfo SET name = '" + newGroupName + "' WHERE _id = " + id +";");
					
					// �����ͺ��̽� ����
					setAdapter();
				}
				// DB�� �׷���� ������ �佺Ʈ�� ���и� �˸���.
				else {
					Toast.makeText(AllGroupListActivity.this, R.string.dialog_add_fail, Toast.LENGTH_SHORT).show();
					cursor.close();
				}
				db.close();
			}
		})
		.setNegativeButton(R.string.button_cancel, null)
		.show();
    	
    	// EditText Ű���� ����ϱ�
    	alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
    
    
    // ContextMenu�� delete������ ���ý� ȣ��Ǵ� �޼���
    public void deleteGroup() {
		PrayerSQLite prayerSqlite = new PrayerSQLite(AllGroupListActivity.this);
    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
    	Cursor cursor;
		
		// group_id��������
    	AllGroupListItem listItem = (AllGroupListItem)m_adapter.getItem(m_position);
		cursor = db.rawQuery("SELECT _id " +
				                      "FROM groupInfo " +
				                      "WHERE name = '" + listItem.m_leftText +
				                      "';", null);
		cursor.moveToNext();
		int id = cursor.getInt(0);
		cursor.close();

		// prayer ���̺��� group_id �����ϱ�
		db.execSQL("DELETE FROM prayer " +
				          "WHERE group_id = " + id +
				          ";");
		// groupInfo ���̺��� _id �����ϱ�
		db.execSQL("DELETE FROM groupInfo " +
				         "WHERE _id = " + id + 
				         ";");
		db.close();
		
		setAdapter();
    }
    
    
    /******
     * 
     *					GorupListAdapter Class
     * 
     ******/
    
    public class AllGroupListAdapter extends BaseAdapter {
    	
    	Context m_context;
    	LayoutInflater m_inflater;
    	ArrayList<AllGroupListItem> m_arrayListItem;
    	
    	boolean m_isSelectMode;
    	
    	
    	public AllGroupListAdapter(Context context) {
    		m_context = context;
    		m_inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		
    		m_isSelectMode = false;
    	}
    	
    	
    	// �ܺη� ���� ArrayList�� �޴´�.
    	public void setArrayList(ArrayList<AllGroupListItem> arrayListItem) {
    		m_arrayListItem = arrayListItem;
    	}

    	
    	// ��ü ������ ���� �����Ѵ�.
    	public int getCount() {
    		// ���ø���϶��� all, ungroup������ �ΰ��� �����Ѵ�.
    		if (m_isSelectMode == true) {
    			int count = m_arrayListItem.size() - 2;
    			// 0���� ���� ��� 0���� �����Ѵ�.
    			if (count < 0)
    				return 0;
    			else
    				return count;
    		}
    		else
    			return m_arrayListItem.size();
    	}
    	

    	// ���ڷ� ���޵� position�� �������� '�׷��̸�'�� �����Ѵ�.
    	public Object getItem(int position) {
    		AllGroupListItem listItem = new AllGroupListItem(m_arrayListItem.get(position));
    		return listItem;
    	}

    	
    	// �������� ID�� �����Ѵ�.
    	public long getItemId(int position) {
    		return position;
    	}
    	
    	
    	// Check���¸� �����Ѵ�.
    	public void setCheckedChange(int position) {
    		m_arrayListItem.get(position+2).m_isChecked = !m_arrayListItem.get(position+2).m_isChecked;
    	}
    	
    	
    	// selectMode�� �����Ѵ�.
    	public void setSelectMode(boolean isSelectMode) {
    		m_isSelectMode = isSelectMode;
    	}
    	
    	// selectmode�� Ȯ���ϱ�
    	public boolean getIsSelectMode() {
    		return m_isSelectMode;
    	}
    	
    	
    	// ��� �������� checked�� false�� �����Ѵ�.
    	public void setAllCheckedFalse() {
    		int i;
    		for (i = m_arrayListItem.size()-1; i >= 0; i--)
    			m_arrayListItem.get(i).m_isChecked = false;
    	}

    	
    	// ��� �������� checked�� Ȯ���Ͽ� true�� DB���� �����Ѵ�. 
    	public void removeAllCheckListItem() {
    		int i;
    		PrayerSQLite prayerSqlite = new PrayerSQLite(AllGroupListActivity.this);
        	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
        	Cursor cursor;
    		for (i = m_arrayListItem.size()-1; i >= 0; i--) {
    			if(m_arrayListItem.get(i).m_isChecked == true) {
    				// group_id��������
    				cursor = db.rawQuery("SELECT _id " +
    						                      "FROM groupInfo " +
    						                      "WHERE name = '" + m_arrayListItem.get(i).m_leftText +
    						                      "';", null);
    				cursor.moveToNext();
    				int id = cursor.getInt(0);
    				cursor.close();

    				// prayer ���̺��� group_id �����ϱ�
    				db.execSQL("DELETE FROM prayer " +
    						          "WHERE group_id = " + id +
    						          ";");
    				
    				// groupInfo ���̺��� _id �����ϱ�
    				db.execSQL("DELETE FROM groupInfo " +
    						         "WHERE _id = " + id + 
    						         ";");
    			}
    		}
    		
    		db.close();
    	}
    	
    	// �ش� position�� �������� �����Ѵ�.
    	public View getView(int position, View convertView, ViewGroup parent) {
    		// convertView�� null�̸� ó�� �����ϴ� �������̴�, �������� �����Ѵ�.
    		if (convertView == null) {
    				convertView = m_inflater.inflate(R.layout.list_view, parent, false);
    				
    				// CheckBox�� �����ʸ� ������� �ʰ�, ListView�� �����ʸ� ����Ѵ�.
    				CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.list_view_checkbox);
    				checkBox.setClickable(false);
    				checkBox.setFocusable(false);
    		}
    		
    		TextView leftText = (TextView)convertView.findViewById(R.id.list_view_left_text);
    		TextView rightText = (TextView)convertView.findViewById(R.id.list_view_right_text);
    		
    		// selectMode�� üũ�ڽ��� ����Ѵ�.
    		CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.list_view_checkbox);
    		if (m_isSelectMode == true) {
    			checkBox.setVisibility(View.VISIBLE);
    			checkBox.setChecked(m_arrayListItem.get(position+2).m_isChecked);
    			
    			leftText.setText(m_arrayListItem.get(position+2).m_leftText);
    			rightText.setText(m_arrayListItem.get(position+2).m_rightText);
    		}
    		else {
    			checkBox.setVisibility(View.GONE);
    			
    			leftText.setText(m_arrayListItem.get(position).m_leftText);
    			rightText.setText(m_arrayListItem.get(position).m_rightText);
    		}
    		
    		return convertView;
    	}
    }
}