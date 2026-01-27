package com.example.myrun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myrun.util.StatusBarUtil;
import com.example.myrun.util.ToastUtil;
import com.example.myrun.util.UserManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    //声明控件
    private BottomNavigationView mBottomNavigation;
    private MaterialToolbar mToolbar;
    private FloatingActionButton mFabAI;
    private CoordinatorLayout mCoordinatorLayout;
    
    // FAB拖拽相关
    private float mFabX;
    private float mFabY;
    private float mTouchX;
    private float mTouchY;
    private boolean mIsDragging = false;
    private static final float CLICK_THRESHOLD = 10f; // 点击阈值，小于此距离认为是点击

    // Fragment相关
    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    protected RunFragment runFragment;
    private RankingFragment rankingFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            // 检查登录状态
            UserManager userManager = UserManager.getInstance(this);
            if (userManager == null || userManager.getCurrentUsername() == null || userManager.getCurrentUsername().isEmpty()) {
                // 未登录，跳转到登录页面
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            // 设置系统状态栏让出空间
            StatusBarUtil.setSystemStatusBar(this);
            setContentView(R.layout.activity_main);

            // 为根布局设置系统栏内边距
            View mainContainer = findViewById(R.id.main_container);
            if (mainContainer != null) {
                StatusBarUtil.setupViewPadding(mainContainer);
            }

            //找到控件
            mBottomNavigation = findViewById(R.id.bottom_navigation);
            mToolbar = findViewById(R.id.toolbar);
            mFabAI = findViewById(R.id.fab_ai);
            mCoordinatorLayout = findViewById(R.id.main_container);
            
            // 确保底部导航栏在最上层，可以接收点击事件
            if (mBottomNavigation != null) {
                mBottomNavigation.bringToFront();
                mBottomNavigation.setClickable(true);
                mBottomNavigation.setFocusable(true);
                // 确保所有菜单项都可见
                mBottomNavigation.setLabelVisibilityMode(com.google.android.material.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
            }
            
            // 设置FAB为可拖拽
            setupDraggableFAB();

            // 初始化Fragment
            initFragments();

            //设置AI FAB点击监听器
            setupAIFABListener();

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
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showMsg(this, "应用启动失败，请重试");
            finish();
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
                            updateToolbarTitle("我的");
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


    private void setupBottomNavigation() {
        if (mBottomNavigation != null) {
            mBottomNavigation.setOnNavigationItemSelectedListener(item -> {
                try {
                    int itemId = item.getItemId();
                    if (itemId == R.id.navigation_home) {
                        // 切换到首页Fragment
                        if (homeFragment != null) {
                            switchFragment(homeFragment);
                            updateToolbarTitle("跑步记录");
                        }
                        return true;
                    } else if (itemId == R.id.navigation_run) {
                        // 切换到跑步Fragment
                        if (runFragment != null) {
                            switchFragment(runFragment);
                            updateToolbarTitle("跑步");
                        }
                        return true;
                    } else if (itemId == R.id.navigation_ranking) {
                        // 切换到排行榜Fragment
                        if (rankingFragment != null) {
                            switchFragment(rankingFragment);
                            updateToolbarTitle("排行榜");
                        }
                        return true;
                    } else if (itemId == R.id.navigation_profile) {
                        // 切换到个人中心Fragment
                        if (profileFragment == null) {
                            profileFragment = new ProfileFragment();
                        }
                        if (profileFragment != null) {
                            switchFragment(profileFragment);
                            updateToolbarTitle("我的");
                        } else {
                            ToastUtil.showMsg(MainActivity.this, "无法加载个人中心");
                        }
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showMsg(this, "切换页面失败");
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
    protected void initFragments() {
        try {
            fragmentManager = getSupportFragmentManager();
            if (fragmentManager == null) {
                return;
            }

            // 创建Fragment实例
            homeFragment = new HomeFragment();
            runFragment = new RunFragment();
            rankingFragment = new RankingFragment();
            profileFragment = new ProfileFragment();

            // 默认显示首页
            if (homeFragment != null) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.fragment_container, homeFragment);
                transaction.commitAllowingStateLoss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showMsg(this, "初始化页面失败");
        }
    }

    /**
     * 切换Fragment
     */
    private void switchFragment(Fragment fragment) {
        if (fragment == null || fragmentManager == null) {
            ToastUtil.showMsg(this, "Fragment为空或FragmentManager未初始化");
            return;
        }
        
        try {
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
            if (fragment.isAdded()) {
                transaction.show(fragment);
            } else {
                // 先移除已存在的相同类型Fragment
                String tag = fragment.getClass().getSimpleName();
                Fragment existingFragment = fragmentManager.findFragmentByTag(tag);
                if (existingFragment != null) {
                    transaction.remove(existingFragment);
                }
                transaction.add(R.id.fragment_container, fragment, tag);
            }
            
            transaction.commitAllowingStateLoss();
            
            // 确保Fragment容器可见
            View fragmentContainer = findViewById(R.id.fragment_container);
            if (fragmentContainer != null) {
                fragmentContainer.setVisibility(View.VISIBLE);
                fragmentContainer.bringToFront();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showMsg(this, "切换页面失败: " + e.getMessage());
        }
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
        try {
            // 如果runFragment为null，重新初始化
            if (runFragment == null) {
                runFragment = new RunFragment();
            }
            
            if (runFragment != null && fragmentManager != null) {
                switchFragment(runFragment);
                updateToolbarTitle("跑步");
                if (mBottomNavigation != null) {
                    mBottomNavigation.setSelectedItemId(R.id.navigation_run);
                }
            } else {
                // 如果Fragment初始化失败，直接启动RunActivity
                Intent intent = new Intent(MainActivity.this, RunActivity.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 如果切换Fragment失败，直接启动RunActivity
            try {
                Intent intent = new Intent(MainActivity.this, RunActivity.class);
                startActivity(intent);
            } catch (Exception ex) {
                ex.printStackTrace();
                ToastUtil.showMsg(this, "启动跑步界面失败");
            }
        }
    }

    /**
     * 设置AI FAB点击监听器
     */
    private void setupAIFABListener() {
        if (mFabAI != null) {
            mFabAI.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 只有在非拖拽状态下才响应点击
                    if (!mIsDragging) {
                        try {
                            // 跳转到AIActivity
                            Intent intent = new Intent(MainActivity.this, AIActivity.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.showMsg(MainActivity.this, "打开AI功能失败");
                        }
                    }
                }
            });
        }
    }
    
    /**
     * 设置FAB为可拖拽
     */
    private void setupDraggableFAB() {
        if (mFabAI == null || mCoordinatorLayout == null) {
            return;
        }
        
        // 恢复FAB位置
        restoreFabPosition();
        
        mFabAI.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mFabAI.getLayoutParams();
                
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 记录初始触摸位置和FAB位置
                        mTouchX = event.getRawX();
                        mTouchY = event.getRawY();
                        mFabX = params.leftMargin;
                        mFabY = params.topMargin;
                        mIsDragging = false;
                        return true;
                        
                    case MotionEvent.ACTION_MOVE:
                        // 计算移动距离
                        float dx = event.getRawX() - mTouchX;
                        float dy = event.getRawY() - mTouchY;
                        float distance = (float) Math.sqrt(dx * dx + dy * dy);
                        
                        // 如果移动距离超过阈值，认为是拖拽
                        if (distance > CLICK_THRESHOLD) {
                            mIsDragging = true;
                            
                            // 计算新位置
                            float newX = mFabX + dx;
                            float newY = mFabY + dy;
                            
                            // 获取屏幕尺寸
                            int screenWidth = mCoordinatorLayout.getWidth();
                            int screenHeight = mCoordinatorLayout.getHeight();
                            int fabWidth = mFabAI.getWidth();
                            int fabHeight = mFabAI.getHeight();
                            
                            // 限制FAB在屏幕范围内
                            newX = Math.max(0, Math.min(newX, screenWidth - fabWidth));
                            newY = Math.max(0, Math.min(newY, screenHeight - fabHeight));
                            
                            // 移除gravity，使用绝对定位
                            params.gravity = 0;
                            params.setMargins((int) newX, (int) newY, 0, 0);
                            mFabAI.setLayoutParams(params);
                        }
                        return true;
                        
                    case MotionEvent.ACTION_UP:
                        // 保存FAB位置
                        if (mIsDragging) {
                            saveFabPosition();
                        }
                        mIsDragging = false;
                        return false; // 让点击事件继续传递
                        
                    default:
                        return false;
                }
            }
        });
    }
    
    /**
     * 保存FAB位置
     */
    private void saveFabPosition() {
        if (mFabAI == null || mCoordinatorLayout == null) {
            return;
        }
        
        try {
            SharedPreferences prefs = getSharedPreferences("fab_position", MODE_PRIVATE);
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mFabAI.getLayoutParams();
            prefs.edit()
                    .putFloat("fab_x", params.leftMargin)
                    .putFloat("fab_y", params.topMargin)
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 恢复FAB位置
     */
    private void restoreFabPosition() {
        if (mFabAI == null || mCoordinatorLayout == null) {
            return;
        }
        
        try {
            SharedPreferences prefs = getSharedPreferences("fab_position", MODE_PRIVATE);
            float savedX = prefs.getFloat("fab_x", -1);
            float savedY = prefs.getFloat("fab_y", -1);
            
            if (savedX >= 0 && savedY >= 0) {
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mFabAI.getLayoutParams();
                int screenWidth = mCoordinatorLayout.getWidth();
                int screenHeight = mCoordinatorLayout.getHeight();
                
                // 如果屏幕尺寸还未确定，延迟恢复
                if (screenWidth == 0 || screenHeight == 0) {
                    mFabAI.post(new Runnable() {
                        @Override
                        public void run() {
                            restoreFabPosition();
                        }
                    });
                    return;
                }
                
                int fabWidth = mFabAI.getWidth();
                int fabHeight = mFabAI.getHeight();
                
                // 如果FAB尺寸还未确定，延迟恢复
                if (fabWidth == 0 || fabHeight == 0) {
                    mFabAI.post(new Runnable() {
                        @Override
                        public void run() {
                            restoreFabPosition();
                        }
                    });
                    return;
                }
                
                // 确保位置在屏幕范围内
                savedX = Math.max(0, Math.min(savedX, screenWidth - fabWidth));
                savedY = Math.max(0, Math.min(savedY, screenHeight - fabHeight));
                
                // 移除gravity，使用绝对定位
                params.gravity = 0;
                params.setMargins((int) savedX, (int) savedY, 0, 0);
                mFabAI.setLayoutParams(params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // 当窗口获得焦点时恢复FAB位置（此时可以获取正确的屏幕尺寸）
        if (hasFocus && mFabAI != null) {
            restoreFabPosition();
        }
    }
}