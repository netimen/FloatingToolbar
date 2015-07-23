package com.netimen.playground;

import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.netimen.playground.R;
import com.netimen.playground.toolbar.CustomFontHelper;


public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_text);
//        ((TextView) findViewById(R.id.aaaa)).setText(CustomFontHelper.getCustomFontText(this, "\ue141"));
        String text = "hello, world";
//        text = "\ue141";
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(text);
        spannable.setSpan(new CustomFontHelper.CustomTypefaceSpan("monospace", Typeface.createFromAsset(getAssets(), "typicons.ttf")), 0, text.length(), Spannable.SPAN_MARK_MARK);
//        spannable.setSpan(new TypefaceSpan("monospace"), 0, text.length(), Spannable.SPAN_MARK_MARK);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_template_icon_bg)
                        .setContentTitle("My notification");
        new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_template_icon_bg)
                        .setContentTitle("My notification")
//                .setStyle(new NotificationCompat.BigPictureStyle()
//                        .bigPicture(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
//                                .bigText(CustomFontHelper.getCustomFontText(this, "\ue141"))
//                        .setSummaryText("+3 more"))
//                        .setStyle(new NotificationCompat.BigTextStyle()
//                                .bigText(Html.fromHtml("aaa<b>bbbb</b><br>cccc <img src='http://icons.iconarchive.com/icons/mazenl77/I-like-buttons-3a/512/Cute-Ball-Go-icon.png'>"))
////                                .bigText(CustomFontHelper.getCustomFontText(this, "\ue141"))
//                                .bigText(spannable)
//                                .setSummaryText("+3 more"))
                        .addAction(R.drawable.abc_btn_check_material, "bbb", null)
                        .addAction(R.drawable.abc_btn_check_material, "ccc", null)
                        .addAction(R.drawable.abc_btn_check_material, "ddd", null)
                        .setContentText(Html.fromHtml("Hello aaa<b>World</b>!"));
        Notification notification = mBuilder.build();
        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        final RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notfication);
//        contentView.setImageViewResource(R.id.notification_image, R.drawable.abc_btn_check_material);
        final TextView textView = new TextView(this);
        textView.setText("0123456789a123456789b123456789c123456789d123456789e123456789");
        final int widthPixels = Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        textView.measure(View.MeasureSpec.makeMeasureSpec(widthPixels, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, 0));
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
        final Bitmap bitmap = Bitmap.createBitmap(textView.getMeasuredWidth(), textView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
//        textView.setLayoutParams(new ViewGroup.LayoutParams(widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT));
//        textView.requestLayout();

        textView.draw(canvas);
        canvas.drawCircle(10, 10, 10, new Paint());

        final int identifier = getResources().getIdentifier("*android:dimen/standard_notification_panel_width", null, null);
        contentView.setImageViewBitmap(R.id.notification_image, bitmap);
//        contentView.setTextViewText(R.id.notification_title, "My custom notification title");
//        contentView.setTextViewText(R.id.notification_text, CustomFontHelper.getCustomFontText(this, "\ue141"));
        notification.contentView = contentView;
//        contentView.setI

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, notification);
    }

}
