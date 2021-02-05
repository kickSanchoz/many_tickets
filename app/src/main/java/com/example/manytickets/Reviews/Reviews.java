package com.example.manytickets.Reviews;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavDeepLinkBuilder;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.manytickets.DBcontroller.DatabaseController;
import com.example.manytickets.R;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Reviews extends AppCompatActivity {
    DatabaseController databaseController;

    Integer titleId;
    String title;

    String[] db_username;
    String[] db_rating;
    String[] db_textReview;
    String[] db_dateReview;
    String[] appConverted_dateReview;

    private int margin_dp;

    private LinearLayout allReviewsLayout;

    private TextView destinationReviewNameView;

    private int countReviews = 10;                  //Количество отзывов на данный фильм или кинотеатр
    private LinearLayout[] specificReviewLayout;
    private LinearLayout[] headerReviewLayout;
    private LinearLayout[] userLayout;
    private ImageView[] avatarView;
    private TextView[] usernameView;
    private LinearLayout[] ratingLayout;
    private ImageView[] starImage;
    private TextView[] ratingView;
    private LinearLayout[] separatorLayout;
    private TextView[] reviewSegmentView;
    private TextView[] reviewDate;

    private String date_fromAPP;                                 //Дата для добавления в бд
    private Calendar currentDate = Calendar.getInstance();  //Сегодняшняя дата
    String app_userRating;                              //Рейтинг на добавление в БД
    String app_userReview;                              //Отзыв на добавление в БД

    String reviewDestination;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);


        final Intent receiveIntent = getIntent();
        reviewDestination = receiveIntent.getStringExtra("movieOrTheater");    //Узнать на что оставлен отзыв
        titleId = receiveIntent.getIntExtra("reviewDestinationId",0);               //Получить id фильма или кинотеатра

        final Toolbar home_toolbar = findViewById(R.id.home_toolbar_id);      //Объявление тулбара
        setSupportActionBar(home_toolbar);
        if (reviewDestination.equals("theater")){   //Если отзыв на кинотеатр, то сменить обычную иконку на иконку кинотеатра
            Drawable inputPicture = getResources().getDrawable(R.drawable.ic_theaters_white);                                                 //Получить иконку для тулбара
            Bitmap bitmap = ((BitmapDrawable) inputPicture).getBitmap();                                                                      //Конвертировать иконку
            int outputSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());      //Расчитать новый размер иконки
            Drawable outputPicture = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, outputSize,outputSize,true)); //Получить новую иконку для тулбара
            home_toolbar.setNavigationIcon(outputPicture);                                                                                    //Установить новую иконку
        }
        home_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainFragmentTransition(reviewDestination);                                 //Переключение на домашнее активити
            }
        });


        databaseController = new DatabaseController();
        try {
            databaseController.setConnection();

            if (titleId != 0){
                title = databaseController.getTitleById(reviewDestination,titleId); //Получить название фильма или кинотеатра по id
            }
            else{
                title = "unknown";
            }

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }

        int margin = 10;                                                                                                            //Желаемые отступы (для лейаута, текста и т.д.)
        margin_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, margin, getResources().getDisplayMetrics());   //Конвертация желаемых отступов в DP


        //На что оставлен отзыв
        destinationReviewNameView = findViewById(R.id.destinationReviewNameView);
        destinationReviewNameView.setText(title);   //Установить в текстовое поле на что оставлен отзыв

        //Оставить отзыв
        final TextView userReview = findViewById(R.id.userReview);
        TextView publish = findViewById(R.id.publish);

        String[] chooseRating = {"???","10","9","8","7","6","5","4","3","2","1"};                                                                        //Рейтинг, который возможно оставить
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Создаем адаптер ArrayAdapter с помощью массива чисел и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, chooseRating);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapter);
        // Устанавливаем цвет выпадающего списка
        spinner.setPopupBackgroundResource(R.color.colorDarkGray);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                app_userRating = (String) parent.getItemAtPosition(position);  //Устанавливает выбранный рейтинг
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);


        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean sendReviewError = false;
                date_appToDb();                                     //Получить и конвертировать сегодняшнюю дату в формат для БД
                if (userReview.getText().toString().equals("")){
                    //userReview.setError("Leave your review");       //Если при добавлени отзыва поле пустое, то выдать ошибку
                    sendReviewError = true;
                }
                if (app_userRating.equals("???")){
                    sendReviewError = true;
                }


                if (sendReviewError)
                    wishDialog();
                else{
                    app_userReview = userReview.getText().toString();   //Получить отзыв, оставленный пользователем, для передачи в бд
                    databaseController.addReview(reviewDestination,app_userReview,date_fromAPP,app_userRating,15,titleId);

                    userReview.setText("");
                    allReviewsLayout.removeAllViewsInLayout();
                    availableReviews();
                }
            }
        });


        availableReviews();
    }


    public void availableReviews(){
        try {
            databaseController.setConnection();

            if (titleId != 0){
                title = databaseController.getTitleById(reviewDestination,titleId); //Получить название фильма или кинотеатра по id
            }
            else{
                title = "unknown";
            }

            String[][] reviewsInfo = databaseController.getReviews(reviewDestination, titleId);
            db_username = reviewsInfo[0];                   //Список имен пользователей из БД
            db_rating = reviewsInfo[1];                     //Список оценок, оставленных пользователями из БД
            db_textReview = reviewsInfo[2];                 //Список отзывов из БД
            db_dateReview = reviewsInfo[3];                 //Список дат отзывов из БД
            appConverted_dateReview = date_dbToApp(db_dateReview);   //Конвертировать полученные даты из БД

            countReviews = db_username.length;

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }

        //Получить все оставленные отзывы
        allReviewsLayout = findViewById(R.id.allReviewsLayout);

        if (countReviews == 0){                                                                      //Если в выбранный день нет доступных сеансов, то вывести лейаут с информацией об отсутствии доступных сеансов
            LinearLayout noReviewsLayout = new LinearLayout(getApplicationContext());
            LinearLayout.LayoutParams noReviewsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            noReviewsLayoutParams.bottomMargin = margin_dp;
            noReviewsLayout.setLayoutParams(noReviewsLayoutParams);
            noReviewsLayout.setOrientation(LinearLayout.HORIZONTAL);
            noReviewsLayout.setBackgroundColor(Color.parseColor("#575757"));

            TextView noReviewsText = new TextView(getApplicationContext());
            LinearLayout.LayoutParams noReviewsTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1);
            noReviewsTextParams.setMargins(margin_dp,margin_dp,margin_dp,margin_dp);
            noReviewsText.setLayoutParams(noReviewsTextParams);
            noReviewsText.setGravity(Gravity.CENTER);
            noReviewsText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            noReviewsText.setTextColor(Color.WHITE);
            noReviewsText.setText("Be the first to leave a review");

            noReviewsLayout.addView(noReviewsText);
            allReviewsLayout.addView(noReviewsLayout);
        }

        specificReviewLayout = new LinearLayout[countReviews];
        headerReviewLayout = new LinearLayout[countReviews];
        userLayout = new LinearLayout[countReviews];
        avatarView = new ImageView[countReviews];
        usernameView = new TextView[countReviews];
        ratingLayout = new LinearLayout[countReviews];
        starImage = new ImageView[countReviews];
        ratingView = new TextView[countReviews];
        separatorLayout = new LinearLayout[countReviews];
        reviewSegmentView = new TextView[countReviews];
        reviewDate = new TextView[countReviews];

        for (int i = 0; i < countReviews; i++){
            specificReviewLayout[i] = new LinearLayout(getApplicationContext());    //Лейаут всего отзыва пользователя
            specificReviewLayout[i].setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams specificReviewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            specificReviewLayoutParams.setMargins(0,0,0,margin_dp);
            specificReviewLayout[i].setLayoutParams(specificReviewLayoutParams);
            specificReviewLayout[i].setPadding(margin_dp,margin_dp,margin_dp,margin_dp);
            specificReviewLayout[i].setBackgroundColor(Color.WHITE);


            headerReviewLayout[i] = new LinearLayout(getApplicationContext());                                                                                          //Лейаут информации отзыва (фото, имя, рейтинг)
            headerReviewLayout[i].setOrientation(LinearLayout.HORIZONTAL);
            headerReviewLayout[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


            userLayout[i] = new LinearLayout(getApplicationContext());                                                                                                  //Лейаут фото + имя
            userLayout[i].setOrientation(LinearLayout.HORIZONTAL);
            userLayout[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            avatarView[i] = new ImageView(getApplicationContext());                                                                                                     //Фото пользователя
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams avatarViewParams = new LinearLayout.LayoutParams(size,size,0);
            avatarViewParams.setMargins(0,0,margin_dp,0);
            avatarView[i].setLayoutParams(avatarViewParams);
            avatarView[i].setImageDrawable(getDrawable(R.drawable.user));

            usernameView[i] = new TextView(getApplicationContext());                                                                                                    //Имя пользователя
            usernameView[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0));
            usernameView[i].setGravity(Gravity.CENTER_VERTICAL);
            usernameView[i].setTextColor(Color.BLACK);
            usernameView[i].setText(db_username[i]);

            userLayout[i].addView(avatarView[i]);   //Добавить фото в лейаут фото + имя
            userLayout[i].addView(usernameView[i]); //Добавить имя пользователя в лейаут фото + имя


            ratingLayout[i] = new LinearLayout(getApplicationContext());                                                                                                    //Лейаут иконки зведы + рейтинг
            ratingLayout[i].setOrientation(LinearLayout.HORIZONTAL);
            ratingLayout[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0));

            starImage[i] = new ImageView(getApplicationContext());                                                                                                          //Иконка звезды
            starImage[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0));
            starImage[i].setImageDrawable(getDrawable(android.R.drawable.btn_star_big_on));

            ratingView[i] = new TextView(getApplicationContext());                                                                                                          //Рейтинг, оставленный пользователем
            ratingView[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
            ratingView[i].setGravity(Gravity.CENTER_VERTICAL);
            ratingView[i].setTextColor(Color.BLACK);
            ratingView[i].setText(db_rating[i]);

            ratingLayout[i].addView(starImage[i]);  //Добавить иконку звезды в лейаут иконки + рейтинг
            ratingLayout[i].addView(ratingView[i]); //Добавить рейтинг в лейаут иконки + рейтинг


            headerReviewLayout[i].addView(userLayout[i]);   //Добавить лейаут фото + имя в лейаут информации отзыва
            headerReviewLayout[i].addView(ratingLayout[i]); //Добавить лейаут иконки зведы + рейтинг в лейаут информации отзыва


            separatorLayout[i] = new LinearLayout(getApplicationContext());                                                                             //Лейаут разделителя между информацией отзыва и самим отзывом
            separatorLayout[i].setOrientation(LinearLayout.HORIZONTAL);
            int separatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams separatorLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, separatorHeight);
            separatorLayoutParams.setMargins(margin_dp/2,margin_dp/2,margin_dp/2,margin_dp/2);
            separatorLayout[i].setLayoutParams(separatorLayoutParams);
            separatorLayout[i].setBackgroundColor(Color.parseColor("#765783"));


            reviewSegmentView[i] = new TextView(getApplicationContext());                                                                                           //Текстовое поле отзыва, полученного из бд
            reviewSegmentView[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            reviewSegmentView[i].setTextColor(Color.BLACK);
            reviewSegmentView[i].setText(db_textReview[i]);


            reviewDate[i] = new TextView(getApplicationContext());                                                                                                  //Поле с датой отзыва
            LinearLayout.LayoutParams reviewDateParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            reviewDateParams.gravity = Gravity.RIGHT;
            reviewDate[i].setLayoutParams(reviewDateParams);
            reviewDate[i].setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
            reviewDate[i].setText(appConverted_dateReview[i]);


            specificReviewLayout[i].addView(headerReviewLayout[i]); //Добавить в лейаут всего отзыва лейаут информации отзыва
            specificReviewLayout[i].addView(separatorLayout[i]);    //Добавить в лейаут всего отзыва лейаут разделителя
            specificReviewLayout[i].addView(reviewSegmentView[i]);  //Добавить в лейаут всего отзыва текстовое поле отзыва
            specificReviewLayout[i].addView(reviewDate[i]);         //Добавить в лейаут всего отзыва текстовое поле с датой отзыва


            allReviewsLayout.addView(specificReviewLayout[i]);       //Добавить в общий скролл лейаут, лейаут всего отзыва
        }
    }


    public void toMainFragmentTransition(String reviewDestination){
        if (reviewDestination.equals("theater")){
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
        else {
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
    }


    public void wishDialog(){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Please, leave your review and rating")
                .setPositiveButton("OK",null)
                .show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#765783"));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#765783"));
    }


    // Получение текущей даты и времени
    @SuppressLint("SimpleDateFormat")
    private void date_appToDb() {
        //Получение текущей даты для БД
        String getDate = DateUtils.formatDateTime(this,
                currentDate.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME);    //Дата в формате Month d, yyyy, HH:mm  (Дата в формате mm/dd/yyyy)

        try{
            SimpleDateFormat sdfIn = new SimpleDateFormat("MMMM d, yyyy, HH:mm");
            SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date sessionsTime = sdfIn.parse(getDate);
            date_fromAPP = sdfOut.format(sessionsTime);                             //Дата для запроса в БД
        }
        catch (Exception e){e.getMessage();}
        //destinationReviewNameView.setText(date_fromAPP); //ТЕСТ даты
    }

    @SuppressLint("SimpleDateFormat")
    private String[] date_dbToApp(String[] getDate){
        List<String> list_setDate = new ArrayList<>();
        String[] array_setDate = null;
        try{
            SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
            SimpleDateFormat sdfOut = new SimpleDateFormat("dd.MM.yyyy");
            for (int i = 0; i < getDate.length; i++){
                Date sessionsTime = sdfIn.parse(getDate[i]);
                list_setDate.add(sdfOut.format(sessionsTime));
            }
            array_setDate = list_setDate.toArray(new String[getDate.length]);
        }
        catch (Exception e){e.getMessage();}

        return array_setDate;
    }
}