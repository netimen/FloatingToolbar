package com.netimen.floatingtoolbar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelSizeRes;
import org.androidannotations.annotations.res.IntArrayRes;

import java.util.Arrays;
import java.util.List;


@EActivity(R.layout.activity_demo)
public class DemoActivity extends AppCompatActivity {

    public enum SelectionAction {
        QUOTE, NOTE, SHARE, COPY, TRANSLATE, PROBLEM, DELETE
    }

    private enum ButtonType {
        TEXT_ICON, QUOTE_ICON, NOTE_ICON, CHOOSE_COLOR_ICON, ICON
    }

    protected enum Button {
        QUOTE(ButtonType.QUOTE_ICON, "", R.string.quote, SelectionAction.QUOTE),
        NOTE(ButtonType.NOTE_ICON, "", R.string.note, SelectionAction.NOTE),
        TRANSLATE(ButtonType.TEXT_ICON, "r", R.string.translate, SelectionAction.TRANSLATE),
        SHARE(ButtonType.TEXT_ICON, "h", R.string.share, SelectionAction.QUOTE),
        COPY(ButtonType.TEXT_ICON, "c", R.string.copy, SelectionAction.COPY),
        PROBLEM(ButtonType.TEXT_ICON, "!", R.string.problem, SelectionAction.PROBLEM),
        DELETE(ButtonType.TEXT_ICON, "t", R.string.delete, SelectionAction.DELETE),
        CHOOSE_COLOR(ButtonType.CHOOSE_COLOR_ICON, "", R.string.quote, SelectionAction.QUOTE),
        COLOR_1(ButtonType.ICON, SelectionAction.QUOTE),
        COLOR_2(ButtonType.ICON, SelectionAction.QUOTE),
        COLOR_3(ButtonType.ICON, SelectionAction.QUOTE),
        COLOR_4(ButtonType.ICON, SelectionAction.QUOTE),
        COLOR_5(ButtonType.ICON, SelectionAction.QUOTE);


        static final List<Button> colorButtons = Arrays.asList(COLOR_1, COLOR_2, COLOR_3, COLOR_4, COLOR_5);

        private final ButtonType type;
        private final CharSequence iconString; // index to get icon from custom font
        private final int captionRes;
        private final SelectionAction selectionAction;

        Button(ButtonType type, CharSequence iconString, @StringRes int captionRes, SelectionAction selectionAction) {
            this.type = type;
            this.iconString = iconString;
            this.captionRes = captionRes;
            this.selectionAction = selectionAction;
        }

        Button(ButtonType buttonType, SelectionAction selectionAction) {
            this(buttonType, "", 0, selectionAction);
        }
    }

    @ViewById
    FrameLayout mainContainer;

    @ViewById
    FloatingToolbar floatingToolbar;

    @IntArrayRes
    int markersColors[];

    @ColorInt
    private int bgColor = Color.WHITE, textColor = Color.BLACK;
    private int currentColorIndex;

    @DimensionPixelSizeRes
    int selectionToolbarHeight;


    @AfterViews
    void ready() {
        mainContainer.setClipChildren(false);
        floatingToolbar.addPanel(new ActionAdapter(floatingToolbar, Arrays.asList(Button.CHOOSE_COLOR, Button.NOTE, Button.TRANSLATE, Button.SHARE, Button.COPY, Button.PROBLEM, Button.DELETE)));
        floatingToolbar.addPanel(new ActionAdapter(floatingToolbar, Button.colorButtons));
        floatingToolbar.setVisibility(View.GONE);
    }

