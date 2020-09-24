package com.sangwoo.simplecommunity.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.sangwoo.simplecommunity.Request.BoardRequest;
import com.sangwoo.simplecommunity.ImageUpload.JSONParser;
import com.sangwoo.simplecommunity.R;
import com.sangwoo.simplecommunity.VO.WriteItemVO;
import com.sangwoo.simplecommunity.Adapter.WriteRecyclerViewAdapter;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class WriteActivity extends AppCompatActivity implements View.OnClickListener {
    String userID = LoginActivity.userID;

    private static final int PICK_FROM_ALBUM = 1;
    // tempFile 에 받아온 이미지를 저장
    private File tempFile;
    Uri photoUri;
    Bitmap bmRotated, risizebitmap;
    int orientation;

    String imgpath; // 이미지 절대경로

    LinearLayout write_contents;
    EditText write_text; // 제목
    EditText write_title; // 내용
    private AlertDialog dialog;
    RecyclerView write_image_recyclerview;

    ArrayList<WriteItemVO> arraylist = new ArrayList<WriteItemVO>();
    ArrayList<String> imgpathlist = new ArrayList<>();
    Button write_gallery, write_cancel, write_upload, write_camera;

    StringBuffer imgurl = new StringBuffer();

    ArrayList<String> orientationList = new ArrayList<>();
    String orientationString = "";

    ProgressDialog progressDialog; // API 26에서 deprecated
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        // 카메라 권한 물어보기
        tedPermission();
        if(Build.VERSION.SDK_INT>22){
            requestPermissions(new String[] {WRITE_EXTERNAL_STORAGE}, 1);
            requestPermissions(new String[] {READ_EXTERNAL_STORAGE}, 1);
        }

        init();
        Toast.makeText(this, userID, Toast.LENGTH_SHORT).show();
        write_contents.setOnClickListener(this);
        write_gallery.setOnClickListener(this);
        write_cancel.setOnClickListener(this);
        write_upload.setOnClickListener(this);
        write_camera.setOnClickListener(this);
        write_image_recyclerview = findViewById(R.id.write_image_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        write_image_recyclerview.setLayoutManager(layoutManager);
       /* write_image_recyclerview.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
*/
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    // 갤러리에서 선택한 사진 정보를 액티비티로 받아올때
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM) {
            if (data != null) {
                photoUri = data.getData();
                Cursor cursor = null;
                try {
                    /*
                     *  Uri 스키마를
                     *  content:/// 에서 file:/// 로  변경한다.
                     */
                    String[] proj = {MediaStore.Images.Media.DATA};
                    assert photoUri != null;
                    // Content Resolver은 결과를 반환해주는 브릿지 역할을 하게 된다.
                    // https://www.crocus.co.kr/1602
                    // Content Resolver을 이용한 query
                    // Cursor 설명 https://pulsebeat.tistory.com/15
                    cursor = getContentResolver().query(photoUri, proj, null, null, null);
                    assert cursor != null;
                    // getColumnIndexOrThrow: 특정 필드의 인덱스값을 반환하며, 필드가 존재하지 않을경우 예외를 발생시킵니다.
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    // moveToFirst: 커서가 쿼리(질의) 결과 레코드들 중에서 가장 처음에 위치한 레코드를 가리키도록 합니다.
                    cursor.moveToFirst();
                    tempFile = new File(cursor.getString(column_index));
                    Log.d("tempFile", cursor.getString(column_index));

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

                setImage();
            }else{
                return;
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.write_contents:
                write_text.requestFocus();
                break;
            case R.id.write_gallery:
                goToAlbum();
                break;
            case R.id.write_cancel:
                finish();
                break;
            case R.id.write_upload:
                if(write_title.getText().toString().equals("")){
                    Toast.makeText(this, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }else if(write_text.getText().toString().equals("")){
                    Toast.makeText(this, "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    UploadImg();
                    UploadText();
                }
                break;
            case R.id.write_camera:
                Toast.makeText(this, "준비중입니다. ㅠ_ㅠ", Toast.LENGTH_SHORT).show();
        }
    }


    // 갤러리로 간다
    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    private void setImage() {
        imgpath = getRealpath(photoUri);
        Log.d("imgpath", imgpath);
        ExifInterface exif = null;
        try {
            // ExifInterface란 이미지가 갖고 있는 정보의 집합 클래스다. 이미지가 갖고 있는 상세정보를 추출할 때 필요하다.
            // https://g-y-e-o-m.tistory.com/103
            exif = new ExifInterface(imgpath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        orientationList.add(Integer.toString(orientation));
        // Bitmap 설명
        // https://mainia.tistory.com/468
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

        Log.d("tempFile", tempFile.getAbsolutePath());
       // Bitmap b = BitmapFactory.decodeByteArray(originalBm, 0, originalBm.lenght);
        imgpathlist.add(tempFile.getAbsolutePath()); // 이미지경로를 담는 리스트
        bmRotated = rotateBitmap(originalBm, orientation);

        arraylist.add(new WriteItemVO(bmRotated));
        Log.d("arraylistSize", arraylist.size() + "");
        for(int i = 0; i < arraylist.size(); i++){
            Log.d("arraylistGet", arraylist.get(i) + "");
        }


        if(arraylist != null){
            write_image_recyclerview.setVisibility(View.VISIBLE);
        }
        WriteRecyclerViewAdapter adapter = new WriteRecyclerViewAdapter(arraylist);
        write_image_recyclerview.setAdapter(adapter);
    }

    private void tedPermission() {
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();


        }
    }

    // 갤러리에서 사진 가져왔을떄 사진이 회전되어서 보이는거 방지 함수
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    // Uri 절대경로 구하는 함수
    public String getRealpath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(uri, proj, null, null, null);
        int index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        c.moveToFirst();
        String path = c.getString(index);

        return path;
    }

    public void UploadImg(){
        for(int i = 0; i<imgpathlist.size(); i++){
            Log.d("imgpathlist",imgpathlist.get(i));
            Log.d("imgpathlist",imgpathlist.size()+ "");

            String imagePath = imgpathlist.get(i).toString();
            long now = System.currentTimeMillis();
            Date mDate = new Date(now);
            SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String getTime = simpleDate.format(mDate);
            getTime = getTime.replaceAll("-", "");
            getTime = getTime.replaceAll(":", "");
            getTime = getTime.replaceAll(" ", "");
            Log.d("time", getTime);
            String ImageUploadURL = "http://112.146.241.138:80/serverimg/uploadimg.php";
            SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
            String idx = pref.getString("idx", i+ userID+ getTime + "");

            imgurl.append("http://112.146.241.138/serverimg/photos/" + i+userID+getTime+ ".jpg"+ " ");

            ImageUploadTask imageUploadTask = new ImageUploadTask();
            imageUploadTask.execute(ImageUploadURL, imagePath, idx);
        }
    }

    public void UploadText() {
        String title = write_title.getText().toString();
        String text = write_text.getText().toString();

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String getTime = simpleDate.format(mDate);
        getTime.replace("-","");
        getTime.replace(" ","");
        Log.d("time", getTime);

        Response.Listener<String> responseLister = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(WriteActivity.this);
                        dialog = builder.setMessage("로그인에 성공했습니다.")
                                .setPositiveButton("확인", null)
                                .create();
                        dialog.show();

                        finish();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(WriteActivity.this);
                        dialog = builder.setMessage("계정을 다시 확인하세요")
                                .setNegativeButton("다시 시도", null)
                                .create();
                        dialog.show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        for(int i = 0; i < orientationList.size(); i++){
            Log.d("orientationList", orientationList.get(i));
            orientationString += orientationList.get(i) + " ";
        }

        if(orientationString != "") {
            orientationString = orientationString.substring(0, orientationString.length() - 1);
        }

        if(imgurl.toString().equals("")){
            imgurl.append("http://112.146.241.138/serverimg/photos/noimage.png");
        }

        BoardRequest boardRequest = new BoardRequest(title, userID, getTime, imgurl, text, orientationString,responseLister);
        Log.d("Integercheck", Integer.toString(orientation));
        RequestQueue queue = Volley.newRequestQueue(WriteActivity.this);
        queue.add(boardRequest);

    }

    public void init(){
        write_upload = findViewById(R.id. write_upload);
        write_contents = findViewById(R.id.write_contents);
        write_text = findViewById(R.id.write_text);
        write_title = findViewById(R.id.write_title);
        write_gallery = findViewById(R.id.write_gallery);
        write_cancel = findViewById(R.id.write_cancel);
        write_camera = findViewById(R.id.write_camera);
    }

    private  class ImageUploadTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(WriteActivity.this);
            progressDialog.setMessage("이미지 업로드중....");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                JSONObject jsonObject = JSONParser.uploadImage(params[0],params[1],params[2]);
                if (jsonObject != null)
                    return jsonObject.getString("result").equals("success");

            } catch (JSONException e) {
                Log.i("TAG", "Error : " + e.getLocalizedMessage());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (progressDialog != null)
                progressDialog.dismiss();

            if (aBoolean){
                Toast.makeText(getApplicationContext(), "파일 업로드 성공", Toast.LENGTH_LONG).show();
            }  else{
                Toast.makeText(getApplicationContext(), "파일 업로드 실패", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    protected void onDestroy() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }
}
