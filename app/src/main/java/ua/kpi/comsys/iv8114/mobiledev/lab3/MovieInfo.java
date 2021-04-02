package ua.kpi.comsys.iv8114.mobiledev.lab3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RawRes;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import ua.kpi.comsys.iv8114.mobiledev.R;

public class MovieInfo {
    @SuppressLint("ClickableViewAccessibility")
    public void showPopupWindow(final View view, Movie movie) {
        view.getContext();
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View popupView = inflater.inflate(R.layout.movie_info, null);

        try {
            parseMovieInfo(readTextFile(popupView.getContext(),
                    getResId(movie.getImdbID(), R.raw.class)), movie);
        } catch (ParseException e) {
            System.err.println("Incorrect content of JSON file!");
            e.printStackTrace();
        }

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        boolean focusable = true;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        if (movie.getPosterPath().length() != 0){
            ((ImageView) popupView.findViewById(R.id.movie_info_image)).setImageResource(
                    getResId(movie.getPosterPath().toLowerCase()
                            .split("\\.")[0], R.drawable.class));
        }
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

    private void parseMovieInfo(String jsonText, Movie movie) throws ParseException {
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

    private static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static String readTextFile(Context context, @RawRes int id){
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
            System.err.println("FIle cannot be reading!");
            e.printStackTrace();
        }
        return outputStream.toString();
    }
}
