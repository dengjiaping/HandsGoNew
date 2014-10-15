package com.soyomaker.handsgo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.server.IChessManualServer;
import com.soyomaker.handsgo.ui.ManualActivity;

/**
 * 棋谱列表适配器
 * 
 * @author like
 * 
 */
public class ChessManualListViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<ChessManual> mChessManuals;
    private IChessManualServer mChessManualServer;
    private TextView mFooterTxt;
    private View mFooterView;

    public ChessManualListViewAdapter(Context ctx, IChessManualServer chessManualServer) {
        this.mContext = ctx;
        this.mChessManualServer = chessManualServer;
        this.mChessManuals = chessManualServer.getChessManuals();
        this.mInflater = LayoutInflater.from(ctx);
        initLoadMoreView();
    }

    public void updateChessManualServer(IChessManualServer chessManualServer) {
        this.mChessManualServer = chessManualServer;
        this.mChessManuals = chessManualServer.getChessManuals();
        notifyDataSetChanged();
    }

    public IChessManualServer getChessManualServer() {
        return mChessManualServer;
    }

    private void initLoadMoreView() {
        mFooterView = LayoutInflater.from(mContext).inflate(R.layout.chessmanual_list_footer, null);
        mFooterTxt = (TextView) mFooterView.findViewById(R.id.footer_txt);
    }

    private View getLoadingMoreView() {
        if (!mChessManualServer.isLoadingMore()) {
            mFooterTxt.setText(R.string.list_load_more);
        } else {
            mFooterTxt.setText(R.string.list_loading);
        }
        return mFooterView;
    }

    @Override
    public int getCount() {
        int count = mChessManuals.size();
        return (count == 0 || !mChessManualServer.canLoadMore()) ? count : count + 1;
    }

    @Override
    public ChessManual getItem(int position) {
        if (position >= mChessManuals.size()) {
            return null;
        } else {
            return mChessManuals.get(position);
        }
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {
        if (position == mChessManuals.size()) {
            return getLoadingMoreView();
        } else {
            ViewHolder viewHolder;
            if (convertView == null || convertView.getTag() == null) {
                convertView = mInflater.inflate(R.layout.chessmanual_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.matchNameTextView = (TextView) convertView
                        .findViewById(R.id.text_match_name);
                viewHolder.matchBlackNameTextView = (TextView) convertView
                        .findViewById(R.id.text_black_name);
                viewHolder.matchWhiteNameTextView = (TextView) convertView
                        .findViewById(R.id.text_white_name);
                viewHolder.matchTimeTextView = (TextView) convertView
                        .findViewById(R.id.text_match_time);
                viewHolder.matchInfoLayout = (LinearLayout) convertView
                        .findViewById(R.id.layout_match_info);
                viewHolder.deleteButton = (Button) convertView.findViewById(R.id.buttonDelete);
                viewHolder.collectButton = (Button) convertView.findViewById(R.id.buttonCollect);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final ChessManual chessManual = getItem(position);
            if (chessManual != null) {
                viewHolder.matchNameTextView.setText(chessManual.getMatchName());
                viewHolder.matchBlackNameTextView.setText(chessManual.getBlackName());
                viewHolder.matchWhiteNameTextView.setText(chessManual.getWhiteName());
                viewHolder.matchTimeTextView.setText(chessManual.getMatchTime());
                viewHolder.matchInfoLayout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(mContext, ManualActivity.class);
                        intent.putExtra(ManualActivity.EXTRA_CHESSMANUAL, chessManual);
                        mContext.startActivity(intent);
                    }
                });
                if (mChessManualServer.canDelete()) {
                    viewHolder.deleteButton.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.deleteButton.setVisibility(View.GONE);
                }
                if (mChessManualServer.canCollect()) {
                    viewHolder.collectButton.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.collectButton.setVisibility(View.GONE);
                }
            }
            return convertView;
        }
    }

    private final class ViewHolder {
        public LinearLayout matchInfoLayout;
        public TextView matchNameTextView;
        public TextView matchWhiteNameTextView;
        public TextView matchBlackNameTextView;
        public TextView matchTimeTextView;

        public Button collectButton;
        public Button deleteButton;
    }
}
