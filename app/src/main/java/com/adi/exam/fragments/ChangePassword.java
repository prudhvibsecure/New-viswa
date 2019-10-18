package com.adi.exam.fragments;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings.Secure;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.exam.R;
import com.adi.exam.SriVishwa;
import com.adi.exam.adapters.MaterialAdapter;
import com.adi.exam.callbacks.IItemHandler;
import com.adi.exam.common.AppPreferences;
import com.adi.exam.controls.CustomEditText;
import com.adi.exam.controls.CustomTextView;
import com.adi.exam.dialogfragments.MessageDialog;
import com.adi.exam.tasks.HTTPPostTask;
import com.adi.exam.utils.TraceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChangePassword extends ParentFragment implements IItemHandler {

    private View layout;

    private ProgressBar progressBar;

    private TextView tv_content_txt;

    private OnFragmentInteractionListener mListener;

    private SriVishwa mActivity;

    private JSONObject subject;

    private CustomEditText  et_password, et_cnfmpassword;

    private CustomTextView tv_login;

    String oldpassword, newpassword, cnfmpassword;

    public ChangePassword(){

    }

    public static ChangePassword newInstance() {
        return new ChangePassword();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListener = (OnFragmentInteractionListener) context;

        mActivity = (SriVishwa) context;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListener.onFragmentInteraction(mActivity.getString(R.string.Change_Password), false);

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.change_password, container, false);

        progressBar = layout.findViewById(R.id.progress);

        tv_content_txt = layout.findViewById(R.id.tv_content_txt);

        et_password = layout.findViewById(R.id.et_password);

        et_cnfmpassword = layout.findViewById(R.id.et_cnfmpassword);

        tv_login = layout.findViewById(R.id.tv_login);

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newpassword = et_password.getText().toString().trim();
                cnfmpassword = et_cnfmpassword.getText().toString().trim();

                if(newpassword.length() == 0) {

                    showokPopUp("Alert", "Please Enter New Password");
                    return;

                }
                if(cnfmpassword.length() == 0) {

                    showokPopUp("Alert", "Please Confirm Password");
                    return;

                }
                if(!newpassword.equals(cnfmpassword)) {

                    showokPopUp("Alert", "Passwords Did'nt Match");
                    return;

                }

                changePassword();
            }
        });

        return layout;

    }

    private void changePassword() {

        try {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("password", newpassword);

            jsonObject.put("device_id", getDevid());

            HTTPPostTask post = new HTTPPostTask(getActivity(), this);

            post.userRequest(getString(R.string.plwait), 1, "update_password", jsonObject.toString());

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }


    @Override
    public void onFinish(Object results, int requestId) {

        try {

            switch (requestId) {

                case 1:

                    if (results != null) {

                        JSONObject object = new JSONObject(results.toString());
                        if(object.optString("statuscode").equalsIgnoreCase("200")) {

                            Toast.makeText(getActivity(), object.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                            mActivity.onKeyDown(4,null);
                            //showokPopUp("Success", object.optString("statusdescription"));

                        }
                        else {
                            showokPopUp("Failed",object.optString("statusdescription"));
                        }

                    }

                    break;


                default:
                    break;
            }

        } catch (Exception e) {
            TraceUtils.logException(e);
        }

    }

    @Override
    public void onError(String errorCode, int requestId) {

    }

    @Override
    public void onProgressChange(int requestId, Long... values) {

    }

    public void showokPopUp(String title, String message) {

        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("message", message);
        MessageDialog.newInstance(bundle).show(getActivity().getSupportFragmentManager(), "dialog");

    }

    public String getDevid()
    {
        String android_id  = Secure.getString(getContext().getContentResolver(),
                Secure.ANDROID_ID);
        return android_id;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mListener.onFragmentInteraction(R.string.dashboard, true);
    }
}
