package com.soyomaker.handsgonew.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.soyomaker.handsgonew.R;
import com.soyomaker.handsgonew.adapter.SearchResultListViewAdapter;
import com.soyomaker.handsgonew.model.SearchResultList;

public class SearchResultActivity extends Activity {

	public static final String EXTRA_SEARCH_RESULT = "extra_search_result";

	private SearchResultList mSearchResultList;
	private ListView mSearchResultListView;
	private SearchResultListViewAdapter mSearchResultListViewAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result);

		initData();
		initView();
	}

	private void initData() {
		Intent intent = getIntent();
		mSearchResultList = (SearchResultList) intent.getSerializableExtra(EXTRA_SEARCH_RESULT);
	}

	private void initView() {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.title_search_result);

		mSearchResultListViewAdapter = new SearchResultListViewAdapter(this);
		if (mSearchResultList != null) {
			mSearchResultListViewAdapter.setSearchResults(mSearchResultList.getSearchResults());
		}
		mSearchResultListView = (ListView) findViewById(R.id.listview_search_result);
		mSearchResultListView.setAdapter(mSearchResultListViewAdapter);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
