package cn.itcast.yinyue.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import cn.itcast.yinyue.untils.GetMusicUtil;
import cn.itcast.yinyue.bean.Music;
import cn.itcast.yinyue.ui.MusicListAdapter;
import cn.itcast.yinyue.R;
import cn.itcast.yinyue.ui.activity.PlayMusicActivity;


public class LocalMusicFragment extends Fragment {
    private static final String TAG = "localMusicFragment";
    List<Music> musicList;
    MusicListAdapter musicListAdapter;
    ListView musiclistView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_music, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        musicList = GetMusicUtil.getMusic(view.getContext());
        musiclistView = view.findViewById(R.id.list_music);
        musicListAdapter = new MusicListAdapter(musicList, view.getContext());
        musiclistView.setAdapter(musicListAdapter);
        musiclistView.setOnItemClickListener((adapterView, view1, i, l) -> {
            Intent intent =new Intent(view.getContext(), PlayMusicActivity.class);
            intent.putExtra("music",musicList.get(i));
            Log.d(TAG, "listViewItemClick: "+ musicList.get(i).toString());
            startActivity(intent);
        });
    }
}