package com.example.lfy.myapplication.splash;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.MineBean;
import com.example.lfy.myapplication.MainActivity;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.Send;
import com.example.lfy.myapplication.Util.SystemStatusManager;
import com.example.lfy.myapplication.Util.UserInfo;
import com.example.lfy.myapplication.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

/**
 * @description
 */
public class SplashActivity extends AppCompatActivity {

    private ImageView mSplashItem_iv = null;
    UserInfo userInfo = new UserInfo(this);
    private ViewPager myViewPager; //页卡内容
    private List<View> list; // 存放页卡
    private List<RadioButton> select; // 存放导航点
    private Button startButton; //按钮，“开始体验”
    //    private CircleImageView splash_logo;
    private ImageView open_logo;
    boolean wifi = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus();
        wifi = isNetworkAvailable(this);

        if (!userInfo.getBooleanInfo("first")) {
            setContentView(R.layout.splash_viewpager);
            initViewPager();
        } else {
            setContentView(R.layout.splash_open);
            mSplashItem_iv = (ImageView) findViewById(R.id.splash_loading_item);
//            splash_logo = (CircleImageView) findViewById(R.id.splash_logo);
            open_logo = (ImageView) findViewById(R.id.open_logo);
            initView();
            Login_xUtils();
        }
    }

    public boolean isNetworkAvailable(Context context) {
        boolean flag = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            //如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            //去进行判断网络是否连接
            if (cm.getActiveNetworkInfo() != null) {
                flag = cm.getActiveNetworkInfo().isAvailable();
            }
            return flag;
        }
        return false;
    }

    protected void initView() {
        Animation translate = AnimationUtils.loadAnimation(this, R.anim.splash_loading);
        translate.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        //execute the task
                        if (wifi) {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class); // 调用父类的intent方法
                            startActivity(intent);
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            finish(); //
                        } else {
                            Intent intent = new Intent(SplashActivity.this, Wifi.class);
                            intent.putExtra("from", "second");
                            startActivity(intent);
                            finish();
                        }
                    }
                }, 800);

            }
        });
        mSplashItem_iv.setAnimation(translate);
    }



    private void initViewPager() {
        myViewPager = (ViewPager) this.findViewById(R.id.viewPager);
        FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public Fragment getItem(int position) {
                SplashFragment fragment = SplashFragment.newInstance(position);
                return fragment;
            }

        };
        myViewPager.setAdapter(mAdapter);
    }


    private void Login_xUtils() {
        UserInfo userInfo = new UserInfo(getApplication());

        String my_name = userInfo.getStringInfo("user_name");
        String my_password = userInfo.getStringInfo("password");

        //获取设备信息
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String DEVICE_ID = tm.getDeviceId();
        Variables.DEVICE_ID = DEVICE_ID;
        if (!my_name.equals("") && !my_password.equals("")) {
            my_password = Send.getMD5(my_password);
            my_password = Send.getMD5(my_password);
            RequestParams params = new RequestParams(Variables.http_login);
            params.addBodyParameter("username", my_name);
            params.addBodyParameter("pwd", my_password);
            params.addBodyParameter("devicetoken", DEVICE_ID);

            params.setCacheMaxAge(10000 * 60);
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
                        Login_JSON(result);
                    }
                }
            });
        }
    }

    public void Login_JSON(String str) {
        try {
            JSONObject json = new JSONObject(str);
            String Ret = json.getString("Ret");
            if (Ret.equals("1")) {
                MineBean communication = new MineBean();
                JSONArray data = json.getJSONArray("Data");
                JSONObject Customer = data.getJSONObject(0);
                communication.setCustomerID(Customer.getString("CustomerID"));
                communication.setUserName(Customer.getString("UserName"));
                communication.setPassword(Customer.getString("Password"));
                communication.setCustomerName(Customer.getString("CustomerName"));
                communication.setSex(Customer.getString("Sex"));
                communication.setBirthday(Customer.getString("Birthday"));
                communication.setPhoneNameber(Customer.getString("PhoneNameber"));
                communication.setEmailAddress(Customer.getString("EmailAddress"));
                communication.setIsChecked(Customer.getString("IsChecked"));
                communication.setCheckedType(Customer.getString("CheckedType"));
                communication.setAddressP(Customer.getString("AddressP"));
                communication.setAddressC(Customer.getString("AddressC"));
                communication.setAddressD(Customer.getString("AddressD"));
                communication.setAddressSQ(Customer.getString("AddressSQ"));
                communication.setAddressZ(Customer.getString("AddressZ"));
                communication.setAddressDY(Customer.getString("AddressDY"));
                communication.setAddressS(Customer.getString("AddressS"));
                communication.setRecommendNo(Customer.getString("RecommendNo"));
                communication.setRecommendName(Customer.getString("RecommendName"));
                communication.setCustomerLevel(Customer.getString("CustomerLevel"));
                communication.setCustomerIntegral(Customer.getString("CustomerIntegral"));
                //communication.setMyCoupon(Customer.getString("MyCoupon"));
                communication.setAccountState(Customer.getString("AccountState"));
                communication.setCreateTime(Customer.getString("CreateTime"));
                communication.setLastLoginTime(Customer.getString("LastLoginTime"));
                communication.setImage(Customer.getString("image"));
                communication.setAuthority(Customer.getString("authority"));
                communication.setPoint(Customer.getString("point"));
                communication.setPartner(Customer.getString("partner"));
                communication.setWeixinNo(Customer.getString("weixinNo"));
                communication.setPartnerName(Customer.getString("partnerName"));
                Variables.my = communication;
                Log.d("登陆了", "哪里来的猴子==" + Variables.my.getCustomerID() + "==看招");

                //头像
//                try {
//                    String liu = communication.getImage();
//                    liu = liu.substring(liu.indexOf(",") + 1);
//                    Bitmap bitmap = Variables.base64ToBitmap(liu);
//                    splash_logo.setImageBitmap(bitmap);
//                } catch (Exception e) {
//                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // 需要setContentView之前调用
    private void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemStatusManager tintManager = new SystemStatusManager(this);
            tintManager.setStatusBarTintEnabled(true);
            // 设置状态栏的颜色
            tintManager.setStatusBarTintResource(R.color.kong);
            getWindow().getDecorView().setFitsSystemWindows(true);
        }
    }
}