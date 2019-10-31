package com.adi.exam.database;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    private final static String APP_DATABASE_NAME = "exam_sri.db";

    private final static int APP_DATABASE_VERSION = 1;

    private String CLASS_TABLE = "CREATE TABLE CLASS(class_id INTEGER, branch_name TEXT, year TEXT, course_name TEXT, program_name TEXT, section TEXT, subject Text);";

    private String STUDENT_TABLE = "CREATE TABLE STUDENTS(student_id INTEGER, username TEXT, student_name TEXT, application_no TEXT, password TEXT, roll_no TEXT, class_id TEXT, course_name TEXT, program_name TEXT, section TEXT, parent_phone_no TEXT, year TEXT, status TEXT);";

    private String LESSONS_TABLE = "CREATE TABLE LESSONS(lessons_id INTEGER, program_id INTEGER, year TEXT, subject TEXT, lesson_name TEXT);";

    private String TOPICS_TABLE = "CREATE TABLE TOPICS(topic_id INTEGER, lessons_id INTEGER, year TEXT, subject TEXT, lesson_name TEXT, topic_name TEXT);";

    private String QUESTIONS_TABLE = "CREATE TABLE QUESTIONS(question_id INTEGER, topic_id INTEGER, topic_name TEXT, question_name TEXT, question_name1 TEXT, question_name2 TEXT, question_name3 TEXT, option_a TEXT, option_b TEXT, option_c TEXT, option_d TEXT, answer TEXT, solution1 TEXT, solution2 TEXT, solution3 TEXT, solution4 TEXT, difficulty TEXT, status TEXT,paragraph TEXT,type_id TEXT);";

    private String EXAM_TABLE = "CREATE TABLE EXAM(exam_id INTEGER, exam_name TEXT, course TEXT, subjects TEXT, no_of_questions TEXT, marks_per_question TEXT, negative_marks TEXT, duration TEXT,duration_sec TEXT)";//,from_timestamp TEXT,to_imestamp TEXT

    private String QUESTIONPAPER_TABLE = "CREATE TABLE QUESTIONPAPER(question_paper_id INTEGER, exam_id INTEGER, exam_date TEXT, from_time TEXT, to_time TEXT, subjects TEXT, topicids TEXT,paper TEXT,year TEXT,jeeQs TEXT,zip_file_name TEXT,down_status TEXT)";

    private String STUDENTQUESTIONPAPER_TABLE = "CREATE TABLE STUDENTQUESTIONPAPER(student_question_paper_id INTEGER, question_paper_id INTEGER, subject TEXT, questions TEXT, options TEXT,section TEXT,type_id TEXT,no_of_questions TEXT,questions_size TEXT)";

    private String ASSIGNMENTS_TABLE = "CREATE TABLE ASSIGNMENTS(assignment_id INTEGER, assignment_name TEXT, no_of_questions TEXT, from_time TEXT, to_time TEXT, duration TEXT, class_name TEXT, subject TEXT, lessons TEXT, topics TEXT, questions TEXT,duration_sec TEXT)";

    private String STUDENTEXAMRESULT_TABLE = "CREATE TABLE STUDENTEXAMRESULT(student_exam_result_id LONG, student_id INTEGER, exam_id INTEGER, exam_name TEXT, exam_date TEXT, total_questions TEXT, total_questions_attempted TEXT, no_of_correct_answers TEXT, score TEXT, percentage TEXT, accuracy TEXT, exam_type TEXT)";

    private String STUDENTQUESTIONTIME_TABLE = "CREATE TABLE STUDENTQUESTIONTIME(student_question_time_id INTEGER, student_id INTEGER, exam_id INTEGER, question_no INTEGER, question_id INTEGER, topic_id INTEGER, lesson_id INTEGER, subject TEXT, given_option TEXT, correct_option TEXT, result TEXT, question_time TEXT, no_of_clicks TEXT, marked_for_review TEXT);";

    private String MATERIAL_TABLE = "CREATE TABLE MATERIAL(material_id INTEGER, course TEXT, subject TEXT, lesson TEXT, topic TEXT, material_data_id TEXT, material_original_name TEXT, material_unique_name TEXT)";

    private String COURSES_TABLE = "CREATE TABLE COURSES(courses_id INTEGER, course TEXT, course_name TEXT, course_subjects TEXT)";

    private String ASSIGNMENT_TABLE = "CREATE TABLE ASSIGNMENT(assignment_id INTEGER, assignment_name TEXT, subject TEXT, course TEXT, course_name TEXT, section TEXT, lessons TEXT, topics TEXT, exam_date TEXT, from_time TEXT, to_time TEXT, duration TEXT, no_of_questions TEXT, marks_per_question TEXT, questions TEXT,exam_time TEXT,duration_sec TEXT,zip_file_name TEXT,down_status TEXT)";

    private String ASSIGNMENTRESULTS_TABLE = "CREATE TABLE ASSIGNMENTRESULTS(assignment_result_id INTEGER, assignment_id INTEGER, student_id INTEGER, total_questions TEXT, total_questions_attempted TEXT, no_of_correct_answers TEXT, score TEXT, question_no INTEGER, subject TEXT)";

    private String ASSIGNMENTSTUDENTQUESTIONRESULTS_TABLE = "CREATE TABLE ASSIGNMENTSTUDENTQUESTIONRESULTS(assignment_student_question_time_id INTEGER, student_id INTEGER, question_no INTEGER,question_id INTEGER, assignment_id INTEGER, given_option TEXT, correct_option TEXT, result TEXT,subject TEXT)";

    private final String DOWNLOAD_QUEUE = "CREATE TABLE IF NOT EXISTS DOWNLOADQUEUE(material_data_id INTEGER, material_original_name TEXT, material_unique_name TEXT, downloadurl TEXT, downloadstatus TEXT, savedpath TEXT, mimetype TEXT, errorMsg TEXT);";

    private final String FILES_DATA = "CREATE TABLE IF NOT EXISTS FILESDATA(assignment_id TEXT,exam_id TEXT,filename TEXT,path TEXT);";

    private final String ADMIN_IMAGES_QA = "CREATE TABLE IMAGESQA(image_name TEXT,status TEXT);";

    private final String PARAGRAPS_TABLE = "CREATE TABLE  PARAGRAPS(paragraph_id  TEXT,paragraph TEXT,topic_id TEXT,lesson_id TEXT,question_ids TEXT,status TEXT);";

    private final String ALlQuesTable = "CREATE TABLE  JEEQS(exam_id TEXT,questions TEXT);";

    public Database(Context context) {
        super(context, APP_DATABASE_NAME, null, APP_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CLASS_TABLE);
        db.execSQL(STUDENT_TABLE);
        db.execSQL(LESSONS_TABLE);
        db.execSQL(TOPICS_TABLE);
        db.execSQL(QUESTIONS_TABLE);
        db.execSQL(EXAM_TABLE);
        db.execSQL(QUESTIONPAPER_TABLE);
        db.execSQL(STUDENTQUESTIONPAPER_TABLE);
        db.execSQL(ASSIGNMENTS_TABLE);
        db.execSQL(STUDENTEXAMRESULT_TABLE);
        db.execSQL(STUDENTQUESTIONTIME_TABLE);
        db.execSQL(MATERIAL_TABLE);
        db.execSQL(COURSES_TABLE);
        db.execSQL(DOWNLOAD_QUEUE);
        db.execSQL(ASSIGNMENT_TABLE);
        db.execSQL(ASSIGNMENTRESULTS_TABLE);
        db.execSQL(ASSIGNMENTSTUDENTQUESTIONRESULTS_TABLE);
        db.execSQL(FILES_DATA);
        db.execSQL(ADMIN_IMAGES_QA);
        db.execSQL(PARAGRAPS_TABLE);
        db.execSQL(ALlQuesTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

       /* if (newVersion > oldVersion && newVersion==2) {
            db.execSQL("ALTER TABLE ASSIGNMENTSTUDENTQUESTIONRESULTS ADD COLUMN question_no INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE ASSIGNMENTSTUDENTQUESTIONRESULTS ADD COLUMN subject TEXT");


        }else if (newVersion > oldVersion && newVersion==3){
            db.execSQL("CREATE TABLE IF NOT EXISTS FILESDATA(assignment_id TEXT,exam_id TEXT,filename TEXT,path TEXT);");
        }*/
        onCreate(db);
    }

}
