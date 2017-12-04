package com.artsovalov.windenergyrss.ui;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import com.artsovalov.windenergyrss.R;
import com.artsovalov.windenergyrss.client.RssLoader;
import com.artsovalov.windenergyrss.model.RssFeedModel;
import com.artsovalov.windenergyrss.service.RssFeedListAdapterListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<RssFeedModel>>,
 DatePickerDialog.OnDateSetListener, RssFeedListAdapterListener {

    public static final String URL_STRING = "http://wind.energy-business-review.com/rss";
    public static final String ARG_PUB_DATE = "date";
    public static final int LOADER_ID = 1;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeLayout;
    private RssFeedListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerView);
        mSwipeLayout = findViewById(R.id.swipeRefreshLayout);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(LOADER_ID, null, this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new RssFeedListAdapter(new ArrayList<RssFeedModel>(),this);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                LoaderManager loaderManager = getLoaderManager();
                loaderManager.restartLoader(LOADER_ID, null, MainActivity.this);

                new Handler().postDelayed(new Runnable() {

                    @Override public void run() {
                        // stop refresh
                        mSwipeLayout.setRefreshing(false);
                    }
                }, 2000);
                Log.i(LOG_TAG, "onRefresh SwipeLayout");
            }
        });
    }

    @Override
    public Loader<List<RssFeedModel>> onCreateLoader(int id, Bundle args) {

        return new RssLoader(this, URL_STRING, args);
    }

    @Override
    public void onLoadFinished(Loader<List<RssFeedModel>> loader, List<RssFeedModel> data) {
        if (data != null) {
            mRecyclerView.setAdapter(new RssFeedListAdapter(data, this));

            if (data.size() == 0)
                Toast.makeText(this, getString(R.string.no_news), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<RssFeedModel>> loader) {
      Log.i(LOG_TAG, "onLoaderReset()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter) {
            showDatePicker();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker(){
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), getString(R.string.datePicker));
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(i, i1, i2);

        Bundle bundle = new Bundle();
        bundle.putLong(ARG_PUB_DATE, calendar.getTimeInMillis());

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(LOADER_ID, bundle, this);
    }

    @Override
    public void rssItemSelected(RssFeedModel item) {
        Uri newsUri = Uri.parse(item.getLink());
        Intent intent = new Intent(Intent.ACTION_VIEW, newsUri);
        startActivity(intent);
    }
}


























