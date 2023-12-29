package in.sunfox.healthcare.java.spandan_qms.bar_code_generator;

import javafx.application.Platform;
import javafx.scene.control.Button;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;

public class BarcodePrinter {

    public void printBarcode(BufferedImage barcodeImage, Button printButton) {
        try {
            // Disable the button before printing
            Platform.runLater(() -> {
                printButton.setDisable(true);
            });

            PrinterJob job = PrinterJob.getPrinterJob();
            PageFormat customFormat = new PageFormat();
            Paper customPaper = new Paper();

            double cmToInch = 0.393701;
            double widthInPoints = (5.08 * cmToInch * 72);
            double heightInPoints = (2.54 * cmToInch * 72);
            customPaper.setSize(widthInPoints, heightInPoints);
            customPaper.setImageableArea(0, 0, widthInPoints, heightInPoints);
            customFormat.setPaper(customPaper);
            customFormat.setOrientation(PageFormat.PORTRAIT);

            job.setPrintable(new PrintableBarcode(barcodeImage), customFormat);

            if (job.printDialog()) {
                // Print the job asynchronously in a separate thread
                Thread printThread = new Thread(() -> {
                    try {
                        job.print();
                    } catch (PrinterException e) {
                        e.printStackTrace();
                    } finally {
                        // Re-enable the button after printing (whether completed or canceled)
                        Platform.runLater(() -> {
                            printButton.setDisable(false);
                        });
                    }
                });
                printThread.start();
            }
            Platform.runLater(() -> {
                printButton.setDisable(false);
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                printButton.setDisable(false);
            });
            e.printStackTrace();
        }
    }

    static class PrintableBarcode implements Printable {
        private final BufferedImage image;

        public PrintableBarcode(BufferedImage image) {
            this.image = image;
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex != 0) {
                return NO_SUCH_PAGE;
            }

            double cmToInch = 0.393701;
            int imgWidth = (int) (5.08 * cmToInch * 72);
            int imgHeight = (int) (1.8 * cmToInch * 72);

            graphics.drawImage(image, 0, 0, imgWidth, imgHeight, null);

            graphics.setFont(new Font("Arial", Font.PLAIN, 8));

            return PAGE_EXISTS;
        }
    }
}
