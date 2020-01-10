package com.skilldistillery.filmquery.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class DatabaseAccessorObject implements DatabaseAccessor {
	private static final String url = "jdbc:mysql://localhost:3306/sdvid?useSSL=false";

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	private String user = "student";
	private String pass = "student";

	@Override
	public Film findFilmById(int filmId) {
		Film film = null;
		Connection conn;
		try {
			conn = DriverManager.getConnection(url, user, pass);
		String sql = "SELECT id, title, description, release_year, rating, language_id FROM film WHERE id = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, filmId);
		ResultSet filmResult = stmt.executeQuery();
		if (filmResult.next()) {
			film = new Film();
			film.setId(filmResult.getInt("id"));
			film.setTitle(filmResult.getString("title"));
			film.setDesc(filmResult.getString("description"));
			film.setRealeaseYear(filmResult.getInt("release_year"));
			film.setRating(filmResult.getString("rating"));
			film.setLangId(findLanguage(filmResult.getInt("language_id")));
			film.setActors(findActorsByFilmId(filmId));
		}	
		filmResult.close();
		stmt.close();
		conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return film;
	}

	public String findLanguage(int langId) {
		String name = "";
		Connection conn;
		try {
			conn = DriverManager.getConnection(url, user, pass);
		String sql = "SELECT name FROM language WHERE id = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, langId);
		ResultSet langResult = stmt.executeQuery();
		if (langResult.next()) {
		name =langResult.getString("name");	
		}	
		langResult.close();
		stmt.close();
		conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return name;
	}
	@Override
	public Actor findActorById(int actorId) {
		Actor actor = null;
		Connection conn;
		try {
			conn = DriverManager.getConnection(url, user, pass);
		String sql = "SELECT id, first_name, last_name FROM actor WHERE id = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, actorId);
		ResultSet actorResult = stmt.executeQuery();
		if (actorResult.next()) {
			actor = new Actor();
			actor.setId(actorResult.getInt("id"));
			actor.setFirstName(actorResult.getString("first_name"));
			actor.setLastName(actorResult.getString("last_name"));
		    actor.setFilms(findFilmsByActorId(actorId)); // An Actor has Films
		}
		actorResult.close();
		stmt.close();
		conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return actor;
	}



	@Override
	public List<Actor> findActorsByFilmId(int filmId) {
		List<Actor> actors = new ArrayList<>();
		try {
			Connection conn = DriverManager.getConnection(url, user, pass);
			String sql = "SELECT actor.id, actor.first_name, actor.last_name "+
					 " FROM actor JOIN film_actor ON actor.id = film_actor.actor_id " + 
					"JOIN film ON film.id =  film_actor.film_id "
					+" WHERE film_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, filmId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				Actor actor =  new Actor(id, firstName, lastName);
				actors.add(actor);
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return actors;
	}
	
	public List<Film> findFilmsByActorId(int actorId) {
		  List<Film> films = new ArrayList<>();
		  try {
		    Connection conn = DriverManager.getConnection(url, user, pass);
		    String sql = "SELECT id, title, description, release_year, language_id, rating "
		               +  " FROM film JOIN film_actor ON film.id = film_actor.film_id "
		               + " WHERE actor_id = ?";
		    PreparedStatement stmt = conn.prepareStatement(sql);
		    stmt.setInt(1, actorId);
		    ResultSet rs = stmt.executeQuery();
		    while (rs.next()) {
		      int filmId = rs.getInt("id");
		      String title = rs.getString("title");
		      String desc = rs.getString("description");
		      short releaseYear = rs.getShort("release_year");
		      String langId = findLanguage(rs.getInt("language_id"));      
		      String rating = rs.getString("rating");
		      Film film = new Film(filmId, title, desc, releaseYear, langId, rating);
		      films.add(film);
		    }
		    rs.close();
		    stmt.close();
		    conn.close();
		  } catch (SQLException e) {
		    e.printStackTrace();
		  }
		  return films;
		}

//	}
	public List<Film> findFilmsByKeyword(String keyword) {
		List<Film> films = new ArrayList<>();
		try {
			Connection conn = DriverManager.getConnection(url, user, pass);
			String sql = "SELECT film.id, film.title, film.description, film.release_year, film.language_id, film.rating "
					+ " FROM film JOIN film_actor ON film.id = film_actor.film_id " + " WHERE film.title LIKE ?"
					+ " OR film.description LIKE ? ";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, "%" + keyword + "%");
			stmt.setString(2, "%" + keyword + "%");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int filmId = rs.getInt("id");
				String title = rs.getString("title");
				String desc = rs.getString("description");
				short releaseYear = rs.getShort("release_year");
				String langId = findLanguage(rs.getInt("language_id"));
				String rating = rs.getString("rating");
				Film film = new Film(filmId, title, desc, releaseYear, langId, rating);
				films.add(film);
				film.setActors(findActorsByFilmId(filmId));
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return films;
	}
}
