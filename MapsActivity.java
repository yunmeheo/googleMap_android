package app.my.googlemaptest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;


//보여지는 화면의 가운데에 마커 찍어주기
public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener{
    View marker_root_view;
    TextView tv_marker;
    private GoogleMap mMap;
    double longitude;
    double latitude;
    Boolean flag=true;


    //지도셋팅
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i("MapActivity","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onStart() {
        Log.i("MapActivity","onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i("MapActivity","onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("MapActivity","onPause");
        super.onPause();
    }

    //맵준비
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        Log.i("MapActivity","onMapReady");
        mMap = googleMap;

        //기본위치 셋팅
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.537523, 126.96558), 14));

        //지도클릭시 액션
        mMap.setOnMapClickListener(this);

        //마커클릭시 액션
        mMap.setOnMarkerClickListener(this);

        //끌어서 놓을때마다 중간위치를 반환한다.
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng center_latlng = mMap.getCameraPosition().target;
                //옮길때마다 마커 클리어
                mMap.clear();
                longitude = center_latlng.longitude;
                latitude = center_latlng.latitude;
                Log.i("MapActivity:","onCameraIdle");
                Log.i("MapActivity:","longitude"+longitude+"latitude"+latitude);

                //뷰 객체생성
                setCustomMarkerView();

                //마커 찍기
                getMarkerItems();
            }
        });
    }

    //마커 찍어주기
    private void getMarkerItems(){
        ArrayList<MarkerItem> parkingList = new ArrayList();
        parkingList.add(new MarkerItem(latitude, longitude, 2500));
        for (MarkerItem markerItem : parkingList) {
            addMarker(markerItem, false);
        }
    }

    //마커 xml호출
    private void setCustomMarkerView() {
        Log.i("MapActivity:","setCustomMarkerView : "+marker_root_view);
        marker_root_view = LayoutInflater.from(this).inflate(R.layout.marker_layout, null);
        tv_marker = (TextView)marker_root_view.findViewById(R.id.tv_marker);
    }

    //실제 마커를 추가하는 addMarker()함수
    private Marker addMarker(MarkerItem markerItem, boolean isSelectedMarker) {

        LatLng position = new LatLng(markerItem.getLat(), markerItem.getLon());
        int price = markerItem.getPrice();

        //마커셋팅
        MarkerOptions markerOptions = new MarkerOptions();

        if(flag == true) {
            String formatted ="기준위치";
            tv_marker.setBackgroundResource(R.drawable.ic_marker_point);
            tv_marker.setTextColor(Color.WHITE);
            tv_marker.setText(formatted);
        }

        if(flag==false){
            tv_marker.setBackgroundResource(R.drawable.ic_marker_ex);
            tv_marker.setText("클릭시 보여줄 내용");
            tv_marker.setTextColor(Color.BLACK);

        }

        markerOptions.title(Integer.toString(price));
        markerOptions.position(position);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_root_view)));
        return mMap.addMarker(markerOptions);
    }


    // View객체를 Bitmap으로 변환해서 보여주자.
    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("MapActivity:","flag : "+flag);
        marker.remove();
        if(flag == true) {
            flag = false;
        }else if(flag==false){
            flag = true;
        }
        marker.setTitle("주차장 정보 띄우기");
        return false;


    }

    //지도가 클릭되면 맵클리어
    @Override
    public void onMapClick(LatLng latLng) {
        Log.i("MapActivity","onMapClick");
        mMap.clear();
    }
}
