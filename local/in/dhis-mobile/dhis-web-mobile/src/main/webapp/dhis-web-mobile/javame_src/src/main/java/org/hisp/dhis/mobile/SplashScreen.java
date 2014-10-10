package org.hisp.dhis.mobile;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class SplashScreen extends Canvas {

    public SplashScreen() {
        this.showNotify();
    }

    protected void paint(Graphics g) {
        String label = DHISMobile.branding + " " + DHISMobile.appVersion + " (" + DHISMobile.lang + ")";
        g.setColor(0x000000);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        try {
            InputStream stream = getClass().getResourceAsStream(DHISMobile.logoPath);
            Image img = Image.createImage(stream);
            g.drawImage(img, this.getWidth() / 2, this.getHeight() / 2, Graphics.VCENTER | Graphics.HCENTER);
            g.setColor(0xffffff);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            int labelX = this.getWidth() / 2;
            int labelY = this.getHeight() / 2 + (img.getHeight()/2) + g.getFont().getHeight();
            g.drawString(label, labelX, labelY, Graphics.BASELINE | Graphics.HCENTER);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
