package uk.co.bluetrail.lwuit;

import com.sun.lwuit.Display;
import com.sun.lwuit.TextArea;

/*
 * extents Text area so that when a alpha key is pressed you go in to edit mode.
 */
public class MobrizTextArea extends TextArea {

    public MobrizTextArea(String defVal, int i, int j) {
        super(defVal, i, j);
    }

    public MobrizTextArea(int i, int i2, int any) {
        super(i, i2, any);
    }

    public void keyPressed(int keyCode) {
        super.keyPressed(keyCode);
        int action = com.sun.lwuit.Display.getInstance().getGameAction(keyCode);
        //edit the textarea if not a direction key
        switch (action) {
            case Display.GAME_UP:
            case Display.GAME_LEFT:
            case Display.GAME_RIGHT:
            case Display.GAME_DOWN:
                break;
            default:
                this.fireClicked();
        }
    }
}
