package com.example.coolmangoesmonitoringapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    //ArrayList<Users> usersArrayList;
    //private Context context;
    //Context ChatUsers;

    //public UserAdapter(ChatUsers chatUsers, ArrayList<Users> usersList) {
     //   this.usersArrayList = usersArrayList;
    ///   this.ChatUsers =chatUsers;
    //}

    ArrayList<Users> usersArrayList;
    Context context;

    public UserAdapter(Context context, ArrayList<Users> usersList) {
        this.context = context;
        this.usersArrayList = usersList;
    }

    @Override
    public UserViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder( UserViewHolder holder, int position) {
        Users users = usersArrayList.get(position);
        // Bind user data to the views in the user_item_layout
        holder.usernameTextView.setText(users.username);
        holder.status.setText(users.status);
        Picasso.get().load(users.profilePic).into(holder.userimg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, chatwindo.class);
                intent.putExtra("nameeee",users.getUserName());
                intent.putExtra("reciverImg", users.getProfilepic());
                intent.putExtra("uid", users.getUserId());
                context.startActivity(intent);

            }
        });
    }



    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView status;
        CircleImageView userimg;

        public UserViewHolder( View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username);
            userimg = itemView.findViewById(R.id.userimg);
            status = itemView.findViewById(R.id.userstatus);
        }
    }
}
