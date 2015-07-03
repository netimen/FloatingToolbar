package com.netimen.floatingtoolbar;

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
        floatingToolbar.addPanel(new ActionAdapter(floatingToolbar, new ActionAdapter.Button[]{ActionAdapter.Button.COLOR_1, ActionAdapter.Button.COLOR_2}));
        floatingToolbar.setVisibility(View.GONE);
    }

    @Touch
    void mainContainerTouched(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP)
            if (floatingToolbar.getVisibility() == View.VISIBLE)
                floatingToolbar.hide();
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
            COLOR_2(ButtonType.ICON, SelectionAction.QUOTE);


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
            final ActionView view = ActionView_.build(toolbar.getContext());
            final Button button = (Button) getItem(position);
            view.bind(button.iconString, button.captionRes == 0 ? "" : toolbar.getResources().getString(button.captionRes));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (button) {
                        case CHOOSE_COLOR:
                            toolbar.showPanel(1);
                            break;
                        default:
                            Toast.makeText(toolbar.getContext(), button.selectionAction.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            return view;
        }
    }

}
