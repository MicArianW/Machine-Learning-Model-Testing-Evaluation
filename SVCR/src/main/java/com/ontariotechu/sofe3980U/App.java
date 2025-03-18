package com.ontariotechu.sofe3980U;

import java.io.FileReader;  // Import FileReader for reading CSV files
import java.util.List;  // Import List for storing CSV data

import com.opencsv.CSVReader;  // Import OpenCSV's CSVReader
import com.opencsv.CSVReaderBuilder;  // Import CSVReaderBuilder to customize reading

public class App {
    public static void main(String[] args) {
        // List of model files to be evaluated
        String[] modelFiles = {"model_1.csv", "model_2.csv", "model_3.csv"};

        // Variables to store the best (lowest) error values and corresponding model names
        double bestMSE = Double.MAX_VALUE;  // Initialize with maximum possible value
        double bestMAE = Double.MAX_VALUE;
        double bestMARE = Double.MAX_VALUE;
        String bestModelMSE = "";  // Store the model with the lowest MSE
        String bestModelMAE = "";  // Store the model with the lowest MAE
        String bestModelMARE = "";  // Store the model with the lowest MARE

        // Iterate through each CSV file to calculate the error metrics
        for (String filePath : modelFiles) {
            double mse = 0, mae = 0, mare = 0;  // Initialize error metrics
            int count = 0;  // Counter to keep track of number of data points

            try {
                // Open and read the CSV file
                FileReader filereader = new FileReader(filePath);
                // Skip the header line and initialize CSVReader
                CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
                // Read all rows from CSV into a list
                List<String[]> allData = csvReader.readAll();

                // Iterate through each row (each data point)
                for (String[] row : allData) {
                    double y_true = Double.parseDouble(row[0]);  // Read actual value
                    double y_pred = Double.parseDouble(row[1]);  // Read predicted value

                    double error = y_true - y_pred;  // Calculate prediction error
                    mse += error * error;  // Sum squared errors for MSE
                    mae += Math.abs(error);  // Sum absolute errors for MAE
                    if (y_true != 0) {  // Avoid division by zero in MARE
                        mare += Math.abs(error / y_true);
                    }
                    count++;  // Increment the data point counter
                }

                // Compute the average error metrics by dividing by count
                if (count > 0) {
                    mse /= count;
                    mae /= count;
                    mare /= count;
                }

                // Print results for the current model
                System.out.println("\nFor " + filePath + ":");
                System.out.printf("MSE = %.5f%n", mse);  // Print MSE with 5 decimal places
                System.out.printf("MAE = %.5f%n", mae);  // Print MAE with 5 decimal places
                System.out.printf("MARE = %.5f%n", mare);  // Print MARE with 5 decimal places

                // Determine the best model for each metric
                if (mse < bestMSE) {
                    bestMSE = mse;
                    bestModelMSE = filePath;
                }
                if (mae < bestMAE) {
                    bestMAE = mae;
                    bestModelMAE = filePath;
                }
                if (mare < bestMARE) {
                    bestMARE = mare;
                    bestModelMARE = filePath;
                }

            } catch (Exception e) {
                // Handle errors if CSV file cannot be read
                System.out.println("Error reading the CSV file: " + filePath);
            }
        }

        // Print the best model based on each error metric
        System.out.println("\nBest Models:");
        System.out.println("According to MSE, the best model is " + bestModelMSE);
        System.out.println("According to MAE, the best model is " + bestModelMAE);
        System.out.println("According to MARE, the best model is " + bestModelMARE);
    }
}
