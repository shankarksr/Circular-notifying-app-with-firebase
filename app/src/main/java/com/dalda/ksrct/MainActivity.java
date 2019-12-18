package com.dalda.ksrct;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private List<Circular> circularList;
    private Adaptor mAdaptor;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if(isConnectingToInternet(MainActivity.this))
        {
            System.out.print("internet is available");
        }
        else {
            Toast.makeText(MainActivity.this,"Check your Internet connection",Toast.LENGTH_LONG).show();
        }

        FirebaseMessaging.getInstance().subscribeToTopic("ksr");

        swipeRefreshLayout = findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shuffle();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,android.R.color.holo_blue_dark,android.R.color.holo_blue_light);

        recyclerView = findViewById(R.id.recview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        progressBar=findViewById(R.id.pBarmain);
        circularList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();

        Toolbar mtoolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("CT circulars");

        FirebaseDatabase fDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = fDatabase.getReference(Constants.DATABASE_PATH_CIRCULAR);
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               //iterating through all the values in database
               for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                   Circular circulars  = postSnapshot.getValue(Circular.class);
                   circularList.add(circulars);
               }
               Collections.reverse(circularList);
               progressBar.setVisibility(View.GONE);
               mAdaptor = new Adaptor(getApplicationContext(),circularList);
               recyclerView.setAdapter(mAdaptor);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               progressBar.setVisibility(View.INVISIBLE);
               Toast.makeText(MainActivity.this,"Unable to fetch data",Toast.LENGTH_SHORT).show();


           }
       });



    }

    private void shuffle() {
        Collections.reverse(circularList);
        mAdaptor = new Adaptor(getApplicationContext(),circularList);
        recyclerView.setAdapter(mAdaptor);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            sendtoLogin();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menuu,menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdaptor.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.logoutb:
                logout();
                return true;
            case R.id.feedback:
                sendtoFeedback();
                return true;
            case R.id.about:
                showAbout();
                return true;
            default:
                return false;
        }
    }

    private void showAbout() {
        Intent intent=new Intent(MainActivity.this,About.class);
        startActivity(intent);
    }

    private void sendtoFeedback() {
        Intent intent=new Intent(MainActivity.this,feedback.class);
        startActivity(intent);
    }

    private void logout() {
        mAuth.signOut();
        sendtoLogin();
    }

    private void sendtoLogin() {
        Intent i= new Intent(MainActivity.this,login.class);
        startActivity(i);
        finish();
    }
    @Override
    public void onBackPressed()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this,R.style.AlertDia));
        builder.setMessage("Are you sure want to exit?");
        builder.setCancelable(true);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public static boolean isConnectingToInternet(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        } else {
            return false;
        }
    }
}
