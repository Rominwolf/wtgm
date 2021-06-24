package ink.wsm.wtgm.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import ink.wsm.wtgm.bean.MaterialNameConvert;
import ink.wsm.wtgm.bean.StageBaseInfo;

import static java.lang.Integer.parseInt;

public class SqLiteHelperUtil extends SQLiteOpenHelper {
    public static final String DB_NAME = "library.db";
    public static final String TABLE_NAME_MATERIALS = "materials";
    public static final String TABLE_NAME_STAGES = "stages";
    public static final String TABLE_NAME_USERS = "users";
    public static final String TABLE_NAME_SETTINGS = "settings";

    public SqLiteHelperUtil(Context context) {
        super(context, DB_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_MATERIALS = "CREATE TABLE " + TABLE_NAME_MATERIALS
                + " (id integer primary key, name_chinese text, name_english text, star integer)";
        String CREATE_TABLE_STAGES = "CREATE TABLE " + TABLE_NAME_STAGES
                + " (id text primary key, code text, sp_cost integer, name text, description text)";
        String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_NAME_USERS
                + " (id integer primary key autoincrement, username text, password text)";
        String CREATE_TABLE_SETTINGS = "CREATE TABLE " + TABLE_NAME_SETTINGS
                + " (k text primary key, v text)";

        db.execSQL(CREATE_TABLE_MATERIALS);
        db.execSQL(CREATE_TABLE_STAGES);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_SETTINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /* 插入材料信息到 materials 表 */
    public long insertMaterialInfo(MaterialNameConvert materialNameConvert) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("id", materialNameConvert.getId());
        contentValues.put("name_chinese", materialNameConvert.getNameChinese());
        contentValues.put("name_english", materialNameConvert.getNameEnglish());
        contentValues.put("star", 0);

        long result = db.insert(TABLE_NAME_MATERIALS, null, contentValues);
        db.close();
        return result;
    }

    /* 插入材料信息到 stages 表 */
    public long insertStageInfo(StageBaseInfo stageBaseInfo) {
        delete(TABLE_NAME_STAGES, "id = ?", new String[]{stageBaseInfo.getId()});

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("id", stageBaseInfo.getId());
        contentValues.put("code", stageBaseInfo.getCode());
        contentValues.put("sp_cost", stageBaseInfo.getSpCost());
        contentValues.put("name", stageBaseInfo.getName());
        contentValues.put("description", stageBaseInfo.getDescription());

        long result = db.insert(TABLE_NAME_STAGES, null, contentValues);
        db.close();
        return result;
    }

    /* 插入新属性到 settings 表 */
    public long insertSettingData(String k, String v) {
        String quotaK = "'" + k + "'";
        String isExistString = getTheStringValue(TABLE_NAME_SETTINGS, "v", "k", quotaK);
        if(isExistString != null) return 0;

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("k", k);
        contentValues.put("v", v);

        long result = db.insert(TABLE_NAME_SETTINGS, null, contentValues);
        db.close();
        return result;
    }

    /* 更新数据 */
    public int updateData(String table, ContentValues contentValues, String where, String[] args) {
        //ContentValues values = new ContentValues();
        //values.put("name", student.getName());
        SQLiteDatabase db = getWritableDatabase();
        int result = db.update(table, contentValues, where, args);
        db.close();
        return result;
    }

    /* 获取指定表中的指定值 */
    public String getTheStringValue(String table, String select, String where, String key) {
        //Select name from table where id = 1;
        String result = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + select + " FROM " + table
                + " where " + where + " = " + key, null);
        if (cursor != null)
            while (cursor.moveToNext())
                result = cursor.getString(cursor.getColumnIndex(select));
        cursor.close();
        db.close();
        return result;
    }

    /* 获取指定表中的指定值 */
    public int getTheIntValue(String table, String select, String where, String key) {
        //Select name from table where id = 1;
        int result = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + select + " FROM " + table
                + " where " + where + " = " + key, null);
        if (cursor != null)
            while (cursor.moveToNext())
                result = cursor.getInt(cursor.getColumnIndex(select));
        cursor.close();
        db.close();
        return result;
    }

    /* 获取指定表的行数 */
    public int getTableRows(String table, String key) {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(" + key + ") AS count FROM " + table, null);
        if (cursor != null)
            while (cursor.moveToNext())
                count = parseInt(cursor.getString(cursor.getColumnIndex("count")));
        cursor.close();
        db.close();
        return count;
    }

    /* 获取 stages 表里指定关卡的数据 */
    public StageBaseInfo getStageBaseInfo(String language, String stageId) {
        String id = language + "/" + stageId;
        SQLiteDatabase db = getReadableDatabase();
        StageBaseInfo stageBaseInfo = new StageBaseInfo();

        Cursor cursor = db.query(TABLE_NAME_STAGES, null, "id like ?",
                new String[]{id},null, null, null);
        if (cursor != null){
            while (cursor.moveToNext()){
                int spCost = cursor.getInt(cursor.getColumnIndex("sp_cost"));
                String code = cursor.getString(cursor.getColumnIndex("code"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String description = cursor.getString(cursor.getColumnIndex("description"));

                stageBaseInfo.setStageId(stageId);
                stageBaseInfo.setSpCost(spCost);
                stageBaseInfo.setName(name);
                stageBaseInfo.setLanguage(language);
                stageBaseInfo.setDescription(description);
                stageBaseInfo.setCode(code);
            }
            cursor.close();
        }
        db.close();
        return stageBaseInfo;
    }

    /* 获取所有标星了的材料 */
    public ArrayList getMaterialsStarredList() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList starList = new ArrayList();
        Cursor cursor = db.query(TABLE_NAME_MATERIALS, null, "star like 1",
                null,null, null, null);
        if (cursor != null){
            while (cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                starList.add(id);
            }
            cursor.close();
        }
        db.close();
        return starList;
    }

    /* 插入新用户到 users 表 */
    public long insertNewUserInfo(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);

        long result =  db.insert(TABLE_NAME_USERS, null, contentValues);
        db.close();
        return result;
    }

    /* 删除一列 */
    public void delete(String table, String where, String[] args) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(table, where, args);
        db.close();
    }
}
