package com.hlogi.wappclone.chats.util;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.databinding.FragmentMessagesBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class SendMessageAudioView {

    public enum  UserBehaviour {
        CANCELING,
        LOCKING,
        NONE
    }

    public enum  RecordingBehaviour {
        CANCELED,
        LOCKED,
        LOCK_DONE,
        RELEASED,
        RELEASED_QUICKLY
    }

    public interface RecordingListener {
        void onRecordingStarted();
        void onRecordingLocked();
        void onRecordingCompleted();
        void onRecordingCanceled();
        void onRecordingCanceledQuickly();
    }

    public interface TypingListener {
        void onTypingStarted();
        void onStillTyping();
        void onTypingStopped();
        void onSendMessage();
    }

    private static final String TAG = SendMessageAudioView.class.getSimpleName();

    private FragmentMessagesBinding binding;
    private RecordingListener recordingListener;
    private TypingListener typingListener;
    private UserBehaviour userBehaviour = UserBehaviour.NONE;
    private Animation animBlink, animJump, animJumpFast;

    private Context context;

    private boolean stopTrackingAction, isDeleting, isLocked = false, isRecording = false,
                    showCameraIcon = true, showAttachmentIcon = true, howEmojiIcon = true,
                    showEmojiIcon = true, isTyping = false, isLayoutDirectionRightToLeft;

    private Handler handler;

    private TimerTask timerTask;
    private Timer audioTimer;
    private SimpleDateFormat timeFormatter;

    private float firstX, firstY, lastX, lastY, directionOffset, cancelOffset, lockOffset, dp = 0;

    int screenWidth, screenHeight, audioTotalTime, count = 0;

    public void initView(FragmentMessagesBinding binding) {

        if (binding == null) {
            Log.e(TAG, "initView: binding can't be NULL");
            return;
        }
        this.binding = binding;

        context = binding.getRoot().getContext();

        timeFormatter = new SimpleDateFormat("m:ss", Locale.ENGLISH);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

//        isLayoutDirectionRightToLeft = context.getResources().getBoolean(R.bool.is_right_to_left);
        isLayoutDirectionRightToLeft = false;

        handler = new Handler(Looper.getMainLooper());

        dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());

        animBlink = AnimationUtils.loadAnimation(context, R.anim.blink);
        animJump = AnimationUtils.loadAnimation(context, R.anim.jump);
        animJumpFast = AnimationUtils.loadAnimation(context, R.anim.jump_fast);

        setupRecording();
    }

    public void onDetach() {
        typingListener = null;
        recordingListener = null;
        binding = null;
    }

    public void setRecordingListener(RecordingListener recordingListener) {
        this.recordingListener = recordingListener;
    }

    public void setTypingListener(TypingListener typingListener) {
        this.typingListener = typingListener;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }

    public int getAudioTotalTime() {
        return audioTotalTime;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupRecording() {

        binding.messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    if (typingListener != null && isTyping) {
                        typingListener.onTypingStopped();
                        isTyping = false;
                        binding.sendMicBtn.setImageDrawable(context.getDrawable(R.drawable.ic_vn_record_white_24dp));
                    }

                    if (showCameraIcon) {
                        if (binding.cameraIc.getVisibility() != View.VISIBLE && !isLocked) {
                            binding.cameraIc.setVisibility(View.VISIBLE);
                            binding.cameraIc.animate().scaleX(1f).scaleY(1f).setDuration(100).setInterpolator(new LinearInterpolator()).start();
                        }
                    }

                } else {
                    if (typingListener != null && (!isTyping && s.length() == 1)) {
                        typingListener.onTypingStarted();
                        isTyping = true;
                        binding.sendMicBtn.setImageDrawable(context.getDrawable(R.drawable.ic_send_white_24dp));
                    }

                    if (typingListener != null && (s.length() > 1)) {
                        typingListener.onStillTyping();
                        isTyping = true;
                    }

                    if (showCameraIcon) {
                        if (binding.cameraIc.getVisibility() != View.GONE) {
                            binding.cameraIc.setVisibility(View.GONE);
                            binding.cameraIc.animate().scaleX(0f).scaleY(0f).setDuration(100).setInterpolator(new LinearInterpolator()).start();
                        }
                    }
                }
            }
        });

        binding.sendMicBtn.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && isTyping) {
                if (typingListener != null)
                    typingListener.onSendMessage();
                return true;
            }
            if (isDeleting || isTyping) {
                return true;
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                cancelOffset = (float) (screenWidth / 2.8);
                lockOffset = (float) (screenWidth / 2.5);

                if (firstX == 0) {
                    firstX = event.getRawX();
                }

                if (firstY == 0) {
                    firstY = event.getRawY();
                }

                if (!isRecording) {
                    startRecord();
                } else if (isLocked) {
                    isLocked = false;
                    stopRecording(RecordingBehaviour.RELEASED);
                    return true;
                }

            } else if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_CANCEL) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!isLocked) {
                        if (audioTotalTime <= 1) {
                            stopRecording(RecordingBehaviour.RELEASED_QUICKLY);
                        } else {
                            if (isRecording)
                                stopRecording(RecordingBehaviour.RELEASED);
                        }
                    }
                }

            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                if (stopTrackingAction) {
                    return true;
                }

                UserBehaviour direction = UserBehaviour.NONE;

                float motionX = Math.abs(firstX - event.getRawX());
                float motionY = Math.abs(firstY - event.getRawY());

                if (isLayoutDirectionRightToLeft ? (motionX > directionOffset && lastX > firstX && lastY > firstY) : (motionX > directionOffset && lastX < firstX && lastY < firstY)) {

                    if (isLayoutDirectionRightToLeft ? (motionX > motionY && lastX > firstX) : (motionX > motionY && lastX < firstX)) {
                        direction = UserBehaviour.CANCELING;

                    } else if (motionY > motionX && lastY < firstY) {
                        direction = UserBehaviour.LOCKING;
                    }

                } else if (isLayoutDirectionRightToLeft ? (motionX > motionY && motionX > directionOffset && lastX > firstX) : (motionX > motionY && motionX > directionOffset && lastX < firstX)) {
                    direction = UserBehaviour.CANCELING;
                } else if (motionY > motionX && motionY > directionOffset && lastY < firstY) {
                    direction = UserBehaviour.LOCKING;
                }

                if (direction == UserBehaviour.CANCELING) {
                    if (userBehaviour == UserBehaviour.NONE || event.getRawY() + binding.sendMicBtn.getWidth() / 2 > firstY) {
                        userBehaviour = UserBehaviour.CANCELING;
                    }

                    if (userBehaviour == UserBehaviour.CANCELING) {
                        translateX(-(firstX - event.getRawX()));
                    }
                } else if (direction == UserBehaviour.LOCKING) {
                    if (userBehaviour == UserBehaviour.NONE || event.getRawX() + binding.sendMicBtn.getWidth() / 2 > firstX) {
                        userBehaviour = UserBehaviour.LOCKING;
                    }

                    if (userBehaviour == UserBehaviour.LOCKING) {
                        translateY(-(firstY - event.getRawY()));
                    }
                }

                lastX = event.getRawX();
                lastY = event.getRawY();

            }

            v.onTouchEvent(event);
            return true;
        });

        binding.slideToCancelTv.setOnClickListener(v -> {
            isLocked = false;
            stopRecording(RecordingBehaviour.CANCELED);
        });

    }

    private void translateY(float y) {
        if (y < -lockOffset) {
            locked();
            binding.sendMicBtn.setTranslationY(0);
            return;
        }

        if (binding.lockLayout.getVisibility() != View.VISIBLE) {
            binding.lockLayout.setVisibility(View.VISIBLE);
        }

        binding.sendMicBtn.setTranslationY(y);
        binding.lockLayout.setTranslationY(y / 2);
        binding.sendMicBtn.setTranslationX(0);
    }

    private void translateX(float x) {

        if (isLayoutDirectionRightToLeft ? x > cancelOffset : x < -cancelOffset) {
            canceled();
            binding.sendMicBtn.setTranslationX(0);
            binding.slideToCancelIv.setTranslationX(0);
            binding.slideToCancelTv.setTranslationX(0);
            return;
        }

        binding.sendMicBtn.setTranslationX(x);
        binding.slideToCancelIv.setTranslationX(0);
        binding.slideToCancelTv.setTranslationX(0);
        binding.lockLayout.setTranslationY(0);
        binding.sendMicBtn.setTranslationY(0);

        if (Math.abs(x) < binding.recordingMic.getWidth() / 2) {
            if (binding.lockLayout.getVisibility() != View.VISIBLE) {
                binding.lockLayout.setVisibility(View.VISIBLE);
            }
        } else {
            if (binding.lockLayout.getVisibility() != View.GONE) {
                binding.lockLayout.setVisibility(View.GONE);
            }
        }
    }

    private void locked() {
        stopTrackingAction = true;
        stopRecording(RecordingBehaviour.LOCKED);
        isLocked = true;
    }

    private void canceled() {
        stopTrackingAction = true;
        stopRecording(RecordingBehaviour.CANCELED);
    }

    private void startRecord() {
        if (recordingListener != null)
            recordingListener.onRecordingStarted();

        stopTrackingAction = false;
        isRecording = true;
        binding.messageEditText.setVisibility(View.INVISIBLE);
        binding.attachIc.setVisibility(View.INVISIBLE);
        binding.cameraIc.setVisibility(View.INVISIBLE);
        binding.emojiIc.setVisibility(View.INVISIBLE);
        binding.sendMicBtn.animate()
                .scaleXBy(1f)
                .scaleYBy(1f)
                .setDuration(200)
                .setInterpolator(new OvershootInterpolator())
                .start();

        binding.recordingTime.setVisibility(View.VISIBLE);
        binding.lockLayout.setVisibility(View.VISIBLE);
        binding.slideToCancelTv.setText(context.getString(R.string.slide_to_cancel));
        binding.slideToCancelTv.setTextColor(context.getColor(R.color.common_google_signin_btn_text_light_default));
        binding.slideToCancelTv.setVisibility(View.VISIBLE);
        binding.slideToCancelIv.setVisibility(View.VISIBLE);
        binding.recordingMic.setVisibility(View.VISIBLE);
        binding.recordingMic.startAnimation(animBlink);
        binding.lockArrow.clearAnimation();
        binding.lock.clearAnimation();
        binding.lockArrow.startAnimation(animJumpFast);
        binding.lock.startAnimation(animJump);

        if (audioTimer == null) {
            audioTimer = new Timer();
            timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    if (binding != null)
                        binding.recordingTime.setText(timeFormatter.format(new Date(audioTotalTime * 1000)));
                    audioTotalTime++;
                });
            }
        };

        audioTotalTime = 0;
        audioTimer.schedule(timerTask, 0, 1000);
        count++;
    }

    private void stopRecording(RecordingBehaviour recordingBehaviour) {
        stopTrackingAction = true;
        firstX = 0;
        firstY = 0;
        lastX = 0;
        lastY = 0;

        userBehaviour = UserBehaviour.NONE;

        binding.sendMicBtn.animate()
                .scaleX(1f)
                .scaleY(1f)
                .translationX(0)
                .translationY(0)
                .setDuration(100)
                .setInterpolator(new LinearInterpolator())
                .start();
        binding.slideToCancelTv.setTranslationX(0);
        binding.slideToCancelIv.setTranslationX(0);
        binding.slideToCancelTv.setVisibility(View.GONE);
        binding.slideToCancelIv.setVisibility(View.GONE);

        binding.lockLayout.setVisibility(View.GONE);
        binding.lockLayout.setTranslationY(0);
        binding.lockArrow.clearAnimation();
        binding.lock.clearAnimation();

        if (isLocked) {
            return;
        }

        if (recordingBehaviour == RecordingBehaviour.LOCKED) {
            binding.slideToCancelTv.setText(context.getString(R.string.cancel));
            binding.slideToCancelTv.setTextColor(context.getColor(R.color.colorRed));
            binding.slideToCancelTv.setVisibility(View.VISIBLE);
            binding.sendMicBtn.setImageDrawable(context.getDrawable(R.drawable.ic_send_white_24dp));

            if (recordingListener != null)
                recordingListener.onRecordingLocked();

        } else if (recordingBehaviour == RecordingBehaviour.CANCELED) {
            isRecording = false;
            binding.recordingTime.clearAnimation();
            binding.recordingTime.setVisibility(View.INVISIBLE);
            binding.sendMicBtn.setImageDrawable(context.getDrawable(R.drawable.ic_vn_record_white_24dp));

            timerTask.cancel();

            delete();

            if (recordingListener != null)
                recordingListener.onRecordingCanceled();

        } else if (recordingBehaviour == RecordingBehaviour.RELEASED || recordingBehaviour == RecordingBehaviour.LOCK_DONE) {
            isRecording = false;
            binding.recordingTime.clearAnimation();
            binding.recordingMic.clearAnimation();
            binding.recordingTime.setVisibility(View.INVISIBLE);
            binding.recordingMic.setVisibility(View.INVISIBLE);
            binding.messageEditText.setVisibility(View.VISIBLE);
            binding.sendMicBtn.setImageDrawable(context.getDrawable(R.drawable.ic_vn_record_white_24dp));
            if (showAttachmentIcon) {
                binding.attachIc.setVisibility(View.VISIBLE);
            }
            if (showCameraIcon) {
                binding.cameraIc.setVisibility(View.VISIBLE);
            }
            if (showEmojiIcon) {
                binding.emojiIc.setVisibility(View.VISIBLE);
            }

            timerTask.cancel();

            if (recordingListener != null)
                recordingListener.onRecordingCompleted();
        } else if (recordingBehaviour == RecordingBehaviour.RELEASED_QUICKLY) {
            isRecording = false;
            binding.recordingTime.clearAnimation();
            binding.recordingMic.clearAnimation();
            binding.recordingTime.setVisibility(View.INVISIBLE);
            binding.recordingMic.setVisibility(View.INVISIBLE);
            binding.messageEditText.setVisibility(View.VISIBLE);
            if (showAttachmentIcon) {
                binding.attachIc.setVisibility(View.VISIBLE);
            }
            if (showCameraIcon) {
                binding.cameraIc.setVisibility(View.VISIBLE);
            }
            if (showEmojiIcon) {
                binding.emojiIc.setVisibility(View.VISIBLE);
            }

            timerTask.cancel();

            if (recordingListener != null)
                recordingListener.onRecordingCanceledQuickly();

        }
    }

    private void delete() {
        if (binding.recordingMic.getVisibility() == View.INVISIBLE || binding.recordingMic.getVisibility() == View.GONE)
            binding.recordingMic.setVisibility(View.VISIBLE);
        binding.recordingMic.setRotation(0);
        isDeleting = true;
        binding.sendMicBtn.setEnabled(false);

        handler.postDelayed(() -> {
            isDeleting = false;
            binding.sendMicBtn.setEnabled(true);

            if (showAttachmentIcon) {
                binding.attachIc.setVisibility(View.VISIBLE);
            }
            if (showCameraIcon) {
                binding.cameraIc.setVisibility(View.VISIBLE);
            }
            if (showEmojiIcon) {
                binding.emojiIc.setVisibility(View.VISIBLE);
            }
        }, 1250);

        binding.recordingMic.animate()
                .translationY(-dp * 150)
                .rotation(180)
                .scaleXBy(0.6f)
                .scaleYBy(0.6f)
                .setDuration(500)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

                float displacement = 0;

                if (isLayoutDirectionRightToLeft) {
                    displacement = dp * 40;
                } else {
                    displacement = -dp * 40;
                }

                if (binding.dustbinLayout.getVisibility() == View.INVISIBLE || binding.dustbinLayout.getVisibility() == View.GONE)
                    binding.dustbinLayout.setVisibility(View.VISIBLE);

                binding.dustbin.setTranslationX(displacement);
                binding.dustbinCover.setTranslationX(displacement);

                binding.dustbinCover.animate()
                        .translationX(-dp * 10)
                        .rotation(-120)
                        .setDuration(350)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();

                binding.dustbin.animate()
                        .translationX(0)
                        .setDuration(350)
                        .setInterpolator(new DecelerateInterpolator())
                        .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                binding.recordingMic.animate()
                        .translationY(0)
                        .scaleX(0.1f)
                        .scaleY(0.1f)
                        .setDuration(350)
                        .setInterpolator(new LinearInterpolator()).setListener(
                        new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                binding.recordingMic.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                binding.recordingMic.setRotation(0);
                                binding.recordingMic.setVisibility(View.INVISIBLE);


                                float displacement = 0;

                                if (isLayoutDirectionRightToLeft) {
                                    displacement = dp * 40;
                                } else {
                                    displacement = -dp * 40;
                                }

                                binding.dustbinLayout.setVisibility(View.VISIBLE);

                                binding.dustbinCover.animate()
                                        .setDuration(150)
                                        .setStartDelay(50)
                                        .start();
                                binding.dustbin.animate()
                                        .translationX(displacement)
                                        .setDuration(200)
                                        .setStartDelay(250)
                                        .setInterpolator(new DecelerateInterpolator())
                                        .start();
                                binding.dustbinCover.animate()
                                        .translationX(-dp * 10)
                                        .rotation(120)
                                        .setDuration(200)
                                        .setStartDelay(250)
                                        .setInterpolator(new DecelerateInterpolator())
                                        .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        binding.messageEditText.setVisibility(View.VISIBLE);
                                        binding.dustbinLayout.setVisibility(View.GONE);
                                        binding.recordingMic.clearAnimation();
                                        binding.recordingMic.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                }).start();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }
                ).start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

}
