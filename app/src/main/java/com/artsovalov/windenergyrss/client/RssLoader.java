package com.artsovalov.windenergyrss.client;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.artsovalov.windenergyrss.model.RssFeedModel;
import com.artsovalov.windenergyrss.ui.MainActivity;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class RssLoader extends AsyncTaskLoader<List<RssFeedModel>> {

    private static final String TAG = RssLoader.class.getSimpleName();
    private static final String DATE_PATTERN = "dd MMM yyyy";
    private static final String RESOURCE_XML = "resourceXml";

    private String feedUrl;
    private long filterDate;
    private Context context;
    private InputStream inputStream;

    public RssLoader(Context context, String url, Bundle bundle) {
        super(context);
        this.feedUrl = url;
        this.context = context;

        if( bundle != null)
            filterDate = bundle.getLong(MainActivity.ARG_PUB_DATE);
        else filterDate = 0;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.i(TAG, "method onStartLoading");
    }

    //action in the background thread
    @Override
    public List<RssFeedModel> loadInBackground() {
        List<RssFeedModel> rssFeedModels = new ArrayList<>();
        SharedPreferences mPreferences;
        try {
            URL mUrl = new URL(feedUrl);
            inputStream = mUrl.openConnection().getInputStream();

            String resourceXml = read(inputStream);

            Log.i(TAG, "Resources string: " + resourceXml);

            RssParser parser = new RssParser();
            rssFeedModels = parser.parseFeed(resourceXml);

            //realization of the date filter
            rssFeedModels = applyDateFilter(rssFeedModels);

            Log.i(TAG, rssFeedModels.toString());

            //implementation of the application in off-line mode
            mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(RESOURCE_XML, resourceXml);
            editor.apply();

        } catch (XmlPullParserException | IOException e) {
            Log.e(TAG, "Error, ", e);

            mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String savedXml = mPreferences.getString(RESOURCE_XML, "");
            RssParser parser = new RssParser();
            try {
                rssFeedModels = parser.parseFeed(savedXml);
                rssFeedModels = applyDateFilter(rssFeedModels);

            } catch (XmlPullParserException | IOException e2) {
                e.printStackTrace();
                Log.e(TAG, "Error", e2);
            }
        }finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "close inputStream failed");
            }
        }
        return rssFeedModels;
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
        Log.i(TAG, "onStopLoading()");
    }

    private String read(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = in.read(buffer)) != -1) {
            out .write(buffer, 0, length);
        }
        return new String(out.toByteArray());
    }

    private List<RssFeedModel> applyDateFilter(List<RssFeedModel> rssFeedModels){
        if (filterDate != 0){

            Iterator<RssFeedModel> it = rssFeedModels.iterator();

            while (it.hasNext()){
                RssFeedModel item = it.next();
                Calendar calendar = Calendar.getInstance();
                calendar.clear();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
                try {
                    Date modelDate = simpleDateFormat.parse(item.getDate());

                    calendar.setTimeInMillis(modelDate.getTime());
                    calendar.clear(Calendar.HOUR);
                    calendar.clear(Calendar.MINUTE);
                    calendar.clear(Calendar.SECOND);
                    if (calendar.getTimeInMillis()!= filterDate){
                        it.remove();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return rssFeedModels;
    }
}
