package com.taomake.teabuddy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taomake.teabuddy.R;
import com.taomake.teabuddy.prefs.ConfigPref_;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.sharedpreferences.Pref;


/**
 * Created by xiaoganghu on 15/7/2.
 */

@EFragment(R.layout.fragment_tea_tab)
public class ShopTabFragment extends Fragment {
    @Pref
    ConfigPref_ configPref;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View chatView = inflater.inflate(R.layout.fragment_tea_tab, container, false);


        return chatView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        init();
    }
}
