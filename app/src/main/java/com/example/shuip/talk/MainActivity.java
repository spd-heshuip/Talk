package com.example.shuip.talk;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;

import com.example.shuip.talk.fragment.ChatListFragment;
import com.example.shuip.talk.fragment.ContactFragment;
import com.example.shuip.talk.fragment.MoreFragment;
import com.example.shuip.talk.util.TabUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements ActionBar.TabListener,ViewPager.OnPageChangeListener{

    private List<Tabs> mTabs;
    private ActionBar mActionBar;
    private ViewPager mViewPaper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mViewPaper = (ViewPager)findViewById(R.id.pager);
        InitTabs();
        InitActionBar();
    }

    private void InitTabs(){
        mTabs = new ArrayList<Tabs>(3);

        mTabs.add(new Tabs(R.string.chart, ChatListFragment.class));
        mTabs.add(new Tabs(R.string.contact, ContactFragment.class));
        mTabs.add(new Tabs(R.string.more, MoreFragment.class));
    }

    private void  InitActionBar(){
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        for(Tabs tabs : mTabs){
            ActionBar.Tab tab  = mActionBar.newTab();

//            tab.setText(tabs.getText());
            tab.setCustomView(TabUtils.renderTabView(this,tabs.getText(),0));
            tab.setTabListener(this);
            mActionBar.addTab(tab);
        }

        mViewPaper.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));
        mViewPaper.addOnPageChangeListener(this);

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPaper.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        mViewPaper.setCurrentItem(i);
        mActionBar.selectTab(mActionBar.getTabAt(i));
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    public class Tabs{
        private int text;
        private Class fragment;
        public Tabs(){

        }

        public Tabs(int text,Class fragment){
            this.text = text;
            this.fragment = fragment;
        }

        public int getText() {
            return text;
        }

        public void setText(int text) {
            this.text = text;
        }

        public Class getFragment() {
            return fragment;
        }

        public void setFragment(Class fragment) {
            this.fragment = fragment;
        }
    }

    class  TabFragmentPagerAdapter extends FragmentPagerAdapter
    {


        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            Fragment fragment = null;

            try {
                fragment = (Fragment) mTabs.get(i).getFragment().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }


    }


}
