package org.hisp.dhis.mobile;

import com.sun.lwuit.Container;
import com.sun.lwuit.Form;
import com.sun.lwuit.TextArea;
import com.sun.lwuit.TextField;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.table.TableLayout;

public class DatasetPage extends FormRunner {

    private String fieldArr[];

    public String getName() {
        return "ANC Form";
    }

    public DatasetPage(String[] fieldArr){
        this.fieldArr = fieldArr;
    }

    protected void execute(Form f, ActionListener commandListener) {
        f.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        for (int i = 0; i < fieldArr.length; i++) {
            Container con = new Container();
            TableLayout layout = new TableLayout(1, 2);
            con.setLayout(layout);
            TableLayout.Constraint constraint = layout.createConstraint();
            constraint.setWidthPercentage(70);
            TextArea t = new TextArea(1, 3);
            t.setGrowByContent(true);
            t.setText(fieldArr[i]);
            t.setFocusable(false);
            con.addComponent(constraint, t);

            TextField tf = new TextField(5);
            TableLayout.Constraint tfConst = layout.createConstraint();
            tfConst.setWidthPercentage(25);
            tf.setConstraint(TextField.NUMERIC);
            tf.setInputModeOrder(new String[]{"123"});
            tf.setUseSoftkeys(false);
            tf.setInputMode("123");
            tf.setMaxSize(3);
            if (i == 0) {
                tf.setNextFocusUp(tf);
            } else if (i == (fieldArr.length - 1)) {
                tf.setNextFocusDown(tf);
            } else {
            }
            con.addComponent(tfConst, tf);
            f.addComponent(con);
        }
    }
}
