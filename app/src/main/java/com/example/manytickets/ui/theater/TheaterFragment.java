package com.example.manytickets.ui.theater;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.manytickets.DBcontroller.DatabaseController;
import com.example.manytickets.R;
import com.example.manytickets.TheaterPackage.SpecificTheater;

import java.sql.SQLException;


public class TheaterFragment extends Fragment {
    DatabaseController databaseController;

    private TheaterViewModel theaterViewModel;

    private static int countTheaters = 0;
    LinearLayout theatersVerticalLayout;
    LinearLayout[] specificTheaterLayout;
    TextView[] theaterName;
    TextView[] theaterAddress;
    Space[] upperSpace, bottomSpace;

    String[] db_theaterId;
    String[] db_theaterName;
    String[] db_theaterAddress;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        theaterViewModel =
                ViewModelProviders.of(this).get(TheaterViewModel.class);
        View root = inflater.inflate(R.layout.fragment_theater, container, false);
//        final TextView textView = root.findViewById(R.id.text_dashboard);
//        theaterViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        setHasOptionsMenu(true);

        databaseController = new DatabaseController();
        try {
            databaseController.setConnection();
            countTheaters = databaseController.getTheaterCount();       //Количество кинотеатров

            String[][] theaterInfo = databaseController.getTheaters();
            db_theaterId = theaterInfo[0];
            db_theaterName = theaterInfo[1];       //Список названий кинотеатров
            db_theaterAddress = theaterInfo[2];    //Список адресов кинотеатров
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }


