package com.example.myrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myrun.util.ToastUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    //声明控件
    private MaterialButton mBtnStartRun;
    private MaterialButton mBtnViewRecords;
    private BottomNavigationView mBottomNavigation;
    private MaterialToolbar mToolbar;
    
    // Fragment相关
    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private RunFragment runFragment;
    private RankingFragment rankingFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        //找到控件
        mBtnStartRun = findViewById(R.id.btn_start_run);
        mBtnViewRecords = findViewById(R.id.btn_view_records);
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        mToolbar = findViewById(R.id.toolbar);

        // 初始化Fragment
        initFragments();
        
        //设置点击监听器
        setListeners();
        
        //设置底部导航监听器
        setupBottomNavigation();
        
        //设置工具栏
        setupToolbar();
        
        // 设置返回键处理
        setupBackPressedHandler();
        
        // 检查是否有从SlideActivity传递过来的Fragment切换参数
        handleIntentFragment();
        
        // 如果没有指定Fragment，默认设置导航栏选中状态为首页
        if (mBottomNavigation != null && mBottomNavigation.getSelectedItemId() == 0) {
            mBottomNavigation.setSelectedItemId(R.id.navigation_home);
        }
    }
    
    private void handleIntentFragment() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("fragment")) {
            String fragmentType = intent.getStringExtra("fragment");
            if (fragmentType != null) {
                switch (fragmentType) {
                    case "run":
                        if (runFragment != null) {
                            switchFragment(runFragment);
                            updateToolbarTitle("跑步");
                            if (mBottomNavigation != null) {
                                mBottomNavigation.setSelectedItemId(R.id.navigation_run);
                            }
                        }
                        break;
                    case "ranking":
                        if (rankingFragment != null) {
                            switchFragment(rankingFragment);
                            updateToolbarTitle("排行榜");
                            if (mBottomNavigation != null) {
                                mBottomNavigation.setSelectedItemId(R.id.navigation_ranking);
                            }
                        }
                        break;
                    case "profile":
                        if (profileFragment != null) {
                            switchFragment(profileFragment);
                            updateToolbarTitle("个人中心");
                            if (mBottomNavigation != null) {
                                mBottomNavigation.setSelectedItemId(R.id.navigation_profile);
                            }
                        }
                        break;
                    default:
                        if (homeFragment != null) {
                            switchFragment(homeFragment);
                            updateToolbarTitle("跑步记录");
                            if (mBottomNavigation != null) {
                                mBottomNavigation.setSelectedItemId(R.id.navigation_home);
                            }
                        }
                        break;
                }
            }
        }
    }
    
    private void setListeners(){
        //开始跑步按钮点击事件
        if (mBtnStartRun != null) {
            mBtnStartRun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 开始跑步功能
                    ToastUtil.showMsg(MainActivity.this, "开始跑步功能");
                }
            });
        }
        
        //查看全部记录按钮点击事件
        if (mBtnViewRecords != null) {
            mBtnViewRecords.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 查看跑步记录功能
                    ToastUtil.showMsg(MainActivity.this, "查看跑步记录功能");
                }
            });
        }
    }
    
    private void setupBottomNavigation() {
        if (mBottomNavigation != null) {
            mBottomNavigation.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    // 切换到首页Fragment
                    switchFragment(homeFragment);
                    updateToolbarTitle("跑步记录");
                    return true;
                } else if (itemId == R.id.navigation_run) {
                    // 切换到跑步Fragment
                    switchFragment(runFragment);
                    updateToolbarTitle("跑步");
                    return true;
                } else if (itemId == R.id.navigation_ranking) {
                    // 切换到排行榜Fragment
                    switchFragment(rankingFragment);
                    updateToolbarTitle("排行榜");
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    // 直接跳转到"我的"界面
                    Intent intent = new Intent(MainActivity.this, MyActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            });
        }
    }
    
    // setupSlideMenu()方法已删除，因为相关控件已不存在
    
    /**
     * 设置返回键处理
     */
    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 执行默认的返回操作
                finish();
            }
        });
    }
    
    private void setupToolbar() {
        // 工具栏设置（如果需要可以添加其他功能）
        if (mToolbar != null) {
            // 可以在这里添加工具栏的其他功能
        }
    }
    
    /**
     * 初始化Fragment
     */
    private void initFragments() {
        fragmentManager = getSupportFragmentManager();
        
        // 创建Fragment实例
        homeFragment = new HomeFragment();
        runFragment = new RunFragment();
        rankingFragment = new RankingFragment();
        profileFragment = new ProfileFragment();
        
        // 默认显示首页
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, homeFragment);
        transaction.commit();
    }
    
    /**
     * 切换Fragment
     */
    private void switchFragment(Fragment fragment) {
        if (fragment == null || fragmentManager == null) {
            return;
        }
        
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        
        // 隐藏所有Fragment
        if (homeFragment != null && homeFragment.isAdded()) {
            transaction.hide(homeFragment);
        }
        if (runFragment != null && runFragment.isAdded()) {
            transaction.hide(runFragment);
        }
        if (rankingFragment != null && rankingFragment.isAdded()) {
            transaction.hide(rankingFragment);
        }
        if (profileFragment != null && profileFragment.isAdded()) {
            transaction.hide(profileFragment);
        }
        
        // 显示目标Fragment
        if (!fragment.isAdded()) {
            transaction.add(R.id.fragment_container, fragment);
        } else {
            transaction.show(fragment);
        }
        
        transaction.commit();
    }
    
    /**
     * 更新工具栏标题
     */
    private void updateToolbarTitle(String title) {
        if (mToolbar != null) {
            mToolbar.setTitle(title);
        }
    }
    
    // 提供给HomeFragment调用的方法，切换到跑步界面
    public void switchToRunFragment() {
        if (runFragment != null) {
            switchFragment(runFragment);
            updateToolbarTitle("跑步中");
            if (mBottomNavigation != null) {
                mBottomNavigation.setSelectedItemId(R.id.navigation_run);
            }
        }
    }
}