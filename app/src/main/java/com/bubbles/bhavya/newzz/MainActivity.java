package com.bubbles.bhavya.newzz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewAnimationUtils;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RSSDownloader.RSSDownloaderNotify, CustomScrollView.OnBottomReachedListener{

    List<NewsItemFragment> newsItemFragmentList;

    boolean bToi, bBBC, bOther;
    boolean bIndia, bWorld, bEnt, bScience, bSports ;

    final int LOADMAX = 6;

    LinearLayout filterBox;
    boolean filterBoxVisible = false;

    SeekBar seekBarDuration;
    TextView tvDuration;
    CheckBox sToi, sBBC, sOther, cIndia, cWorld, cEnt, cScience, cSports;


    int iCounter;

    CustomScrollView customScrollView;
    int permsRequestCode = 200;
    RSSParser rssParser;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        bToi = bBBC = bOther = bIndia = bWorld = bEnt = bScience = bSports = true;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customScrollView = (CustomScrollView)findViewById(R.id.scrollMain);
        customScrollView.setOnBottomReachedListener(this);

        filterBox = (LinearLayout)findViewById(R.id.filterMenuContainer);
        seekBarDuration = (SeekBar)findViewById(R.id.sb_duration);
        tvDuration = (TextView)findViewById(R.id.tvDuration) ;

        sToi = (CheckBox)findViewById(R.id.chkTOI);
        sBBC = (CheckBox)findViewById(R.id.chkBBC);
        sOther = (CheckBox)findViewById(R.id.chkOther);
        cIndia = (CheckBox)findViewById(R.id.chkIndia);
        cWorld = (CheckBox)findViewById(R.id.chkWorld);
        cEnt = (CheckBox)findViewById(R.id.chkEntertainment);
        cSports = (CheckBox)findViewById(R.id.chkSports);
        cScience = (CheckBox)findViewById(R.id.chkScience);


        seekBarDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvDuration.setText(String.valueOf(progress) + " days");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(shouldAskPermission())
        {
            String[] perms = {"android.permission. WRITE_EXTERNAL_STORAGE"};
            requestPermissions(perms, permsRequestCode);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!filterBoxVisible) {
                    enterReveal();
                    filterBoxVisible=true;
                }
                else {
                    exitReveal();
                    filterBoxVisible = false;
                }

                updateFilterSettings();
                iCounter = 0;
                clearScroll();
                addToScrollView();
            }
        });

        RSSDownloader rssDownloader = new RSSDownloader(this);
        rssDownloader.setRssDownloaderNotify(this);
        rssDownloader.begin();
        rssParser = new RSSParser();

        File f = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        Log.i("info", String.valueOf(f.listFiles().length));
        for(int i = 0; i < f.listFiles().length; i++)
        {
            Log.i("info", f.listFiles()[i].getName().toString());
        }

        newsItemFragmentList = new ArrayList<>();
    }

    @Override
    public void onRSSDownloadDone() {
        rssParser.parse(this);
        addToScrollView();

    }

    void addToScrollView()
    {
        DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        Date today = new Date();
        today = addDays(today, seekBarDuration.getProgress());
        int c = 0;
        for(int i = iCounter; i < rssParser.newsItems.size(); i++)
        {

            if(isTrue(rssParser.newsItems.get(i).source)) {

                if (isTrue(rssParser.newsItems.get(i).category)) {
                    Date d = null;
                    try {

                        d = formatter.parse(rssParser.newsItems.get(i).time);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (d.after(today)) {

                        Bundle b = new Bundle();
                        b.putString("time", rssParser.newsItems.get(i).time);
                        b.putString("desc", rssParser.newsItems.get(i).desc);
                        b.putString("link", rssParser.newsItems.get(i).link);
                        b.putString("title", rssParser.newsItems.get(i).title);
                        b.putString("cat", rssParser.newsItems.get(i).category);
                        b.putString("source", rssParser.newsItems.get(i).source);

                        NewsItemFragment newsItemFragment = new NewsItemFragment();
                        newsItemFragment.setArguments(b);
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.fragmentContainer, newsItemFragment, String.valueOf(i)).commit();
                        newsItemFragmentList.add(newsItemFragment);

                        c++;

                        if (c == LOADMAX) {
                            c = 0;
                            break;
                        }
                    }
                }
            }

        }
        iCounter += LOADMAX;
    }

    private boolean shouldAskPermission(){

        return(Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(permsRequestCode){

            case 200:

                boolean writeAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                break;

        }}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBottomReached() {
        addToScrollView();
    }

    void enterReveal() {

        int cx = fab.getMeasuredWidth() / 2 + ((int) fab.getLeft() + getWindow().getDecorView().getWidth());
        int cy = fab.getMeasuredHeight() / 2 + ((int) fab.getTop() + getWindow().getDecorView().getHeight());

        int finalRadius = Math.max(filterBox.getWidth(), filterBox.getHeight()) / 2;

        Animator anim =
                ViewAnimationUtils.createCircularReveal(filterBox, cx, cy, 0, finalRadius);

        filterBox.setBackgroundColor(Color.argb(230, 255, 255, 255));
        filterBox.setVisibility(View.VISIBLE);

        anim.start();
    }

    void exitReveal() {

        int cx = fab.getMeasuredWidth() / 2 + ((int) fab.getLeft() +getWindow().getDecorView().getWidth());
        int cy = fab.getMeasuredHeight() / 2 + ((int) fab.getTop() + getWindow().getDecorView().getHeight());

        int initialRadius = filterBox.getWidth() / 2;

        Animator anim =
                ViewAnimationUtils.createCircularReveal(filterBox, cx, cy, initialRadius, 0);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                filterBox.setBackgroundColor(Color.argb(0, 255, 255, 255));
                filterBox.setVisibility(View.INVISIBLE);

            }
        });

        anim.start();
    }

    void updateFilterSettings()
    {
        bBBC = sBBC.isChecked();
        bToi = sToi.isChecked();
        bOther = sOther.isChecked();
        bIndia = cIndia.isChecked();
        bWorld = cWorld.isChecked();
        bScience = cScience.isChecked();
        bSports = cSports.isChecked();
        bEnt = cEnt.isChecked();

    }

    boolean isTrue(String source)
    {
        if(source == "bbc" && bBBC == true)
        {
            return true;
        }
        else if(source == "toi" && bToi == true)
        {
            return true;
        }
        else if(source == "other" && bOther == true)
        {
            return true;
        }
        else if(source == "India" && bIndia == true)
        {
            return true;
        }
        else if(source == "World" && bWorld == true)
        {
            return true;
        }
        else if(source == "Entertainment" && bEnt == true)
        {
            return true;
        }
        else if(source == "Science" && bScience == true)
        {
            return true;
        }
        else if(source == "Sports" && bSports == true)
        {
            return true;
        }

        return false;
    }

    public static Date addDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);

        return cal.getTime();
    }

    void clearScroll()
    {
        FragmentManager fragmentManager = getFragmentManager();
        for(int i = 0; i < newsItemFragmentList.size(); i++)
        {
            fragmentManager.beginTransaction().remove(newsItemFragmentList.get(i)).commit();
        }
    }
}
