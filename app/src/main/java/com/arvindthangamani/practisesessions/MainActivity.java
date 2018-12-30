package com.arvindthangamani.practisesessions;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    long timeWhenStopped = 0;
    private final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 11;
    private boolean isPause;
    private boolean isStop = true;

    private MediaRecorder recorder;
    private String outputFile;
    private long timestamp = 0;
    private Gson gson;
    private List<Recording> recordings;
    RecordingsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final SharedPreferences pref = getApplicationContext().getSharedPreferences("PracticeSessions", 0); // 0 - for private mode
        final SharedPreferences.Editor editor = pref.edit();
        gson = new Gson();
        String json = pref.getString("practicesessions", null);
        if (json == null) {
            recordings = new ArrayList<>();
        } else {
            recordings = gson.fromJson(json, new TypeToken<List<Recording>>() {
            }.getType());
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecordingsAdapter(recordings);
        recyclerView.setAdapter(adapter);

        Toast.makeText(getApplicationContext(), String.valueOf(recordings.size()), Toast.LENGTH_SHORT).show();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
        }

        final Button start = findViewById(R.id.start_button);
        final Button pause = findViewById(R.id.pause_button);
        final Button play = findViewById(R.id.play_button);
        final Chronometer timer = findViewById(R.id.simpleChronometer);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStop) {
                    timer.setBase(SystemClock.elapsedRealtime());
                    timer.start();
                    start.setText("Stop");
                    try {
                        timestamp = System.currentTimeMillis();
                        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + String.valueOf(timestamp) + ".3gp";
                        recorder = new MediaRecorder();
                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                        recorder.setOutputFile(outputFile);

                        recorder.prepare();
                        recorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    isStop = false;
                } else {
                    timer.stop();
                    start.setText("Start");
                    isStop = true;
                    recorder.stop();
                    recorder.release();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater inflater = getLayoutInflater();

                    View inflate = inflater.inflate(R.layout.save_dialog, null);
                    final EditText recordingName = inflate.findViewById(R.id.recording_name);
                    recordingName.setText("Recording " + (recordings.size() + 1));
                    builder.setView(inflate)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    Recording recording = new Recording(timestamp, System.currentTimeMillis() - timestamp,
                                            recordingName.getText().toString(), outputFile);
                                    recordings.add(recording);
                                    String json = gson.toJson(recordings);
                                    editor.putString("practicesessions", json);
                                    editor.apply();
                                    adapter.notifyDataSetChanged();


                                }
                            })
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    timer.setBase(SystemClock.elapsedRealtime());
                                }
                            });


                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();




                }

            }
        });


        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPause) {
                    timer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                    timer.start();
                    pause.setText("Pause");
                    isPause = false;

                } else {
                    timeWhenStopped = timer.getBase() - SystemClock.elapsedRealtime();
                    timer.stop();
                    pause.setText("Resume");
                    isPause = true;
                }


            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(outputFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    // make something
                }
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Recording recording = recordings.get(position);

                MediaPlayer mediaPlayer = new MediaPlayer();

                try {
                    String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + String.valueOf(recording.getStartTimestamp()) + ".3gp";
                    mediaPlayer.setDataSource(file);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (Exception e) {
                    // make something
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }


        }
    }
}
