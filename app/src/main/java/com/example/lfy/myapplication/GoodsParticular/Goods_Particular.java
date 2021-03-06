package com.example.lfy.myapplication.GoodsParticular;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lfy.myapplication.Bean.GoodsBean;
import com.example.lfy.myapplication.FragmentCar.Shop_Car;
import com.example.lfy.myapplication.R;
import com.example.lfy.myapplication.Util.BadgeView;
import com.example.lfy.myapplication.Variables;
import com.example.lfy.myapplication.user_login.Login;
import com.example.lfy.myapplication.user_login.LoginBg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.UnsupportedEncodingException;

/**
 * Created by lfy on 2016/7/7.
 */
public class Goods_Particular extends AppCompatActivity implements View.OnClickListener {

    ImageView return_all;
    ImageView car;
    ImageView image;
    TextView goods_title;
    TextView goods_content;
    TextView Standard;
    TextView goods_price;
    TextView goods_vip;
    ImageView more_image;
    WebView scroll_webview;

    ImageView add;
    ImageView remove;
    TextView count;
    TextView add_car;
    TextView intent_car;
    CheckBox mine_favorite;
    int a = 1;
    String productId;

    public static BadgeView bv;
    View home_car_red;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Variables.setTranslucentStatus(this);
        setContentView(R.layout.goods_particular);

        Intent intent = getIntent();
        productId = intent.getStringExtra("productId");
        initView();
        GetGoods();
        SetLiner();
        setBadgeView();
        if (Variables.my != null) {
            favorite_xUtils();
        }
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

    private void initView() {
        return_all = (ImageView) findViewById(R.id.return_but);
        car = (ImageView) findViewById(R.id.car);
        image = (ImageView) findViewById(R.id.image);
        goods_title = (TextView) findViewById(R.id.goods_title);
        goods_content = (TextView) findViewById(R.id.goods_content);
        Standard = (TextView) findViewById(R.id.Standard);
        goods_price = (TextView) findViewById(R.id.goods_price);
        goods_vip = (TextView) findViewById(R.id.goods_vip);
        more_image = (ImageView) findViewById(R.id.more_image);
        scroll_webview = (WebView) findViewById(R.id.scroll_webview);
        mine_favorite = (CheckBox) findViewById(R.id.classify_favorite);
        home_car_red = findViewById(R.id.home_car_red);

        add = (ImageView) findViewById(R.id.car_item_add);
        remove = (ImageView) findViewById(R.id.car_item_remove);
        count = (TextView) findViewById(R.id.car_item_count);
        add_car = (TextView) findViewById(R.id.add_car);
        intent_car = (TextView) findViewById(R.id.intent_car);


        count.setText(a + "");

        return_all.setOnClickListener(this);
        add.setOnClickListener(this);
        remove.setOnClickListener(this);
        intent_car.setOnClickListener(this);
        car.setOnClickListener(this);
        mine_favorite.setOnClickListener(this);

        add_car.setOnClickListener(this);

        if (Variables.point.getState().equals("1")) {
            add_car.setEnabled(true);
            add_car.setBackgroundResource(R.color.green);
            intent_car.setEnabled(true);
            intent_car.setBackgroundResource(R.color.actionsheet_red);
        } else {
            add_car.setEnabled(false);
            add_car.setBackgroundResource(R.color.line_grey);
            intent_car.setEnabled(false);
            intent_car.setBackgroundResource(R.color.huiseziti);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_but:
                finish();
                break;
            case R.id.car_item_add:
                a = a + 1;
                count.setText(a + "");
                break;
            case R.id.car_item_remove:
                if (a > 1) {
                    a = a - 1;
                } else {
                    a = 1;
                }
                count.setText(a + "");
                break;
            case R.id.intent_car:
                if (Variables.point.getState().equals("1")) {
                    if (Variables.my != null) {
                        Insert_xUtils(productId, "into");
                    } else {
                        login();
                    }
                } else {
                    intent_car.setEnabled(false);
                    intent_car.setBackgroundResource(R.color.huiseziti);
                }
                break;
            case R.id.add_car:
                if (Variables.point.getState().equals("1")) {
                    add_car.setEnabled(true);
                    add_car.setBackgroundResource(R.color.green);
                    if (Variables.my != null) {
                        Insert_xUtils(productId, "add_car");
                    } else {
                        login();
                    }
                } else {
                    add_car.setEnabled(false);
                    add_car.setBackgroundResource(R.color.line_grey);
                }
                break;
            case R.id.car:
                if (Variables.my != null) {
                    Intent intent = new Intent(Goods_Particular.this, Shop_Car.class);
                    startActivity(intent);
                } else {
                    login();
                }
                break;
            case R.id.classify_favorite:
                if (Variables.my == null) {
                    mine_favorite.setChecked(false);
                    login();
                } else {
                    if (mine_favorite.isChecked()) {
                        my_favorite(Variables.http_collection, "add");
                    } else {
                        my_favorite(Variables.http_uncollection, "remove");
                    }
                }
                break;
        }
    }

