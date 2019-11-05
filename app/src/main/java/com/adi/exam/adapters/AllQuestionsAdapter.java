package com.adi.exam.adapters;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.exam.R;
import com.adi.exam.common.AESEncryptionDecryption;
import com.adi.exam.common.AppSettings;
import com.adi.exam.utils.TraceUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

public class AllQuestionsAdapter extends RecyclerView.Adapter<AllQuestionsAdapter.ContactViewHolder> {

    private JSONArray array = new JSONArray();

    private ImageLoader imageLoader;

    private Context mContext;

    private String PATH = Environment.getExternalStorageDirectory().toString();

    private final String IMGPATH = PATH + "/System/allFiles/";

    public AllQuestionsAdapter(Context context) {

        mContext = context;

        imageLoader = ImageLoader.getInstance();

    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder contactViewHolder, int position) {

        try {
          /*  try {

                contactViewHolder.iv_qimage.setImageDrawable(null);
                ImageLoader.getInstance().clearMemoryCache();
                ImageLoader.getInstance().clearDiskCache();

            } catch (Exception e) {

                TraceUtils.logException(e);

            }*/
            JSONObject jsonObject = array.getJSONObject(position);

            contactViewHolder.tv_qnumber.setText(mContext.getString(R.string.qnumber, jsonObject.optString("sno")));

         /*   String extFileDirPath = IMGPATH;

            File externalFileDir = mContext.getExternalFilesDir(null);

            if (externalFileDir != null) {

                extFileDirPath = externalFileDir.getAbsolutePath() + "/";

            }
            String encPath = IMGPATH + jsonObject.optString("question_name");

            String plnPath = extFileDirPath + "question.PNG";

            boolean isValid = decryptCipher(encPath, plnPath);

            if (isValid) {

                ImageLoader.getInstance().displayImage("file://" + plnPath, contactViewHolder.iv_qimage);

            }*/
           // imageLoader.displayImage(AppSettings.getInstance().getPropertyValue("download_img")+ jsonObject.optString("question_name"), contactViewHolder.iv_qimage);
            File file = new File(IMGPATH, jsonObject.optString("question_name"));

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
            contactViewHolder.iv_qimage.clearCache(true);
            contactViewHolder.iv_qimage.clearHistory();
            contactViewHolder.iv_qimage.loadDataWithBaseURL("",data_questions[0], "text/html", "utf-8",null);
        } catch (Exception e) {
            TraceUtils.logException(e);

        }

    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_allquestions_item, viewGroup,
                false);

        return new ContactViewHolder(itemView);
    }

    public int getCount() {
        return array.length();
    }

    public long getItemId(int position) {
        return position + 1;
    }

    public void setItems(JSONArray aArray) {
        this.array = aArray;
    }

    public JSONArray getItems() {
        return this.array;
    }

    public void clear() {

        array = null;

        array = new JSONArray();

    }

    public void release() {

        array = null;

    }

    class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView tv_qnumber;

        WebView iv_qimage;

        ContactViewHolder(View v) {
            super(v);

            tv_qnumber = v.findViewById(R.id.tv_qnumber);

           // iv_qimage = v.findViewById(R.id.iv_qimage);
            iv_qimage = v.findViewById(R.id.iv_qimage);

        }

    }
    private boolean decryptCipher(String localLogoPath, String tmpFilePath) {

        FileInputStream fis = null;

        FileOutputStream fos = null;

        CipherInputStream cis = null;

        try {

            fis = new FileInputStream(localLogoPath);

            fos = new FileOutputStream(tmpFilePath);

            Cipher cipher = Cipher.getInstance("ARC4");

            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec("filepickerapp".getBytes(), "ARC4"));

            cis = new CipherInputStream(fis, cipher);

            int b;

            int chunkSize = 1024;

            byte[] d = new byte[chunkSize];

            while ((b = cis.read(d)) != -1) {

                fos.write(d, 0, b);

            }

            fos.flush();

            fis.close();

            fos.close();

            cis.close();

        } catch (Exception e) {

            TraceUtils.logException(e);

            return false;

        } finally {

            try {
                if (fis != null)
                    fis.close();

                if (fos != null)
                    fos.close();

                if (cis != null)
                    cis.close();

            } catch (Exception e) {

                TraceUtils.logException(e);

            }

        }

        return true;

    }

}