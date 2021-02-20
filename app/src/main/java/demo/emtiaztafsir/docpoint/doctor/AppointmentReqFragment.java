package demo.emtiaztafsir.docpoint.doctor;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import demo.emtiaztafsir.docpoint.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentReqFragment extends Fragment {

    private ListView lv;
    private PendingAppAdapter paAdapter;

    public AppointmentReqFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_appointment_req, container, false);
        lv = root.findViewById(R.id.pen_app_list);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        DoctorActivity parent = (DoctorActivity)getActivity();
        paAdapter = parent.getPaAdapter();
        lv.setAdapter(paAdapter);
    }
}
