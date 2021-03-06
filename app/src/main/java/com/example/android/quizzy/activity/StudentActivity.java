package com.example.android.quizzy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.quizzy.R;
import com.example.android.quizzy.api.DataRepo;
import com.example.android.quizzy.fragment.AwardFragment;
import com.example.android.quizzy.fragment.StudentReports;
import com.example.android.quizzy.fragment.student_quiz_list;
import com.example.android.quizzy.util.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.containerStudent)
    FrameLayout containerStudent;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    FragmentManager manager = getSupportFragmentManager();
    private FragmentTransaction transition;

    public String studentID;
    public String studentName;
    public String teacherUUID;

    public static final String TAG = "StudentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        //// TODO: 12/14/2018 integrate with Auth  
        checkLoginState(); // used to check state of curent user
        try {
            studentID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (Exception e) {
            studentID = "EHefJOONtBO6fU6GVHVpAjHnoa92";
        }
        Intent intent = getIntent();
        studentName = intent.getStringExtra(Constants.STUDENT_NAME_KEY);
        teacherUUID = intent.getStringExtra(Constants.TEACHER_TELEPHONE_NUMBER_KEY);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navView.setNavigationItemSelectedListener(this);

        TextView textView = navView.getHeaderView(0).findViewById(R.id.nav_user_name);
        textView.setText(studentName);
        openQuizzListFragment();

    }

    private void checkLoginState() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            openLoginActivity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            FirebaseAuth.getInstance().signOut();
            getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE).edit().clear().apply();
            openLoginActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_quiz:
                openQuizzListFragment();
                break;
            case R.id.nav_reorts:
                openReports();
                break;
            case R.id.nav_award:
                openAward();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openQuizzListFragment() {
        transition = manager.beginTransaction();
        transition.setCustomAnimations(R.anim.slide_up, 0);
        student_quiz_list teacher = new student_quiz_list();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.STUDENT_UUID, studentID);
        bundle.putString(Constants.STUDENT_NAME, studentName);
        bundle.putString(Constants.STUDENT_Teacher_uuid, teacherUUID);
        teacher.setArguments(bundle);
        transition.replace(R.id.containerStudent, teacher).commit();
    }

    private void openAward() {
        transition = manager.beginTransaction();
        AwardFragment awardFragment = new AwardFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TEACHER_TELEPHONE_NUMBER_KEY, teacherUUID);
        awardFragment.setArguments(bundle);
        transition.replace(R.id.containerStudent, awardFragment).commit();
        transition.addToBackStack(null);
    }

    private void openReports() {
        transition = manager.beginTransaction();
        StudentReports studentReports = new StudentReports();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.STUDENT_NAME, studentName);
        bundle.putString(Constants.STUDENT_Teacher_uuid, teacherUUID);
        bundle.putString(Constants.STUDENT_UUID, studentID);
        studentReports.setArguments(bundle);
        transition.replace(R.id.containerStudent, studentReports).commit();
        transition.addToBackStack(StudentReports.TAG);
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * show toast messages
     *
     * @param studentName
     */
    private void show(String studentName) {
        Toast.makeText(this, studentName, Toast.LENGTH_SHORT).show();
    }

    /**
     * handle on BackPressed if drawer is open it close it
     * otherwise it check if BackStack contains fragement if yes it pop it  (return back)
     * else it back to prev activity
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
            //      super.onBackPressed();
        }
    }


}
