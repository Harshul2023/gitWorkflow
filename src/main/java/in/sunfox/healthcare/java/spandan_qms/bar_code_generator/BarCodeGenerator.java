package in.sunfox.healthcare.java.spandan_qms.bar_code_generator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BarCodeGenerator {
static Image good=null;
    public Image formBar(String barcodeText){

        try {
            BufferedImage barcodeImage = generateBarcode(barcodeText);


            PageFormat customFormat = new PageFormat();
            Paper customPaper = new Paper();

            double cmToInch = 0.393701;
            double widthInPoints = 5.08 * cmToInch * 72;
            double heightInPoints = 2.54 * cmToInch * 72;

            customPaper.setSize(widthInPoints, heightInPoints);
            customPaper.setImageableArea(0, 0, widthInPoints, heightInPoints);
            customFormat.setPaper(customPaper);
            customFormat.setOrientation(PageFormat.PORTRAIT);
            saveBarcodeToFile(barcodeImage, barcodeText);



        }
        catch (Exception e){}
        return good;
    }
    public static BufferedImage generateBarcode(String text) throws Exception {
        int width = 200;
        int height = 70;
        int padding = 25;
        int totalHeight = height + padding;
//        BarcodePrinter barcodePrinter=new BarcodePrinter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.CODE_128, width, height, hints);
        BufferedImage barcodeOnlyImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        BufferedImage combinedImage = new BufferedImage(width, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) combinedImage.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, totalHeight);
        g.drawImage(barcodeOnlyImage, 0, 0, null);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        int textWidth = g.getFontMetrics().stringWidth(text);
        int textX = (width - textWidth) / 2;
        int textY = height + 20;
        g.drawString(text, textX, textY);
//        barcodePrinter.printBarcode(combinedImage);
         good=SwingFXUtils.toFXImage(combinedImage,null);


        return combinedImage;
    }


    public static BufferedImage saveBarcodeToFile(BufferedImage image, String filename) {
        try {
            String userHome = System.getProperty("user.home");
            File downloadsDir = new File(userHome, "Downloads");
            File outputFile = new File(downloadsDir, filename + ".png");

            ImageIO.write(image, "PNG", outputFile);
            System.out.println("Barcode saved to: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }


}