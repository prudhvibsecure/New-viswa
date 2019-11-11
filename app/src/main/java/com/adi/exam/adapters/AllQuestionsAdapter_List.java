package com.adi.exam.adapters;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.exam.R;
import com.adi.exam.common.AESEncryptionDecryption;
import com.adi.exam.models.QuestonsModel;
import com.adi.exam.utils.TraceUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

public class AllQuestionsAdapter_List extends ArrayAdapter<QuestonsModel> implements View.OnClickListener{

    private ArrayList<QuestonsModel> dataSet;
    Context mContext;
    private String PATH = Environment.getExternalStorageDirectory().toString();
    private final String IMGPATH = PATH + "/System/allFiles/";
    // View lookup cache
    private static class ViewHolder {
        TextView tv_qnumber;

        WebView iv_qimage;
    }

    public AllQuestionsAdapter_List(ArrayList<QuestonsModel> data, Context context) {
        super(context, R.layout.row_allquestions_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

       /* int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModel dataModel=(DataModel)object;

        switch (v.getId())
        {
            case R.id.item_info:
                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }*/
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        QuestonsModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_allquestions_item, parent, false);
            viewHolder.tv_qnumber = convertView.findViewById(R.id.tv_qnumber);
            viewHolder.iv_qimage = convertView.findViewById(R.id.iv_qimage);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        try {
            lastPosition = position;
            File file = new File(IMGPATH, dataModel.getQuestion_name());

            StringBuilder html_content = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    html_content.append(line);
                    html_content.append('\n');
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String my_qs_file = AESEncryptionDecryption.decrypt(html_content.toString());
            String[] data_questions = my_qs_file.split("</html>");
            viewHolder.tv_qnumber.setText(dataModel.getSno());
            viewHolder.iv_qimage.loadDataWithBaseURL("", data_questions[0], "text/html", "utf-8", null);
        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }

}