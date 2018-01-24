package io.weichao.stickynavigationbar.model;

import android.util.Log;

import io.weichao.stickynavigationbar.activity.MainActivity;
import io.weichao.stickynavigationbar.bean.DataResponseBean;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chao.wei on 2018/1/24.
 */
public class MainModel {
    private static final String TAG = "MainModel";

    private static final String BASE_URL = "http://api.meituan.com/";

    private MainActivity mActivity;

    private MainClient mClient;
    private Call mCall;

    public MainModel(MainActivity activity) {
        mActivity = activity;
    }

    public void onDestroy() {
        mActivity = null;
        if (mCall != null && mCall.isCanceled()) {
            mCall.cancel();
            mCall = null;
        }
        mClient = null;
    }

    private void initClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mClient = retrofit.create(MainClient.class);
    }

    public void getData() {
        Log.d(TAG, "getData()");
        if (mClient == null) {
            initClient();
        }
        mCall = mClient.getData();
        mCall.enqueue(new Callback<DataResponseBean>() {
            @Override
            public void onResponse(Call<DataResponseBean> call, Response<DataResponseBean> response) {
                if (mActivity != null) {
                    if (response == null || response.body() == null) {
                        mActivity.onFailedGetData("response == null || response.body() == null");
                    } else {
                        DataResponseBean bean = response.body();
                        mActivity.onSuccessGetData(bean);
                    }
                }
            }

            @Override
            public void onFailure(Call<DataResponseBean> call, Throwable t) {
                if (mActivity != null) {
                    mActivity.onFailedGetData("onFailure()");
                }
            }
        });
    }
}
