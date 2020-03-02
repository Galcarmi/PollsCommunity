package com.hit.pollscommunity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class PollDetailsActivity extends Activity {

    private Poll poll;
    private TextView createdAt;
    private TextView creator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_details);

        createdAt = findViewById(R.id.created_at);
        creator = findViewById(R.id.creator_text);

        Intent intent = getIntent();
        poll = (Poll)intent.getSerializableExtra("poll");

        creator.setText(poll.getCreator().getFirstName()+" "+poll.getCreator().getLastName());

        if(poll.getCreatedAt()!=null){
            createdAt.setText(poll.getCreatedAt());
        }
        else{
            createdAt.setText("00/00/0000");
        }
    }
}
