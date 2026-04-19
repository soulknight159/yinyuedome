package cn.itcast.yinyue.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.yinyue.consts.Consts;
import cn.itcast.yinyue.bean.Music;
import cn.itcast.yinyue.R;
import cn.itcast.yinyue.net.BaseCallback;
import cn.itcast.yinyue.net.MusicApi;
import cn.itcast.yinyue.net.RetrofitClient;
import cn.itcast.yinyue.ui.MusicListAdapter;
import cn.itcast.yinyue.ui.activity.PlayMusicActivity;

public class NetMusicFragment extends Fragment {
    ListView listView;
    TextView emptyText;
    MusicListAdapter musicListAdapter;
    List<Music> musicList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_net_music, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.net_music_list);
        emptyText = view.findViewById(R.id.emptyText);

        MusicApi musicApi = RetrofitClient.getInstance().getApiService(MusicApi.class);
        Log.d("MusicFragment", "请求对象：" + musicApi);

        musicApi.getMusics().enqueue(new BaseCallback<>() {
            @Override
            public void onSuccess(List<Music> data) {
               musicList.clear();
               musicList.addAll(data);
               musicListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onHttpError(int code) {
                if (code == 404){
                    Log.i("NetMusicFragment", "onHttpError: 404");
                }
            }
        });

        listView.setEmptyView(emptyText);
        musicListAdapter = new MusicListAdapter(musicList,view.getContext());
        listView.setAdapter(musicListAdapter);

        listView.setOnItemClickListener((adapterView, view1, i, l) -> {
            Intent intent =new Intent(view.getContext(), PlayMusicActivity.class);
            intent.putExtra("music",musicList.get(i));
            startActivity(intent);
        });
    }
}
