package birkan.nfcsinifyoklama.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import birkan.nfcsinifyoklama.Listeners.LessonTabListener;
import birkan.nfcsinifyoklama.Listeners.StudentTabListener;
import birkan.nfcsinifyoklama.Objects.Lesson;
import birkan.nfcsinifyoklama.Objects.Student;
import birkan.nfcsinifyoklama.R;

/**
 * Created by birkan on 2.04.2017.
 */

public class OgrenciAdapter extends BaseAdapter {

    private List<Student> list;
    private LayoutInflater layoutInflater;
    private Context context;

    public OgrenciAdapter(Context _context, List<Student> _student) {
        this.context = _context;
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        this.list = _student;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View satirView = layoutInflater.inflate(R.layout.ogrenci_list_item, null);

        LinearLayout itemLayout = (LinearLayout) satirView.findViewById(R.id.ogr_item_layout);

        ImageView icon = (ImageView) satirView.findViewById(R.id.ogr_icon);
        TextView name = (TextView) satirView.findViewById(R.id.ogr_name);
        TextView ogr_no = (TextView) satirView.findViewById(R.id.ogr_no);

        Student student = list.get(position);

        itemLayout.setOnTouchListener(new StudentTabListener());   /// her item a touch listener
        itemLayout.setOnLongClickListener(new StudentTabListener());   // her item a long click listener

        if(position%2 == 0)
            satirView.setBackgroundResource(R.color.ders_item_1);
        else
            satirView.setBackgroundResource(R.color.ders_item_2);

        icon.setImageResource(R.drawable.student_icon);
        name.setText(student.getName());
        ogr_no.setText(student.getStudent_no());

        return satirView;
    }
}
