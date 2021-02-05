package com.example.manytickets.TheaterPackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavDeepLinkBuilder;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.example.manytickets.DBcontroller.DatabaseController;
import com.example.manytickets.MoviesPackage.SpecificMovie;
import com.example.manytickets.R;
import com.example.manytickets.Reviews.Reviews;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SpecificTheater extends AppCompatActivity {
    DatabaseController databaseController;

    Integer theaterId;
    String db_theaterName;
    String db_theaterAddress;
    String db_theaterRating;
    Integer db_countReviews;

    String[] db_moviesId;
    String[] db_moviesName;
    String[] db_moviesRating;
    String ratingOutta = "/10";
    String[] db_sessionsTime;
    String[] app_sessionsTime;
    String[] db_sessionsCost;

    private int margin_dp;

    private LinearLayout headerTheaterLayout;
    private TextView theaterNameView;
    private TextView theaterAddressView;
    private TextView theaterRatingView;
    private TextView theaterReviewsView;


    private String toDB_date;
    private TextView testTextView;
    private TextView inputDateView;
    private Calendar selectedDate = Calendar.getInstance();

    int moviesCount = 10;
    int sessionsCount = 10;
    private LinearLayout contentLayout;
    private LinearLayout[] sessionVerticalLayout;
    private Space[] upperSpace;
    private TextView[] movieName;
    private LinearLayout[] sessionsSegment;
    private LinearLayout[] sessionsRowLayout;
    private LinearLayout[][] specificSessionLayout;
    private TextView[] sessionTime;
    private TextView[] sessionCost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_theater);

        Toolbar home_toolbar = findViewById(R.id.home_toolbar_id);      //Объявление тулбара
        setSupportActionBar(home_toolbar);
        Drawable inputPicture = getResources().getDrawable(R.drawable.ic_theaters_white);                                                 //Получить иконку для тулбара
        Bitmap bitmap = ((BitmapDrawable) inputPicture).getBitmap();                                                                      //Конвертировать иконку
        int outputSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());      //Расчитать новый размер иконки
        Drawable outputPicture = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, outputSize,outputSize,true)); //Получить новую иконку для тулбара
        home_toolbar.setNavigationIcon(outputPicture);                                                                                    //Установить новую иконку
        home_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toTheaterFragmentTransition();                                 //Переключение на домашнее активити
            }
        });

        //Шапка кинотеатра
        Intent receiveIntent = getIntent();
        Integer theaterBackgroundColor = receiveIntent.getIntExtra("theaterBackgroundColor", Color.WHITE);
        headerTheaterLayout = findViewById(R.id.headerTheaterLayout);
        headerTheaterLayout.setBackgroundColor(theaterBackgroundColor);

        theaterId = receiveIntent.getIntExtra("theaterId", 0);

        //Расписание
        testTextView = findViewById(R.id.testTextView);             //MUST BE DELETE
        inputDateView = findViewById(R.id.inputDateView);
        setInitialDateTime();                                       //Установить текущую дату в соответсвующее поле

        databaseController = new DatabaseController();
        try {
            databaseController.setConnection();

            String[] specificTheaterInfo = databaseController.getSpecificTheaterInfo(theaterId);
            db_theaterName = specificTheaterInfo[0];
            db_theaterAddress = specificTheaterInfo[1];
            db_theaterRating = specificTheaterInfo[2];
            db_countReviews = Integer.parseInt(specificTheaterInfo[3]);

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }


        int margin = 10;                                                                                                            //Желаемые отступы (для лейаута, текста и т.д.)
        margin_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, margin, getResources().getDisplayMetrics());   //Конвертация желаемых отступов в DP



        theaterNameView = findViewById(R.id.theaterNameView);
        theaterNameView.setText(db_theaterName);                    //Название кинотеатра из БД

        theaterAddressView = findViewById(R.id.theaterAddressView);
        theaterAddressView.setText(db_theaterAddress);              //Адрес кинотеатра из БД

        theaterRatingView = findViewById(R.id.theaterRatingView);
        final String theaterRatingAvg = db_theaterRating;           //Рейтинг фильма из БД
        if (theaterRatingAvg == null || theaterRatingAvg.equals("0")){          //Если кинотеатра не был оценен пользователями, то отобразить следующий текст...
            theaterRatingView.setText("Not rated");
            theaterRatingView.setTextColor(Color.GRAY);
            theaterRatingView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }
        else{                                                                   //Иначе преобразовать рейтинг в конечный вид
            final String theaterRatingOutta = "/10";                                                                                                                                          //Максимальная оценка для фильма
            final SpannableString theaterRating = new SpannableString(theaterRatingAvg + theaterRatingOutta);                                                                          //Рейтинг из БД + макс. оценка
            theaterRating.setSpan(new RelativeSizeSpan(0.85f),theaterRating.length()-theaterRatingOutta.length(), theaterRating.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //Размер макс. возможной оценки (на 15% меньше)
            theaterRating.setSpan(new ForegroundColorSpan(Color.GRAY),theaterRating.length()-theaterRatingOutta.length(), theaterRating.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);     //Цвет макс. возможной оценка (серый)
            theaterRatingView.setText(theaterRating);
        }



        String theaterReviewsText = "See all reviews";                      //Постоянный текст
        String theaterReviewsCount = "(" + db_countReviews + ")";           //Количество отзывов, полученных из БД
        String theaterReviews = theaterReviewsText + theaterReviewsCount;   //Постоянный текст + кол-во отзывов
        theaterReviewsView = findViewById(R.id.theaterReviewsView);
        theaterReviewsView.setText(theaterReviews);
        theaterReviewsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReviewsActivity(theaterId);
            }
        });


        inputDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });
        ImageView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });


        availableSessions();
    }


    public void availableSessions(){
        try {                                                                                       //Обновить все доступные фильмы
            String[][] moviesInfo = databaseController.getMoviesByTheaterByDay(theaterId, toDB_date);
            db_moviesId = moviesInfo[0];
            db_moviesName = moviesInfo[1];
            db_moviesRating = moviesInfo[2];
            moviesCount = db_moviesName.length;
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }

        //Фильм + билеты
        contentLayout = findViewById(R.id.contentLayout);

        if (moviesCount == 0){                                                                      //Если в выбранный день нет доступных сеансов, то вывести лейаут с информацией об отсутствии доступных сеансов
            LinearLayout noSessionsLayout = new LinearLayout(getApplicationContext());
            LinearLayout.LayoutParams noSessionsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            noSessionsLayoutParams.bottomMargin = margin_dp;
            noSessionsLayout.setLayoutParams(noSessionsLayoutParams);
            noSessionsLayout.setOrientation(LinearLayout.HORIZONTAL);
            noSessionsLayout.setBackgroundColor(Color.parseColor("#575757"));

            TextView noSessionsText = new TextView(getApplicationContext());
            LinearLayout.LayoutParams noSessionsTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1);
            noSessionsTextParams.setMargins(margin_dp,margin_dp,margin_dp,margin_dp);
            noSessionsText.setLayoutParams(noSessionsTextParams);
            noSessionsText.setGravity(Gravity.CENTER);
            noSessionsText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            noSessionsText.setTextColor(Color.WHITE);
            noSessionsText.setText("No sessions for the selected date");

            noSessionsLayout.addView(noSessionsText);
            contentLayout.addView(noSessionsLayout);
        }

        sessionVerticalLayout = new LinearLayout[moviesCount];
        upperSpace = new Space[moviesCount];
        movieName = new TextView[moviesCount];
        sessionsSegment = new LinearLayout[moviesCount];

        sessionTime = new TextView[sessionsCount];
        sessionCost = new TextView[sessionsCount];

        for (int i = 0; i < moviesCount; i++){      //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! цикл с curSession реализован не до конца верно, в данном случае использует лишь первые 4 элемента и работает с ними !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            //Random random = new Random();               //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! MUST BE DELETE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //sessionsCount =  1 + random.nextInt(12);

            sessionVerticalLayout[i] = new LinearLayout(getApplicationContext());   //Лейаут каждого фильма со всеми сеансами
            sessionVerticalLayout[i].setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams sessionVerticalLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            sessionVerticalLayoutParams.setMargins(0,0,0,margin_dp);
            sessionVerticalLayout[i].setLayoutParams(sessionVerticalLayoutParams);
            sessionVerticalLayout[i].setBackgroundColor(Color.WHITE);


            upperSpace[i] = new Space(getApplicationContext()); //Верхний разделитель
            upperSpace[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, margin_dp));




            //String db_movieName = "Back To The Future 2";
            movieName[i] = new TextView(getApplicationContext());   //Название фильма
            LinearLayout.LayoutParams movieNameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            movieNameParams.setMargins(margin_dp,margin_dp,margin_dp,margin_dp);
            movieName[i].setLayoutParams(movieNameParams);
            movieName[i].setGravity(Gravity.CENTER);
            movieName[i].setTextColor(Color.BLACK);
            movieName[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            movieName[i].setText(db_moviesName[i]);
            movieName[i].setId(Integer.parseInt(db_moviesId[i]));
            int finalI = i;
            movieName[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSpecificMovieActivity(movieName[finalI].getId(),db_moviesRating[finalI],ratingOutta);
                }
            });


            try {                                                                                       //Обновить все доступные сеансы
                String[][] sessionsInfo = databaseController.getSessionsByTheaterByDayByMovie(theaterId, toDB_date, movieName[i].getId());
                db_sessionsTime = sessionsInfo[0];
                app_sessionsTime = sessionsDuration_dbToApp(db_sessionsTime);
                db_sessionsCost = sessionsInfo[1];
                sessionsCount = db_sessionsTime.length;

            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }

            sessionsSegment[i] = new LinearLayout(getApplicationContext()); //Лейаут со всеми сеансами
            sessionsSegment[i].setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams sessionsSegmentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            sessionsSegmentParams.setMargins(margin_dp,margin_dp,margin_dp,margin_dp);
            sessionsSegment[i].setLayoutParams(sessionsSegmentParams);

            int rowsCount;                      //Количество строк для сеансов
            if (sessionsCount % 4 == 0){
                rowsCount = sessionsCount/4;
            }
            else {
                rowsCount = sessionsCount/4 + 1;
            }

            specificSessionLayout = new LinearLayout[rowsCount][sessionsCount]; //Лейаут каждого отдельного сеанса

            int correctSessionsCount = 0;    //Переменная, с помощью которой делается проверка на остановку динамического добавления сеансов
            for (int row = 0; row < rowsCount; row++){
                sessionsRowLayout = new LinearLayout[rowsCount];    //Строка с сеансами
                sessionsRowLayout[row] = new LinearLayout(getApplicationContext());
                sessionsRowLayout[row].setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams sessionsRowLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                sessionsRowLayoutParams.setMargins(0,0,0,margin_dp);
                sessionsRowLayout[row].setLayoutParams(sessionsRowLayoutParams);


                for (int curSession = 0; curSession < 4; curSession++){
                    if (correctSessionsCount >= sessionsCount){
                        break;
                    }


                    specificSessionLayout[row][curSession] = new LinearLayout(getApplicationContext());  //Лейаут каждого отдельного сеанса
                    specificSessionLayout[row][curSession].setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams specificSessionLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
                    specificSessionLayoutParams.setMargins(margin_dp,0,margin_dp,margin_dp/2);
                    specificSessionLayout[row][curSession].setLayoutParams(specificSessionLayoutParams);
                    specificSessionLayout[row][curSession].setBackgroundColor(Color.parseColor("#765783"));
                    specificSessionLayout[row][curSession].setPadding(margin_dp,0,margin_dp,0);


                    //String db_sessionTime = "15:00";
                    sessionTime[curSession] = new TextView(getApplicationContext());    //Время сеанса
                    sessionTime[curSession].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    sessionTime[curSession].setGravity(Gravity.CENTER);
                    sessionTime[curSession].setTextColor(Color.WHITE);
                    sessionTime[curSession].setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    sessionTime[curSession].setText(app_sessionsTime[correctSessionsCount]);


                    //String db_sessionCost = "500 р.";
                    sessionCost[curSession] = new TextView(getApplicationContext());    //Цена сеанса
                    sessionCost[curSession].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    sessionCost[curSession].setGravity(Gravity.CENTER);
                    sessionCost[curSession].setTextColor(Color.WHITE);
                    sessionCost[curSession].setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    sessionCost[curSession].setText(db_sessionsCost[correctSessionsCount]);


                    specificSessionLayout[row][curSession].addView(sessionTime[curSession]); //Добавить время и цену в предназначенный для это лейаут
                    specificSessionLayout[row][curSession].addView(sessionCost[curSession]);
                    specificSessionLayout[row][curSession].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });


                    sessionsRowLayout[row].addView(specificSessionLayout[row][curSession]);  //Добавить каждый сеанс в строку

                    correctSessionsCount++;
                }

                sessionsSegment[i].addView(sessionsRowLayout[row]); //Добавить строку в лейаут с сеансами
            }

            sessionVerticalLayout[i].addView(upperSpace[i]);    //Добавить в лейаут каждого фильма отступ, название фильма и лейаут со всеми сеансами для каждого фильма
            sessionVerticalLayout[i].addView(movieName[i]);
            sessionVerticalLayout[i].addView(sessionsSegment[i]);

            contentLayout.addView(sessionVerticalLayout[i]);    //Добавить в лейаут со ВСЕМИ сеансами на выбранный день сеансы каждого фильма
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String[] sessionsDuration_dbToApp(String[] getTime){
        List<String> list_setTime = new ArrayList<>();
        String[] array_setTime = null;
        try{
            SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
            SimpleDateFormat sdfOut = new SimpleDateFormat("HH:mm");
            for (int i = 0; i < getTime.length; i++){
                Date sessionsTime = sdfIn.parse(getTime[i]);
                list_setTime.add(sdfOut.format(sessionsTime));
            }
            array_setTime = list_setTime.toArray(new String[getTime.length]);
        }
        catch (Exception e){e.getMessage();}

        return array_setTime;
    }


    public void toTheaterFragmentTransition(){
        PendingIntent pendingIntent = new NavDeepLinkBuilder(this.getApplicationContext())
                .setGraph(R.navigation.mobile_navigation)
                .setDestination(R.id.navigation_theater)
                .createPendingIntent(); //Установка интента для перехода из стороннего активити к домашнему (в данном случае к фрагменту navigation_theater)

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
        reviewIntent.putExtra("movieOrTheater", "theater");
        startActivity(reviewIntent);
    }


    public void openSpecificMovieActivity(Integer movieId, String ratingAvg, String ratingOutta){
        Intent intent = new Intent(this, SpecificMovie.class);
        intent.putExtra("movieId", movieId);
        intent.putExtra("ratingAvg", ratingAvg);
        intent.putExtra("ratingOutta", ratingOutta);
        startActivity(intent);
    }


    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(SpecificTheater.this, R.style.DatePickerDialogTheme, d,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, monthOfYear);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();


            contentLayout.removeAllViewsInLayout();                                                     //Удалить все предыдущие доступные фильмы
            availableSessions();                                                                        //Отобразить доступные фильмы
        }
    };

    // установка начальных даты и времени
    @SuppressLint("SimpleDateFormat")
    private void setInitialDateTime() {
        inputDateView.setText(DateUtils.formatDateTime(this,
                selectedDate.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE));                                          //Дата в формате MONTH day

        //Получение даты для БД
        String getDate = DateUtils.formatDateTime(this,
                selectedDate.getTimeInMillis(),
                DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);    //Дата в формате mm/dd/yyyy

        try{
            SimpleDateFormat sdfIn = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd");

            Date sessionsTime = sdfIn.parse(getDate);
            toDB_date = sdfOut.format(sessionsTime);                             //Дата для запроса в БД
        }
        catch (Exception e){e.getMessage();}
        testTextView.setText(toDB_date);
    }
}