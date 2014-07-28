/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {
	/**Total Scores of the players*/
	private int[] PlayerScores;
	/**Each players upper score*/
	private int[] PlayerUpperScore;
	/**EachPlayers lower score */
	private int[] PlayerLowerScore;
	
	public static void main(String[] args) {
		new Yahtzee().start(args);
	}
	
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
		NPlayers = chooseNumberOfPlayers();	
		
		/* Set up the players array by reading names for each player. */
		PlayerNames = new String[NPlayers];
		PlayerScores = new int[NPlayers];
		PlayerUpperScore = new int[NPlayers];
		PlayerLowerScore = new int[NPlayers];

		for (int i = 0; i < NPlayers; i++) {
			/* IODialog is a class that allows us to prompt the user for information as a
			 * series of dialog boxes.  We will use this here to read player names.
			 */
			PlayerNames[i] = choosePlayerName(i + 1);
		}
	}
	
	private String choosePlayerName(int Player) {
		while(true) {
			IODialog dialog = getDialog();
			String Name = dialog.readLine("Enter name for player " + Player);
			if(Name.length() > 0) {
				return Name;
			}	else {
				dialog.println("That is not a valid name");
			}
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
		Display = new YahtzeeDisplay(getGCanvas(), PlayerNames);
	}

	/**
	 * Actually plays a game of Yahtzee.  This is where you should begin writing your
	 * implementation.
	 */
	private void playGame() {
		IODialog StartGame = getDialog();
		StartGame.println("Let's Play");
		for(int Round = 0; Round < 13; Round++) {
			play1Round(PlayerNames);
		}
		giveUpperBonus();
		StartGame.print(winnerWinner());
		StartGame.println("The game has ended");
	}
	
	private void rollDiceOnce(int[] DiceValues, RandomGenerator DiceRoll) {
		for(int Die = 0; Die < N_DICE; Die++) {
			DiceValues[Die] = DiceRoll.nextInt(1, 6);
		}
	}
	
	/**
	 * Plays one round of the game.
	 * @param Players Names of the player participating.
	 * @param PlayerScores Scores of the participants.
	 */
	private void play1Round(String[] Players) {
		for(int PlayerTurn = 0; PlayerTurn < Players.length; PlayerTurn++) {
			int[] DiceValues = new int[N_DICE];
			RandomGenerator DiceRoller = RandomGenerator.getInstance();
			rollDiceOnce(DiceValues, DiceRoller);
			
			IODialog YourTurn = getDialog();
			YourTurn.println(Players[PlayerTurn] + "'s turn.");
			
			Display.waitForPlayerToClickRoll(PlayerTurn);
			Display.displayDice(DiceValues);
			int RerollTimes = 2;
			for(int i = 0; i < RerollTimes; i++) {
				Display.waitForPlayerToSelectDice();
				redrawDice(DiceValues, DiceRoller);
			}
			updateScore(PlayerTurn, DiceValues);
		}
	}
	
	private void giveUpperBonus() {
		for(int PlayerTurn = 0; PlayerTurn < PlayerNames.length; PlayerTurn++) {
			if(PlayerUpperScore[PlayerTurn] >= 63) {
				Display.updateScorecard(7, PlayerTurn, 35);
				PlayerScores[PlayerTurn] += 35;
			}
		}
	}
	
	private void updateScore(int PlayerTurn, int[] DiceValues) {
		int Category = Display.waitForPlayerToSelectCategory();
		int Score = categoryScore(DiceValues, Category);
		PlayerScores[PlayerTurn] += Score;
		Display.updateScorecard(Category, PlayerTurn, Score);
		if(Category < 6) {
			PlayerUpperScore[PlayerTurn] += Score;
		}	else if(Category > 7) {
			PlayerLowerScore[PlayerTurn] += Score;
		}
		Display.updateScorecard(16, PlayerTurn, PlayerScores[PlayerTurn]);
	}
	
	/**
	 * Gets the person who wins the game.
	 * @return The name of the winner in string form.
	 */
	private String winnerWinner() {
		String WinnerName = "THERE WAS NO WINNER!!???!!??!!!??";
		int HighScore = 0;
		for(int People = 0; People < PlayerNames.length; People++) {
			if(PlayerScores[People] > HighScore) {
				HighScore = PlayerScores[People];
				WinnerName = PlayerNames[People];
			}
		}
		return WinnerName;
	}
	
	/**
	 * Redraws the dice after the user selected them and pressed roll again.
	 * @param Dice The current stage of the dice. 
	 * @param DiceRolling The RandomGenerator used to roll the dice.
	 */
	private void redrawDice(int[] Dice, RandomGenerator DiceRolling) {
		boolean[] TheSelectedDie = getSelectedDie();
		for(int Die = 0; Die < 5; Die++) {
			if(TheSelectedDie[Die]) {
				Dice[Die] = DiceRolling.nextInt(1, 6);
				Display.displayDice(Dice);
			}
		}
	}
	
	/**
	 * Gets the Selected Die.
	 * @return A boolean[] of which die are selected.
	 */
	private boolean[] getSelectedDie() {
		boolean[] SelectedDie = new boolean[5];
		for(int Die = 0; Die < 5; Die++) {
			SelectedDie[Die] = Display.isDieSelected(Die);
		}
		return SelectedDie;
	}
	
	private int UpperScoreCalculator(int Category, int[] Dice) {
		int score = 0;
		int DieNumber = Category + 1;
		for(int Die = 0; Die < N_DICE; Die++) {
			if(Dice[Die] == DieNumber) {
				score += DieNumber;
			}
		}
		return score;
	}
	
	/**
	 * Gets the score that would be displayed for the category the user chose.
	 * @author Maxwell
	 * @param Dice The current Dice.
	 * @param Category The category that is being tested.
	 * @return The score that is given for that category.
	 */
	private int categoryScore(int[] Dice, int Category) {
		int score = 0;
		if(Category < 6) {
			score = UpperScoreCalculator(Category, Dice);
			return score;
		}
		switch(Category) {
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
			default: 
				return score;
		}
	}
	
	/**
	 * Adds all of the dice. Could also add up all integers in an int[].
	 * @param CurrentDice The dice in there current form.
	 * @return A single integer that is all of the integers in the array summed up.
	 */
	private int addAllDice(int[] CurrentDice){
		int Score = 0;
		for(int DiceIndex = 0; DiceIndex < CurrentDice.length; DiceIndex++) {
			Score += CurrentDice[DiceIndex];
		}
		return Score;
	}
	
	/**
	 * Checks to see if you have Yahtzee.
	 * @param CurrentDice Dice in their current form.
	 * @return A true or false depending on if you are eligible for a Yahtzee.
	 */
	private boolean yahtzeeElegibility(int[] CurrentDice) {
		if(howManyEqual(CurrentDice, CurrentDice[0]) == 5) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks your smallStraight eligibility.
	 * @param CurrentDice Dice in their current form.
	 * @return A true or false depending on if you are eligible or not.
	 */
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
		if((ContainsThree && ContainsFour) && ((ContainsOne && ContainsTwo) || (ContainsTwo && ContainsFive) || (ContainsFive && ContainsSix))) {
			return true;
		}
		return false;
	}
	
	/**
	 * Check to see if you have a large straight.
	 * @param CurrentDice Dice in their current form.
	 * @return A true or false depending on if you are eligible or not.
	 */
	private boolean largeStraightElegibility(int[] CurrentDice) {
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
	
	/**
	 * Checks to see if you are eligible for a full house.
	 * @param CurrentDice Dice in their current form.
	 * @return A true or false depending on if you are eligible or not.
	 */
	private boolean fullHouseElegibility(int[] CurrentDice) {
		boolean ThreeEqual = false;
		boolean TwoEqual = false;
		for(int DiceTooCheck = 0; DiceTooCheck < N_DICE; DiceTooCheck++) {
			int AmmountDiceEqual = howManyEqual(CurrentDice, DiceTooCheck); 
			if(AmmountDiceEqual == 3) {
				ThreeEqual = true;
			} else if(AmmountDiceEqual == 2) {
				TwoEqual = true;
			}
			if(ThreeEqual && TwoEqual) {
				return true;
			}

		}
		return false;
	}
	
	/**
	 * Checks to see if you are eligible for a three of a kind. 
	 * @param CurrentDice Dice in their current form.
	 * @return A true or false depending on if you are eligible or not.
	 */
	private boolean threeOfAKindElegibility(int[] CurrentDice) {
		for(int DiceTooCheck = 0; DiceTooCheck < 3; DiceTooCheck++) {
			int AmmountDiceEqual = howManyEqual(CurrentDice, DiceTooCheck); 
			if(AmmountDiceEqual >= 3) {
				return true;
			}

		}
		return false;
	}
	
	/**
	 * Checks to see if you are eligible for a four of a kind.  
	 * @param CurrentDice Dice in their current form.
	 * @return A true or false depending on if you are eligible or not.
	 */
	private boolean fourOfAKindElegibility(int[] CurrentDice) {
		for(int DiceTooCheck = 0; DiceTooCheck < 2; DiceTooCheck++) {
			int AmmountDiceEqual = howManyEqual(CurrentDice, DiceTooCheck); 
			if(AmmountDiceEqual >= 4) {
				return true;
			}

		}
		return false;
	}
	
	/**
	 * Checks how many Dice are equal to the Die in the Dice array
	 * @param DiceArray Dice in their current form.
	 * @param DiceCheck Which die you want to see how many equal it.
	 * @return How many dice are equal to the Dice to be checked.
	 */
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
	private int NPlayers;
	private String[] PlayerNames;
	private YahtzeeDisplay Display;
}
