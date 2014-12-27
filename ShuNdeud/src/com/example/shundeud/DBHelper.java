package com.example.shundeud;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 16 * @author Joshua 17 * �÷��� 18 * DBHelper dbHelper = new DBHelper(this); 19
 * * dbHelper.createDataBase(); 20 * SQLiteDatabase db =
 * dbHelper.getWritableDatabase(); 21 * Cursor cursor = db.query() 22 *
 * db.execSQL(sqlString); 23 * ע�⣺execSQL��֧�ִ�;�Ķ���SQL��䣬ֻ��һ��һ����ִ�У����˺ܾò����� 24 *
 * ��execSQL��Դ��ע�� (Multiple statements separated by ;s are not supported.) 25 *
 * ����assets�µ����ݿ��ļ�ֱ�Ӹ��Ƶ�DB_PATH�������ݿ��ļ���С������1M���� 26 *
 * ����г���1M�Ĵ��ļ�������Ҫ�ȷָ�ΪN��С�ļ���Ȼ��ʹ��copyBigDatabase()�滻copyDatabase() 27
 */
public class DBHelper extends SQLiteOpenHelper {
	// �û����ݿ��ļ��İ汾
	private static final int DB_VERSION = 1;
	// ���ݿ��ļ�Ŀ����·��ΪϵͳĬ��λ�ã�cn.arthur.examples ����İ���
	private static String DB_PATH = "/data/data/com.example.shundeud/databases/";
	/*
	 * 34 //�����������ݿ��ļ������SD���Ļ� 35 private static String DB_PATH =
	 * android.os.Environment.getExternalStorageDirectory().getAbsolutePath() 36
	 * + "/arthurcn/drivertest/packfiles/"; 37
	 */
	private static String DB_NAME = "ndeud.db";
	private static String ASSETS_NAME = "ndeud.db";

	private SQLiteDatabase myDataBase = null;
	private final Context myContext;

	/**
	 * 45 * ������ݿ��ļ��ϴ�ʹ��FileSplit�ָ�ΪС��1M��С�ļ� 46 * �����зָ�Ϊ hello.db.101
	 * hello.db.102 hello.db.103 47
	 */
	// ��һ���ļ�����׺
	private static final int ASSETS_SUFFIX_BEGIN = 101;
	// ���һ���ļ�����׺
	private static final int ASSETS_SUFFIX_END = 103;

	/**
	 * 54 * ��SQLiteOpenHelper�����൱�У������иù��캯�� 55 * @param context �����Ķ��� 56 * @param
	 * name ���ݿ����� 57 * @param factory һ�㶼��null 58 * @param version
	 * ��ǰ���ݿ�İ汾��ֵ���������������ǵ�����״̬ 59
	 */
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		// ����ͨ��super���ø��൱�еĹ��캯��
		super(context, name, null, version);
		this.myContext = context;
	}

	public DBHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public DBHelper(Context context, String name) {
		this(context, name, DB_VERSION);
	}

	public DBHelper(Context context) {
		this(context, DB_PATH + DB_NAME);
	}

	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();
		if (dbExist) {
			// ���ݿ��Ѵ��ڣ�do nothing.
		} else {
			// �������ݿ�
			try {
				File dir = new File(DB_PATH);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File dbf = new File(DB_PATH + DB_NAME);
				if (dbf.exists()) {
					dbf.delete();
				}
				SQLiteDatabase.openOrCreateDatabase(dbf, null);
				// ����asseets�е�db�ļ���DB_PATH��
				copyDataBase();
			} catch (IOException e) {
				throw new Error("���ݿⴴ��ʧ��");
			}
		}
	}

	// ������ݿ��Ƿ���Ч
	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		String myPath = DB_PATH + DB_NAME;
		try {
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			// database does't exist yet.
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	/**
	 * 118 * Copies your database from your local assets-folder to the just
	 * created empty database in the 119 * system folder, from where it can be
	 * accessed and handled. 120 * This is done by transfering bytestream. 121 *
	 */
	private void copyDataBase() throws IOException {
		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(ASSETS_NAME);
		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;
		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	// ����assets�µĴ����ݿ��ļ�ʱ�����
	private void copyBigDataBase() throws IOException {
		InputStream myInput;
		String outFileName = DB_PATH + DB_NAME;
		OutputStream myOutput = new FileOutputStream(outFileName);
		for (int i = ASSETS_SUFFIX_BEGIN; i < ASSETS_SUFFIX_END + 1; i++) {
			myInput = myContext.getAssets().open(ASSETS_NAME + "." + i);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}
			myOutput.flush();
			myInput.close();
		}
		myOutput.close();
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null) {
			myDataBase.close();
		}
		super.close();
	}

	/**
	 * 168 * �ú������ڵ�һ�δ�����ʱ��ִ�У� 169 * ʵ�����ǵ�һ�εõ�SQLiteDatabase�����ʱ��Ż����������� 170
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	/**
	 * 176 * ���ݿ���ṹ�б仯ʱ���� 177
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}