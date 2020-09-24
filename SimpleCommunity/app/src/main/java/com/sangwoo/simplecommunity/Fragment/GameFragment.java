package com.sangwoo.simplecommunity.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sangwoo.simplecommunity.R;
import com.sangwoo.simplecommunity.ShootingGame.BackScrollActivity;
import com.sangwoo.simplecommunity.ShootingGame.RankingRecyclerAdapter;
import com.sangwoo.simplecommunity.ShootingGame.RankingVO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GameFragment extends Fragment {

    Button GameStartBtn;
    RecyclerView recyclerView;
    RankingRecyclerAdapter adapter;

    List<RankingVO> Ranklist = new ArrayList<>();

    String target;

    String user;
    String score;
    String data;

    public GameFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_ranking);

        GameStartBtn = view.findViewById(R.id.GameStartBtn);

        GameStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BackScrollActivity.class);
                startActivity(intent);
            }
        });

        RankingSelect rankingSelect = new RankingSelect();
        rankingSelect.execute();

        return view;
    }

    public class RankingSelect extends AsyncTask<Void, Void, String> {
        ProgressDialog asyncDialog = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            try {
                target = "http://112.146.241.138:80/serverimg/rankingselect.php";
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
                int count = 0;

                while (count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    user = object.getString("user");
                    score = object.getString("score");
                    data = object.getString("data");

                    count++;

                    Ranklist.add(0, new RankingVO(user, score, data));

                }

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);

                adapter = new RankingRecyclerAdapter(Ranklist);
                recyclerView.setAdapter(adapter);


            } catch (Exception e) {
                e.printStackTrace();
            }
            asyncDialog.dismiss();
        }
    }
}
