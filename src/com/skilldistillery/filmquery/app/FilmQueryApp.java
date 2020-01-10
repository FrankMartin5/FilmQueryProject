package com.skilldistillery.filmquery.app;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.skilldistillery.filmquery.database.DatabaseAccessor;
import com.skilldistillery.filmquery.database.DatabaseAccessorObject;
import com.skilldistillery.filmquery.entities.Film;

public class FilmQueryApp {
  
  DatabaseAccessor db = new DatabaseAccessorObject();

  public static void main(String[] args) throws SQLException, ClassNotFoundException {
    FilmQueryApp app = new FilmQueryApp();
//    app.test();
    app.launch();
  }

//  private void test() throws SQLException {
//    Film film = db.findFilmById(1);
//    System.out.println(film);
//  }

  private void launch() throws SQLException {
    Scanner input = new Scanner(System.in);
    
    startUserInterface(input);
    
    input.close();
  }

  private void startUserInterface(Scanner input) throws SQLException {
    boolean keepGoing = true;
    do {
    	System.out.println("1. Search film by id \n2. Search film by keyword \n3. Exit ");
    	String userInput =  input.next();
    	
    	switch (userInput) {
		case "1":
			System.out.println("What film would you like to retrieve?");
			userInput = input.next();
			Film pickFilm = db.findFilmById(Integer.parseInt(userInput));
			System.out.println(pickFilm);
			break;
		case "2":
			System.out.println("Please enter in a keyword: ");
			userInput = input.next();
			List<Film> keyword =  db.findFilmsByKeyword(userInput);
			for (Film film : keyword) {
				System.out.println(film);
			}
			break;
		case "3":
			keepGoing = false;
			break;
		default:
			System.out.println("That is not a valid selection");
			break;
		}
    } while(keepGoing);
  }
  

}
