package cn.itcast.yinyue.net;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseCallback<T> implements Callback<ApiResponse<T>> {

    @Override
    public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
        if (response.isSuccessful()) {
            ApiResponse<T> apiResponse = response.body();
            if (apiResponse != null) {
                Log.d("BaseCallback", "响应结果"+apiResponse.getCode());
                if (apiResponse.getCode() == 200) {
                    // 业务成功
                    Log.d("BaseCallback", "响应结果"+apiResponse.getData());
                    onSuccess(apiResponse.getData());
                } else {
                    // 业务失败
                    Log.d("BaseCallback", "响应结果"+apiResponse.getMsg());
                    onBusinessError(apiResponse.getCode(), apiResponse.getMsg());
                }
            } else {
                onFailure(call, new Exception("response body is null"));
            }
        } else {
            onHttpError(response.code());
        }
    }

    @Override
    public void onFailure(Call<ApiResponse<T>> call, Throwable t) {
//        Log.e("BaseCallBack", "onFailure: ",t );
//        onError(t);
    }

    // 成功
    public abstract void onSuccess(T data);

    // 业务错误（code != 200）
    public void onBusinessError(int code, String msg) {}

    // HTTP错误（404、500等）
    public void onHttpError(int code) {}

    // 网络/解析异常
    public void onError(Throwable t) {}
}