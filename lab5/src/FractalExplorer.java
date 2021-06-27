import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class FractalExplorer {
    private int displaySize; //размер дисплея
    private JImageDisplay display; //для обновления отображения в разных методах
    private FractalGenerator fractal; //будет использоваться ссылка на баззовый класс для отображения других фракталов
    private Rectangle2D.Double range; //указывает диапазон коплексной плоскости, выводящийся на экран
    public FractalExplorer(int size) {
        //размер дисплея
        displaySize = size;
        //инициализирует фрактал генератор
        fractal = new Mandelbrot();
        range = new Rectangle2D.Double();
        fractal.getInitialRange(range);
        display = new JImageDisplay(displaySize, displaySize);
    }

    //создаем и рисуем на окне
    public void createAndShowGUI() {
        //рамка для java.awt.BorderLayout
        display.setLayout(new BorderLayout());
        JFrame myframe = new JFrame("Fractal Explorer");
        //изображение
        myframe.add(display, BorderLayout.CENTER);
        //кнопка сброса
        JButton resetButton = new JButton("Reset Display");
        //сброс кнопки сброса //5
        MouseHandler click = new MouseHandler();
        display.addMouseListener(click);
        //кнопка закрыть //5
        myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //добавляем combo box //5
        JComboBox myComboBox = new JComboBox();
        //добавляем типы фракталов в combo box
        FractalGenerator mandelbrotFractal = new Mandelbrot();
        myComboBox.addItem(mandelbrotFractal);
        FractalGenerator tricornFractal = new Tricorn();
        myComboBox.addItem(tricornFractal);
        FractalGenerator burningShipFractal = new BurningShip();
        myComboBox.addItem(burningShipFractal);
        //добавляем выбор в btnH //5
        ButtonHandler fractalChooser = new ButtonHandler();
        myComboBox.addActionListener(fractalChooser);
        //Создаем панель JPanel //5
        JPanel myPanel = new JPanel();
        JLabel myLabel = new JLabel("Fractal:");
        myPanel.add(myLabel);
        myPanel.add(myComboBox);
        myframe.add(myPanel, BorderLayout.NORTH);
        //кнорка сейв //5
        JButton saveButton = new JButton("Save");
        JPanel myBottomPanel = new JPanel();
        myBottomPanel.add(saveButton);
        myBottomPanel.add(resetButton);
        myframe.add(myBottomPanel, BorderLayout.SOUTH);
        //кнопка сейв в btnH //5
        ButtonHandler saveHandler = new ButtonHandler();
        saveButton.addActionListener(saveHandler);
        //запрет растяжения и вывод //5
        myframe.pack();
        myframe.setVisible(true);
        myframe.setResizable(false);
    }

    //рисуем фрактал
    private void drawFractal()
    {
        //смотрим каждый пиксель
        for (int x=0; x<displaySize; x++){
            for (int y=0; y<displaySize; y++){
                // координаты х и у фракталы
                double xCoord = fractal.getCoord(range.x, range.x + range.width, displaySize, x);
                double yCoord = fractal.getCoord(range.y, range.y + range.height, displaySize, y);
                //количество итераций для координат в области отображения фрактала
                int iteration = fractal.numIterations(xCoord, yCoord);
                //если итераций 0, то черный пиксель
                if (iteration == -1){
                    display.drawPixel(x, y, 0);
                }
                else
                    {
                    //ставим цвет исходя из числа итераций
                    float hue = 0.7f + (float) iteration / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    //обновляем дисплей
                    display.drawPixel(x, y, rgbColor);
                }
            }
        }
        //перерисовываем JImageDisplay
        display.repaint();
    }

    //события btnH //5
    private class ButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            //источник действия
            String command = e.getActionCommand();
            //если выбран combo box, то выводим фрактал
            if (e.getSource() instanceof JComboBox) {
                JComboBox mySource = (JComboBox) e.getSource();
                fractal = (FractalGenerator) mySource.getSelectedItem();
                fractal.getInitialRange(range);
                drawFractal();

            }
            //если кнопка сброса, то перерисовывем
            else if (command.equals("Reset")) {
                fractal.getInitialRange(range);
                drawFractal();
            }
            //если кнопка сохранения, то сохраняем
            else if (command.equals("Save")) {
                //выбираем файл для сохранения
                JFileChooser myFileChooser = new JFileChooser();
                //сохраняем в png
                FileFilter extensionFilter =
                        new FileNameExtensionFilter("PNG Images", "png");
                myFileChooser.setFileFilter(extensionFilter);
                //выбираем имя файла
                myFileChooser.setAcceptAllFileFilterUsed(false);
                //выбираем каталог
                int userSelection = myFileChooser.showSaveDialog(display);
                //пересохранение
                if (userSelection == JFileChooser.APPROVE_OPTION) {

                    //получаем название файла
                    java.io.File file = myFileChooser.getSelectedFile();
                    String file_name = file.toString();

                    try {
                        BufferedImage displayImage = display.getImage();
                        javax.imageio.ImageIO.write(displayImage, "png", file);
                    }
                    catch (Exception exception) {
                        JOptionPane.showMessageDialog(display,
                                exception.getMessage(), "Cannot Save Image",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
                else return;
            }
        }
    }

    //класс для обработки событий MouseListener с дисплея
    private class MouseHandler extends MouseAdapter {
        //приблежает при щелчке
        @Override
        public void mouseClicked(MouseEvent e) {
            //получаем х при щелчке
            int x = e.getX();
            double xCoord = fractal.getCoord(range.x, range.x + range.width, displaySize, x);
            //получаем у при щелчке
            int y = e.getY();
            double yCoord = fractal.getCoord(range.y, range.y + range.height, displaySize, y);
            //вызов приблежения с увеличение 2 раза
            fractal.recenterAndZoomRange(range, xCoord, yCoord, 0.5);
            //перерисовываем фрактал
            drawFractal();
        }
    }

    //запускаем это дерьмо с размером 600*600
    public static void main(String[] args) {
        FractalExplorer displayExplorer = new FractalExplorer(600);
        displayExplorer.createAndShowGUI();
        displayExplorer.drawFractal();
    }
}
