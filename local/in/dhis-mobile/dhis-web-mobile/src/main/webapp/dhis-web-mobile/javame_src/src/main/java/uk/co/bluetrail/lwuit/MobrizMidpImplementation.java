package uk.co.bluetrail.lwuit;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import com.sun.lwuit.Component;
import com.sun.lwuit.Display;

import com.sun.lwuit.impl.midp.GameCanvasImplementation;
import com.sun.lwuit.plaf.UIManager;

/**
 * Extends GameCanvasImplementation to add native date editing for LWUIT.
 *
 */
public class MobrizMidpImplementation extends GameCanvasImplementation {

    private static javax.microedition.lcdui.Display display;
    private WaitForEdit waitForEdit;
    private DateForm dateForm;
    private MobrizDateField currentTextComponent;
    /**
     * The command used for accepting a text field change
     */
    private static Command CONFIRM_COMMAND;
    /**
     * The command used for canceling a text field change
     */
    private static Command CANCEL_COMMAND;

    public void init(Object m) {
        super.init(m);
        display = javax.microedition.lcdui.Display.getDisplay((MIDlet) m);
    }

    public void editString(Component cmp, int maxSize, int constraint, final String text) {

        //process as normal if not a dateField
        if (constraint != MobrizDateField.TIME && constraint != MobrizDateField.DATE) {
            super.editString(cmp, maxSize, constraint, text);
            return;
        }

        //ok lets process as a datefield
        currentTextComponent = (MobrizDateField) cmp;

        UIManager m = UIManager.getInstance();
        CONFIRM_COMMAND = new Command(m.localize("ok", "OK"), Command.OK, 1);
        CANCEL_COMMAND = new Command(m.localize("cancel", "Cancel"), Command.CANCEL, 2);
        dateForm = new DateForm(currentTextComponent.getDateValueString(), constraint);

        dateForm.setCommandListener(new CommandListener() {

            public void commandAction(Command c, Displayable d) {
                if (d == dateForm) {
                    if (c == CONFIRM_COMMAND) {
                        String text2 = dateForm.getDateText();
                        Display.getInstance().onEditingComplete(currentTextComponent, text2);
                    }

                    dateForm = null;
                    waitForEdit.setDone(true);

                    // we must return to the LWUIT thread otherwise there is a risk of the MIDP
                    // thread blocking on dialog.show calls essentially breaking text editing in dialogs
                    Display.getInstance().callSerially(new Runnable() {

                        public void run() {
                            if (currentTextComponent.getComponentForm() == Display.getInstance().getCurrent()) {
                                currentTextComponent.getComponentForm().show();
                            }

                        }
                    });
                }
            }
        });

        dateForm.addCommand(CONFIRM_COMMAND);
        dateForm.addCommand(CANCEL_COMMAND);

        display.setCurrent(dateForm);
        waitForEdit = new WaitForEdit();
        waitForEdit.setDone(false);

        Display.getInstance().invokeAndBlock(waitForEdit);
    }

    class WaitForEdit implements Runnable {

        private boolean done;

        public void run() {
            while (!done) {
                synchronized (getDisplayLock()) {
                    try {
                        getDisplayLock().wait(50);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        public void setDone(boolean done) {
            this.done = done;
            synchronized (getDisplayLock()) {
                getDisplayLock().notify();
            }
        }
    }
}
