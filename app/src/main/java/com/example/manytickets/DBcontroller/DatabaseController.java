package com.example.manytickets.DBcontroller;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

public class DatabaseController{
    String classname = "com.mysql.jdbc.Driver";

    String url = "jdbc:mysql://censored/many_tickets?allowPublicKeyRetrieval=true&useSSL=false";
    String user = "censored";
    String password = "censored";

    Connection dbconnection;

    public Connection setConnection() throws SQLException, ClassNotFoundException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try{
            Class.forName(classname);
            Log.d("success","Connection to Driver successful!");
            try{
                dbconnection = DriverManager.getConnection(url, user, password);
                Log.d("working", "Connection to DB successful!");
            }
            catch (Exception e){
                Log.d("NOT EVEN CLOSE", e.getMessage());
                Log.d("NOT EVEN CLOSE (reason))", e.getCause().toString());
            }
        }
        catch(Exception ex){
            Log.d("failed","Connection failed...");

            Log.d("exeption", String.valueOf(ex));
        }
        return dbconnection;
    }


    //----------------------TheaterFragment----------------------
    //Количество кинотеатров
    int db_countTheaters;
    public Integer getTheaterCount() throws SQLException, ClassNotFoundException{
        String query_theaterCount = "select count(theater.name) as count from theater;";
        Statement statement = dbconnection.createStatement();
        ResultSet rs = statement.executeQuery(query_theaterCount);

        rs.next();
        db_countTheaters = rs.getInt("count");

        return db_countTheaters;
    }

    //ОСНОВНАЯ информация по ВСЕМ кинотеатрам (id, name, address)
    public String[][] getTheaters() throws SQLException, ClassNotFoundException{
        String query_theaterName = "select id, name, address from theater order by name;";

        List<String> list_theaterId = new ArrayList<>();
        List<String> list_theaterName = new ArrayList<>();
        List<String> list_theaterAddress = new ArrayList<>();

        String[] array_theaterId = null;
        String[] array_theaterName = null;
        String[] array_theaterAddress = null;

        try(Statement statement = dbconnection.createStatement()){
            ResultSet rs = statement.executeQuery(query_theaterName);
            while (rs.next()) {
                list_theaterId.add(rs.getString("id"));
                list_theaterName.add(rs.getString("name"));
                list_theaterAddress.add(rs.getString("address"));
            }
            rs.close();

            array_theaterId = list_theaterId.toArray(new String[db_countTheaters]);
            array_theaterName = list_theaterName.toArray(new String[db_countTheaters]);
            array_theaterAddress = list_theaterAddress.toArray(new String[db_countTheaters]);
        }
        catch (Exception st_e){st_e.getMessage();}

        String[][] theatersInfo = new String[3][];
        theatersInfo[0] = array_theaterId;
        theatersInfo[1] = array_theaterName;
        theatersInfo[2] = array_theaterAddress;
        return theatersInfo;
    }


    //----------------------SpecificTheater----------------------
    //Каждый кинотеатр
    public String[] getSpecificTheaterInfo(Integer theaterId) throws  SQLException, ClassNotFoundException{
        String query_specificTheaterInfo = "select theater.name, theater.address, round(avg(review.rating),1) as theaterRating, count(review.rating) as countReviews from theater " +
                                            "left join review on review.theater_id = theater.id " +
                                            "where theater.id = \"" + theaterId + "\";";

        String theaterName = null;
        String theaterAddress = null;
        String theaterRating = null;
        String countReviews = null;

        try (Statement statement = dbconnection.createStatement()){
            ResultSet rs = statement.executeQuery(query_specificTheaterInfo);
            rs.next();

            theaterName = rs.getString("theater.name");
            theaterAddress = rs.getString("theater.address");
            theaterRating = rs.getString("theaterRating");
            countReviews = rs.getString("countReviews");

            rs.close();
        }
        catch (Exception st_e){ Log.d("getSpecificTheaterInfo exception", st_e.getMessage());}

        String[] specificTheaterInfo = new String[4];
        specificTheaterInfo[0] = theaterName;
        specificTheaterInfo[1] = theaterAddress;
        specificTheaterInfo[2] = theaterRating;
        specificTheaterInfo[3] = countReviews;

        return specificTheaterInfo;
    }

    //Все фильмы для заданного кинотеатра в заданный день
    public String[][] getMoviesByTheaterByDay(Integer theaterId, String selectedDate) throws SQLException, ClassNotFoundException{
        String query_moviesByTheaterByDay = "select distinct(movie.name) as movieName, movie.id, AverageMovieRating(movie.id) as avgRating from movie " +
                                            "join session on session.movie_id = movie.id " +
                                            "join cinemahall on cinemahall.id = session.cinemahall_id " +
                                            "join theater on theater.id = cinemahall.theater_id " +
                                            "where theater.id = \"" + theaterId + "\" and date(session.datetime) = \"" + selectedDate + "\";";

        List<String> list_movieId = new ArrayList<>();
        List<String> list_movieName = new ArrayList<>();
        List<String> list_movieRating = new ArrayList<>();

        String[] array_movieId = null;
        String[] array_movieName = null;
        String[] array_movieRating = null;

        try (Statement statement = dbconnection.createStatement()){
            ResultSet rs = statement.executeQuery(query_moviesByTheaterByDay);
            while(rs.next()){
                list_movieName.add(rs.getString("movieName"));
                list_movieId.add(rs.getString("movie.id"));

                list_movieRating.add(rs.getString("avgRating"));
            }
            rs.close();

            array_movieId = list_movieId.toArray(new String[list_movieId.size()]);
            array_movieName = list_movieName.toArray(new String[list_movieId.size()]);
            array_movieRating = list_movieRating.toArray(new String[list_movieId.size()]);
        }
        catch (Exception st_e){ Log.d("query_moviesByTheaterByDay exception", st_e.getMessage());}

        String[][] moviesInfo = new String[3][];
        moviesInfo[0] = array_movieId;
        moviesInfo[1] = array_movieName;
        moviesInfo[2] = array_movieRating;

        return moviesInfo;
    }

    //Все сеансы для конкретного фильма в конкретном кинотеарте в конкретный день
    public String[][] getSessionsByTheaterByDayByMovie(Integer theaterId, String selectedDate, Integer movieId) throws SQLException, ClassNotFoundException{
        String query_sessionsByTheaterByDayByMovie = "select theater.name as theaterName, movie.name as movieName, session.datetime, session.cost from movie " +
                                                    "join session on session.movie_id = movie.id " +
                                                    "join cinemahall on cinemahall.id = session.cinemahall_id " +
                                                    "join theater on theater.id = cinemahall.theater_id " +
                                                    "where theater.id = \"" + theaterId + "\" and date(session.datetime) = \"" + selectedDate + "\" and movie.id = \"" + movieId + "\"";

        List<String> list_sessionsTime = new ArrayList<>();
        List<String> list_sessionsCost = new ArrayList<>();

        String[] array_sessionsTime = null;
        String[] array_sessionsCost = null;

        try(Statement statement = dbconnection.createStatement()){
            ResultSet rs = statement.executeQuery(query_sessionsByTheaterByDayByMovie);
            while(rs.next()){
                list_sessionsTime.add(rs.getString("session.datetime"));
                list_sessionsCost.add(rs.getString("session.cost"));
            }
            rs.close();

            array_sessionsTime = list_sessionsTime.toArray(new String[list_sessionsTime.size()]);
            array_sessionsCost = list_sessionsCost.toArray(new String[list_sessionsCost.size()]);
        }

        String[][] sessionsInfo = new String[2][];
        sessionsInfo[0] = array_sessionsTime;
        sessionsInfo[1] = array_sessionsCost;

        return sessionsInfo;
    }


    //----------------------AllMovies----------------------
    //Количество фильмов
    int db_countMovies;
    public Integer getMoviesCount() throws SQLException, ClassNotFoundException{
        String query_movieCount = "select count(movie.name) as count from movie;";

        try(Statement statement = dbconnection.createStatement()){
            ResultSet rs = statement.executeQuery(query_movieCount);

            rs.next();
            db_countMovies = rs.getInt("count");
            rs.close();
        }
        catch (Exception st_e){ Log.d("query_movieCount exception", st_e.getMessage());}

        return db_countMovies;
    }

    //ОСНОВНАЯ информация по ВСЕМ фильмам (id, name, avgRating)
    public String[][] getMovies() throws SQLException, ClassNotFoundException{
        String query_movies = "select movie.id, movie.name, AverageMovieRating(movie.id) as avgRating from movie " +
                                "order by movie.name;";

        List<String> list_movieId = new ArrayList<>();
        List<String> list_movieName = new ArrayList<>();
        List<String> list_movieRating = new ArrayList<>();

        String[] array_movieId = null;
        String[] array_movieName = null;
        String[] array_movieRating = null;

        try (Statement statement = dbconnection.createStatement()){
            ResultSet rs = statement.executeQuery(query_movies);
            while (rs.next()){
                list_movieId.add(rs.getString("movie.id"));
                list_movieName.add(rs.getString("movie.name"));
                list_movieRating.add(rs.getString("avgRating"));
            }
            rs.close();

            array_movieId = list_movieId.toArray(new String[db_countMovies]);
            array_movieName = list_movieName.toArray(new String[db_countMovies]);
            //List<String> movieRating_string = list_movieRating.stream().map(Objects::toString).collect(Collectors.toList());
            array_movieRating = list_movieRating.toArray(new String[db_countMovies]);
        }
        catch (Exception st_e){ Log.d("query_movie exception", st_e.getMessage());}

        String[][] moviesInfo = new String[3][];
        moviesInfo[0] = array_movieId;
        moviesInfo[1] = array_movieName;
        moviesInfo[2] = array_movieRating;
        return moviesInfo;
    }


    //----------------------SpecificMovie----------------------
    //Информация по каждому фильму (movie.name, movie.duration, movie.daterelease, director.fullname, countReviews, movie.description)
    public String[] getSpecificMovieInfo(Integer movieId){
        String query_specificMovieInfo = "select movie.name, movie.duration, movie.daterelease, director.id, director.fullname, count(review.id) as countReviews, movie.description from movie " +
                                        "left join movie_director on movie_director.movie_id = movie.id " +
                                        "left join director on director.id = movie_director.director_id " +
                                        "left join review on review.movie_id = movie.id " +
                                        "where movie.id = \"" + movieId + "\";";

        String movieName = null;
        String movieDuration = null;
        String movieDaterelease = null;
        String movieDirectorId = null;
        String movieDirectorName = null;
        String movieCountReviews = null;
        String movieDescription = null;

        try (Statement statement = dbconnection.createStatement()){
            ResultSet rs = statement.executeQuery(query_specificMovieInfo);
            rs.next();

            movieName = rs.getString("movie.name");
            movieDuration = rs.getString("movie.duration");
            movieDaterelease = rs.getString("movie.daterelease");
            movieDirectorId = rs.getString("director.id");
            movieDirectorName = rs.getString("director.fullname");
            movieCountReviews = rs.getString("countReviews");
            movieDescription = rs.getString("movie.description");

            rs.close();
        }
        catch (Exception st_e){ Log.d("getSpecificMovieInfo exception", st_e.getMessage());}

        String[] specificMovieInfo = new String[7];
        specificMovieInfo[0] = movieName;
        specificMovieInfo[1] = movieDuration;
        specificMovieInfo[2] = movieDaterelease;
        specificMovieInfo[3] = movieDirectorId;
        specificMovieInfo[4] = movieDirectorName;
        specificMovieInfo[5] = movieCountReviews;
        specificMovieInfo[6] = movieDescription;

        return specificMovieInfo;
    }

    //Рейтинг фильма по 3-м пабликам (IMDb, Kinopoisk, Rotten Tomatoes)
    public String[] getRatingPerPublic(Integer movieId){
        String query_ratingIMDb = "select movie_public.rating from movie " +
                            "join movie_public on movie_public.movie_id = movie.id " +
                            "join public on public.id = movie_public.public_id " +
                            "where movie.id = \"" + movieId + "\" and public.name = \"IMDb\";";
        String query_ratingKinopoisk = "select movie_public.rating from movie " +
                                "join movie_public on movie_public.movie_id = movie.id " +
                                "join public on public.id = movie_public.public_id " +
                                "where movie.id = \"" + movieId + "\" and public.name = \"Kinopoisk\";";
        String query_ratingRottenTomatoes = "select movie_public.rating from movie " +
                                    "join movie_public on movie_public.movie_id = movie.id " +
                                    "join public on public.id = movie_public.public_id " +
                                    "where movie.id = \"" + movieId + "\" and public.name = \"Rotten Tomatoes\";";

        String ratingIMDb = null;
        String ratingKinopoisk = null;
        String ratingRottenTomatoes = null;

        try (Statement statement = dbconnection.createStatement()){
            try {
                ResultSet rs_IMDb = statement.executeQuery(query_ratingIMDb);
                rs_IMDb.next();
                ratingIMDb = rs_IMDb.getString("movie_public.rating");
            }
            catch (Exception e_IMDb){e_IMDb.getMessage();}

            try {
                ResultSet rs_Kinopoisk = statement.executeQuery(query_ratingKinopoisk);
                rs_Kinopoisk.next();
                ratingKinopoisk = rs_Kinopoisk.getString("movie_public.rating");
            }
            catch (Exception e_Kinopoisk){e_Kinopoisk.getMessage();}

            try{
                ResultSet rs_RottenTomatoes = statement.executeQuery(query_ratingRottenTomatoes);
                rs_RottenTomatoes.next();
                ratingRottenTomatoes = rs_RottenTomatoes.getString("movie_public.rating");
            }
            catch (Exception e_RT){e_RT.getMessage();}
        }
        catch (Exception st_e){ Log.d("getSpecificMovieInfo exception", st_e.getMessage());}

        if (ratingIMDb == null) ratingIMDb = "Not rated";
        if (ratingKinopoisk == null) ratingKinopoisk = "Not rated";
        if (ratingRottenTomatoes == null) ratingRottenTomatoes = "Not rated";

        String[] movieRatingPerPublic = new String[3];
        movieRatingPerPublic[0] = ratingIMDb;
        movieRatingPerPublic[1] = ratingKinopoisk;
        movieRatingPerPublic[2] = ratingRottenTomatoes;

        return movieRatingPerPublic;
    }

    //Количество трейлеров
    int db_countTrailers;
    public Integer getTrailersCount(Integer movieId) throws SQLException, ClassNotFoundException{
        String query_trailersCount = "select movie.name, count(trailers.id) as countTrailers from trailers " +
                                    "right join movie on movie.id = trailers.movie_id " +
                                    "where movie.id = \"" + movieId + "\";";

        try {
            Statement statement = dbconnection.createStatement();
            ResultSet rs = statement.executeQuery(query_trailersCount);
            rs.next();
            db_countTrailers = rs.getInt("countTrailers");
            rs.close();
        }
        catch (Exception st_e){st_e.getMessage();}

        return db_countTrailers;
    }

    //Информация по ВСЕМ трейлерам (duration, name, url)
    public String[][] getTrailersInfo(Integer movieId) throws SQLException, ClassNotFoundException{
        String query_trailersInfo = "select trailers.duration, trailers.name, trailers.url from trailers " +
                                    "right join movie on movie.id = trailers.movie_id " +
                                    "where movie.id = \"" + movieId + "\";";

        List<String> list_trailerDuration = new ArrayList<>();
        List<String> list_trailerName = new ArrayList<>();
        List<String> list_trailerUrl = new ArrayList<>();

        String[] array_trailerDuration = null;
        String[] array_trailerName = null;
        String[] array_trailerUrl = null;

        try (Statement statement = dbconnection.createStatement()){
            ResultSet rs = statement.executeQuery(query_trailersInfo);
            while (rs.next()){
                list_trailerDuration.add(rs.getString("trailers.duration"));
                list_trailerName.add(rs.getString("trailers.name"));
                list_trailerUrl.add(rs.getString("trailers.url"));
            }
            rs.close();

            array_trailerDuration = list_trailerDuration.toArray(new String[db_countTrailers]);
            array_trailerName = list_trailerName.toArray(new String[db_countTrailers]);
            array_trailerUrl = list_trailerUrl.toArray(new String[db_countTrailers]);
        }
        catch (Exception st_e){ Log.d("query_trailersInfo exception", st_e.getMessage());}

        String[][] trailersInfo = new String[3][];
        trailersInfo[0] = array_trailerDuration;
        trailersInfo[1] = array_trailerName;
        trailersInfo[2] = array_trailerUrl;
        return trailersInfo;
    }

    //Каст фильма
    public String[][] getCast(Integer movieId) throws SQLException, ClassNotFoundException{
        String query_cast = "select actor.id, actor.fullname from movie " +
                            "join movie_actor on movie_actor.movie_id = movie.id " +
                            "join actor on actor.id = movie_actor.actor_id " +
                            "where movie.id = \"" + movieId + "\";";

        List<String> list_castId = new ArrayList<>();
        List<String> list_castName = new ArrayList<>();

        String[] array_castId = null;
        String[] array_castName = null;

        try (Statement statement = dbconnection.createStatement()){
            ResultSet rs = statement.executeQuery(query_cast);
            while (rs.next()){
                list_castId.add(rs.getString("actor.id"));
                list_castName.add(rs.getString("actor.fullname"));
            }
            rs.close();

            array_castId = list_castId.toArray(new String[list_castId.size()]);
            array_castName = list_castName.toArray(new String[list_castId.size()]);
        }
        catch (Exception st_e){ Log.d("query_cast exception", st_e.getMessage());}

        String[][] castInfo = new String[2][];
        castInfo[0] = array_castId;
        castInfo[1] = array_castName;

        return castInfo;
    }


    //----------------------SpecificActorOrDirector----------------------
    //Каждый актер или режиссер
    public String[] getPersonInfo(Integer personId, String profession){
        String query_personInfo;

        if (profession.equals("Actor")){
            query_personInfo = "select actor.fullname as name, actor.born as born from actor " +
                                "where actor.id = \"" + personId + "\";";
        }
        else if (profession.equals("Director")){
            query_personInfo = "select director.fullname as name, director.born as born from director " +
                                "where director.id = \"" + personId + "\";";
        }
        else throw new EmptyStackException();

        String personName = null;
        String personBorn = null;

        try (Statement statement = dbconnection.createStatement()){
            ResultSet rs = statement.executeQuery(query_personInfo);
            rs.next();
            personName = rs.getString("name");
            personBorn = rs.getString("born");
            rs.close();
        }
        catch (Exception st_e){ Log.d("query_actorInfo exception", st_e.getMessage());}

        String[] personInfo = new String[2];
        personInfo[0] = personName;
        personInfo[1] = personBorn;
        return personInfo;
    }

    //Фильмография АКТЕРА или РЕЖИССЕРА
    public String[][] getMoviesForPerson(Integer personId, String profession){
        String query_movies;

        if (profession.equals("Actor")){
            query_movies = "select movie.id, movie.name, AverageMovieRating(movie.id) as avgRating from movie " +
                            "join movie_actor on movie_actor.movie_id = movie.id " +
                            "join actor on actor.id = movie_actor.actor_id " +
                            "where actor.id = \"" + personId + "\" " +
                            "order by movie.name;";
        }
        else if (profession.equals("Director")){
            query_movies = "select movie.id, movie.name, AverageMovieRating(movie.id) as avgRating from movie " +
                            "join movie_director on movie_director.movie_id = movie.id " +
                            "join director on director.id = movie_director.director_id " +
                            "where director.id = \"" + personId + "\" " +
                            "order by movie.name;";
        }
        else throw new EmptyStackException();

        List<String> list_movieId = new ArrayList<>();
        List<String> list_movieName = new ArrayList<>();
        List<String> list_movieRating = new ArrayList<>();

        String[] array_movieId = null;
        String[] array_movieName = null;
        String[] array_movieRating = null;

        try (Statement statement = dbconnection.createStatement()){
            ResultSet rs = statement.executeQuery(query_movies);
            while (rs.next()){
                list_movieId.add(rs.getString("movie.id"));
                list_movieName.add(rs.getString("movie.name"));
                list_movieRating.add(rs.getString("avgRating"));
            }
            rs.close();

            array_movieId = list_movieId.toArray(new String[db_countMovies]);
            array_movieName = list_movieName.toArray(new String[db_countMovies]);
            array_movieRating = list_movieRating.toArray(new String[db_countMovies]);
        }
        catch (Exception st_e){ Log.d("query_movie exception", st_e.getMessage());}

        String[][] moviesInfo = new String[3][];
        moviesInfo[0] = array_movieId;
        moviesInfo[1] = array_movieName;
        moviesInfo[2] = array_movieRating;
        return moviesInfo;
    }


    //----------------------AllActorsOrDirectors----------------------
    //Все АКТЕРЫ или РЕЖИССЕРЫ
    public String[][] getAllPersons(String profession){
        String query_persons;

        if (profession.equals("Actor"))
        {
            query_persons = "select actor.id as id, actor.fullname as fullname from actor " +
                            "order by actor.fullname;";
        }
        else if (profession.equals("Director")){
            query_persons = "select director.id as id, director.fullname as fullname from director " +
                            "order by director.fullname;";
        }
        else throw new EmptyStackException();

        List<String> list_personsId = new ArrayList<>();
        List<String> list_personsName = new ArrayList<>();

        String[] array_personsId = null;
        String[] array_personsName = null;

        try(Statement statement = dbconnection.createStatement()){
            ResultSet rs = statement.executeQuery(query_persons);
            while (rs.next()){
                list_personsId.add(rs.getString("id"));
                list_personsName.add(rs.getString("fullname"));
            }
            rs.close();

            array_personsId = list_personsId.toArray(new String[list_personsId.size()]);
            array_personsName = list_personsName.toArray(new String[list_personsId.size()]);
        }
        catch (Exception st_e){ Log.d("query_persons exception", st_e.getMessage());}

        String[][] personsInfo = new String[2][];
        personsInfo[0] = array_personsId;
        personsInfo[1] = array_personsName;

        return personsInfo;
    }


    //----------------------ProfileFragment----------------------
    //Все билеты для каждого пользователя
    public String[][] getTicketsInfo(Integer userId){
        String query_tickets = "select movie.name, session.datetime, movie.picture from user " +
                                "join user_session on user_session.user_id = user.id " +
                                "join session on session.id = user_session.session_id " +
                                "join movie on movie.id = session.movie_id " +
                                "where user.id = \"" + userId + "\" " +
                                "order by session.datetime desc;";

        List<String> list_movieName = new ArrayList<>();
        List<String> list_sessionDate = new ArrayList<>();
        List<String> list_moviePicture = new ArrayList<>();
        String[] array_movieName = null;
        String[] array_sessionDate = null;
        String[] array_moviePicture = null;

        try (Statement statement = dbconnection.createStatement()){
            ResultSet rs = statement.executeQuery(query_tickets);
            while (rs.next()){
                list_movieName.add(rs.getString("movie.name"));
                list_sessionDate.add(rs.getString("session.datetime"));
                list_moviePicture.add(rs.getString("movie.picture"));
            }
            rs.close();

            array_movieName = list_movieName.toArray(new String[list_movieName.size()]);
            array_sessionDate = list_sessionDate.toArray(new String[list_sessionDate.size()]);
            array_moviePicture = list_moviePicture.toArray(new String[list_moviePicture.size()]);
        }
        catch (Exception st_e){ Log.d("query_persons exception", st_e.getMessage());}

        String[][] ticketsInfo = new String[3][];
        ticketsInfo[0] = array_movieName;
        ticketsInfo[1] = array_sessionDate;
        ticketsInfo[2] = array_moviePicture;

        return ticketsInfo;
    }


    //----------------------Reviews----------------------
    //Получить название фильма или кинотеатра по id
    public String getTitleById(String destination, Integer movieOrTheater_id){
        String query_titleById;

        if (destination.equals("movie")){
            query_titleById = "select movie.name as title from movie " +
                                "where movie.id = \""+ movieOrTheater_id + "\";";
        }
        else if (destination.equals("theater")){
            query_titleById = "select theater.name as title from theater " +
                                "where theater.id = \""+ movieOrTheater_id + "\";";
        }
        else throw new EmptyStackException();

        String title = null;
        try (Statement statement = dbconnection.createStatement()){
            ResultSet rs = statement.executeQuery(query_titleById);
            rs.next();
            title = rs.getString("title");
            rs.close();
        }
        catch (Exception st_e){ Log.d("query_titleById exception", st_e.getMessage());}

        return title;
    }

    //Отзывы для фильма или кинотеатра
    public String[][] getReviews(String destination, Integer movieOrTheater_id){
        String query_reviews;

        if (destination.equals("movie")){
            query_reviews = "select user.nickname, review.rating, review.text_review, review.date_review from movie " +
                            "join review on review.movie_id = movie.id " +
                            "join user on user.id = review.user_id " +
                            "where movie.id = \"" + movieOrTheater_id + "\" " +
                            "order by review.date_review desc;";
        }
        else if (destination.equals("theater")){
            query_reviews = "select user.nickname, review.rating, review.text_review, review.date_review from theater " +
                            "join review on review.theater_id = theater.id " +
                            "join user on user.id = review.user_id " +
                            "where theater.id = \"" + movieOrTheater_id + "\" " +
                            "order by review.date_review desc;";
        }
        else throw new EmptyStackException();

        List<String> list_users = new ArrayList<>();
        List<String> list_ratings = new ArrayList<>();
        List<String> list_textReviews = new ArrayList<>();
        List<String> list_dateReviews = new ArrayList<>();

        String[] array_users = null;
        String[] array_rating = null;
        String[] array_textReviews = null;
        String[] array_dateReviews = null;

        try(Statement statement = dbconnection.createStatement()){
            ResultSet rs = statement.executeQuery(query_reviews);
            while(rs.next()){
                list_users.add(rs.getString("user.nickname"));
                list_ratings.add(rs.getString("review.rating"));
                list_textReviews.add(rs.getString("review.text_review"));
                list_dateReviews.add(rs.getString("review.date_review"));
            }
            rs.close();

            array_users = list_users.toArray(new String[list_users.size()]);
            array_rating = list_ratings.toArray(new String[list_users.size()]);
            array_textReviews = list_textReviews.toArray(new String[list_users.size()]);
            array_dateReviews = list_dateReviews.toArray(new String[list_users.size()]);
        }
        catch (Exception st_e){ Log.d("query_reviews exception", st_e.getMessage());}

        String[][] reviewsInfo =  new String[4][];
        reviewsInfo[0] = array_users;
        reviewsInfo[1] = array_rating;
        reviewsInfo[2] = array_textReviews;
        reviewsInfo[3] = array_dateReviews;

        return reviewsInfo;
    }

    //ДОБАВИТЬ отзыв
    public void addReview(String destination, String textReview, String dateReview, String ratingReview, Integer user_id, Integer movieOrTheater_id){
        String query_addReview;

        if (destination.equals("movie")){
            query_addReview = "insert review (text_review, date_review, rating, movie_id, user_id, theater_id) values (" +
                                            "\"" + textReview + "\"," +
                                            "\"" + dateReview + "\"," +
                                            "\"" + ratingReview + "\"," +
                                            "\"" + movieOrTheater_id + "\"," +
                                            "\"" + user_id + "\"," +
                                            "null);";
        }
        else if (destination.equals("theater")){
            query_addReview = "insert review (text_review, date_review, rating, movie_id, user_id, theater_id) values (" +
                                            "\"" + textReview + "\"," +
                                            "\"" + dateReview + "\"," +
                                            "\"" + ratingReview + "\"," +
                                            "null," +
                                            "\"" + user_id + "\"," +
                                            "\"" + movieOrTheater_id + "\");";
        }
        else throw new EmptyStackException();

        try (Statement statement = dbconnection.createStatement()){
            statement.executeUpdate(query_addReview);
        }
        catch (Exception st_e){ Log.d("query_addReview exception", st_e.getMessage());}
    }


    //----------------------AddOrRemovePerson----------------------
    //ДОБАВИТЬ человека в БД
    public void addPerson(String profession, String fullname, String born, String photo){
        String query_addPerson;

        if (profession.equals("Actor")){
            query_addPerson = "insert actor (fullname, born, photo) value (" +
                                            "\""+ fullname + "\"," +
                                            "\"" + born + "\"," +
                                            "\"" + photo + "\");";
        }
        else if (profession.equals("Director")){
            query_addPerson = "insert director (fullname, born, photo) value (" +
                    "\""+ fullname + "\"," +
                    "\"" + born + "\"," +
                    "\"" + photo + "\");";
        }
        else throw new EmptyStackException();

        try (Statement statement = dbconnection.createStatement()){
            statement.executeUpdate(query_addPerson);
        }
        catch (Exception st_e){ Log.d("query_addPerson exception", st_e.getMessage());}
    }


    //УДАЛИТЬ актера или режиссера из БД
    public void deletePerson(String profession, Integer personId){
        String query_deletePerson;

        if (profession.equals("Actor")){
            query_deletePerson = "delete from actor where actor.id = \"" + personId + "\";";
        }
        else if (profession.equals("Director")){
            query_deletePerson = "delete from director where director.id = \"" + personId + "\";";
        }
        else throw new EmptyStackException();

        //--------------------------------------------------------------------------------------------------------------------------------------РАСКОММЕНТИРОВАТЬ----------------------------------------------------
        try (Statement statement = dbconnection.createStatement()){
            statement.executeUpdate(query_deletePerson);
        }
        catch (Exception st_e){ Log.d("query_deletePerson exception", st_e.getMessage());}
    }
}