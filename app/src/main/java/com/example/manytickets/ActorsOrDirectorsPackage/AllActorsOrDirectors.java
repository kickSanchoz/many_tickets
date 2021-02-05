package com.example.manytickets.ActorsOrDirectorsPackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavDeepLinkBuilder;

import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

public class AllActorsOrDirectors extends AppCompatActivity {
    DatabaseController databaseController;

    private int countPerson = 10;
    String[] db_personsId = null;
    String[] db_personsName = null;

    String profession = null;

    private TextView actorOrDirectorTextView;

    private LinearLayout personVerticalLayout;
    private LinearLayout[] personHorizontalLayout;
    private ImageView[] personPhoto;
    private TextView[] personName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_actors_or_directors);


        Toolbar home_toolbar = findViewById(R.id.home_toolbar_id);      //Объявление тулбара
        setSupportActionBar(home_toolbar);
        home_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toHomeFragmentTransition();                                 //Переключение на домашнее активити
            }
        });


        Intent receiveIntent = getIntent();
        String title = receiveIntent.getStringExtra("actorOrDirector");


        if (title == null){
            profession = "!!Error from AllActorsOrDirectors Activity!!";
        }
        else if (title.equals("Directors")){
            profession = "Director";    //Если из предыдущего активити получено директоРЫ, то установить профессию равной директоР
        }
        else if(title.equals("Actors")){
            profession = "Actor";       //Если из предыдущего активити получено актеРЫ, то установить профессию равной актеР
        }

        actorOrDirectorTextView = findViewById(R.id.actorOrDirectorTextView);
        if (title == null){
            actorOrDirectorTextView.setText("Can't get profession :(");
        }
        else {
            actorOrDirectorTextView.setText(title);
        }


        databaseController = new DatabaseController();
        try{
            databaseController.setConnection();

            String[][] personsInfo = databaseController.getAllPersons(profession);
            db_personsId = personsInfo[0];
            db_personsName = personsInfo[1];
            countPerson = db_personsId.length;

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }



        int margin = 10;                                                                                                            //Желаемые отступы (для лейаута, текста и т.д.)
        int margin_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, margin, getResources().getDisplayMetrics());   //Конвертация желаемых отступов в DP


        personVerticalLayout = findViewById(R.id.personVerticalLayout);
        personHorizontalLayout = new LinearLayout[countPerson];
        personPhoto = new ImageView[countPerson];
        personName = new TextView[countPerson];
        //String[] db_personName = {"Christopher Lloyd", "Heath Ledger", "Morgan Freeman"};                                //Имя актера, полученное из БД

        if (countPerson == 0){                                                                                                                          //Если ни один человек не найден, то вывести текст ошибки
            TextView errorView = new TextView(getApplicationContext());
            errorView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            errorView.setText("People ain't found");
            errorView.setTextColor(Color.BLACK);
            errorView.setGravity(Gravity.CENTER);
            personVerticalLayout.addView(errorView);
        }

        for (int i = 0; i < countPerson; i++){
            personHorizontalLayout[i] = new LinearLayout(getApplicationContext());
            personHorizontalLayout[i].setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams personHorizontalLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);  //Параметры ВНУТРЕННЕГО ГОРИЗОНТАЛЬНОГО лейаута: ширина(родителя) + высота(вложенного контента)
            personHorizontalLayoutParams.setMargins(0,0,0,margin_dp/5);                                                                                      //Параметр отступов: со всех сторон относительно ВНЕШНЕГО лейаута
            personHorizontalLayout[i].setLayoutParams(personHorizontalLayoutParams);                                                                                                 //Присвоить ВНУТРЕННЕМУ ГОРИЗОНТАЛЬНОМУ лейату, настроенные выше параметры
            personHorizontalLayout[i].setPadding(margin_dp/2,margin_dp/2,margin_dp/2,margin_dp/2);
            personHorizontalLayout[i].setBackgroundColor(Color.WHITE);

            personPhoto[i] = new ImageView(getApplicationContext());
            int photoWidthSize = 45;                                                                                                                    //Желаемая ширина фотографии актера
            int photoHeightSize = 60;                                                                                                                   //Желаемая высота фотографии актера
            int photoWidthSize_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, photoWidthSize, getResources().getDisplayMetrics());   //Ширина фотографии актера в DP
            int photoHeightSize_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, photoHeightSize, getResources().getDisplayMetrics()); //Высота фотографии актера в DP
            LinearLayout.LayoutParams personPhotoParams = new LinearLayout.LayoutParams(photoWidthSize_dp, photoHeightSize_dp, 0);               //Параметры фотографии актера
            personPhotoParams.setMargins(0,0,margin_dp,0);                                                                            //Параметр отступов: справа от фотографии
            personPhoto[i].setLayoutParams(personPhotoParams);                                                                                          //Установить фотографии, установленные выше параметры
            personPhoto[i].setImageDrawable(getDrawable(R.drawable.person));                                                                            //Установить фотографии, выбранную картинку


            personName[i] = new TextView(getApplicationContext());
            personName[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1)); //Параметры имени актера
            personName[i].setText(db_personsName[i]);                                                                                                                //Присвоить полю имя актера
            personName[i].setId(Integer.parseInt(db_personsId[i]));
            /* Для проверки имени -----------MUST BE DELETE-----------
            if (i == 7 ){
                personName[i].setText(db_personName[1]);
            }
            else if (i == 8){
                personName[i].setText(db_personName[2]);
            }
             */

            personName[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);                                                                                          //Установить размер текста
            personName[i].setTextColor(Color.BLACK);                                                                                                                 //Установить цвет текста
            personName[i].setGravity(Gravity.CENTER_VERTICAL);                                                                                                       //Установить текст по середние поля


            personHorizontalLayout[i].addView(personPhoto[i]);                           //Добавить в ГОРИЗОНТАЛЬНЫЙ лейаут фото человека
            personHorizontalLayout[i].addView(personName[i]);                            //Добавить в ГОРИЗОНТАЛЬНЫЙ лейаут имя человека
            final Integer personId = personName[i].getId();                              //Запомнить id человека для передачи в следующее активити
            personName[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openSpecificActorOrDirectorActivity(personId, profession);     //Переход к активити с актером или директором
                }
            });


            personVerticalLayout.addView(personHorizontalLayout[i]);
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


    public void openSpecificActorOrDirectorActivity(Integer personId, String profession){
        Intent intent = new Intent(this, SpecificActorOrDirector.class);
        intent.putExtra("personId",personId);
        intent.putExtra("profession",profession);
        startActivity(intent);
    }


    public void addRemovePeople(){
        Intent intent = new Intent(this, AddOrRemovePerson.class);
        intent.putExtra("profession", profession);
        startActivity(intent);
    }


    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.search_menu_toolbar, menu);     //Установка, заранее созданного меню

        MenuItem itemAddActorOrDirector = menu.findItem(R.id.action_addActorOrDirector);
        itemAddActorOrDirector.setVisible(true);
        itemAddActorOrDirector.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addRemovePeople();
                return false;
            }
        });

        MenuItem searchItem = menu.findItem(R.id.action_search);    //Нахождение значка поиска в меню
        SearchManager searchManager = (SearchManager) AllActorsOrDirectors.this.getSystemService(Context.SEARCH_SERVICE);  //Объявление поиск менеджера


        final SearchView searchView = (SearchView) searchItem.getActionView();  //Объявление view, находящегося при открытии поиска
