package com.xinyang.alienware.gaodeiosstyle;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.RegeocodeRoad;
import com.amap.api.services.geocoder.StreetNumber;

import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import com.xinyang.alienware.gaodeiosstyle.adapter.ChoutiRecyclerViewAdapter;
import com.xinyang.alienware.gaodeiosstyle.adapter.PoiKeywordSearchAdapter;
import com.xinyang.alienware.gaodeiosstyle.bean.PoiAddressBean;
import com.xinyang.alienware.gaodeiosstyle.utils.KeyBoardUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alienware on 2017/12/7.
 */

public class LocationFragment extends Fragment implements LocationSource, AMapLocationListener, PoiSearch.OnPoiSearchListener{
    private TextView mHint;
    private MapView mapView ;
    private View bottomSheet;
    private BottomSheetBehavior<View> behavior;
    private EditText searchead;
    private List<PoiItem> pois;
    private RecyclerView recyclerView;
    private ChoutiRecyclerViewAdapter adapter;
    //定位
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private LatLng point;
    private Marker marker ;
    private AMap aMap;
    //poi搜索
    private String keyWord = "";// 要输入的poi搜索关键字
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch       poiSearch;// POI搜索



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, null);
        initView(view,savedInstanceState);
        return view;
    }


    private void initView(View parentView, @Nullable Bundle savedInstanceState) {
        mapView = parentView.findViewById(R.id.mapview);
        mHint = parentView.findViewById(R.id.tv_tishi);
        mHint.setTextColor(getResources().getColor(R.color.tab_Indicator_color));
        recyclerView = parentView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        initLocation();
        //底部抽屉栏展示地址
        bottomSheet = parentView.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        searchead = parentView.findViewById(R.id.edit_search_maphead);
        searchead.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                keyWord = String.valueOf(charSequence);
                mHint.setText("搜索结果");
                mHint.setTextColor(getResources().getColor(R.color.bbb_color));
                if ("".equals(keyWord)) {
                    //  Toast.makeText(getActivity(),"请输入搜索关键字",Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    doSearchQuery();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


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


        /***
         * 当底部抽屉栏全部打开时才给Edittext获取焦点
         */
        searchead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                if (behavior.getState() == 3){
                    searchead.setFocusable(true);
                    searchead.setFocusableInTouchMode(true);
                    searchead.requestFocus();
                    KeyBoardUtils.openKeyBorad(searchead,getActivity());

                }else {
                    searchead.setFocusable(false);

                }
            }
        });

        if (pois!=null){
            adapter = new ChoutiRecyclerViewAdapter(getActivity(),pois);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

        }


    }


    /***
     * POI搜索变
     * @param
     */
    private void doSearchQuery() {
        currentPage = 0;
        //不输入城市名称有些地方搜索不到
        query = new PoiSearch.Query(keyWord, "", "北京");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        //这里没有做分页加载了,默认给50条数据
        query.setPageSize(50);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(getActivity(), query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();


    }
    /**
     * POI信息查询回调方法
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {

        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {  // 搜索poi的结果
                if (result.getQuery().equals(query)) {  // 是否是同一条
                    poiResult = result;
                    final ArrayList<PoiAddressBean> data = new ArrayList<PoiAddressBean>();//自己创建的数据集合
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    for(PoiItem item : poiItems){
                        //获取经纬度对象
                        LatLonPoint llp = item.getLatLonPoint();
                        double lon = llp.getLongitude();
                        double lat = llp.getLatitude();

                        String title = item.getTitle();
                        String text = item.getSnippet();
                        String provinceName = item.getProvinceName();
                        String cityName = item.getCityName();
                        String adName = item.getAdName();
                        data.add(new PoiAddressBean(String.valueOf(lon), String.valueOf(lat), title, text,provinceName,
                                cityName,adName));
                    }

                    PoiKeywordSearchAdapter poiadapter = new PoiKeywordSearchAdapter(getActivity(),data);
                    recyclerView.setAdapter(poiadapter);
                    poiadapter.setItemClickListener(new PoiKeywordSearchAdapter.PoiItemClickListener() {
                        @Override
                        public void onItemClick(View view, int pos) {
                            Double poiLatitude = Double.valueOf(data.get(pos).getLatitude()).doubleValue();
                            Double poiLongtude = Double.valueOf(data.get(pos).getLongitude()).doubleValue();

                            //通过经纬度重新再地图获取位置
                            CameraPosition  cp = aMap.getCameraPosition();
                            CameraPosition cpNew = CameraPosition.fromLatLngZoom(new LatLng(poiLatitude,poiLongtude),cp.zoom);
                            CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cpNew);
                            aMap.moveCamera(cu);
                            behavior.setState(4); //设置底部bottom sheet为半隐藏
                            searchead.setText(null);//清空Edittext内容
                            mHint.setText("附近热点");
                            mHint.setTextColor(getResources().getColor(R.color.tab_Indicator_color));
                            Log.d("ceshi","postion"+pos+"lat"+data.get(pos).getLatitude()+"long:"+data.get(pos).getLongitude());

                        }
                    });
                }
            } else {
                Toast.makeText(getActivity(),"no_result",Toast.LENGTH_SHORT).show();

            }
        } else {
            Toast.makeText(getActivity(),""+rCode,Toast.LENGTH_SHORT).show();

        }

    }
    /**
     * POI信息查询回调方法
     */
    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }


    /***
     * 初始化定位
     */
    private void initLocation() {
        if (aMap != null){
            MyLocationStyle locationStyle = new MyLocationStyle();
           // locationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.em_unread_count_bg));
            locationStyle.strokeColor(Color.BLACK);
            locationStyle.radiusFillColor(Color.argb(100,0,0,180));
            locationStyle.strokeWidth(1.0f);
            aMap.setMyLocationStyle( locationStyle);
            aMap.setLocationSource(this);
            aMap.getUiSettings().setMyLocationButtonEnabled(true);
            aMap.setMyLocationEnabled(true);
            //监听地图发生变化之后
            aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {

                    //发生变化时获取到经纬度传递逆地理编码获取周边数据
                    regeocodeSearch(cameraPosition.target.latitude,cameraPosition.target.longitude,2000);
                    point = new LatLng(cameraPosition.target.latitude,cameraPosition.target.longitude);
                    marker.remove();//将上一次描绘的mark清除

                }

                @Override
                public void onCameraChangeFinish(CameraPosition cameraPosition) {
                    //地图发生变化之后描绘mark
                    marker = aMap.addMarker(new com.amap.api.maps.model.MarkerOptions()
                            .position(point)
                            .title("")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.em_unread_count_bg)));
                }
            });
        }

    }




    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

    }


    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null){
            mlocationClient = new AMapLocationClient(getActivity().getApplicationContext());
            mLocationOption = new AMapLocationClientOption();
            mlocationClient.setLocationListener(this);
            mLocationOption.setOnceLocation(true); //只定位一次
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();

        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient !=null){
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
        mLocationOption = null;

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation!=null && mListener != null){
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0){
                mListener.onLocationChanged(aMapLocation);
                Double weidu = aMapLocation.getLatitude();
                Double jingdu = aMapLocation.getLongitude();
                regeocodeSearch(weidu, jingdu, 2000);


            }else {
                String errorText = "faild to located"+aMapLocation.getErrorCode()+":"+aMapLocation.getErrorInfo();
                Log.d("ceshi",errorText);
            }
        }

    }


    /***
     * 逆地理编码获取定位后的附近地址
     * @param weidu
     * @param jingdu
     * @param distances 设置查找范围
     */
    private void regeocodeSearch(Double weidu, Double jingdu, int distances ) {
        LatLonPoint point = new LatLonPoint(weidu,jingdu);
        GeocodeSearch geocodeSearch = new GeocodeSearch(getActivity());
        RegeocodeQuery regeocodeQuery = new RegeocodeQuery(point,distances,geocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(regeocodeQuery);

        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
                String preAdd = "";//地址前缀
                if (1000 == rCode) {
                    final RegeocodeAddress address = regeocodeResult.getRegeocodeAddress();
                    StringBuffer stringBuffer = new StringBuffer();
                    String area = address.getProvince();//省或直辖市
                    String loc = address.getCity();//地级市或直辖市
                    String subLoc = address.getDistrict();//区或县或县级市
                    String ts = address.getTownship();//乡镇
                    String thf = null;//道路
                    List<RegeocodeRoad> regeocodeRoads = address.getRoads();//道路列表
                    if (regeocodeRoads != null && regeocodeRoads.size() > 0) {
                        RegeocodeRoad regeocodeRoad = regeocodeRoads.get(0);
                        if (regeocodeRoad != null) {
                            thf = regeocodeRoad.getName();
                        }
                    }
                    String subthf = null;//门牌号
                    StreetNumber streetNumber = address.getStreetNumber();
                    if (streetNumber != null) {
                        subthf = streetNumber.getNumber();
                    }
                    String fn = address.getBuilding();//标志性建筑,当道路为null时显示
                    if (area != null) {
                        stringBuffer.append(area);
                        preAdd += area;
                    }
                    if (loc != null && !area.equals(loc)) {
                        stringBuffer.append(loc);
                        preAdd += loc;
                    }
                    if (subLoc != null) {
                        stringBuffer.append(subLoc);
                        preAdd += subLoc;
                    }
                    if (ts != null)
                        stringBuffer.append(ts);
                    if (thf != null)
                        stringBuffer.append(thf);
                    if (subthf != null)
                        stringBuffer.append(subthf);
                    if ((thf == null && subthf == null) && fn != null && !subLoc.equals(fn))
                        stringBuffer.append(fn + "附近");
                    //  String ps = "poi";

                    pois = address.getPois();//获取周围兴趣点

                    adapter = new ChoutiRecyclerViewAdapter(getActivity(),pois);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    adapter.setItemClickListener(new ChoutiRecyclerViewAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int pos) {
                            Toast.makeText(getActivity(), "postion:"+pos+"地址：" +pois.get(pos).getTitle(), Toast.LENGTH_LONG).show();

                        }

                    });

                }


            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });

    }



}
