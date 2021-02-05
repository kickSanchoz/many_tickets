package com.example.manytickets.MoviesPackage;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavDeepLinkBuilder;

import com.example.manytickets.ActorsOrDirectorsPackage.SpecificActorOrDirector;
import com.example.manytickets.DBcontroller.DatabaseController;
import com.example.manytickets.R;
import com.example.manytickets.Reviews.Reviews;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpecificMovie extends AppCompatActivity {
    DatabaseController databaseController;

    String db_movieName = null;
    String db_movieDirectorId = null;
    String db_movieDuration = null;
    String app_movieDuration = null;
    String db_movieDaterelease = null;
    String app_movieDaterelease = null;
    String db_movieDirectorName = null;
    Integer db_movieCountReviews = 0;
    String db_movieDescription = null;

    String public_IMDb = null;
    String public_Kinopoisk = null;
    String public_RottenTomatoes = null;

    private int countTrailers = 10;
    String[] db_trailersDuration;
    String[] app_trailersDuration;
    String[] db_trailersName;
    String[] db_trailersUrl;
    String[] app_trailersText;

    String[] db_castId;
    String[] db_castName;

    private Boolean expanded = false;
    private LinearLayout wrappedTextLayout;
    private TextView wrappedTextViewCollapsed, wrappedTextViewExpanded;
    private TextView reviewsTextView;

    private LinearLayout[] trailerLayout;
    private ImageView[] trailerPreview;
    private TextView[] trailerTitleDuration;

    private int countActorsInMovie = 10;
    private LinearLayout[] castActorLayout;
    private ImageView[] castActorPhoto;
    private TextView[] castActorName;


//    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_movie);

        Toolbar home_toolbar = findViewById(R.id.home_toolbar_id);      //Объявление тулбара
        setSupportActionBar(home_toolbar);
        home_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toHomeFragmentTransition();                                 //Переключение на домашнее активити
            }
        });


        Intent receiveIntent = getIntent();
        //final String movieTitle = receiveIntent.getStringExtra("title");
        final Integer movieId = receiveIntent.getIntExtra("movieId", 0);
        Log.d("movie ID", String.valueOf(movieId));


        databaseController = new DatabaseController();
        try {
            databaseController.setConnection();

            //Получить данные о фильме в шапку страницы
            String[] specificMovieInfo = databaseController.getSpecificMovieInfo(movieId);
            db_movieName = specificMovieInfo[0];                        //Название фильма из БД
            db_movieDuration = specificMovieInfo[1];                    //Полученная длительность фильма из БД
            app_movieDuration = movieDuration_dbToApp(db_movieDuration);
            Log.d("Duration", app_movieDuration);

            db_movieDaterelease = specificMovieInfo[2];                 //Полученная дата релиза из БД
            app_movieDaterelease = dateRelease_dbToApp(db_movieDaterelease);
            Log.d("Release date", app_movieDaterelease);

            db_movieDirectorId = specificMovieInfo[3];                      //Полученный id режиссера из БД
            db_movieDirectorName = specificMovieInfo[4];                        //Полученное имя режиссера из БД
            db_movieCountReviews = Integer.parseInt(specificMovieInfo[5]);  //Количество отзывов на фильм
            db_movieDescription = specificMovieInfo[6];                     //Описание, получаемое из БД


            String[] movieRatingPerPublic = databaseController.getRatingPerPublic(movieId);
            public_IMDb = movieRatingPerPublic[0];               //Рейтинг IMDb, полученный из БД для заданного фильма
            public_Kinopoisk = movieRatingPerPublic[1];          //Рейтинг Kinopoisk, полученный из БД для заданного фильма
            public_RottenTomatoes = movieRatingPerPublic[2];     //Рейтинг RottenTomatoes, полученный из БД для заданного фильма


            countTrailers = databaseController.getTrailersCount(movieId);            //Количество трейлеров
            String[][] trailersInfo = databaseController.getTrailersInfo(movieId);
            db_trailersDuration = trailersInfo[0];                                      //Список с длительностью каждого трейлера
            app_trailersDuration = trailersDuration_dbToApp(db_trailersDuration);
            db_trailersName = trailersInfo[1];                                          //Список с названием каждого трейлера
            app_trailersText = trailersText(app_trailersDuration, db_trailersName);         //Преобразовать полученные длительность и название к конечному виду
            db_trailersUrl = trailersInfo[2];                                           //Список с ссылкой на каждый трейлер

            String[][] castInfo = databaseController.getCast(movieId);
            db_castId = castInfo[0];                    //Список id актеров, полученный из БД
            db_castName = castInfo[1];                  //Список актеров, полученный из БД
            countActorsInMovie = db_castId.length;                //Количество актеров

        } catch (SQLException | ClassNotFoundException | ParseException throwables) {
            throwables.printStackTrace();
        }


        int margin = 10;                                                                                                            //Желаемые отступы (для лейаута, текста и т.д.)
        int margin_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, margin, getResources().getDisplayMetrics());   //Конвертация желаемых отступов в DP



        //Имя фильма
        TextView movieNameView = findViewById(R.id.movieNameView);
        movieNameView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));   //Параметры view с названием фильма
        movieNameView.setPadding(margin_dp/2,0,margin_dp/2,0);
        movieNameView.setGravity(Gravity.CENTER);                                                                                                       //Установить название по середине view
        movieNameView.setText(db_movieName);                                                                                                              //Установить во view, полученное название фильма из прошлого активити
        movieNameView.setTypeface(movieNameView.getTypeface(), Typeface.BOLD);                                                                          //Установить жирность для текста
        movieNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);                                                                                 //Установить размер текста
        movieNameView.setTextColor(Color.WHITE);                                                                                                        //Установить цвет текста


        //Длительность фильма
        TextView movieDurationView = findViewById(R.id.movieDurationView);
        movieDurationView.setText(app_movieDuration);


        //Дата релиза фильма
        TextView moveReleaseView = findViewById(R.id.moveReleaseView);
        moveReleaseView.setText(app_movieDaterelease);


        //Режиссер фильма
        TextView directorView = findViewById(R.id.directorView);
        directorView.setId(Integer.parseInt(db_movieDirectorId));                                                                                               //Установить полю id режиссера
        directorView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1)); //Параметры view рейтинга
        if (db_movieDirectorName == null){  //Если не удалось получить имя из БД, то установить текст и запретить нажатие на этот текст
            db_movieDirectorName = "unknown";
            directorView.setTextColor(Color.GRAY);                                                                                                              //Установить текст текста
        }
        else{
            directorView.setTextColor(Color.parseColor("#ABE7FF"));                                                                                  //Установить текст текста
            directorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSpecificActorOrDirectorActivity(directorView.getId(), "Director");    //Переход к активити с актером или директором
                }
            });
        }
        directorView.setText(db_movieDirectorName);                                                                                                             //Установить во view, полученное имя



        //Рейтинг фильма
        String ratingAvg = receiveIntent.getStringExtra("ratingAvg");                   //Получить из предыдушего активити средней рейтинг фильма
        String ratingOutta = receiveIntent.getStringExtra("ratingOutta");               //Получить из предыдушего активити максимально возможную оценку фильма
        SpannableString rating = new SpannableString(ratingAvg + ratingOutta);         //Средняя оценка + максимально возможная оценка
        assert ratingOutta != null;
        rating.setSpan(new ForegroundColorSpan(Color.WHITE), 0, rating.length()-ratingOutta.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);            //Цвет средней оценки фильма (белый)
        rating.setSpan(new ForegroundColorSpan(Color.GRAY), rating.length()-ratingOutta.length(), rating.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //Цвет максимально возможной оценки (серый)
        rating.setSpan(new RelativeSizeSpan(0.85f), rating.length()-ratingOutta.length(), rating.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //Размер максимально возможной оценки (на 15% меньше)

        TextView movieRatingView = findViewById(R.id.movieRatingView);  //Объявление рейтингового поля
        if (ratingAvg == null || ratingAvg.equals("0")){
            movieRatingView.setText("Not rated");                       //Если рейтинг не был получен, то установить указанный текст
            movieRatingView.setTextColor(Color.GRAY);
            movieRatingView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }
        else
            movieRatingView.setText(rating);                            //Если рейтинг был получен, то установить его в ретийнговое поле

        LinearLayout.LayoutParams publicRatingParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1); //Параметр поля рейтинга паблика (общий)
        int ratingColor = Color.WHITE;                                                                                                                                          //Цвет рейтинга паблика (общий)
        int ratingColor_null = Color.GRAY;                                                                                                                                      //Если в БД нет рейтинга для данного фильма
        int ratingGravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;                                                                                                         //Гравити рейтинга паблика (общий)

        TextView IMDb_rating = findViewById(R.id.IMDb_rating);                          //Рейтинг для IMDb
        IMDb_rating.setLayoutParams(publicRatingParams);
        IMDb_rating.setText(public_IMDb);
        if (public_IMDb.matches("Not rated")){
            IMDb_rating.setTextColor(ratingColor_null);
            IMDb_rating.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }
        else IMDb_rating.setTextColor(ratingColor);
        IMDb_rating.setGravity(ratingGravity);

        TextView Kinopoisk_rating = findViewById(R.id.Kinopoisk_rating);                //Рейтинг для Kinopoisk
        Kinopoisk_rating.setLayoutParams(publicRatingParams);
        Kinopoisk_rating.setText(public_Kinopoisk);
        if (public_Kinopoisk.matches("Not rated")){
            Kinopoisk_rating.setTextColor(ratingColor_null);
            Kinopoisk_rating.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }
        else Kinopoisk_rating.setTextColor(ratingColor);
        Kinopoisk_rating.setGravity(ratingGravity);

        TextView RottenTomatoes_rating = findViewById(R.id.RottenTomatoes_rating);      //Рейтинг для Rotten Tomatoes
        RottenTomatoes_rating.setLayoutParams(publicRatingParams);
        RottenTomatoes_rating.setText(public_RottenTomatoes);
        if (public_RottenTomatoes.matches("Not rated")){
            RottenTomatoes_rating.setTextColor(ratingColor_null);
            RottenTomatoes_rating.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }
        else RottenTomatoes_rating.setTextColor(ratingColor);
        RottenTomatoes_rating.setGravity(ratingGravity);


        //Описание фильма
        wrappedTextLayout = findViewById(R.id.wrappedTextLayout);
        LinearLayout.LayoutParams wrappedTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);  //Параметры ВЕРТИКАЛЬНОГО лейаута для Collapsed и Expanded текста
        wrappedTextLayoutParams.setMargins(0,0,0,margin_dp);                                                                                                //Параметр отступов: внизу для следующего лейаута
        wrappedTextLayout.setLayoutParams(wrappedTextLayoutParams);                                                                                                         //Присвоить лейауту для Collapsed и Expanded текста, настроенные выше параметры
        wrappedTextLayout.setBackgroundColor(Color.WHITE);                                                                                                                  //Установить цвет фона лейаута

        int textSize_sp = 14;                                                                                                                                               //Желаемый размер текста в SP (конвертация в SP происходит непосредственно в параметрах TextView)
        LinearLayout.LayoutParams wrappedTextViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);    //Параметры текстового поля (%ViewCollapsed and %ViewExpanded)
        wrappedTextViewParams.setMargins(margin_dp,margin_dp,margin_dp,margin_dp);                                                                                          //Параметр отступов для текстового поля

        wrappedTextViewCollapsed = new TextView(getApplicationContext());
        wrappedTextViewCollapsed.setText(db_movieDescription);                          //Присвоить текстовому полю заданное описание
        wrappedTextViewCollapsed.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize_sp);  //Присвоить текстовому полю размер текста
        wrappedTextViewCollapsed.setTextColor(Color.BLACK);                             //Присвоить текстовому полю цвет текста
        wrappedTextViewCollapsed.setMaxLines(3);                                        //Присвоить текстовому полю максимальное количество строк
        wrappedTextViewCollapsed.setEllipsize(TextUtils.TruncateAt.END);                //Добавить последней возможной строке троеточие
        wrappedTextViewCollapsed.setLayoutParams(wrappedTextViewParams);                //Присвоить текстовому полю, настроенные выше параметры

        wrappedTextViewExpanded = new TextView(getApplicationContext());
        wrappedTextViewExpanded.setText(db_movieDescription);                           //Присвоить текстовому полю заданное описание
        wrappedTextViewExpanded.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize_sp);   //Присвоить текстовому полю размер текста
        wrappedTextViewExpanded.setTextColor(Color.BLACK);                              //Присвоить текстовому полю цвет текста
        wrappedTextViewExpanded.setLayoutParams(wrappedTextViewParams);                 //Присвоить текстовому полю, настроенные выше параметры


        wrappedTextLayout.addView(wrappedTextViewCollapsed);    //Добавить в ВЕРТИКАЛЬНЫЙ текстовый лейаут Collapsed текст
        wrappedTextLayout.addView(wrappedTextViewExpanded);     //Добавить в ВЕРТИКАЛЬНЫЙ текстовый лейаут Expanded текст
        if (db_movieDescription != null) wrappedTextViewCollapsed.setVisibility(View.VISIBLE);      //Установить Collapsed текст видимым по умолчанию (Если описание было получено)
        else wrappedTextViewCollapsed.setVisibility(View.GONE);                                         //Установить Collapsed текст скрытым (Если отсутствует описание к фильму
        wrappedTextViewExpanded.setVisibility(View.GONE);                                           //Установить Expanded текст скрытым по умолчанию

        wrappedTextLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!expanded){
                    wrappedTextViewCollapsed.setVisibility(View.GONE);      //Скрыть Collapsed текст
                    wrappedTextViewExpanded.setVisibility(View.VISIBLE);    //Отобразить Expanded текст
                }
                else{
                    wrappedTextViewCollapsed.setVisibility(View.VISIBLE);   //Отобразить Collapsed текст
                    wrappedTextViewExpanded.setVisibility(View.GONE);       //Скрыть Expanded текст
                }
                expanded = !expanded;                                       //Флаг переключения состояния
            }
        });



        //Комментарии
        LinearLayout reviewLayout = findViewById(R.id.reviewLayout);
        String reviewText = "See all reviews";                                     //Постоянный текст
        String reviewCount = "(" + db_movieCountReviews + ")";     //Количество отзывов, полученных из БД
        String review = reviewText + reviewCount;                                  //Постоянный текст + кол-во отзывов
        reviewsTextView = findViewById(R.id.reviewsTextView);                                                                                                                      //Объявление поля с отзывыми
        reviewsTextView.setText(review);                                                                                                                                           //Установить текст отзыва
        LinearLayout.LayoutParams reviewTextViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1); //Параметры view отзыва
        reviewsTextView.setLayoutParams(reviewTextViewParams);                                                                                                                     //Присвоить view, настроенные выше параметры
        reviewsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);                                                                                                          //Установить размер текста
        reviewsTextView.setTextColor(Color.BLACK);                                                                                                                                 //Установить цвет текста
        reviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReviewsActivity(movieId);      //Переход к активити с отзывами
            }
        });



        //Трейлеры
        LinearLayout trailersScrollLayout = findViewById(R.id.trailersScrollLayout);
        trailerLayout = new LinearLayout[countTrailers];
        trailerPreview = new ImageView[countTrailers];
        trailerTitleDuration = new TextView[countTrailers];
