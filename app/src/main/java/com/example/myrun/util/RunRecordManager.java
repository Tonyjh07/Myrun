package com.example.myrun.util;

import android.content.Context;
import com.example.myrun.model.RunRecord;
import java.util.List;

public class RunRecordManager {
    private static RunRecordManager instance;
    
    private RunRecordManager(Context context) {}
    
    public static RunRecordManager getInstance(Context context) {
        if (instance == null) {
            instance = new RunRecordManager(context);
        }
        return instance;
    }
    
    public void saveRecord(RunRecord record) {}
    
    public List<RunRecord> getRecords(String username) {
        return null;
    }
    
    public void addRunRecord(RunRecord record) {
        // TODO: 实现添加跑步记录逻辑
    }
}