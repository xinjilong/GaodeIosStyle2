package com.xinyang.alienware.gaodeiosstyle;

import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.xinyang.alienware.gaodeiosstyle.adapter.PaginationFragmentPagerAdapter;
import com.xinyang.alienware.gaodeiosstyle.permissions.PermissionsActivity;
import com.xinyang.alienware.gaodeiosstyle.permissions.PermissionsChecker;
import com.xinyang.alienware.gaodeiosstyle.view.NoScrollViewPager;

import java.util.ArrayList;


/***
 *  主界面：1NoScrollViewPager 进行不同Style页面的区分 因为地图有手势滑动操作这里做了禁止滑动Viewpager处理
 *  2 Android 6.0 之后需要 动态获取定位等权限
 */

public class MainActivity extends AppCompatActivity {

    Resources resources;
    private NoScrollViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private ImageView ivBottomLine;
    private TextView iosStyle,defaultStyle;
    private int currIndex = 0 ;
    private int bottomLineWidth;
    private int offset = 0;
    private int position_one;
    public final static int num = 2 ;
    Fragment tab1;
    Fragment tab2;
    //定位需要的动态权限
    private static final int REQUEST_CODE = 0 ;
    private String[] strPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE} ;
    private PermissionsChecker checker ; //权限检测器
    //高德地图定位
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checker = new PermissionsChecker(this) ;//检测是否已经获取权限
        resources = getResources();
        InitWidth();
        InitTextView();
        InitViewPager();
        initLocationPermission();
        TranslateAnimation animation = new TranslateAnimation(position_one, offset, 0, 0);
        animation.setFillAfter(true);
        animation.setDuration(300);
        ivBottomLine.startAnimation(animation);
    }

    /***
     * 获取滑动线动画屏幕设置,此处已经在XML文件中进行了隐藏可以手动打开
     * @param
     */
    private void InitWidth() {
        ivBottomLine = findViewById(R.id.iv_bottom_line);
        bottomLineWidth = ivBottomLine.getLayoutParams().width;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset =  ((screenW / num - bottomLineWidth) / 2); //屏幕三等分
        int avg =  (screenW / num);
        position_one = avg + offset;

    }
    private void InitTextView() {
        iosStyle = findViewById(R.id.tv_tab_1);
        defaultStyle =  findViewById(R.id.tv_tab_2);
        iosStyle.setOnClickListener(new MyOnClickListener(0));
        defaultStyle.setOnClickListener(new MyOnClickListener(1));

    }

    private void InitViewPager() {
        mPager =  findViewById(R.id.vPager);
        fragmentsList = new ArrayList<Fragment>();

        tab1 = new LocationFragment();
        tab2 = new DefaultLocationFragment();

        fragmentsList.add(tab1);
        fragmentsList.add(tab2);

        mPager.setAdapter(new PaginationFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
        mPager.setOnPageChangeListener(new MapOnPageChangeListener());
        mPager.setCurrentItem(0);
        mPager.setOffscreenPageLimit(0);
    }


    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }
    };

    /***
     * viewpager 滑动监听
     */
    public class MapOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {

            Animation animation = null;
            switch (arg0) {

                case 0:
                    if (currIndex == 1){
                        animation = new TranslateAnimation(position_one, offset, 0, 0);
                        defaultStyle.setTextColor(resources.getColor(R.color.bbb_color));

                    }

                    iosStyle.setTextColor(resources.getColor(R.color.tab_Indicator_color));
                    break;
                case 1:

                    //判断是否是又最后一页返回0代表从前到后滑动，反之从后向前
                    if (currIndex == 0){
                        animation = new TranslateAnimation(offset, position_one, 0, 0);
                        iosStyle.setTextColor(resources.getColor(R.color.bbb_color));

                    }

                    defaultStyle.setTextColor(resources.getColor(R.color.tab_Indicator_color));
                    break;

            }
            currIndex = arg0;
            animation.setFillAfter(true);
            animation.setDuration(300);
            ivBottomLine.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    }

    /***
     * 动态设置地图地位相关权限6.0适配
     *
     */

    private void initLocationPermission() {
        if(Build.VERSION.SDK_INT>=23){
            if(checker.lacksPermissions(strPermissions)){
                permissionActivity();
            }
        }
    }


    private void permissionActivity(){
        PermissionsActivity.startActivityForResult(this,REQUEST_CODE,strPermissions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         //动态添加权限回传
         if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED){
            finish();
        }

    }


}
