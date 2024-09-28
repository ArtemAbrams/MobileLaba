package com.mobileapp.mobilelaba1;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Function;

public class MainActivity extends AppCompatActivity {

    private EditText inputCoefficients;
    private Spinner functionTypeSpinner;
    private Button calculateButton, saveCoefficientsButton, showGraphButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Зв'язування елементів з XML
        inputCoefficients = findViewById(R.id.inputCoefficients);
        functionTypeSpinner = findViewById(R.id.functionTypeSpinner);
        calculateButton = findViewById(R.id.calculateButton);
        saveCoefficientsButton = findViewById(R.id.saveCoefficientsButton);
        showGraphButton = findViewById(R.id.showGraphButton);

        // Обробка подій на кнопках
        calculateButton.setOnClickListener(view -> calculateIntegral());
        saveCoefficientsButton.setOnClickListener(view -> saveCoefficients());
        showGraphButton.setOnClickListener(view -> showGraph());
    }

    private void calculateIntegral() {
        String coefficientsText = inputCoefficients.getText().toString();
        if (coefficientsText.isEmpty()) {
            Toast.makeText(this, "Будь ласка, введіть коефіцієнти", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] coefficientsArray = coefficientsText.split(",");
        double[] coefficients = new double[coefficientsArray.length];
        try {
            for (int i = 0; i < coefficientsArray.length; i++) {
                coefficients[i] = Double.parseDouble(coefficientsArray[i].trim());
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Помилка у введених коефіцієнтах", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedFunction = functionTypeSpinner.getSelectedItem().toString();
        Function<Double, Double> function;

        switch (selectedFunction) {
            case "Лінійна":
                function = (x) -> coefficients[0] * x + coefficients[1];
                break;
            case "Квадратична":
                function = (x) -> coefficients[0] * x * x + coefficients[1] * x + coefficients[2];
                break;
            default:
                Toast.makeText(this, "Невідомий тип функції", Toast.LENGTH_SHORT).show();
                return;
        }

        double a = 0; // Нижня межа інтегралу
        double b = 10; // Верхня межа інтегралу
        int n = 100; // Кількість кроків

        // Вимірювання часу для методу прямокутників
        long startTimeRectangle = System.nanoTime();
        double rectangleResult = rectangleMethod(a, b, n, function);
        long endTimeRectangle = System.nanoTime();
        long durationRectangle = endTimeRectangle - startTimeRectangle; // Час у наносекундах

        // Вимірювання часу для методу трапецій
        long startTimeTrapezoidal = System.nanoTime();
        double trapezoidalResult = trapezoidalMethod(a, b, n, function);
        long endTimeTrapezoidal = System.nanoTime();
        long durationTrapezoidal = endTimeTrapezoidal - startTimeTrapezoidal; // Час у наносекундах

        // Вимірювання часу для методу Симпсона
        long startTimeSimpson = System.nanoTime();
        double simpsonResult = simpsonMethod(a, b, n, function);
        long endTimeSimpson = System.nanoTime();
        long durationSimpson = endTimeSimpson - startTimeSimpson; // Час у наносекундах

        // Форматування результатів
        String result = "Метод прямокутників: " + rectangleResult + "\n" +
                "Час виконання: " + durationRectangle + " нс\n\n" +
                "Метод трапецій: " + trapezoidalResult + "\n" +
                "Час виконання: " + durationTrapezoidal + " нс\n\n" +
                "Метод Симпсона: " + simpsonResult + "\n" +
                "Час виконання: " + durationSimpson + " нс";

        showResultDialog(result);
    }

    public void openAuthorActivity(View view) {
        Intent intent = new Intent(this, AuthorActivity.class);
        startActivity(intent);
    }

    private void showResultDialog(String result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Результат обчислення");

        // Створюємо TextView для відображення результату
        TextView resultView = new TextView(this);
        resultView.setText(result);
        resultView.setPadding(20, 20, 20, 20);
        resultView.setTextSize(16);

        // Додаємо TextView у діалогове вікно
        builder.setView(resultView);

        // Додаємо кнопку для закриття діалогу
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void saveCoefficients() {
        String coefficientsText = inputCoefficients.getText().toString();
        if (coefficientsText.isEmpty()) {
            Toast.makeText(this, "Будь ласка, введіть коефіцієнти", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            FileOutputStream fos = openFileOutput("coefficients.txt", MODE_PRIVATE);
            fos.write(coefficientsText.getBytes());
            fos.close();
            Toast.makeText(this, "Коефіцієнти успішно збережені", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Помилка під час збереження коефіцієнтів", Toast.LENGTH_SHORT).show();
        }
    }

    private void showGraph() {
        Intent intent = new Intent(MainActivity.this, GraphActivity.class);
        String coefficientsText = inputCoefficients.getText().toString();
        intent.putExtra("coefficients", coefficientsText);
        intent.putExtra("functionType", functionTypeSpinner.getSelectedItem().toString());
        startActivity(intent);
    }

    public double rectangleMethod(double a, double b, int n, Function<Double, Double> func) {
        double h = (b - a) / n;
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            sum += func.apply(a + i * h);
        }
        return sum * h;
    }

    public double trapezoidalMethod(double a, double b, int n, Function<Double, Double> func) {
        double h = (b - a) / n;
        double sum = 0.5 * (func.apply(a) + func.apply(b));
        for (int i = 1; i < n; i++) {
            sum += func.apply(a + i * h);
        }
        return sum * h;
    }

    public double simpsonMethod(double a, double b, int n, Function<Double, Double> func) {
        if (n % 2 != 0) n++;
        double h = (b - a) / n;
        double sum = func.apply(a) + func.apply(b);
        for (int i = 1; i < n; i += 2) {
            sum += 4 * func.apply(a + i * h);
        }
        for (int i = 2; i < n - 1; i += 2) {
            sum += 2 * func.apply(a + i * h);
        }
        return sum * h / 3;
    }
}
