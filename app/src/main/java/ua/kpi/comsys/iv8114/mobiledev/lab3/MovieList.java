package ua.kpi.comsys.iv8114.mobiledev.lab3;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.daimajia.swipe.SwipeLayout;

import ua.kpi.comsys.iv8114.mobiledev.R;

public class MovieList {

    public Object[] moviePack;

    public MovieList(Context context, LinearLayout movieList, Movie movie){
        moviePack = newMovieList(context, movieList, movie);
    }

    private Object[] newMovieList(Context context, LinearLayout movieList, Movie movie){
        SwipeLayout swipeLayout = new SwipeLayout(context);
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.setLayoutParams(
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        movieList.addView(swipeLayout);

        ImageButton deleteButton = new ImageButton(context);
        deleteButton.setImageResource(R.drawable.ic_delete_sweep_black_24dp);
        deleteButton.setBackgroundColor(Color.RED);
        deleteButton.setPadding(50, 0, 50, 0);
        LinearLayout.LayoutParams btnBinParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        btnBinParams.gravity = Gravity.RIGHT;
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.addView(deleteButton, 0, btnBinParams);

        ConstraintLayout movieLayTmp = new ConstraintLayout(context);
        movieLayTmp.setBackgroundResource(R.drawable.movielist);
        movieLayTmp.setLayoutParams(
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        swipeLayout.addView(movieLayTmp, 1);

        deleteButton.setOnClickListener(v -> Lab3.binClicked(swipeLayout));
        movieLayTmp.setOnClickListener(v -> {
            if (movie.getImdbID().length() != 0 && !movie.getImdbID().equals("noid")) {
                MovieInfo popUpClass = new MovieInfo();
                popUpClass.showPopupWindow(v, movie);
            }
        });

        ProgressBar loadingImageBar = new ProgressBar(context);
        loadingImageBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(context, R.color.purple_500),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        loadingImageBar.setVisibility(View.GONE);
        loadingImageBar.setId(loadingImageBar.hashCode());
        movieLayTmp.addView(loadingImageBar);

        ImageView imageTmp = new ImageView(context);
        imageTmp.setId(imageTmp.hashCode());
        if (movie.getPosterPath().length() != 0){
            imageTmp.setVisibility(View.INVISIBLE);
            loadingImageBar.setVisibility(View.VISIBLE);
            new Lab3.DownloadImageTask(imageTmp, loadingImageBar, context).execute(movie.getPosterPath());
        }
        ConstraintLayout.LayoutParams imgParams =
                new ConstraintLayout.LayoutParams(300, 300);
        movieLayTmp.addView(imageTmp, imgParams);

        TextView textTitle = new TextView(context);
        textTitle.setText(movie.getTitle());
        textTitle.setEllipsize(TextUtils.TruncateAt.END);
        textTitle.setMaxLines(1);
        textTitle.setId(textTitle.hashCode());
        movieLayTmp.addView(textTitle, new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT));

        TextView textYear = new TextView(context);
        textYear.setText(movie.getYear());
        textYear.setEllipsize(TextUtils.TruncateAt.END);
        textYear.setMaxLines(4);
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

        return new Object[] {swipeLayout, movie};
    }
}
