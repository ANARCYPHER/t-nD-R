package com.rifcode.nearheart.Adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.rifcode.nearheart.View.MatchsFragment;
import com.rifcode.nearheart.View.MessagingFragment;
import com.rifcode.nearheart.View.MyProfileFragment;

/*
 * Created by ibra_ on 12/10/2017.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    Context context;

    public SectionsPagerAdapter(FragmentManager fm, Context nContext) {
        super(fm);
        context = nContext;
    }

    @Override
    public Fragment getItem(int position) {

        switch(position){

            case 0:
                MyProfileFragment myProfileFragment = new MyProfileFragment();
                return myProfileFragment;

            case 1:
                MatchsFragment matchesFrag = new MatchsFragment();
                return matchesFrag;

            case 2:
                MessagingFragment messagingFragment = new MessagingFragment();
                return messagingFragment;
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }


}
