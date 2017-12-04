package com.artsovalov.windenergyrss.client;

import android.util.Log;
import android.util.Xml;

import com.artsovalov.windenergyrss.model.RssFeedModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

class RssParser {

    private static final String ITEM = "item";
    private static final String TITLE = "title";
    private static final String LINK = "link";
    private static final String DESCRIPTION = "description";
    private static final String PUB_DATE = "pubDate";
    private static final String TAG = RssParser.class.getSimpleName();

    ArrayList<RssFeedModel> parseFeed(String resourceXml) throws XmlPullParserException, IOException {

        String title = null;
        String link = null;
        String description = null;
        String date = null;
        boolean isItem = false;

        ArrayList<RssFeedModel> items = new ArrayList<>();

            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(new StringReader(resourceXml));
            xmlPullParser.nextTag();

            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if (name == null)
                    continue;

                if (eventType == XmlPullParser.END_TAG) {
                    if (name.equalsIgnoreCase(ITEM)) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if (name.equalsIgnoreCase(ITEM)) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d(TAG, "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase(TITLE)){
                    title = result;
                } else if (name.equalsIgnoreCase(LINK)) {
                    link = result;
                } else if (name.equalsIgnoreCase(DESCRIPTION)) {
                    description = result;
                } else if (name.equalsIgnoreCase(PUB_DATE)){
                    date = result;
                    //Thu, 09 Nov 2017 00:00:00 Z
                    date = date.substring(5, 16);
                }

                if (title != null && link != null && description != null && date != null) {
                    if(isItem) {
                        RssFeedModel item = new RssFeedModel(title, link, description, date);
                        items.add(item);
                    }
                    title = null;
                    link = null;
                    description = null;
                    date = null;
                    isItem = false;
                }
            }
            return items;
    }
}
