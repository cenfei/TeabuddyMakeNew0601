package com.taomake.teabuddy.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.taomake.teabuddy.R;
import com.taomake.teabuddy.adapter.FragmentAdapter;
import com.taomake.teabuddy.base.MainApp;
import com.taomake.teabuddy.component.MyViewpager;

import org.androidannotations.annotations.EFragment;

import java.util.ArrayList;

@EFragment(R.layout.fragment_tea_tab)
public class DesignTabFragment extends Fragment{

    Resources resources;
    private MyViewpager mPager;
    private ArrayList<Fragment> fragmentsList;
    private ImageView ivBottomLine;


    Fragment home1;
    Fragment home2;


    private LinearLayout ll_container;//小圆点容器
    private int mCurrentIndex = 0;//当前小圆点的位置
    private int[] imgArray = {R.drawable.rounded_cicrle_30white, R.drawable.rounded_cicrle_white};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_tea_tab, null);
        resources = getResources();

//        InitTextView(view);
//        InitWidth(view);
        InitViewPager(view);






        return view;
    }



    private void InitViewPager(View parentView) {
        mPager = (MyViewpager) parentView.findViewById(R.id.vPager);
        fragmentsList = new ArrayList<Fragment>();

        home1 = new HotFragment_();
        home2 = new AllFragment_();

        fragmentsList.add(home1);
        fragmentsList.add(home2);

        mPager.setAdapter(new FragmentAdapter(getChildFragmentManager(), fragmentsList));
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mPager.setCurrentItem(0);




        ll_container = (LinearLayout) parentView.findViewById(R.id.ll_container);
        for (int i = 0; i < 2; i++) {
//            ImageView imageView = new ImageView(getActivity());//获取4个圆点
//            imageView.setImageResource(imgArray[i]);
            ImageView dot = new ImageView(getActivity());
            if (i == mCurrentIndex) {
                dot.setImageResource(R.drawable.rounded_cicrle_white);//设置当前页的圆点
            } else {
                dot.setImageResource(R.drawable.rounded_cicrle_30white);//其余页的圆点
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                    .LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i > 0) {
                params.leftMargin = 10;//设置圆点边距
            }
            dot.setLayoutParams(params);
            ll_container.addView(dot);//将圆点添加到容器中
        }


    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            Log.d("DesignTabFragment", "不可见");

        } else {

            Log.d("DesignTabFragment", "可见");

//            blindDeviceId = configPref.userDeviceMac().get();
//            blindDeviceId = MyStringUtils.macStringToUpper(blindDeviceId);
//            Log.e("blindDeviceId:", blindDeviceId);
//            getTeaInfoByUnionid();
//
//            connectFindDevice();
//
//            Log.d("HotFragment", "当前可见");

        }
    }




    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
//            if(mPager.getCurrentItem()==0&&!HotFragment.canChangePager){
//                mPager.setIsCanScroll(false);
//                ll_container.setVisibility(View.GONE);
//            }else{
//                mPager.setIsCanScroll(true);
//
//                ll_container.setVisibility(View.VISIBLE);
//
//
//
//            }





        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
//            if(mPager.getCurrentItem()==0&&!HotFragment.canChangePager){
//                mPager.setIsCanScroll(false);
//                ll_container.setVisibility(View.GONE);
//            }else {
//                mPager.setIsCanScroll(true);
//
//                ll_container.setVisibility(View.VISIBLE);
//            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

            if(arg0==0){
                positionSelect=mPager.getCurrentItem();
            }

            MainApp mainApp=(MainApp)getActivity().getApplicationContext();
            mainApp.boolchoosePaocha=positionSelect==0?true:false;
                if(mainApp.boolchoosePaocha&&!HotFragment.canChangePager){
                    mPager.setIsCanScroll(false);
                    ll_container.setVisibility(View.GONE);
                }else {
                    mPager.setIsCanScroll(true);

                    ll_container.setVisibility(View.VISIBLE);
                }

        }
    }


    int  positionSelect=0;

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        if (isCanScroll) {
//            //允许滑动则应该调用父类的方法
//            return super.onTouchEvent(ev);
//        } else {
//            //禁止滑动则不做任何操作，直接返回true即可
////            Toast.makeText(context, "what are you 弄啥嘞？", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//    }
//
//    @Override
//    public void scrollTo(int x, int y) {
//
//        /**
//         *必须这样重写，否则会出现画面"一半，一半"的现象，根据Debug来分析可能是虽然onTouchEvent方法返回了true
//         *但是依然在返回true前，viewpager开始调用了scrollTo方法，导致画面"拖出来一点"
//         */
//        if (isCanScroll) {
//            //只有允许滑动的时候才调用滑动的方法
//            super.scrollTo(x, y);
//        }
//    }
//
//    //设置是否允许滑动，true是可以滑动，false是禁止滑动
//    public void setIsCanScroll(boolean isCanScroll) {
//        this.isCanScroll = isCanScroll;
//    }

}
