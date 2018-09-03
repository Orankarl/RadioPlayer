package com.inorin.orankarl.radioplayer.PlayMusic.view;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.inorin.orankarl.radioplayer.LrcView;
import com.inorin.orankarl.radioplayer.PlayMusic.presenter.PlayerPresenter;
import com.inorin.orankarl.radioplayer.R;
import com.inorin.orankarl.radioplayer.SingleLrcView;
import com.inorin.orankarl.radioplayer.Utils.BilibiliUtil;
import com.inorin.orankarl.radioplayer.Utils.PlayUtil;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayActivity extends AppCompatActivity implements IPlayerView {

    private LrcView lrcView;
    private SingleLrcView singleLrcView;
    private ImageView imageView;
    private ImageButton button, forwardFive, replayFive;
    private SeekBar seekBar;
    private TextView currentTime, totalTime;
    private ConstraintLayout lrcLayout;
    private PlayerPresenter playerPresenter = new PlayerPresenter(this);
    private android.support.v7.widget.Toolbar toolbar;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
//        PlayUtil.startService(this, PlayUtil.currentMusic, PlayUtil.PLAY);

        initLrcLayout();
        initView();

        playOrPause(button);

        playerPresenter.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && this.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            this.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

        }

        BilibiliUtil util = new BilibiliUtil();
        util.init(6509075, this);

    }

    private void initLrcLayout() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        int width = bitmap.getWidth(), height = bitmap.getHeight();
        Log.d("width&height", String.valueOf(width)+" "+String.valueOf(height));
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, (int)(1.0 * (width-height)/2), 0, height, height);
        BitmapDrawable bitmapDrawableLRC = new BitmapDrawable(getResources(), newBitmap);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), newBitmap);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        imageView = findViewById(R.id.lrc_background);
        imageView.setLayoutParams(new ConstraintLayout.LayoutParams(screenWidth, screenWidth));
        imageView.setBackground(bitmapDrawable);

        lrcView = findViewById(R.id.lrc_view);
//        lrcView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//        lrcView.setBackground(getDrawable(R.drawable.background_img));

