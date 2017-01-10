package fusion.com.soicalrpgpuzzle;

import android.content.ClipData;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

/**
 * Created by Brian on 5/25/2016.
 */
public class MyDragShadowBuilder extends View.DragShadowBuilder {

    private static Drawable shadow;
    Canvas canvas;
    Point size, touch;
    boolean check = true;
    View view;
    ClipData dragData;

    public MyDragShadowBuilder(ClipData dragData, View v, boolean check) {

        // Stores the View parameter passed to myDragShadowBuilder.
        super(v);
        this.view = v;
        this.check = check;
        this.dragData = dragData;


        shadow = new ColorDrawable(Color.LTGRAY);

    }

    public MyDragShadowBuilder(boolean check) {
        this.check = check;
    }



    @Override
    public void onProvideShadowMetrics(Point size, Point touch) {
        if (check) {
            this.size = size;
            Log.d(GoogleServiceApi.TAG, "Sizebefore:" + size);
            super.onProvideShadowMetrics(size, touch);

        } else {
            Log.d(GoogleServiceApi.TAG, "infalse");
            int width, height;

            // Sets the width of the shadow to half the width of the original View
            width = 0;

            // Sets the height of the shadow to half the height of the original View
            height = 0;

            // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
            // Canvas that the system will provide. As a result, the drag shadow will fill the
            // Canvas.
            shadow.setBounds(0, 0, width, height);

            // Sets the size parameter's width and height values. These get back to the system
            // through the size parameter.
            size.set(width, height);

            // Sets the touch point's position to be in the middle of the drag shadow
            touch.set(width / 2, height / 2);
        }

           /* int width, height;

            // Sets the width of the shadow to half the width of the original View
            width = 0;

            // Sets the height of the shadow to half the height of the original View
            height = 0;

            // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
            // Canvas that the system will provide. As a result, the drag shadow will fill the
            // Canvas.
            shadow.setBounds(0, 0, width, height);

            // Sets the size parameter's width and height values. These get back to the system
            // through the size parameter.
            size.set(width, height);

            // Sets the touch point's position to be in the middle of the drag shadow
            touch.set(width / 2, height / 2);*/





    }

    @Override
    public void onDrawShadow(Canvas canvas) {

        if (check) {
            this.canvas = canvas;
            super.onDrawShadow(canvas);
        } else {
            shadow.draw(canvas);
        }

           // shadow.draw(canvas);


    }

    public void setShadowInvisible() {
        Log.d(GoogleServiceApi.TAG, "Sizeafer:" + size);
        MyDragShadowBuilder newstuff = new MyDragShadowBuilder(false);
        view.startDrag(dragData, newstuff, view, 0);

    }

}
