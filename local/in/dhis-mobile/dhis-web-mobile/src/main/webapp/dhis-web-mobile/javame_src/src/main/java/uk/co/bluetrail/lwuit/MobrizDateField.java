package uk.co.bluetrail.lwuit;

import java.util.Calendar;
import java.util.Date;

/*
 * Makes an editable date and time field from a text areas
 */
public class MobrizDateField extends MobrizTextArea {

    public static final int DATE = 1001;
    public static final int TIME = 1002;
    private static int defaultMaxSize = 124;
    private int constraint;
    private long dateValue;
    private String dateValueString = "";
    private static String id = "MobrizDateField";
    private String format = "dmy";

    /*protected String getUIID() {
    return id;
    }
     */
    public MobrizDateField(String dtValue, int columns, int constraint, String format) {
        super(1, columns, constraint);
        this.constraint = constraint;
        if (dtValue == null || format == null) {
            throw new IllegalArgumentException();
        }
        setText(dtValue);
        this.format = format.toLowerCase();
    }

    public void setText(String text) {
        try {
            dateValue = Long.parseLong(text);
            dateValueString = dateValue + "";
        } catch (Exception e) {
            dateValueString = "";
        }

        if (constraint == TIME) {
            super.setText(this.getTimeString());
        } else {
            super.setText(this.getDateString());
        }
    }

    public long getDateValue() {
        return dateValue;
    }

    public String getTimeString() {

        if (this.dateValueString.equals("")) {
            return "";
        }

        try {
            Calendar currentTime = Calendar.getInstance();
            currentTime.setTime(new Date(dateValue));

            int h = currentTime.get(Calendar.HOUR_OF_DAY);
            int m = currentTime.get(Calendar.MINUTE);

            StringBuffer dt = new StringBuffer();

            if (h < 10) {
                dt.append('0');
            }

            dt.append(h);
            dt.append(':');
            if (m < 10) {
                dt.append('0');
            }
            dt.append(m);
            return dt.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public String getDateString() {
        if (this.dateValueString.equals("")) {
            return "";
        }

        try {
            Calendar currentTime = Calendar.getInstance();
            currentTime.setTime(new Date(dateValue));

            int d = currentTime.get(Calendar.DAY_OF_MONTH);
            int m = currentTime.get(Calendar.MONTH) + 1;
            int y = currentTime.get(Calendar.YEAR);

            StringBuffer dt = new StringBuffer();

            for (int i = 0; i < 3; i++) {
                switch (format.charAt(i)) {

                    case 'd':
                        dt.append(stuff(d));
                        break;
                    case 'm':
                        dt.append(stuff(m));
                        break;
                    case 'y':
                        dt.append(y);
                }
                if (i == 0 || i == 1) {
                    dt.append("/");
                }
            }
            return dt.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String stuff(int i) {
        if (i < 10) {
            return "0" + i;
        } else {
            return i + "";
        }
    }

    public String getDateValueString() {
        return this.dateValueString;
    }
}
