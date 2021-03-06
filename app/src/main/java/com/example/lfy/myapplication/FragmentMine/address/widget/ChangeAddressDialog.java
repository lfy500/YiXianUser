package com.example.lfy.myapplication.FragmentMine.address.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lfy.myapplication.FragmentMine.address.widget.adapters.AbstractWheelTextAdapter;
import com.example.lfy.myapplication.FragmentMine.address.widget.views.OnWheelChangedListener;
import com.example.lfy.myapplication.FragmentMine.address.widget.views.OnWheelScrollListener;
import com.example.lfy.myapplication.FragmentMine.address.widget.views.WheelView;
import com.example.lfy.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 更改封面对话框
 *
 * @author ywl
 */
public class ChangeAddressDialog extends Dialog implements View.OnClickListener {

    private WheelView wvProvince;
    private WheelView wvCitys;
    private WheelView wvDis;

    private View lyChangeAddress;
    private View lyChangeAddressChild;
    private TextView btnSure;
    private TextView btnCancel;

    private Context context;
    private JSONObject mJsonObj;
    private String[] mProvinceDatas;

    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    private Map<String, String[]> DisMap = new HashMap<>();

    private ArrayList<String> arrProvinces = new ArrayList<String>();
    private ArrayList<String> arrCitys = new ArrayList<String>();
    private ArrayList<String> arrDis = new ArrayList<>();

    private AddressTextAdapter provinceAdapter;
    private AddressTextAdapter cityAdapter;
    private AddressTextAdapter disAdapter;

    private String strProvince = "浙江";
    private String strCity = "杭州";
    private String strDis = "江干区";
    private OnAddressCListener onAddressCListener;

    private int maxsize = 24;
    private int minsize = 14;

//    public ChangeAddressDialog(Context context) {
//        super(context);
//        this.context = context;
//    }

    public ChangeAddressDialog(Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_address);

        wvProvince = (WheelView) findViewById(R.id.wv_address_province);
        wvCitys = (WheelView) findViewById(R.id.wv_address_city);
        wvDis = (WheelView) findViewById(R.id.wv_address_dis);

        lyChangeAddress = findViewById(R.id.ly_myinfo_changeaddress);
        lyChangeAddressChild = findViewById(R.id.ly_myinfo_changeaddress_child);

        btnSure = (TextView) findViewById(R.id.btn_myinfo_sure);
        btnCancel = (TextView) findViewById(R.id.btn_myinfo_cancel);

        lyChangeAddress.setOnClickListener(this);
        lyChangeAddressChild.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        initJsonData();//读取json
        initDatas();//解析数据

        initProvinces();
        provinceAdapter = new AddressTextAdapter(context, arrProvinces, getProvinceItem(strProvince), maxsize, minsize);
        wvProvince.setVisibleItems(5);
        wvProvince.setViewAdapter(provinceAdapter);
        wvProvince.setCurrentItem(getProvinceItem(strProvince));

        initCitys(mCitisDatasMap.get(strProvince));
        cityAdapter = new AddressTextAdapter(context, arrCitys, getCityItem(strCity), maxsize, minsize);
        wvCitys.setVisibleItems(5);
        wvCitys.setViewAdapter(cityAdapter);
        wvCitys.setCurrentItem(getCityItem(strCity));

        initDis(DisMap.get(strDis));//===================
        disAdapter = new AddressTextAdapter(context, arrDis, getDisItem(strDis), maxsize, minsize);
        wvDis.setVisibleItems(5);
        wvDis.setViewAdapter(disAdapter);
        wvDis.setCurrentItem(getDisItem(strDis));

