package com.example.myrun.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.myrun.model.RunRecord;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 跑步记录管理工具类
 * 使用SharedPreferences存储跑步记录
 */
public class RunRecordManager {
    private static final String PREF_NAME = "run_record_prefs";
    private static final String KEY_RECORDS_PREFIX = "records_"; // 用户记录前缀

    private SharedPreferences sharedPreferences;
    private static RunRecordManager instance;

    private RunRecordManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 获取RunRecordManager单例
     */
    public static synchronized RunRecordManager getInstance(Context context) {
        if (instance == null) {
            instance = new RunRecordManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * 添加跑步记录
     * @param record 跑步记录
     */
    public void addRunRecord(RunRecord record) {
        if (record == null || record.getUsername() == null || record.getUsername().isEmpty()) {
            return;
        }

        List<RunRecord> records = getRunRecords(record.getUsername());
        records.add(record);
        saveRunRecords(record.getUsername(), records);
    }

    /**
     * 保存跑步记录（兼容旧方法名）
     */
    public void saveRecord(RunRecord record) {
        addRunRecord(record);
    }

    /**
     * 获取指定用户的所有跑步记录
     * @param username 用户名
     * @return 跑步记录列表，按时间倒序排列
     */
    public List<RunRecord> getRunRecords(String username) {
        List<RunRecord> records = new ArrayList<>();
        String json = sharedPreferences.getString(KEY_RECORDS_PREFIX + username, null);
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    RunRecord record = new RunRecord();
                    record.setId(jsonObject.optString("id", ""));
                    record.setUsername(jsonObject.optString("username", username));
                    record.setDistance(jsonObject.optDouble("distance", 0.0));
                    record.setTime(jsonObject.optLong("time", 0L));
                    record.setCalories(jsonObject.optInt("calories", 0));
                    record.setPace(jsonObject.optDouble("pace", 0.0));
                    record.setTimestamp(jsonObject.optLong("timestamp", System.currentTimeMillis()));
                    record.setDate(jsonObject.optString("date", ""));
                    records.add(record);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // 按时间倒序排列
        Collections.sort(records, new Comparator<RunRecord>() {
            @Override
            public int compare(RunRecord o1, RunRecord o2) {
                return Long.compare(o2.getTimestamp(), o1.getTimestamp());
            }
        });
        return records;
    }

    /**
     * 获取指定用户的所有跑步记录（兼容旧方法名）
     */
    public List<RunRecord> getRecords(String username) {
        return getRunRecords(username);
    }

    /**
     * 保存跑步记录列表
     */
    private void saveRunRecords(String username, List<RunRecord> records) {
        JSONArray jsonArray = new JSONArray();
        for (RunRecord record : records) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", record.getId());
                jsonObject.put("username", record.getUsername());
                jsonObject.put("distance", record.getDistance());
                jsonObject.put("time", record.getTime());
                jsonObject.put("calories", record.getCalories());
                jsonObject.put("pace", record.getPace());
                jsonObject.put("timestamp", record.getTimestamp());
                jsonObject.put("date", record.getDate());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        sharedPreferences.edit().putString(KEY_RECORDS_PREFIX + username, jsonArray.toString()).apply();
    }

    /**
     * 清空指定用户的跑步记录（用于测试）
     */
    public void clearRunRecords(String username) {
        sharedPreferences.edit().remove(KEY_RECORDS_PREFIX + username).apply();
    }
}
