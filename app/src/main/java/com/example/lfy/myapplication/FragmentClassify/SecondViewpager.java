package com.example.lfy.myapplication.FragmentClassify;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.GridPhoto;
import com.example.lfy.myapplication.Bean.OrderBy;
import com.example.lfy.myapplication.Bean.SecondMenuBean;
import com.example.lfy.myapplication.FragmentCar.Shop_Car;
import com.example.lfy.myapplication.FragmentHome.search.Search_edit;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.BadgeView;
import com.example.lfy.myapplication.Variables;
import com.example.lfy.myapplication.user_login.LoginBg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lfy on 2016/6/6.
 */
public class SecondViewpager extends AppCompatActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    VpSimpleFragment fragment;
    TextView classify_title;
    ImageView shop_car;

    public static FrameLayout classify_fragment;
    //一级分类
    GridPhoto gridPhoto;
    List<GridPhoto> all_gridPhoto;

    TextView orderby1;
    TextView orderby2;
    TextView orderby3;
    private Spinner spinner;

    ImageView secondViewpager;
    ImageView second_return;
    View home_car_red;
    public static BadgeView bv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Variables.setTranslucentStatus(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classfiy_second);
        if (savedInstanceState != null) {
            gridPhoto = (GridPhoto) savedInstanceState.getSerializable("one_classify");
            all_gridPhoto = (List<GridPhoto>) savedInstanceState.getSerializable("all_classify");
        } else {
            Intent intent = getIntent();
            gridPhoto = (GridPhoto) intent.getSerializableExtra("one_classify");
            all_gridPhoto = (List<GridPhoto>) intent.getSerializableExtra("all_classify");

        }

        initView();
        initmPopupWindowView(all_gridPhoto);

        OnRefresh(gridPhoto);
        setBadgeView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bv.setBadgeCount(Variables.count);
    }

    private void setBadgeView() {

        bv = new BadgeView(this);
        bv.setTargetView(home_car_red);
        bv.setTextColor(Color.WHITE);
        bv.setGravity(Gravity.TOP | Gravity.RIGHT);
        bv.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC));
    }

    private void OnRefresh(GridPhoto typename) {

        OrderBy_xUtils();//请求排序方式
        SecondMenu_xUtils(typename.getType1());//请求二级分类
        classify_title.setText(typename.getTypeName1());
    }

    private void initData(final List<SecondMenuBean> orderby) {

        FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return orderby.size();
            }

            @Override
            public Fragment getItem(int position) {
                VpSimpleFragment fragment = VpSimpleFragment.newInstance(orderby.get(position).getType2(), gridPhoto.getShowType());
                return fragment;
            }

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                fragment = (VpSimpleFragment) object;
                super.setPrimaryItem(container, position, object);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return orderby.get(position % orderby.size()).getTypename2();
            }
        };
        mViewPager.setAdapter(mAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        if (orderby.size() < 5) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void initView() {

        classify_title = (TextView) findViewById(R.id.classify_title);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        shop_car = (ImageView) findViewById(R.id.shop_car);
        classify_fragment = (FrameLayout) findViewById(R.id.classify_fragment);
        home_car_red = findViewById(R.id.home_car_red);

        secondViewpager = (ImageView) findViewById(R.id.secondViewpager);
        second_return = (ImageView) findViewById(R.id.second_return);
        orderby1 = (TextView) findViewById(R.id.orderby1);
        orderby2 = (TextView) findViewById(R.id.orderby2);
        orderby3 = (TextView) findViewById(R.id.orderby3);
        spinner = (Spinner) findViewById(R.id.orderby4);
        second_return.setOnClickListener(this);
        secondViewpager.setOnClickListener(this);
        shop_car.setOnClickListener(this);
        orderby1.setOnClickListener(this);
        orderby2.setOnClickListener(this);
        orderby3.setOnClickListener(this);
    }


    private void SecondMenu_xUtils(String type) {
        RequestParams params = new RequestParams(Variables.second_type);
        params.addBodyParameter("type1", type);
        params.setCacheMaxAge(1000 * 60);
        x.http().get(params, new Callback.CacheCallback<String>() {
            private boolean hasError = false;
            private String result = null;

            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
            }

            @Override
            public void onSuccess(String result) {
                // 注意: 如果服务返回304 或 onCache 选择了信任缓存, 这时result为null.
                if (result != null) {
                    this.result = result;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    // ...
                } else { // 其他错误
                    // ...
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    // 成功获取数据
                    Type_JSON(result);
                }
            }
        });

    }

    private void Type_JSON(String json) {
        try {
            JSONObject object = new JSONObject(json);
            String Ret = object.getString("Ret");
            if (Ret.equals("1")) {
                JSONArray data = object.getJSONArray("Data");
                List<SecondMenuBean> Orderby = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject everyone = data.getJSONObject(i);
                    SecondMenuBean all = new SecondMenuBean();
                    all.setType2(everyone.getString("Type2"));
                    all.setTypename2(everyone.getString("Typename2"));
                    Orderby.add(all);
                }

                initData(Orderby);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void OrderBy_xUtils() {
        RequestParams params = new RequestParams(Variables.second_orderby);
        params.setCacheMaxAge(1000 * 60);
        x.http().get(params, new Callback.CacheCallback<String>() {
            private boolean hasError = false;
            private String result = null;

            @Override
            public boolean onCache(String result) {
                this.result = result;
                return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
            }

            @Override
            public void onSuccess(String result) {
                // 注意: 如果服务返回304 或 onCache 选择了信任缓存, 这时result为null.
                if (result != null) {
                    this.result = result;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
                Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    // ...
                } else { // 其他错误
                    // ...
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    // 成功获取数据
                    OrderBy_JSON(result);
                }
            }
        });

    }

    private void OrderBy_JSON(String json) {
        try {
            JSONObject object = new JSONObject(json);
            String Ret = object.getString("Ret");
            if (Ret.equals("1")) {
                JSONArray data = object.getJSONArray("Data");
                List<OrderBy> Orderby = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject everyone = data.getJSONObject(i);
                    OrderBy all = new OrderBy();
                    all.setOrderByName(everyone.getString("OrderByName"));
                    all.setOrderByNum(everyone.getString("OrderByNum"));
                    all.setId(everyone.getString("id"));
                    Orderby.add(all);
                }
                orderby1.setText(Orderby.get(0).getOrderByName());
                orderby2.setText(Orderby.get(1).getOrderByName());
                orderby3.setText(Orderby.get(2).getOrderByName());

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.secondViewpager:
                Intent intent1 = new Intent(SecondViewpager.this, Search_edit.class);
                startActivity(intent1);
                break;
            case R.id.second_return:
                finish();
                break;
            case R.id.orderby1:
                fragment.setSort("1", orderby1);
                break;
            case R.id.orderby2:
                fragment.setSort("2", orderby2);
                break;
            case R.id.orderby3:
                fragment.setSort("3", orderby3);
                break;
            case R.id.shop_car:
                if (Variables.my != null) {
                    Intent intent = new Intent(SecondViewpager.this, Shop_Car.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SecondViewpager.this, LoginBg.class);
                    startActivity(intent);
                }
                break;
        }
    }

    boolean spinner_flag = false;

    public void initmPopupWindowView(final List<GridPhoto> type) {
        String[] str = new String[type.size() + 1];
        for (int a = 0; a < type.size() + 1; a++) {
            if (a == type.size()) {
                str[a] = "切换分类";
            } else {
                str[a] = type.get(a).getTypeName1();
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, str);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

        spinner.setSelection(type.size());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner_flag) {
                    if (position == type.size()) {
                    } else {
                        Intent intent = new Intent(SecondViewpager.this, SecondViewpager.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("one_classify", all_gridPhoto.get(position));
                        bundle.putSerializable("all_classify", (Serializable) all_gridPhoto);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    spinner_flag = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}
