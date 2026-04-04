package cn.itcast.yinyue.net;

import java.util.List;

import cn.itcast.yinyue.bean.Music;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MusicApi {
    @GET("music/list")
    Call<ApiResponse<List<Music>>> getMusics();
}
