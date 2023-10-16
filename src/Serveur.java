import java.io.*;
import java.net.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Serveur {
    public static void main(String[] args) {
        ServerSocket serverSocket;
        Socket clientSocket1;
        Socket clientSocket2;

        try {
            serverSocket = new ServerSocket(2009);
            clientSocket1 = serverSocket.accept();
            clientSocket2 = serverSocket.accept();

            BufferedImage originalImage = ImageIO.read(new File("C:\\Users\\LENOVO\\eclipse-workspace\\ExerciceServeur\\src\\washing.jpg"));

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            int splitWidth = width / 2;
            BufferedImage part1 = originalImage.getSubimage(0, 0, splitWidth, height);
            BufferedImage part2 = originalImage.getSubimage(splitWidth, 0, width - splitWidth, height);

            ObjectOutputStream out1 = new ObjectOutputStream(clientSocket1.getOutputStream());
            ObjectOutputStream out2 = new ObjectOutputStream(clientSocket2.getOutputStream());

            // Send part1 to Client1
            ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
            ImageIO.write(part1, "jpg", byteArrayOutputStream1);
            byte[] part1Bytes = byteArrayOutputStream1.toByteArray();
            out1.writeObject(part1Bytes);

            // Send part2 to Client2
            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
            ImageIO.write(part2, "jpg", byteArrayOutputStream2);
            byte[] part2Bytes = byteArrayOutputStream2.toByteArray();
            out2.writeObject(part2Bytes);

            // Receive the filtered parts from clients
            ObjectInputStream in1 = new ObjectInputStream(clientSocket1.getInputStream());
            ObjectInputStream in2 = new ObjectInputStream(clientSocket2.getInputStream());

            byte[] filteredPart1Bytes = (byte[]) in1.readObject();
            byte[] filteredPart2Bytes = (byte[]) in2.readObject();

            // Create BufferedImage objects from the filtered part bytes
            BufferedImage filteredPart1 = ImageIO.read(new ByteArrayInputStream(filteredPart1Bytes));
            BufferedImage filteredPart2 = ImageIO.read(new ByteArrayInputStream(filteredPart2Bytes));

            // Combine the filtered parts into one image
            BufferedImage filteredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            filteredImage.getGraphics().drawImage(filteredPart1, 0, 0, null);
            filteredImage.getGraphics().drawImage(filteredPart2, splitWidth, 0, null);

            // Save the final filtered image
            ImageIO.write(filteredImage, "jpg", new File("filtered_image.jpg"));

            out1.close();
            in1.close();
            clientSocket1.close();
            out2.close();
            in2.close();
            clientSocket2.close();
            serverSocket.close();
            System.out.println("Filtered image created and saved as filtered_image.jpg.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
