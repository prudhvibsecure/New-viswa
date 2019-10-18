package com.adi.exam.fragments;


import androidx.fragment.app.Fragment;

import com.adi.exam.R;

public class ParentFragment extends Fragment {

    public boolean back() {
        return false;
    }

    public String getFragmentName() {
        return getString(R.string.app_name);
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(int stringid, boolean blockMenu);

        void onFragmentInteraction(String title, boolean blockMenu);

    }

    public void onRequestPermissionsResult(int requestCode, boolean permissionState) {

    }

}
