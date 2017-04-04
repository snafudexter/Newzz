package com.bubbles.bhavya.newzz;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Prabh on 4/2/2017.
 */

public class NewsItemFragment extends Fragment implements View.OnClickListener

{

    TextView title, desc, source, time, cat;


    public NewsItemFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.newsitem_frag_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        title = (TextView)view.findViewById(R.id.tv_title);
        time = (TextView)view.findViewById(R.id.tv_time);
        desc = (TextView)view.findViewById(R.id.tv_desc);
        cat = (TextView)view.findViewById(R.id.tv_cat);
        source = (TextView)view.findViewById(R.id.tv_source);

        title.setText(getArguments().getString("title"));
        desc.setText(getArguments().getString("desc"));
        source.setText("Source: " + getArguments().getString("source"));
        time.setText("Time: " + getArguments().getString("time"));
        cat.setText("Category: " + getArguments().getString("cat"));

        view.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(getActivity().getBaseContext(), web.class);
        i.putExtra("url", getArguments().getString("link"));
        startActivity(i);
    }
}
