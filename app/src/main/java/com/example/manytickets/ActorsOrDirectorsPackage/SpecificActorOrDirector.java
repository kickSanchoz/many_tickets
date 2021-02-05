package com.example.manytickets.ActorsOrDirectorsPackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavDeepLinkBuilder;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.manytickets.DBcontroller.DatabaseController;
import com.example.manytickets.MoviesPackage.SpecificMovie;
import com.example.manytickets.R;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SpecificActorOrDirector extends AppCompatActivity {
    DatabaseController databaseController;

    String db_personName = null;
    String db_personBorn = null;
    String app_personBorn = null;
    String[] db_movieId = null;
    String[] db_movieName = null;
    String[] db_movieRating = null;

    int countMovie = 10;
    private LinearLayout[] movieHorizontalLayout;       //ГОРИЗОНТАЛЬНЫЙ лейаут для добавления всех частей фильма (динамический)
    private ImageView[] moviePoster;                    //Постер фильма
    private TextView[] movieName;                       //Название фильма
    private TextView[] movieRating;                     //Рейтинг фильма

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_actor_or_director);

        Toolbar home_toolbar = findViewById(R.id.home_toolbar_id);      //Объявление тулбара
        setSupportActionBar(home_toolbar);
        home_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toHomeFragmentTransition();                                 //Переключение на домашнее активити
            }
        });


        int margin = 10;                                                                                                            //Желаемые отступы (для лейаута, текста и т.д.)
        int margin_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, margin, getResources().getDisplayMetrics());   //Конвертация желаемых отступов в DP


        Intent receiveIntent = getIntent();
        Integer personId = receiveIntent.getIntExtra("personId",0);     //Получить имя человека из предыдущего активити
        String profession = receiveIntent.getStringExtra("profession");             //Получить профессию человка из предыдущего активити



        databaseController = new DatabaseController();
        try{
            databaseController.setConnection();

            String[] personInfo = databaseController.getPersonInfo(personId, profession);
            db_personName = personInfo[0];
            db_personBorn = personInfo[1];
            app_personBorn = born_dbToApp(db_personBorn);

            String[][] movieInfo = databaseController.getMoviesForPerson(personId, profession);
            db_movieId = movieInfo[0];          //Id фильмов, получаемых из БД
            db_movieName = movieInfo[1];        //Названия фильмов, получаемых из БД
            db_movieRating = movieInfo[2];      //Рейтинги фильмов, получаемых из БД

            countMovie = db_movieId.length;


        } catch (SQLException | ClassNotFoundException | ParseException throwables) {
            throwables.printStackTrace();
        }




        TextView fullnameView = findViewById(R.id.fullnameView);
        fullnameView.setText(db_personName);

        TextView professionView = findViewById(R.id.professionView);
        professionView.setText(profession);

        //String born = "01 January 2000";
        TextView bornView = findViewById(R.id.bornView);
        bornView.setText(app_personBorn);



        LinearLayout filmographyVerticalLayout = findViewById(R.id.filmographyVerticalLayout);

        movieHorizontalLayout = new LinearLayout[countMovie];
        moviePoster = new ImageView[countMovie];
        movieName = new TextView[countMovie];
        movieRating = new TextView[countMovie];
        if (countMovie == 0){                                                                                                                           //Если ни один фильм не найден, то вывести текст ошибки
            TextView errorView = new TextView(getApplicationContext());
            errorView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            errorView.setText("Movies ain't found");
            errorView.setTextColor(Color.BLACK);
            errorView.setGravity(Gravity.CENTER);
            filmographyVerticalLayout.addView(errorView);
        }

        for(int i = 0; i<countMovie; i++){
            movieHorizontalLayout[i] = new LinearLayout(getApplicationContext());
            movieHorizontalLayout[i].setOrientation(LinearLayout.HORIZONTAL);                                                                                                           //Установить лейату с фильмом ГОРИЗОНТАЛЬНУЮ ориентацию
            LinearLayout.LayoutParams movieHorizontalLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);      //Параметры ГОРИЗОНТАЛЬНОГО лейаута
            movieHorizontalLayoutParams.setMargins(0,0,0,margin_dp/5);                                                                                               //Параметр отступов: слева, справа от ВЕРТИКАЛЬНОГО лейаута, снизу между ГОРИЗОНТАЛЬНЫМИ лейаутами
            movieHorizontalLayout[i].setLayoutParams(movieHorizontalLayoutParams);                                                                                                      //Присвоить ГОРИЗОНТАЛЬНОМУ лейауту, настроенные выше параметры
            movieHorizontalLayout[i].setPadding(margin_dp/2, margin_dp/2,margin_dp/2,margin_dp/2);
            movieHorizontalLayout[i].setBackgroundColor(Color.WHITE);


            int posterWidthSize = 60;                                                                                                                       //Желаемая ширина постера
            int posterHeightSize = 90;                                                                                                                      //Желаемая высота постера
            int posterWidthSize_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, posterWidthSize, getResources().getDisplayMetrics());     //Ширина постера в DP
            int posterHeightSize_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, posterHeightSize, getResources().getDisplayMetrics());   //Высота постера в DP
            moviePoster[i] = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams moviePosterParams = new LinearLayout.LayoutParams(posterWidthSize_dp,posterHeightSize_dp);    //Параметры постера: ширина, высота
            moviePosterParams.setMargins(0,0,margin_dp,0);                                                        //Параметр отступа между постером и текстом
            moviePoster[i].setLayoutParams(moviePosterParams);                                                                      //Присвоить постеру, настроенные выше параметры
            moviePoster[i].setImageDrawable(getDrawable(R.drawable.movieposter));                                                   //Присвоить ПОСТЕРУ картинку


            movieName[i] = new TextView(getApplicationContext());
            LinearLayout.LayoutParams movieNameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);//Параметры поля с названием фильма: ширина, высота, вес
            movieName[i].setLayoutParams(movieNameParams);                                                                                                                      //Присвоить полю с названием, настроенные выше параметры
            movieName[i].setText(db_movieName[i]);                                                                                                                              //Установить НАЗВАНИЕ фильма
            movieName[i].setTextColor(Color.BLACK);                                                                                                                             //Установить цвет текста
            movieName[i].setGravity(Gravity.CENTER_VERTICAL);                                                                                                                   //Установить гравити текста
            movieName[i].setId(Integer.parseInt(db_movieId[i]));                                                                                                                //Установить полю id фильма
            /* Для проверки поиска -----------MUST BE DELETED-----------
            if (i == 8){
                movieName[i].setText("SSS text " + i);
            }
            else if (i == 7){
                movieName[i].setText("SSX text " + i);
            }
             */

            final Integer titleId = movieName[i].getId();                                                                                                                       //Записать название фильма в переменную, для дальнейшей передачи в другое активити


            boolean isRated = true;
            String ratingAvg = db_movieRating[i];                                                                                                                             //Рейтинг фильма из БД
            if (ratingAvg == null || ratingAvg.equals("0")) isRated = false;
            final String final_ratingAvg = ratingAvg;
            final String ratingOutta = "/10";

            //Максимальная оценка для фильма
            final SpannableString finalRating = new SpannableString(ratingAvg + ratingOutta);                                                                               //Рейтинг из БД + макс. оценка
            finalRating.setSpan(new RelativeSizeSpan(0.85f),finalRating.length()-ratingOutta.length(), finalRating.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    //Размер макс. возможной оценки (на 15% меньше)
            finalRating.setSpan(new ForegroundColorSpan(Color.GRAY),finalRating.length()-ratingOutta.length(), finalRating.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);       //Цвет макс. возможной оценка (серый)

            movieRating[i] = new TextView(getApplicationContext());
            LinearLayout.LayoutParams movieRatingParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0); //Параметры поля с рейтингом фильма: ширина, высота, вес
            movieRatingParams.setMargins(10,0,10,0);                                                                                                        //Параметр отступов: слева от названия, справа от края лейаута
            movieRating[i].setLayoutParams(movieRatingParams);                                                                                                                      //Присвоить полю с рейтингом, настроенный выше параметры
            movieRating[i].setTextColor(Color.BLACK);                                                                                                                               //Установить цвет текста
            movieRating[i].setGravity(Gravity.CENTER);                                                                                                                              //Установить гравити текста
            if (!isRated){
                movieRating[i].setText("Not rated");                                                                                                                                //Установить пустой РЕЙТИНГ фильма
                movieRating[i].setTextColor(Color.GRAY);
                movieRating[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            }
            else
                movieRating[i].setText(finalRating);                                                                                                                                //Установить РЕЙТИНГ фильма


            movieHorizontalLayout[i].addView(moviePoster[i]);
            movieHorizontalLayout[i].addView(movieName[i]);
            movieHorizontalLayout[i].addView(movieRating[i]);
            movieHorizontalLayout[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSpecificMovieActivity(titleId, final_ratingAvg, ratingOutta);  //По нажатию на лейаут с фильмом перейти в новую активность
                }
            });


            filmographyVerticalLayout.addView(movieHorizontalLayout[i]);
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


    public void openSpecificMovieActivity(Integer titleId, String ratingAvg, String ratingOutta){
        Intent intent = new Intent(this, SpecificMovie.class);
        intent.putExtra("movieId", titleId);
        intent.putExtra("ratingAvg", ratingAvg);
        intent.putExtra("ratingOutta", ratingOutta);
        startActivity(intent);
    }


    @SuppressLint("SimpleDateFormat")
    private String born_dbToApp(String getDate) throws ParseException {
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
}