package cn.itcast.yinyue.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import cn.itcast.yinyue.bean.Music;
import cn.itcast.yinyue.R;

public class MusicListAdapter extends BaseAdapter {
    List<Music> list;
    Context context;
    public MusicListAdapter(List<Music> musicList, Context context){
        super();
        this.list=musicList;
        this.context=context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            // 加载列表项布局
            view = LayoutInflater.from(context).inflate(R.layout.item_music_list, viewGroup, false);
            viewHolder = new ViewHolder();
            // 初始化控件
            viewHolder.musicImg = view.findViewById(R.id.icon_music);
            viewHolder.musicTitle = view.findViewById(R.id.music_title);
            viewHolder.singer = view.findViewById(R.id.singer);
            // 将ViewHolder存储到convertView中，实现复用
            view.setTag(viewHolder);
        } else {
            // 复用已存在的convertView
            viewHolder = (ViewHolder) view.getTag();
        }
        // 绑定数据
        if (list.get(i).getTitle() == null){
            viewHolder.musicTitle.setText(list.get(i).getFileName());
        }else {
            viewHolder.musicTitle.setText(list.get(i).getTitle());
        }
        viewHolder.singer.setText(list.get(i).getSinger());
        return view;
    }
}
