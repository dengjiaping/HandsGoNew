package com.soyomaker.handsgo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.model.SearchResult;
import com.soyomaker.handsgo.search.SearchController;
import com.soyomaker.handsgo.ui.ManualActivity;
import com.weibo.image.SinaImageLoader;

public class SearchResultListViewAdapter extends BaseAdapter {

	private final class ListViewHolder {

		public RelativeLayout resultLayout;

		public ImageView resultImageView;

		public TextView resultTitleTextView;

		public RelativeLayout resultLayout2;

		public ImageView resultImageView2;

		public TextView resultTitleTextView2;
	}

	private Context mContext;

	private Handler mHandler = new Handler(Looper.getMainLooper());

	private LayoutInflater mInflater;

	private ArrayList<SearchResult> mResults;

	public SearchResultListViewAdapter(Context ctx, ArrayList<SearchResult> results) {
		this.mContext = ctx;
		this.mResults = results;
		this.mInflater = LayoutInflater.from(ctx);
	}

	public SearchResultListViewAdapter(Context ctx) {
		this.mContext = ctx;
		this.mResults = new ArrayList<SearchResult>();
		this.mInflater = LayoutInflater.from(ctx);
	}

	public ArrayList<SearchResult> getSearchResults() {
		return mResults;
	}

	public void setSearchResults(ArrayList<SearchResult> results) {
		this.mResults = results;
	}

	@Override
	public int getCount() {
		return (mResults.size() + 1) / 2;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ListViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.search_result_grid_item, null);
			viewHolder = new ListViewHolder();
			viewHolder.resultLayout = (RelativeLayout) convertView.findViewById(R.id.result_layout);
			viewHolder.resultTitleTextView = (TextView) convertView.findViewById(R.id.result_title);
			viewHolder.resultImageView = (ImageView) convertView.findViewById(R.id.result_image);
			viewHolder.resultLayout2 = (RelativeLayout) convertView
					.findViewById(R.id.result_layout_2);
			viewHolder.resultTitleTextView2 = (TextView) convertView
					.findViewById(R.id.result_title_2);
			viewHolder.resultImageView2 = (ImageView) convertView.findViewById(R.id.result_image_2);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ListViewHolder) convertView.getTag();
		}
		int id1 = position * 2;
		if (mResults.size() > id1) {
			final SearchResult result = mResults.get(id1);
			viewHolder.resultLayout.setVisibility(View.VISIBLE);
			viewHolder.resultLayout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					new Thread() {
						public void run() {
							final ChessManual chessManual = SearchController.getInstance()
									.getChessManual(mContext, result.getId());
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									Intent intent = new Intent(mContext, ManualActivity.class);
									intent.putExtra(ManualActivity.EXTRA_CHESSMANUAL, chessManual);
									mContext.startActivity(intent);
								}
							});
						}
					}.start();
				}
			});
			viewHolder.resultTitleTextView.setText(result.getName());
			SinaImageLoader.create(result.getImageUrl()).setDefaultRes(R.drawable.ic_launcher)
					.into(viewHolder.resultImageView);
		} else {
			viewHolder.resultLayout.setVisibility(View.GONE);
		}
		int id2 = position * 2 + 1;
		if (mResults.size() > id2) {
			final SearchResult result2 = mResults.get(id2);
			viewHolder.resultLayout2.setVisibility(View.VISIBLE);
			viewHolder.resultLayout2.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					new Thread() {
						public void run() {
							final ChessManual chessManual = SearchController.getInstance()
									.getChessManual(mContext, result2.getId());
							mHandler.post(new Runnable() {

								@Override
								public void run() {
									Intent intent = new Intent(mContext, ManualActivity.class);
									intent.putExtra(ManualActivity.EXTRA_CHESSMANUAL, chessManual);
									mContext.startActivity(intent);
								}
							});
						}
					}.start();
				}
			});
			viewHolder.resultTitleTextView2.setText(result2.getName());
			SinaImageLoader.create(result2.getImageUrl()).setDefaultRes(R.drawable.ic_launcher)
					.into(viewHolder.resultImageView2);
		} else {
			viewHolder.resultLayout2.setVisibility(View.GONE);
		}
		return convertView;
	}
}