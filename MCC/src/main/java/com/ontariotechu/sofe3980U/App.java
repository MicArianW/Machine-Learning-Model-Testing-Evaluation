package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class App {
    public static void main(String[] args) {
        String filePath = "model.csv"; // Path to CSV file
        int numClasses = 5; // Number of classes (1 to 5)
        int[][] confusionMatrix = new int[numClasses][numClasses]; // Initialize confusion matrix
        double crossEntropy = 0.0; // Initialize Cross-Entropy loss
        int count = 0; // Counter for number of samples

        try {
            // Read CSV file
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            List<String[]> allData = csvReader.readAll();

            // Loop through each row (each sample)
            for (String[] row : allData) {
                int y_true = Integer.parseInt(row[0]) - 1; // Convert class index (1-based) to (0-based)
                double[] y_predicted = new double[numClasses]; // Store predicted probabilities
                int predictedClass = 0; // Predicted class index
                double maxProb = -1; // Store max probability for finding predicted class

                // Read predicted probabilities from CSV
                for (int i = 0; i < numClasses; i++) {
                    y_predicted[i] = Double.parseDouble(row[i + 1]); // Read predicted probability
                    if (y_predicted[i] > maxProb) {
                        maxProb = y_predicted[i];
                        predictedClass = i; // Select class with highest probability
                    }
                }

                // Compute Cross-Entropy Loss
                crossEntropy += -Math.log(y_predicted[y_true]);

                // Update confusion matrix
                confusionMatrix[predictedClass][y_true]++;

                count++; // Increment sample count
            }

            // Compute final Cross-Entropy loss
            crossEntropy /= count;

            // Print Cross-Entropy
            System.out.printf("\nCE = %.7f\n", crossEntropy);

            // Print Confusion Matrix
            System.out.println("Confusion Matrix:");
            System.out.print("\t\t ");
            for (int i = 1; i <= numClasses; i++) {
                System.out.print("y=" + i + "\t ");
            }
            System.out.println();
            for (int i = 0; i < numClasses; i++) {
                System.out.print("y^=" + (i + 1) + "\t");
                for (int j = 0; j < numClasses; j++) {
                    System.out.print(confusionMatrix[i][j] + "\t ");
                }
                System.out.println();
            }

        } catch (Exception e) {
            System.out.println("Error reading the CSV file: " + filePath);
        }
    }
}

