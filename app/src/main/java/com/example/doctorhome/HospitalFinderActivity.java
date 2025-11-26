package com.example.doctorhome;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doctorhome.adapter.HospitalAdapter;
import com.example.doctorhome.model.Hospital;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.KakaoMapSdk;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.camera.CameraUpdateFactory;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.LabelStyles;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class HospitalFinderActivity extends AppCompatActivity implements HospitalAdapter.OnHospitalClickListener {

    private static final String TAG = "HospitalFinder";

    private static final String KAKAO_NATIVE_APP_KEY = BuildConfig.KAKAO_NATIVE_APP_KEY;;
    private static final String KAKAO_REST_API_KEY = BuildConfig.KAKAO_REST_API_KEY;

    private MapView mapView;
    private KakaoMap kakaoMap;
    private TextView tvLocationInfo;
    private Button btnSearchHospital, btnSearchPharmacy;
    private RecyclerView rvHospitals;
    private HospitalAdapter adapter;

    private FusedLocationProviderClient fusedLocationClient;
    private double currentLat = 37.5665; //기본값: 서울시청
    private double currentLng = 126.9780;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    //Bitmap 마커 추가 코드
    private Bitmap markerBitmap;
    private LabelStyle markerStyle;

    //위치 권한 요청
    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                Boolean coarseLocationGranted = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);

                if ((fineLocationGranted != null && fineLocationGranted) ||
                        (coarseLocationGranted != null && coarseLocationGranted)) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(this, "위치 권한이 거부되었습니다", Toast.LENGTH_SHORT).show();
                    setupMap();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_finder);

        //sdk 초기화 필수
        KakaoMapSdk.init(this, KAKAO_NATIVE_APP_KEY);

        //Bitmap 로드
        markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
        markerStyle = LabelStyle.from(markerBitmap);

        mapView = findViewById(R.id.mapView);
        tvLocationInfo = findViewById(R.id.tvLocationInfo);
        btnSearchHospital = findViewById(R.id.btnSearchHospital);
        btnSearchPharmacy = findViewById(R.id.btnSearchPharmacy);
        rvHospitals = findViewById(R.id.rvHospitals);

        //위치 클라이언트 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        rvHospitals.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HospitalAdapter(this, new ArrayList<>(), this);
        rvHospitals.setAdapter(adapter);

        btnSearchHospital.setOnClickListener(v -> searchPlaces("병원"));
        btnSearchPharmacy.setOnClickListener(v -> searchPlaces("약국"));

        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            currentLat = location.getLatitude();
                            currentLng = location.getLongitude();

                            //한국 범위 체크 추가(카카오맵은 해외 지원x -> AVD는 미국으로 되어 있어서 안 뜸)
                            if (isInKorea(currentLat, currentLng)) {
                                tvLocationInfo.setText(String.format("현재 위치: %.4f, %.4f", currentLat, currentLng));
                                Log.d(TAG, "Location in Korea: " + currentLat + ", " + currentLng);
                            } else {
                                Log.w(TAG, "Location outside Korea, using default: " + currentLat + ", " + currentLng);
                                tvLocationInfo.setText("해외 위치 감지됨. 서울로 설정합니다.");
                                // 기본값 유지 (서울)
                                currentLat = 37.5665;
                                currentLng = 126.9780;
                            }
                        }
                        setupMap();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to get location", e);
                        tvLocationInfo.setText("위치를 가져올 수 없습니다");
                        setupMap();
                    });
        } else {
            setupMap();
        }
    }

    private boolean isInKorea(double lat, double lng) {
        return lat >= 33.0 && lat <= 43.0 && lng >= 124.0 && lng <= 132.0;
    }

    private void setupMap() {
        mapView.start(new MapLifeCycleCallback() {
            @Override
            public void onMapDestroy() {
                Log.d(TAG, "Map destroyed");
            }

            @Override
            public void onMapError(Exception e) {
                Log.e(TAG, "Map error", e);
                Toast.makeText(HospitalFinderActivity.this,
                        "지도를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
            }
        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull KakaoMap map) {
                kakaoMap = map;

                //현재 위치로 중심 이동
                LatLng position = LatLng.from(currentLat, currentLng);
                kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(position, 15));

                //현재 위치 마커 표시
                LabelOptions options = LabelOptions.from(position)
                        .setStyles(LabelStyles.from(markerStyle));

                kakaoMap.getLabelManager().getLayer().addLabel(options);

                Log.d(TAG, "Map ready");
            }
        });
    }

    private void searchPlaces(String keyword) {
        tvLocationInfo.setText("검색 중...");

        executorService.execute(() -> {
            try {
                String query = URLEncoder.encode(keyword, "UTF-8");
                String urlString = "https://dapi.kakao.com/v2/local/search/keyword.json"
                        + "?query=" + query
                        + "&x=" + currentLng
                        + "&y=" + currentLat
                        + "&radius=5000"
                        + "&size=15";

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "KakaoAK " + KAKAO_REST_API_KEY);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                if (responseCode == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray documents = jsonResponse.getJSONArray("documents");

                    List<Hospital> hospitals = new ArrayList<>();
                    for (int i = 0; i < documents.length(); i++) {
                        JSONObject place = documents.getJSONObject(i);

                        Hospital hospital = new Hospital();
                        hospital.setName(place.getString("place_name"));
                        hospital.setAddress(place.getString("address_name"));
                        hospital.setPhone(place.optString("phone", ""));
                        hospital.setLatitude(Double.parseDouble(place.getString("y")));
                        hospital.setLongitude(Double.parseDouble(place.getString("x")));
                        hospital.setType(keyword);

                        hospitals.add(hospital);
                    }

                    runOnUiThread(() -> {
                        displayHospitals(hospitals);
                        tvLocationInfo.setText(keyword + " " + hospitals.size() + "곳 검색됨");
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "검색 실패", Toast.LENGTH_SHORT).show();
                        tvLocationInfo.setText("검색 실패");
                    });
                }

                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Search error", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "검색 중 오류 발생", Toast.LENGTH_SHORT).show();
                    tvLocationInfo.setText("검색 오류");
                });
            }
        });
    }

    private void displayHospitals(List<Hospital> hospitals) {
        if (kakaoMap != null) {
            kakaoMap.getLabelManager().getLayer().removeAll();
        }

        adapter.updateData(hospitals);

        //지도에 마커 추가
        if (kakaoMap != null) {
            //현재 위치 마커 다시 추가
            LatLng currentPosition = LatLng.from(currentLat, currentLng);
            LabelOptions currentOptions = LabelOptions.from(currentPosition)
                    .setStyles(LabelStyles.from(markerStyle));
            kakaoMap.getLabelManager().getLayer().addLabel(currentOptions);

            //병원/약국 마커 추가
            for (Hospital hospital : hospitals) {
                LatLng position = LatLng.from(hospital.getLatitude(), hospital.getLongitude());

                LabelOptions options = LabelOptions.from(position)
                        .setStyles(LabelStyles.from(markerStyle))
                        .setTexts(hospital.getName());

                kakaoMap.getLabelManager().getLayer().addLabel(options);
            }
        }
    }

    @Override
    public void onHospitalClick(Hospital hospital) {
        //병원 클릭 시 지도 중심 이동
        if (kakaoMap != null) {
            LatLng position = LatLng.from(hospital.getLatitude(), hospital.getLongitude());
            kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(position, 17));
            Toast.makeText(this, hospital.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}