    private void login() {
        Intent intent = new Intent(Goods_Particular.this, LoginBg.class);
        startActivity(intent);
    }

    private void GetGoods() {
        RequestParams params = new RequestParams(Variables.goods_one);
        params.addBodyParameter("productid", productId);
        x.http().post(params, new Callback.CacheCallback<String>() {
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
                Toast.makeText(x.app(), "请检查网络", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    // 成功获取数据
                    GetGoodsJSON(result);
                }
            }
        });

    }

    private void GetGoodsJSON(String str) {
        try {
            JSONObject json = new JSONObject(str);
            String msg = json.getString("Ret");
            if (msg.equals("1")) {
                JSONArray data = json.getJSONArray("Data");
                JSONObject Customer = data.getJSONObject(0);
                GoodsBean goodsBean = new GoodsBean();

                String url = Customer.getString("Image1");
                url = "http://www.baifenxian.com/" + java.net.URLEncoder.encode(url, "UTF-8");
                goodsBean.setImage1(url);
                goodsBean.setTitle(Customer.getString("Title"));
                goodsBean.setPlace(Customer.getString("Place"));
                goodsBean.setStandard(Customer.getString("Standard"));
                goodsBean.setPrice(Customer.getString("Price"));
                goodsBean.setPromotionPrice(Customer.getString("PromotionPrice"));
                goodsBean.setContent(Customer.getString("Content"));
                SetView(goodsBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void SetLiner() {

        LinearLayout.LayoutParams topimage;
        topimage = (LinearLayout.LayoutParams) image.getLayoutParams();
        topimage.height = Variables.PhoneWidth;
        topimage.width = Variables.PhoneWidth;
        image.setLayoutParams(topimage);


        LinearLayout.LayoutParams botimage;
        botimage = (LinearLayout.LayoutParams) more_image.getLayoutParams();
        botimage.height = Variables.PhoneWidth * 5 / 7;
        botimage.width = Variables.PhoneWidth;
        more_image.setLayoutParams(botimage);
    }

    private void SetView(GoodsBean goodsBean) {

        ImageOptions imageOptions = new ImageOptions.Builder()
                .setIgnoreGif(false)//是否忽略gif图。false表示不忽略。不写这句，默认是true
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setFailureDrawableId(R.mipmap.all_longding)
                .setLoadingDrawableId(R.mipmap.all_longding)
                .build();
        x.image().bind(image, goodsBean.getImage1(), imageOptions);
        x.image().bind(more_image, "http://www.baifenxian.com/images/peisong.png", imageOptions);

        goods_title.setText(goodsBean.getTitle());
        goods_content.setText(goodsBean.getPlace());
        Standard.setText(goodsBean.getStandard());
        goods_price.setText("￥" + goodsBean.getPrice());
        goods_vip.setText("会员:￥" + goodsBean.getPromotionPrice());
        initWebView(goodsBean.getContent());
    }

    private void initWebView(String content) {

        //WebView加载web资源
        scroll_webview.loadUrl(content);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        scroll_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
    }


    private void Insert_xUtils(String productId, final String type) {

        RequestParams params = new RequestParams(Variables.http_InsertCar);
        params.addBodyParameter("CustomerID", Variables.my.getCustomerID());
        params.addBodyParameter("ProductID", productId);
        params.addBodyParameter("point", Variables.point.getID());
        params.addBodyParameter("type", "1");
        params.addBodyParameter("num", a + "");

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
                    Toast.makeText(Goods_Particular.this, "加入成功", Toast.LENGTH_SHORT).show();
                    Variables.count = Variables.count + a;
                    bv.setBadgeCount(Variables.count);
                    if (type.equals("into")){
                        Intent intent = new Intent(Goods_Particular.this, Shop_Car.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(Goods_Particular.this, "加入失败，请重新尝试", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void my_favorite(String http, final String str) {

        RequestParams params = new RequestParams(http);
        params.addBodyParameter("userid", Variables.my.getCustomerID());
        params.addBodyParameter("pointid", Variables.point.getID());
        params.addBodyParameter("productid", productId);
        x.http().post(params, new Callback.CacheCallback<String>() {
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
                    if (str.equals("add")) {
                        Toast.makeText(Goods_Particular.this, "收藏成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Goods_Particular.this, "取消成功", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Goods_Particular.this, "请求失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void favorite_xUtils() {

        RequestParams params = new RequestParams(Variables.http_select_collection);
        params.addBodyParameter("userid", Variables.my.getCustomerID());
        x.http().post(params, new Callback.CacheCallback<String>() {
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
                    mine_favorite.setClickable(true);
                    try {
                        JSONObject object = new JSONObject(result);
                        Log.d("我是返回的json", result);
                        String Ret = object.getString("Ret");
                        if (Ret.equals("1")) {
                            JSONArray data = object.getJSONArray("Data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject everyone = data.getJSONObject(i);
                                Log.d("收藏", everyone.toString());
                                if (productId.equals(everyone.getString("ProductID"))) {
                                    mine_favorite.setChecked(true);
                                    break;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Goods_Particular.this, "加入失败，请重新尝试", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
