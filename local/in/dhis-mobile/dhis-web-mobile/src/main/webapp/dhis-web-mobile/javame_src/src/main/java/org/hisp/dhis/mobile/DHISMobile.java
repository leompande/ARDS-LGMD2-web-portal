package org.hisp.dhis.mobile;

import com.sun.lwuit.Command;
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.impl.ImplementationFactory;
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.Resources;
import java.io.InputStream;
import javax.microedition.midlet.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import uk.co.bluetrail.lwuit.MobrizImplementationFactory;

public class DHISMobile extends MIDlet implements ActionListener {

    //<editor-fold defaultstate="collapsed" desc=" Declarations: Customizable Elements ">
    public static String logoPath = "/splash/dhislogo.png";
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Declarations: Program Flow ">
    private static String[][] dataElements = {{"Total No. of Pregnant Women", "No. Registered Within 1st Trimester", "No. of New Women Registered Under JSY", "No. of Women Received 3 ANC Checkup", "No. of Women Given TT1", "No. of Women Given TT2 / Booster", "No. of Women Given 100 IFA Tablets", "New Pregnant Women with Hypertension", "No. of Pregnant Women Having HB < 11"},{""},};
    private static final FormsListPage formListPage = new FormsListPage();
    private static final PeriodPage periodPage = new PeriodPage();
    private static final DatasetPage datasetPage = new DatasetPage(dataElements[0]);
    private static final SendPage sendPage = new SendPage();
    private static final SettingsPage settingsPage = new SettingsPage();
    public static final FormRunner[] PAGES = new FormRunner[]{
        formListPage, periodPage, datasetPage, sendPage, settingsPage};
    public static int pageFlowIndex = 0;
    public static int formID = 0;
    public static Form previous;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Declarations: Editing Previous Report ">
    private boolean firstRun = false;
    private RecordStore dataStore = null;
    private boolean savedMsg = false;
    private boolean midletPaused = false;
    private boolean editingLastReport = false;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Declaration: Version Management ">
    public static String appVersion = "v2.0";
    public static String branding = "DHIS Mobile";
    public static String lang = "";
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" Declarations: Command Buttons ">
    private static final int EXIT_COMMAND = 1;
    private static final int NEXT_COMMAND = 2;
    private static final int BACK_COMMAND = 3;
    private static final int SETTINGS_COMMAND = 4;
    private static final int SAVE_COMMAND = 5;
    private static final int SEND_COMMAND = 6;
    public static final Command exitCommand = new Command("Exit", EXIT_COMMAND);
    public static final Command nextCommand = new Command("Next", NEXT_COMMAND);
    public static final Command backCommand = new Command("Back", BACK_COMMAND);
    public static final Command settingsCommand = new Command("Settings", SETTINGS_COMMAND);
    public static final Command saveCommand = new Command("Save", SAVE_COMMAND);
    public static final Command sendCommand = new Command("Send SMS", SEND_COMMAND);
    //</editor-fold>

    public DHISMobile() {
    }

    public void startApp() {
        try {
            ImplementationFactory.setInstance(new MobrizImplementationFactory());
            Display.init(this);
            InputStream stream = getClass().getResourceAsStream("/dhismobile.res");
            Resources theme = Resources.open(stream);
            UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));
            //<editor-fold defaultstate="collapsed" desc=" Initialize Record Store ">
            new Thread(new Runnable() {

                public void run() {
                    try {
                        //<editor-fold defaultstate="collapsed" desc=" Check if store exists ">
                        dataStore = RecordStore.openRecordStore("dataStore", true);
                        if (dataStore.getNumRecords() == 0) {
                            firstRun = true;
                        } else {
                            firstRun = false;
                        }
                        //</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc=" Create RecordStore with 90 rows ">
                        if (firstRun) {
                            for (int i = 0; i < 90; i++) {
                                try {
                                    dataStore.addRecord("".getBytes(), 0, "".getBytes().length);
                                } catch (RecordStoreException rsex) {
                                    rsex.printStackTrace();
                                }
                            }
                        } //</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc=" Check if any previous msg Stored ">
                        else {
                            if (dataStore.getRecord(10) != null) {
                                String checkSaved = new String(dataStore.getRecord(10));
                                if (checkSaved.equals("true")) {
                                    savedMsg = true;
                                }
                            }
                        }
                        //</editor-fold>
                    } catch (RecordStoreException ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
            //</editor-fold>
            showSplashScreen();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*
     * 1.) Shows Splash Screen in full screen
     * 2.) Sleeps for 3 seconds
     * 3.) Checks the edit previous report message
     * 4.) Displays the first screen in the PAGE Flow
     */
    private void showSplashScreen() {
        SplashScreen splash = new SplashScreen();
        splash.setFullScreenMode(true);
        getDisplay().setCurrent(splash);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (savedMsg) {
            // will return true if the user clicks "YES"
            if (!Dialog.show("Question", "Do you want to edit your last submitted report?", "YES", "NO")) {
                editingLastReport = true;
            }
        }
        PAGES[pageFlowIndex].run(new Command[]{exitCommand, settingsCommand}, this);
    }

    private void showSettingsPage() {
        SettingsPage form = new SettingsPage();
        DHISMobile.previous = form.run(new Command[]{backCommand, saveCommand}, this);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void exitMIDlet() {
        getDisplay().setCurrent(null);
        destroyApp(true);
        notifyDestroyed();
    }

    public javax.microedition.lcdui.Display getDisplay() {
        return javax.microedition.lcdui.Display.getDisplay(this);
    }

    public void actionPerformed(ActionEvent evt) {
        Command cmd = evt.getCommand();
        switch (cmd.getId()) {
            case EXIT_COMMAND:
                exitMIDlet();
                break;
            case NEXT_COMMAND:
                if (pageFlowIndex < (PAGES.length - 3)) {
                    pageFlowIndex += 1;
                    previous = PAGES[pageFlowIndex].run(new Command[]{backCommand, nextCommand}, this);
                } else if (pageFlowIndex == (PAGES.length - 3)) {
                    pageFlowIndex += 1;
                    previous = PAGES[pageFlowIndex].run(new Command[]{backCommand, settingsCommand, sendCommand}, this);
                }
                break;
            case BACK_COMMAND:
                if (Display.getInstance().getCurrent().getTitle().equals("Settings")) {
                    previous.show();
                } else if (pageFlowIndex > 1) {
                    pageFlowIndex -= 1;
                    previous = PAGES[pageFlowIndex].run(new Command[]{backCommand, nextCommand}, this);
                } else if (pageFlowIndex == 1) {
                    pageFlowIndex -= 1;
                    previous = PAGES[pageFlowIndex].run(new Command[]{exitCommand, settingsCommand}, this);
                }
                break;
            case SETTINGS_COMMAND:
                showSettingsPage();
                break;
            case SAVE_COMMAND:
                break;
        }
    }
}
