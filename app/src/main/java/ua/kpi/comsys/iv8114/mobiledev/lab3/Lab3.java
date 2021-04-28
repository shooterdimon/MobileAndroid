package ua.kpi.comsys.iv8114.mobiledev.lab3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.daimajia.swipe.SwipeLayout;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import ua.kpi.comsys.iv8114.mobiledev.R;

public class Lab3 extends Fragment {
    private static View root;
    private static LinearLayout moviesLayout;
    private static HashMap<SwipeLayout, Movie> moviesHash;
    private static TextView noItems;
    private static ProgressBar loadingBar;
    private static Set<SwipeLayout> removeSet;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.lab3, container, false);
        moviesLayout = root.findViewById(R.id.scroll_lay);
        moviesHash = new HashMap<>();

        noItems = root.findViewById(R.id.no_movies_view);
        loadingBar = root.findViewById(R.id.no_items_progressbar);

        removeSet = new HashSet<>();

        SearchView searchView = root.findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                removeSet.addAll(moviesHash.keySet());
                if (query.length() >= 3) {
                    AsyncLoadMovies aTask = new AsyncLoadMovies();
                    loadingBar.setVisibility(View.VISIBLE);
                    noItems.setVisibility(View.GONE);
                    aTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);
                }
                else {
                    for (SwipeLayout swipeLayout : removeSet) {
                        binClicked(swipeLayout);
                    }
                    removeSet.clear();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                removeSet.addAll(moviesHash.keySet());
                if (query.length() >= 3) {
                    AsyncLoadMovies aTask = new AsyncLoadMovies();
                    loadingBar.setVisibility(View.VISIBLE);
                    noItems.setVisibility(View.GONE);
                    aTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);
                }
                else {
                    for (SwipeLayout swipeLayout : removeSet) {
                        binClicked(swipeLayout);
                    }
                    removeSet.clear();
                }
                return false;
            }
        });

        Button btnAddMovie = root.findViewById(R.id.button_add_movie);
        btnAddMovie.setOnClickListener(v -> {
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


    protected static void loadMovies(ArrayList<Movie> movies){
        if (movies != null) {
            for (SwipeLayout swipeLayout : removeSet) {
                binClicked(swipeLayout);
            }
            removeSet.clear();
            if (movies.size() > 0) {
                noItems.setVisibility(View.GONE);
                for (Movie movie :
                        movies) {
                    Object[] tmp = new MovieList(root.getContext(), moviesLayout, movie).moviePack;

                    moviesHash.put((SwipeLayout) tmp[0], (Movie)tmp[1]);
                }
            } else {
                noItems.setVisibility(View.VISIBLE);
            }
        }
        else {
            noItems.setVisibility(View.VISIBLE);
            Toast.makeText(root.getContext(), "Cannot load data!", Toast.LENGTH_LONG).show();
        }
        loadingBar.setVisibility(View.GONE);
    }

    public static void binClicked(SwipeLayout swipeLayout){
        moviesHash.remove(swipeLayout);
        moviesLayout.removeView(swipeLayout);
        if (moviesHash.keySet().isEmpty()){
            noItems.setVisibility(View.VISIBLE);
        }
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

    private static class AsyncLoadMovies extends AsyncTask<String, Void, ArrayList<Movie>> {
        private String getRequest(String url) {
            StringBuilder result = new StringBuilder();
            try {
                URL getReq = new URL(url);
                URLConnection movieConnection = getReq.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(movieConnection.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null)
                    result.append(inputLine).append("\n");

                in.close();

            } catch (MalformedURLException e) {
                System.err.println(String.format("Incorrect URL <%s>!", url));
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result.toString();
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
        private ArrayList<Movie> search(String newText){
            String API_KEY = "493d9265";
            String jsonResponse = String.format("http://www.omdbapi.com/?apikey=%s&s=\"%s\"&page=1", API_KEY, newText);
            try {
                ArrayList<Movie> movies = parseMovies(getRequest(jsonResponse));
                return movies;
            } catch (ParseException e) {
                System.err.println("Incorrect content of JSON file!");
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {
            return search(strings[0]);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            Lab3.loadMovies(movies);
        }
    }
    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @SuppressLint("StaticFieldLeak")
        ImageView bmImage;
        @SuppressLint("StaticFieldLeak")
        ProgressBar loadingBar;
        @SuppressLint("StaticFieldLeak")
        Context context;

        public DownloadImageTask(ImageView bmImage, ProgressBar loadingBar, Context context) {
            this.bmImage = bmImage;
            this.loadingBar = loadingBar;
            this.context = context;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null)
                bmImage.setImageBitmap(result);
            else {
                bmImage.setBackgroundResource(R.drawable.not_found);
                Toast.makeText(context, "Cannot load data!", Toast.LENGTH_LONG).show();
            }
            loadingBar.setVisibility(View.GONE);
            bmImage.setVisibility(View.VISIBLE);
        }
    }

}