        int margin = 10;                                                                                                            //Желаемые отступы (для лейаута, текста и т.д.)
        int margin_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, margin, getResources().getDisplayMetrics());   //Конвертация желаемых отступов в DP


        theatersVerticalLayout = root.findViewById(R.id.theatersVerticalLayout);
        specificTheaterLayout = new LinearLayout[countTheaters];
        theaterName = new TextView[countTheaters];
        theaterAddress = new TextView[countTheaters];
        upperSpace = new Space[countTheaters];
        bottomSpace = new Space[countTheaters];
        //final String[] db_theaterName = {"Синема Парк Европа Сити Молл", "The Egyptian Theatre Hollywood"};
        //final String[] db_theaterAddress = {"Волгоград, просп. имени В.И.Ленина, 54б, ТЦ «Европа сити-молл»", "6712 Hollywood Blvd, Los Angeles, CA 90028"};

        for (int i = 0; i < countTheaters; i++){
            specificTheaterLayout[i] = new LinearLayout(getContext());
            specificTheaterLayout[i].setOrientation(LinearLayout.VERTICAL);                                                                                                         //Установить ВЕРТИКАЛЬНУЮ ориентацию каждому лейтауту с кинотеатром
            LinearLayout.LayoutParams specificTheaterLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);  //Параметры ВЕРТИКАЛЬНОГО лейаута
            specificTheaterLayoutParams.setMargins(0,0,0,margin_dp);                                                                                                //Параметр отступов между лейаута кинотеатров
            specificTheaterLayout[i].setLayoutParams(specificTheaterLayoutParams);                                                                                                  //Установить ВЕРТИКАЛЬНОМУ, настроенные выше параметры
            specificTheaterLayout[i].setPadding(margin_dp,margin_dp,margin_dp,margin_dp);                                                                                           //Установить паддинг для лейаута с кинотеатром
            if (i % 2 == 0)
                specificTheaterLayout[i].setBackgroundColor(Color.parseColor("#765783"));                                                                                //Установить цвет фона
            else
                specificTheaterLayout[i].setBackgroundColor(Color.parseColor("#5D3F6A"));                                                                                //Установить цвет фона
            final int backgroundColor = ((ColorDrawable) specificTheaterLayout[i].getBackground()).getColor();


            theaterName[i] = new TextView(getContext());
            theaterName[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));  //Параметры поля с названием кинотеатра
            theaterName[i].setGravity(Gravity.CENTER);                                                                                                      //Установить гравити для названия
            theaterName[i].setTextColor(Color.WHITE);                                                                                                       //Установить цвет текста
            theaterName[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);                                                                                //Установить размер текста
            theaterName[i].setText(db_theaterName[i]);                                                                                                      //Установить название кинотеатрта
            theaterName[i].setId(Integer.parseInt(db_theaterId[i]));                                                                                        //Присвоить полю id кинотеатра


            theaterAddress[i] = new TextView(getContext());
            theaterAddress[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));   //Параметры лейаута с названием кинотеатра
            theaterAddress[i].setGravity(Gravity.CENTER);                                                                                                       //Установить гравити для названия
            theaterAddress[i].setTextColor(Color.parseColor("#CCCCCC"));                                                                             //Установить цвет текста
            theaterAddress[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);                                                                                 //Установить размер текста
            theaterAddress[i].setText(db_theaterAddress[i]);                                                                                                    //Установить адрес кинотеатра


            upperSpace[i] = new Space(getContext());
            upperSpace[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()))); //Параметры верхнего отступа
            bottomSpace[i] = new Space(getContext());
            bottomSpace[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics())));//Параметры нижнего отступа


            specificTheaterLayout[i].addView(upperSpace[i]);        //Добавить в лейаут с кинотеатром - верхний отступ
            specificTheaterLayout[i].addView(theaterName[i]);       //Добавить в лейаут с кинотеатром - название кинотеатра
            specificTheaterLayout[i].addView(theaterAddress[i]);    //Добавить в лейаут с кинотеатром - адрес кинотеатра
            specificTheaterLayout[i].addView(bottomSpace[i]);       //Добавить в лейаут с кинотеатром - нижний отступ
            final int finalI = i;
            specificTheaterLayout[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSpecificTheaterActivity(theaterName[finalI].getId(), backgroundColor);  //Открыть активити выбранного кинотеатра
                }
            });

            theatersVerticalLayout.addView(specificTheaterLayout[i]);   //Добвить в общий ВЕРТИКАЛЬНЫЙ лейаут, лейаут с каждым кинотеатром
        }



        return root;
    }


    public void openSpecificTheaterActivity(Integer theaterId, Integer backgroundColor){
        Intent intent = new Intent(getActivity(), SpecificTheater.class);
        intent.putExtra("theaterId", theaterId);
        intent.putExtra("theaterBackgroundColor", backgroundColor);
        startActivity(intent);
    }



    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        menuInflater.inflate(R.menu.search_menu_toolbar, menu);     //Установка, заранее созданного меню
        MenuItem searchItem = menu.findItem(R.id.action_search);    //Нахождение значка поиска в меню
        super.onCreateOptionsMenu(menu, menuInflater);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);   //Объявление поиск менеджера
        final SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));    //Получить информацию находящуюся во view поиска ???
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();                //Убрать фокус с view после принятия запроса
                theatersVerticalLayout.clearFocus();    //Убрать фокус с ВЕРТИКАЛЬНОГО лейаута после принятия запроса

                return false;                           //false = не обрабатывать запрос слушателем
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                selectFound(newText);
                return false;   //false = view должно выполнять просто показ всех доступных вариантов, не перехватываяюсь слушателем
            }

            public void selectFound(String newText){
                for (int index = 0; index < countTheaters; index++){
                    //Проверяет каждый кинотеатр, находящийся на странице, и если находит совпадения в запросе, то включает данный лейаут и переходит на следующую итерацию...
                    if (theaterName[index].getText().toString().toLowerCase().contains(newText.toLowerCase())){
                        specificTheaterLayout[index].setVisibility(View.VISIBLE);
                        continue;
                    }
                    //иначе выключает видимость для данного лейаута с кинотеатром и сдвигает следующие вверх
                    specificTheaterLayout[index].setVisibility(View.GONE);
                }
            }
        });
    }
}