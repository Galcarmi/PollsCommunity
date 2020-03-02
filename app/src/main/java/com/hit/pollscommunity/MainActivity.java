package com.hit.pollscommunity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;



public class MainActivity extends AppCompatActivity {

    //views of main activity
    private Button navigateToPollsBtn;
    private Button addNewPollBtn;
    private ProgressBar loader;
    private TextView welcomeTitle;
    private boolean canNavigate;

    //firebase////
    private User loggedUser = LoggedUser.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("/Users");

    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authStateListener;


    ////option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    ///option menu operations
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_logout:
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.canNavigate = false;

        navigateToPollsBtn = findViewById(R.id.view_polls_button);
        addNewPollBtn = findViewById(R.id.add_poll_button);
        loader = findViewById(R.id.loader);
        welcomeTitle = findViewById(R.id.welcome_title);

        setLoadingUi(true);

        Utils.checkForInternetConnection(this);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                }
                else{
                    getLoggedUserDetails();
                }
            }
        };

        navigateToPollsBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(canNavigate&&LoggedUser.getInstance().getUid()!=null){
                    startActivity(new Intent(MainActivity.this, PollsActivity.class));
                }
                else{
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.general_error_text_try_again),Toast.LENGTH_SHORT).show();

                }

            }
        });

        addNewPollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canNavigate&&LoggedUser.getInstance().getUid()!=null){
                    startActivity(new Intent(MainActivity.this, AddPollActivity.class));
                }
                else{
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.general_error_text_try_again),Toast.LENGTH_SHORT).show();
                }
            }
        });
        
    }


    ////app life cycle
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }

    private void getLoggedUserDetails(){
        Query userQuery = usersRef.child(this.mAuth.getCurrentUser().getUid()).orderByValue();
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User loggedUserSnapshot = dataSnapshot.getValue(User.class);
                LoggedUser.setUser(loggedUserSnapshot);
                canNavigate = true;
                setLoadingUi(false);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setLoadingUi(boolean loading){
        int visibility;
        if(loading){
            loader.setVisibility(View.VISIBLE);
            welcomeTitle.setVisibility(View.INVISIBLE);
            navigateToPollsBtn.setVisibility(View.INVISIBLE);
            addNewPollBtn.setVisibility(View.INVISIBLE);
        }
        else{
            loader.setVisibility(View.INVISIBLE);
            welcomeTitle.setVisibility(View.VISIBLE);
            navigateToPollsBtn.setVisibility(View.VISIBLE);
            addNewPollBtn.setVisibility(View.VISIBLE);
        }

    }




}


