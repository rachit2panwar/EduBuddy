package com.rachitpanwar.mycredible1.Classes;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rachitpanwar.mycredible1.Fragments.EducationFragment;
import com.rachitpanwar.mycredible1.Fragments.PersonalFragment;
import com.rachitpanwar.mycredible1.Fragments.ProfessionalFragment;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter{
    public SimpleFragmentPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        switch(position)
        {
            case 0 :
                return new EducationFragment();
            case 1 :
                return new ProfessionalFragment();
            case 2:
                return new PersonalFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Education";
            case 1:
                return "Professional";
            case 2:
                return "Personal";
            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        return 3;
    }
}
