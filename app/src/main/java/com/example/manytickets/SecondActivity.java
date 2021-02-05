package com.example.manytickets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavDeepLinkBuilder;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        final EditText testEditText = findViewById(R.id.testEditText);

        Toolbar home_toolbar = findViewById(R.id.home_toolbar_id);
        setSupportActionBar(home_toolbar);
        home_toolbar.setTitle("Test title");
        home_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainHomeTransition();
            }
        });

        Button second_button = (Button) findViewById(R.id.second_button);
        second_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openSecondPage();
                if (testEditText.getText().toString().equals("")){
                    testEditText.setError("UEIWQUIOEOIU");
                }
                else testEditText.setText("");
            }
        });




    }

    public void toMainHomeTransition(){
        PendingIntent pendingIntent = new NavDeepLinkBuilder(this.getApplicationContext())
                .setGraph(R.navigation.mobile_navigation)
                .setDestination(R.id.navigation_home)
                .createPendingIntent();

        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public void openSecondPage(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
//        return true;
//    }

//    public boolean onOptionsItemSelected(MenuItem item){
//        if (item.getItemId() == R.id.backHome)
//        {
//            //openSecondPage();
////            Intent intent = new Intent(this, MainActivity.class);
////            intent.putExtra("frag", "activityFromHome");
////            startActivity(intent);
//            ProfileFragment notificationsFragment = new ProfileFragment();
//            FragmentManager manager = getSupportFragmentManager();
//            manager.beginTransaction().replace(R.id.container, notificationsFragment, notificationsFragment.getTag()).commit();
//        }
//        return true;
//    }
}