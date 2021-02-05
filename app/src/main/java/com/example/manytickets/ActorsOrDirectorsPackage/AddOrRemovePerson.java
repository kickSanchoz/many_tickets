package com.example.manytickets.ActorsOrDirectorsPackage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavDeepLinkBuilder;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.manytickets.DBcontroller.DatabaseController;
import com.example.manytickets.R;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddOrRemovePerson extends AppCompatActivity {
    DatabaseController databaseController;

    String[] db_personsId;
    String[] db_personName;

    private String profession;

    private TextView testTextView;
    private Calendar selectedDate = Calendar.getInstance();


    Intent receiveIntent;
    private int countPerson = 10;
    boolean upperSegmentSelected = true;

    private LinearLayout mainLayout;


    LinearLayout.LayoutParams realSegmentLayoutParams;
    LinearLayout.LayoutParams fakeSegmentLayoutParams;
    LinearLayout.LayoutParams fake_arrowNtext;

    private LinearLayout addSegmentLayout_fake;
    private TextView addText;
    private ImageView addArrow;

    private LinearLayout removeSegmentLayout_fake;
    private TextView removeText;
    private ImageView removeArrow;

    private LinearLayout addSegmentLayout;
    private ScrollView removeSegmentLayout;


    private LinearLayout addContentLayout;
    private TextView addTitle;
    private LinearLayout addFullnameLayout;
    private TextView addFullnameText;
    private EditText addFullnameEdit;
    private LinearLayout addBornLayout;
    private TextView addBornText;
    private TextView addBornDate;
    private ImageView addCalendarView;
    private Button btn_add;


    private LinearLayout removeContentLayout;
    private LinearLayout[] removePersonLayout;
    private ImageView[] removePersonPhoto;
    private TextView[] removePersonName;
    private ImageView[] removeIcon;


    private int margin;      //Желаемые отступы (для лейаута, текста и т.д.)
    private int margin_dp;   //Конвертация желаемых отступов в DP

    private String app_fullname;    //Имя, получаемое при добавлении актера или режиссера
    private String app_bornDate;    //Конверирования для БД дата рождения актера или режиссера

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_remove_person);

        receiveIntent = getIntent();
        profession =  receiveIntent.getStringExtra("profession");

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
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }


        margin = 10;                                                                                                            //Желаемые отступы (для лейаута, текста и т.д.)
        margin_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, margin, getResources().getDisplayMetrics());   //Конвертация желаемых отступов в DP

        mainLayout = findViewById(R.id.mainLayout);

        realSegmentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);      //Параметры для настоящих сегментов
        fakeSegmentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 10);     //Параметры для фейковых сегментов
        fake_arrowNtext = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);                        //Параметры для фейковых стрелок и текстов

        addSegmentLayout = findViewById(R.id.addSegmentLayout);         //Настоящий лейаут добавления
        addSegmentLayout.setLayoutParams(realSegmentLayoutParams);

        removeSegmentLayout = findViewById(R.id.removeSegmentLayout);   //Настоящий лейаут удаления
        removeSegmentLayout.setLayoutParams(realSegmentLayoutParams);



        //Контент фейкового добавления записи
        fake_addContent();

        //Контент фейкового удаления записи
        fake_removeContent();


        //Начальная установка видимости (что показывается изначально: добавление или удаление)
        addSegmentLayout.setVisibility(View.VISIBLE);
        removeSegmentLayout_fake.setVisibility(View.VISIBLE);
        removeSegmentLayout.setVisibility(View.GONE);
        addSegmentLayout_fake.setVisibility(View.GONE);


        //Контент добавления записи
        addContent();

        //Контент удаления записи
        removeContent();


        addSegmentLayout_fake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSegmentLayout.setVisibility(View.VISIBLE);
                removeSegmentLayout_fake.setVisibility(View.VISIBLE);

                removeSegmentLayout.setVisibility(View.GONE);
                addSegmentLayout_fake.setVisibility(View.GONE);

                upperSegmentSelected = true;
                invalidateOptionsMenu();        //Обновить меню

            }
        });


        removeSegmentLayout_fake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSegmentLayout.setVisibility(View.GONE);
                removeSegmentLayout_fake.setVisibility(View.GONE);

                removeSegmentLayout.setVisibility(View.VISIBLE);
                addSegmentLayout_fake.setVisibility(View.VISIBLE);

                upperSegmentSelected = false;
                invalidateOptionsMenu();        //Обновить меню

                removeContentLayout.removeAllViewsInLayout();
                removeContent();
            }
        });
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


    public void fake_addContent(){
        addSegmentLayout_fake = findViewById(R.id.addSegmentLayout_fake);   //Лейаут фейкового добавления актера или режиссера
        addSegmentLayout_fake.setLayoutParams(fakeSegmentLayoutParams);
        addSegmentLayout_fake.setGravity(Gravity.CENTER);

        addArrow = new ImageView(getApplicationContext());                  //Стрелка вниз для фейкового добавления
        addArrow.setLayoutParams(fake_arrowNtext);
        addArrow.setImageDrawable(getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24));

        addText = new TextView(getApplicationContext());                    //Текст фейкового добавления
        addText.setLayoutParams(fake_arrowNtext);
        addText.setGravity(Gravity.CENTER_VERTICAL);
        addText.setTextColor(Color.BLACK);
        addText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        addText.setText("ADD");

        addSegmentLayout_fake.addView(addArrow);                            //Добавить стрелку вниз в лейаут фейкового добавления
        addSegmentLayout_fake.addView(addText);                             //Добавить текст в лейаут фейкового добавления
    }


    public void fake_removeContent(){
        removeSegmentLayout_fake = findViewById(R.id.removeSegmentLayout_fake); //Лейаут фейкового удаления актера или режиссера
        removeSegmentLayout_fake.setLayoutParams(fakeSegmentLayoutParams);
        removeSegmentLayout_fake.setGravity(Gravity.CENTER);

        removeArrow = new ImageView(getApplicationContext());                   //Стрелка вверх для фейкового удаления
        removeArrow.setLayoutParams(fake_arrowNtext);
        removeArrow.setImageDrawable(getDrawable(R.drawable.ic_baseline_keyboard_arrow_up_24));

        removeText = new TextView(getApplicationContext());                     //Текст фейкового удаления
        removeText.setLayoutParams(fake_arrowNtext);
        removeText.setGravity(Gravity.CENTER_VERTICAL);
        removeText.setTextColor(Color.BLACK);
        removeText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        removeText.setText("REMOVE");

        removeSegmentLayout_fake.addView(removeArrow);                          //Добавить стрелку вверх в лайаут фейкового удаления
        removeSegmentLayout_fake.addView(removeText);                           //Добавить текст в лайаут фейкового удаления
    }


    public void addContent(){
        addContentLayout = new LinearLayout(getApplicationContext());   //Лейаут добавления записи актера или режиссера в БД
        addContentLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams addContentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentLayoutParams.setMargins(2*margin_dp,2*margin_dp,2*margin_dp,2*margin_dp);
        addContentLayout.setLayoutParams(addContentLayoutParams);


        addTitle = new TextView(getApplicationContext());               //На кого оставляется отзыв
        addTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        addTitle.setGravity(Gravity.CENTER);
        addTitle.setTextColor(Color.BLACK);
        addTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        if (profession == null)
            addTitle.setText("Can't get profession :(");
        else
            addTitle.setText(profession);


        addFullnameLayout = new LinearLayout(getApplicationContext());  //Лейаут с добавлением имени
        addFullnameLayout.setOrientation(LinearLayout.HORIZONTAL);
        addFullnameLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        addFullnameText = new TextView(getApplicationContext());        //Статичный текст Fullname:
        addFullnameText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        addFullnameText.setGravity(Gravity.BOTTOM | Gravity.LEFT);
        addFullnameText.setTextColor(Color.BLACK);
        addFullnameText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        addFullnameText.setText("Fullname: ");

        addFullnameEdit = new EditText(getApplicationContext());        //Вводимое имя
        addFullnameEdit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        addFullnameEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        addFullnameEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        addFullnameEdit.setHint("Enter full name");
        addFullnameEdit.setBackgroundTintList(getApplicationContext().getColorStateList(R.color.colorBackground));
        addFullnameEdit.setHighlightColor(getColor(R.color.colorBackground));
        addFullnameEdit.setLinkTextColor(getColor(R.color.colorBackground));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            addFullnameEdit.setTextCursorDrawable(getDrawable(R.drawable.color_cursor));
        }


        addFullnameLayout.addView(addFullnameText); //Добавить статичный текст в лейаут с добавленем имени
        addFullnameLayout.addView(addFullnameEdit); //Добавить вводимое имя в лейаут с добавлением имени


        addBornLayout = new LinearLayout(getApplicationContext());      //Лейаут с добавлением даты рождения
        addBornLayout.setOrientation(LinearLayout.HORIZONTAL);
        addBornLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        addBornText = new TextView(getApplicationContext());            //Статичный текст Born:
        addBornText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        addBornText.setTextColor(Color.BLACK);
        addBornText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        addBornText.setText("Born: ");

        addBornDate = new TextView(getApplicationContext());            //Поле с выбранной датой
        addBornDate.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        addBornDate.setTextColor(Color.BLACK);
        addBornDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
        addBornDate.setHint("Enter date of birth");

        addCalendarView = new ImageView(getApplicationContext());       //Календарь для выбора даты
        addCalendarView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        addCalendarView.setImageDrawable(getDrawable(R.drawable.ic_calendar_black));

        addBornLayout.addView(addBornText);     //Добавить статичный текст в лейаут с добавлением даты рождения
        addBornLayout.addView(addBornDate);     //Добавить поле с выбранной датой в лейаут с добавлением даты рождения
        addBornLayout.addView(addCalendarView); //Добавить календарь для выбора даты в лейаут с добавлением даты рождения

        testTextView = new TextView(getApplicationContext()); //!!!!!!!!!!!!!!!!!!!!!!!!!

        btn_add = new Button(getApplicationContext());
        LinearLayout.LayoutParams btn_addParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btn_addParams.setMargins(0,margin_dp,0,0);
        btn_add.setLayoutParams(btn_addParams);
        btn_add.setBackgroundColor(Color.parseColor("#765783"));
        btn_add.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        btn_add.setTextColor(Color.WHITE);
        btn_add.setText("ADD");

        addContentLayout.addView(addTitle);             //Добавить на кого оставляется отзыв в лейаут добавления записи актера или режиссера в БД
        addContentLayout.addView(addFullnameLayout);    //Добавить лейаут добавления имени в лейаут добавления записи актера или режиссера в БД
        addContentLayout.addView(addBornLayout);        //Добавить лейаут с добавлением даты рождения в лейаут добавления записи актера или режиссера в БД
        addContentLayout.addView(testTextView); //!!!!!!!!!!!!!!!!!!!!!!!!!
        addContentLayout.addView(btn_add);


        addSegmentLayout.addView(addContentLayout);         //Добавить лейаут добавления записи актера или режиссера в БД в сегмент добавления


        //Дата рождения
        addBornDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
                addBornDate.setError(null);
            }
        });
        addCalendarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
                addBornDate.setError(null);
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app_fullname = addFullnameEdit.getText().toString();
                String validateDate = addBornDate.getText().toString();
                if (app_fullname.equals("")){
                    addFullnameEdit.setError("Please enter a name");       //Если при добавлени отзыва поле пустое, то выдать ошибку
                }
                else if (!app_fullname.matches("^[a-zA-Z.\\-\\s]*$") || app_fullname.length() < 3 || app_fullname.length() > 100)   //Проверка на валидность введенного имени: 3-100 символов, только буквы, точки, дефисы, пробелы
                {
                    addFullnameEdit.setError("Full name must be between 3 and 100 characters, and may contain letters, dots, hyphens");
                }
                else if (validateDate.equals("")){
                    addBornDate.setError("Please enter date of birth");
                }
                else{
                    Log.d("fullname", app_fullname);
                    Log.d("born", app_bornDate);
                    databaseController.addPerson(profession,app_fullname,app_bornDate,"www.internet.net/actors_photo/test_photo.jpg");
                    addFullnameEdit.setText("");
                    addBornDate.setText("");
                }
            }
        });
    }


    public void removeContent(){
        try {
            String[][] personsInfo = databaseController.getAllPersons(profession);
            db_personsId = personsInfo[0];
            db_personName = personsInfo[1];
            countPerson = db_personName.length;
        } catch (Exception e) {
            e.printStackTrace();
        }


        removeContentLayout = findViewById(R.id.removeContentLayout);

        removePersonLayout = new LinearLayout[countPerson];
        removePersonPhoto = new ImageView[countPerson];
        removePersonName = new TextView[countPerson];
        removeIcon = new ImageView[countPerson];

        //String[] db_personName = {"Christopher Lloyd", "Michael J. Fox", "Morgan Freeman"};                                //Имя актера, полученное из БД

        for (int i = 0; i<countPerson; i++)
        {
            removePersonLayout[i] = new LinearLayout(getApplicationContext());  //Лейаут с каждый человеком для удаления (фото+имя+крестик)
            removePersonLayout[i].setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams removePersonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            removePersonLayoutParams.setMargins(0,0,0,margin_dp/5);
            removePersonLayout[i].setLayoutParams(removePersonLayoutParams);
            removePersonLayout[i].setPadding(margin_dp/2,margin_dp/2,margin_dp/2,margin_dp/2);
            removePersonLayout[i].setBackgroundColor(Color.WHITE);


            removePersonPhoto[i] = new ImageView(getApplicationContext());  //Фото человека для удаления
            int removePhotoWidth = 45;                                                                                                                      //Желаемая ширина фотографии актера
            int removePhotoHeight = 60;                                                                                                                     //Желаемая высота фотографии актера
            int removePhotoWidth_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, removePhotoWidth, getResources().getDisplayMetrics());   //Ширина фотографии актера в DP
            int removePhotoHeight_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, removePhotoHeight, getResources().getDisplayMetrics()); //Высота фотографии актера в DP
            LinearLayout.LayoutParams removePersonPhotoParams = new LinearLayout.LayoutParams(removePhotoWidth_dp, removePhotoHeight_dp, 0);
            removePersonPhotoParams.setMargins(0,0,margin_dp,0);
            removePersonPhoto[i].setLayoutParams(removePersonPhotoParams);
            removePersonPhoto[i].setImageDrawable(getDrawable(R.drawable.person));


            removePersonName[i] = new TextView(getApplicationContext());    //Имя человка для удаления
            removePersonName[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
            removePersonName[i].setGravity(Gravity.CENTER_VERTICAL);
            removePersonName[i].setTextColor(Color.BLACK);
            removePersonName[i].setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
            removePersonName[i].setText(db_personName[i]);
            removePersonName[i].setId(Integer.parseInt(db_personsId[i]));
            /* Для проверки имени -----------MUST BE DELETE-----------
            if (i == 7 ){
                removePersonName[i].setText(db_personName[1]);
            }
            else if (i == 8){
                removePersonName[i].setText(db_personName[2]);
            }
             */


            removeIcon[i] = new ImageView(getApplicationContext());         //Иконка крестика для удаления
            removeIcon[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0));
            removeIcon[i].setImageDrawable(getDrawable(R.drawable.ic_baseline_cross_24));


            removePersonLayout[i].addView(removePersonPhoto[i]);    //Добавить фото человека для удаления в лейаут с каждый человеком для удаления
            removePersonLayout[i].addView(removePersonName[i]);     //Добавить имя человека для удаления в лейаут с каждый человеком для удаления
            removePersonLayout[i].addView(removeIcon[i]);           //Добавить иконку крестика для удаления в лейаут с каждый человеком для удаления


            removeContentLayout.addView(removePersonLayout[i]);     //Добавить лейаут с каждый человеком для удаления в общий лейаут со всеми людьми

            final int finalI = i;
            removeIcon[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //removeContentLayout.removeView(removePersonLayout[finalI]); //Удалить лейаут и запись из БД
                    questionToDeleteDialog(finalI, removePersonName[finalI].getText().toString(), removePersonName[finalI].getId());  //Создание диалогового окна, чтобы узнать, действительно ли нужно удалять запись
                }
            });
        }

    }

    public void questionToDeleteDialog(final int finalI, String personName, Integer personId){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Are you sure to delete \"" + personName + "\"?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeContentLayout.removeView(removePersonLayout[finalI]); //Удалить лейаут из приложения и запись из БД
                        try{
                            databaseController.deletePerson(profession,personId);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#765783"));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#765783"));
    }


    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(AddOrRemovePerson.this, R.style.DatePickerDialogTheme, d,
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
        }
    };

    // установка начальных даты и времени
    @SuppressLint("SimpleDateFormat")
    private void setInitialDateTime() {
        addBornDate.setText(DateUtils.formatDateTime(this,
                selectedDate.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));      //Дата в формате MONTH day, yyyy

        //Получение даты для БД
        String getDate = DateUtils.formatDateTime(this,
                selectedDate.getTimeInMillis(),
                DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);    //Дата в формате mm/dd/yyyy

        try{
            SimpleDateFormat sdfIn = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd");

            Date sessionsTime = sdfIn.parse(getDate);
            app_bornDate = sdfOut.format(sessionsTime);                             //Дата для запроса в БД
        }
        catch (Exception e){e.getMessage();}
        testTextView.setText(app_bornDate);
    }



    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.search_menu_toolbar, menu);     //Установка, заранее созданного меню

        MenuItem searchItem = menu.findItem(R.id.action_search);    //Нахождение значка поиска в меню
        SearchManager searchManager = (SearchManager) AddOrRemovePerson.this.getSystemService(Context.SEARCH_SERVICE);  //Объявление поиск менеджера

        if (upperSegmentSelected){
            searchItem.setVisible(false);   //Если выбран сегмет добавления, то скрыть инонку поиска
        }
        else {
            searchItem.setVisible(true);    //Если выбран сегмент удаления, то отобразить иконку поиска
        }


        final SearchView searchView = (SearchView) searchItem.getActionView();  //Объявление view, находящегося при открытии поиска
//        if (searchItem != null) {
//            searchView = (SearchView) searchItem.getActionView(); // Если значок поиска был найден, то получить view при открытии поиска
//        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(AddOrRemovePerson.this.getComponentName())); //Получить информацию находящуюся во view поиска ???
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();            //Убрать фокус с view после принятия запроса
                removeSegmentLayout.clearFocus();  //Убрать фокус с ВЕРТИКАЛЬНОГО лейаута после принятия запроса
                return false;                       //false = не обрабатывать запрос слушателем
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                selectFound(newText);
                return false;   //false = view должно выполнять просто показ всех доступных вариантов, не перехватываяюсь слушателем
            }

            public void selectFound(String newText){
                for (int index = 0; index < countPerson; index++){
                    //Проверяет каждого человека, находящийся на странице, и если находит совпадения в запросе, то включает данный лейаут и переходит на следующую итерацию...
                    if (removePersonName[index].getText().toString().toLowerCase().contains(newText.toLowerCase())){
                        removePersonLayout[index].setVisibility(View.VISIBLE);
                        continue;
                    }
                    //иначе выключает видимость для данного лейаута с человеком и сдвигает следующие вверх
                    removePersonLayout[index].setVisibility(View.GONE);
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}