package com.hit.pollscommunity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class PollsActivity extends Activity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference pollsRef = database.getReference("/Polls");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polls);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        final List<Poll> polls = new ArrayList<>();
        final PollAdapter adapter = new PollAdapter(polls);

        Utils.checkForInternetConnection(PollsActivity.this);


        adapter.setPollListener(new PollAdapter.PollListener() {
            @Override
            public void onPollDelete(final int position, View view) {
                pollsRef.child(polls.get(position).getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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
                i.putExtra("poll",polls.get(position));
                startActivity(i);
            }
        });

        recyclerView.setAdapter(adapter);


        pollsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Poll newPoll = dataSnapshot.getValue(Poll.class);
                polls.add(newPoll);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Poll changedPoll = dataSnapshot.getValue(Poll.class);
                for(int i=0;i<polls.size();i++){
                    if(polls.get(i).getKey().equals(dataSnapshot.getKey())){
                        polls.set(i,changedPoll);
                        adapter.notifyItemChanged(i);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for(int i=0;i<polls.size();i++){
                    if(polls.get(i).getKey().equals(dataSnapshot.getKey())){
                        polls.remove(i);
                        adapter.notifyItemRemoved(i);
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
}
