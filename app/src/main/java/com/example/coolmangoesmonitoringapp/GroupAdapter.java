package com.example.coolmangoesmonitoringapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GroupAdapter extends Fragment {
    Context context;

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    private View groupFragView;

    private ListView listView;

    private GroupArrayAdapter arrayAdapter;

    private ArrayList<String> listofgroups = new ArrayList<>();

    private DatabaseReference groupRef;
    ArrayList<MsgModelClass> messagesAdpterArrayList;
    public GroupAdapter(Context context, ArrayList<MsgModelClass> messagesAdpterArrayList) {
        this.context = context;
        this.messagesAdpterArrayList = messagesAdpterArrayList;
    }


    public static void setLayoutManager(LinearLayoutManager linearLayoutManager) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        groupFragView = inflater.inflate(R.layout.activity_posts_dashboard, container, false);
        groupRef = FirebaseDatabase.getInstance().getReference().child("group");
        InitializeFields();
        RetrieveAndDisplayGroups();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String currentGroupName = arrayAdapter.getItem(position);
                Intent groupChatIntent = new Intent(getContext(), PostWindo.class);
                groupChatIntent.putExtra("groupName", currentGroupName);
                startActivity(groupChatIntent);

            }
        });
        return groupFragView;
    }

    private void RetrieveAndDisplayGroups() {
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> set = new HashSet<>();
                Iterator iter = snapshot.getChildren().iterator();

                while (iter.hasNext()) {
                    set.add(((DataSnapshot) iter.next()).getKey());
                }
                listofgroups.clear();
                listofgroups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void InitializeFields() {
        listView = groupFragView.findViewById(R.id.groupRecyclerView);
        arrayAdapter = new GroupArrayAdapter(getContext(), R.layout.group_item, listofgroups);
        listView.setAdapter(arrayAdapter);
    }

    private static class GroupViewHolder {
        TextView groupNameTextView;

        GroupViewHolder(View view) {
            groupNameTextView = view.findViewById(R.id.group_name);
        }
    }

    private class GroupArrayAdapter extends ArrayAdapter<String> {
        private LayoutInflater inflater;
        private int resource;

        GroupArrayAdapter(Context context, int resource, ArrayList<String> objects) {
            super(context, resource, objects);
            this.inflater = LayoutInflater.from(context);
            this.resource = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GroupViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(resource, parent, false);
                holder = new GroupViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (GroupViewHolder) convertView.getTag();
            }

            String groupName = getItem(position);
            holder.groupNameTextView.setText(groupName);

            return convertView;
        }
    }
}


/*package com.example.coolmangoesmonitoringapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.checkerframework.common.initializedfields.qual.InitializedFields;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;



public class GroupAdapter extends Fragment {

    FirebaseAuth auth;
    FirebaseDatabase database;

    private View groupFragView;

    private ListView listView;

    private ArrayAdapter<String> arrayAdapter;

    private ArrayList<String> listofgroups = new ArrayList<>();

    private DatabaseReference groupRef;


    public GroupAdapter(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        groupFragView = inflater.inflate(R.layout.activity_posts_dashboard, container, false);
        groupRef = FirebaseDatabase.getInstance().getReference().child("group");
        InitializeFields();
        RetrieveAndDisplayGroups();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String currentGroupName = adapterView.getItemAtPosition(position).toString();
                Intent groupChatIntent = new Intent(getContext(), PostWindo.class);
                groupChatIntent.putExtra("groupName", currentGroupName);
                startActivity(groupChatIntent);

            }
        });
        return groupFragView;
    }

    private void RetrieveAndDisplayGroups() {
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> set = new HashSet<>();
                Iterator iter = snapshot.getChildren().iterator();

                while(iter.hasNext()){

                    set.add(((DataSnapshot)iter.next()).getKey());
                }
                listofgroups.clear();
                listofgroups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void InitializeFields() {

        listView= (ListView) groupFragView.findViewById(R.id.groupRecyclerView);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listofgroups);
        listView.setAdapter(arrayAdapter);
    }
}

/*

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
*/