    @Touch
    void mainContainerTouched(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP)
            if (floatingToolbar.getVisibility() == View.VISIBLE)
                floatingToolbar.hide(false);
            else
                floatingToolbar.show(new Point((int) e.getX() - mainContainer.getPaddingLeft(), (int) e.getY() - mainContainer.getPaddingTop()));
    }

    private class ActionAdapter extends BaseAdapter {

        private final FloatingToolbar toolbar;
        private final List<Button> buttons;

        ActionAdapter(FloatingToolbar toolbar, List<Button> buttons) {
            this.toolbar = toolbar;
            this.buttons = buttons;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return buttons.get(position);
        }

        @Override
        public int getCount() {
            return buttons.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Button button = (Button) getItem(position);
            final View view = buildView(button);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (button) {
                        case CHOOSE_COLOR:
                            toolbar.showPanel(1); // CUR move 1 somewhere
                            break;
                        case COLOR_1:
                        case COLOR_2:
                        case COLOR_3:
                        case COLOR_4:
                        case COLOR_5: // no break before default intentionally!
                            toolbar.getActionView(Button.CHOOSE_COLOR).invalidate();
                            toolbar.getActionView(Button.NOTE).invalidate();
                            currentColorIndex = Button.colorButtons.indexOf(button);
                            for (Button colorButton : Button.colorButtons)
                                if (colorButton != button)
                                    ((SelectionColorButton) toolbar.getActionView(colorButton)).setChecked(false);
                        default:
                            Toast.makeText(toolbar.getContext(), button.selectionAction.toString(), Toast.LENGTH_LONG).show();
                            toolbar.hide(false);
                    }
                }
            });
            return view;
        }

        private View buildView(Button button) {
            final int iconCircleRadius = (int) (selectionToolbarHeight * .18f);
            switch (button.type) {
                case ICON:
                    return new SelectionColorButton(toolbar.getContext(), iconCircleRadius, markersColors[Button.colorButtons.indexOf(button)]);
                case CHOOSE_COLOR_ICON:
                    return DynamicIconActionView_.build(toolbar.getContext()).bind(new ChooseColorIconRenderer(), iconCircleRadius * 3, toolbar.getResources().getString(button.captionRes));
                case NOTE_ICON:
                    return DynamicIconActionView_.build(toolbar.getContext()).bind(new NoteIconRenderer(), iconCircleRadius * 2, toolbar.getResources().getString(button.captionRes));
                default:
                    return ActionView_.build(toolbar.getContext()).bind(button.iconString, button.captionRes == 0 ? "" : toolbar.getResources().getString(button.captionRes));
            }
        }
    }

    private abstract class Renderer implements DynamicIconView.IconRenderer {
        final Paint paint = new Paint();
        final TextCenterRenderer textRenderer = new TextCenterRenderer(getString(R.string.selection_quote));

        Renderer() {
            paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        }
    }

    private class NoteIconRenderer extends Renderer {

        @Override
        public void draw(Canvas canvas) {
            paint.setColor(bgColor);
            final int r = canvas.getWidth() / 2, cy = canvas.getHeight() / 2;
            canvas.drawCircle(r, cy, r, paint);
            textRenderer.draw(canvas, r, cy);
            paint.setColor(markersColors[currentColorIndex]);
            paint.setStrokeWidth(3);
            final int lineY = cy + r / 2;
            canvas.drawLine(r - r / 2, lineY, r + r / 2, lineY, paint);
        }

        @Override
        public void onMeasure(int measuredWidth, int measuredHeight) {
            textRenderer.onMeasure(measuredWidth / 2);
        }
    }

    private class ChooseColorIconRenderer extends Renderer {
        private float r, y;

        @Override
        public void draw(Canvas canvas) {
            int circlesNumber = 3;
            paint.setColor(Utils.blendColors(markersColors[currentColorIndex == 2 ? circlesNumber : 2], bgColor));
            canvas.drawCircle(canvas.getWidth() - r, y, r, paint);
            paint.setColor(Utils.blendColors(markersColors[currentColorIndex == 1 ? circlesNumber : 1], bgColor));
            canvas.drawCircle(canvas.getWidth() - r - r / 2, y, r, paint);
            paint.setColor(Utils.blendColors(markersColors[currentColorIndex], bgColor));
            canvas.drawCircle(r, y, r, paint);
            textRenderer.draw(canvas, r, y);
        }

        @Override
        public void onMeasure(int measuredWidth, int measuredHeight) {
            r = measuredWidth / 3;
            y = measuredHeight / 2;
            textRenderer.onMeasure(r);
        }
    }
} // CUR
// 1. padding right, cycle circles, don't remove other circle, shadow on 5.0
