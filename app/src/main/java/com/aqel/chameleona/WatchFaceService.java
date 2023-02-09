package com.aqel.chameleona;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.TextPaint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn"t
 * shown. On devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient
 * mode. The watch face is drawn with less contrast in mute mode.
 * <p>
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */
public class WatchFaceService extends CanvasWatchFaceService {

    /*
     * Updates rate in milliseconds for interactive mode. We update once a second to advance the
     * second hand.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = 50; //TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<WatchFaceService.Engine> mWeakReference;

        public EngineHandler(WatchFaceService.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            WatchFaceService.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        private static final float HOUR_STROKE_WIDTH = 3f;
        private static final float MINUTE_STROKE_WIDTH = 6f;
        private static final float SECOND_TICK_STROKE_WIDTH = 3f;

        private static final float CENTER_GAP_AND_CIRCLE_RADIUS = 4f;

        private static final int SHADOW_RADIUS = 2;

        private static final int DEFAULT_BACKGROUND_COLOR = Color.BLACK;
        private static final int DEFAULT_HOUR_COLOR = Color.GREEN;
        private static final int DEFAULT_MINUTE_COLOR = Color.WHITE;
        private static final int DEFAULT_SECOND_COLOR = Color.RED;
        private static final int DEFAULT_CIRCLE_COLOR = Color.WHITE;
        private static final int DEFAULT_DATE_COLOR = Color.WHITE;
        private static final float HAND_END_CAP_RADIUS = 6f;

        private static final float DATE_OFFSET = 0.5f;


        /* Handler to update the time once a second in interactive mode. */
        private final Handler mUpdateTimeHandler = new EngineHandler(this);
        SharedPreferences mSharedPref;
        private Calendar mCalendar;
        private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };
        private boolean mRegisteredTimeZoneReceiver = false;
        private boolean mMuteMode;
        private float mCenterX;
        private float mCenterY;
        private float mSecondHandLength;
        private float sMinuteHandLength;
        private float sHourHandLength;
        private int mWatchHourHandColor;
        private int mWatchMinuteHandColor;
        private int mWatchHandHighlightColor;
        private int mWatchHandShadowColor;
        private int mWatchMiddleCircleColor;
        private int mWatchSubTickColor;
        private int mBackgroundColor;
        private int mWatchDateColor;
        private Paint mHourPaint;
        private Paint mMinutePaint;
        private Paint mSecondPaint;
        private Paint mTickAndCirclePaint;
        private Paint mSubTickPaint;
        private Paint mBackgroundPaint;
        private Paint mDatePaint;
        private TextPaint mTextPaint;
        private boolean mAmbient;
        private boolean mLowBitAmbient;
        private boolean mBurnInProtection;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            Context context = getApplicationContext();
            mSharedPref = context.getSharedPreferences(
                    context.getString(R.string.analog_config_file),
                    Context.MODE_PRIVATE);
            setWatchFaceStyle(new WatchFaceStyle.Builder(WatchFaceService.this)
                    .setAcceptsTapEvents(true)
                    .build());

            mCalendar = Calendar.getInstance();

            loadSavedPreferences();
            initializeBackground();
            initializeWatchFace();
        }

        private void loadSavedPreferences() {
            String hoursPreferenceString = getApplicationContext().getString(R.string.config_hours_color);
            mWatchHourHandColor = mSharedPref.getInt(hoursPreferenceString, mWatchHourHandColor);

            String minutesPreferenceString = getApplicationContext().getString(R.string.config_minutes_color);
            mWatchMinuteHandColor = mSharedPref.getInt(minutesPreferenceString, mWatchMinuteHandColor);

            String secondsPreferenceString = getApplicationContext().getString(R.string.config_seconds_color);
            mWatchHandHighlightColor = mSharedPref.getInt(secondsPreferenceString, mWatchHandHighlightColor);

            String backgroundPreferenceString = getApplicationContext().getString(R.string.config_background_color);
            mBackgroundColor = mSharedPref.getInt(backgroundPreferenceString, mBackgroundColor);

            String ticksPreferenceString = getApplicationContext().getString(R.string.config_ticks_color);
            mWatchMiddleCircleColor = mSharedPref.getInt(ticksPreferenceString, mWatchMiddleCircleColor);

            mWatchSubTickColor = Color.argb(175,
                    Color.red(mWatchMiddleCircleColor),
                    Color.green(mWatchMiddleCircleColor),
                    Color.blue(mWatchMiddleCircleColor));

            String datePreferenceString = getApplicationContext().getString(R.string.config_date_color);
            mWatchDateColor = mSharedPref.getInt(datePreferenceString, mWatchDateColor);
        }

        private void initializeBackground() {
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(DEFAULT_BACKGROUND_COLOR);

        }

        private void initializeWatchFace() {
            /* Set defaults for colors */
            mWatchHourHandColor = DEFAULT_HOUR_COLOR;
            mWatchMinuteHandColor = DEFAULT_MINUTE_COLOR;
            mWatchHandHighlightColor = DEFAULT_SECOND_COLOR;
            mWatchMiddleCircleColor = DEFAULT_CIRCLE_COLOR;
            mWatchHandShadowColor = Color.BLACK;
            mWatchDateColor = DEFAULT_DATE_COLOR;

            mHourPaint = new Paint();
            mHourPaint.setColor(mWatchHourHandColor);
            mHourPaint.setStrokeWidth(HOUR_STROKE_WIDTH);
            mHourPaint.setAntiAlias(true);
            mHourPaint.setStrokeCap(Paint.Cap.SQUARE);
            mHourPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);

            mMinutePaint = new Paint();
            mMinutePaint.setColor(mWatchMinuteHandColor);
            mMinutePaint.setStrokeWidth(MINUTE_STROKE_WIDTH);
            mMinutePaint.setAntiAlias(true);
            mMinutePaint.setStrokeCap(Paint.Cap.ROUND);
            mMinutePaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);

            mSecondPaint = new Paint();
            mSecondPaint.setColor(mWatchHandHighlightColor);
            mSecondPaint.setStrokeWidth(SECOND_TICK_STROKE_WIDTH);
            mSecondPaint.setAntiAlias(true);
            mSecondPaint.setStrokeCap(Paint.Cap.ROUND);
            mSecondPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);

            mTickAndCirclePaint = new Paint();
            mTickAndCirclePaint.setColor(mWatchMiddleCircleColor);
            mTickAndCirclePaint.setStrokeWidth(SECOND_TICK_STROKE_WIDTH);
            mTickAndCirclePaint.setAntiAlias(true);
            mTickAndCirclePaint.setStyle(Paint.Style.STROKE);
            mTickAndCirclePaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);

            mTextPaint = new TextPaint();
            mTextPaint.setColor(mWatchMiddleCircleColor);
            mTextPaint.setStrokeWidth(1f);
            mTextPaint.setAntiAlias(true);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setTextSize(10f);


            mWatchSubTickColor = Color.argb(175,
                    Color.red(mWatchMiddleCircleColor),
                    Color.green(mWatchMiddleCircleColor),
                    Color.blue(mWatchMiddleCircleColor));
            mSubTickPaint = new Paint();
            mSubTickPaint.setColor(mWatchSubTickColor);
            mSubTickPaint.setStrokeWidth(SECOND_TICK_STROKE_WIDTH);
            mSubTickPaint.setAntiAlias(true);
            mTickAndCirclePaint.setStyle(Paint.Style.STROKE);
            mSubTickPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);

            mDatePaint = new TextPaint();
            mDatePaint.setColor(mWatchDateColor);
            mDatePaint.setStrokeWidth(5f);
            mDatePaint.setAntiAlias(true);
            mDatePaint.setTextAlign(Paint.Align.CENTER);
            mDatePaint.setTextSize(20f);
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            mAmbient = inAmbientMode;

            updateWatchStyle();

            /* Check and trigger whether or not timer should be running (only in active mode). */
            updateTimer();
        }

        private void updateWatchStyle() {
            if (mAmbient) {
                mHourPaint.clearShadowLayer();
                mMinutePaint.clearShadowLayer();
                mSecondPaint.clearShadowLayer();
                mTickAndCirclePaint.clearShadowLayer();
                mSubTickPaint.clearShadowLayer();
                mBackgroundPaint.setColor(DEFAULT_BACKGROUND_COLOR);
            } else {
                mHourPaint.setColor(mWatchHourHandColor);
                mMinutePaint.setColor(mWatchMinuteHandColor);
                mSecondPaint.setColor(mWatchHandHighlightColor);
                mTickAndCirclePaint.setColor(mWatchMiddleCircleColor);
                mTextPaint.setColor(mWatchMiddleCircleColor);
                mSubTickPaint.setColor(mWatchSubTickColor);

                mHourPaint.setAntiAlias(true);
                mMinutePaint.setAntiAlias(true);
                mSecondPaint.setAntiAlias(true);
                mTickAndCirclePaint.setAntiAlias(true);
                mSubTickPaint.setAntiAlias(true);

                mHourPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);
                mMinutePaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);
                mSecondPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);
                mTickAndCirclePaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);
                mSubTickPaint.setShadowLayer(SHADOW_RADIUS, 0, 0, mWatchHandShadowColor);


                mBackgroundPaint.setColor(mBackgroundColor);
            }
        }

        @Override
        public void onInterruptionFilterChanged(int interruptionFilter) {
            super.onInterruptionFilterChanged(interruptionFilter);
            boolean inMuteMode = (interruptionFilter == android.support.wearable.watchface.WatchFaceService.INTERRUPTION_FILTER_NONE);

            /* Dim display in mute mode. */
            if (mMuteMode != inMuteMode) {
                mMuteMode = inMuteMode;
                mHourPaint.setAlpha(inMuteMode ? 100 : 255);
                mMinutePaint.setAlpha(inMuteMode ? 100 : 255);
                mSecondPaint.setAlpha(inMuteMode ? 80 : 255);
                invalidate();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            /*
             * Find the coordinates of the center point on the screen, and ignore the window
             * insets, so that, on round watches with a "chin", the watch face is centered on the
             * entire screen, not just the usable portion.
             */
            mCenterX = width / 2f;
            mCenterY = height / 2f;

            /*
             * Calculate lengths of different hands based on watch screen size.
             */
            mSecondHandLength = (float) (mCenterX * 0.875);
            sMinuteHandLength = (float) (mCenterX * 0.875);
            sHourHandLength = (float) (mCenterX * 0.7);

        }

        /**
         * Captures tap event (and tap type). The {@link android.support.wearable.watchface.WatchFaceService#TAP_TYPE_TAP} case can be
         * used for implementing specific logic to handle the gesture.
         */
        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.
                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    // TODO: Add code to handle the tap gesture.
                    Toast.makeText(getApplicationContext(), R.string.message, Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);

            drawBackground(canvas);
            drawWatchFace(canvas);
        }

        private void drawBackground(Canvas canvas) {
            canvas.drawColor(mBackgroundColor);
        }

        private void drawWatchFace(Canvas canvas) {

            /*
             * Draw ticks. Usually you will want to bake this directly into the photo, but in
             * cases where you want to allow users to select their own photos, this dynamically
             * creates them on top of the photo.
             */

            drawMainTicks(canvas);
            drawSubTicks(canvas);

            /*
             * These calculations reflect the rotation in degrees per unit of time, e.g.,
             * 360 / 60 = 6 and 360 / 12 = 30.
             */
            final float seconds =
                    (mCalendar.get(Calendar.SECOND) + mCalendar.get(Calendar.MILLISECOND) / 1000f);
            final float secondsRotation = seconds * 6f;

            final float minutesRotation = mCalendar.get(Calendar.MINUTE) * 6f;

            final float hourHandOffset = mCalendar.get(Calendar.MINUTE) / 2f;
            final float hoursRotation = (mCalendar.get(Calendar.HOUR) * 30) + hourHandOffset;
            DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d");
            final String dateText = dateFormat.format(mCalendar.getTime());

            /*
             * Save the canvas state before we can begin to rotate it.
             */
            canvas.save();
            canvas.drawText(dateText, mCenterX, mCenterY - mCenterY*DATE_OFFSET, mDatePaint);

            canvas.rotate(hoursRotation, mCenterX, mCenterY);
            drawHand(canvas, sHourHandLength, mHourPaint);


            canvas.rotate(minutesRotation - hoursRotation, mCenterX, mCenterY);
            drawHand(canvas, sMinuteHandLength, mMinutePaint);


            /*
             * Ensure the "seconds" hand is drawn only when we are in interactive mode.
             * Otherwise, we only update the watch face once a minute.
             */
            if (!mAmbient) {
                canvas.rotate(secondsRotation - minutesRotation, mCenterX, mCenterY);
//                drawHandDoubleRect(canvas, mSecondHandLength, mSecondPaint);
                canvas.drawLine(
                        mCenterX,
                        mCenterY - CENTER_GAP_AND_CIRCLE_RADIUS,
                        mCenterX,
                        mCenterY - mSecondHandLength,
                        mSecondPaint);

            }
            canvas.drawCircle(
                    mCenterX,
                    mCenterY,
                    CENTER_GAP_AND_CIRCLE_RADIUS,
                    mTickAndCirclePaint);

            /* Restore the canvas" original orientation. */
            canvas.restore();
        }

        private void drawMainTicks(Canvas canvas) {
            float innerTickRadius = mCenterX - (mCenterX * .1f);
            float innerTextRadius = innerTickRadius - (mCenterX * .08f);
            float outerTickRadius = mCenterX;

            for (Integer tickIndex = 0; tickIndex < 12; tickIndex++) {
                float tickRot = (float) (tickIndex * Math.PI * 2 / 12);
                float innerX = (float) Math.sin(tickRot) * innerTickRadius;
                float innerY = (float) -Math.cos(tickRot) * innerTickRadius;
                float innerTextX = (float) Math.sin(tickRot) * innerTextRadius;
                float innerTextY = (float) -Math.cos(tickRot) * innerTextRadius;
                float outerX = (float) Math.sin(tickRot) * outerTickRadius;
                float outerY = (float) -Math.cos(tickRot) * outerTickRadius;
                canvas.drawLine(mCenterX + innerX, mCenterY + innerY,
                        mCenterX + outerX, mCenterY + outerY, mTickAndCirclePaint);
                mTextPaint.setTextSize(25f);
                canvas.drawText(tickIndex == 0 ? "12" : tickIndex.toString(), mCenterX + (innerTextX), mCenterY + (innerTextY) + (mCenterX * .032f), mTextPaint);
//                canvas.drawText(tickIndex == 0 ? "12" : tickIndex.toString(), mCenterX + (innerTextX * 1f), mCenterY + (innerTextY * 1f), mTextPaint);

            }
        }

        private void drawSubTicks(Canvas canvas) {
            float innerSmallTickRadius = mCenterX - (mCenterX * .06f);
            float outerSmallTickRadius = mCenterX;
            for (int tickIndex = 0; tickIndex < 60; tickIndex++) {
                if (tickIndex % 5 == 0) {
                    continue;
                }
                float tickRot = (float) (tickIndex * Math.PI * 2 / 60);
                float innerX = (float) Math.sin(tickRot) * innerSmallTickRadius;
                float innerY = (float) -Math.cos(tickRot) * innerSmallTickRadius;
                float outerX = (float) Math.sin(tickRot) * outerSmallTickRadius;
                float outerY = (float) -Math.cos(tickRot) * outerSmallTickRadius;
                canvas.drawLine(mCenterX + innerX, mCenterY + innerY,
                        mCenterX + outerX, mCenterY + outerY, mSubTickPaint);
            }
        }

        private void drawHand(Canvas canvas, float handLength, Paint paint) {
            canvas.drawRoundRect(mCenterX - HAND_END_CAP_RADIUS, mCenterY - handLength,
                    mCenterX + HAND_END_CAP_RADIUS, mCenterY + HAND_END_CAP_RADIUS,
                    HAND_END_CAP_RADIUS, HAND_END_CAP_RADIUS, paint);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();
                loadSavedPreferences();
                updateWatchStyle();
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            } else {
                unregisterReceiver();
            }

            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            WatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            WatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
        }

        /**
         * Starts/stops the {@link #mUpdateTimeHandler} timer based on the state of the watch face.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer
         * should only run in active mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !mAmbient;
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }
    }
}