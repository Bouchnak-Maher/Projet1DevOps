 import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Client1 {
    public static void main(String[] args) {
        try {
            InetAddress addr = InetAddress.getByName("192.168.1.151");
            Socket socket = new Socket(addr, 2009);
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            byte[] imageBytes = (byte[]) in.readObject(); // Receive the image byte array from the server

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
            BufferedImage receivedPart = ImageIO.read(byteArrayInputStream);

            // Apply a filter to the received part
            BufferedImage filteredPart = applyGaussianBlur(receivedPart);

            // Send the filtered part back to the server
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(filteredPart, "jpg", byteArrayOutputStream);
            byte[] filteredImageBytes = byteArrayOutputStream.toByteArray();

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(filteredImageBytes);

            in.close();
            out.close();
            socket.close();
            System.out.println("Filter applied to part 1 and sent to the server.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage applyGaussianBlur(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage filteredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int kernelSize = 2; // Ajustez la taille du noyau selon vos besoins
        double sigma = 1.5; // Ajustez l'écart-type du filtre gaussien

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int red = 0, green = 0, blue = 0;
                int numPixels = 0;

                for (int ky = -kernelSize; ky <= kernelSize; ky++) {
                    for (int kx = -kernelSize; kx <= kernelSize; kx++) {
                        int pixelX = x + kx;
                        int pixelY = y + ky;

                        if (pixelX >= 0 && pixelX < width && pixelY >= 0 && pixelY < height) {
                            int color = image.getRGB(pixelX, pixelY);
                            red += (color >> 16) & 0xFF;
                            green += (color >> 8) & 0xFF;
                            blue += color & 0xFF;
                            numPixels++;
                        }
                    }
                }

                red /= numPixels;
                green /= numPixels;
                blue /= numPixels;
                int blurredColor = (red << 16) | (green << 8) | blue;
                filteredImage.setRGB(x, y, blurredColor);
            }
        }

        return filteredImage;
    }
}