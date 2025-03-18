package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class App {
    public static void main(String[] args) {
        // List of model files
        String[] modelFiles = {"model_1.csv", "model_2.csv", "model_3.csv"};

        // Variables to track the best models
        double bestBCE = Double.MAX_VALUE, bestAccuracy = 0, bestPrecision = 0, bestRecall = 0, bestF1 = 0, bestAUC = 0;
        String bestModelBCE = "", bestModelAccuracy = "", bestModelPrecision = "", bestModelRecall = "", bestModelF1 = "", bestModelAUC = "";

        for (String filePath : modelFiles) {
            int TP = 0, FP = 0, TN = 0, FN = 0;
            double BCE = 0;
            List<Double> yTrueList = new ArrayList<>();
            List<Double> yPredList = new ArrayList<>();
            int count = 0;

            try {
                // Read the CSV file
                FileReader filereader = new FileReader(filePath);
                CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
                List<String[]> allData = csvReader.readAll();

                for (String[] row : allData) {
                    int y_true = Integer.parseInt(row[0]); // Convert actual class label to int (0 or 1)
                    double y_pred = Double.parseDouble(row[1]); // Predicted probability

                    // Store values for AUC calculation
                    yTrueList.add((double) y_true);
                    yPredList.add(y_pred);

                    // Compute BCE
                    BCE += -((y_true * Math.log(y_pred)) + ((1 - y_true) * Math.log(1 - y_pred)));

                    // Threshold at 0.5 for confusion matrix
                    int y_pred_class = (y_pred >= 0.5) ? 1 : 0;

                    if (y_true == 1 && y_pred_class == 1) TP++; // True Positive
                    if (y_true == 1 && y_pred_class == 0) FN++; // False Negative
                    if (y_true == 0 && y_pred_class == 1) FP++; // False Positive
                    if (y_true == 0 && y_pred_class == 0) TN++; // True Negative

                    count++;
                }

                // Calculate BCE, Accuracy, Precision, Recall, F1 Score, AUC-ROC
                BCE /= count;
                double accuracy = (double) (TP + TN) / (TP + TN + FP + FN);
                double precision = TP / (double) (TP + FP);
                double recall = TP / (double) (TP + FN);
                double f1_score = 2 * (precision * recall) / (precision + recall);
                double auc_roc = calculateAUC(yTrueList, yPredList);

                // Print results
                System.out.println("\nFor " + filePath + ":");
                System.out.printf("BCE = %.7f%n", BCE);
                System.out.println("Confusion Matrix:");
                System.out.println("\t\t y=1\t y=0");
                System.out.println("y^=1\t " + TP + "\t " + FP);
                System.out.println("y^=0\t " + FN + "\t " + TN);
                System.out.printf("Accuracy = %.6f%n", accuracy);
                System.out.printf("Precision = %.6f%n", precision);
                System.out.printf("Recall = %.6f%n", recall);
                System.out.printf("F1 Score = %.6f%n", f1_score);
                System.out.printf("AUC ROC = %.6f%n", auc_roc);

                // Update best models
                if (BCE < bestBCE) { bestBCE = BCE; bestModelBCE = filePath; }
                if (accuracy > bestAccuracy) { bestAccuracy = accuracy; bestModelAccuracy = filePath; }
                if (precision > bestPrecision) { bestPrecision = precision; bestModelPrecision = filePath; }
                if (recall > bestRecall) { bestRecall = recall; bestModelRecall = filePath; }
                if (f1_score > bestF1) { bestF1 = f1_score; bestModelF1 = filePath; }
                if (auc_roc > bestAUC) { bestAUC = auc_roc; bestModelAUC = filePath; }

            } catch (Exception e) {
                System.out.println("Error reading the CSV file: " + filePath);
            }
        }

        // Print best models
        System.out.println("\nBest Models:");
        System.out.println("According to BCE, the best model is " + bestModelBCE);
        System.out.println("According to Accuracy, the best model is " + bestModelAccuracy);
        System.out.println("According to Precision, the best model is " + bestModelPrecision);
        System.out.println("According to Recall, the best model is " + bestModelRecall);
        System.out.println("According to F1 Score, the best model is " + bestModelF1);
        System.out.println("According to AUC ROC, the best model is " + bestModelAUC);
    }

    // Function to compute AUC-ROC (using simple rank-based method)
    public static double calculateAUC(List<Double> yTrue, List<Double> yPred) {
        int n = yTrue.size();
        List<Double> sortedPred = new ArrayList<>(yPred);
        Collections.sort(sortedPred, Collections.reverseOrder());

        int rankSum = 0, posCount = 0, negCount = 0;
        for (int i = 0; i < n; i++) {
            if (yTrue.get(i) == 1) posCount++;
            else negCount++;
            rankSum += sortedPred.indexOf(yPred.get(i)) + 1;
        }

        return (rankSum - (posCount * (posCount + 1) / 2.0)) / (posCount * negCount);
    }
}

