package com.hit.pollscommunity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class PollsActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference pollsRef = database.getReference("/Polls");
    private RecyclerView recyclerView;
    private PollAdapter globalAdapter;
    private PollAdapter myAdapter;
    private String viewMode = "global_polls";



    ////option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.polls_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    ///option menu operations
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_global_polls:
                viewMode="global_polls";
                setViewMode();
                return true;
            case R.id.menu_item_my_polls:
                viewMode = "my_polls";
                setViewMode();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polls);

        recyclerView = findViewById(R.id.recycler);




        recyclerView.setHasFixedSize(true);
        final List<Poll> globslPolls = new ArrayList<Poll>();
        final List<Poll> myPolls = new ArrayList<Poll>();
        globalAdapter = new PollAdapter(globslPolls);
        myAdapter = new PollAdapter(myPolls);
        Utils.checkForInternetConnection(PollsActivity.this);


        setViewMode();

        PollAdapter.PollListener pollListener = new PollAdapter.PollListener() {
            @Override
            public void onPollDelete(final int position, View view) {
                Poll currentPoll;
                if(viewMode.equals("global_polls")){
                    currentPoll=globslPolls.get(position);
                }
                else{
                    currentPoll=myPolls.get(position);
                }
                pollsRef.child(currentPoll.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(PollsActivity.this, getResources().getString(R.string.general_error_text),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onPollClicked(int position, View view) {
                Intent i = new Intent(PollsActivity.this,PollViewActivity.class);
                if(viewMode.equals("global_polls")){
                    i.putExtra("poll",globslPolls.get(position));
                }
                else{
                    i.putExtra("poll",myPolls.get(position));
                }
                startActivity(i);
            }
        };

        globalAdapter.setPollListener(pollListener);
        myAdapter.setPollListener(pollListener);

        pollsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Poll newPoll = dataSnapshot.getValue(Poll.class);
                globslPolls.add(newPoll);
                globalAdapter.notifyDataSetChanged();

                if(newPoll.getCreator().getUid().equals(LoggedUser.getInstance().getUid())){
                    myPolls.add(newPoll);
                    myAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Poll changedPoll = dataSnapshot.getValue(Poll.class);
                for(int i=0;i<globslPolls.size();i++){
                    if(globslPolls.get(i).getKey().equals(dataSnapshot.getKey())){
                        globslPolls.set(i,changedPoll);
                        globalAdapter.notifyDataSetChanged();
                    }
                }

                if(changedPoll.getCreator().getUid().equals(LoggedUser.getInstance().getUid())){
                    for(int i=0;i<myPolls.size();i++){
                        if(myPolls.get(i).getKey().equals(dataSnapshot.getKey())){
                            myPolls.set(i,changedPoll);
                            myAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Poll deletedPoll = dataSnapshot.getValue(Poll.class);
                for(int i=0;i<globslPolls.size();i++){
                    if(globslPolls.get(i).getKey().equals(dataSnapshot.getKey())){
                        globslPolls.remove(i);
                        globalAdapter.notifyItemRemoved(i);
                    }
                }

                if(deletedPoll.getCreator().getUid().equals(LoggedUser.getInstance().getUid())) {
                    for(int i=0;i<myPolls.size();i++){
                        if(myPolls.get(i).getKey().equals(dataSnapshot.getKey())){
                            myPolls.remove(i);
                            myAdapter.notifyItemRemoved(i);
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setViewMode(){
        if(viewMode.equals("my_polls")){
            recyclerView.setAdapter(myAdapter);

        }
        else{
            recyclerView.setAdapter(globalAdapter);
        }
    }
}
