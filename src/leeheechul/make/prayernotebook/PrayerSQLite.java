package leeheechul.make.prayernotebook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PrayerSQLite extends SQLiteOpenHelper {
	
	// DB ������
	public PrayerSQLite(Context context) {
		
		// �������� : 1. DB�� �����ϴ� ���ؽ�Ʈ, 2. DB ���ϸ�, 3. CursorFactory, 4.Version
		super(context, "PrayerDB.db", null, 1);
	}

	// DB�� ó�� ��������� ȣ��ȴ�. ���⼭ ���̺��� ����� �ʱ� ���ڵ带 �����Ѵ�.
	public void onCreate(SQLiteDatabase db) {
		
		// groupInfo ���̺�
		db.execSQL("CREATE TABLE groupInfo (" +
				         "_id INTEGER PRIMARY KEY, " +			// �⺻Ű�� ���� �׷� ID
				         "name TEXT" +								// �׷��
				         ");");
		
		// prayer ���̺�
		db.execSQL("CREATE TABLE prayer (" +
				         "_id INTEGER PRIMARY KEY, " +          // �⺻Ű�� ���� �⵵ ID 
				         "target TEXT, " +							// �����
				         "date TEXT, " +								// ��¥
				         "group_id INTEGER, " +						// �ܺ�Ű�� ���� �׷� ID
				         "title TEXT, " +								// �⵵����
				         "answer TEXT, " +							// ���䳻��
				         "word TEXT, " +								// ��������
				         "FOREIGN KEY(group_id) REFERENCES groupInfo(_id)" +		// group_id�� �ܺ�Ű�� ����
				         ");");
	}
	
	// DB�� ���׷��̵��� �� ȣ��ȴ�. ���� ���̺��� �����ϰ� ���� ����ų� ALTER TABLE�� ��Ű���� �����Ѵ�.
	// DB version�� DB �����ڸ� ���� Version���ڷ� Ȯ���Ͽ� �ʿ�� onUpgrade�� ȣ���Ѵ�.
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}