package com.hit.pollscommunity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddPollActivity extends Activity {

    private Button addOptionBtn;
    private TextView optionNameView;
    private TextView pollNameView;
    private Button addPollBtn;

    private ArrayList<Option> options = new ArrayList<Option>();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference pollsRef = database.getReference("/Polls");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_poll);

        addOptionBtn = findViewById(R.id.add_option);
        optionNameView = findViewById(R.id.already_voted_text);
        pollNameView = findViewById(R.id.poll_name);
        addPollBtn = findViewById(R.id.add_poll);

        addOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String optionName = optionNameView.getText().toString();
                if(!TextUtils.isEmpty(optionName.trim())){
                    options.add(new Option(optionName,new ArrayList<Vote>()));
                    optionNameView.setText("");
                    reRenderOptionsList();
                }else{
                    Toast.makeText(AddPollActivity.this,getResources().getString(R.string.add_option_name_toast),Toast.LENGTH_LONG).show();
                }
            }
        });

        addPollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.checkForInternetConnection(AddPollActivity.this)){
                    addPoll();
                }
            }
        });
    }

    private void addPoll(){
        String pollName = pollNameView.getText().toString();
        if(LoggedUser.getInstance().getUid()==null){
            Toast.makeText(AddPollActivity.this, getResources().getString(R.string.general_error_text),Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pollName.trim())){
            Toast.makeText(AddPollActivity.this, getResources().getString(R.string.add_poll_text_toast),Toast.LENGTH_SHORT).show();
        }else if(options.size()==0){
            Toast.makeText(AddPollActivity.this, getResources().getString(R.string.add_options_request),Toast.LENGTH_SHORT).show();
        }
        else{
            Poll poll = new Poll(pollName,options,LoggedUser.getInstance(),new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            String key = pollsRef.push().getKey();
            poll.setKey(key);
            addPollBtn.setEnabled(false);
            addPollBtn.setAlpha(0.5f);
            pollsRef.child(key).setValue(poll).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        startActivity(new Intent(AddPollActivity.this, MainActivity.class));
                    }
                    else{
                        Toast.makeText(AddPollActivity.this, getResources().getString(R.string.general_error_text),Toast.LENGTH_SHORT).show();
                        addPollBtn.setEnabled(true);
                        addPollBtn.setAlpha(1f);
                    }
                }
            });
        }
    }

    private void reRenderOptionsList(){
        LinearLayout optionListLayout=findViewById(R.id.option_list);
        optionListLayout.removeAllViews();
        for(int i=0;i<this.options.size();i++){
            Button newItem=new Button(AddPollActivity.this);

            LinearLayout.LayoutParams layoutParms=new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParms.setMarginEnd((int) (50 / Resources.getSystem().getDisplayMetrics().density));
            newItem.setLayoutParams(layoutParms);

            newItem.setText(this.options.get(i).getOptionName());
            newItem.setId(i);

            newItem.setBackground(getResources().getDrawable(R.drawable.button_shape_option));
            newItem.setTextColor(Color.WHITE);


            newItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id=v.getId();
                    options.remove(id);
                    reRenderOptionsList();
                }
            });

            optionListLayout.addView(newItem);
        }
    }
}
