package demo.emtiaztafsir.docpoint.user;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class UserTabsAdapter extends FragmentPagerAdapter {

    public UserTabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                AppointmentsFragment appFrag = new AppointmentsFragment();
                return appFrag;
            case 1:
                FindDoctorsFragment docFrag = new FindDoctorsFragment();
                return docFrag;
            case 2:
                ChatsFragment chtFrag = new ChatsFragment();
                return chtFrag;
            default:
                 return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Appointments";
            case 1:
                return "Nearby Doctors";
            case 2:
                return "Chats";
            default:
                return null;

        }
    }
}
