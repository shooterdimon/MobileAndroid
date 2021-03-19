package ua.kpi.comsys.iv8114.mobiledev.lab3;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.lang.reflect.Field;

import ua.kpi.comsys.iv8114.mobiledev.R;

public class MovieList {

    public ConstraintLayout moviePack;

    public MovieList(Context context, LinearLayout movieList, Movie movie){
        moviePack = newMovieList(context, movieList, movie);
    }

    private ConstraintLayout newMovieList(Context context, LinearLayout movieList, Movie movie){
        ConstraintLayout movieLayTmp = new ConstraintLayout(context);
        movieLayTmp.setBackgroundResource(R.drawable.movielist);
        movieLayTmp.setLayoutParams(
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        movieList.addView(movieLayTmp);

        ImageView imageTmp = new ImageView(context);
        imageTmp.setId(imageTmp.hashCode());
        if (movie.getPosterPath().length() != 0)
            imageTmp.setImageResource(
                    getResId(movie.getPosterPath().toLowerCase().split("\\.")[0], R.drawable.class));
        ConstraintLayout.LayoutParams imgParams =
                new ConstraintLayout.LayoutParams(300, 300);
        movieLayTmp.addView(imageTmp, imgParams);

        TextView textTitle = new TextView(context);
        textTitle.setText(movie.getTitle());
        textTitle.setEllipsize(TextUtils.TruncateAt.END);
        textTitle.setMaxLines(4);
        textTitle.setId(textTitle.hashCode());
        movieLayTmp.addView(textTitle, new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT));

        TextView textYear = new TextView(context);
        textYear.setText(movie.getYear());
        textYear.setEllipsize(TextUtils.TruncateAt.END);
        textYear.setMaxLines(1);
        textYear.setId(textYear.hashCode());
        movieLayTmp.addView(textYear, new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT));

        TextView textType = new TextView(context);
        textType.setText(movie.getType());
        textType.setId(textType.hashCode());
        movieLayTmp.addView(textType, new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT));

        ConstraintSet textConstraintSet = new ConstraintSet();
        textConstraintSet.clone(movieLayTmp);

        textConstraintSet.connect(imageTmp.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        textConstraintSet.connect(imageTmp.getId(), ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        textConstraintSet.connect(imageTmp.getId(), ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

        textConstraintSet.connect(textTitle.getId(), ConstraintSet.START,
                imageTmp.getId(), ConstraintSet.END);
        textConstraintSet.connect(textTitle.getId(), ConstraintSet.TOP,
                imageTmp.getId(), ConstraintSet.TOP);
        textConstraintSet.connect(textTitle.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);
        textConstraintSet.connect(textTitle.getId(), ConstraintSet.BOTTOM,
                imageTmp.getId(), ConstraintSet.BOTTOM);

        textConstraintSet.connect(textYear.getId(), ConstraintSet.START,
                textTitle.getId(), ConstraintSet.START);
        textConstraintSet.connect(textYear.getId(), ConstraintSet.TOP,
                textTitle.getId(), ConstraintSet.BOTTOM);
        textConstraintSet.connect(textYear.getId(), ConstraintSet.END,
                textTitle.getId(), ConstraintSet.END);

        textConstraintSet.connect(textType.getId(), ConstraintSet.START,
                textYear.getId(), ConstraintSet.START);
        textConstraintSet.connect(textType.getId(), ConstraintSet.TOP,
                textYear.getId(), ConstraintSet.BOTTOM);
        textConstraintSet.connect(textType.getId(), ConstraintSet.END,
                textYear.getId(), ConstraintSet.END);

        textConstraintSet.setMargin(textTitle.getId(), ConstraintSet.START, 8);
        textConstraintSet.setMargin(textTitle.getId(), ConstraintSet.END, 8);
        textConstraintSet.setVerticalBias(textTitle.getId(), 0.15f);

        textConstraintSet.setMargin(textYear.getId(), ConstraintSet.TOP, 8);

        textConstraintSet.setMargin(textType.getId(), ConstraintSet.TOP, 24);

        textConstraintSet.applyTo(movieLayTmp);

        return movieLayTmp;
    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
