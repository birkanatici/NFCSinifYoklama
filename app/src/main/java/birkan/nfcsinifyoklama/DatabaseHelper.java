package birkan.nfcsinifyoklama;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import birkan.nfcsinifyoklama.Objects.Lesson;
import birkan.nfcsinifyoklama.Objects.Student;

/**
 * Created by birkan on 1.04.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "nfcyoklama";
    private static final int DBVERSION = 6;

    public DatabaseHelper(Context context){
        super(context, DBNAME, null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createLessonTable = "CREATE TABLE `lesson` (\n" +
                "\t`lesson_id`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\t`name`\tTEXT\n" +
                ");";

        String createStudentTable = "CREATE TABLE `student` (\n" +
                "\t`student_no`\tTEXT,\n" +
                "\t`name`\tTEXT,\n" +
                "\t`surname`\tTEXT,\n" +
                "\t`rfid`\tTEXT,\n" +
                "\tPRIMARY KEY(`student_no`)\n" +
                ");";

        String createPollingTable = "CREATE TABLE `polling` (\n" +
                "\t`lesson_id`\tINTEGER,\n" +
                "\t`student_no`\tTEXT,\n" +
                "\t`week_id`\tINTEGER,\n" +
                "\t`datetime`\tTEXT\n" +
                ");";

        String createLessonStudentTable = "CREATE TABLE `lesson_student` (\n" +
                "\t`lesson_id`\tINTEGER,\n" +
                "\t`student_no`\tTEXT\n" +
                ");";

        db.execSQL(createLessonTable);
        db.execSQL(createStudentTable);
        db.execSQL(createPollingTable);
        db.execSQL(createLessonStudentTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String dropLesson = "DROP TABLE IF EXISTS lesson";
        String dropStudent = "DROP TABLE IF EXISTS student";
        String dropPolling = "DROP TABLE IF EXISTS polling";
        String dropLessonStudent = "DROP TABLE IF EXISTS lesson_student";

        db.execSQL(dropLesson);
        db.execSQL(dropStudent);
        db.execSQL(dropPolling);
        db.execSQL(dropLessonStudent);

        onCreate(db);
    }

    public void InsertStudent(Student student){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", student.getName());
        values.put("surname", student.getSurname());
        values.put("student_no", student.getStudent_no());
        values.put("rfid", student.getRfid());

        db.insert("student", null, values);
        db.close();

        Log.d("Öğrenci", student.getName() + " Eklendi.");
    }

    public void InsertLesson(Lesson lesson){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", lesson.getName());

        db.insert("lesson", null, values);
        db.close();

        Log.d("Ders", lesson.getName() + " Eklendi.");
    }

    public void InsertPolling(String student_no, int lesson_id, int week_id){
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String datetime = sdf.format(new Date());


        ContentValues values = new ContentValues();

        values.put("lesson_id", lesson_id);
        values.put("student_no", student_no);
        values.put("week_id", week_id);
        values.put("datetime", datetime);

        db.insert("polling", null, values);
        db.close();
    }

    public int getLastWeek(int lessonId){


        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "Select MAX(week_id) from polling where lesson_id = " + lessonId;
        int lastWeek = 0;

        Cursor cursor = db.rawQuery(sql,null);

        while (cursor.moveToNext()){
            lastWeek = cursor.getInt(0);
        }

        db.close();
        return lastWeek;
    }

    public Student getStudentWithRfid(String rfid){

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT name, student_no, rfid FROM student where rfid = "+rfid;

        Cursor cursor = db.rawQuery(sql,null);

        Student student = new Student();
        student.setStudent_no("0");

        while (cursor.moveToNext()){
            student.setName(cursor.getString(0));
            student.setStudent_no(cursor.getString(1));
            student.setRfid(cursor.getString(2));
        }

        return student;
    }

    public void InsertLessonStudent(String student_no, int lesson_id){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("lesson_id", lesson_id);
        values.put("student_no", student_no);

        db.insertWithOnConflict("lesson_student", null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public List<Student> getAllStudent(){
        SQLiteDatabase db = this.getReadableDatabase();

        List<Student> studentList = new ArrayList<Student>();

        String getAllStudent = "SELECT name, surname, student_no, rfid FROM student order by student_no asc";
        Cursor cursor = db.rawQuery(getAllStudent, null);

        while(cursor.moveToNext()){
            Student s = new Student();

            s.setName(cursor.getString(0));
            s.setSurname(cursor.getString(1));
            s.setStudent_no(cursor.getString(2));
            s.setRfid(cursor.getString(3));

            studentList.add(s);
        }

        db.close();
        cursor.close();

        return studentList;
    }

    public List<Lesson> getAllLesson(){
        SQLiteDatabase db = this.getReadableDatabase();

        List<Lesson> lessonList = new ArrayList<Lesson>();

        String getAllStudent = "SELECT lesson_id, name FROM lesson ORDER BY name";
        Cursor cursor = db.rawQuery(getAllStudent, null);

        while (cursor.moveToNext()){
            Lesson l = new Lesson();

            l.setLesson_id(cursor.getInt(0));
            l.setName(cursor.getString(1));

            lessonList.add(l);
        }

        db.close();
        cursor.close();

        return lessonList;
    }

    public List<Student> getStudentLesson(int lessonId){
        SQLiteDatabase db = this.getReadableDatabase();

        List<Student> studentList = new ArrayList<Student>();

        String getAllStudentLesson = "SELECT s.name, s.surname, s.student_no, s.rfid, l.lesson_id, l.name FROM lesson_student ls " +
                                     "LEFT JOIN student s ON (ls.student_no = s.student_no) " +
                                     "LEFT JOIN lesson l ON (l.lesson_id = ls.lesson_id) WHERE ls.lesson_id = " + lessonId;

        Cursor cursor = db.rawQuery(getAllStudentLesson, null);

        while(cursor.moveToNext()){
            Student s = new Student();

            s.setName(cursor.getString(0));
            s.setSurname(cursor.getString(1));
            s.setStudent_no(cursor.getString(2));
            s.setRfid(cursor.getString(3));

            studentList.add(s);
        }

        db.close();
        cursor.close();

        return studentList;

    }

    public List<Student> getWeekPolling(String lessonId, int week_id){
        SQLiteDatabase db = this.getReadableDatabase();

        List<Student> studentList = new ArrayList<Student>();

        String getAllWeekPolling = "SELECT s.name, s.surname, s.student_no, s.rfid, l.lesson_id, l.name, p.datetime FROM polling p " +
                                   "LEFT JOIN student s ON (s.student_no = p.student_no ) " +
                                   "LEFT JOIN lesson l ON (l.lesson_id = p.lesson_id) WHERE ls.lesson_id = " + lessonId + " AND p.week_id = " + week_id;

        Cursor cursor = db.rawQuery(getAllWeekPolling, null);

        while(cursor.moveToNext()){
            Student s = new Student();

            s.setName(cursor.getString(0));
            s.setSurname(cursor.getString(1));
            s.setStudent_no(cursor.getString(2));
            s.setRfid(cursor.getString(3));

            studentList.add(s);
        }

        db.close();
        cursor.close();

        return studentList;
    }

    public boolean deleteLesson(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        boolean isDelete =  db.delete("lesson", "lesson_id" + "=" + id, null) > 0;
        db.close();

        return isDelete;

    }

    public boolean deletePolling(int weekId, int lessonId){
        SQLiteDatabase db = this.getWritableDatabase();

        boolean isDelete =  db.delete("polling", "week_id" + "=" + weekId +" AND lesson_id = "+lessonId, null) > 0;
        db.close();

        return isDelete;
    }


    public boolean deleteStudent(String student_no){
        SQLiteDatabase db = this.getWritableDatabase();

        boolean isDelete =  db.delete("student", "student_no" + "=" + student_no, null) > 0;
        db.close();

        return isDelete;
    }

    public Lesson getLesson(int lessonId){
        SQLiteDatabase db = this.getReadableDatabase();

        String getLessonSql = "SELECT name from lesson where lesson_id = "+lessonId;

        Cursor cursor = db.rawQuery(getLessonSql, null);
        Lesson lesson = new Lesson();

        while (cursor.moveToNext()){
            lesson.setLesson_id(lessonId);
            lesson.setName(cursor.getString(0));
        }
        db.close();
        return lesson;
    }

    public List<Student> getAllStudentDifference(int lessonId) {
        SQLiteDatabase db = this.getReadableDatabase();

        List<Student> studentList = new ArrayList<Student>();

        String getAllStudent = "SELECT name, surname, student_no, rfid FROM student where student_no not in(select distinct student_no from lesson_student where lesson_id = "+lessonId+")  order by name asc";
        Cursor cursor = db.rawQuery(getAllStudent, null);

        while(cursor.moveToNext()){
            Student s = new Student();

            s.setName(cursor.getString(0));
            s.setSurname(cursor.getString(1));
            s.setStudent_no(cursor.getString(2));
            s.setRfid(cursor.getString(3));

            studentList.add(s);
        }

        db.close();
        cursor.close();

        return studentList;
    }
}
