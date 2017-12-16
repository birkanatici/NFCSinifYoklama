package birkan.nfcsinifyoklama.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import birkan.nfcsinifyoklama.Adapters.OgrenciAdapter;
import birkan.nfcsinifyoklama.Adapters.PollingAdapter;
import birkan.nfcsinifyoklama.DatabaseHelper;
import birkan.nfcsinifyoklama.MainActivity;
import birkan.nfcsinifyoklama.Objects.Lesson;
import birkan.nfcsinifyoklama.Objects.Student;
import birkan.nfcsinifyoklama.R;
import birkan.nfcsinifyoklama.Utility;

import static java.security.AccessController.getContext;

public class PollingActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private CollapsingToolbarLayout toolbarLayout;
    private FloatingActionMenu menu;
    private FloatingActionButton ekleBtn, yoklamaBtn, mailGonderBtn;
    private ListView ogrenciListView;
    private PollingAdapter adapter;
    private List<Student> pollingList;
    private static final int SECOND_ACTIVITY_RESULT_CODE = 0;
    private List<Student> allStudent;
    private ArrayList<String> selectedStudent;
    private String studentArray[];
    private String[] studentNoArray;
    private boolean isSelectedStudent[];
    private int lessonId;


    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private int lastWeek;
    private List<String> checkedList;

    private TextView nfcPopupTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        db = new DatabaseHelper(getApplicationContext());
        lessonId = intent.getIntExtra("lessonId", 0);
        final Lesson lesson = db.getLesson(lessonId);

        setContentView(R.layout.activity_polling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(lesson.getName());

        pollingList = db.getStudentLesson(lessonId);   // derse kayıtlı öğrencileri aldık

        lastWeek =  db.getLastWeek(lessonId);    // son yoklama alınan haftayı aldık
        checkedList = new ArrayList<>();        // derse gelen öğrencileri tutacağımız liste


        allStudent = db.getAllStudentDifference(lessonId);   // derse kayıtlı olmayan öğrenciler

        studentArray = new String[allStudent.size()];
        studentNoArray = new String[allStudent.size()];
        selectedStudent = new ArrayList<>();
        isSelectedStudent = new boolean[allStudent.size()];

        for(int i=0; i< allStudent.size(); i++)
        {
            studentArray[i] = allStudent.get(i).getName();
            studentNoArray[i] = allStudent.get(i).getStudent_no();
        }


        adapter = new PollingAdapter(getApplicationContext(), pollingList, checkedList);  // adaptera koyduk

        ogrenciListView = (ListView) findViewById(R.id.polling_student_list);
        ogrenciListView.setAdapter(adapter); // listview a adapteri mapledik

        menu = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        mailGonderBtn = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        ekleBtn = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);
        yoklamaBtn = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item3);


        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            //nfc not support your device.
            return;
        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        mailGonderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.close(true);

                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.polling_list_layout);
                linearLayout.setDrawingCacheEnabled(true);
                linearLayout.buildDrawingCache(true);
                Bitmap saveBm = Bitmap.createBitmap(linearLayout.getDrawingCache());
                linearLayout.setDrawingCacheEnabled(false);

                int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                           PollingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
                } else {
                    sendMail(saveBm, lesson.getName() +"_Hafta_"+lastWeek);
                }

                Log.d("ActionMenu", "Mail Gönder");
            }
        });

        ekleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.close(true);

               /* Intent intent = new Intent(getApplicationContext(), AddStudentLesson.class);
                startActivityForResult(intent, SECOND_ACTIVITY_RESULT_CODE);*/

               if(allStudent.size() == 0){
                   Toast.makeText(getApplicationContext(), "Tüm öğrenciler bu derse eklendi.", Toast.LENGTH_LONG).show();
                    return;
               }

                final AlertDialog.Builder studentSelector = new AlertDialog.Builder(PollingActivity.this);
                studentSelector.setTitle("Öğrenci Seçiniz..");

                studentSelector.setMultiChoiceItems(studentArray, isSelectedStudent, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked){
                            selectedStudent.add(studentNoArray[which]);
                        }else
                            selectedStudent.remove(studentNoArray[which]);
                    }
                });

                studentSelector.setCancelable(false);
                studentSelector.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // seçilen öğrencileri derse ekle
                        for(int i=0; i<selectedStudent.size(); i++){
                            db.InsertLessonStudent(selectedStudent.get(i), lessonId);
                        }
                        reloadListView();

                    }
                });

                studentSelector.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = studentSelector.create();
                dialog.show();

                Log.d("ActionMenu", "Öğrenci Ekle");
            }
        });


        yoklamaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.close(true);

                    lastWeek++;                             // 1 arttırdık
                    checkedList.clear();
                    reloadListView();

                    AlertDialog.Builder polling = new AlertDialog.Builder(PollingActivity.this);

                    View pollingView = getLayoutInflater().inflate(R.layout.polling_popup, null);


                    nfcPopupTv = (TextView) pollingView.findViewById(R.id.polling_popup_tv);
                    nfcPopupTv.setText(Html.fromHtml("<b>Toplam Öğrenci :</b> "+ pollingList.size() + "<br><b>Gelen Öğrenci :</b> "+ checkedList.size()));
                    ImageView nfc_img = (ImageView) pollingView.findViewById(R.id.nfcAnimView);
                    nfc_img.setBackgroundResource(R.drawable.nfc_animation);
                    final AnimationDrawable nfcAnim = (AnimationDrawable) nfc_img.getBackground();
                    nfcAnim.start();

                    polling.setView(pollingView);
                    polling.setCancelable(false);
                    Utility.STATE = 2;

                    polling.setNegativeButton("İptal Et", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {     // eğer yoklama iptal edilirse db deki kayıtları sil
                            db.deletePolling(lastWeek, lessonId);
                            lastWeek = db.getLastWeek(lessonId);
                            checkedList.clear();
                            reloadListView();

                            dialog.cancel();
                            Utility.STATE = 0;
                            nfcAnim.stop();
                        }
                    });

                    polling.setPositiveButton("Tamamla", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utility.STATE = 0;
                            nfcAnim.stop();
                        }
                    });

                    AlertDialog alertDialog = polling.create();
                    alertDialog.show();

                Log.d("ActionMenu", "Yoklama Al");
            }
        });
    }

    public void reloadListView(){
        pollingList.clear();
        pollingList.addAll(db.getStudentLesson(lessonId));

        adapter = new PollingAdapter(getApplicationContext(), pollingList, checkedList);  // adaptera koyduk
        ogrenciListView.setAdapter(adapter);

        allStudent = db.getAllStudentDifference(lessonId);   // derse kayıtlı olmayan öğrenciler

        studentArray = new String[allStudent.size()];
        studentNoArray = new String[allStudent.size()];
        selectedStudent = new ArrayList<>();
        isSelectedStudent = new boolean[allStudent.size()];

        for(int i=0; i< allStudent.size(); i++)
        {
            studentArray[i] = allStudent.get(i).getName();
            studentNoArray[i] = allStudent.get(i).getStudent_no();
        }

        if(nfcPopupTv != null)
            nfcPopupTv.setText(Html.fromHtml("<b>Toplam Öğrenci :</b> "+ pollingList.size() + "<br><b>Gelen  Öğrenci :</b> "+ checkedList.size()));
    }


    @Override
    protected void onNewIntent(Intent intent){
        getTagInfo(intent);
    }

    private void getTagInfo(Intent intent) {
        String action = intent.getAction();
        Utility func = new Utility();

        String rfid = "";

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            rfid = func.dumpTagData(tag); // kartın rfid'sini aldık

             if(Utility.STATE == 2){   // yoklama al

                 // insert lesson polling

                 Student student = db.getStudentWithRfid(rfid);

                 if(student.getStudent_no().equalsIgnoreCase("0")){
                   Toast.makeText(getApplicationContext(), "Öğrenci Bulunamadı.", Toast.LENGTH_SHORT).show();
                 }else {

                     Toast.makeText(getApplicationContext(), "Hoşgeldin "+ student.getName() , Toast.LENGTH_SHORT).show();
                     db.InsertPolling(student.getStudent_no(), lessonId, lastWeek);
                     if(!checkedList.contains(student.getStudent_no()))
                         checkedList.add(student.getStudent_no());
                 }

                 reloadListView();
                Log.d("YoklamaAl", "Rfid : "+rfid);
            }


        }
    }

    private void sendMail(Bitmap bitmap, String fileName) {
        String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,fileName, fileName);
        Uri bmpUri = Uri.parse(pathofBmp);
        final Intent emailIntent1 = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri);
        emailIntent1.setType("image/png");
        startActivityForResult(emailIntent1, 55);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == 55) {
            if (resultCode == RESULT_OK) {
            //    Toast.makeText(getApplicationContext(), "Mail Başarıtla Gönderildi.", Toast.LENGTH_SHORT).show();
            }else if(resultCode == RESULT_CANCELED){
           //     Toast.makeText(getApplicationContext(), "Mail Gönderilemedi.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                //    callMethod();
                }
                break;

            default:
                break;
        }
    }

}
