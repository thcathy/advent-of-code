package com.adventofcode.year2015;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class Day22 {
	static Logger log = LoggerFactory.getLogger(Day22.class);
	static final int MAX_MANA_COST = 2000;
	
	public static void main(String... args) throws Exception {
		Day22 day22 = new Day22();
		day22.firstStar();
		day22.secondStar();
	}

	void secondStar() throws Exception {
		final int playerHP = 50;
		final int playerMana = 500;
		final int bossHP = 58;
		final int bossDamage = 9;
		
		Game game = new Game(playerHP, playerMana, bossHP, bossDamage, 0, 0, 0, 0, -1);
		int cost = nextRound(game).filter(Game::isWin).mapToInt(g -> g.totalManaCost).min().getAsInt();

		log.debug("Min cost to default boss in second star: {}", cost);
	}

	
	void firstStar() throws Exception {
		final int playerHP = 50;
		final int playerMana = 500;
		final int bossHP = 58;
		final int bossDamage = 9;
		
		Game game = new Game(playerHP, playerMana, bossHP, bossDamage, 0, 0, 0, 0, 0);
		int cost = nextRound(game).filter(Game::isWin).mapToInt(g -> g.totalManaCost).min().getAsInt();

		log.debug("Min cost to default boss in first star: {}", cost);
	}
	
	Stream<Game> nextRound(Game turn) {
		Game t = turn.adjustPlayerHP();
		if (t.isWin()) return Stream.of(t);
		if (t.isLose() || t.totalManaCost > MAX_MANA_COST) return Stream.empty(); 
		
		return Arrays.stream(Spell.values())
				.filter(s -> s.canApply(t))
				.flatMap(s -> nextRound(t.playerTurn(s).bossTurn()));
	}
		
	static enum Spell {
		MagicMissile(53, 0, 4, 0, 0, 0, t -> true), 
		Drain(73, 2, 2, 0, 0, 0, t -> true), 
		Shield(113, 0, 0, 6, 0, 0, t -> t.shieldTimer <= 1), 
		Poison(173, 0, 0, 0, 6, 0, t -> t.poisonTimer <= 1), 
		Recharge(229, 0, 0, 0, 0, 5, t -> t.rechargeTimer <= 1);
		
		int mana;
		int plusHP;
		int damage;
		int shieldTimer;
		int poisonTimer;
		int rechargeTimer;
		
		final Function<Game, Boolean> canApply;
				
		private Spell(int mana, int plusHP, int damage, int shieldTimer, int poisonTimer, int rechargeTimer,
				Function<Game, Boolean> canApply) {
			this.mana = mana;
			this.plusHP = plusHP;
			this.damage = damage;
			this.shieldTimer = shieldTimer;
			this.poisonTimer = poisonTimer;
			this.rechargeTimer = rechargeTimer;
			this.canApply = canApply;
		}
		
		private boolean canApply(Game turn) {
			return turn.playerMana >= mana && canApply.apply(turn);
		}

		private Game apply(Game turn) {
			return new Game(turn.playerHP + plusHP,
							turn.playerMana - mana,
							turn.bossHP - damage,
							turn.bossDamage,
							turn.shieldTimer < 1 ? shieldTimer : turn.shieldTimer,
							turn.poisonTimer < 1 ? poisonTimer : turn.poisonTimer,
							turn.rechargeTimer < 1 ? rechargeTimer : turn.rechargeTimer,
							turn.totalManaCost + mana,
							turn.playerHPAdjustment
					);
		}
	}
	
	static class Game {
		final int playerHP;
		final int playerMana;
		final int bossHP;
		final int bossDamage;
		final int shieldTimer;
		final int poisonTimer;
		final int rechargeTimer;
		final int totalManaCost;
		final int playerHPAdjustment;
		
		Game(int playerHP, int playerMana, int bossHP, int bossDamage, int shieldTimer, int poisonTimer,
				int rechargeTimer, int totalManaCost, int playerHPAdjustment) {
			this.playerHP = playerHP;
			this.playerMana = playerMana;
			this.bossHP = bossHP;
			this.bossDamage = bossDamage;
			this.shieldTimer = shieldTimer;
			this.poisonTimer = poisonTimer;
			this.rechargeTimer = rechargeTimer;
			this.totalManaCost = totalManaCost;
			this.playerHPAdjustment = playerHPAdjustment;
		}
		
		public Game adjustPlayerHP() {
			return new Game(playerHP + playerHPAdjustment, playerMana, bossHP, bossDamage, shieldTimer, poisonTimer, rechargeTimer, totalManaCost, playerHPAdjustment);
		}

		Game playerTurn(Spell s) {
			int newBossHP = poisonTimer > 0 ? bossHP -3 : bossHP;
			int newPlayerMana = rechargeTimer > 0 ? playerMana + 101 : playerMana;
			return s.apply(new Game(playerHP, newPlayerMana, newBossHP, bossDamage, shieldTimer-1, poisonTimer-1, rechargeTimer-1, totalManaCost, playerHPAdjustment));
		}
		
		Game bossTurn() {
			int newBossHP = poisonTimer > 0 ? bossHP -3 : bossHP;
			int newPlayerMana = rechargeTimer > 0 ? playerMana + 101 : playerMana;
			int newPlayerHP = playerHP - bossRealDamage();
						
			return new Game(newPlayerHP, newPlayerMana, newBossHP, bossDamage, shieldTimer-1, poisonTimer-1, rechargeTimer-1, totalManaCost, playerHPAdjustment);
		}
		
		boolean isWin() {
			return bossHP <= 0;
		}
		
		boolean isLose() {
			return playerHP <= 0 || playerMana < 0;
		}
		
		private int bossRealDamage() {
			if (shieldTimer > 0) {
				return bossDamage > 7 ? bossDamage - 7 : 1;
			} else {
				return bossDamage;
			}
		}

		@Override
		public String toString() {
			return String.format(
					"Game [playerHP=%s, playerMana=%s, bossHP=%s, bossDamage=%s, shieldTimer=%s, poisonTimer=%s, rechargeTimer=%s, totalManaCost=%s]",
					playerHP, playerMana, bossHP, bossDamage, shieldTimer, poisonTimer, rechargeTimer, totalManaCost);
		}
	}
	
	@Test
	public void testGameByScenario1() {
		Game game = new Game(10, 250, 13, 8, 0, 0, 0, 0, 0);
		
		game = game.playerTurn(Spell.Poison);
		assertEquals(10, game.playerHP);
		assertEquals(77, game.playerMana);
		assertEquals(13, game.bossHP);
		assertEquals(6, game.poisonTimer);
		assertEquals(173, game.totalManaCost);
		
		game = game.bossTurn();
		
		assertEquals(2, game.playerHP);
		assertEquals(77, game.playerMana);
		assertEquals(10, game.bossHP);
		assertEquals(5, game.poisonTimer);
		
		game = game.playerTurn(Spell.MagicMissile);
				
		assertEquals(2, game.playerHP);
		assertEquals(24, game.playerMana);
		assertEquals(3, game.bossHP);
		assertEquals(4, game.poisonTimer);
		assertEquals(226, game.totalManaCost);
		
		game = game.bossTurn();
		
		assertEquals(-6, game.playerHP);
		assertEquals(24, game.playerMana);
		assertEquals(0, game.bossHP);
		assertEquals(3, game.poisonTimer);
		assertEquals(226, game.totalManaCost);
		assertEquals(true, game.isWin());
	}
	
	@Test
	public void testGameByScenario2() {
		Game turn = new Game(10, 250, 14, 8, 0, 0, 0, 0, 0);
		
		turn = turn.playerTurn(Spell.Recharge);
		assertEquals(10, turn.playerHP);
		assertEquals(21, turn.playerMana);
		assertEquals(14, turn.bossHP);
		assertEquals(5, turn.rechargeTimer);
		assertEquals(229, turn.totalManaCost);
		
		turn = turn.bossTurn();
		assertEquals(2, turn.playerHP);
		assertEquals(122, turn.playerMana);
		assertEquals(14, turn.bossHP);
		assertEquals(4, turn.rechargeTimer);
		assertEquals(229, turn.totalManaCost);
		
		turn = turn.playerTurn(Spell.Shield);
		assertEquals(2, turn.playerHP);
		assertEquals(110, turn.playerMana);
		assertEquals(14, turn.bossHP);
		assertEquals(3, turn.rechargeTimer);
		assertEquals(6, turn.shieldTimer);
		assertEquals(342, turn.totalManaCost);
		
		turn = turn.bossTurn();
		assertEquals(1, turn.playerHP);
		assertEquals(211, turn.playerMana);
		assertEquals(14, turn.bossHP);
		assertEquals(2, turn.rechargeTimer);
		assertEquals(5, turn.shieldTimer);
		assertEquals(342, turn.totalManaCost);
		
		turn = turn.playerTurn(Spell.Drain);
		assertEquals(3, turn.playerHP);
		assertEquals(239, turn.playerMana);
		assertEquals(12, turn.bossHP);
		assertEquals(1, turn.rechargeTimer);
		assertEquals(4, turn.shieldTimer);
		assertEquals(415, turn.totalManaCost);
		
		turn = turn.bossTurn();
		assertEquals(2, turn.playerHP);
		assertEquals(340, turn.playerMana);
		assertEquals(12, turn.bossHP);
		assertEquals(0, turn.rechargeTimer);
		assertEquals(3, turn.shieldTimer);
		assertEquals(415, turn.totalManaCost);
		
		turn = turn.playerTurn(Spell.Poison);
		assertEquals(2, turn.playerHP);
		assertEquals(167, turn.playerMana);
		assertEquals(12, turn.bossHP);
		assertEquals(true, turn.rechargeTimer <= 0);
		assertEquals(2, turn.shieldTimer);
		assertEquals(6, turn.poisonTimer);
		assertEquals(588, turn.totalManaCost);
		
		turn = turn.bossTurn();
		assertEquals(1, turn.playerHP);
		assertEquals(167, turn.playerMana);
		assertEquals(9, turn.bossHP);
		assertEquals(5, turn.poisonTimer);
		assertEquals(1, turn.shieldTimer);
		assertEquals(588, turn.totalManaCost);
		
		turn = turn.playerTurn(Spell.MagicMissile);
		assertEquals(1, turn.playerHP);
		assertEquals(114, turn.playerMana);
		assertEquals(2, turn.bossHP);
		assertEquals(4, turn.poisonTimer);
		assertEquals(0, turn.shieldTimer);
		assertEquals(641, turn.totalManaCost);
		assertEquals(false, turn.isWin());
		
		turn = turn.bossTurn();
		assertEquals(-7, turn.playerHP);
		assertEquals(114, turn.playerMana);
		assertEquals(-1, turn.bossHP);
		assertEquals(3, turn.poisonTimer);
		assertEquals(641, turn.totalManaCost);
		assertEquals(true, turn.isWin());
	}
}
