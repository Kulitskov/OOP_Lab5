import javax.swing.*;
import java.awt.image.*;
import java.awt.*;

class JImageDisplay extends JComponent{
    private BufferedImage displayImage;


    public BufferedImage getImage() {
        return displayImage;
    }

    //выводим дисплей
    public JImageDisplay(int width, int height){
        displayImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //setPreferredSize()
        Dimension imageDimension = new Dimension(width, height);
        super.setPreferredSize(imageDimension);
    }

    //закращиваем пиксель
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(displayImage, 0, 0, displayImage.getWidth(), displayImage.getHeight(), null);
    }

    //очищает поле
    public void clearImage() {
        int[] blankArray = new int[getWidth() * getHeight()];
        displayImage.setRGB(0, 0, getWidth(), getHeight(), blankArray, 0, 1);
    }

    //закрашивает пиксель
    public void drawPixel(int x, int y, int rgbColor) {
        displayImage.setRGB(x, y, rgbColor);
    }
}