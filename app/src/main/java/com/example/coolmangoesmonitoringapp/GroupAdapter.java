package com.example.coolmangoesmonitoringapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder>  {


    ArrayList<Groups> groupArrayList;
    Context context;

    public GroupAdapter(Context context, ArrayList<Groups> groupList) {
        this.context = context;
        this.groupArrayList = groupList;
    }

    @Override
    public GroupAdapter.GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false);
        return new GroupAdapter.GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupAdapter.GroupViewHolder holder, int position) {
        Groups group = groupArrayList.get(position);
        // Bind user data to the views in the user_item_layout
        holder.nameTextView.setText(group.name);
        //holder.status.setText(users.status);
       // Picasso.get().load(users.profilePic).into(holder.userimg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostWindo.class);
                intent.putExtra("nameeee",group.getName());
                context.startActivity(intent);

            }
        });
    }



    @Override
    public int getItemCount() {
        return groupArrayList.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public GroupViewHolder( View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
        }

    }


}
