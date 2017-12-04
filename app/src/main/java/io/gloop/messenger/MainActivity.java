package io.gloop.messenger;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.gloop.Gloop;
import io.gloop.messenger.dialogs.ChooseUserDialog;
import io.gloop.messenger.dialogs.DayNightSettingsDialog;
import io.gloop.messenger.model.UserInfo;
import io.gloop.messenger.utils.SharedPreferencesStore;
import io.gloop.permissions.GloopUser;

import static io.gloop.messenger.ListFragment.VIEW_CHATS;
import static io.gloop.messenger.ListFragment.VIEW_CLOSED_TASKS;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

//    private CircleImageView userImage;
//    private TextView username;
    private TextView navHeaderUsername;
    private ViewPager viewPager;
    private CircleImageView navHeaderUserImage;
    private FloatingActionButton floatingActionButton;
//    private LinearLayout header;
    private LinearLayout navHeader;
    private AppBarLayout appBar;


    private GloopUser owner;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Messenger");
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // Load the currently logged in GloopUser of the app.
        this.owner = Gloop.getOwner();
        // Load user info
        userInfo = Gloop.allLocal(UserInfo.class)
                .where()
                .equalsTo("phone", owner.getName())
                .first();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        appBar = (AppBarLayout) findViewById(R.id.appbar);
//        header = (LinearLayout) findViewById(R.id.header);

//        username = (TextView) findViewById(R.id.username);
        View navigationHeader = navigationView.getHeaderView(0);
        navHeaderUsername = (TextView) navigationHeader.findViewById(R.id.nav_header_username);
        navHeaderUserImage = (CircleImageView) navigationHeader.findViewById(R.id.nav_header_user_image);

        navHeader = (LinearLayout) navigationHeader.findViewById(R.id.nav_header_background);


//        userImage = (CircleImageView) findViewById(R.id.user_image);
//        userImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new UserProfileDialog(MainActivity.this, userImage, userInfo);
//            }
//        });

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_menu_item_new);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Context context = view.getContext();
//                Intent intent = new Intent(context, ChatActivity.class);
//                context.startActivity(intent);
                new ChooseUserDialog(MainActivity.this, userInfo);
            }
        });

        AppCompatDelegate.setDefaultNightMode(SharedPreferencesStore.getNightMode());

    }

    public void setFABVisibility(int enable) {
        if (enable == View.INVISIBLE)
            floatingActionButton.hide(true);
        else
            floatingActionButton.show(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_my_tasks:
                viewPager.setCurrentItem(0);
                break;
            case R.id.nav_groups:
                viewPager.setCurrentItem(1);
                break;
            case R.id.nav_user:
//                new UserProfileDialog(MainActivity.this, userImage, userInfo);
                break;
            case R.id.nav_night_mode:
                new DayNightSettingsDialog(MainActivity.this);
                break;
            case R.id.nav_logout:
                logout();
                break;
        }

        item.setChecked(true);
        mDrawerLayout.closeDrawers();
        return true;
    }

    private void logout() {
        Gloop.logout();
        SharedPreferencesStore.clearUser();
        finish();
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        final ListFragment frag1 = ListFragment.newInstance(VIEW_CHATS, userInfo, owner);
        final ContactsFragment frag2 = ContactsFragment.newInstance(VIEW_CLOSED_TASKS, userInfo, owner);
        adapter.addFragment(frag1, "Chats");
        adapter.addFragment(frag2, "Contacts");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                frag1.setupRecyclerView();
                frag2.setupRecyclerView();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        Adapter(FragmentManager fm) {
            super(fm);
        }

        void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                setUserInfo();
//            }
//        }).start();
    }

//    private void setUserInfo() {
//        if (userInfo != null) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Uri imageURL = userInfo.getImageURL();
//                    if (imageURL != null) {
//                        Picasso.with(getApplicationContext())
//                                .load(imageURL)
//                                .into(userImage);
//
//                        Picasso.with(getApplicationContext())
//                                .load(imageURL)
//                                .into(navHeaderUserImage);
//                    } else {
//                        userImage.setVisibility(View.GONE);
//                        navHeaderUserImage.setVisibility(View.GONE);
//                    }
//
//                    if (userInfo.getUserName() == null || userInfo.getUserName().equals("")) {
////                        username.setVisibility(View.GONE);
//                        navHeaderUsername.setVisibility(View.GONE);
//                    } else {
////                        username.setText(userInfo.getUserName());
//                        navHeaderUsername.setText(userInfo.getUserName());
//                    }
//                }
//            });
//        } else {
//            ViewGroup.LayoutParams layoutParams = appBar.getLayoutParams();
//            layoutParams.height = 175;
//            appBar.setLayoutParams(layoutParams);
//            header.setVisibility(View.GONE);
//
//            ViewGroup.LayoutParams layoutParams1 = navHeader.getLayoutParams();
//            layoutParams1.height =25;
//            navHeader.setLayoutParams(layoutParams1);
//
//
//            userImage.setVisibility(View.GONE);
////            username.setVisibility(View.GONE);
//            navHeaderUsername.setVisibility(View.GONE);
//            navHeaderUserImage.setVisibility(View.GONE);
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}