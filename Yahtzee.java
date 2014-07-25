/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {
	public void run() {
		setupPlayers();
		initDisplay();
		playGame();
	}
	
	/**
	 * Prompts the user for information about the number of players, then sets up the
	 * players array and number of players.
	 */
	private void setupPlayers() {
		nPlayers = chooseNumberOfPlayers();	
		
		/* Set up the players array by reading names for each player. */
		playerNames = new String[nPlayers];
		for (int i = 0; i < nPlayers; i++) {
			/* IODialog is a class that allows us to prompt the user for information as a
			 * series of dialog boxes.  We will use this here to read player names.
			 */
			IODialog dialog = getDialog();
			playerNames[i] = dialog.readLine("Enter name for player " + (i + 1));
		}
	}
	
	/**
	 * Prompts the user for a number of players in this game, reprompting until the user
	 * enters a valid number.
	 * 
	 * @return The number of players in this game.
	 */
	private int chooseNumberOfPlayers() {
		/* See setupPlayers() for more details on how IODialog works. */
		IODialog dialog = getDialog();
		
		while (true) {
			/* Prompt the user for a number of players. */
			int result = dialog.readInt("Enter number of players");
			
			/* If the result is valid, return it. */
			if (result > 0 && result <= MAX_PLAYERS)
				return result;
			
			dialog.println("Please enter a valid number of players. It must be less than five.");
		}
	}
	
	/**
	 * Sets up the YahtzeeDisplay associated with this game.
	 */
	private void initDisplay() {
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
	}

	/**
	 * Actually plays a game of Yahtzee.  This is where you should begin writing your
	 * implementation.
	 */
	private void playGame() {
		IODialog StartGame = getDialog();
		StartGame.println("Let's Play");
		for(int Round = 0; Round < 13; Round++) {
			play1Round(playerNames);
		}
		StartGame.println("The game has ended");
	}
	private void play1Round(String[] Players) {
		for(int PlayerTurn = 0; PlayerTurn < Players.length; PlayerTurn++) {
			int[] DiceValues = new int[N_DICE];
			RandomGenerator DiceRoller = RandomGenerator.getInstance();
			for(int Die = 0; Die < N_DICE; Die++) {
				DiceValues[Die] = DiceRoller.nextInt(1, 6);
			}
			IODialog YourTurn = getDialog();
			YourTurn.println(Players[PlayerTurn] + "'s turn.");
			display.waitForPlayerToClickRoll(PlayerTurn);
			try {
				display.displayDice(DiceValues);
			} catch(ErrorException ex) {
				
			}
			int Category = display.waitForPlayerToSelectCategory();
			int Score = categoryScore(DiceValues, Category);
			display.updateScorecard(Category, PlayerTurn, Score);
		}
	}
	private int categoryScore(int[] Dice, int Category) {
		int score = 0;
		switch(Category) {
			case 0: 
				for(int Die = 0; Die < N_DICE; Die++) {
					if(Dice[Die] == 1) {
						score++;
					}
				}
				return score;
				
			case 1:
				for(int Die = 0; Die < N_DICE; Die++) {
					if(Dice[Die] == 2) {
						score += 2;
					}
				}
				return score;

			case 2:
				for(int Die = 0; Die < N_DICE; Die++) {
					if(Dice[Die] == 3) {
						score += 3;
					}
				}
				return score;
				
			case 3:
				for(int Die = 0; Die < N_DICE; Die++) {
					if(Dice[Die] == 4) {
						score += 4;
					}
				}
				return score;
				
			case 4:
				for(int Die = 0; Die < N_DICE; Die++) {
					if(Dice[Die] == 5) {
						score += 5;
					}
				}
				return score;
				
			case 5:
				for(int Die = 0; Die < N_DICE; Die++) {
					if(Dice[Die] == 6) {
						score += 6;
					}
				}
				return score;
				
			case 6:
				
			case 7:
				
			case 8:
				if(threeOfAKindElegibility(Dice)) {
					score = addAllDice(Dice);
				}
				return score;
			case 9:
				if(fourOfAKindElegibility(Dice)) {
					score = addAllDice(Dice);
				}
				return score;
			case 10:
				if(fullHouseElegibility(Dice)) {
					score = 25;
				}
				return score;
			case 11:
				if(smallStraightElegibility(Dice)) {
					score = 30;
				}
				return score;
			case 12:
				if(largeStraightElegibility(Dice)) {
					score = 40;
				}
				return score;
			case 13:
				if(yahtzeeElegibility(Dice)) {
					score = 50;
				}
				return score;
			case 14:
				score = addAllDice(Dice);
				return score;
			case 15:
				break;
			case 16:
				
				break;
			default: 
				return score;
		}
		return score;
	}
	
	private int addAllDice(int[] CurrentDice){
		int Score = 0;
		for(int DiceIndex = 0; DiceIndex < 5; DiceIndex++) {
			Score += CurrentDice[DiceIndex];
		}
		return Score;
	}
	
	private boolean yahtzeeElegibility(int[] CurrentDice) {
		if(howManyEqual(CurrentDice, CurrentDice[0]) == 5) {
			return true;
		}
		return false;
	}
	
	private boolean smallStraightElegibility(int[] CurrentDice) {
		boolean ContainsOne = false;
		boolean ContainsTwo = false;
		boolean ContainsThree = false;
		boolean ContainsFour = false;
		boolean ContainsFive = false;
		boolean ContainsSix = false;
		for(int DiceCheck = 0; DiceCheck < 5; DiceCheck++) {
			switch(CurrentDice[DiceCheck]) {
				case 1:
					ContainsOne = true;
					break;
				case 2:
					ContainsTwo = true;
					break;
				case 3:
					ContainsThree = true;
					break;
				case 4:
					ContainsFour = true;
					break;
				case 5:
					ContainsFive = true;
					break;
				case 6:
					ContainsSix = true;
					break;
				default: return false;
			}
				
		}
		if((ContainsTwo && ContainsThree && ContainsFour && ContainsFive) && (ContainsOne || ContainsSix)) {
			return true;
		}
		return false;
	}
	
	private boolean largeStraightElegibility(int[] CurrentDice) {
		for(int DiceTooCheck = 0; DiceTooCheck < 4; DiceTooCheck++) {
			int AmmountDiceEqual = howManyEqual(CurrentDice, DiceTooCheck); 
			if(AmmountDiceEqual >= 2) {
				return false;
			}
		}
		return true;
	}
	
	private boolean fullHouseElegibility(int[] CurrentDice) {
		boolean ThreeEqual = false;
		boolean TwoEqual = false;
		for(int DiceTooCheck = 0; DiceTooCheck < 3; DiceTooCheck++) {
			int AmmountDiceEqual = howManyEqual(CurrentDice, DiceTooCheck); 
			if(AmmountDiceEqual >= 3) {
				ThreeEqual = true;
			} else if(AmmountDiceEqual >= 2) {
				TwoEqual = true;
			}
			if(ThreeEqual && TwoEqual) {
				return true;
			}

		}
		return false;
	}
	
	private boolean threeOfAKindElegibility(int[] CurrentDice) {
		for(int DiceTooCheck = 0; DiceTooCheck < 3; DiceTooCheck++) {
			int AmmountDiceEqual = howManyEqual(CurrentDice, DiceTooCheck); 
			if(AmmountDiceEqual >= 3) {
				return true;
			}

		}
		return false;
	}
	
	private boolean fourOfAKindElegibility(int[] CurrentDice) {
		for(int DiceTooCheck = 0; DiceTooCheck < 2; DiceTooCheck++) {
			int AmmountDiceEqual = howManyEqual(CurrentDice, DiceTooCheck); 
			if(AmmountDiceEqual >= 4) {
				return true;
			}

		}
		return false;
	}
	
	private int howManyEqual(int[] DiceArray, int DiceCheck) {
		int AmmountDiceEqual = 0;
		for(int Dice = 0; Dice < 5; Dice++) {
			if(DiceArray[DiceCheck] == DiceArray[Dice]) {
				AmmountDiceEqual++;
			}
		}
		return AmmountDiceEqual;
	}
	
	/* Private instance variables */
	private int nPlayers;
	private String[] playerNames;
	private YahtzeeDisplay display;
}