//        String trailer_duration = "(1:55) ";                                                                        //Полученное время трейлера из БД
//        String trailer_title = "Interstellar Official Teaser Trailer #1 (2014) Christopher Nolan Sci-Fi Movie HD";  //Полученное название трейлера из БД
//        String trailer_titleDuration = trailer_duration + trailer_title;                                            //Время + название трейлера

        if (countTrailers == 0){                                                                                                                        //Если ни один трейлер не найден, то вывести текст ошибки
            TextView errorView = new TextView(getApplicationContext());
            errorView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            errorView.setText("No trailers for this movie");
            errorView.setTextColor(Color.GRAY);
            errorView.setGravity(Gravity.CENTER);
            trailersScrollLayout.addView(errorView);
        }

        for (int i = 0; i < countTrailers; i++){
            trailerLayout[i] = new LinearLayout(getApplicationContext());
            trailerLayout[i].setOrientation(LinearLayout.VERTICAL);                                                                                                                     //Установить общему лейауту с трейлерами ВЕРТИКАЛЬНУЮ ориаентацию
            LinearLayout.LayoutParams trailerLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);   //Параметры общего ВЕРТИКАЛЬНОГО лейаута
            trailerLayoutParams.setMargins(margin_dp,margin_dp,margin_dp,margin_dp);                                                                                                    //Параметр отступов между соседними ВЕРТИКАЛЬНЫМИ лейаутами
            trailerLayout[i].setLayoutParams(trailerLayoutParams);                                                                                                                      //Присвоить общему ВЕРТИКАЛЬНОМУ лейауту, настроенные выше параметры


            trailerPreview[i] = new ImageView(getApplicationContext());
            int previewWidthSize = 120;                                                                                                                     //Желаемая ширина превью трейлера
            int previewHeightSize = 80;                                                                                                                     //Желаемая высота превью трейлера
            int previewWidthSize_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, previewWidthSize, getResources().getDisplayMetrics());   //Ширина превью трейлера в DP
            int previewHeightSize_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, previewHeightSize, getResources().getDisplayMetrics()); //Высота превью трейлера в DP
            trailerPreview[i].setLayoutParams(new LinearLayout.LayoutParams(previewWidthSize_dp, previewHeightSize_dp));                                    //Установить превью размеры, установленные выше
            trailerPreview[i].setImageDrawable(getDrawable(R.drawable.trailerhorizontal));                                                                  //Установить превью выбранную картинку


            trailerTitleDuration[i] = new TextView(getApplicationContext());
            trailerTitleDuration[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)); //Установить полю с названием и длительностью Параметры ширины и высоты
            trailerTitleDuration[i].setText(app_trailersText[i]);                                                                                                 //Установить полю с названием и длительностью указанный текст
            trailerTitleDuration[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize_sp);                                                                           //Установить размер текста
            trailerTitleDuration[i].setTextColor(Color.BLACK);                                                                                                      //Установить цвет текста
            trailerTitleDuration[i].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);                                                                                   //Установить выравнивание текста
            trailerTitleDuration[i].setMaxLines(3);                                                                                                                 //Установить максимально возжможное количество строк
            trailerTitleDuration[i].setEllipsize(TextUtils.TruncateAt.END);                                                                                         //Добавить последней возможной строке троеточие

            trailerLayout[i].addView(trailerPreview[i]);        //Добавить в общий лейаут (превью + название трейлера + длительнось) превью
            trailerLayout[i].addView(trailerTitleDuration[i]);  //Добавить в общей лейаут название трейлера + длительность

            int finalI = i;
            trailerLayout[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openTrailerURL(db_trailersUrl[finalI]);   //Открыть ссылку из трейлера
                }
            });

            trailersScrollLayout.addView(trailerLayout[i]); //Добавить в ГОРИЗОНТАЛЬНЫЙ скролл лейаут Общий лейаут с трейлером
        }



        //Актерский состав
        LinearLayout castVerticalLayout = findViewById(R.id.castVerticalLayout);
        castActorLayout = new LinearLayout[countActorsInMovie];
        castActorPhoto = new ImageView[countActorsInMovie];
        castActorName = new TextView[countActorsInMovie];
        //String[] actor_name = {"Christopher Lloyd", "Michael J. Fox", "Morgan Freeman"};                                //ТЕСТОВОЕ имя актера, полученное из БД

        if (countActorsInMovie == 0){                                                                                                                   //Если ни один человек не найден, то вывести текст ошибки
            TextView errorView = new TextView(getApplicationContext());
            errorView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            errorView.setText("Actors ain't found");
            errorView.setTextColor(Color.BLACK);
            errorView.setGravity(Gravity.CENTER);
            castVerticalLayout.addView(errorView);
        }

        for (int i = 0; i < countActorsInMovie; i++){
            castActorLayout[i] = new LinearLayout(getApplicationContext());
            castActorLayout[i].setOrientation(LinearLayout.HORIZONTAL);                                                                                                         //Установить ГОРИЗОНТАЛЬНУЮ ориентацию лейауту с информацией об актере
            LinearLayout.LayoutParams castActorLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);    //Параметры ГОРИЗОНТАЛЬНОГО лейаута
            castActorLayoutParams.setMargins(0,0,0,margin_dp/5);                                                                                        //Параметр отступов: слева, справа от ВЕРТИКАЛЬНОГО лейаута, снизу между ГОРИЗОНТАЛЬНЫМИ лейаутами
            castActorLayout[i].setLayoutParams(castActorLayoutParams);                                                                                                          //Присвоить ГОРИЗОНТАЛЬНОМУ лейауту, настроенные выше параметры
            castActorLayout[i].setPadding(margin_dp/2,margin_dp/2,margin_dp/2,margin_dp/2);
            castActorLayout[i].setBackgroundColor(Color.WHITE);

            castActorPhoto[i] = new ImageView(getApplicationContext());
            int photoWidthSize = 45;                                                                                                                    //Желаемая ширина фотографии актера
            int photoHeightSize = 60;                                                                                                                   //Желаемая высота фотографии актера
            int photoWidthSize_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, photoWidthSize, getResources().getDisplayMetrics());   //Ширина фотографии актера в DP
            int photoHeightSize_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, photoHeightSize, getResources().getDisplayMetrics()); //Высота фотографии актера в DP
            LinearLayout.LayoutParams castActorPhotoParams = new LinearLayout.LayoutParams(photoWidthSize_dp, photoHeightSize_dp, 0);           //Параметры фотографии актера
            castActorPhotoParams.setMargins(0,0,margin_dp,0);                                                                         //Параметр отступов: справа от фотографии
            castActorPhoto[i].setLayoutParams(castActorPhotoParams);                                                                                    //Установить фотографии, установленные выше параметры
            castActorPhoto[i].setImageDrawable(getDrawable(R.drawable.person));                                                                         //Установить фотографии, выбранную картинку


            castActorName[i] = new TextView(getApplicationContext());
            castActorName[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1)); //Параметры имени актера
            castActorName[i].setText(db_castName[i]);                                                                                                                   //Присвоить полю имя актера
            castActorName[i].setId(Integer.parseInt(db_castId[i]));                                                                                                     //Присвоить подю id актера
            /* Для проверки имени -----------MUST BE DELETED-----------
            if (i == 7 ){
                castActorName[i].setText(actor_name[1]);
            }
            else if (i == 8){
                castActorName[i].setText(actor_name[2]);
            }
             */

            castActorName[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);                                                                                          //Установить размер текста
            castActorName[i].setTextColor(Color.BLACK);                                                                                                                 //Установить цвет текста
            castActorName[i].setGravity(Gravity.CENTER_VERTICAL);                                                                                                       //Установить текст по середние поля


            castActorLayout[i].addView(castActorPhoto[i]);  //Добавить в ГОРИЗОНТАЛЬНЫЙ лейаут фотографию актера
            castActorLayout[i].addView(castActorName[i]);   //Добавить в ГОРИЗОНТАЛЬНЫЙ лейаут имя актера

            final Integer actorId = castActorName[i].getId();
            castActorLayout[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSpecificActorOrDirectorActivity(actorId, "Actor");  //Переход к активити с актером или директором
                }
            });

            castVerticalLayout.addView(castActorLayout[i]); //Добавить в ВЕРТИКАЛЬНЫЙ лейаут ГОРИЗОНТАЛЬНЫЙ лейаут
        }
    }


    public void toHomeFragmentTransition(){
        PendingIntent pendingIntent = new NavDeepLinkBuilder(this.getApplicationContext())
                .setGraph(R.navigation.mobile_navigation)
                .setDestination(R.id.navigation_home)
                .createPendingIntent(); //Установка интента для перехода из стороннего активити к домашнему (в данном случае к фрагменту navigation_home)

        try {
            pendingIntent.send();   //Переход к домашнему активити
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public void openReviewsActivity(Integer reviewDestinationId){
        Intent reviewIntent = new Intent(this, Reviews.class);
        reviewIntent.putExtra("reviewDestinationId", reviewDestinationId);
        reviewIntent.putExtra("movieOrTheater", "movie");
        startActivity(reviewIntent);
    }


    public void openTrailerURL(String trailerUrl){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
        startActivity(browserIntent);
    }


    public void openSpecificActorOrDirectorActivity(Integer personId, String profession){
        Intent intent = new Intent(this, SpecificActorOrDirector.class);
        intent.putExtra("personId",personId);
        intent.putExtra("profession",profession);
        startActivity(intent);
    }

    @SuppressLint("SimpleDateFormat")
    private String dateRelease_dbToApp(String getDate) throws ParseException {
        if (getDate == null){
            return "unknown";
        }

        String setDate = "";
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(getDate);
            sdf = new SimpleDateFormat("d MMMM yyyy");
            setDate = sdf.format(date);
        }
        catch (Exception e){e.getMessage();}

        return setDate;
    }

    private String movieDuration_dbToApp(String getDuration) throws ParseException {
        if (getDuration == null){
            return "unknown";
        }

        String[] time_fromDB = getDuration.split(":");
        String setDuration = "";

        if (time_fromDB[0].startsWith("0")) setDuration += time_fromDB[0].substring(1) + "h ";
        else setDuration += time_fromDB[0] + "h ";

        if (!time_fromDB[1].matches("00")){
            if (time_fromDB[1].startsWith("0")) setDuration += time_fromDB[1].substring(1) + "min";
            else setDuration += time_fromDB[1] + "min";
        }

        return setDuration;
    }

    @SuppressLint("SimpleDateFormat")
    private String[] trailersDuration_dbToApp(String[] getDuration){
        List<String> list_setDuration = new ArrayList<>();
        String[] array_setDuration = null;
        try{
            SimpleDateFormat sdfIn = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat sdfOut = new SimpleDateFormat("m:ss");
            String[] tmp = new String[getDuration.length];
            for (int i = 0; i < getDuration.length; i++){
                Date durationTime = sdfIn.parse(getDuration[i]);
                tmp[i] = "(" + sdfOut.format(durationTime) + ") ";
                list_setDuration.add(tmp[i]);
            }
            array_setDuration = list_setDuration.toArray(new String[getDuration.length]);
        }
        catch (Exception e){e.getMessage();}

        return array_setDuration;
    }

    private String[] trailersText(String[] getDuration, String[] getName){
        String[] finalText = new String[countTrailers];
        for (int i = 0; i < countTrailers; i++){
            finalText[i] = getDuration[i] + getName[i];
        }

        return finalText;
    }
}