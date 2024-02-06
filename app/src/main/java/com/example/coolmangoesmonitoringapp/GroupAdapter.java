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


