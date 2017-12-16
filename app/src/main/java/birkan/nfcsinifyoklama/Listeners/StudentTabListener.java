package birkan.nfcsinifyoklama.Listeners;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * Created by birkan on 17.04.2017.
 */

public class StudentTabListener implements  View.OnTouchListener, View.OnLongClickListener {

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                animation1.setDuration(1000);
                animation1.setFillBefore(true);
                v.startAnimation(animation1);

                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return false;
    }

    @Override
    public boolean onLongClick(View v) {

        return false;
    }
}
