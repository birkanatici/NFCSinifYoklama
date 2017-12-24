package birkan.nfcsinifyoklama.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import birkan.nfcsinifyoklama.Listeners.LessonTabListener;
import birkan.nfcsinifyoklama.MainActivity;
import birkan.nfcsinifyoklama.Objects.Lesson;
import birkan.nfcsinifyoklama.R;

/**
 * Created by birkan on 2.04.2017.
 */

public class DersAdapter extends BaseAdapter {

    private List<Lesson> list;
    LayoutInflater layoutInflater;
    Context context;


    public DersAdapter(Context _context, List<Lesson> _lesson) {

        this.context = _context;
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        this.list = _lesson;
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

        View satirView = layoutInflater.inflate(R.layout.ders_list_item, null);

        ImageView icon = (ImageView) satirView.findViewById(R.id.ders_icon);
        LinearLayout itemLayout = (LinearLayout) satirView.findViewById(R.id.ders_item_layout);

        LessonTabListener tabListener = new LessonTabListener(context);

        itemLayout.setTag(""+list.get(position).getLesson_id());
        itemLayout.setOnTouchListener(tabListener);   /// her item a touch listener
        itemLayout.setOnLongClickListener(tabListener);   // her item a long click listener


        if(position%2 == 0)
            satirView.setBackgroundResource(R.color.ders_item_1);
        else
            satirView.setBackgroundResource(R.color.ders_item_2);

        TextView name = (TextView) satirView.findViewById(R.id.ders_name);

        Lesson ders = list.get(position);
        icon.setImageResource(R.drawable.book_icon);
        name.setText(ders.getName());

        return satirView;
    }
}
