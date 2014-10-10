package org.hisp.dhis.mobile;

import com.sun.lwuit.ComboBox;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Form;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.FocusListener;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.list.ListCellRenderer;
import java.util.Date;
import uk.co.bluetrail.lwuit.MobrizDateField;

public class PeriodPage extends FormRunner {

    private String fieldArr[] = {"Monthly", "Weekly", "Daily"};
    private String monthArr[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private String periodArr[] = new String[24];

    public PeriodPage() {
        java.util.Calendar now = java.util.Calendar.getInstance();
        int year = now.get(java.util.Calendar.YEAR);
        for (int i = 0; i < 24; i++) {
            int j = i;
            if (i < 12) {
                periodArr[i] = monthArr[j] + " " + year;
            } else {
                j = i - 12;
                periodArr[i] = monthArr[j] + " " + (year-1);
            }
        }
    }

    public String getName() {
        return "Reporting Period";
    }

    protected void execute(final Form f, ActionListener commandListener) {
        f.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        final ComboBox periodTypeCombo = createCombo(fieldArr, new PeriodTypeRenderer());
        final ComboBox monthCombo = createCombo(periodArr, new PeriodTypeRenderer());
        final MobrizDateField dateField = new MobrizDateField(new Date().getTime() + "",
                10, MobrizDateField.DATE, "dmy");
        dateField.addFocusListener(new FocusListener() {

            public void focusGained(Component cmp) {
                dateField.getStyle().setBgColor(0x0000ff);
                dateField.getStyle().setFgColor(0xffffff);
            }

            public void focusLost(Component cmp) {
                dateField.getStyle().setBgColor(0x0000ff);
                dateField.getStyle().setFgColor(0x000000);
            }
        });

        periodTypeCombo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if (periodTypeCombo.getSelectedIndex() == 0) {
                    f.removeComponent(dateField);
                    if (!f.contains(monthCombo)) {
                        f.addComponent(monthCombo);
                    }
                } else if (periodTypeCombo.getSelectedIndex() == 1) {
                    f.removeComponent(monthCombo);
                    if (!f.contains(dateField)) {
                        f.addComponent(dateField);
                    }
                } else if (periodTypeCombo.getSelectedIndex() == 2) {
                    f.removeComponent(monthCombo);
                    if (!f.contains(dateField)) {
                        f.addComponent(dateField);
                    }
                }
            }
        });
        f.addComponent(periodTypeCombo);
        f.addComponent(monthCombo);
    }

    private ComboBox createCombo(String[] names, ListCellRenderer renderer) {
        ComboBox combo = new ComboBox(names);
        combo.setListCellRenderer(renderer);
        return combo;
    }

    class PeriodTypeRenderer extends Container implements ListCellRenderer {

        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
            Label l = new Label(value.toString());
            list.getStyle().setBgColor(0x0000ff);
            if (isSelected) {
                l.setFocus(true);
                l.getStyle().setFgColor(0xffffff);
                l.getStyle().setBgColor(0x000000);
            } else {
                l.setFocus(false);
            }
            return l;
        }

        public Component getListFocusComponent(List list) {
            return null;
        }
    }
}
