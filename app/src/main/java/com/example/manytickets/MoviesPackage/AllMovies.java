package com.example.manytickets.MoviesPackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavDeepLinkBuilder;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.manytickets.DBcontroller.DatabaseController;
import com.example.manytickets.R;

import java.sql.SQLException;

public class AllMovies extends AppCompatActivity {
    DatabaseController databaseController;

    LinearLayout allmoviesTextLayout;                   //Верхний текст

    private int countMovie = 0;                        //Количество всех фильмов
    private LinearLayout mainVerticalLayout;            //ВЕРТИКАЛЬНЫЙ лейаут, состоящий из множества горизонтальных (статический)
    private LinearLayout[] movieHorizontalLayout;       //ГОРИЗОНТАЛЬНЫЙ лейаут для добавления всех частей фильма (динамический)
    private ImageView[] moviePoster;                    //Постер фильма
    private TextView[] movieName;                       //Название фильма
    private TextView[] movieRating;                     //Рейтинг фильма

    String[] db_movieId;
    String[] db_movieName;
    String[] db_movieRating;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_movies);

        Toolbar home_toolbar = findViewById(R.id.home_toolbar_id);      //Объявление тулбара
        setSupportActionBar(home_toolbar);
        home_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toHomeFragmentTransition();                                 //Переключение на домашнее активити
            }
        });

        databaseController = new DatabaseController();
        try {
            databaseController.setConnection();            
            countMovie = databaseController.getMoviesCount();    //Количество фильмов
            
            String[][] movieInfo = databaseController.getMovies();
            db_movieId = movieInfo[0];      //Список id фильмов
            db_movieName = movieInfo[1];    //Список названий фильмов
            db_movieRating = movieInfo[2];  //Список рейтингов для фильмов
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }


        int margin = 10;                                                                                                            //Желаемые отступы (для лейаута, текста и т.д.)
        int margin_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, margin, getResources().getDisplayMetrics());   //Конвертация желаемых отступов в DP


        allmoviesTextLayout = findViewById(R.id.allmoviesTextLayout);


        mainVerticalLayout = findViewById(R.id.mainVerticalLayout);
        movieHorizontalLayout = new LinearLayout[countMovie];
        moviePoster = new ImageView[countMovie];
        movieName = new TextView[countMovie];
        movieRating = new TextView[countMovie];


        if (countMovie == 0){
            TextView errorView = new TextView(getApplicationContext());
            errorView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            errorView.setText("Movies ain't found");
            errorView.setTextColor(Color.BLACK);
            errorView.setGravity(Gravity.CENTER);
            mainVerticalLayout.addView(errorView);
        }
        for (int i = 0; i < countMovie; i++)
        {
            movieHorizontalLayout[i] = new LinearLayout(getApplicationContext());
            movieHorizontalLayout[i].setOrientation(LinearLayout.HORIZONTAL);                                                                                                       //Установить лейату с фильмом ГОРИЗОНТАЛЬНУЮ ориентацию
            LinearLayout.LayoutParams movieHorizontalLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);  //Параметры ВНУТРЕННЕГО ГОРИЗОНТАЛЬНОГО лейаута: ширина(родителя) + высота(вложенного контента)
            movieHorizontalLayoutParams.setMargins(0,0,0,margin_dp/5);                                                                                      //Параметр отступов: снизу между лейаутами
            movieHorizontalLayout[i].setLayoutParams(movieHorizontalLayoutParams);                                                                                                  //Присвоить ВНУТРЕННЕМУ ГОРИЗОНТАЛЬНОМУ лейату, настроенные выше параметры
            movieHorizontalLayout[i].setPadding(margin_dp/2,margin_dp/2,margin_dp/2,margin_dp/2);
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
            movieName[i].setId(Integer.parseInt(db_movieId[i]));                                                                                                                //Присвоить полю id фильма
            movieName[i].setGravity(Gravity.CENTER_VERTICAL);                                                                                                                   //Установить текст по середине левой стороне
            movieName[i].setSingleLine(true);                                                                                                                                   //Установить текст одной линией (если текст выходит за границы, ставится троеточие)
            movieName[i].setTextColor(Color.BLACK);                                                                                                                             //Установить цвет текста
            movieName[i].setEllipsize(TextUtils.TruncateAt.END);
            /* Для проверки поиска -----------MUST BE DELETED-----------
            if (i == 8){
                movieName[i].setText("SSS text " + i);
            }
            else if (i == 7){
                movieName[i].setText("SSX text " + i);
            }
            */

            final Integer movieId = movieName[i].getId();                                                                                                                       //Записать id фильма в переменную, для дальнейшей передачи в другое активити

            boolean isRated = true;
            String ratingAvg = db_movieRating[i];                                                                                                                              //Рейтинг фильма из БД
            //if (ratingAvg.equals("10.0")) ratingAvg = "10";
            if (ratingAvg == null || ratingAvg.equals("0")) isRated = false;
            final String final_ratingAvg = ratingAvg;
            String ratingOutta = "/10";                                                                                                                                             //Максимальная оценка для фильма

            final SpannableString finalRating = new SpannableString(ratingAvg + ratingOutta);                                                                                //Рейтинг из БД + макс. оценка
            finalRating.setSpan(new RelativeSizeSpan(0.85f),finalRating.length()-ratingOutta.length(), finalRating.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);     //Размер макс. возможной оценки (на 15% меньше)
            finalRating.setSpan(new ForegroundColorSpan(Color.GRAY),finalRating.length()-ratingOutta.length(), finalRating.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);        //Цвет макс. возможной оценка (серый)

            movieRating[i] = new TextView(getApplicationContext());
            LinearLayout.LayoutParams movieRatingParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0); //Параметры поля с рейтингом фильма: ширина, высота, вес
            movieRatingParams.setMargins(margin_dp,0,margin_dp,0);                                                                                                     //Параметр отступов: слева от названия, справа от края лейаута
            movieRating[i].setLayoutParams(movieRatingParams);                                                                                                                      //Присвоить полю с рейтингом, настроенный выше параметры
            movieRating[i].setGravity(Gravity.CENTER_VERTICAL);                                                                                                                     //Установить текст по середине левой стороне
            movieRating[i].setTextColor(Color.BLACK);                                                                                                                               //Установить цвет текста
            if (!isRated){
                movieRating[i].setText("Not rated");                                                                                                                                //Установить пустой РЕЙТИНГ фильма
                movieRating[i].setTextColor(Color.GRAY);
                movieRating[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            }
            else
                movieRating[i].setText(finalRating);                                                                                                                                //Установить РЕЙТИНГ фильма




            movieHorizontalLayout[i].addView(moviePoster[i]);                       //Добавить ПОСТЕР в ГОРИЗОНТАЛЬНЫЙ лейаут
            movieHorizontalLayout[i].addView(movieName[i]);                         //Добавить НАЗВАНИЕ в ГОРИЗОНТАЛЬНЫЙ лейаут
            movieHorizontalLayout[i].addView(movieRating[i]);                       //Добавить РЕЙТИНГ в ГОРИЗОНТАЛЬНЫЙ лейаут
            movieHorizontalLayout[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSpecificMovieActivity(movieId, final_ratingAvg, ratingOutta);  //По нажатию на лейаут с фильмом перейти в новую активность
                }
            });

            mainVerticalLayout.addView(movieHorizontalLayout[i]);          //Добавить ВНЕШНИЙ ГОРИЗОНТАЛЬНЫЙ лейаут в ВЕРТИКАЛЬНЫЙ лейаут
        }
    }

    public void openSpecificMovieActivity(Integer movieId, String ratingAvg, String ratingOutta){
        Intent intent = new Intent(this, SpecificMovie.class);
        intent.putExtra("movieId", movieId);
        intent.putExtra("ratingAvg", ratingAvg);
        intent.putExtra("ratingOutta", ratingOutta);
        startActivity(intent);
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

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu_toolbar, menu);     //Установка, заранее созданного меню

        MenuItem searchItem = menu.findItem(R.id.action_search);    //Нахождение значка поиска в меню

        SearchManager searchManager = (SearchManager) AllMovies.this.getSystemService(Context.SEARCH_SERVICE);  //Объявление поиск менеджера


        final SearchView searchView = (SearchView) searchItem.getActionView();  //Объявление view, находящегося при открытии поиска
