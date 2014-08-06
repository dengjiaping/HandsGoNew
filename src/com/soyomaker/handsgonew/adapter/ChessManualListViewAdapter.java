package com.soyomaker.handsgonew.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.soyomaker.handsgonew.R;
import com.soyomaker.handsgonew.model.ChessManual;
import com.soyomaker.handsgonew.ui.ManualActivity;

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

	public ChessManualListViewAdapter(Context ctx, ArrayList<ChessManual> chessManuals) {
		this.mContext = ctx;
		this.mChessManuals = chessManuals;
		this.mInflater = LayoutInflater.from(ctx);
	}

	public ArrayList<ChessManual> getChessManuals() {
		return mChessManuals;
	}

	public void setChessManuals(ArrayList<ChessManual> mChessManuals) {
		this.mChessManuals = mChessManuals;
	}

	@Override
	public int getCount() {
		return mChessManuals.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mChessManuals.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder;
		if (convertView == null) {
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
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final ChessManual chessManual = mChessManuals.get(position);
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
		return convertView;
	}

	private final class ViewHolder {

		public LinearLayout matchInfoLayout;
		public TextView matchNameTextView;
		public TextView matchWhiteNameTextView;
		public TextView matchBlackNameTextView;
		public TextView matchTimeTextView;
	}
}
