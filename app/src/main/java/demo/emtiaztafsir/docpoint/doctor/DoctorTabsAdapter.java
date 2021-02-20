package demo.emtiaztafsir.docpoint.doctor;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class DoctorTabsAdapter extends FragmentPagerAdapter {

    public DoctorTabsAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                DocAppointmentFragment appFrag = new DocAppointmentFragment();
                return appFrag;
            case 1:
                AppointmentReqFragment docFrag = new AppointmentReqFragment();
                return docFrag;
            case 2:
                DocChatsFragment chtFrag = new DocChatsFragment();
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
                return "My Appointments";
            case 1:
                return "Appointment Requests";
            case 2:
                return "Chats";
            default:
                return null;

        }
    }
}
