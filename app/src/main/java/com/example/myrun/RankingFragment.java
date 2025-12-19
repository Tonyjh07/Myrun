package com.example.myrun;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrun.adapter.RankingAdapter;
import com.example.myrun.model.RankingRecord;
import com.example.myrun.util.RankingManager;

import java.util.List;

/**
 * 排行榜Fragment
 */
public class RankingFragment extends Fragment {

    private RecyclerView rvRanking;
    private LinearLayout llEmpty;
    private TextView tvTotalUsers;
    private TextView tvTotalDistance;
    private TextView tvTotalRuns;
    private RankingAdapter adapter;
    private RankingManager rankingManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        
        // 初始化RankingManager
        if (getContext() != null) {
            rankingManager = RankingManager.getInstance(getContext());
        }
        
        // 初始化视图
        initViews(view);
        
        // 加载排行榜数据
        loadRankingData();
        
        return view;
    }

    /**
     * 初始化视图
     */
    private void initViews(View view) {
        rvRanking = view.findViewById(R.id.rv_ranking);
        llEmpty = view.findViewById(R.id.ll_empty);
        tvTotalUsers = view.findViewById(R.id.tv_total_users);
        tvTotalDistance = view.findViewById(R.id.tv_total_distance);
        tvTotalRuns = view.findViewById(R.id.tv_total_runs);
        
        // 设置RecyclerView
        if (rvRanking != null && getContext() != null) {
            rvRanking.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new RankingAdapter(null);
            rvRanking.setAdapter(adapter);
        }
    }

    /**
     * 加载排行榜数据
     */
    private void loadRankingData() {
        if (rankingManager == null && getContext() != null) {
            rankingManager = RankingManager.getInstance(getContext());
        }
        
        List<RankingRecord> rankingList = rankingManager.getRankingList();
        
        if (rankingList == null || rankingList.isEmpty()) {
            // 显示空状态
            if (rvRanking != null) {
                rvRanking.setVisibility(View.GONE);
            }
            if (llEmpty != null) {
                llEmpty.setVisibility(View.VISIBLE);
            }
            updateStatistics(0, 0.0, 0);
        } else {
            // 显示排行榜
            if (rvRanking != null) {
                rvRanking.setVisibility(View.VISIBLE);
            }
            if (llEmpty != null) {
                llEmpty.setVisibility(View.GONE);
            }
            
            // 更新适配器数据
            if (adapter != null) {
                adapter.updateData(rankingList);
            }
            
            // 计算统计数据
            int totalUsers = rankingList.size();
            double totalDistance = 0.0;
            int totalRuns = 0;
            
            for (RankingRecord record : rankingList) {
                totalDistance += record.getTotalDistance();
                totalRuns += record.getTotalRuns();
            }
            
            updateStatistics(totalUsers, totalDistance, totalRuns);
        }
    }

    /**
     * 更新统计数据
     */
    private void updateStatistics(int totalUsers, double totalDistance, int totalRuns) {
        if (tvTotalUsers != null) {
            tvTotalUsers.setText(String.valueOf(totalUsers));
        }
        if (tvTotalDistance != null) {
            tvTotalDistance.setText(String.format("%.1f", totalDistance));
        }
        if (tvTotalRuns != null) {
            tvTotalRuns.setText(String.valueOf(totalRuns));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 每次显示时刷新数据
        loadRankingData();
    }
}