//        if (searchItem != null) {
//            searchView = (SearchView) searchItem.getActionView(); // Если значок поиска был найден, то получить view при открытии поиска
//        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(AllActorsOrDirectors.this.getComponentName())); //Получить информацию находящуюся во view поиска ???
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();            //Убрать фокус с view после принятия запроса
                personVerticalLayout.clearFocus();  //Убрать фокус с ВЕРТИКАЛЬНОГО лейаута после принятия запроса
                return false;                       //false = не обрабатывать запрос слушателем
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty())
                {
                    actorOrDirectorTextView.setVisibility(View.VISIBLE);
                }
                else{
                    actorOrDirectorTextView.setVisibility(View.GONE);
                }
                selectFound(newText);
                return false;   //false = view должно выполнять просто показ всех доступных вариантов, не перехватываяюсь слушателем
            }

            public void selectFound(String newText){
                for (int index = 0; index < countPerson; index++){
                    //Проверяет каждый фильм, находящийся на странице, и если находит совпадения в запросе, то включает данный лейаут и переходит на следующую итерацию...
                    if (personName[index].getText().toString().toLowerCase().contains(newText.toLowerCase())){
                        personHorizontalLayout[index].setVisibility(View.VISIBLE);
                        continue;
                    }
                    //иначе выключает видимость для данного лейаута с фильмом и сдвигает следующие вверх
                    personHorizontalLayout[index].setVisibility(View.GONE);
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}