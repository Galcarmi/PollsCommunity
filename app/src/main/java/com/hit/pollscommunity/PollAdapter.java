package com.hit.pollscommunity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PollAdapter extends RecyclerView.Adapter {

    private PollListener pollListener;
    interface PollListener{
        void onPollClicked(int position , View view);
        void onPollDelete(int position ,View view);
    }

    public void setPollListener(PollListener pollListener){
        this.pollListener = pollListener;
    }

    private List<Poll> polls;

    public PollAdapter(List<Poll> polls){
        this.polls = polls;
    }

    public class PollViewHolder extends RecyclerView.ViewHolder{
        TextView pollname;
        Button viewPollBtn;
        Button deletePollBtn;

        public PollViewHolder(View itemView){
            super(itemView);
            pollname = itemView.findViewById(R.id.poll_name);
            viewPollBtn = itemView.findViewById(R.id.view_button);
            deletePollBtn = itemView.findViewById(R.id.delete_button);

            viewPollBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(pollListener!=null){
                        pollListener.onPollClicked(getAdapterPosition(),view);
                    }
                }
            });

            deletePollBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Poll poll = polls.get(getAdapterPosition());
                    if(LoggedUser.getInstance().getAdmin() || LoggedUser.getInstance().getUid().equals(poll.getCreator().getUid()))
                    {
                        pollListener.onPollDelete(getAdapterPosition(),view);
                    }
                }
            });
        }


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.poll_cell, parent, false);
        PollViewHolder pollViewHolder = new PollViewHolder(view);
        return pollViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        onMyBindViewHolder((PollViewHolder) holder,position);
    }

    private void onMyBindViewHolder(PollViewHolder holder , int position){
        Poll poll = this.polls.get(position);
        holder.pollname.setText(poll.getPollName());
        holder.deletePollBtn.setVisibility(View.INVISIBLE);

        if(LoggedUser.getInstance().getAdmin() || LoggedUser.getInstance().getUid().equals(poll.getCreator().getUid())){
            holder.deletePollBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return this.polls.size();
    }
}
