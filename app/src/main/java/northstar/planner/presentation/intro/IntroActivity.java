package northstar.planner.presentation.intro;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import northstar.planner.R;
import northstar.planner.presentation.intro.adapter.MyViewPagerAdapter;

public class IntroActivity
        extends AppCompatActivity {

    private MyViewPagerAdapter myViewPagerAdapter;
    private int[] layouts;
    private TextView[] dots;


    @BindView(R.id.activity_intro_view_pager)
    ViewPager viewPager;

    @BindView(R.id.activity_intro_dots)
    LinearLayout dotsLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);

        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        layouts = new int[]{
                R.layout.activity_intro_slide0,
                R.layout.activity_intro_slide0_1,
                R.layout.activity_intro_slide1,
                R.layout.activity_intro_slide2,
                R.layout.activity_intro_slide3,
                R.layout.activity_intro_slide4,
                R.layout.activity_intro_slide5,
                R.layout.activity_intro_slide6,
                };

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter(layouts, this);
        viewPager.setAdapter(myViewPagerAdapter);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            adjustStatusThemeColor();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void adjustStatusThemeColor() {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.grey));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.colorPrimary));//colorsActive[currentPage]);
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @OnClick(R.id.activity_intro_btn_skip)
    public void onClickSkip() {
        finish();
    }

    @OnClick(R.id.activity_intro_btn_next)
    public void onClick(View v) {
        updatePositionDots(1);
    }

    private void updatePositionDots(int pageOffset) {
        // checking for last page
        // if last page home screen will be launched
        int current = getItem(pageOffset);
        if (current < layouts.length) {
            // move to next screen
            viewPager.setCurrentItem(current);
            addBottomDots(current);
        } else {
            finish();
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }
}
