package com.netimen.floatingtoolbar;

import android.graphics.Color;
import android.graphics.Point;
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


@EActivity(R.layout.activity_demo)
public class DemoActivity extends AppCompatActivity {

    public enum SelectionAction {
        QUOTE, NOTE, SHARE, COPY, TRANSLATE, PROBLEM, DELETE
    }

    @ViewById
    FrameLayout mainContainer;

    @ViewById
    FloatingToolbar floatingToolbar;

    @AfterViews
    void ready() {
        mainContainer.setClipChildren(false);
        floatingToolbar.addPanel(new ActionAdapter(floatingToolbar, new ActionAdapter.Button[]{ActionAdapter.Button.CHOOSE_COLOR, ActionAdapter.Button.NOTE, ActionAdapter.Button.TRANSLATE, ActionAdapter.Button.SHARE, ActionAdapter.Button.COPY, ActionAdapter.Button.PROBLEM, ActionAdapter.Button.DELETE}));
        floatingToolbar.addPanel(new ActionAdapter(floatingToolbar, new ActionAdapter.Button[]{ActionAdapter.Button.COLOR_1, ActionAdapter.Button.COLOR_2, ActionAdapter.Button.COLOR_3, ActionAdapter.Button.COLOR_4}));
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

    private static class ActionAdapter extends BaseAdapter {

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
            COLOR_4(ButtonType.ICON, SelectionAction.QUOTE);


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

        private final FloatingToolbar toolbar;
        private final Button[] buttons;

        ActionAdapter(FloatingToolbar toolbar, Button[] buttons) {
            this.toolbar = toolbar;
            this.buttons = buttons;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return buttons[position];
        }

        @Override
        public int getCount() {
            return buttons.length;
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
                        case COLOR_4: // no break before default intentionally!
                            final Button[] colorButtons = {Button.COLOR_1, Button.COLOR_2, Button.COLOR_3, Button.COLOR_4};
                            for (Button colorButton : colorButtons)
                                if (colorButton != button)
                                    ((SelectionColorButton)toolbar.getActionView(colorButton)).setChecked(false);
                        default:
                            Toast.makeText(toolbar.getContext(), button.selectionAction.toString(), Toast.LENGTH_LONG).show();
                            toolbar.hide(false);
                    }
                }
            });
            return view;
        }

        private View buildView(Button button) {
            View view;
            switch (button.type) {
                case ICON:
                    int color = 0;
                    switch (button) {
                        case COLOR_1:
                            color = Color.argb(100, 255, 0, 0);
                            break;
                        case COLOR_2:
                            color = Color.argb(100, 0, 255, 0);
                            break;
                        case COLOR_3:
                            color = Color.argb(100, 255, 255, 0);
                            break;
                        case COLOR_4:
                            color = Color.argb(100, 255, 0, 255);
                            break;

                    }
                    view = new SelectionColorButton(toolbar.getContext(), 160, 0.5f, 0.8f, 0.1f, color);
                    return view;
                default:
                    view = ActionView_.build(toolbar.getContext()).bind(button.iconString, button.captionRes == 0 ? "" : toolbar.getResources().getString(button.captionRes));
                    return view;
            }
        }
    }

}
