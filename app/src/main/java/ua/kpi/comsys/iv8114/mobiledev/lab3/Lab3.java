package ua.kpi.comsys.iv8114.mobiledev.lab3;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.daimajia.swipe.SwipeLayout;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import ua.kpi.comsys.iv8114.mobiledev.R;

public class Lab3 extends Fragment {
    private View root;
    private static LinearLayout moviesLayout;
    private static HashMap<SwipeLayout, Movie> moviesHash;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.lab3, container, false);
        moviesLayout = root.findViewById(R.id.scroll_lay);
        moviesHash = new HashMap<>();

        try {
            ArrayList<Movie> movies = parseMovies(readTextFile(root.getContext(), R.raw.movies));
            for (Movie movie : movies) {
                Object[] tmp = new MovieList(root.getContext(), moviesLayout, movie).moviePack;
                moviesHash.put((SwipeLayout) tmp[0], (Movie) tmp[1]);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SearchView searchView = root.findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                int countResults = 0;
                for (SwipeLayout movie : moviesHash.keySet()) {
                    if (query == null){
                        movie.setVisibility(View.VISIBLE);
                        countResults++;
                    }
                    else {
                        if (moviesHash.get(movie).getTitle().toLowerCase()
                                .contains(query.toLowerCase()) || query.length() == 0){
                            movie.setVisibility(View.VISIBLE);
                            countResults++;
                        }
                        else
                           movie.setVisibility(View.GONE);
                    }
                }

                if (countResults == 0){
                    root.findViewById(R.id.no_movies_view).setVisibility(View.VISIBLE);
                }
                else {
                    root.findViewById(R.id.no_movies_view).setVisibility(View.GONE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                int countResults = 0;
                for (SwipeLayout movie :
                        moviesHash.keySet()) {
                    if (query == null){
                        movie.setVisibility(View.VISIBLE);
                        countResults++;
                    }
                    else {
                        if (moviesHash.get(movie).getTitle().toLowerCase()
                                .contains(query.toLowerCase()) || query.length() == 0){
                            movie.setVisibility(View.VISIBLE);
                            countResults++;
                        }
                        else
                            movie.setVisibility(View.GONE);
                    }
                }

                if (countResults == 0){
                    root.findViewById(R.id.no_movies_view).setVisibility(View.VISIBLE);
                }
                else {
                    root.findViewById(R.id.no_movies_view).setVisibility(View.GONE);
                }
                return false;
            }
        });

        Button btnAddBook = root.findViewById(R.id.button_add_movie);
        btnAddBook.setOnClickListener(v -> {
            MovieAdd popUpClass = new MovieAdd();
            Object[] popups = popUpClass.showPopupWindow(v);

            View popupView = (View) popups[0];
            PopupWindow popupWindow = (PopupWindow) popups[1];

            EditText inputTitle = popupView.findViewById(R.id.input_title);
            EditText inputYear = popupView.findViewById(R.id.input_year);
            EditText inputType = popupView.findViewById(R.id.input_type);

            Button buttonAdd = popupView.findViewById(R.id.button_add_add);
            buttonAdd.setOnClickListener(v1 -> {
                if (inputTitle.getText().toString().length() != 0 &&
                        inputYear.getText().toString().length() != 0 &&
                        inputType.getText().toString().length() != 0) {
                    Object[] tmp = new MovieList(root.getContext(), moviesLayout,
                            new Movie(inputTitle.getText().toString(),
                                    inputYear.getText().toString(),
                                    inputType.getText().toString())).moviePack;

                    moviesHash.put((SwipeLayout) tmp[0], (Movie)tmp[1]);
                    changeLaySizes();

                    popupWindow.dismiss();
                }
                else{
                    Toast.makeText(getActivity(), "Incorrect data!",
                            Toast.LENGTH_LONG).show();
                }
            });
        });


        changeLaySizes();

        return root;
    }

    public static void binClicked(SwipeLayout swipeLayout){
        moviesHash.remove(swipeLayout);
        moviesLayout.removeView(swipeLayout);
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        changeLaySizes();
    }

    private void changeLaySizes(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) root.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        for (SwipeLayout moviesList : moviesHash.keySet()) {
            ((ConstraintLayout)moviesList.getChildAt(1)).getChildAt(0).setLayoutParams(
                    new ConstraintLayout.LayoutParams(300, 300));
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
