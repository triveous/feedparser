package org.feedreader;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListOfTitlesActivity extends ListActivity {

    private View spinnerContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        new MyAsyncTask().execute();
    }


    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        FeedItem item = (FeedItem) this.getListAdapter().getItem(position);
        Intent showDetailsIntent = new Intent(this.getApplicationContext(),
                ShowFeedDetailsActivity.class);
        showDetailsIntent.putExtra("title", item.getTitle());
        showDetailsIntent.putExtra("content", item.getContent());
        showDetailsIntent.putExtra("contentURL", item.getContentURL());
        startActivity(showDetailsIntent);
        //Toast.makeText(this, "Clicked on "+item.getTitle(), Toast.LENGTH_SHORT).show();
    }


    private class FeedDataAdapter extends BaseAdapter {

        private Context activity;
        private List<FeedItem> feedItems;

        FeedDataAdapter(Activity activity, List<FeedItem> feedItems) {
            this.activity = activity;
            this.feedItems = feedItems;
        }

        @Override
        public int getCount() {
            return feedItems.size();
        }

        @Override
        public FeedItem getItem(int position) {
            return feedItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            TitleView titleView = null;
            if (rowView == null) {
                LayoutInflater inflater = ((Activity) activity).getLayoutInflater();
                rowView = inflater.inflate(R.layout.row, null);
                titleView = new TitleView();
                titleView.title = (TextView) rowView.findViewById(R.id.title_row);
                rowView.setTag(titleView);
            } else {
                titleView = (TitleView) rowView.getTag();
            }

            FeedItem currentFeedItem = feedItems.get(position);
            titleView.title.setText(currentFeedItem.getTitle());

            return rowView;
        }

    }

    protected static class TitleView {
        protected TextView title;
    }

    class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        List<String> titles = new ArrayList<String>();
        List<FeedItem> feedItems = new ArrayList<FeedItem>();

        @Override
        protected Void doInBackground(Void... params) {
            try {
                RSSParser rssParser = new RSSParser(getString(R.string.feed_url));
                feedItems = rssParser.getListOfItemsFromFeed();
                for (FeedItem feedItem : feedItems) {
                    titles.add(feedItem.getTitle());
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            FeedDataAdapter feedDataAdapter = new FeedDataAdapter(ListOfTitlesActivity.this, feedItems);
            setListAdapter(feedDataAdapter);
        }
    }

}
