package com.example.manytickets.ui.profile;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.manytickets.DBcontroller.DatabaseController;
import com.example.manytickets.R;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileFragment extends Fragment {
    DatabaseController databaseController;
    String[] db_movieName = null;
    String[] db_sessionDate = null;
    String[] app_sessionDate = null;

    private ProfileViewModel profileViewModel;

    private Integer userId;
    private String userNickname;
    private TextView usernameView;

    int countTickets = 1;
    private LinearLayout profileScrollLayout;
    private LinearLayout[] rowTicketsLayout;
    private LinearLayout[][] specificTicketLayout;
    private ImageView[] moviePoster;
    private TextView[] movieName;
    private TextView[] sessionDate;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
//        final TextView textView = root.findViewById(R.id.text_notifications);
//        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        int margin = 10;                                                                                                            //Желаемые отступы (для лейаута, текста и т.д.)
        int margin_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, margin, getResources().getDisplayMetrics());   //Конвертация желаемых отступов в DP

        userId = 15;
        userNickname = "testUser";
        usernameView = root.findViewById(R.id.usernameView);
        usernameView.setText(userNickname);

        databaseController = new DatabaseController();
        try {
            databaseController.setConnection();
            String[][] sessionInfo = databaseController.getTicketsInfo(userId);
            db_movieName = sessionInfo[0];                                              //Назавания фильмов, получаемые из БД
            db_sessionDate = sessionInfo[1];                                            //Даты сеансов, получаемые из БД
            app_sessionDate = dateSession_dbToApp(db_sessionDate);                          //Преобразовать дату к конечному виду (2000-12-31 --> 31 December 2000)

            countTickets = db_movieName.length;

        } catch (SQLException | ClassNotFoundException | ParseException throwables) {
            throwables.printStackTrace();
        }


        profileScrollLayout = root.findViewById(R.id.profileScrollLayout);
        //specificTicketLayout = new LinearLayout[countTickets];
        moviePoster = new ImageView[countTickets];
        //String db_movieName = "Once Upon a Time... in Hollywood";   //Назавания фильмов, получаемые из БД
        movieName = new TextView[countTickets];
        //String db_sessionDate = "8 August 2019";                    //Даты сеансов, получаемые из БД
        sessionDate = new TextView[countTickets];


        int rowsCount;                      //Количество строк для билетов
        if (countTickets % 2 == 0){
            rowsCount = countTickets/2;
        }
        else {
            rowsCount = countTickets/2 + 1;
        }
        rowTicketsLayout = new LinearLayout[rowsCount];
        specificTicketLayout = new LinearLayout[rowsCount][countTickets];

        int correctCountTickets = 0;        //Переменная, с помощью которой делается проверка на остановку динамического добавления билетов
        for (int row = 0; row < rowsCount; row++){
            rowTicketsLayout[row] = new LinearLayout(getContext());                                                                                                             //Строка с парой (одним) билетом
            rowTicketsLayout[row].setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowTicketsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rowTicketsLayoutParams.setMargins(0,0,0,margin_dp);
            rowTicketsLayout[row].setLayoutParams(rowTicketsLayoutParams);


            for (int curTicket = 0; curTicket < 2; curTicket++){
                if (correctCountTickets >= countTickets) {                               //Недопускание выхода за массив количества билетов на фильм у пользователя (из БД)
                    LinearLayout plugLayout = new LinearLayout(getContext());
                    plugLayout.setLayoutParams(specificTicketLayout[0][0].getLayoutParams()); //Создать новый лейаут (заглушку), взять параметры самого первого (он существует для выравнивания лейаутов на первой итерации)
                    rowTicketsLayout[row].addView(plugLayout);
                    plugLayout.setVisibility(View.INVISIBLE);
                    break;
                }


                specificTicketLayout[row][curTicket] = new LinearLayout(getContext());                                                                                                               //Лейаут содержимого билета
                specificTicketLayout[row][curTicket].setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams specificTicketLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                specificTicketLayoutParams.setMargins(margin_dp/2, 0,margin_dp/2,0);
                specificTicketLayout[row][curTicket].setLayoutParams(specificTicketLayoutParams);
                specificTicketLayout[row][curTicket].setPadding(margin_dp/2,margin_dp/2,margin_dp/2,margin_dp/2);
                specificTicketLayout[row][curTicket].setBackgroundColor(Color.WHITE);
                specificTicketLayout[row][curTicket].setGravity(Gravity.CENTER_HORIZONTAL);





                moviePoster[curTicket] = new ImageView(getContext());                                                                                           //Постер фильма в билете
                int moviePosterWidth = 150;
                int moviePosterHeight = 225;
                int moviePosterWidth_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, moviePosterWidth, getResources().getDisplayMetrics());
                int moviePosterHeight_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, moviePosterHeight, getResources().getDisplayMetrics());
                LinearLayout.LayoutParams moviePosterParams = new LinearLayout.LayoutParams(moviePosterWidth_dp, moviePosterHeight_dp);
                moviePosterParams.setMargins(0,0,0,margin_dp);
                moviePoster[curTicket].setLayoutParams(moviePosterParams);
                moviePoster[curTicket].setImageDrawable(getResources().getDrawable(R.drawable.movieposter));


                movieName[curTicket] = new TextView(getContext());                                                                                              //Название фильма в билете
                movieName[curTicket].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                movieName[curTicket].setGravity(Gravity.CENTER);
                movieName[curTicket].setTextColor(Color.BLACK);
                movieName[curTicket].setText(db_movieName[correctCountTickets]);


                sessionDate[curTicket] = new TextView(getContext());                                                                                            //Время сеанса в билете
                sessionDate[curTicket].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                sessionDate[curTicket].setGravity(Gravity.CENTER);
                sessionDate[curTicket].setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                sessionDate[curTicket].setText(app_sessionDate[correctCountTickets]);


                specificTicketLayout[row][curTicket].addView(moviePoster[curTicket]);    //Добавить постер, название и дату в лейаут с билетом
                specificTicketLayout[row][curTicket].addView(movieName[curTicket]);
                specificTicketLayout[row][curTicket].addView(sessionDate[curTicket]);
                specificTicketLayout[row][curTicket].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                rowTicketsLayout[row].addView(specificTicketLayout[row][curTicket]);     //Добавить билет в строку

                correctCountTickets++;
            }

            profileScrollLayout.addView(rowTicketsLayout[row]);                     //Добавить строку в общий лейаут
        }

        return root;
    }

    @SuppressLint("SimpleDateFormat")
    private String[] dateSession_dbToApp(String[] getDate) throws ParseException {
        List<String> list_setDate = new ArrayList<>();
        String[] array_setDate = null;
        try{
            SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfOut = new SimpleDateFormat("d MMMM yyyy");
            for (int i = 0; i < getDate.length; i++){
                Date date = sdfIn.parse(getDate[i]);
                list_setDate.add(sdfOut.format(date));
            }
            array_setDate = list_setDate.toArray(new String[getDate.length]);
        }
        catch (Exception e){e.getMessage();}

        return array_setDate;
    }
}