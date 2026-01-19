package com.example.myrun.util;

import android.content.Context;
import com.example.myrun.model.RankingRecord;
import java.util.List;

public class RankingManager {
    private static RankingManager instance;
    
    private RankingManager(Context context) {}
    
    public static RankingManager getInstance(Context context) {
        if (instance == null) {
            instance = new RankingManager(context);
        }
        return instance;
    }
    
    public List<RankingRecord> getRankingList() {
        return null;
    }
    
    public RankingRecord getUserRecord(String username) {
        return null;
    }
    
    public void addRunningRecord(String username, double distance, long time) {
        // TODO: 实现添加跑步记录逻辑
    }
}