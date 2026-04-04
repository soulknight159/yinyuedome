package cn.itcast.yinyue.net;
import cn.itcast.yinyue.consts.Consts;
import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static RetrofitClient instance;
    private final Retrofit retrofit;

    private RetrofitClient() {
        // 日志拦截器
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
//                .addInterceptor(loggingInterceptor)
//                .addInterceptor(new HeaderInterceptor()) // 如需全局Header，打开这里
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Consts.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // 单例
    public static RetrofitClient getInstance() {
        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    instance = new RetrofitClient();
                }
            }
        }
        return instance;
    }

    // 获取ApiService
    public <T> T getApiService(Class<T> service) {
        return retrofit.create(service);
    }
}