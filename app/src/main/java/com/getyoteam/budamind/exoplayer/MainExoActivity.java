/*
package com.getyoteam.budamind.exoplayer;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.support.v4.media.session.PlaybackStateCompat;
//import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.getyoteam.budamind.Model.ChapterListModel;
import com.getyoteam.budamind.Model.ChapterListPlayedModel;
import com.getyoteam.budamind.Model.CommanResponseModel;
import com.getyoteam.budamind.Model.CourseDownloadModel;
import com.getyoteam.budamind.Model.CourseListModel;
import com.getyoteam.budamind.Model.DownloadFileModel;
import com.getyoteam.budamind.MyApplication;
import com.getyoteam.budamind.R;
import com.getyoteam.budamind.activity.QuotesActivity;
import com.getyoteam.budamind.activity.ReminderActivity;
import com.getyoteam.budamind.interfaces.ClarityAPI;
import com.getyoteam.budamind.utils.AppDatabase;
import com.getyoteam.budamind.utils.ManagePermissions;
import com.getyoteam.budamind.utils.MyPreferenceManager;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.google.android.exoplayer2.Player;
import com.google.gson.Gson;
import com.mindfulness.greece.model.MeditationStateModel;
import com.white.progressview.CircleProgressView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainExoActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "MainActivity";
    private Boolean animation = false;

    private int PermissionsRequestCode = 123;
    private AppDatabase db;
    private boolean mUserIsSeeking = false;
    private ImageView playStopBtn, gifImageView;
    private SeekBar seekbar;
    private DrawableImageViewTarget imageViewTarget;
    private boolean mBound = false;
    private MusicServiceExo musicService;
    private String streamUrl = "http://uk3.internet-radio.com:8021/listen.pls&t=.m3u";
    private static final int READ_PHONE_STATE_REQUEST_CODE = 22;
    private BroadcastReceiver broadcastReceiver;
    private ObjectAnimator anim;
    private ImageView ivAnimatedImage;
    private ManagePermissions managePermissions;
    private MyPreferenceManager mMyPrefManager;
    private CourseListModel courseModel;
    private MeditationStateModel meditationStateModel;
    private DownloadFileModel downloadFileModelOld;
    private ChapterListModel chapterModel;
    private String audioPath = "";
    private String headerSubTitle = "";
    private String headerTitle = "";
    private String modelName;
    private int fileId;
    private boolean dragging;
    private String customerId = "";
    private int totalMin = 0;
    private int totaDailyMin = 0;
    private int totalWeeklyMin = 0;
    private int totalSession = 0;
    private int longestStreak = 0;
    private int currentStreak = 0;
    private boolean isAPICallingDone = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            MusicServiceExo.MusicBinder mServiceBinder = (MusicServiceExo.MusicBinder) iBinder;
            musicService = mServiceBinder.getService();
            musicService.play(audioPath);
            anim.start();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.exit(0);
        }
    };
    private TextView tvTotalTime, tvTime, tvPlayTitle, tvPlaySubTitle;
    private ProgressBar progressBar;
    private ImageView ivClose, ivDownload, ivForward, ivBackword;
    private long seekTo15000 = 15000;
    private String filename;
    private DownloadFileModel downloadFileModel;
    private RelativeLayout parentLayout;
    private CircleProgressView circleProgressNormal;
    private int downloadId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_exo_test);

        db = AppDatabase.getDatabase(this);
        mMyPrefManager = new MyPreferenceManager(this);
        List<String> list = Arrays.asList(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        );
        managePermissions = new ManagePermissions(this, list, PermissionsRequestCode);

        parentLayout = findViewById(R.id.parentLayout);
        playStopBtn = findViewById(R.id.ivPlay);
        ivClose = findViewById(R.id.ivClose);
        ivDownload = findViewById(R.id.ivDownload);
        progressBar = findViewById(R.id.progress_bar);
        seekbar = findViewById(R.id.song_progressbar);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        tvTime = findViewById(R.id.tvTime);
        tvPlayTitle = findViewById(R.id.tvPlayTitle);
        tvPlaySubTitle = findViewById(R.id.tvPlaySubTitle);
        ivAnimatedImage = findViewById(R.id.ivAnimatedImage);
        ivForward = findViewById(R.id.ivForward);
        ivBackword = findViewById(R.id.ivBackword);
        circleProgressNormal = findViewById(R.id.circleProgressNormal);

        ivClose.setOnClickListener(this);
        ivDownload.setOnClickListener(this);
        ivForward.setOnClickListener(this);
        ivBackword.setOnClickListener(this);

        anim = ObjectAnimator.ofFloat(ivAnimatedImage, View.ROTATION, 0f, 90f);
        anim.setDuration(3000);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setInterpolator(new LinearInterpolator());

        //Retrieve Data from preference into ModelArrayList
        Gson gson = new Gson();
        String jsonChapter = mMyPrefManager.getChapterModel();
        String jsonCourse = mMyPrefManager.getCourseModel();
        String jsonMeditation = mMyPrefManager.getMeditationState();

        chapterModel = gson.fromJson(jsonChapter, ChapterListModel.class);
        courseModel = gson.fromJson(jsonCourse, CourseListModel.class);
        meditationStateModel = gson.fromJson(jsonMeditation, MeditationStateModel.class);
        headerTitle = chapterModel.getCourseName();
        headerSubTitle = chapterModel.getChapterName();
        audioPath = chapterModel.getAudioUrl();
        fileId = chapterModel.getChapterId();
        audioPath = chapterModel.getAudioUrl();
        modelName = "chapterModel";

        tvPlayTitle.setText(headerTitle);
        tvPlaySubTitle.setText("Day\n" + headerSubTitle);

        initializeSeekBar();

        if (courseModel.getFocus()!= null && courseModel.getFocus() == 1) {
            playStopBtn.setColorFilter(getResources().getColor(R.color.color_card_yellow), PorterDuff.Mode.SRC_IN);
            ivForward.setColorFilter(getResources().getColor(R.color.color_card_yellow), PorterDuff.Mode.SRC_IN);
            ivBackword.setColorFilter(getResources().getColor(R.color.color_card_yellow), PorterDuff.Mode.SRC_IN);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.color_card_yellow), PorterDuff.Mode.SRC_IN);
            seekbar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_yellow_drawable));
            seekbar.getThumb().setColorFilter(getResources().getColor(R.color.color_card_yellow), PorterDuff.Mode.SRC_IN);
            ivAnimatedImage.setImageResource(R.drawable.ic_animation_yellow);
        } else if (courseModel.getStress()!= null && courseModel.getStress() == 1) {
            ivForward.setColorFilter(getResources().getColor(R.color.color_card_green), PorterDuff.Mode.SRC_IN);
            ivBackword.setColorFilter(getResources().getColor(R.color.color_card_green), PorterDuff.Mode.SRC_IN);
            playStopBtn.setColorFilter(getResources().getColor(R.color.color_card_green), PorterDuff.Mode.SRC_IN);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.color_card_green), PorterDuff.Mode.SRC_IN);
            seekbar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_green_drawable));
            seekbar.getThumb().setColorFilter(getResources().getColor(R.color.color_card_green), PorterDuff.Mode.SRC_IN);
            ivAnimatedImage.setImageResource(R.drawable.ic_animation_green);
        } else if (courseModel.getSleep()!= null && courseModel.getSleep() == 1) {
            ivForward.setColorFilter(getResources().getColor(R.color.color_card_red), PorterDuff.Mode.SRC_IN);
            ivBackword.setColorFilter(getResources().getColor(R.color.color_card_red), PorterDuff.Mode.SRC_IN);
            playStopBtn.setColorFilter(getResources().getColor(R.color.color_card_red), PorterDuff.Mode.SRC_IN);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.color_card_red), PorterDuff.Mode.SRC_IN);
            seekbar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_red_drawable));
            seekbar.getThumb().setColorFilter(getResources().getColor(R.color.color_card_red), PorterDuff.Mode.SRC_IN);
            ivAnimatedImage.setImageResource(R.drawable.ic_animation_red);
        } else if (courseModel.getHappiness()!= null && courseModel.getHappiness() == 1) {
            ivForward.setColorFilter(getResources().getColor(R.color.color_card_blue), PorterDuff.Mode.SRC_IN);
            ivBackword.setColorFilter(getResources().getColor(R.color.color_card_blue), PorterDuff.Mode.SRC_IN);
            playStopBtn.setColorFilter(getResources().getColor(R.color.color_card_blue), PorterDuff.Mode.SRC_IN);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.color_card_blue), PorterDuff.Mode.SRC_IN);
            seekbar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_blue_drawable));
            seekbar.getThumb().setColorFilter(getResources().getColor(R.color.color_card_blue), PorterDuff.Mode.SRC_IN);
            ivAnimatedImage.setImageResource(R.drawable.ic_animation_blue);
        } else if (courseModel.getAnxiety()!= null && courseModel.getAnxiety() == 1) {
            ivForward.setColorFilter(getResources().getColor(R.color.color_card_yellow), PorterDuff.Mode.SRC_IN);
            ivBackword.setColorFilter(getResources().getColor(R.color.color_card_yellow), PorterDuff.Mode.SRC_IN);
            playStopBtn.setColorFilter(getResources().getColor(R.color.color_card_yellow), PorterDuff.Mode.SRC_IN);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.color_card_yellow), PorterDuff.Mode.SRC_IN);
            seekbar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_yellow_drawable));
            seekbar.getThumb().setColorFilter(getResources().getColor(R.color.color_card_yellow), PorterDuff.Mode.SRC_IN);
            ivAnimatedImage.setImageResource(R.drawable.ic_animation_yellow);
        } else if (courseModel.getGratitute()!= null && courseModel.getGratitute() == 1) {
            ivForward.setColorFilter(getResources().getColor(R.color.color_card_green), PorterDuff.Mode.SRC_IN);
            ivBackword.setColorFilter(getResources().getColor(R.color.color_card_green), PorterDuff.Mode.SRC_IN);
            playStopBtn.setColorFilter(getResources().getColor(R.color.color_card_green), PorterDuff.Mode.SRC_IN);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.color_card_green), PorterDuff.Mode.SRC_IN);
            seekbar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_green_drawable));
            seekbar.getThumb().setColorFilter(getResources().getColor(R.color.color_card_green), PorterDuff.Mode.SRC_IN);
            ivAnimatedImage.setImageResource(R.drawable.ic_animation_green);
        } else if (courseModel.getSelfEsteem()!= null && courseModel.getSelfEsteem() == 1) {
            ivForward.setColorFilter(getResources().getColor(R.color.color_card_red), PorterDuff.Mode.SRC_IN);
            ivBackword.setColorFilter(getResources().getColor(R.color.color_card_red), PorterDuff.Mode.SRC_IN);
            playStopBtn.setColorFilter(getResources().getColor(R.color.color_card_red), PorterDuff.Mode.SRC_IN);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.color_card_red), PorterDuff.Mode.SRC_IN);
            seekbar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_red_drawable));
            seekbar.getThumb().setColorFilter(getResources().getColor(R.color.color_card_red), PorterDuff.Mode.SRC_IN);
            ivAnimatedImage.setImageResource(R.drawable.ic_animation_red);
        } else if (courseModel.getMeditate()!= null && courseModel.getMeditate() == 1) {
            ivForward.setColorFilter(getResources().getColor(R.color.color_card_blue), PorterDuff.Mode.SRC_IN);
            ivBackword.setColorFilter(getResources().getColor(R.color.color_card_blue), PorterDuff.Mode.SRC_IN);
            playStopBtn.setColorFilter(getResources().getColor(R.color.color_card_blue), PorterDuff.Mode.SRC_IN);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.color_card_blue), PorterDuff.Mode.SRC_IN);
            seekbar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_blue_drawable));
            seekbar.getThumb().setColorFilter(getResources().getColor(R.color.color_card_blue), PorterDuff.Mode.SRC_IN);
            ivAnimatedImage.setImageResource(R.drawable.ic_animation_blue);
        } else {
            ivForward.setColorFilter(getResources().getColor(R.color.color_card_blue), PorterDuff.Mode.SRC_IN);
            ivBackword.setColorFilter(getResources().getColor(R.color.color_card_blue), PorterDuff.Mode.SRC_IN);
            playStopBtn.setColorFilter(getResources().getColor(R.color.color_card_blue), PorterDuff.Mode.SRC_IN);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.color_card_blue), PorterDuff.Mode.SRC_IN);
            seekbar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_blue_drawable));
            seekbar.getThumb().setColorFilter(getResources().getColor(R.color.color_card_blue), PorterDuff.Mode.SRC_IN);
            ivAnimatedImage.setImageResource(R.drawable.ic_animation_blue);
        }

        List<MeditationStateModel> meditationStateModelArrayList = db.meditationStateDao().getAll();
        MeditationStateModel meditationStateModelTemp = new MeditationStateModel();
        if (meditationStateModelArrayList.size() > 0)
            meditationStateModelTemp = meditationStateModelArrayList.get(0);

        currentStreak = meditationStateModelTemp.getCurrentStreak();
        longestStreak = meditationStateModelTemp.getLongestStreak();
        totalSession = meditationStateModelTemp.getTotalSessions();
        totalMin = meditationStateModelTemp.getMinuteMeditated();
        totaDailyMin = meditationStateModelTemp.getDailyMinutes();
        totalWeeklyMin = meditationStateModelTemp.getWeeklyMinutes();

        if (audioPath != null && !audioPath.contains("data/")) {
            String fileSting = audioPath.substring(audioPath.lastIndexOf('/') + 1);
            filename = fileSting.replace("%20", " ");
            downloadFileModel = new DownloadFileModel();
            downloadFileModel.setFileId(fileId);
            downloadFileModel.setFileName(filename);
            downloadFileModel.setFileType("audio");
            downloadFileModel.setImageFile("");
            downloadFileModel.setModelName(modelName);
            downloadFileModel.setMinute("");
            downloadFileModel.setSubTitle(chapterModel.getChapterName());
            downloadFileModel.setTitle(chapterModel.getCourseName());
            downloadFileModel.setAudioFilePath(getString(R.string.download_path) + getPackageName() + "/");
        }

        downloadFileModelOld = db.downloadDao().loadDownloadFile(filename);
        if (downloadFileModelOld != null) {
            ivDownload.setImageResource(R.drawable.ic_download_done);
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int playerState = intent.getIntExtra("state", 0);
                if (playerState == PlaybackStateCompat.STATE_BUFFERING) {
//                    Glide.with(MainExoActivity.this).load(R.drawable.not_playing).into(imageViewTarget);
                    playStopBtn.setImageResource(R.drawable.ic_pause_black);
                    progressBar.setVisibility(View.VISIBLE);
                } else if (playerState == PlaybackStateCompat.STATE_PLAYING) {
                    progressBar.setVisibility(View.INVISIBLE);
                    updateProgressBar();
                    playStopBtn.setImageResource(R.drawable.ic_pause_black);
//                    Glide.with(MainExoActivity.this).load(R.drawable.playing).into(imageViewTarget);
                } else if (playerState == PlaybackStateCompat.STATE_PAUSED) {
                    handler.removeCallbacks(updateProgressAction);
                    playStopBtn.setImageResource(R.drawable.ic_play_black);
//                    Glide.with(MainExoActivity.this).load(R.drawable.not_playing).into(imageViewTarget);
                } else if (playerState == PlaybackStateCompat.STATE_STOPPED) {
                    handler.removeCallbacks(updateProgressAction);
                    Log.e("@@@", "STATE_STOPPED");
                    if (!isAPICallingDone) {
                        int min = (int) TimeUnit.MILLISECONDS.toMinutes(musicService.getPlayer().getCurrentPosition());
                        long sec = TimeUnit.MILLISECONDS.toSeconds(musicService.getPlayer().getCurrentPosition()) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                        musicService.getPlayer().getCurrentPosition()
                                )
                        );
                        if (min == 0) {
                            if (sec > 50) {
                                setMeditationState(1, currentStreak, longestStreak, totalSession);
                            }
                        } else {
                            setMeditationState(min, currentStreak, longestStreak, totalSession);
                        }
                    }
                    ChapterListPlayedModel chapterListPlayedModel = new ChapterListPlayedModel();
                    chapterListPlayedModel.setAudioUrl(chapterModel.getAudioUrl());
                    chapterListPlayedModel.setChapterId(chapterModel.getChapterId());
                    chapterListPlayedModel.setChapterName(chapterModel.getChapterName());
                    chapterListPlayedModel.setCourseId(chapterModel.getCourseId());
                    chapterListPlayedModel.setFreePaid(chapterModel.getFreePaid());
                    chapterListPlayedModel.setCourseName(chapterModel.getCourseName());
                    db.chapterPlayedDao().insertAll(chapterListPlayedModel);
                    MyApplication.getPref().setCourseId(chapterModel.getCourseId());
                    boolean isFirtsTime = MyApplication.getPref().isFirstTime();
                    if (isFirtsTime) {
                        intent = new Intent(MainExoActivity.this, ReminderActivity.class);
                        intent.putExtra("chapterModel", chapterModel);
                        intent.putExtra("courseModel", courseModel);
                        intent.putExtra("meditationStateModel", meditationStateModel);
                        startActivity(intent);
                    } else {
                        intent = new Intent(MainExoActivity.this, QuotesActivity.class);
                        intent.putExtra("chapterModel", chapterModel);
                        intent.putExtra("courseModel", courseModel);
                        intent.putExtra("meditationStateModel", meditationStateModel);
                        startActivity(intent);
                    }

                    if (mBound) {
                        unbindService(serviceConnection);
                        mBound = false;
                    }

                    finish();
                }
            }
        };
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.intent.action.PHONE_STATE");
//        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        registerReceiver(broadcastReceiver, filter);

        createNotificationChannel();

    }


    private void apiCallAfterAudioComleteAt90Per(int min) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date prevDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String todayAsString = dateFormat.format(today);
        String prevDateAsString = dateFormat.format(prevDate);
        String previousDatePref = MyApplication.getPref().getPrevDate();

        if (previousDatePref.equals(prevDateAsString)) {
            currentStreak++;
            MyApplication.getPref().setPrevDate(todayAsString);
        } else {
            if (!previousDatePref.equals(todayAsString)) {
                currentStreak = 1;
                MyApplication.getPref().setPrevDate(todayAsString);
            }
        }

        if (currentStreak > longestStreak) {
            setMeditationState(min, currentStreak, longestStreak + 1, totalSession + 1);
        } else {
            setMeditationState(min, currentStreak, longestStreak, totalSession + 1);
        }
    }

    private void setMeditationState(int min, int currentStreak, int longestStreak, int totalSession) {
        String userId = MyApplication.getPref().getUserId();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HashMap<String, String> userMap = new HashMap<String, String>();
        userMap.put("userId", userId);
        userMap.put("currentStreak", String.valueOf(currentStreak));
        userMap.put("longestStreak", String.valueOf(longestStreak));
//        userMap.put("minuteMeditate", String.valueOf((min + totalMin)));
//        userMap.put("dailyMinutes", String.valueOf((min + totaDailyMin)));
        userMap.put("totalSessions", String.valueOf(totalSession));

        ClarityAPI mindFulNessAPI = retrofit.create(ClarityAPI.class);
        Call<CommanResponseModel> call = mindFulNessAPI.updateProfile(userMap);
        MeditationStateModel meditationStateModelTemp = new MeditationStateModel();
        meditationStateModelTemp.setUserId(Integer.valueOf(userId));
        meditationStateModelTemp.setCurrentStreak(currentStreak);
        meditationStateModelTemp.setLongestStreak(longestStreak);
        meditationStateModelTemp.setMinuteMeditated((min + totalMin));
        meditationStateModelTemp.setDailyMinutes((min + totaDailyMin));
        meditationStateModelTemp.setWeeklyMinutes((min + totalWeeklyMin));
        meditationStateModelTemp.setTotalSessions(totalSession);

        db.meditationStateDao().insertAll(meditationStateModelTemp);
        meditationStateModel = meditationStateModelTemp;

        call.enqueue(new Callback<CommanResponseModel>() {
            @Override
            public void onResponse(Call<CommanResponseModel> call, Response<CommanResponseModel> response) {

            }

            @Override
            public void onFailure(Call<CommanResponseModel> call, Throwable t) {

            }
        });
    }


    public void closeService() {
        if (isMomentServiceRunning()) {
            Intent intent = new Intent(MainExoActivity.this, MusicServiceExo.class);
            stopService(intent);
        }
        finish();
    }

    private boolean isMomentServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MusicServiceExo.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    Handler handler = new Handler();

    private void updateProgressBar() {
        long duration = musicService.getPlayer() == null ? 0 : musicService.getPlayer().getDuration();
        Log.e("@@@Duration", duration + "");
        long position = musicService.getPlayer() == null ? 0 : musicService.getPlayer().getCurrentPosition();
        Log.e("@@@Current", position + "");
        long bufferedPosition = musicService.getPlayer() == null ? 0 : musicService.getPlayer().getBufferedPosition();
        Log.e("@@@Buffer", bufferedPosition + "");
        seekbar.setProgress((int) position);
        seekbar.setSecondaryProgress((int) bufferedPosition);
        // Remove scheduled updates.
//        handler.removeCallbacks(updateProgressAction);
        // Schedule an update if necessary.
        int playbackState = musicService.getPlayer() == null ? Player.STATE_IDLE : musicService.getPlayer().getPlaybackState();
        if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
            long delayMs;
            if (musicService.getPlayer().getPlayWhenReady() && playbackState == Player.STATE_READY) {
                delayMs = 1000 - (position % 1000);
                if (delayMs < 200) {
                    delayMs += 1000;
                }
            } else {
                delayMs = 1000;
            }
            handler.postDelayed(updateProgressAction, delayMs);
        }
        seekbar.setMax((int) duration);

        String totalDuration = getTimeFromMilliSec(duration);
        String currentTime = getTimeFromMilliSec(position);
        tvTime.setText(currentTime);
        tvTotalTime.setText(totalDuration);

        long milliSec80Per = (duration * 80) / 100;
        if (!isAPICallingDone) {
            if (position > milliSec80Per) {
                if (MyApplication.getPref().getFirstMeditationId() == 0) {
                    MyApplication.getPref().setFirstMeditationId(courseModel.getCourseId());
                }
                isAPICallingDone = true;
                long min = TimeUnit.MILLISECONDS.toMinutes(duration);
                apiCallAfterAudioComleteAt90Per((int) min);
            }
        }
    }

    @NotNull
    private String getTimeFromMilliSec(long duration) {
        return String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                                duration
                        )
                )
        );
    }

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            updateProgressBar();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MainExoActivity.this, MusicServiceExo.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver),
                new IntentFilter("com.example.exoplayer.PLAYER_STATUS")
        );
        startService(intent);

    }

    private void restorePlayerStatus() {
        seekbar.setEnabled(isPlayer());

        //if we are playing and the activity was restarted
        //update the controls panel
        if (musicService.getPlayer() != null && isPlayer()) {
            handler.postDelayed(updateProgressAction, 1000);
//            updatePlayingInfo(true, false, 0);
        }
    }


    public boolean isPlayer() {
        if (musicService.getPlayer() != null) {
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (musicService != null && musicService.isPlaying()) {
            playStopBtn.setImageResource(R.drawable.ic_pause_black);
//            Glide.with(MainExoActivity.this).load(R.drawable.playing).into(imageViewTarget);
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "activity destroy called");
        Log.i(TAG, "activity destroyed");
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }

    public void playStop(View view) {
        if (!musicService.isPlaying()) {
            musicService.play(audioPath);
            anim.resume();
        } else {
            musicService.stop();
            anim.pause();
        }
    }

    private void initializeSeekBar() {
        seekbar.setOnSeekBarChangeListener(

                new SeekBar.OnSeekBarChangeListener() {
                    int userSelectedPosition = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            userSelectedPosition = progress;
                            handler.removeCallbacks(updateProgressAction);
                            musicService.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = true;
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (mUserIsSeeking) {
                        }
                        mUserIsSeeking = false;
                    }
                });
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Streaming Radio";
            String description = "test channel for streaming radio";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("radio_playback_channel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onClick(View v) {

        long current = musicService.getPlayer().getCurrentPosition();
        switch (v.getId()) {
            case R.id.ivDownload:
                if (downloadFileModelOld != null) {
                    Snackbar snackbar = Snackbar
                            .make(parentLayout, getString(R.string.str_delete_a_file), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getString(R.string.str_yes), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String path = downloadFileModelOld.getAudioFilePath() + downloadFileModelOld.getFileName();
                                    File myFile = new File(path);
                                    if (myFile.exists()) {
                                        myFile.delete();
                                        db.downloadDao().delete(downloadFileModelOld);
                                        downloadFileModelOld = null;
                                        circleProgressNormal.setProgress(0);
                                        ivDownload.setImageResource(R.drawable.ic_download_dark);
                                    } else
                                        Toast.makeText(MainExoActivity.this, "file not found", Toast.LENGTH_SHORT).show();
                                }
                            });

                    snackbar.show();
                } else {
                    if (audioPath != null && !audioPath.equals("null"))
                        if (downloadFileModelOld == null) {
                            if (checkInternetConnection()) {
                                checkDownloadPermmission();
                            } else {
                                Toast.makeText(
                                        this,
                                        getString(R.string.str_check_internet_connection),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                }
                break;
            case R.id.ivForward:
                musicService.getPlayer().seekTo(current + seekTo15000);
                break;
            case R.id.ivBackword:
                musicService.getPlayer().seekTo(current - seekTo15000);
                break;
            case R.id.ivClose:
                closeService();
                break;
        }
    }

    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void checkDownloadPermmission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    MainExoActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
                    == PackageManager.PERMISSION_GRANTED
            ) {
                downloadAudio();
            } else {
                managePermissions.checkPermissions();
            }
        } else {
            downloadAudio();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 123:
                boolean isPermissionsGranted = managePermissions
                        .processPermissionsResult(requestCode, permissions, grantResults);

                break;

        }
    }


    private void downloadAudio() {
//        TODO("/data/user/0/com.mindfulness.greece/76a86802-8f51-449d-94b2-f8d2f182cbf5.mp3")
        String dirPath = getApplicationInfo().dataDir;
        circleProgressNormal.setVisibility(View.VISIBLE);
        ivDownload.setVisibility(View.GONE);
        downloadId = PRDownloader.download(audioPath, dirPath, filename)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;

                        circleProgressNormal.setProgress((int) progressPercent);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        circleProgressNormal.setVisibility(View.GONE);
                        circleProgressNormal.setProgress(0);
                        ivDownload.setVisibility(View.VISIBLE);
                        ivDownload.setImageResource(R.drawable.ic_download_done);

                        //Todo: after download set course true to display in download
                        courseModel.setIsDownloaded(true);
                        CourseDownloadModel courseDownloadModel = new CourseDownloadModel();
                        courseDownloadModel.setBanner(courseModel.getBanner());
                        courseDownloadModel.setCourseId(courseModel.getCourseId());
                        courseDownloadModel.setCourseName(courseModel.getCourseName());
                        courseDownloadModel.setDescription(courseModel.getDescription());
                        courseDownloadModel.setFromMinutes(courseModel.getFromMinutes());
                        courseDownloadModel.setToMinutes(courseModel.getToMinutes());
                        courseDownloadModel.setColorCode(courseModel.getColorCode());
                        db.courseDownloadDao().insertAll(courseDownloadModel);

                        db.downloadDao().insertDownloadFile(downloadFileModel);
                        filename = audioPath.substring(audioPath.lastIndexOf('/') + 1);
                        downloadFileModelOld = downloadFileModel;
                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
    }


}


*/
