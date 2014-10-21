package com.soyomaker.handsgo.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.model.Comment;

public class CommentListViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.getDefault());
    private ArrayList<Comment> mComments;

    public CommentListViewAdapter(Context ctx, ArrayList<Comment> comments) {
        this.mInflater = LayoutInflater.from(ctx);
        this.mComments = comments;
    }

    public void updateComments(ArrayList<Comment> comments) {
        this.mComments = comments;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.mComments.size();
    }

    @Override
    public Comment getItem(int position) {
        if (position >= mComments.size()) {
            return null;
        } else {
            return mComments.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = mInflater.inflate(R.layout.comment_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.userNameTextView = (TextView) convertView.findViewById(R.id.txt_user_name);
            viewHolder.commentTextView = (TextView) convertView.findViewById(R.id.txt_comment);
            viewHolder.commentTimeTextView = (TextView) convertView
                    .findViewById(R.id.txt_comment_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Comment comment = getItem(position);
        if (comment != null) {
            viewHolder.userNameTextView.setText(comment.getUserName());
            viewHolder.commentTextView.setText(comment.getComment());
            viewHolder.commentTimeTextView.setText(mFormat.format(comment.getInsertTimeDate()));
        }
        return convertView;
    }

    private final class ViewHolder {
        public TextView userNameTextView;
        public TextView commentTextView;
        public TextView commentTimeTextView;
    }
}
