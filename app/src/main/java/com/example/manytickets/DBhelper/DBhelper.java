/*
USELESS CLASS, DON'T USED
 */

package com.example.manytickets.DBhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBhelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME    = "manytickets.db";
    private static final int    DATABASE_VERSION = 1;


    public DBhelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ACTOR = "CREATE TABLE actor ( " +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "  fullname TEXT NOT NULL, " +
                "  born INTEGER NOT NULL, " +
                "  photo TEXT DEFAULT NULL);";
        db.execSQL(SQL_CREATE_ACTOR);

        String SQL_CREATE_TRAILERS = "CREATE TABLE `trailers` ( " +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "`name` TEXT NOT NULL, " +
                "`duration` INTEGER NOT NULL, " +
                "`url` TEXT DEFAULT NULL, " +
                "`description` text, " +
                "`movie_id` INTEGER NOT NULL, " +
                "FOREIGN KEY (`movie_id`) REFERENCES `movie` (`id`) " +
                ");";
        db.execSQL(SQL_CREATE_TRAILERS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void InsertData (SQLiteDatabase db){
        db.execSQL("insert into actor (fullname, born, photo) values ('Michael J. Fox','19610609','www.internet.net/actors_photo/michael_j_fox.jpg'), " +
                                                                    "('Christopher Lloyd','19381022','www.internet.net/actors_photo/christopher_lloyd.jpg')  ," +
                                                                    "('Robert Pattinson','19860513','www.internet.net/actors_photo/robert_pattinson.jpg')  ," +
                                                                    "('Morgan Freeman','19370601','www.internet.net/actors_photo/morgan_freeman.jpg')  ," +
                                                                    "('Christian Bale','19740130','www.internet.net/actors_photo/christian_bale.jpg')  ," +
                                                                    "('Heath Ledger','19790404','www.internet.net/actors_photo/heath_ledger.jpg')  ," +
                                                                    "('Brad Pitt','19631218','www.internet.net/actors_photo/brad_pitt.jpg')  ," +
                                                                    "('Leonardo DiCaprio','19741111','www.internet.net/actors_photo/leonardo_dicaprio.jpg')  ," +
                                                                    "('Matthew McConaughey','19691104','www.internet.net/actors_photo/matthew_mcconaughey.jpg')  ," +
                                                                    "('Scarlett Johansson','19841122','www.internet.net/actors_photo/scarlett_johansson.jpg')  ," +
                                                                    "('Hugh Jackman','19681012','www.internet.net/actors_photo/hugh_jackman.jpg')  ," +
                                                                    "('Quentin Tarantino','19630327','www.internet.net/actors_photo/quentin_tarantino.jpg')  ," +
                                                                    "('Margo Robbie','19900702','www.internet.net/actors_photo/margo_robbie.jpg')  ," +
                                                                    "('Robert Downey Jr.','19650404','www.internet.net/actors_photo/robert_downey_jr.jpg')  ," +
                                                                    "('Jamie Foxx','19671213','www.internet.net/actors_photo/jamie_foxx.jpg')  ," +
                                                                    "('Zoe Saldana','19780619','www.internet.net/actors_photo/zoe_saldana.jpg')  ," +
                                                                    "('Zendaya Coleman','19960901','www.internet.net/actors_photo/zendaya_coleman.jpg')  ," +
                                                                    "('Jennifer Lawrence','19900815','www.internet.net/actors_photo/jennifer_lawrence.jpg')  ," +
                                                                    "('Will Smith','19680925','www.internet.net/actors_photo/will_smith.jpg')  ," +
                                                                    "('Tom Cruise','19620703','www.internet.net/actors_photo/tom_cruise.jpg')  ," +
                                                                    "('Angelina Jolie','19750604','www.internet.net/actors_photo/angelina_jolie.jpg')  ," +
                                                                    "('Ben Affleck','19720815','www.internet.net/actors_photo/ben_affleck.jpg');");

        db.execSQL("insert into movie (name, daterelease, duration, picture, description) values ('Back to the Future','1991-12-27','1:56','www.internet.net/films_pictures/back_to_the_future.jpg','Marty McFly, a 17-year-old high school student, is accidentally sent thirty years into the past in a time-traveling DeLorean invented by his close friend, the eccentric scientist Doc Brown')," +
                                                                                            "('Tenet','2:30','2020-09-03','www.internet.net/films_pictures/tenet.jpg','Armed with only one word, Tenet, and fighting for the survival of the entire world, a Protagonist journeys through a twilight world of international espionage on a mission that will unfold in something beyond real time')," +
                                                                                            "('The Shawshank Redemption','1994-10-14','2:22','www.internet.net/films_pictures/the_shawshank_redemprion.jpg','Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency')," +
                                                                                            "('The Dark Knight','2008-08-14','2:32','www.internet.net/films_pictures/the_dark_knight.jpg','When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice')," +
                                                                                            "('Fight Club','2000-01-13','2:19','www.internet.net/films_pictures/fight_club.jpg','An insomniac office worker and a devil-may-care soapmaker form an underground fight club that evolves into something much, much more')," +
                                                                                            "('Inception','2010-07-22','2:28','www.internet.net/films_pictures/inception.jpg','A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O')," +
                                                                                            "('Interstellar','2014-11-06','2:49','www.internet.net/films_pictures/interstellar.jpg',\"A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival\")," +
                                                                                            "('The Prestige','2007-01-18','2:10','www.internet.net/films_pictures/the_prestige.jpg','After a tragic accident, two stage magicians engage in a battle to create the ultimate illusion while sacrificing everything they have to outwit each other')," +
                                                                                            "('Joker','2019-10-03','2:02','www.internet.net/films_pictures/joker.jpg','In Gotham City, mentally troubled comedian Arthur Fleck is disregarded and mistreated by society. He then embarks on a downward spiral of revolution and bloody crime. This path brings him face-to-face with his alter-ego: the Joker')," +
                                                                                            "('The Gentlemen','2020-02-13','1:53','www.internet.net/films_pictures/the_gentlemen.jpg','An American expat tries to sell off his highly profitable marijuana empire in London, triggering plots, schemes, bribery and blackmail in an attempt to steal his domain out from under him')," +
                                                                                            "('Once Upon a Time... in Hollywood','2019-08-08','2:41','www.internet.net/films_pictures/once_upon_a_time_in_hollywood.jpg',\"A faded television actor and his stunt double strive to achieve fame and success in the final years of Hollywood's Golden Age in 1969 Los Angeles\")," +
                                                                                            "('Ready Player One','2018-03-29','2:20','www.internet.net/films_pictures/ready_player_one.jpg','When the creator of a virtual reality called the OASIS dies, he makes a posthumous challenge to all OASIS users to find his Easter Egg, which will give the finder his fortune and control of his world')," +
                                                                                            "('The Terminator','1991-10-26','1:47','www.internet.net/films_pictures/the_terminator.jpg',\"A human soldier is sent from 2029 to 1984 to stop an almost indestructible cyborg killing machine, sent from the same year, which has been programmed to execute a young woman whose unborn son is the key to humanity's future salvation\")," +
                                                                                            "('The Shining','1980-06-13','2:26','www.internet.net/films_pictures/the_shining.jpg','A family heads to an isolated hotel for the winter where a sinister presence influences the father into violence, while his psychic son sees horrific forebodings from both past and future')," +
                                                                                            "('Green Book','2019-01-24','2:10','www.internet.net/films_pictures/green_book.jpg','A working-class Italian-American bouncer becomes the driver of an African-American classical pianist on a tour of venues through the 1960s American South')," +
                                                                                            "('The Wolf of Wall Street','2014-02-06','3:00','www.internet.net/films_pictures/the_wolf_of_wall_street.jpg','Based on the true story of Jordan Belfort, from his rise to a wealthy stock-broker living the high life to his fall involving crime, corruption and the federal government')," +
                                                                                            "('Logan','2017-03-02','2:17','www.internet.net/films_pictures/logan.jpg','In a future where mutants are nearly extinct, an elderly and weary Logan leads a quiet life. But when Laura, a mutant child pursued by scientists, comes to him for help, he must get her to safety')," +
                                                                                            "('Spider-Man: Homecoming','2017-07-06','2:13','www.internet.net/films_pictures/spider_man_homecoming.jpg','Peter Parker balances his life as an ordinary high school student in Queens with his superhero alter-ego Spider-Man, and finds himself on the trail of a new menace prowling the skies of New York City')," +
                                                                                            "('The Soloist','2009-04-24','1:57','www.internet.net/films_pictures/the_soloist.jpg','A newspaper journalist discovers a homeless musical genius and tries to improve his situation')," +
                                                                                            "('The Judge','2014-10-16','2:21','www.internet.net/films_pictures/the_jugje.jpg',\"Big-city lawyer Hank Palmer returns to his childhood home where his father, the town's judge, is suspected of murder. Hank sets out to discover the truth and, along the way, reconnects with his estranged family\")," +
                                                                                            "('Avatar','2009-12-17','2:42','www.internet.net/films_pictures/avatar.jpg','A paraplegic Marine dispatched to the moon Pandora on a unique mission becomes torn between following his orders and protecting the world he feels is his home')," +
                                                                                            "('Passengers','2016-12-22','1:56','www.internet.net/films_pictures/passengers.jpg','A malfunction in a sleeping pod on a spacecraft traveling to a distant colony planet wakes one passenger 90 years early')," +
                                                                                            "('The Hunger Games','2012-03-22','2:22','www.internet.net/films_pictures/the_hunger_games.jpg',\"Katniss Everdeen voluntarily takes her younger sister's place in the Hunger Games: a televised competition in which two teenagers from each of the twelve Districts of Panem are chosen at random to fight to the death\")," +
                                                                                            "('Men in Black','1997-09-12','1:38','www.internet.net/films_pictures/men_in_black.jpg','A police officer joins a secret organization that polices and monitors extraterrestrial interactions on Earth');");

//        db.execSQL("insert trailers (name, duration, url, description, movie_id) values ('Back to the Future','00:01:20','https://www.imdb.com/video/vi252380953?playlistId=tt0088763&ref_=tt_ov_vi','Teaser trailer',1);\n" +
//                "insert trailers (name, duration, url, description, movie_id) values ('TENET | Official Trailer','00:00:30','https://www.imdb.com/video/vi1504821529?playlistId=tt6723592&ref_=tt_ov_vi','In Cinemas Now',2);\n" +
//                "insert trailers (name, duration, url, description, movie_id) values ('Official Trailer','00:02:10','https://www.imdb.com/video/vi3877612057?playlistId=tt0111161&ref_=tt_ov_vi','Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency',3);\n" +
//                "insert trailers (name, duration, url, description, movie_id) values ('The Dark Knight','00:00:32','https://www.imdb.com/video/vi324468761?playlistId=tt0468569&ref_=tt_ov_vi','Trailer for Blu-ray/DVD release of most recent Batman installment',4);\n" +
//                "insert trailers (name, duration, url, description, movie_id) values ('Fight Club','00:01:59','https://www.imdb.com/video/vi781228825?playlistId=tt0137523&ref_=tt_ov_vi','Trailer for Fight Club',5);\n" +
//                "insert trailers (name, duration, url, description, movie_id) values ('10th Anniversary Dream Trailer','00:01:44','https://www.imdb.com/video/vi2959588889?playlistId=tt1375666&ref_=tt_ov_vi','A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O',6);\n" +
//                "insert trailers (name, duration, url, description, movie_id) values ('Trailer #4','00:02:27','https://www.imdb.com/video/vi1586278169?playlistId=tt0816692&ref_=tt_ov_vi','A group of explorers make use of a newly discovered wormhole to surpass the limitations on human space travel and conquer the vast distances involved in an interstellar voyage',7);\n" +
//                "insert trailers (name, duration, url, description, movie_id) values ('Official Trailer','00:02:34','https://www.imdb.com/video/vi2885334553?playlistId=tt0482571&ref_=tt_ov_vi','Two stage magicians engage in competitive one-upmanship in an attempt to create the ultimate stage illusion',8);\n" +
//                "insert trailers (name, duration, url, description, movie_id) values ('Theatrical Trailer','00:02:24','https://www.imdb.com/video/vi1723318041?playlistId=tt7286456&ref_=tt_ov_vi','An exploration of Arthur Fleck, a man disregarded by society',9);\n" +
//                "insert trailers (name, duration, url, description, movie_id) values ('Trailer #2','00:01:10','https://www.imdb.com/video/vi3741564697?playlistId=tt8367814&ref_=tt_ov_vi',\"American expat Mickey Pearson (Matthew McConaughey) has built a highly profitable marijuana empire in London. When word gets out that he's looking to cash out of the business forever it triggers plots, schemes, bribery and blackmail in an attempt to steal his domain out from under him\",10);\n" +
//                "insert trailers (name, duration, url, description, movie_id) values ('Interstellar - Trailer - Official Warner Bros. UK','00:02:54','https://www.youtube.com/watch?v=zSWdZVtXT7E&ab_channel=WarnerBros.UK%26Ireland','#Interstellar  -- Like the official Facebook page for updates https://www.facebook.com/InterstellarUK Follow us on Twitter at @InterstellarUK The New Official Trailer for Interstellar - ON BLU-RAY™ AND DVD MARCH 30, 2015...',7);\n" +
//                "insert trailers (name, duration, url, description, movie_id) values ('Interstellar Official Trailer #2 (2014) - Matthew McConaughey, Christopher Nolan Sci-Fi Movie HD','00:02:33','https://www.youtube.com/watch?v=Lm8p5rlrSkY&ab_channel=MovieclipsTrailers','Subscribe to TRAILERS: http://bit.ly/sxaw6h\n" +
//                "Subscribe to COMING SOON: http://bit.ly/H2vZUn\n" +
//                "Like us on FACEBOOK: http://goo.gl/dHs73\n" +
//                "Follow us on TWITTER: http://bit.ly/1ghOWmt\n" +
//                "Interstellar Official Trailer #2 (2014) - Matthew McConaughey, Christopher Nolan Sci-Fi Movie HD...',7);\n" +
//                "insert trailers (name, duration, url, description, movie_id) values ('Interstellar Movie - Official Trailer 3','00:02:27','https://www.youtube.com/watch?v=0vxOhd4qlnA&ab_channel=InterstellarMovie',\"The latest trailer from Christopher Nolan's INTERSTELLAR. In theaters & IMAX November 7th. http://interstellar.withgoogle.com\",7);\n" +
//                "insert trailers (name, duration, url, description, movie_id) values ('Interstellar Official Teaser Trailer #1 (2014) Christopher Nolan Sci-Fi Movie HD','00:01:55','https://www.youtube.com/watch?v=3WzHXI5HizQ&ab_channel=MovieclipsTrailers','Watch the TRAILER REVIEW: http://goo.gl/fhKbK0\n" +
//                "Subscribe to TRAILERS: http://bit.ly/sxaw6h\n" +
//                "Subscribe to COMING SOON: http://bit.ly/H2vZUn\n" +
//                "Like us on FACEBOOK: http://goo.gl/dHs73\n" +
//                "Interstellar Official Teaser Trailer #1 (2014) Christopher Nolan Sci-Fi Movie HD...',7);\n" +
//                "insert trailers (name, duration, url, description, movie_id) values ('Interstellar Movie - Official Trailer 2','00:02:33','https://www.youtube.com/watch?v=Rt2LHkSwdPQ&ab_channel=InterstellarMovie','The latest Interstellar movie trailer from Christopher Nolan, starring Matthew McConaughey. http://www.InterstellarMovie.com/',7);\n");
    }
}