        wvProvince.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) provinceAdapter.getItemText(wheel.getCurrentItem());
                strProvince = currentText;
                setTextviewSize(currentText, provinceAdapter);
                String[] citys = mCitisDatasMap.get(currentText);

                initCitys(citys);
                cityAdapter = new AddressTextAdapter(context, arrCitys, 0, maxsize, minsize);
                wvCitys.setVisibleItems(5);
                wvCitys.setViewAdapter(cityAdapter);
                wvCitys.setCurrentItem(0);

                String[] diss = DisMap.get(citys[0]);
                if (diss == null) {
                    arrDis.clear();
                    arrDis.add("--");
                    disAdapter = new AddressTextAdapter(context, arrDis, 0, maxsize, minsize);
                    wvDis.setVisibleItems(5);
                    wvDis.setViewAdapter(disAdapter);
                    wvDis.setCurrentItem(0);
                } else {
                    initDis(diss);
                    disAdapter = new AddressTextAdapter(context, arrDis, 0, maxsize, minsize);
                    wvDis.setVisibleItems(5);
                    wvDis.setViewAdapter(disAdapter);
                    wvDis.setCurrentItem(0);
                }

            }
        });

        wvProvince.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) provinceAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, provinceAdapter);
            }
        });

        wvCitys.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) cityAdapter.getItemText(wheel.getCurrentItem());
                strCity = currentText;
                setTextviewSize(currentText, cityAdapter);

                String[] diss = DisMap.get(currentText);
                if (diss == null) {
                    arrDis.clear();
                    arrDis.add("--");
                    disAdapter = new AddressTextAdapter(context, arrDis, 0, maxsize, minsize);
                    wvDis.setVisibleItems(5);
                    wvDis.setViewAdapter(disAdapter);
                    wvDis.setCurrentItem(0);
                } else {
                    initDis(diss);
                    disAdapter = new AddressTextAdapter(context, arrDis, 0, maxsize, minsize);
                    wvDis.setVisibleItems(5);
                    wvDis.setViewAdapter(disAdapter);
                    wvDis.setCurrentItem(0);
                }
            }
        });

        wvCitys.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) cityAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, cityAdapter);
            }
        });


        wvDis.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) disAdapter.getItemText(wheel.getCurrentItem());
                strDis = currentText;
                setTextviewSize(currentText, disAdapter);

            }
        });

        wvDis.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) disAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, disAdapter);
            }
        });
    }


    private class AddressTextAdapter extends AbstractWheelTextAdapter {
        ArrayList<String> list;

        protected AddressTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize, int minsize) {
            super(context, R.layout.dialog_address_item, NO_RESOURCE, currentItem, maxsize, minsize);
            this.list = list;
            setItemTextResource(R.id.tempValue);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }
    }

    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, AddressTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(24);
            } else {
                textvew.setTextSize(14);
            }
        }
    }

    public void setAddresskListener(OnAddressCListener onAddressCListener) {
        this.onAddressCListener = onAddressCListener;
    }

    @Override
    public void onClick(View v) {
        if (v == btnSure) {
            if (onAddressCListener != null) {
                onAddressCListener.onClick(strProvince, strCity, strDis);
            }
        } else if (v == btnCancel) {

        } else if (v == lyChangeAddressChild) {
            return;
        } else {
            dismiss();
        }
        dismiss();
    }

    /**
     * 回调接口
     *
     * @author Administrator
     */
    public interface OnAddressCListener {
        public void onClick(String province, String city, String dis);
    }

    /**
     * 从文件中读取地址数据
     */
    private void initJsonData() {
        try {
            StringBuffer sb = new StringBuffer();
            InputStream is = context.getAssets().open("city.json");
            int len = -1;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1) {
                sb.append(new String(buf, 0, len, "gbk"));
            }
            is.close();
            mJsonObj = new JSONObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析数据
     */
    private void initDatas() {
        try {
            JSONArray jsonArray = mJsonObj.getJSONArray("citylist");
            mProvinceDatas = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonP = jsonArray.getJSONObject(i);
                String province = jsonP.getString("p");
                mProvinceDatas[i] = province;//省份

                JSONArray jsonCs = null;
                try {
                    /**
                     * Throws JSONException if the mapping doesn't exist or is
                     * not a JSONArray.
                     */
                    jsonCs = jsonP.getJSONArray("c");
                } catch (Exception e1) {
                    continue;
                }
                String[] mCitiesDatas = new String[jsonCs.length()];
                for (int j = 0; j < jsonCs.length(); j++) {
                    JSONObject jsonCity = jsonCs.getJSONObject(j);
                    String city = jsonCity.getString("n"); //城市
                    mCitiesDatas[j] = city;
                    JSONArray jsonAreas = null;
                    try {
                        /**
                         * Throws JSONException if the mapping doesn't exist or
                         * is not a JSONArray.
                         */
                        jsonAreas = jsonCity.getJSONArray("a");
                    } catch (Exception e) {
                        continue;
                    }

                    String[] mAreasDatas = new String[jsonAreas.length()];
                    for (int k = 0; k < jsonAreas.length(); k++) {
                        String area = jsonAreas.getJSONObject(k).getString("s");//区
                        mAreasDatas[k] = area;
                    }
                    DisMap.put(city, mAreasDatas);
                }

                mCitisDatasMap.put(province, mCitiesDatas);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mJsonObj = null;
    }


//    /**
//     * * 根据当前的市，更新区WheelView的信息
//     */
//    private void updateAreas() {
//        int pCurrent = mCity.getCurrentItem();
//        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
//        String[] areas = mAreaDatasMap.get(mCurrentCityName);
//
//        if (areas == null) {
//            areas = new String[]{""};
//        }
//        mArea.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
//        mArea.setCurrentItem(0);
//    }
//
//    /**
//     * 根据当前的省，更新市WheelView的信息
//     */
//    private void updateCities() {
//        int pCurrent = mProvince.getCurrentItem();
//        mCurrentProviceName = mProvinceDatas[pCurrent];
//        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
//        if (cities == null) {
//            cities = new String[]{""};
//        }
//        mCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
//        mCity.setCurrentItem(0);
//        updateAreas();
//    }


    /**
     * 初始化省会
     */
    public void initProvinces() {
        int length = mProvinceDatas.length;
        for (int i = 0; i < length; i++) {
            arrProvinces.add(mProvinceDatas[i]);
        }
    }

    /**
     * 根据省会，生成该省会的所有城市
     *
     * @param citys
     */
    public void initCitys(String[] citys) {
        if (citys != null) {
            arrCitys.clear();
            int length = citys.length;
            for (int i = 0; i < length; i++) {
                arrCitys.add(citys[i]);
            }
        } else {
            String[] city = mCitisDatasMap.get("浙江");
            arrCitys.clear();
            int length = city.length;
            for (int i = 0; i < length; i++) {
                arrCitys.add(city[i]);
            }
        }
        if (arrCitys != null && arrCitys.size() > 0
                && !arrCitys.contains(strCity)) {
            strCity = arrCitys.get(0);
        }
    }

    public void initDis(String[] diss) {
        if (diss != null) {
            arrDis.clear();
            int length = diss.length;
            for (int i = 0; i < length; i++) {
                arrDis.add(diss[i]);
            }
        } else {
            String[] dis = DisMap.get("杭州");
            arrDis.clear();
            int length = dis.length;
            for (int i = 0; i < length; i++) {
                arrDis.add(dis[i]);
            }
        }
        if (arrDis != null && arrDis.size() > 0
                && !arrDis.contains(strDis)) {
            strDis = arrDis.get(0);
        }
    }

    /**
     * 初始化地点
     *
     * @param province
     * @param city
     * @param dis
     */
    public void setAddress(String province, String city, String dis) {
        if (province != null && province.length() > 0) {
            this.strProvince = province;
        }
        if (city != null && city.length() > 0) {
            this.strCity = city;
        }
        if (dis != null && dis.length() > 0) {
            this.strDis = dis;
        }
    }

    /**
     * 返回省会索引，没有就返回默认“浙江”
     *
     * @param province
     * @return
     */
    public int getProvinceItem(String province) {
        int size = arrProvinces.size();
        int provinceIndex = 0;
        boolean noprovince = true;
        for (int i = 0; i < size; i++) {
            if (province.equals(arrProvinces.get(i))) {
                noprovince = false;
                return provinceIndex;
            } else {
                provinceIndex++;
            }
        }
        if (noprovince) {
            strProvince = "浙江";
            return 22;
        }
        return provinceIndex;
    }

    /**
     * 得到城市索引，没有返回默认“杭州”
     *
     * @param city
     * @return
     */
    public int getCityItem(String city) {
        int size = arrCitys.size();
        int cityIndex = 0;
        boolean nocity = true;
        for (int i = 0; i < size; i++) {
            System.out.println(arrCitys.get(i));
            if (city.equals(arrCitys.get(i))) {
                nocity = false;
                return cityIndex;
            } else {
                cityIndex++;
            }
        }
        if (nocity) {
            strCity = "杭州";
            return 0;
        }
        return cityIndex;
    }

    /**
     * 得到城市索引，没有返回默认“江干区”
     *
     * @param dis
     * @return
     */
    public int getDisItem(String dis) {
        int size = arrDis.size();
        int disIndex = 0;
        boolean nocity = true;
        for (int i = 0; i < size; i++) {
            System.out.println(arrDis.get(i));
            if (dis.equals(arrDis.get(i))) {
                nocity = false;
                return disIndex;
            } else {
                disIndex++;
            }
        }
        if (nocity) {
            strDis = "江干区";
            return 0;
        }
        return disIndex;
    }

}