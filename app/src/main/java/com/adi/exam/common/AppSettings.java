package com.adi.exam.common;

import com.adi.exam.utils.TraceUtils;

import java.util.Properties;

public class AppSettings {

    private static AppSettings settings = null;

    private Properties properties = null;


    public static AppSettings getInstance() {

        if (settings == null)
            settings = new AppSettings();

        return settings;

    }

    private AppSettings() {
        loadProperties();
    }

    private void loadProperties() {

        try {

            properties = new Properties();

           // String BASE_PATH = "https://bsecuresoftechsolutions.com/viswa/";   //Live path
           String BASE_PATH="https://bsecuresoftechsolutions.com/viswa_dev/";  //local path

            //String BASE_PATH = "https://bsecuresoftechsolutions.com/smc_api/";

            properties.setProperty("get_new_questions_new", BASE_PATH + "student/get_new_questions_new");

            properties.setProperty("getquestionpaper_jadvance", BASE_PATH + "jee_advanced/get_question_paper");

            properties.setProperty("studentlogin", BASE_PATH + "student_login");

            properties.setProperty("checkqustionpaper", BASE_PATH + "admin/check_question_paper");

            properties.setProperty("getquestionpaper", BASE_PATH + "admin/get_question_paper");

            properties.setProperty("updatedevice", BASE_PATH + "admin/update_device");

            properties.setProperty("checkversion", BASE_PATH + "admin/check_version");

            properties.setProperty("getmaterial", BASE_PATH + "material/get_material");

            properties.setProperty("checkassignment", BASE_PATH + "assignment/check_assignment");

            properties.setProperty("getassignment", BASE_PATH + "assignment/get_assignment");

            properties.setProperty("uploadfile_admin", BASE_PATH + "admin/uploadfile");

            properties.setProperty("submit_exam_result", BASE_PATH + "admin/submit_exam_result");

            properties.setProperty("submit_assignment_result", BASE_PATH + "admin/submit_assignment_result");

            properties.setProperty("get_student_details", BASE_PATH + "get_student_details");

            properties.setProperty("update_password", BASE_PATH + "update_password");

            properties.setProperty("download_qs", BASE_PATH + "assets/upload/quest_uploads/");

            properties.setProperty("download_img", BASE_PATH + "assets/upload/questions/");

            properties.setProperty("check_network", BASE_PATH + "check_network");

            properties.setProperty("get_new_questions", BASE_PATH + "get_new_questions");

            properties.setProperty("set_student_status", BASE_PATH + "student/set_student_status");

            properties.setProperty("zip_download", BASE_PATH + "assets/upload/zip_questions/");

            properties.setProperty("exam_histroy", BASE_PATH + "analysis?student_id=");

            properties.setProperty("assign_histroy", BASE_PATH + "analysis/assignment?student_id=");

            properties.setProperty("upload_question_papers", BASE_PATH + "admin/upload_question_papers");

            properties.setProperty("rtf_zip_download", BASE_PATH + "assets/upload/rtf_zip/");

            properties.setProperty("question_files", BASE_PATH + "question_files/get_assignmnet_questions");

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    public String getPropertyValue(String key) {

        return properties.getProperty(key, "");

    }

}
