package com.sangwoo.simplecommunity.ImageUpload;

import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JSONParser {

    public static JSONObject uploadImage(String imageUploadUrl, String sourceImageFile, String idx){
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
        Log.d("uploadImage", imageUploadUrl + "  " + sourceImageFile + "  " + idx);
        try {
            File sourceFile = new File(sourceImageFile);
            Log.d("TAG", "File...::::" + sourceFile + " : " + sourceFile.exists());
            String filename = sourceImageFile.substring(sourceImageFile.lastIndexOf("/")+1);
            Log.d("123123", "1. " + sourceFile + " 2. " +sourceFile.getAbsolutePath());
            // OKHTTP3
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("uploaded_file", filename, RequestBody.create(MEDIA_TYPE_PNG, sourceFile))
                    .addFormDataPart("idx", idx)
                    .build();


            Request request = new Request.Builder()
                    .url(imageUploadUrl)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            if (response != null) {
                if (response.isSuccessful()) {
                    String res = response.body().string();
                    Log.e("TAG", "Error: " + res);
                    return new JSONObject(res);
                }
            }
        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e("TAG", "Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e("TAG", "Other Error: " + e.getLocalizedMessage());
        }
        return null;
    }
}