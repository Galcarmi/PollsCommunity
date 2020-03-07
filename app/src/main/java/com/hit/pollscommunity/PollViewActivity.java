package com.hit.pollscommunity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class PollViewActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference pollsRef = database.getReference("/Polls");
    private User loggedUser = LoggedUser.getInstance();
    private PieChart pieChart;
    private Button voteBtn;
    private Poll poll;
    private Spinner spinner;
    private int votePosition;
    private TextView pollName;
    private boolean alreadyVotedFlag=false;


    ////option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.poll_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    ///option menu operations
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_view_details:
                Intent i = new Intent(PollViewActivity.this,PollDetailsActivity.class);
                i.putExtra("poll",poll);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_view_v2);

        pieChart = (PieChart)findViewById(R.id.pieChart);
        voteBtn = findViewById(R.id.vote_button);
        spinner = findViewById(R.id.options_dropdown);
        pollName = findViewById(R.id.poll_name);
        View backgroundImage = findViewById(R.id.layout);
        Drawable background = backgroundImage.getBackground();
        background.setAlpha(95);

        Intent intent = getIntent();
        poll = (Poll)intent.getSerializableExtra("poll");

        pollName.setText(poll.getPollName());

        initSpinner();
        initPieChart(true);

        //check if the user already voted for this poll
        for(int i =0 ; i<poll.getOptions().size();i++){
            for(Vote vote:poll.getOptions().get(i).getVotes()){
                if(vote.getUid().equals(loggedUser.getUid())){
                    disableVoteOption(i);
                    alreadyVotedFlag=true;
                }
            }
        }

        voteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.checkForInternetConnection(PollViewActivity.this)) {
                    return;
                }
                if(spinner.getSelectedItem().toString().equals(getResources().getString(R.string.select_option_text))){
                    Toast.makeText(PollViewActivity.this, getResources().getString(R.string.choose_option_toast),Toast.LENGTH_SHORT).show();
                }
                else{
                    voteBtn.setEnabled(false);
                    voteBtn.setAlpha(0.5f);
                    votePosition=findCurrentIndexOfOption();
                    final Vote newVote = new Vote(loggedUser.getUid(),loggedUser.getFirstName()+" "+loggedUser.getLastName());
                    poll.getOptions().get(votePosition).addVote(newVote);
                    pollsRef.child(poll.getKey()).setValue(poll).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                disableVoteOption(votePosition);
                                initPieChart(false);
                                alreadyVotedFlag=true;
                            }
                            else{
                                Toast.makeText(PollViewActivity.this, getResources().getString(R.string.general_error_text),Toast.LENGTH_SHORT).show();
                                voteBtn.setEnabled(true);
                                voteBtn.setAlpha(1f);
                            }
                        }
                    });
                }
            }
        });

    }

    private ArrayList getData(){
        ArrayList<PieEntry> entries = new ArrayList<>();
        for(Option option : poll.getOptions()){
            if(option.getNumberOfVotes()>0){
                entries.add(new PieEntry(option.getNumberOfVotes(),option.getOptionName()));
            }
        }
        return entries;
    }

    private void initSpinner(){
        ArrayList<String> spinnerOptions = new ArrayList<String>();
        spinnerOptions.add(getResources().getString(R.string.select_option_text));
        for(Option option:poll.getOptions()){
            spinnerOptions.add(option.getOptionName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String text = adapterView.getItemAtPosition(position).toString();
                if(!text.equals(getResources().getString(R.string.select_option_text))){
                    pieChart.setCenterText(getResources().getString(R.string.selected_option_text)+"\n"+text);
                    pieChart.notifyDataSetChanged();
                    pieChart.invalidate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initPieChart(boolean firstTime){
        ArrayList<PieEntry> entries = getData();
        PieDataSet pieDataSet = new PieDataSet(entries,"options");
        PieData pieData = new PieData(pieDataSet);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieData.setValueFormatter(new DecimalRemover());
        pieData.setValueTextSize(spToPx(10f,PollViewActivity.this));
        pieChart.setData(pieData);
        pieChart.animateXY(100, 1000);
        pieChart.setCenterTextSizePixels(spToPx(20f,PollViewActivity.this));
        pieChart.invalidate();
        pieChart.notifyDataSetChanged();

        if(firstTime){
            pieChart.setOnChartValueSelectedListener(this);
            pieChart.getDescription().setEnabled(false);
            pieChart.getLegend().setEnabled(false);
        }
    }

    private int findCurrentIndexOfOption(){
        String selectedOption = spinner.getSelectedItem().toString();
        for(int i=0;i<poll.getOptions().size();i++){
            if(selectedOption.equals(poll.getOptions().get(i).getOptionName())){
                return i;
            }
        }
        return -1;
    }

    private void disableVoteOption(int position){
        spinner.setSelection(position+1);
        spinner.setEnabled(false);
        spinner.setClickable(false);
        pieChart.setCenterText(getResources().getString(R.string.selected_option_text)+"\n"+poll.getOptions().get(position).getOptionName());
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
        voteBtn.setEnabled(false);
        voteBtn.setAlpha(0.5f);
    }

    @Override
    public void onValueSelected(Entry e, Highlight highlight) {
        if(!alreadyVotedFlag){
            String highlightedName="";
            int position;
            for(int i=0;i<poll.getOptions().size();i++){
                if(poll.getOptions().get(i).getNumberOfVotes()==(int)e.getY()){
                    highlightedName = poll.getOptions().get(i).getOptionName();
                    position = i;
                    pieChart.setCenterText(getResources().getString(R.string.you_choose_text) +"\n"+ highlightedName);
                    spinner.setSelection(position+1);
                    break;
                }
            }
        }
    }

    @Override
    public void onNothingSelected() {
    }

    private int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
}
