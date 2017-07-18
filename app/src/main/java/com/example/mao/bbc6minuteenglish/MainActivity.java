package com.example.mao.bbc6minuteenglish;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.mao.bbc6minuteenglish.data.BBCContentContract;
import com.example.mao.bbc6minuteenglish.data.BBCContentDbHelper;
import com.example.mao.bbc6minuteenglish.utilities.BBCHtmlUtil;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();

    private BBCContentAdapter mBBCContentAdapter;
    private RecyclerView mContentRecycleView;
    BBCContentDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_content_list);

        mContentRecycleView = (RecyclerView) findViewById(R.id.rv_content_list);

        mBBCContentAdapter = new BBCContentAdapter(this);
        mContentRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mContentRecycleView.setAdapter(mBBCContentAdapter);

        dbHelper = new BBCContentDbHelper(this);
        new TestTask().execute();
    }

    // For test
    public class TestTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... params) {
            Log.v(TAG, "Do in background");
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            try{
                BBCHtmlUtil.updateDocument();
            } catch (Exception e) {
                return null;
            }
            //Log.v(TAG, BBCHtmlUtil.sAllContents.toString());
            Elements contentList = BBCHtmlUtil.getContentsList();

            for (int i = 0; i < 20; i++) {
                Element element = contentList.get(i);
                //Log.v(TAG, element.toString());
                ContentValues contentValues = new ContentValues();
                contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TITLE,
                        BBCHtmlUtil.getTitle(element));
                contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIME,
                        BBCHtmlUtil.getTime(element));
                contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_DESCRIPTION,
                        BBCHtmlUtil.getDescription(element));
                contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_HREF,
                        BBCHtmlUtil.getHref(element));
                database.insert(BBCContentContract.BBC6MinuteEnglishEntry.TABLE_NAME, null, contentValues);
            }
            return dbHelper.getReadableDatabase().query(BBCContentContract.BBC6MinuteEnglishEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            mBBCContentAdapter.swapCursor(cursor);
        }
    }
}
