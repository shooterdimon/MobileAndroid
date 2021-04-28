package ua.kpi.comsys.iv8114.mobiledev.lab3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import ua.kpi.comsys.iv8114.mobiledev.R;

public class MovieInfo {
    private static View popupView;
    private static ProgressBar loadingImage;
    private static ImageView movieImage;
    private static Movie movie;

    @SuppressLint("ClickableViewAccessibility")
    public void showPopupWindow(final View view, Movie movie) {
        view.getContext();
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.movie_info, null);
        MovieInfo.movie = movie;


        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        boolean focusable = true;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        loadingImage = popupView.findViewById(R.id.loadingImageInfo);
        movieImage = popupView.findViewById(R.id.movie_info_image);
        AsyncLoadMovieInfo aTask = new AsyncLoadMovieInfo();
        aTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, movie.getImdbID());

    }
    protected static void setInfoData() {
        movieImage.setVisibility(View.INVISIBLE);
        loadingImage.setVisibility(View.VISIBLE);
        new Lab3.DownloadImageTask(movieImage, loadingImage, popupView.getContext()).execute(movie.getPosterPath());
        ((TextView) popupView.findViewById(R.id.movie_info_title)).setText(movie.getTitle());
        ((TextView) popupView.findViewById(R.id.movie_info_year)).setText(movie.getYear());
        ((TextView) popupView.findViewById(R.id.movie_info_genre)).setText(movie.getGenre());
        ((TextView) popupView.findViewById(R.id.movie_info_director)).setText(movie.getDirector());
        ((TextView) popupView.findViewById(R.id.movie_info_actors)).setText(movie.getActors());
        ((TextView) popupView.findViewById(R.id.movie_info_country)).setText(movie.getCountry());
        ((TextView) popupView.findViewById(R.id.movie_info_language)).setText(movie.getLanguage());
        ((TextView) popupView.findViewById(R.id.movie_info_production)).setText(movie.getProduction());
        ((TextView) popupView.findViewById(R.id.movie_info_released)).setText(movie.getReleased());
        ((TextView) popupView.findViewById(R.id.movie_info_runtime)).setText(movie.getRuntime());
        ((TextView) popupView.findViewById(R.id.movie_info_awards)).setText(movie.getAwards());
        ((TextView) popupView.findViewById(R.id.movie_info_rating)).setText(movie.getImdbRating());
        ((TextView) popupView.findViewById(R.id.movie_info_plot)).setText(movie.getPlot());
    }
    private static class AsyncLoadMovieInfo extends AsyncTask<String, Void, Void> {
        private String getRequest(String url){
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

        private void parseMovieInfo(String jsonText) throws ParseException {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonText);

            movie.setGenre((String) jsonObject.get("Genre"));
            movie.setDirector((String) jsonObject.get("Director"));
            movie.setActors((String) jsonObject.get("Actors"));
            movie.setCountry((String) jsonObject.get("Country"));
            movie.setLanguage((String) jsonObject.get("Language"));
            movie.setProduction((String) jsonObject.get("Production"));
            movie.setReleased((String) jsonObject.get("Released"));
            movie.setRuntime((String) jsonObject.get("Runtime"));
            movie.setAwards((String) jsonObject.get("Awards"));
            movie.setImdbRating(jsonObject.get("imdbRating") + "/10");
            movie.setPlot((String) jsonObject.get("Plot"));

        }
        private void search(String imdbID) {
            String API_KEY="493d9265";
            String jsonResponse = String.format("http://www.omdbapi.com/?apikey=%s&i=%s", API_KEY, imdbID);
            try {
                parseMovieInfo(getRequest(jsonResponse));
            } catch (ParseException e) {
                System.err.println("Incorrect content of JSON file!");
                e.printStackTrace();
            }
        }
        @Override
        protected Void doInBackground(String... strings) {
            search(strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MovieInfo.setInfoData();
        }
    }
}
