package ua.kpi.comsys.iv8114.mobiledev.lab3;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import ua.kpi.comsys.iv8114.mobiledev.R;

public class Lab3 extends Fragment {
    private View root;
    private ArrayList<ConstraintLayout> moviesLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.lab3, container, false);
        LinearLayout movieLinear = root.findViewById(R.id.scroll_lay);
        moviesLayout = new ArrayList<>();

        try {
            ArrayList<Movie> movies = parseMovies(readTextFile(root.getContext(), R.raw.movies));
            for (Movie movie : movies) {
                moviesLayout.add(new MovieList(root.getContext(), movieLinear, movie).moviePack);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        changeLaySizes();

        return root;
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        changeLaySizes();
    }

    private void changeLaySizes(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) root.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        for (ConstraintLayout moviesList :
                moviesLayout) {
            moviesList.getChildAt(0).setLayoutParams(
                    new ConstraintLayout.LayoutParams(300, 400));
        }
    }

    public static String readTextFile(Context context, @RawRes int id){
        InputStream inputStream = context.getResources().openRawResource(id);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int size;
        try {
            while ((size = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, size);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            System.err.println("File can not be read!");
            e.printStackTrace();
        }
        return outputStream.toString();
    }

    private ArrayList<Movie> parseMovies(String jsonText) throws ParseException {
        ArrayList<Movie> result = new ArrayList<>();

        org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) new JSONParser().parse(jsonText);

        org.json.simple.JSONArray movies = (JSONArray) jsonObject.get("Search");
        for (Object movie : movies) {
            org.json.simple.JSONObject tmp = (JSONObject) movie;
            result.add(new Movie(
                    (String) tmp.get("Title"),
                    (String) tmp.get("Year"),
                    (String) tmp.get("imdbID"),
                    (String) tmp.get("Type"),
                    (String) tmp.get("Poster")
            ));
        }

        return result;
    }

}
