package com.example.doctorhome;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doctorhome.database.DatabaseHelper;
import com.example.doctorhome.model.Medicine;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import okhttp3.*;

public class AIDiagnosisActivity extends AppCompatActivity {

    private TextInputEditText etSymptoms;
    private Button btnDiagnose;
    private ProgressBar progressBar;
    private ScrollView svResult;
    private TextView tvResult;
    private DatabaseHelper dbHelper;

    private static final String GEMINI_API_KEY = "AIzaSyD9of1ffiq3rf4x5d3ycljEm2XxwYs530M";
    private static final String MODEL_NAME = "gemini-2.0-flash-exp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_diagnosis);

        dbHelper = new DatabaseHelper(this);

        etSymptoms = findViewById(R.id.etSymptoms);
        btnDiagnose = findViewById(R.id.btnDiagnose);
        progressBar = findViewById(R.id.progressBar);
        svResult = findViewById(R.id.svResult);
        tvResult = findViewById(R.id.tvResult);

        //진단 버튼 클릭
        btnDiagnose.setOnClickListener(v -> {
            String symptoms = etSymptoms.getText().toString().trim();

            if (symptoms.isEmpty()) {
                Toast.makeText(this, "증상을 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            //API 키 확인
            if (GEMINI_API_KEY.equals("YOUR_GEMINI_API_KEY")) {
                Toast.makeText(this, "Gemini API 키를 설정하세요", Toast.LENGTH_LONG).show();
                return;
            }

            diagnose(symptoms);
        });
    }

    private void diagnose(String symptoms) {
        //UI 업데이트
        btnDiagnose.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        svResult.setVisibility(View.GONE);

        //등록된 약 목록 가져오기
        List<Medicine> medicines = dbHelper.getAllMedicines();
        String medicineNames = medicines.stream()
                .map(Medicine::getName)
                .collect(Collectors.joining(", "));

        //백그라운드 스레드에서 API 호출
        new Thread(() -> {
            try {
                String result = callGeminiAPI(symptoms, medicineNames);

                //UI 스레드에서 결과 표시
                runOnUiThread(() -> {
                    btnDiagnose.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    svResult.setVisibility(View.VISIBLE);
                    tvResult.setText(result);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    btnDiagnose.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "오류: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private String callGeminiAPI(String symptoms, String medicineNames) throws IOException {
        String apiUrl = String.format(
                "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                MODEL_NAME,
                GEMINI_API_KEY
        );

        String prompt;
        if (medicineNames.isEmpty()) {
            prompt = "당신은 의사 또는 약사입니다. 사용자의 증상을 보내드릴테니, 그 증상에 맞는 일반적인 진단, 약, 영양제, 치료법 또는 병원 등을 추천해주세요.\n" +
                    "증상: " + symptoms;
        } else {
            prompt = "당신은 의사 또는 약사입니다. 사용자의 증상을 보내드릴테니, 그 증상에 맞는 일반적인 진단, 약, 영양제, 치료법 또는 병원 등을 추천해주세요.\n" +
                    "사용자가 이미 가지고 있는 약 목록: " + medicineNames + "\n" +
                    "증상: " + symptoms + "\n\n" +
                    "1. 가지고 있는 약 중에서 증상에 맞는 약이 있으면 추천해주세요.\n" +
                    "2. 없으면 다른 약을 추천하고, 이유를 설명해주세요.\n" +
                    "3. 필요하다면 병원 방문을 권장해주세요.";
        }

        JSONObject requestBody = new JSONObject();
        try {
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();

            part.put("text", prompt);
            parts.put(part);
            content.put("parts", parts);
            contents.put(content);
            requestBody.put("contents", contents);
        } catch (Exception e) {
            throw new IOException("JSON 생성 실패: " + e.getMessage());
        }

        //HTTP 요청
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API 요청 실패: " + response.code());
            }

            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);

            //응답 파싱
            JSONArray candidates = jsonResponse.getJSONArray("candidates");
            JSONObject candidate = candidates.getJSONObject(0);
            JSONObject contentObj = candidate.getJSONObject("content");
            JSONArray responseParts = contentObj.getJSONArray("parts");
            JSONObject responsePart = responseParts.getJSONObject(0);

            return responsePart.getString("text");

        } catch (Exception e) {
            throw new IOException("응답 파싱 실패: " + e.getMessage());
        }
    }
}