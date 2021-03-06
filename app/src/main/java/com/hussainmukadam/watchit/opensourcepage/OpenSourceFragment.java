package com.hussainmukadam.watchit.opensourcepage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hussainmukadam.watchit.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hussain on 27/10/17.
 */

public class OpenSourceFragment extends Fragment {
    @BindView(R.id.toolbar_main)
    Toolbar toolbarMain;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_open_source_ack, container, false);
        ButterKnife.bind(this, view);

        toolbarMain.setTitle("Open Source Libraries");
        toolbarMain.setTitleTextColor(getResources().getColor(R.color.colorWhite));

        toolbarMain.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }
}
