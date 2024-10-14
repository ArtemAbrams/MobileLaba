package com.mobileapp.mobilelaba1;


import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.softmoore.android.graphlib.Graph;
import com.softmoore.android.graphlib.GraphView;
import com.softmoore.android.graphlib.Point;

import java.io.FileInputStream;
import java.io.IOException;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        String coefficientsText = readCoefficientsFromFile();
        if (coefficientsText == null || coefficientsText.isEmpty()) {
            Toast.makeText(this, "Не вдалося завантажити коефіцієнти з файлу", Toast.LENGTH_SHORT).show();
            return;
        }

        String functionType = getIntent().getStringExtra("functionType");
        String[] coefficientsArray = coefficientsText.split(",");
        double[] coefficients = new double[coefficientsArray.length];
        for (int i = 0; i < coefficientsArray.length; i++) {
            coefficients[i] = Double.parseDouble(coefficientsArray[i].trim());
        }

        // Створюємо масив точок для побудови графіка
        Point[] points = new Point[101];
        for (int i = 0; i <= 100; i++) {
            double x = i / 10.0;
            double y;
            if (functionType.equals("Лінійна")) {
                y = coefficients[0] * x + coefficients[1];
            } else if (functionType.equals("Квадратична")) {
                y = coefficients[0] * x * x + coefficients[1] * x + coefficients[2];
            } else {
                Toast.makeText(this, "Невідомий тип функції", Toast.LENGTH_SHORT).show();
                return;
            }
            points[i] = new Point(x, y);
        }

        // Створюємо графік і додаємо точки
        Graph graph = new Graph.Builder()
                .setWorldCoordinates(0, 10, -10, 10) // Налаштування координатної системи
                .addLineGraph(points) // Додаємо точки на графік
                .build();

        // Відображення графіка
        GraphView graphView = findViewById(R.id.graph_view);
        graphView.setGraph(graph);
    }

    private String readCoefficientsFromFile() {
        StringBuilder coefficientsText = new StringBuilder();
        try {
            FileInputStream fis = openFileInput("coefficients.txt");
            int c;
            while ((c = fis.read()) != -1) {
                coefficientsText.append((char) c);
            }
            fis.close();
        } catch (IOException e) {
            Toast.makeText(this, "Помилка під час читання файлу: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
        return coefficientsText.toString();
    }
}