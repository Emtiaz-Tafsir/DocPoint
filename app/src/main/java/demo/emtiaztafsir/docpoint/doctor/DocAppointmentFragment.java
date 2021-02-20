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
public class DocAppointmentFragment extends Fragment {

    ListView lv;
    AcceptedAppAdapter acAdapter;

    public DocAppointmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_doc_appointment, container, false);
        lv = root.findViewById(R.id.acc_app_list);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        DoctorActivity parent = (DoctorActivity)getActivity();
        acAdapter = parent.getAcAdapter();
        lv.setAdapter(acAdapter);
    }
}
