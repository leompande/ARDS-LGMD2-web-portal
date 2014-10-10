package uk.co.bluetrail.lwuit;

import java.util.Calendar;
import java.util.Date;

import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Form;

/**
 * DateForm is the basic lcdui that is displayed when a user clicks on a MobrizDateField.
 * 
 * @author Richard Spence, Bluetrail
 */
public class DateForm extends Form {

    private DateField dateField;

    public DateForm(String dtAnswer, int constraint) {
        super("");

        if (constraint == MobrizDateField.TIME) {
            dateField = new DateField("", DateField.TIME);
        } else {
            dateField = new DateField("", DateField.DATE);
        }

        if (!dtAnswer.equals("")) {
            long dtLong = Long.parseLong(dtAnswer);
            Date dt = new Date();
            dt.setTime(dtLong);
            dateField.setDate(dt);
        } else {
            //set to now
            Calendar defTime = Calendar.getInstance();
            if (constraint == MobrizDateField.TIME) {
                defTime.setTime(new Date(0));  //make it the beginning of the epoch for times
                Calendar currentTime = Calendar.getInstance();
                currentTime.setTime(new Date());
                currentTime.set(Calendar.YEAR, defTime.get(Calendar.YEAR));
                currentTime.set(Calendar.MONTH, defTime.get(Calendar.MONTH));
                currentTime.set(Calendar.DATE, defTime.get(Calendar.DATE));

                Date dt = currentTime.getTime();

                dateField.setDate(dt);
            } else {
                Calendar currentTime = Calendar.getInstance();
                Date dt = currentTime.getTime();

                dateField.setDate(dt);
            }
        }
        append(dateField);
    }

    public String getDateText() {
        if (dateField.getDate() == null) {
            return "" + (dateField.getDate().getTime());
        } else {
            return "" + (dateField.getDate().getTime());
        }
    }
}
