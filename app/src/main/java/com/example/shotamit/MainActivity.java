package com.example.shotamit;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button btnCheck;
    TextView tvResult, tvDate;
    Spinner spinnerGroups;
    String channelURL = "https://t.me/s/cek_info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerGroups = findViewById(R.id.spinnerGroups);
        btnCheck = findViewById(R.id.btnCheck);
        tvResult = findViewById(R.id.tvResult);
        tvDate = findViewById(R.id.tvDate);


        String[] groups = {"Nvidia","Штучний Інтелект","Apple","Google","Microsoft","Asus","Windows","AMD","Intel","Xiaomi","Samsung","YouTube"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroups.setAdapter(adapter);


        btnCheck.setOnClickListener(v -> {
            String selectedGroup = spinnerGroups.getSelectedItem().toString();
            checkTelegram(selectedGroup);
        });


    }

    private void checkTelegram(String group) {
        tvResult.setText("Завантаження...");
        new Thread(() -> {
            try{
                Document doc = Jsoup.connect(channelURL).userAgent("Mozilla/5.0").timeout(15000).get();
                Elements posts = doc.select(".tgme_widget_message");
                String result = "Незнайдено публікацій";
                String dateTime = "Незнайдено дату";

                if (!posts.isEmpty()){
                    for (int i = posts.size()-1; i>=0; i--){
                        Element lastPost = posts.get(i);
                        Element textElement = lastPost.selectFirst(".tgme_widget_message_text");
                        if (textElement == null){
                            continue;
                        }
                        String postText = textElement.text();
                        if (postText.contains(group)){
                            Element timeElement = lastPost.selectFirst(".tgme_widget_message_date time");
                            if (textElement != null){
                                result = textElement.text();
                            } else {
                                result = "Постів незнайдено";
                            }
                            if (timeElement != null){
                                dateTime = timeElement.attr("datetime");
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
                                Date date = inputFormat.parse(dateTime);
                                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
                                dateTime = outputFormat.format(date);
                            } else {
                                dateTime = "Дати незнайдено";
                            }
                            break;
                        }
                    }


                }
                String finalResult = result;
                String finalDate = dateTime;
                runOnUiThread(()-> tvResult.setText(finalResult));
                runOnUiThread(()-> tvDate.setText(finalDate));
            } catch (Exception e){
                runOnUiThread(() -> tvResult.setText("Помилка завантаження!"));
            }
        }).start();
    }
}