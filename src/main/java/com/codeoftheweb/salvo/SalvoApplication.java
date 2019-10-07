package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository repositoryPlayer, GameRepository repositoryGame,
									  GamePlayerRepository repositoryGamePlayer, ShipRepository repositoryShip,
									  SalvoRepository repositorySalvo, ScoreRepository repositoryScore) {
		return (args) -> {
			Player player1 = new Player ("j.bauer@ctu.gov");
			Player player2 = new Player ("c.obrian@ctu.gov");
			Player player3 = new Player ("kim_bauer@gmail.com");
			Player player4 = new Player ("t.almeida@ctu.gov");
			Player player5 = new Player ("d.palmer@whitehouse.gov");

			Date date1 = new Date ();
			Date date2 = new Date(System.currentTimeMillis() - 3600 * 1000);
			Date date3 = new Date(System.currentTimeMillis() - 7200 * 1000);
			Date date4 = new Date(System.currentTimeMillis() - 10800 * 1000);
			Date date5 = new Date(System.currentTimeMillis() - 14400 * 1000);
			Date date6 = new Date(System.currentTimeMillis() - 18000 * 1000);
			Date date7 = new Date(System.currentTimeMillis() - 21600 * 1000);
			Date date8 = new Date(System.currentTimeMillis() - 25200 * 1000);

			Game game1 = new Game(date1);
			Game game2 = new Game(date2);
			Game game3 = new Game(date3);
			Game game4 = new Game(date4);
			Game game5 = new Game(date5);
			Game game6 = new Game(date6);
			Game game7 = new Game(date7);
			Game game8 = new Game(date8);


			GamePlayer gamePlayer1= new GamePlayer(game1, player1, date1);
			GamePlayer gamePlayer2= new GamePlayer(game1, player2, date1);
			GamePlayer gamePlayer3= new GamePlayer(game2, player1, date2);
			GamePlayer gamePlayer4= new GamePlayer(game2, player2, date2);
			GamePlayer gamePlayer5= new GamePlayer(game3, player2, date3);
			GamePlayer gamePlayer6= new GamePlayer(game3, player4, date3);
			GamePlayer gamePlayer7= new GamePlayer(game4, player1, date4);
			GamePlayer gamePlayer8= new GamePlayer(game4, player2, date4);
			GamePlayer gamePlayer9= new GamePlayer(game5, player4, date5);
			GamePlayer gamePlayer10= new GamePlayer(game5, player1, date5);
			GamePlayer gamePlayer11= new GamePlayer(game6, player3, date6);
			GamePlayer gamePlayer12= new GamePlayer(game7, player4, date7);
			GamePlayer gamePlayer13= new GamePlayer(game8, player3, date8);

			System.out.println(gamePlayer6.toString());

			List<String> shipLocations1 = Arrays.asList("H2", "H3", "H4");
			List<String> shipLocations2 = Arrays.asList("E1", "F1", "G1");
			List<String> shipLocations3 = Arrays.asList("B4", "B5");
			List<String> shipLocations4 = Arrays.asList("B5", "C5", "D5");
			List<String> shipLocations5 = Arrays.asList("F1", "F2");
			List<String> shipLocations6 = Arrays.asList("B5", "C5", "D5");
			List<String> shipLocations7 = Arrays.asList("C6", "C7");
			List<String> shipLocations8 = Arrays.asList("A2", "A3", "A4");
			List<String> shipLocations9 = Arrays.asList("G6", "H6");


			Ship ship1 = new Ship("Destroyer", shipLocations1);
			Ship ship2 = new Ship("Submarine", shipLocations2);
			Ship ship3 = new Ship("Patrol Boat", shipLocations3);
			Ship ship4 = new Ship("Destroyer", shipLocations4);
			Ship ship5 = new Ship("Patrol Boat", shipLocations5);
			Ship ship6 = new Ship("Submarine", shipLocations6);
			Ship ship7 = new Ship("Patrol Boat", shipLocations7);
			Ship ship8 = new Ship("Destroyer", shipLocations8);
			Ship ship9 = new Ship("Patrol Boat", shipLocations9);

			gamePlayer1.addShip(ship1);
			gamePlayer1.addShip(ship2);
			gamePlayer1.addShip(ship3);
			gamePlayer2.addShip(ship4);
			gamePlayer2.addShip(ship5);
			gamePlayer3.addShip(ship6);
			gamePlayer3.addShip(ship7);
			gamePlayer4.addShip(ship8);
			gamePlayer4.addShip(ship9);

			Date turn1 = new Date();
			Date turn2 = new Date();

			List<String> salvoLocations1=Arrays.asList("B5", "C5", "F1");
			List<String> salvoLocations2=Arrays.asList("B4", "B5", "B6");
			List<String> salvoLocations3=Arrays.asList("F2", "D5");
			List<String> salvoLocations4=Arrays.asList("E1", "H3", "A2");

			Salvo salvo1= new Salvo(turn1, salvoLocations1);
			Salvo salvo2= new Salvo(turn1, salvoLocations2);
			Salvo salvo3= new Salvo(turn2, salvoLocations3);
			Salvo salvo4= new Salvo(turn2, salvoLocations4);

			gamePlayer1.addSalvo(salvo1);
			gamePlayer1.addSalvo(salvo3);
			gamePlayer2.addSalvo(salvo2);
			gamePlayer2.addSalvo(salvo4);

			Date finishDate1 = new Date(System.currentTimeMillis() - 1800 * 1000);
			Date finishDate2 = new Date(System.currentTimeMillis() - 5400 * 1000);
			Date finishDate3 = new Date(System.currentTimeMillis() - 7200 * 1000);
			Date finishDate4 = new Date(System.currentTimeMillis() - 9000 * 1000);

			double playerScore1 = 1.0;
			double playerScore2= 0;
			double playerScore3=0.5;
			double playerScore4=0.5;
			double playerScore5 = 1.0;
			double playerScore6= 0;
			double playerScore7=0.5;
			double playerScore8=0.5;
			//double playerScore9=null;
			//double playerScore10=null;

			Score score1= new Score(finishDate1, playerScore1, game1);
			Score score2= new Score(finishDate1, playerScore2, game1);
			Score score3= new Score(finishDate2, playerScore3, game2);
			Score score4= new Score(finishDate2, playerScore4, game2);
			Score score5= new Score(finishDate3, playerScore5, game3);
			Score score6= new Score(finishDate3, playerScore6, game3);
			Score score7= new Score(finishDate4, playerScore7, game4);
			Score score8= new Score(finishDate4, playerScore8, game4);

			player1.addScore(score1);
			player2.addScore(score2);
			player1.addScore(score3);
			player2.addScore(score4);
			player2.addScore(score5);
			player4.addScore(score6);
			player2.addScore(score7);
			player1.addScore(score8);

			repositoryPlayer.save(player1);
			repositoryPlayer.save(player2);
			repositoryPlayer.save(player3);
			repositoryPlayer.save(player4);
			repositoryPlayer.save(player5);

			repositoryGame.save(game1);
			repositoryGame.save(game2);
			repositoryGame.save(game3);
			repositoryGame.save(game4);
			repositoryGame.save(game5);
			repositoryGame.save(game6);
            repositoryGame.save(game7);
            repositoryGame.save(game8);

			repositoryGamePlayer.save(gamePlayer1);
			repositoryGamePlayer.save(gamePlayer2);
			repositoryGamePlayer.save(gamePlayer3);
			repositoryGamePlayer.save(gamePlayer4);
			repositoryGamePlayer.save(gamePlayer5);
			repositoryGamePlayer.save(gamePlayer6);
			repositoryGamePlayer.save(gamePlayer7);
			repositoryGamePlayer.save(gamePlayer8);
			repositoryGamePlayer.save(gamePlayer9);
			repositoryGamePlayer.save(gamePlayer10);
			repositoryGamePlayer.save(gamePlayer11);
			repositoryGamePlayer.save(gamePlayer12);
			repositoryGamePlayer.save(gamePlayer13);

			repositoryShip.save(ship1);
			repositoryShip.save(ship2);
			repositoryShip.save(ship3);
			repositoryShip.save(ship4);
			repositoryShip.save(ship5);
			repositoryShip.save(ship6);
			repositoryShip.save(ship7);
			repositoryShip.save(ship8);
			repositoryShip.save(ship9);

			repositorySalvo.save(salvo1);
			repositorySalvo.save(salvo2);
			repositorySalvo.save(salvo3);
			repositorySalvo.save(salvo4);

			repositoryScore.save(score1);
			repositoryScore.save(score2);
			repositoryScore.save(score3);
			repositoryScore.save(score4);
			repositoryScore.save(score5);
			repositoryScore.save(score6);
			repositoryScore.save(score7);
			repositoryScore.save(score8);
		};
	}


}
