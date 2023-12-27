package com.example.coolmangoesmonitoringapp;

import static com.example.coolmangoesmonitoringapp.chatwindo.reciverIImg;
import static com.example.coolmangoesmonitoringapp.chatwindo.senderImg;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<MsgModelClass> messagesAdpterArrayList;
    int ITEM_SEND=1;
    int ITEM_RECIVE=2;
    int ITEM_VOICE = 3;

    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer;


    public MessageAdapter(Context context, ArrayList<MsgModelClass> messagesAdpterArrayList) {
        this.context = context;
        this.messagesAdpterArrayList = messagesAdpterArrayList;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof senderVierwHolder) {
            // Release MediaPlayer resources when the ViewHolder is recycled
            releaseMediaPlayer();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new senderVierwHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout, parent, false);
            return new reciverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MsgModelClass messages = messagesAdpterArrayList.get(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context).setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Handle message deletion
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

                return false;
            }
        });

        if (holder instanceof senderVierwHolder) {
            senderVierwHolder viewHolder = (senderVierwHolder) holder;
            viewHolder.msgtxt.setText(messages.getMessage());
            viewHolder.msgtxt.setVisibility(View.VISIBLE);
            Picasso.get().load(senderImg).into(viewHolder.circleImageView);




            if (messages.getImageUri() != null && !messages.getImageUri().isEmpty()) {
                Log.d("Picasso", "Loading image URL: " + messages.getImageUri());
                Picasso.get().load(messages.getImageUri()).into(viewHolder.messageImageView);
                viewHolder.messageImageView.setVisibility(View.VISIBLE); // Ensure visibility
                viewHolder.msgtxt.setVisibility(View.GONE);
                viewHolder.voiceRecordCV.setVisibility(View.GONE);

            }

            else if (messages.getVoiceUrl() != null && !messages.getVoiceUrl().isEmpty()) {

                Log.d("Picasso", "Loading Recording: " + messages.getVoiceUrl());

                // Show the visual representation of the voice message (e.g., waveform)
                // For example, you can load an image or set background drawable to voiceVisualizerImageView
                Picasso.get().load(R.drawable.waveform).into(viewHolder.voiceVisualizerImageView);

                viewHolder.voiceRecordCV.setVisibility(View.VISIBLE);
                viewHolder.messageImageView.setVisibility(View.GONE);
                viewHolder.msgtxt.setVisibility(View.GONE);

                // Set an OnClickListener to play or pause the voice message
                viewHolder.recordIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Implement logic to play or pause the voice message
                        if (isPlaying) {
                            // Pause the playback
                            viewHolder.recordIV.setVisibility(View.GONE);
                            viewHolder.pauseIV.setVisibility(View.VISIBLE);
                            pausePlayback(viewHolder);
                        } else {
                            // Start or resume the playback
                            startOrResumePlayback(viewHolder, messages.getVoiceUrl());
                        }
                    }
                });
            }

            else {
                Log.d("Picasso", "No image URL provided");
                Log.d("Picasso", "No voice URL provided");

                // Hide the message image view if there is no image
                viewHolder.messageImageView.setVisibility(View.GONE);
                viewHolder.voiceRecordCV.setVisibility(View.GONE);

            }


        } else if (holder instanceof reciverViewHolder) {
            reciverViewHolder viewHolder = (reciverViewHolder) holder;
            viewHolder.msgtxt.setText(messages.getMessage());

            // Load the profile image for the receiver
            Picasso.get().load(reciverIImg).into(viewHolder.circleImageView);


            if (messages.getImageUri() != null && !messages.getImageUri().isEmpty()) {
                Log.d("Picasso", "Loading image URL: " + messages.getImageUri());
                Picasso.get().load(messages.getImageUri()).into(viewHolder.messageImageView);
                viewHolder.messageImageView.setVisibility(View.VISIBLE); // Ensure visibility
                viewHolder.msgtxt.setVisibility(View.GONE);
                viewHolder.voiceRecordCV.setVisibility(View.GONE);



            } else if (messages.getVoiceUrl() != null && !messages.getVoiceUrl().isEmpty()) {

                Log.d("Picasso", "Loading Recording: " + messages.getVoiceUrl());

                // Show the visual representation of the voice message (e.g., waveform)
                // For example, you can load an image or set background drawable to voiceVisualizerImageView
                Picasso.get().load(R.drawable.waveform).into(viewHolder.voiceVisualizerImageView);

                viewHolder.voiceRecordCV.setVisibility(View.VISIBLE);
                viewHolder.messageImageView.setVisibility(View.GONE);
                viewHolder.msgtxt.setVisibility(View.GONE);

                // Set an OnClickListener to play or pause the voice message
                viewHolder.recordIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Implement logic to play or pause the voice message
                        if (isPlaying) {
                            // Pause the playback
                            viewHolder.recordIV.setVisibility(View.GONE);
                            viewHolder.pauseIV.setVisibility(View.VISIBLE);
                            pausePlayback(viewHolder);
                        } else {
                            // Start or resume the playback
                            startOrResumePlayback(viewHolder, messages.getVoiceUrl());
                        }
                    }
                });
            }


            else {
                Log.d("Picasso", "No image URL provided");
                Log.d("Picasso", "No voice URL provided");

                // Hide the message image view if there is no image
                viewHolder.messageImageView.setVisibility(View.GONE);
                viewHolder.voiceRecordCV.setVisibility(View.GONE);
            }
        }

        /*else if (holder instanceof voiceViewHolder) {
            voiceViewHolder voiceViewHolder = (voiceViewHolder) holder;
            // Handle voice messages
            if (messages.getVoiceUrl() != null && !messages.getVoiceUrl().isEmpty()) {

                voiceViewHolder.voiceRecordCV.setVisibility(View.VISIBLE);

                Log.d("Picasso", "Loading Recording: " + messages.getVoiceUrl());

                // Show the visual representation of the voice message (e.g., waveform)
                // For example, you can load an image or set background drawable to voiceVisualizerImageView
                Picasso.get().load(R.drawable.waveform).into(voiceViewHolder.voiceVisualizerImageView);

                // Set an OnClickListener to play or pause the voice message
                voiceViewHolder.voiceRecordCV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Implement logic to play or pause the voice message
                        if (isPlaying) {
                            // Pause the playback
                            voiceViewHolder.recordIV.setVisibility(View.GONE);
                            voiceViewHolder.pauseIV.setVisibility(View.VISIBLE);
                            pausePlayback(voiceViewHolder);
                        } else {
                            // Start or resume the playback
                            startOrResumePlayback(voiceViewHolder, messages.getVoiceUrl());
                        }
                    }
                });
            }
        }*/
    }



    private void startOrResumePlayback(senderVierwHolder viewHolder, String voiceUrl) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(voiceUrl);
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        isPlaying = false;
                        // Update UI if needed (e.g., change play/pause button icon)
                    }
                });
            }

            mediaPlayer.start();
            isPlaying = true;
            // Update UI if needed (e.g., change play/pause button icon)

            viewHolder.recordIV.setVisibility(View.GONE);
            viewHolder.pauseIV.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startOrResumePlayback(reciverViewHolder viewHolder, String voiceUrl) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(voiceUrl);
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        isPlaying = false;
                        // Update UI if needed (e.g., change play/pause button icon)
                    }
                });
            }

            mediaPlayer.start();
            isPlaying = true;
            // Update UI if needed (e.g., change play/pause button icon)

            viewHolder.recordIV.setVisibility(View.GONE);
            viewHolder.pauseIV.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pausePlayback(senderVierwHolder viewHolder) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            // Update UI if needed (e.g., change play/pause button icon)

            viewHolder.pauseIV.setVisibility(View.GONE);
            viewHolder.recordIV.setVisibility(View.VISIBLE);
        }
    }

    private void pausePlayback(reciverViewHolder viewHolder) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            // Update UI if needed (e.g., change play/pause button icon)

            viewHolder.pauseIV.setVisibility(View.GONE);
            viewHolder.recordIV.setVisibility(View.VISIBLE);
        }
    }

    // Ensure to release the MediaPlayer resources when the activity/fragment is destroyed
    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
    }



    @Override
    public int getItemCount() {
        return messagesAdpterArrayList.size();
    }

    /*
    @Override
    public int getItemViewType(int position) {
        MsgModelClass messages = messagesAdpterArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderid())) {
            return messages.getImageUri() != null ? ITEM_SEND : ITEM_VOICE;
        } else {
            return messages.getImageUri() != null ? ITEM_RECIVE : ITEM_VOICE;
        }
    }*/
    @Override
    public int getItemViewType(int position) {
        MsgModelClass messages = messagesAdpterArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderid())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECIVE;
        }
    }

    class senderVierwHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        ImageView messageImageView;
        TextView msgtxt;
        CardView voiceRecordCV;
        ImageView pauseIV;
        ImageView recordIV;
        ImageView voiceVisualizerImageView;

        public senderVierwHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profilerggg);
            messageImageView = itemView.findViewById(R.id.message);
            msgtxt = itemView.findViewById(R.id.msgsendertyp);
            voiceRecordCV = itemView.findViewById(R.id.voiceRecordCV);
            pauseIV = itemView.findViewById(R.id.stopBtn);
            recordIV = itemView.findViewById(R.id.recordBtn);
            voiceVisualizerImageView = itemView.findViewById(R.id.voiceVisualizerImageView);
        }
    }


    class reciverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        ImageView messageImageView;

        TextView msgtxt;

        CardView voiceRecordCV;
        ImageView pauseIV;
        ImageView recordIV;
        ImageView voiceVisualizerImageView;

        public reciverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.pro);
            messageImageView = itemView.findViewById(R.id.mess);
            msgtxt = itemView.findViewById(R.id.recivertextset);
            voiceRecordCV = itemView.findViewById(R.id.voiceRecordCV1);
            pauseIV = itemView.findViewById(R.id.stopBtn1);
            recordIV = itemView.findViewById(R.id.recordBtn1);
            voiceVisualizerImageView = itemView.findViewById(R.id.voiceVisualizerImageView1);
        }
    }




}