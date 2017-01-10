package fusion.com.soicalrpgpuzzle;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pandora on 5/28/2016.
 */
public class SampleFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList ;
    private final List<String> mFragmentTitleList;
    private ArrayList<View> tabView;

    private Context context;
    private int prevFragmentPage;
    View v;


    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        mFragmentList = new ArrayList<>();
        mFragmentTitleList = new ArrayList<>();
        tabView = new ArrayList<>();
      //  prevFragmentPage = 2;
    }


    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public void addFrag(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public View getTabView(int position, boolean initialState) {

        if (!initialState) {
            this.v = LayoutInflater.from(context).inflate(R.layout.activity_custom_tab, null);
            tabView.add(v);

            TextView description = (TextView) v.findViewById(R.id.description);
            View divider =  v.findViewById(R.id.divider);

            if (position == 0) {
                divider.setVisibility(View.INVISIBLE);
            } else if (position == 2) {
                description.setText("Battle");
                prevFragmentPage = position;
            }

        } else {
            this.v = tabView.get(position);

            TextView description = (TextView) v.findViewById(R.id.description);

            switch (position) {
                case 0:
                    description.setText("Shop");
                    break;
                case 1:
                    description.setText("Level");
                    break;
                case 2:
                    description.setText("Battle");
                    break;
                case 3:
                    description.setText("Profile");
                    break;
                case 4:
                    description.setText("Record");
                    break;
            }

            description.setVisibility(View.VISIBLE);


            removeDesc();


            prevFragmentPage = position;
        }

        return v;
    }


    private void removeDesc() {
        View mView = this.tabView.get(prevFragmentPage);
        TextView description = (TextView) mView.findViewById(R.id.description);
        description.setVisibility(View.INVISIBLE);
    }

    public int getPrevFragmentPage() {
        return prevFragmentPage;
    }



}
