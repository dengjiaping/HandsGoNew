package com.soyomaker.handsgo.adapter;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.soyomaker.handsgo.HandsGoApplication;
import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.db.DBService;
import com.soyomaker.handsgo.manager.CollectManager;
import com.soyomaker.handsgo.manager.HistoryManager;
import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.model.Group;
import com.soyomaker.handsgo.server.IChessManualServer;
import com.soyomaker.handsgo.ui.ManualActivity;
import com.soyomaker.handsgo.util.DialogUtils;
import com.soyomaker.handsgo.util.DialogUtils.ItemSelectedListener;

/**
 * 棋谱列表适配器
 * 
 * @author like
 * 
 */
public class ChessManualListViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<ChessManual> mChessManuals = new ArrayList<ChessManual>();
    private IChessManualServer mChessManualServer;
    private TextView mFooterTxt;
    private View mFooterView;

    public ChessManualListViewAdapter(Context ctx, IChessManualServer chessManualServer) {
        this.mContext = ctx;
        this.mChessManualServer = chessManualServer;
        this.mInflater = LayoutInflater.from(ctx);
        initLoadMoreView();
        updateChessManuals();
    }

    public void updateChessManuals() {
        this.mChessManuals.clear();
        this.mChessManuals.addAll(this.mChessManualServer.getChessManuals());
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
                viewHolder.deleteButton = (ImageView) convertView.findViewById(R.id.btn_delete);
                viewHolder.collectButton = (ImageView) convertView.findViewById(R.id.btn_collect);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final ChessManual chessManual = getItem(position);
            if (chessManual != null) {
                if (HistoryManager.getInstance().isHistory(chessManual)) {
                    viewHolder.matchNameTextView.setTextColor(Color.parseColor("#ff543210"));
                } else {
                    viewHolder.matchNameTextView.setTextColor(Color.parseColor("#ff000000"));
                }
                viewHolder.matchNameTextView.setText(chessManual.getMatchName());
                viewHolder.matchBlackNameTextView.setText(chessManual.getBlackName());
                viewHolder.matchWhiteNameTextView.setText(chessManual.getWhiteName());
                viewHolder.matchTimeTextView.setText(chessManual.getMatchTime());
                viewHolder.matchInfoLayout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        HandsGoApplication.setChessManual(chessManual);
                        Intent intent = new Intent(mContext, ManualActivity.class);
                        mContext.startActivity(intent);
                    }
                });
                if (mChessManualServer.canDelete()) {
                    viewHolder.deleteButton.setVisibility(View.VISIBLE);
                    viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            mChessManualServer.delete(chessManual);
                            updateChessManuals();
                        }
                    });
                } else {
                    viewHolder.deleteButton.setVisibility(View.GONE);
                }
                if (mChessManualServer.canCollect()) {
                    viewHolder.collectButton.setVisibility(View.VISIBLE);
                    viewHolder.collectButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (CollectManager.getInstance().isCollect(chessManual)) {
                                new AlertDialog.Builder(mContext)
                                        .setMessage(R.string.cancel_collect_dialog_msg)
                                        .setTitle(R.string.cancel_collect_dialog_title)
                                        .setIcon(R.drawable.ic_launcher)
                                        .setPositiveButton(
                                                R.string.cancel_collect_dialog_ok,
                                                new android.content.DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog,
                                                            int which) {
                                                        CollectManager.getInstance().cancelCollect(
                                                                chessManual);

                                                        updateChessManuals();
                                                    }
                                                })
                                        .setNegativeButton(R.string.cancel_collect_dialog_ok, null)
                                        .show();
                            } else {
                                final ArrayList<Group> groups = DBService.getGroupCaches();
                                String[] groupsNames = new String[groups.size()];
                                for (int i = 0; i < groups.size(); i++) {
                                    groupsNames[i] = groups.get(i).getName();
                                }
                                DialogUtils.showItemsDialog(mContext,
                                        R.string.collect_dialog_title, groupsNames,
                                        new ItemSelectedListener() {

                                            @Override
                                            public void onItemSelected(DialogInterface dialog,
                                                    String text, int which) {
                                                chessManual.setGroupId(groups.get(which).getId());
                                                CollectManager.getInstance().collect(chessManual);
                                                Toast.makeText(mContext,
                                                        R.string.toast_collect_success,
                                                        Toast.LENGTH_LONG).show();

                                                updateChessManuals();
                                            }
                                        });

                            }
                        }
                    });
                    if (CollectManager.getInstance().isCollect(chessManual)) {
                        viewHolder.collectButton.setImageResource(R.drawable.star_full_large);
                    } else {
                        viewHolder.collectButton.setImageResource(R.drawable.star_empty_large);
                    }
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

        public ImageView collectButton;
        public ImageView deleteButton;
    }
}
