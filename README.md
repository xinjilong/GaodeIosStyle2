# GaodeIosStyle2
高德地图 实现 定位 逆向地理编码返回周边信息，POI关键字搜索功能，仿苹果地图的UI 以及6.0以上的动态获取权限


1 清单文件中需要配置的key
        <!--高德地图需要的key,根据官方API将自己申请的key进行替换 value-->
        <meta-data
            android:name="com.amap.api.v2.apikey"
            android:value="9d8d5b93db86edce55ccb4432fb0f464"/>
        <!--定位需要服务-->
        <service android:name="com.amap.api.location.APSService"></service>
        
      2 声明的Activity  windowSoftInputMode 属性防止软键盘将底部顶上来
            <activity android:name=".MainActivity"
            android:screenOrientation="portrait"
            android:windowSoftInputMode="adjustPan">
            
      3 拷贝项目中的 jar包 以及main目录下的 jniLibs (sow文件)否则无法加载地图
      
      4 MainActivity中进行动态权限获取
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
    
      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checker = new PermissionsChecker(this) ;//检测是否已经获取权限
        initLocationPermission();
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
    
    
    LocationFragment中进行定位 poi搜索 逆向地理编码返回周边搜索 功能 主要为底部仿苹果地图的的可伸缩的抽屉效果
    
    //底部抽屉栏展示地址
        bottomSheet = parentView.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
          behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, @BottomSheetBehavior.State int newState) {
                String state = "null";
                switch (newState) {
                    case 1:
                        state = "STATE_DRAGGING";//过渡状态此时用户正在向上或者向下拖动bottom sheet
                        KeyBoardUtils.closeKeyBoard(searchead,getActivity());
                        break;
                    case 2:
                        state = "STATE_SETTLING"; // 视图从脱离手指自由滑动到最终停下的这一小段时间
                        break;
                    case 3:
                        state = "STATE_EXPANDED"; //处于完全展开的状态

                        break;
                    case 4:
                        state = "STATE_COLLAPSED"; //默认的折叠状态
                        KeyBoardUtils.closeKeyBoard(searchead,getActivity());
                        break;
                    case 5:
                        state = "STATE_HIDDEN"; //下滑动完全隐藏 bottom sheet
                        KeyBoardUtils.closeKeyBoard(searchead,getActivity());
                        break;
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                Log.d("BottomSheetDemo", "slideOffset:" + slideOffset);
            }
        });




