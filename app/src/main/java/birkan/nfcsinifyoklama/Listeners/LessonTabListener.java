package birkan.nfcsinifyoklama.Listeners;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import birkan.nfcsinifyoklama.Activity.PollingActivity;
import birkan.nfcsinifyoklama.DatabaseHelper;
import birkan.nfcsinifyoklama.MainActivity;
import birkan.nfcsinifyoklama.Objects.Lesson;
import birkan.nfcsinifyoklama.R;
import birkan.nfcsinifyoklama.Tabs.TabDersler;

/**
 * Created by birkan on 17.04.2017.
 */

public class LessonTabListener  extends Activity implements  View.OnTouchListener, View.OnLongClickListener {

    private Context context;
    private boolean longTouch ;
    private DatabaseHelper db;

    public LessonTabListener(Context _context){
        this.context = _context;
        longTouch = false;
        db = new DatabaseHelper(context);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                longTouch = false;
                Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                animation1.setDuration(1000);
                animation1.setFillBefore(true);
                v.startAnimation(animation1);

                Log.d("LessonTab", "OnTouch ActionDown");
                break;
            case MotionEvent.ACTION_UP:
                if(!longTouch){
                    LinearLayout itemLayout = (LinearLayout) v.findViewById(R.id.ders_item_layout);
                    int lessonId = Integer.parseInt(itemLayout.getTag().toString());

                    Intent pollIntent = new Intent(context, PollingActivity.class);
                    pollIntent.putExtra("lessonId", lessonId);

                    context.startActivity(pollIntent);
                }
                Log.d("LessonTab", "OnTouch ActionUp");
                break;
        }

        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        longTouch = true;
        return false;
    }
}
