package com.pomelo.pudding.utils;

import android.content.Context;
import android.view.View;

import com.pomelo.pudding.R;
import com.pomelo.pudding.view.CustomPopupWindow;
import com.pomelo.pudding.view.SiftDialogView;

/**
 * Created by Sherry on 2019/11/4
 */

public class DialogUtils {

    public static void showSiftDialog(Context context, View dependView, final View.OnClickListener listener) {
        final CustomPopupWindow window = new CustomPopupWindow(context);
        SiftDialogView contentView = new SiftDialogView(context)
                .setHeader(R.drawable.dialog_sift_header)
                .setContent(context.getResources().getString(R.string.dialog_sift_content))
                .setContentTwo(context.getResources().getString(R.string.dialog_sift_content_two))
                .setPositiveButton(context.getResources().getString(R.string.dialog_sift_confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                        if (listener != null) {
                            listener.onClick(v);
                        }
                    }
                }).setNegativeButton(null);
        window.show(dependView, contentView, R.style.dialog_anim_style);
    }

}
