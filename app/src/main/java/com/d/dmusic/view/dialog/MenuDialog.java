package com.d.dmusic.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.d.dmusic.R;
import com.d.dmusic.module.global.Cst;

/**
 * MenuDialog
 * Created by D on 2017/4/29.
 */
public class MenuDialog implements View.OnClickListener {
    private Dialog dialog;
    private LinearLayout rootView;
    private View.OnClickListener onClickListener;

    public MenuDialog(Context context, int layoutRes) {
        rootView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog_more, null);
        rootView.setOnClickListener(this);
        initMenu(context, layoutRes);

        dialog = new Dialog(context, R.style.PopTopInDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        Window dialogWindow = dialog.getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.TOP);
            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            p.width = Cst.SCREEN_WIDTH; //宽度设置
            p.height = Cst.SCREEN_HEIGHT; //高度设置
            dialogWindow.setAttributes(p);
        }
        dialog.setContentView(rootView);
    }

    private void initMenu(Context context, int layoutRes) {
        if (layoutRes == -1) {
            return;
        }
        LayoutInflater.from(context).inflate(layoutRes, rootView);
        if (rootView.getChildCount() < 2) {
            return;
        }
        View m = rootView.getChildAt(1);
        if (!(m instanceof ViewGroup)) {
            m.setOnClickListener(this);
            return;
        }
        ViewGroup menu = (ViewGroup) rootView.getChildAt(1);
        int count = menu.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = menu.getChildAt(i);
            v.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (onClickListener != null) {
            onClickListener.onClick(v);
        }
    }

    /**
     * 显示dialog
     */
    public void show() {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * 隐藏dialog
     */
    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        onClickListener = listener;
    }
}