//        lrcView.setBackground(bitmapDrawableLRC);
//        lrcView.getBackground().setColorFilter(Color.parseColor("grey"), PorterDuff.Mode.MULTIPLY);

        lrcView.setBackgroundColor(getResources().getColor(R.color.colorBlackTransparent));
        lrcView.setLayoutParams(new ConstraintLayout.LayoutParams(screenWidth, screenWidth));

        singleLrcView = findViewById(R.id.lrc_single_view);
        singleLrcView.setBackgroundColor(getResources().getColor(R.color.colorBlackTransparent));

        lrcLayout = findViewById(R.id.lrc_layout);

        lrcLayout.setLayoutParams(new LinearLayout.LayoutParams(screenWidth, screenWidth));
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_play);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle("夢のつぼみ");
        toolbar.setSubtitle("水瀬いのり");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);

        button = findViewById(R.id.play);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playOrPause(view);
            }
        });

        seekBar = findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    PlayUtil.player.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                PlayUtil.player.pause();
                PlayUtil.CURRENT_STATE = PlayUtil.PAUSE;
                playerPresenter.start();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playOrPause(button);
                playerPresenter.start();
            }
        });

        currentTime = findViewById(R.id.current_time);
        totalTime = findViewById(R.id.total_time);

        forwardFive = findViewById(R.id.forward_5);
        replayFive = findViewById(R.id.replay_5);
        forwardFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                PlayUtil.pause();
                PlayUtil.player.seekTo(PlayUtil.player.getCurrentPosition() + 5000);
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        PlayUtil.pause();
//                    }
//                };
//                handler.postDelayed(runnable, 1000);
            }
        });
        replayFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                PlayUtil.pause();
                PlayUtil.player.seekTo(PlayUtil.player.getCurrentPosition() - 5000);
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        PlayUtil.pause();
//                    }
//                };
//                handler.postDelayed(runnable, 1000);
            }
        });
    }

    //根据当前音乐改变界面
    @Override
    public void updatePlayerControl() {
//        if (PlayUtil.CURRENT_STATE == PlayUtil.PLAY) {
//            button.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
//        } else {
//            button.setImageResource(R.drawable.ic_play_circle_filled_light_64dp);
//        }
        playerPresenter.loadLrc(1, this);
        if (PlayUtil.player != null) {
            seekBar.setMax(PlayUtil.player.getDuration());
            totalTime.setText(dateFormat.format(new Date(PlayUtil.player.getDuration())));
        }
        handler.sendEmptyMessage(0);
    }

    @Override
    public void initLrcView(String lrcStr) {
        lrcView.setLrc(lrcStr);
        lrcView.setPlayer(PlayUtil.player);
        lrcView.init();
        lrcView.setVisibility(View.GONE);

        singleLrcView.setLrc(lrcStr);
        singleLrcView.setPlayer(PlayUtil.player);
        singleLrcView.init();
    }

    public void playOrPause(View view) {
        if (PlayUtil.CURRENT_STATE == PlayUtil.STOP) {
            button.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
            PlayUtil.startService(this, PlayUtil.currentMusic, PlayUtil.PLAY);
        } else if (PlayUtil.CURRENT_STATE == PlayUtil.PAUSE) {
            button.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
            PlayUtil.startService(this, PlayUtil.currentMusic, PlayUtil.PAUSE);
        } else {
            button.setBackgroundResource(R.drawable.ic_play_circle_filled_light_64dp);
            PlayUtil.startService(this, PlayUtil.currentMusic, PlayUtil.PAUSE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_play_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.lrc_show:
                if (lrcView.getVisibility() == View.VISIBLE) {
                    fadeOutLRCView(lrcView, singleLrcView);
                } else if (lrcView.getVisibility() == View.GONE) {
                    fadeInLRCView(lrcView, singleLrcView);
                }
                break;
            case R.id.info:
                break;
            default:
                break;
        }
        return true;
    }

    public void switchLRCView(View view) {
        if (lrcView.getVisibility() == View.VISIBLE) {
            fadeOutLRCView(lrcView, singleLrcView);
        } else if (lrcView.getVisibility() == View.GONE) {
            fadeInLRCView(lrcView, singleLrcView);
        }
    }

    private void fadeOutLRCView(final LrcView view, final SingleLrcView singleView) {
//        Animation fadeOut = new AlphaAnimation(1, 0);
//        fadeOut.setInterpolator(new AccelerateInterpolator());
//        fadeOut.setDuration(300);
//
//        fadeOut.setAnimationListener(new Animation.AnimationListener()
//        {
//            public void onAnimationEnd(Animation animation)
//            {
//                view.setVisibility(View.GONE);
//            }
//            public void onAnimationRepeat(Animation animation) {}
//            public void onAnimationStart(Animation animation) {}
//        });
//        view.startAnimation(fadeOut);
//
//        Animation fadeIn = new AlphaAnimation(0, 1);
//        fadeIn.setInterpolator(new AccelerateInterpolator());
//        fadeIn.setDuration(300);
//
//        fadeIn.setAnimationListener(new Animation.AnimationListener()
//        {
//            public void onAnimationEnd(Animation animation)
//            {}
//            public void onAnimationRepeat(Animation animation) {}
//            public void onAnimationStart(Animation animation) {
//                singleView.setVisibility(View.VISIBLE);
//            }
//        });
//        singleView.startAnimation(fadeIn);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(singleView, "alpha", 0f, 1f, 1f);
        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                singleView.setVisibility(View.VISIBLE);
            }
        });

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f, 0f);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeIn, fadeOut);
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    private void fadeInLRCView(final LrcView view, final SingleLrcView singleView) {
//        Animation fadeIn = new AlphaAnimation(0, 1);
//        fadeIn.setInterpolator(new AccelerateInterpolator());
//        fadeIn.setDuration(300);
//
//        fadeIn.setAnimationListener(new Animation.AnimationListener()
//        {
//            public void onAnimationEnd(Animation animation)
//            {}
//            public void onAnimationRepeat(Animation animation) {}
//            public void onAnimationStart(Animation animation) {
//                view.setVisibility(View.VISIBLE);
//            }
//        });
//
//
//
//        Animation fadeOut = new AlphaAnimation(1, 0);
//        fadeOut.setInterpolator(new AccelerateInterpolator());
//        fadeOut.setDuration(300);
//
//        fadeOut.setAnimationListener(new Animation.AnimationListener()
//        {
//            public void onAnimationEnd(Animation animation)
//            {
//                singleView.setVisibility(View.GONE);
//            }
//            public void onAnimationRepeat(Animation animation) {}
//            public void onAnimationStart(Animation animation) {}
//        });
//        singleView.startAnimation(fadeOut);
//
//        view.startAnimation(fadeIn);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f, 1f);
        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }
        });

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(singleView, "alpha", 1f, 0f, 0f);
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                singleView.setVisibility(View.GONE);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeIn, fadeOut);
        animatorSet.setDuration(500);
        animatorSet.start();
    }

    private static class TrackHandler extends Handler{
        private final WeakReference<PlayActivity> activityWeakReference;

        TrackHandler(PlayActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            activityWeakReference.get().seekBar.setProgress(PlayUtil.player.getCurrentPosition());
            activityWeakReference.get().seekBar.setMax(PlayUtil.player.getDuration());
//            Log.d("update seekbar:", String.valueOf(PlayUtil.player.getCurrentPosition()));
//            Log.d("seekbar max", String.valueOf(activityWeakReference.get().seekBar.getMax()));

            activityWeakReference.get().currentTime.setText(activityWeakReference.get().dateFormat.format(new Date(PlayUtil.player.getCurrentPosition())));
            activityWeakReference.get().totalTime.setText(activityWeakReference.get().dateFormat.format(new Date(PlayUtil.player.getDuration())));
            this.sendEmptyMessageDelayed(0, 200);
        }
    }

    private final TrackHandler handler = new TrackHandler(this);
}
