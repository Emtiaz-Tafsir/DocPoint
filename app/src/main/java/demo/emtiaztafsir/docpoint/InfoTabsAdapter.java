package demo.emtiaztafsir.docpoint;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class InfoTabsAdapter extends FragmentPagerAdapter {

    public InfoTabsAdapter(FragmentManager fm){
        super(fm);
    }

    public Fragment getItem(int position) {
        switch (position){
            case 0:
                NewUserFragment nuFrag = new NewUserFragment();
                return nuFrag;
            case 1:
                NewDoctorFragment docFrag = new NewDoctorFragment();
                return docFrag;
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Patient";
            case 1:
                return "Doctor";
            default:
                return null;

        }
    }
}
