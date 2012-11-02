package leeheechul.make.prayernotebook;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class GroupListActivity extends Activity implements OnItemClickListener {
	
	int m_position;
	int m_groupId;
	String m_groupName;
	GroupListAdapter m_adapter;
	int m_sortMode;
	boolean m_isSelectMove;
	ArrayAdapter<String> m_groupList;
	
	Button m_cancelButton;
	Button m_okButton;
	Button m_addButton;
	TextView m_title;
	TextView m_selectTitle;
	ListView m_listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        
        // ���� �ʱ�ȭ
        m_cancelButton = (Button)findViewById(R.id.group_list_activity_cancel_button);
        m_okButton = (Button)findViewById(R.id.group_list_activity_ok_botton);
        m_addButton = (Button)findViewById(R.id.group_list_activity_add_botton);
        m_title = (TextView)findViewById(R.id.group_list_activity_title);
        m_selectTitle = (TextView)findViewById(R.id.group_list_activity_title_select_mode);
        
        // �׼ǹ� '���', 'Ȯ��'��ư �����
        m_cancelButton.setVisibility(View.GONE);
        m_okButton.setVisibility(View.GONE);
        
        // �׼ǹ��� Ÿ��Ʋ�� ��ġ�� �����Ѵ�.
        m_selectTitle.setVisibility(View.GONE);
        
        // ����Ʈ ���ý� ������ �������� �����Ѵ�.
        m_listView = (ListView)findViewById(R.id.group_list_activity_list);
        m_listView.setSelector(R.drawable.my_selector);
        
        // ����Ʈ ������ �ʱ�ȭ
        m_listView.setOnItemClickListener(this);
        registerForContextMenu(m_listView);
        
        // ����Ʈ���� ������������
        Intent intent = getIntent();
        m_groupName = intent.getStringExtra("groupName");
        
        // Ÿ��Ʋ�� groupName���� ����
        m_title.setText(m_groupName);
        
        PrayerSQLite prayerSqlite = new PrayerSQLite(this);
    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
        Cursor cursor;
        
        // DB���� �׷�ID��������
        if (!m_groupName.equals("All") && !m_groupName.equals("Ungroup")) {
	        cursor = db.rawQuery("SELECT _id FROM groupInfo WHERE name = '" + m_groupName + "';", null);
	        cursor.moveToNext();
	        m_groupId = cursor.getInt(0);
	        cursor.close();
        }
        db.close();
        
        
        // ������� �ʱ�ȭ
        m_adapter = new GroupListAdapter(this);
        m_groupList = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        
        // sort_mode�� �����Ѵ�.
        SharedPreferences pref = getSharedPreferences("Setting", 0);
        m_sortMode = pref.getInt("sortMode", 1);
    }
    
    
    @Override
    public void onStart() {
    	super.onStart();
    	
    	// ��й�ȣ������ ture�̰�, ��׶��� ���ͽ� ��й�ȣ��Ƽ��Ƽ�� �����Ѵ�.
    	PrayerApplication app = (PrayerApplication)getApplication();
    	if (app.getIsSetPassword() == true) {
			if (app.getPreActivity().equals("GroupListActivity")) {
				Intent intent = new Intent(this, PasswordActivity.class);
	    		
	    		startActivity(intent);
			}
			
			// ���� ��Ƽ��Ƽ�� �����Ѵ�.
			app.setPreActivity("GroupListActivity");
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
    	ArrayList<GroupListItem> arrayListItem = new ArrayList<GroupListItem>();
        Cursor cursor;
        GroupListItem listItem;
    	
        // DB���� ������ �����´�.
        if(m_groupName.equals("All")) {
        	if(m_sortMode == 1)
        		cursor = db.rawQuery("SELECT _id, target, date FROM prayer ORDER BY target ASC;", null);
        	else if(m_sortMode == 2)
            	cursor = db.rawQuery("SELECT _id, target, date FROM prayer ORDER BY target DESC;", null);
        	else if(m_sortMode == 3)
            	cursor = db.rawQuery("SELECT _id, target, date FROM prayer ORDER BY date ASC;", null);
        	else
            	cursor = db.rawQuery("SELECT _id, target, date FROM prayer ORDER BY date DESC;", null);
        }
        else if (m_groupName.equals("Ungroup")) {
        	if(m_sortMode == 1)
        		cursor = db.rawQuery("SELECT _id, target, date FROM prayer WHERE group_id IS NULL ORDER BY target ASC;", null);
        	else if(m_sortMode == 2)
        		cursor = db.rawQuery("SELECT _id, target, date FROM prayer WHERE group_id IS NULL ORDER BY target DESC;", null);
        	else if(m_sortMode == 3)
        		cursor = db.rawQuery("SELECT _id, target, date FROM prayer WHERE group_id IS NULL ORDER BY date ASC;", null);
        	else
        		cursor = db.rawQuery("SELECT _id, target, date FROM prayer WHERE group_id IS NULL ORDER BY date DESC;", null);
        }
        else {
        	if(m_sortMode == 1)
        		cursor = db.rawQuery("SELECT _id, target, date FROM prayer WHERE group_id = '" + m_groupId + "' ORDER BY target ASC;", null);
        	else if(m_sortMode == 2)
        		cursor = db.rawQuery("SELECT _id, target, date FROM prayer WHERE group_id = '" + m_groupId + "' ORDER BY target DESC;", null);
        	else if(m_sortMode == 3)
        		cursor = db.rawQuery("SELECT _id, target, date FROM prayer WHERE group_id = '" + m_groupId + "' ORDER BY date ASC;", null);
        	else
        		cursor = db.rawQuery("SELECT _id, target, date FROM prayer WHERE group_id = '" + m_groupId + "' ORDER BY date DESC;", null);
        }
        
        // cursor�� �׷������� arrayList�� �ű��.
        while (cursor.moveToNext()) {
        	listItem = new GroupListItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
        	arrayListItem.add(listItem);
        }
        cursor.close();
        db.close();

        // adapter�� �����ϱ�
        m_adapter.setArrayList(arrayListItem);
        
        // ListView�� adapter �����ϱ�
        ListView list = (ListView)findViewById(R.id.group_list_activity_list);
        list.setAdapter(m_adapter);

        m_adapter.notifyDataSetChanged();
    }
    
    /*****
     * 
     * 					Button Method
     * 
     *****/
    
    public void backButton(View view){
    	finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
    
    // �߰���ư Ŭ���� ȣ��Ǵ� �޼���
    public void addButton(View view) {
    	Intent intent = new Intent(this, NewPrayerActivity.class);
    	intent.putExtra("groupName", m_groupName);
		
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
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
    	if (m_isSelectMove == true) {
    		// ���õ� �������� �̵��Ѵ�.
    		openContextMenu(m_listView);
    	}
    	else {
    		// �׼ǹٿ� '���', 'Ȯ��', '�߰�' ��ư�� Ŭ���ڵ鷯�� ���´�.
        	m_cancelButton.setClickable(false);
        	m_okButton.setClickable(false);
        	m_addButton.setClickable(false);
        	
	        // ���õ� �������� �����Ѵ�.
	        m_adapter.removeAllCheckListItem();
	        // ListView�� ���ø��� �����Ѵ�.
	 		m_adapter.setSelectMode(false);
	 		// �������� checked�� �����Ѵ�.
	        m_adapter.setAllCheckedFalse();
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
    	// ������ ���ý� prayerActivity�� �̵��Ѵ�.
    	else {
    		GroupListItem listItem = (GroupListItem)parent.getItemAtPosition(position);
    		
    		Intent intent = new Intent(this, PrayerActivity.class);
    		intent.putExtra("prayerId", listItem.m_id);
    		intent.putExtra("groupName", m_groupName);
    		
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
    		finish();
    		overridePendingTransition(R.anim.right_in, R.anim.right_out);
    	}
    }

    
    /******
     * 
     * 					Option Menu Method
     * 
     ******/
    
    // ��Ƽ��Ƽ������ ó�� �ѹ�����Ǵ� �ݹ�޼���
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, 101, 0, R.string.menu_select_move);
    	menu.add(0, 102, 0, R.string.menu_select_delete);
    	SubMenu sort = menu.addSubMenu(R.string.menu_sort);
    	
    	sort.add(0, 201, 0, R.string.sub_menu_sort_abc);
    	sort.add(0, 202, 0, R.string.sub_menu_sort_cba);
    	sort.add(0, 203, 0, R.string.sub_menu_sort_123);
    	sort.add(0, 204, 0, R.string.sub_menu_sort_321);
    	
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
    	SharedPreferences pref = getSharedPreferences("Setting", 0);
    	SharedPreferences.Editor edit = pref.edit();
    	
    	switch (item.getItemId()) {
    	// �����̵�
    	case 101 :
    		m_isSelectMove = true;
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
    		m_selectTitle.setText(R.string.menu_select_move);
    		
    		return true;
    	// ���û���
    	case 102 :
    		m_isSelectMove = false;
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
    		m_selectTitle.setText(R.string.menu_select_delete);
    		
    		return true;
        /***
         * 			Sort Mode
         ***/
    	// ������ 
    	case 201 :
    		m_sortMode = 1;
    		edit.putInt("sortMode", 1);
    		edit.commit();
    		setAdapter();
    		return true;
    	// ������
    	case 202 :
    		m_sortMode = 2;
    		edit.putInt("sortMode", 2);
    		edit.commit();
    		setAdapter();
    		return true;
    	// 1 2 3
    	case 203 :
    		m_sortMode = 3;
    		edit.putInt("sortMode", 3);
    		edit.commit();
    		setAdapter();
    		return true;
    	// 3 2 1
    	case 204 :
    		m_sortMode = 4;
    		edit.putInt("sortMode", 4);
    		edit.commit();
    		setAdapter();
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
	    	GroupListItem listItem = (GroupListItem)m_adapter.getItem(position);
	    	menu.setHeaderTitle(listItem.m_leftText);
	    	
	    	menu.add(0, 201, 0, R.string.context_select);
	    	menu.add(0, 202, 0, R.string.Context_edit);
	    	menu.add(0, 203, 0, R.string.Context_delete);
	    	menu.add(0, 204, 0, R.string.Context_cancel);
    	}
    	else if (m_isSelectMove == true && m_adapter.getIsSelectMode() == true) {
    		// DB���� �׷�� ��������
			PrayerSQLite prayerSqlite = new PrayerSQLite(this);
	    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
	    	Cursor cursor;
	    	
	    	cursor = db.rawQuery("SELECT name FROM groupInfo ORDER BY name;", null);
	    	
	    	m_groupList.clear();
	    	m_groupList.add("Ungroup");
	    	while(cursor.moveToNext())
	    		m_groupList.add(cursor.getString(0));
	    	cursor.close();
	
		    menu.setHeaderTitle(R.string.prayer_group);
		    
		    for (int i = 0; i < m_groupList.getCount(); i++)
		    	menu.add(0, i, 0, m_groupList.getItem(i).toString());
    	}
    }
    
    
    public boolean onContextItemSelected (MenuItem item) {
    	Log.i("TAG", ">>>>> itme : " + item.getItemId());
    	if (m_isSelectMove == true) {
    		int position = item.getItemId();
    		m_adapter.moveAllCheckListItem(m_groupList.getItem(position));
    		m_isSelectMove = false;
    		// m_isSelectMove=false �Ǿ����� ��ư�� ���߱� ���� �ѹ��� �����Ѵ�.
    		okButton(null);
    	}
    	else {
    		// position������ m_position���� �����Ѵ�.
        	AdapterView.AdapterContextMenuInfo adapterInfo;
        	adapterInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        	m_position = adapterInfo.position;
        	
	    	switch (item.getItemId()) {
	    	// ����
	    	case 201 :
	    		selectPrayer();
	    		return true;
	    	// ����
	    	case 202 :
	    		editPrayer();
	    		return true;
	    	// ����
	    	case 203 :
	    		deletePrayer();
	    		return true;
	    	// ���
	    	case 204 :
	    		// �� ������ 
	    		return true;
	    	}
    	}

    	return false;
    }
    
    public void selectPrayer() {
    	GroupListItem listItem = (GroupListItem)m_adapter.getItem(m_position);
		
		Intent intent = new Intent(this, PrayerActivity.class);
		intent.putExtra("prayerId", listItem.m_id);
		intent.putExtra("groupName", m_groupName);
		
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }
    
    // ContextMenu�� edit������ ���ý� ȣ��Ǵ� �޼���
    public void editPrayer() {
    	GroupListItem listItem = (GroupListItem)m_adapter.getItem(m_position);
    	
    	Intent intent = new Intent(this, EditPrayerActivity.class);
		intent.putExtra("prayerId", listItem.m_id);
		intent.putExtra("groupName", m_groupName);
		// editGroup���� prayer������ PrayerActivity�� �����Ѵ�.
		// GroupListAcitivy���� ȣ�������� false�� �Ѵ�.
		intent.putExtra("isCallPrayerActivity", false);			
		
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }
    
    
    // ContextMenu�� delete������ ���ý� ȣ��Ǵ� �޼���
    public void deletePrayer() {
		PrayerSQLite prayerSqlite = new PrayerSQLite(GroupListActivity.this);
    	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
    	
    	GroupListItem listItem = (GroupListItem)m_adapter.getItem(m_position);
    	
    	db.execSQL("DELETE FROM prayer WHERE _id = " + listItem.m_id + ";");
    	
		db.close();
		
		setAdapter();
    }
    
    
    /******
     * 
     *					AllGorupListAdapter Class
     * 
     ******/
    
    public class GroupListAdapter extends BaseAdapter {
    	
    	Context m_context;
    	LayoutInflater m_inflater;
    	ArrayList<GroupListItem> m_arrayListItem;
    	
    	boolean m_isSelectMode;
    	
    	
    	public GroupListAdapter(Context context) {
    		m_context = context;
    		m_inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		
    		m_isSelectMode = false;
    	}
    	
    	
    	// �ܺη� ���� ArrayList�� �޴´�.
    	public void setArrayList(ArrayList<GroupListItem> arrayListItem) {
    		m_arrayListItem = arrayListItem;
    	}

    	
    	// ��ü ������ ���� �����Ѵ�.
    	public int getCount() {
    		return m_arrayListItem.size();
    	}
    	

    	// ���ڷ� ���޵� position�� ListItem�� �����Ѵ�.
    	public Object getItem(int position) {
    		GroupListItem listItem = new GroupListItem(m_arrayListItem.get(position));
    		return listItem;
    	}

    	
    	// �������� ID�� �����Ѵ�.
    	public long getItemId(int position) {
    		return position;
    	}
    	
    	
    	// Check���¸� �����Ѵ�.
    	public void setCheckedChange(int position) {
    		m_arrayListItem.get(position).m_isChecked = !m_arrayListItem.get(position).m_isChecked;
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
    	
    	
    	// ��� �������� checked�� Ȯ���Ͽ� true�� group_id�� �����Ѵ�.
    	public void moveAllCheckListItem(String groupName) {
    		PrayerSQLite prayerSqlite = new PrayerSQLite(GroupListActivity.this);
        	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
        	Cursor cursor;
        	
        	if (groupName.toLowerCase().equals("ungroup")) {
        		int i;
	    		for (i = m_arrayListItem.size()-1; i >= 0; i--) {
	    			if(m_arrayListItem.get(i).m_isChecked == true) {
	    				db.execSQL("UPDATE prayer SET group_id = null WHERE _id = " + m_arrayListItem.get(i).m_id + ";");
	    			}
	    		}
        	}
        	else {
        		// �׷� IDã��
	        	cursor = db.rawQuery("SELECT _id FROM groupInfo WHERE name = '" + groupName + "';", null);
	        	cursor.moveToNext();
	        	int groupId = cursor.getInt(0);
	        	cursor.close();
	        	
	        	int i;
	    		for (i = m_arrayListItem.size()-1; i >= 0; i--) {
	    			if(m_arrayListItem.get(i).m_isChecked == true) {
	    				db.execSQL("UPDATE prayer SET group_id = " + groupId + " WHERE _id = " + m_arrayListItem.get(i).m_id + ";");
	    			}
	    		}
        	}
    		db.close();
    		
    		// ListView�� ���ø��� �����Ѵ�.
	 		m_adapter.setSelectMode(false);
	 		// �������� checked�� �����Ѵ�.
	        m_adapter.setAllCheckedFalse();
	 		// adapter�� ������Ʈ�Ѵ�.
	 		setAdapter();
    	}

    	
    	// ��� �������� checked�� Ȯ���Ͽ� true�� DB���� �����Ѵ�. 
    	public void removeAllCheckListItem() {
    		int i;
    		PrayerSQLite prayerSqlite = new PrayerSQLite(GroupListActivity.this);
        	SQLiteDatabase db = prayerSqlite.getWritableDatabase();
    		for (i = m_arrayListItem.size()-1; i >= 0; i--) {
    			if(m_arrayListItem.get(i).m_isChecked == true) {
    				// prayer ���̺��� �ش� ���� �����ϱ�
    				db.execSQL("DELETE FROM prayer " +
    						          "WHERE target = '" + m_arrayListItem.get(i).m_leftText +
    						          "' AND date = '" + m_arrayListItem.get(i).m_rightText + "';");
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
    		
    		leftText.setText(m_arrayListItem.get(position).m_leftText);
			rightText.setText(m_arrayListItem.get(position).m_rightText);
    		
    		// selectMode�� üũ�ڽ��� ����Ѵ�.
    		CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.list_view_checkbox);
    		if (m_isSelectMode == true) {
    			checkBox.setVisibility(View.VISIBLE);
    			checkBox.setChecked(m_arrayListItem.get(position).m_isChecked);
    		}
    		else {
    			checkBox.setVisibility(View.GONE);
    		}
    		
    		return convertView;
    	}
    }
}