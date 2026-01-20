package com.example.myrun.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.myrun.model.RankingRecord;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RankingManager {
    private static final String PREF_NAME = "ranking_prefs";
    private static final String KEY_RANKING_LIST = "ranking_list";
    
    private static RankingManager instance;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    
    private RankingManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }
    
    public static RankingManager getInstance(Context context) {
        if (instance == null) {
            instance = new RankingManager(context.getApplicationContext());
        }
        return instance;
    }
    
    public List<RankingRecord> getRankingList() {
        String json = sharedPreferences.getString(KEY_RANKING_LIST, null);
        if (json == null) {
            // 返回默认数据用于测试
            List<RankingRecord> defaultList = new ArrayList<>();
            defaultList.add(new RankingRecord("测试用户", 5, 25.5, 3600));
            defaultList.add(new RankingRecord("跑步达人", 3, 15.2, 2400));
            defaultList.add(new RankingRecord("新手用户", 1, 3.5, 600));
            return defaultList;
        }
        
        Type type = new TypeToken<List<RankingRecord>>(){}.getType();
        return gson.fromJson(json, type);
    }
    
    public RankingRecord getUserRecord(String username) {
        List<RankingRecord> rankingList = getRankingList();
        if (rankingList != null) {
            for (RankingRecord record : rankingList) {
                if (record.getUsername().equals(username)) {
                    return record;
                }
            }
        }
        return null;
    }
    
    public void addRunningRecord(String username, double distance, long time) {
        List<RankingRecord> rankingList = getRankingList();
        if (rankingList == null) {
            rankingList = new ArrayList<>();
        }
        
        // 查找是否已存在该用户的记录
        RankingRecord existingRecord = null;
        for (RankingRecord record : rankingList) {
            if (record.getUsername().equals(username)) {
                existingRecord = record;
                break;
            }
        }
        
        if (existingRecord != null) {
            // 更新现有记录
            int newTotalRuns = existingRecord.getTotalRuns() + 1;
            double newTotalDistance = existingRecord.getTotalDistance() + distance;
            long newTotalTime = existingRecord.getTotalTime() + time;
            
            rankingList.remove(existingRecord);
            rankingList.add(new RankingRecord(username, newTotalRuns, newTotalDistance, newTotalTime));
        } else {
            // 添加新记录
            rankingList.add(new RankingRecord(username, 1, distance, time));
        }
        
        // 保存到SharedPreferences
        String json = gson.toJson(rankingList);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_RANKING_LIST, json);
        editor.apply();
    }
}