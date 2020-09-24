package com.sangwoo.simplecommunity.Fragment;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.sangwoo.simplecommunity.Activity.PrivacyActivity;
import com.sangwoo.simplecommunity.Adapter.MyRecyclerAdapter;
import com.sangwoo.simplecommunity.Activity.BoardActivity;
import com.sangwoo.simplecommunity.Activity.WriteActivity;
import com.sangwoo.simplecommunity.R;
import com.sangwoo.simplecommunity.Request.DeleteBoardRequest;
import com.sangwoo.simplecommunity.VO.AllBoard;
import com.sangwoo.simplecommunity.VO.Board;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommunityFragment extends Fragment implements MyRecyclerAdapter.MyRecyclerViewClickListener, MyRecyclerAdapter.MyRecyclerViewLongClickListener , View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    FrameLayout frameLayout;

    private Animation fab_open, fab_close;
    private FloatingActionButton fab_sub, fab_sub_add, fab_sub_select;
    private Boolean isFabOpen = false;

    private Boolean activityboolean = false;
    String target;
    String refresh_target;
    String SearchValue; // 검색값
    String userid; // 접속자 아이디

    List<AllBoard> allBoardList = new ArrayList<>();
    List<Board> BoardList = new ArrayList<>();

    int count;
    int pageing_count = 1;
    int pageing = 10; // 이 숫자마다 페이징 ex) 10으로 하면 10개 보이고 로딩 후 다시 10개
    int refresh_count = 0;

    RecyclerView recyclerView;
    MyRecyclerAdapter adapter;

    String[] img1;
    String[] orientationarray;
    Bitmap bm = null;

    int maxidx;
    int idx;

    String user;
    String data;
    String title;
    String picture;
    String content;
    String orientation;
    String delete_confirm;

    int count2;

    SharedPreferences pref;

    SwipeRefreshLayout mSwipeRefreshLayout;

    private AlertDialog.Builder dialog;

    public CommunityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_community, container, false);
        recyclerView = v.findViewById(R.id.recycler_view);

        pref = this.getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        userid = pref.getString("userID", null);
        Log.d("pref_id", userid);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        /*layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);*/

        adapter = new MyRecyclerAdapter(allBoardList);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("onScrolled", " / 제발");
                int totalCount = recyclerView.getAdapter().getItemCount() - 1;
                int firstPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                Log.d("firstPosition", firstPosition + " / firstPosition  " + totalCount);
                if (totalCount == firstPosition) {
                    int count_size = 0;
                    BoardViewPageing pageing = new BoardViewPageing();
                    pageing.execute();
                }
            }
        });


        adapter.setOnClickListener(this);
        adapter.setOnLongClickListener(this);


        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);

        frameLayout = v.findViewById(R.id.frameLayout);

        fab_sub = v.findViewById(R.id.fab_sub);
        fab_sub_add = v.findViewById(R.id.fab_sub_add);
        fab_sub_select = v.findViewById(R.id.fab_sub_select);
        fab_sub.setOnClickListener(this);
        fab_sub_add.setOnClickListener(this);
        fab_sub_select.setOnClickListener(this);

        BoardView boardView = new BoardView();
        boardView.execute();
        return v;
    }

    // 리사이클러뷰 리스트를 클릭했을때 호출됨
    @Override
    public void onItemClicked(int position) {
        Board item = BoardList.get(position);

        /*SharedPreferences.Editor editor = pref.edit(); // editor에 put하기
        editor.putString("user", item.getUser());
        editor.putString("user", item.getData());
        editor.putString("user", item.getTitle());
        editor.putString("user", item.getStrpicture());
        editor.putString("user", item.getContent());
        editor.commit();*/

        Intent intent = new Intent(getActivity(), BoardActivity.class);
        intent.putExtra("idx", item.getIdx());
        intent.putExtra("user", item.getUser());
        intent.putExtra("data", item.getData());
        intent.putExtra("title", item.getTitle());
        intent.putExtra("Strpicture", item.getStrpicture());
        intent.putExtra("content", item.getContent());
        intent.putExtra("count", count2);
        intent.putExtra("orientationarray", BoardList.get(position).getOrientationarray());

        for (int i = 0; i < orientationarray.length; i++) {
            Log.d("orientationarrayCheck", orientationarray[i] + " / 제발");

        }
        Log.d("intentCheck", item.getUser() + " /" + item.getData() + " /" + item.getTitle() + " /" + item.getStrpicture() + " /" + item.getContent());

        startActivity(intent);
    }

    // 리사이클러뷰 리스트를 롱클릭했을때 호출됨
    @Override
    public void onItemLongClicked(final int position) {
        Board item = BoardList.get(position);
        dialog = new AlertDialog.Builder(getActivity());

        idx = item.getIdx();
        final String delete_user = item.getUser();

        dialog.setTitle("삭제").setMessage("정말로 삭제 하시겠습니까?");

        dialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(userid.equals(delete_user)) {
                    BoardList.remove(position);
                    allBoardList.remove(position);
                    DeleteBoard();
                    Toast.makeText(getContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();

                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(getContext(), "본인이 작성한 게시물만 삭제 할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    // 플로팅 버튼 클릭 이벤트
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fab_sub:
                anim();
                break;
            case R.id.fab_sub_add:
                anim();
                Intent intent = new Intent(getActivity(), WriteActivity.class);
                startActivity(intent);
                break;
            case R.id.fab_sub_select:
                anim();
                Privacy();
                Toast.makeText(getActivity(), "개인 정보", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // 플로팅 버튼 생성 및 제거
    public void anim() {

        if (isFabOpen) {
            fab_sub_add.startAnimation(fab_close);
            fab_sub_select.startAnimation(fab_close);
            fab_sub_add.setClickable(false);
            fab_sub_select.setClickable(false);
            isFabOpen = false;
        } else {
            fab_sub_add.startAnimation(fab_open);
            fab_sub_select.startAnimation(fab_open);
            fab_sub_add.setClickable(true);
            fab_sub_select.setClickable(true);
            isFabOpen = true;
        }
    }


    public class BoardView extends AsyncTask<Void, Void, String> {
        ProgressDialog asyncDialog = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            try {
                target = "http://112.146.241.138:80/serverimg/allboard.php";
            } catch (Exception e) {
                e.printStackTrace();
            }
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("잠시만 기다려 주세요...");
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = jsonArray.length() - pageing_count;
                int n = 0;
                Log.d("jsonArray.length()", jsonArray.length() + "");
                Log.d("잘되나", "BoardView");
                boolean maxidxcheck = true;
                allBoardList.clear();
                BoardList.clear();
                while (count >= jsonArray.length() - pageing) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    if (maxidxcheck) {
                        maxidx = object.getInt("idx");
                        maxidxcheck = false;
                    }
                    idx = object.getInt("idx");
                    user = object.getString("user");
                    data = object.getString("data");
                    title = object.getString("title");
                    picture = object.getString("picture");
                    Log.d("picture", picture);
                    content = object.getString("content");
                    orientation = object.getString("orientation");
                    Log.d("orientationCheck", orientation + "번");
                    delete_confirm = object.getString("delete_confirm");
                    Log.d("delete_confirm", delete_confirm + "번");
                    img1 = picture.split(" ");
                    orientationarray = orientation.split(" ");
                    count2 = img1.length;

                    for (int i = 0; i < orientationarray.length; i++) {
                        Log.d("orientationarrayCheck2", orientationarray[i] + " / 제발" + i + "번");
                    }

                    for (int i = 0; i < img1.length; i++) {
                        Log.d("img1", img1[i] + "  " + i);

                    }
                    Img img = new Img();
                    bm = img.execute().get();

                    if (delete_confirm.equals("1")) {
                        // 사진이 있을때
                        if (picture != null) {
                            // 사진이 없을때
                            if (picture.equals("")) {
                                allBoardList.add(new AllBoard(idx, user, data, title, content));
                                BoardList.add(new Board(idx, user, data, title, content, orientationarray));
                            } else {
                                Log.d("bm!", bm + "");
                                allBoardList.add(new AllBoard(idx, user, data, title, bm, content));
                                BoardList.add(new Board(idx, user, data, title, picture, content, orientationarray));
                            }
                        }
                    }

                    count--;
                    adapter.notifyDataSetChanged();
                    pageing_count++;
                    n++;
                }
                pageing = pageing + n;
            } catch (Exception e) {
                e.printStackTrace();
            }
            asyncDialog.dismiss();
        }
    }


    public class Img extends AsyncTask<Bitmap, Bitmap, Bitmap> {
        @SuppressLint("WrongThread")
        @Override
        protected Bitmap doInBackground(Bitmap... voids) {
            try {
                URL url = new URL(img1[0]);
                URLConnection conn = url.openConnection();
                conn.connect();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                bm = BitmapFactory.decodeStream(bis);

                bm = rotateBitmap(bm, Integer.parseInt(orientation));
                Log.d("bm!!", bm + "");
                bis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bm;
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
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onRefresh() {
        RefreshBoardView refreshBoardView = new RefreshBoardView();
        refreshBoardView.execute();
        for (int i = 0; i < allBoardList.size(); i++) {
            Log.d("allBoardList", allBoardList.get(i).getPicture() + " " + i + "번째");
        }

        adapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public class RefreshBoardView extends AsyncTask<Void, Void, String> {
        ProgressDialog asyncDialog = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            try {
                refresh_target = "http://112.146.241.138:80/serverimg/allboardrefresh.php?idx=" + URLEncoder.encode(String.valueOf(maxidx), "UTF-8");
                Log.d("idx", String.valueOf(idx));
            } catch (Exception e) {
                e.printStackTrace();
            }
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("잠시만 기다려 주세요...");
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(refresh_target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                count = 0;
                boolean maxidxcheck = true;
                Log.d("jsonArray.length()", jsonArray.length() + "");
                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    maxidx = object.getInt("idx");
                    idx = object.getInt("idx");
                    user = object.getString("user");
                    data = object.getString("data");
                    title = object.getString("title");
                    picture = object.getString("picture");
                    Log.d("picture", picture);
                    content = object.getString("content");
                    orientation = object.getString("orientation");

                    img1 = picture.split(" ");
                    orientationarray = orientation.split(" ");
                    count2 = img1.length;

                    for (int i = 0; i < img1.length; i++) {
                        Log.d("img1", img1[i] + "  " + i);

                    }
                    Img img = new Img();
                    bm = img.execute().get();
                    // 사진이 있을때
                    if (picture != null) {
                        // 사진이 없을때
                        if (picture.equals("")) {
                            allBoardList.add(0, new AllBoard(idx, user, data, title, content));
                            BoardList.add(0, new Board(idx, user, data, title, content, orientationarray));
                        } else {
                            Log.d("bm!", bm + "");
                            allBoardList.add(0, new AllBoard(user, data, title, bm, content));
                            BoardList.add(0, new Board(idx, user, data, title, picture, content, orientationarray));
                        }
                    }

                    count++;
                    refresh_count++;
                    adapter.notifyDataSetChanged();
                    // recyclerView.scrollToPosition(allBoardList.size()-1);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            asyncDialog.dismiss();
        }
    }

    public class BoardViewPageing extends AsyncTask<Void, Void, String> {
        ProgressDialog asyncDialog = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            try {
                target = "http://112.146.241.138:80/serverimg/allboard.php";
            } catch (Exception e) {
                e.printStackTrace();
            }
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("잠시만 기다려 주세요...");
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = jsonArray.length() - (pageing_count + refresh_count);
                int n = 0;
                Log.d("jsonArray.length()", jsonArray.length() + "");
                Log.d("refresh_count", refresh_count + "");
                boolean maxidxcheck = true;
                while (count >= jsonArray.length() - pageing) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    idx = object.getInt("idx");
                    user = object.getString("user");
                    data = object.getString("data");
                    title = object.getString("title");
                    picture = object.getString("picture");
                    Log.d("picture", picture);
                    content = object.getString("content");
                    orientation = object.getString("orientation");
                    Log.d("orientationCheck", orientation + "번");
                    img1 = picture.split(" ");
                    orientationarray = orientation.split(" ");
                    count2 = img1.length;

                    for (int i = 0; i < orientationarray.length; i++) {
                        Log.d("orientationarrayCheck2", orientationarray[i] + " / 제발" + i + "번");
                    }

                    for (int i = 0; i < img1.length; i++) {
                        Log.d("img1", img1[i] + "  " + i);

                    }
                    Img img = new Img();
                    bm = img.execute().get();
                    // 사진이 있을때
                    if (picture != null) {
                        // 사진이 없을때
                        if (picture.equals("")) {
                            allBoardList.add(new AllBoard(idx, user, data, title, content));
                            BoardList.add(new Board(idx, user, data, title, content, orientationarray));
                        } else {
                            Log.d("bm!", bm + "");
                            allBoardList.add(new AllBoard(user, data, title, bm, content));
                            BoardList.add(new Board(idx, user, data, title, picture, content, orientationarray));
                        }
                    }
                    count--;
                    adapter.notifyDataSetChanged();
                    pageing_count++;
                    n++;
                }

                pageing = pageing + n;


            } catch (Exception e) {
                e.printStackTrace();
            }
            asyncDialog.dismiss();
        }
    }

    public void DeleteBoard() {
        Response.Listener<String> responseLister = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {

                    } else {
                        Toast.makeText(getContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        DeleteBoardRequest boardRequest = new DeleteBoardRequest(idx, responseLister);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(boardRequest);
    }

    public void Privacy(){
        Intent intent = new Intent(getContext(), PrivacyActivity.class);
        startActivity(intent);
    }

}
