package cn.ipmsgchat.android.adapter;

import java.util.ArrayList;
import java.util.List;

import cn.ipmsgchat.android.entity.Avatar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ipmsgchat.android.R;

public class AvatarAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Avatar> pictures;

    public AvatarAdapter(String[] titles, int[] images, Context context) {
        super();
        pictures = new ArrayList<Avatar>();
        inflater = LayoutInflater.from(context);
        int mSize = images.length;
        for (int i = 0; i < mSize; i++) {
            Avatar picture = new Avatar(titles[i], images[i]);
            pictures.add(picture);
        }
    }

    @Override
    public int getCount() {
        if (null != pictures) {
            return pictures.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return pictures.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.picture_item, null);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.myprofile_avatarimage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.image.setImageResource(((Avatar) pictures.get(position)).getImageId());
        return convertView;
    }

    class ViewHolder {
        public TextView title;
        public ImageView image;
    }
}
