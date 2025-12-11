package inventory.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CsvWriter {
    public static <T> void writeToCsv(String fileName, List<T> objects) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName,false),true)) {
            for (T obj : objects) {
                // Assuming each object has a toCSV() method
                if (obj instanceof inventory.models.Item) {
                    writer.write(((inventory.models.Item) obj).toCSV() + "\n");
                } else if (obj instanceof inventory.models.Product) {
                    writer.write(((inventory.models.Product) obj).toCSV() + "\n");
                } else if (obj instanceof inventory.models.FinishedProduct) {
                    writer.write(((inventory.models.FinishedProduct) obj).toCSV() + "\n");
                } else if (obj instanceof inventory.models.Task) {
                    writer.write(((inventory.models.Task) obj).toCSV() + "\n");
                } else if (obj instanceof inventory.models.ProductLine) {
                    writer.write(((inventory.models.ProductLine) obj).toCSV() + "\n");
                } else if (obj instanceof inventory.models.User) {
                    writer.write(((inventory.models.User) obj).toCSV() + "\n");
                }
            }
        }
    }
}