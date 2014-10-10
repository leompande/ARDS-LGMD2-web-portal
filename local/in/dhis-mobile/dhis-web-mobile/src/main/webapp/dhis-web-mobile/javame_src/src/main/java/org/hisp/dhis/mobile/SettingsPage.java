package org.hisp.dhis.mobile;

import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;

public class SettingsPage extends FormRunner {

    private String fieldArr[] = {"Enter phone #1:", "Enter phone #2:", "Enter phone #3:"};

    public String getName() {
        return "Settings";
    }

    protected void execute(Form f, ActionListener commandListener) {
        f.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        Label[] phoneLabel = new Label[fieldArr.length];
        PhoneField[] phoneFields = new PhoneField[fieldArr.length];

        for(int i=0;i<fieldArr.length;i++){
            phoneLabel[i] = new Label(fieldArr[i]);
            f.addComponent(phoneLabel[i]);
            phoneFields[i] = new PhoneField();
            f.addComponent(phoneFields[i]);
        }
    }

    class PhoneField extends TextField {
        public PhoneField() {
            super.setConstraint(TextField.PHONENUMBER);
            super.setInputModeOrder(new String[]{"123"});
            super.setInputMode("123");
            super.setMaxSize(10);
        }
    }
}
