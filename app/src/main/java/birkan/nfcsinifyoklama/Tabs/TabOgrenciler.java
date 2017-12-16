package birkan.nfcsinifyoklama.Tabs;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.ListFragment;

import java.util.ArrayList;
import java.util.List;

import birkan.nfcsinifyoklama.Adapters.DersAdapter;
import birkan.nfcsinifyoklama.Adapters.OgrenciAdapter;
import birkan.nfcsinifyoklama.DatabaseHelper;
import birkan.nfcsinifyoklama.Listeners.LessonTabListener;
import birkan.nfcsinifyoklama.Listeners.StudentTabListener;
import birkan.nfcsinifyoklama.MainActivity;
import birkan.nfcsinifyoklama.Objects.Lesson;
import birkan.nfcsinifyoklama.Objects.Student;
import birkan.nfcsinifyoklama.R;
import birkan.nfcsinifyoklama.Utility;

/**
 * Created by birkan on 3.04.2017.
 */

public class TabOgrenciler extends Fragment {

    private List<Student> ogrenciList;
    private OgrenciAdapter oAdapter;
    private ListView ogrenciListView;
    private DatabaseHelper db;

    private AlertDialog alertDialog;
    private StudentTabListener tabListener;

    public TabOgrenciler(DatabaseHelper db, Context context) {
        this.db = db;
        ogrenciList = db.getAllStudent();
        oAdapter = new OgrenciAdapter(context, ogrenciList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.ogrenciler, container, false);
        ogrenciListView = (ListView) rootView.findViewById(R.id.ogrenciList);
        ogrenciListView.setAdapter(oAdapter);
        ogrenciListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        
        registerForContextMenu(ogrenciListView);

        return rootView;
    }

    public void reloadListView(){
        ogrenciList.clear();
        ogrenciList.addAll(db.getAllStudent());

        ogrenciListView.setAdapter(oAdapter);
    }

    public void showGetRfid(MainActivity context, View v){

        AlertDialog.Builder builderShowGetRfid = new AlertDialog.Builder(context);

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.waiting_rfid, (ViewGroup) v, false);
        builderShowGetRfid.setView(viewInflated);

        Utility.STATE = 3;

        builderShowGetRfid.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog = builderShowGetRfid.show();
    }

    public void showAddStudent(MainActivity context, View v, final String rfid){

        Utility.STATE = 0;   // state 3 deyken buraya girecek tekrar state i sıfırlamak gerek
        if(alertDialog.isShowing() && alertDialog != null)
            alertDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.add_student_poup, (ViewGroup) v, false);

        final EditText name = (EditText) viewInflated.findViewById(R.id.ogr_add_name);
        final EditText ogr_no = (EditText) viewInflated.findViewById(R.id.ogr_add_no);
        final TextView rfidTv = (TextView) viewInflated.findViewById(R.id.ogr_add_rfid);
        builder.setView(viewInflated);

        rfidTv.setText(rfid);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Student s = new Student();
                s.setRfid(rfid);
                s.setName(name.getText().toString());
                s.setStudent_no(ogr_no.getText().toString());

                db.InsertStudent(s);    // veritabanına yaz
                reloadListView();     // listview yenile

                dialog.dismiss();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }


    public void deleteItem(int itemId){
        db.deleteStudent(ogrenciList.get(itemId).getStudent_no());
        reloadListView();
    }
}
