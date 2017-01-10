package fusion.com.soicalrpgpuzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Brian on 4/8/2016.
 */
class TypeWriter extends TextView {

    private CharSequence mText;
    private int mIndex;
    private long mDelay = 500;//Default 500ms delay
    RelativeLayout action_menu;
    boolean target;
    Context context;


    public TypeWriter(Context context) {
        super(context);
        this.context = context;

    }

    public TypeWriter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Handler mHandler = new Handler();
    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(mText.subSequence(0, mIndex++));
            if(mIndex <= mText.length()) {
                    mHandler.postDelayed(characterAdder, mDelay);
            } else if (target){
                Animation action_menu_anim = new TranslateAnimation(-1000, 0,0, 0);
                action_menu_anim.setDuration(500);
                action_menu.startAnimation(action_menu_anim);
                action_menu.setVisibility(View.VISIBLE);
                action_menu_anim.setAnimationListener(new myAnimationListener());

            }
        }
    };

    private class  myAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {


        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }



    }

    public void animateText(Context context, CharSequence text, RelativeLayout action_menu, boolean target) {
        mText = text;
        mIndex = 0;

        if (target) {
            this.target = true;

            this.context = context;
        } else {
            this.target = false;
        }

        setText("");
        mHandler.removeCallbacks(characterAdder);
        mHandler.postDelayed(characterAdder, mDelay);

        this.action_menu = action_menu;

    }

    public void setCharacterDelay(long millis) {
        mDelay = millis;
    }
}