//        if (searchItem != null) {
//            searchView = (SearchView) searchItem.getActionView(); // Если значок поиска был найден, то получить view при открытии поиска
//        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(AllMovies.this.getComponentName())); //Получить информацию находящуюся во view поиска ???
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();            //Убрать фокус с view после принятия запроса
                mainVerticalLayout.clearFocus();    //Убрать фокус с ВЕРТИКАЛЬНОГО лейаута после принятия запроса
                return false;                       //false = не обрабатывать запрос слушателем
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()){
                    allmoviesTextLayout.setVisibility(View.VISIBLE);    //Если строка пустая (нет запроса на поиск фильма), то включить текст
                }
                else{
                    allmoviesTextLayout.setVisibility(View.GONE);
                }
                selectFound(newText);

                return false;   //false = view должно выполнять просто показ всех доступных вариантов, не перехватываяюсь слушателем
            }

            public void selectFound(String newText){
                for (int index = 0; index < countMovie; index++){
                    //Проверяет каждый фильм, находящийся на странице, и если находит совпадения в запросе, то включает данный лейаут и переходит на следующую итерацию...
                    if (movieName[index].getText().toString().toLowerCase().contains(newText.toLowerCase())){
                        movieHorizontalLayout[index].setVisibility(View.VISIBLE);
                        continue;
                    }
                    //иначе выключает видимость для данного лейаута с фильмом и сдвигает следующие вверх
                    movieHorizontalLayout[index].setVisibility(View.GONE);
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}