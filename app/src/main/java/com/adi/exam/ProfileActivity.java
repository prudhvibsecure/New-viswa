package com.adi.exam;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.adi.exam.callbacks.IItemHandler;
import com.adi.exam.common.AppPreferences;
import com.adi.exam.common.AppSettings;
import com.adi.exam.common.NetworkInfoAPI;
import com.adi.exam.dialogfragments.MessageDialog;
import com.adi.exam.tasks.HTTPPostTask;
import com.adi.exam.utils.TraceUtils;

import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity implements IItemHandler {

    private NetworkInfoAPI network = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_profile);

        getSupportActionBar().hide();

        findViewById(R.id.tv_dashboard).setOnClickListener(onClick);

        network = new NetworkInfoAPI();

        network.initialize(this);

        Intent intent = getIntent();

        if(intent != null) {

            try {

                JSONObject studentDetails = new JSONObject(intent.getStringExtra("studentDetails"));

                ((TextView) findViewById(R.id.et_name)).setText(studentDetails.optString("student_name"));

                ((TextView) findViewById(R.id.et_class)).setText(studentDetails.optString("program_name"));

                ((TextView) findViewById(R.id.et_branch)).setText(studentDetails.optString("section"));

            } catch (Exception e) {

                TraceUtils.logException(e);

            }

        }

    }

    OnClickListener onClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.tv_dashboard:
                    launchDashboardActivity(SriVishwa.class);
                    break;

                default:
                    break;
            }

        }
    };

    public void launchActivity() {
        Intent intent = new Intent(this, SriVishwa.class);
        ProfileActivity.this.finish();
        startActivity(intent);
    }

    private void getUserProfile(String userid) {

        try {

            String link = AppPreferences.getInstance(this).getFromStore("serverlink");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Uid", userid);

            HTTPPostTask post = new HTTPPostTask(this, this);
            post.userRequest(getString(R.string.plwait), 1, link + AppSettings.getInstance().getPropertyValue("authentication"), jsonObject.toString());

        } catch (Exception e) {
            TraceUtils.logException(e);
        }

    }

    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void showToast(int text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgressChange(int requestId, Long... values) {

    }

    @Override
    public void onFinish(Object results, int requestType) {

        try {

            switch (requestType) {
                case 1:
                    parseLoginResponse((String) results);
                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            TraceUtils.logException(e);
        }

    }

    @Override
    public void onError(String errorCode, int requestType) {

        switch (requestType) {
            case 1:
                showToast(errorCode);
                break;

            default:
                break;
        }
    }

    public void showokPopUp(String title, String message) {

        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("message", message);
        MessageDialog.newInstance(bundle).show(this.getSupportFragmentManager(), "dialog");

    }

    private void parseLoginResponse(String response) throws Exception {

        if (response != null && response.length() > 0) {

            if(!response.equalsIgnoreCase("0")) {
                AppPreferences.getInstance(this).addToStore("id", response, false);
                launchActivity();
                return;
            }
            showToast(R.string.invalidcred);
        }
    }

    public void launchDashboardActivity(Class<?> cls) {

        Intent intent = new Intent(this, cls);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

        ProfileActivity.this.finish();

    }

}
