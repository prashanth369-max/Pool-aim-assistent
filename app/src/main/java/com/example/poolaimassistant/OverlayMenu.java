package com.example.poolaimassistant;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;

/**
 * Movable compact control panel for toggles and rendering options.
 */
public class OverlayMenu {

    public interface Listener {
        void onShowDirectChanged(boolean enabled);
        void onShowBankChanged(boolean enabled);
        void onThicknessChanged(float px);
        void onClearTarget();
        void onOverlayVisibleChanged(boolean visible);
    }

    private final WindowManager windowManager;
    private final WindowManager.LayoutParams params;
    private final View rootView;

    public OverlayMenu(Context context, Listener listener) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        rootView = LayoutInflater.from(context).inflate(R.layout.overlay_menu, null, false);

        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 30;
        params.y = 200;

        bindUi(rootView, listener);
        enableDragging(rootView);
    }

    public void show() {
        windowManager.addView(rootView, params);
    }

    public void dismiss() {
        if (rootView.getWindowToken() != null) {
            windowManager.removeView(rootView);
        }
    }

    private void bindUi(View view, Listener listener) {
        Switch direct = view.findViewById(R.id.switch_direct);
        Switch bank = view.findViewById(R.id.switch_bank);
        Switch visible = view.findViewById(R.id.switch_visible);
        SeekBar thickness = view.findViewById(R.id.seek_thickness);
        Button clear = view.findViewById(R.id.btn_clear_target);

        direct.setChecked(true);
        bank.setChecked(true);
        visible.setChecked(true);

        direct.setOnCheckedChangeListener((buttonView, isChecked) -> listener.onShowDirectChanged(isChecked));
        bank.setOnCheckedChangeListener((buttonView, isChecked) -> listener.onShowBankChanged(isChecked));
        visible.setOnCheckedChangeListener((buttonView, isChecked) -> listener.onOverlayVisibleChanged(isChecked));
        clear.setOnClickListener(v -> listener.onClearTarget());

        thickness.setProgress(4);
        thickness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                listener.onThicknessChanged(Math.max(2f, progress + 1f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    private void enableDragging(View dragView) {
        dragView.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;
            private float touchX;
            private float touchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = params.x;
                        startY = params.y;
                        touchX = event.getRawX();
                        touchY = event.getRawY();
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        params.x = startX + (int) (event.getRawX() - touchX);
                        params.y = startY + (int) (event.getRawY() - touchY);
                        windowManager.updateViewLayout(rootView, params);
                        return false;
                    default:
                        return false;
                }
            }
        });
    }
}
