package com.soyomaker.handsgo.adapter;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.manager.CollectManager;
import com.soyomaker.handsgo.manager.HistoryManager;
import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.model.Group;
import com.soyomaker.handsgo.ui.ManualActivity;

public class GroupExpandableListViewAdapter extends BaseExpandableListAdapter {

	private ArrayList<Group> mGroups;
	private Context mContext;
	private LayoutInflater mInflater;

	public GroupExpandableListViewAdapter(Context context, ArrayList<Group> groups) {
		this.mContext = context;
		this.mGroups = groups;
		this.mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getGroupCount() {
		return mGroups.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mGroups.get(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mGroups.get(groupPosition).getChessManuals().size();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mGroups.get(groupPosition).getChessManuals().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
			ViewGroup parent) {
		GroupHolder viewGroupHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.group_item, null);
			viewGroupHolder = new GroupHolder();
			viewGroupHolder.groupNameTextView = (TextView) convertView
					.findViewById(R.id.group_name);
			viewGroupHolder.editBtn = (Button) convertView.findViewById(R.id.edit_group);
			viewGroupHolder.deleteBtn = (Button) convertView.findViewById(R.id.delete_group);
			convertView.setTag(viewGroupHolder);
		} else {
			viewGroupHolder = (GroupHolder) convertView.getTag();
		}
		final Group group = (Group) getGroup(groupPosition);
		viewGroupHolder.groupNameTextView.setText(group.toString());
		if (group.getId() != Group.DEFAULT_GROUP) {
			viewGroupHolder.editBtn.setVisibility(View.VISIBLE);
			viewGroupHolder.deleteBtn.setVisibility(View.VISIBLE);
		} else {
			viewGroupHolder.editBtn.setVisibility(View.GONE);
			viewGroupHolder.deleteBtn.setVisibility(View.GONE);
		}
		viewGroupHolder.editBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (group.getId() != Group.DEFAULT_GROUP) {
					final View view = mInflater.inflate(R.layout.dialog_edit_group_edt, null);
					final EditText editText = (EditText) view.findViewById(R.id.editText);
					editText.setText(group.getName());
					new AlertDialog.Builder(mContext)
							.setTitle(R.string.edit_group_dialog_title)
							.setIcon(R.drawable.ic_launcher)
							.setView(view)
							.setPositiveButton(R.string.edit_group_dialog_ok,
									new OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											String newName = editText.getText().toString();
											if (!TextUtils.isEmpty(newName)) {
												group.setName(newName);
												CollectManager.getInstance().updateGroup(group);

												notifyDataSetChanged();
											} else {
												// TODO toast
											}
										}
									}).setNegativeButton(R.string.edit_group_dialog_cancel, null)
							.show();
				}
			}
		});
		viewGroupHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (group.getId() != Group.DEFAULT_GROUP) {
					new AlertDialog.Builder(mContext)
							.setMessage(R.string.delete_group_dialog_msg)
							.setTitle(R.string.delete_group_dialog_title)
							.setIcon(R.drawable.ic_launcher)
							.setPositiveButton(R.string.delete_group_dialog_ok,
									new android.content.DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											CollectManager.getInstance().deleteGroup(group);

											notifyDataSetChanged();
										}
									}).setNegativeButton(R.string.delete_group_dialog_cancel, null)
							.show();
				}
			}
		});
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
			View convertView, ViewGroup parent) {
		ListViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.chessmanual_list_item, null);
			viewHolder = new ListViewHolder();
			viewHolder.matchInfoLayout = (LinearLayout) convertView
					.findViewById(R.id.layout_match_info);
			viewHolder.matchNameTextView = (TextView) convertView
					.findViewById(R.id.text_match_name);
			viewHolder.matchBlackNameTextView = (TextView) convertView
					.findViewById(R.id.text_black_name);
			viewHolder.matchWhiteNameTextView = (TextView) convertView
					.findViewById(R.id.text_white_name);
			viewHolder.matchTimeTextView = (TextView) convertView
					.findViewById(R.id.text_match_time);
			viewHolder.deleteBtn = (ImageView) convertView.findViewById(R.id.btn_delete);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ListViewHolder) convertView.getTag();
		}
		final ChessManual chessManual = mGroups.get(groupPosition).getChessManuals()
				.get(childPosition);
		if (chessManual != null) {
			viewHolder.matchNameTextView.setText(chessManual.getMatchName());
			if (HistoryManager.getInstance().isHistory(chessManual)) {
				viewHolder.matchNameTextView.setTextColor(Color.parseColor("#ff543210"));
			} else {
				viewHolder.matchNameTextView.setTextColor(Color.parseColor("#ff000000"));
			}
			viewHolder.matchBlackNameTextView.setText(chessManual.getBlackName());
			viewHolder.matchWhiteNameTextView.setText(chessManual.getWhiteName());
			viewHolder.matchTimeTextView.setText(chessManual.getMatchTime());
			viewHolder.matchInfoLayout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(mContext, ManualActivity.class);
					intent.putExtra(ManualActivity.EXTRA_CHESSMANUAL, chessManual);
					mContext.startActivity(intent);
				}
			});
			viewHolder.deleteBtn.setVisibility(View.VISIBLE);
			viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					new AlertDialog.Builder(mContext)
							.setMessage(R.string.cancel_collect_dialog_msg)
							.setTitle(R.string.cancel_collect_dialog_title)
							.setIcon(R.drawable.ic_launcher)
							.setPositiveButton(R.string.cancel_collect_dialog_ok,
									new android.content.DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											CollectManager.getInstance().cancelCollect(chessManual);

											notifyDataSetChanged();
										}
									}).setNegativeButton(R.string.cancel_collect_dialog_ok, null)
							.show();
				}
			});
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private final class ListViewHolder {
		public LinearLayout matchInfoLayout;
		public TextView matchNameTextView;
		public TextView matchWhiteNameTextView;
		public TextView matchBlackNameTextView;
		public TextView matchTimeTextView;

		public ImageView deleteBtn;
	}

	private final class GroupHolder {
		public TextView groupNameTextView;

		public Button editBtn;
		public Button deleteBtn;
	}
}
