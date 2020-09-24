package com.sangwoo.simplecommunity.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.sangwoo.simplecommunity.Adapter.BoardCommentRecyclerViewAdapter;
import com.sangwoo.simplecommunity.Request.BoardCommentRequest;
import com.sangwoo.simplecommunity.VO.BoardCommentVO;
import com.sangwoo.simplecommunity.VO.BoardExample;
import com.sangwoo.simplecommunity.Adapter.BoardExampleRecyclerViewAdapter;
import com.sangwoo.simplecommunity.VO.BoardRotatedExample;
import com.sangwoo.simplecommunity.Interface.CommentCountSelect;
import com.sangwoo.simplecommunity.Request.DeleteCommentRequest;
import com.sangwoo.simplecommunity.R;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BoardActivity extends AppCompatActivity implements BoardExampleRecyclerViewAdapter.BoardExampleRecyclerViewClickListener, BoardCommentRecyclerViewAdapter.BoardCommentRecyclerLongClickListener, CommentCountSelect {
    TextView board_title;
    TextView board_user;
    TextView board_time;
    TextView board_text;
    EditText board_comment;
    Button board_comment_upload;
    TextView board_comment_count;

    RecyclerView board_image_recyclerview;
    BoardExampleRecyclerViewAdapter adapter;

    RecyclerView board_comment_lecyclerview;
    BoardCommentRecyclerViewAdapter comment_adapter;

    List<BoardExample> BoardExamplelist = new ArrayList<>();
    List<BoardRotatedExample> BoardRotatedExamplelist = new ArrayList<>();
    List<BoardCommentVO> BoardCommentlist = new ArrayList<>();
    String[] Allimg;
    byte[] bytes = null;

    int idx;
    int board_idx;
    String seq;
    String user;
    String data;
    String title;
    String Strpicture;
    String content;
    String seq_count_imp;
    String seq_count_task;
    String seq_count_seq;
    String seq_count;

    String select_board_idx;
    String select_seq;
    String select_user;
    String select_content;
    String select_data;
    String select_delete_confirm;

    String Update_Comment_count_Task;
    String Update_Comment_count;

    String[] orientationarray;

    String Comment_Select_idx;

    int orientation;

    BoardExample boardExample;
    BoardRotatedExample boardRotatedExample;

    Bitmap oneImage;
    Bitmap bmRotated;
    int count , count2, count3;

    int BoardCommentlist_size;

    BoardCommentSelect boardCommentSelect;

    private AlertDialog.Builder dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        init();

        // 처음 시작했을때 EditText에 키보드 보이는거 방지
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(board_comment.getWindowToken(),0);

        board_comment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                board_comment.setInputType(1);
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(board_comment, 0);

            }
        });

        Intent intent = getIntent();
        idx = intent.getExtras().getInt("idx");
        user = intent.getExtras().getString("user");
        data = intent.getExtras().getString("data");
        title = intent.getExtras().getString("title");
        Strpicture = intent.getExtras().getString("Strpicture");
        content = intent.getExtras().getString("content");
        count = intent.getExtras().getInt("count");
        orientationarray = intent.getStringArrayExtra("orientationarray");
        for(int i = 0; i < orientationarray.length; i++){
            Log.d("arrayCheck_Board", orientationarray[i] + " / 제발");
        }

        Log.d("BoardintentCheck", user + " /" + data + " /" + title + " /" + Strpicture + " /" + content + " /" + orientationarray[0]);

        board_user.setText(user);
        board_time.setText(data);
        board_title.setText(title);
        board_text.setText(content);
        if(Strpicture != null) {
            Allimg = Strpicture.split(" ");
            board_image_recyclerview.setVisibility(View.VISIBLE);
        }


        try {
            Img img = new Img();
            BoardExamplelist = img.execute().get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 이미지 가져오기
        for(int i = 0; i < BoardExamplelist.size(); i++){
            boardExample = BoardExamplelist.get(i);
             if(!orientationarray[i].equals("")) {
                orientation = Integer.parseInt(orientationarray[i]);

                oneImage = boardExample.getPicture();

                bmRotated = rotateBitmap(oneImage, orientation);
                BoardRotatedExamplelist.add(new BoardRotatedExample(bmRotated));
            }
        }

        //댓글 작성 완료버튼을 눌렀을때
        board_comment_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(board_comment.getText().toString().equals("")){
                    Toast.makeText(BoardActivity.this, "댓글을 입력해 주세요", Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        CommentCount commentCount = new CommentCount();
                        seq_count_seq = commentCount.execute().get();

                        UpdateCommentCount updateCommentCount = new UpdateCommentCount();
                        Update_Comment_count = updateCommentCount.execute().get();
                        UploadText();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    board_comment.setText("");
                    //키보드 없앤다
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(board_comment.getWindowToken(),0);
                }

            }
        });


        boardCommentSelect = new BoardCommentSelect(this);
        boardCommentSelect.execute();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BoardActivity.this);
        board_image_recyclerview.setLayoutManager(layoutManager);

        adapter = new BoardExampleRecyclerViewAdapter(BoardRotatedExamplelist);
        board_image_recyclerview.setAdapter(adapter);


    }

    @Override
    public void onItemClicked(int position) {
        Toast.makeText(this, "클릭", Toast.LENGTH_SHORT).show();
    }

    // 댓글 롱 클릭 리스너
    // 댓글 삭제
    @Override
    public void onItemLongClicked(final int position) {
        BoardCommentVO item = BoardCommentlist.get(position);
        dialog = new AlertDialog.Builder(BoardActivity.this);

        board_idx = item.getIdx();
        seq = item.getSeq();
        final String delete_user = item.getUser();

        dialog.setTitle("삭제").setMessage("정말로 삭제 하시겠습니까?");

        dialog.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(LoginActivity.userID.equals(delete_user)) {
                    BoardCommentlist.remove(position);
                    DeleteComment();
                    try {
                        UpdateCommentCount updateCommentCount = new UpdateCommentCount();
                        Update_Comment_count = updateCommentCount.execute().get();

                        int j = Integer.parseInt(Update_Comment_count);

                        board_comment_count.setText(j+"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Toast.makeText(BoardActivity.this, "삭제되었습니다", Toast.LENGTH_SHORT).show();

                    comment_adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(BoardActivity.this, "본인이 작성한 게시물만 삭제 할 수 있습니다.", Toast.LENGTH_SHORT).show();
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

    public void init() {
        board_title = findViewById(R.id.board_title);
        board_user = findViewById(R.id.board_user);
        board_time = findViewById(R.id.board_time);
        board_text = findViewById(R.id.board_text);
        board_image_recyclerview = findViewById(R.id.board_image_recyclerview);

        board_comment = findViewById(R.id.board_comment);
        board_comment_upload = findViewById(R.id.board_comment_upload);
        board_comment_lecyclerview = findViewById(R.id.board_comment_lecyclerview);
        board_comment_count = findViewById(R.id.board_comment_count);

    }

    @Override
    public void comment_count_select(String s) {
        seq_count = s;
        Log.d("seq_count", seq_count);

    }




    public class Img extends AsyncTask<Bitmap, Bitmap,  List<BoardExample>>{
        byte[] byteArray;
        Bitmap bm;

        @SuppressLint("WrongThread")
        @Override
        protected  List<BoardExample> doInBackground(Bitmap... voids) {
            try {
                for(int i = 0; i < Allimg.length; i++) {
                    URL url = new URL(Allimg[i]);
                    URLConnection conn = url.openConnection();
                    conn.connect();
                    BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                    bm = BitmapFactory.decodeStream(bis);
                    Log.d("bm!!", bm + "");
                    bis.close();
                    BoardExamplelist.add(new BoardExample(bm));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return BoardExamplelist;
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

    // 댓글 업로드
    public void UploadText() {
        String comment = board_comment.getText().toString();

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
                        Toast.makeText(BoardActivity.this, " 댓글작성이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(BoardActivity.this, " 댓글작성이 실패되었습니다", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        /*BoardCommentSelect boardCommentSelect2 = new BoardCommentSelect(this);
        boardCommentSelect2.execute();*/

        int i = Integer.parseInt(seq_count_seq);
        i++;

        int j = Integer.parseInt(Update_Comment_count);
        j++;
        board_comment_count.setText(j+"");
        seq_count_seq = String.valueOf(i);
        BoardCommentRequest boardCommentRequest = new BoardCommentRequest(idx, LoginActivity.userID, getTime, comment, seq_count_seq, responseLister);
        RequestQueue queue = Volley.newRequestQueue(BoardActivity.this);
        queue.add(boardCommentRequest);

        BoardCommentlist.add(new BoardCommentVO(idx, LoginActivity.userID, getTime, comment, seq_count_seq, "1"));
        comment_adapter.notifyDataSetChanged();

        BoardCommentlist_size = BoardCommentlist.size();
        Log.d("BoardCommentlist_size", BoardCommentlist_size+"");

    }

    // 댓글을 읽어온다
    public class BoardCommentSelect extends AsyncTask<Void, Void, String> {
        ProgressDialog asyncDialog = new ProgressDialog(BoardActivity.this);
        // Count를 메인Activity로 가져오기위한 인터페이스
        private CommentCountSelect commentCountSelect;

        public BoardCommentSelect(CommentCountSelect commentCountSelect){
            this.commentCountSelect = commentCountSelect;
        }

        @Override
        protected void onPreExecute() {
            try {
                Comment_Select_idx = "http://112.146.241.138:80/serverimg/commentselect.php?idx=" + URLEncoder.encode(String.valueOf(idx), "UTF-8");
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
                URL url = new URL(Comment_Select_idx);
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
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("response"); // 댓글 검색
                JSONArray jsonArray2 = jsonObject.getJSONArray("response2");
                JSONArray jsonArray3 = jsonObject.getJSONArray("response3");
                count = 0;
                count2 = 0;
                count3 = 0;
                Log.d("jsonArray.length()", jsonArray.length() + "");
                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    select_board_idx = object.getString("board_idx");
                    select_seq = object.getString("seq");
                    select_user = object.getString("user");
                    select_content = object.getString("content");
                    select_data = object.getString("create_date");
                    select_delete_confirm = object.getString("delete_confirm");
                    if(select_delete_confirm.equals("1")) {
                        BoardCommentlist.add(new BoardCommentVO(idx, select_user, select_data, select_content, select_seq, "1"));
                    }

                    count++;
                }

                /*while (count2 < jsonArray2.length()) {
                    JSONObject object = jsonArray2.getJSONObject(count2);
                    seq_count_imp = object.getString("count(*)");
                    // BoardCommentlist.add(new BoardCommentVO(user, data, title));
                    count2++;
                }*/

                while (count3 < jsonArray3.length()) {
                    JSONObject object = jsonArray3.getJSONObject(count3);
                    seq_count_imp = object.getString("count(*)");
                    // BoardCommentlist.add(new BoardCommentVO(user, data, title));
                    count3++;
                }

                board_comment_count.setText(seq_count_imp);

                RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(BoardActivity.this);
                board_comment_lecyclerview.setLayoutManager(layoutManager2);

                comment_adapter = new BoardCommentRecyclerViewAdapter(BoardCommentlist);
                comment_adapter.setOnLongClickListener(BoardActivity.this);
                board_comment_lecyclerview.setAdapter(comment_adapter);
                Log.d("BoardCommentlistsize", BoardCommentlist.size()+"");


                comment_adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 인터페이스를 이용하여 seq_count_imp값을 메인Activity로 가져온다.
            // https://stackoverflow.com/questions/13815807/return-value-from-asynctask-class-onpostexecute-method
            commentCountSelect.comment_count_select(seq_count_imp);

            asyncDialog.dismiss();
        }
    }

    // 댓글 삭제
    public void DeleteComment() {
        Response.Listener<String> responseLister = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {

                    } else {
                        Toast.makeText(BoardActivity.this, "삭제 실패", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        DeleteCommentRequest commentRequest = new DeleteCommentRequest(board_idx, seq, responseLister);
        RequestQueue queue = Volley.newRequestQueue(BoardActivity.this);
        queue.add(commentRequest);
    }

    public class CommentCount extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try{
                Comment_Select_idx = "http://112.146.241.138:80/serverimg/commentselect.php?idx=" + URLEncoder.encode(String.valueOf(idx), "UTF-8");

                int comment_count = 0;

                URL url = new URL(Comment_Select_idx);
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

                JSONObject jsonObject = new JSONObject(stringBuilder.toString().trim());
                JSONArray jsonArray2 = jsonObject.getJSONArray("response2");

                while (comment_count < jsonArray2.length()) {
                    JSONObject object = jsonArray2.getJSONObject(comment_count);
                    seq_count_task = object.getString("count(*)");
                    // BoardCommentlist.add(new BoardCommentVO(user, data, title));
                    comment_count++;
                }

                Log.d("seq_count_imp2", seq_count_task);

            }catch (Exception e){
                e.printStackTrace();
            }
            return seq_count_task;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            cancel(true);
        }
    }


    // 댓글 업로드 했을때 댓글 총 갯수 가져오기
    public class UpdateCommentCount extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try{
                Comment_Select_idx = "http://112.146.241.138:80/serverimg/commentselect.php?idx=" + URLEncoder.encode(String.valueOf(idx), "UTF-8");

                int comment_count = 0;

                URL url = new URL(Comment_Select_idx);
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

                JSONObject jsonObject = new JSONObject(stringBuilder.toString().trim());
                JSONArray jsonArray3 = jsonObject.getJSONArray("response3");

                while (comment_count < jsonArray3.length()) {
                    JSONObject object = jsonArray3.getJSONObject(comment_count);
                    Update_Comment_count_Task = object.getString("count(*)");
                    // BoardCommentlist.add(new BoardCommentVO(user, data, title));
                    comment_count++;
                }

                Log.d("seq_count_imp2", Update_Comment_count_Task);

            }catch (Exception e){
                e.printStackTrace();
            }
            return Update_Comment_count_Task;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            cancel(true);
        }
    }

}
