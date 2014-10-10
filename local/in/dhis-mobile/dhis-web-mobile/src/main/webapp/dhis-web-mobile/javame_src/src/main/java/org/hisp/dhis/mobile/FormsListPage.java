package org.hisp.dhis.mobile;

import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Font;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.list.ListCellRenderer;

public class FormsListPage extends FormRunner {

    private String formNames[] = {"Sub-Center Form", "IDSP Form S", "IDSP Form P", "PHC MIES", "Patient-Data"};

    public String getName() {
        return "Select a Form";
    }

    protected void execute(final Form f, final ActionListener commandListener) {
        f.setLayout(new BorderLayout());
        f.setScrollable(false);
        final List formList = createList(formNames, new ButtonRenderer());
        formList.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                DHISMobile.formID = formList.getSelectedIndex() + 1;
                DHISMobile.pageFlowIndex += 1;
                DHISMobile.previous = DHISMobile.PAGES[DHISMobile.pageFlowIndex].run(new Command[]{DHISMobile.backCommand,
                            DHISMobile.nextCommand}, commandListener);
            }
        });
        f.addComponent(BorderLayout.CENTER, formList);
    }

    private List createList(String[] names, ListCellRenderer renderer) {
        List list = new List(names);
        list.setListCellRenderer(renderer);
        return list;
    }

    class ButtonRenderer extends Container implements ListCellRenderer {

        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
            Label l = new Label(value.toString());
            if (isSelected) {
                l.setFocus(true);
                l.getStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
                l.getStyle().setBgTransparency(100);
            } else {
                l.setFocus(false);
                l.getStyle().setFont(Font.createSystemFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
                l.getStyle().setBgTransparency(0);
            }
            return l;
        }

        public Component getListFocusComponent(List list) {
            return null;
        }
    }
}
