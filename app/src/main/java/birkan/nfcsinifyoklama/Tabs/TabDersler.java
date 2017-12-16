package birkan.nfcsinifyoklama.Tabs;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.app.ListActivity;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

import birkan.nfcsinifyoklama.Adapters.DersAdapter;
import birkan.nfcsinifyoklama.Adapters.OgrenciAdapter;
import birkan.nfcsinifyoklama.DatabaseHelper;
import birkan.nfcsinifyoklama.Listeners.LessonTabListener;
import birkan.nfcsinifyoklama.MainActivity;
import birkan.nfcsinifyoklama.Objects.Lesson;
import birkan.nfcsinifyoklama.Objects.Student;
import birkan.nfcsinifyoklama.R;

/**
 * Created by birkan on 3.04.2017.
 */

public class TabDersler extends Fragment{

    private List<Lesson> dersList;
    private DersAdapter dAdapter;
    private ListView dersListView;
    private DatabaseHelper db;
    private LinearLayout itemLayout;

    private Context context;

    public TabDersler(DatabaseHelper _db, Context _context) {
        this.db = _db;
        this.context = _context;
        dersList = db.getAllLesson();
        dAdapter = new DersAdapter(_context, dersList);
    }


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dersler, container, false);
        dersListView = (ListView) rootView.findViewById(R.id.dersList);
        dersListView.setAdapter(dAdapter);

        registerForContextMenu(dersListView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void showAddLesson(MainActivity context, View v){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.add_lesson_popup, (ViewGroup) v, false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.add_lesson_name);
        builder.setView(viewInflated);

        final String[] input_name = {""};

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                input_name[0] = String.valueOf(input.getText());
                Lesson l = new Lesson();
                l.setName(input_name[0]);

                db.InsertLesson(l);    // veritabanına yaz
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

    public void reloadListView(){
        dersList.clear();
        dersList.addAll(db.getAllLesson());

        dersListView.setAdapter(dAdapter);
    }

    public void deleteItem(int itemId){

        db.deleteLesson(dersList.get(itemId).getLesson_id());
        reloadListView();
    }
}
