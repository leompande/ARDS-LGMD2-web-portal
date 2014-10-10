package org.hisp.dhis.mobile;

import com.sun.lwuit.Command;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.events.ActionListener;

public abstract class FormRunner {

    public abstract String getName();

    public final Form run(final Command[] commands, ActionListener commandListener) {
        Form previous = Display.getInstance().getCurrent();
        System.gc();
        final Form form = new Form(getName());
        for (int i = 0; i < commands.length; i++) {
            if (commands[i].getCommandName().equals("Back")) {
                form.addCommand(commands[i]);
                form.setBackCommand(commands[i]);
            } else {
                form.addCommand(commands[i]);
            }
        }
        form.addCommandListener(commandListener);
        execute(form, commandListener);
        form.show();

        return previous;
    }

    /**
     * The Pages should place its UI into the given form
     */
    protected abstract void execute(Form f, ActionListener commandListener);

    public void cleanup() {
    }
}
