package com.narxoz.rpg.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class BattleEngine {
    private static BattleEngine instance;
    private Random random = new Random(1L);

    private BattleEngine() {
    }

    public static BattleEngine getInstance() {
        if (instance == null) {
            instance = new BattleEngine();
        }
        return instance;
    }

    public BattleEngine setRandomSeed(long seed) {
        this.random = new Random(seed);
        return this;
    }

    public void reset() {
        this.random = new Random(1L);
    }

    public EncounterResult runEncounter(List<Combatant> teamA, List<Combatant> teamB) {
        if (teamA == null || teamB == null) {
            throw new IllegalArgumentException("Teams must not be null");
        }

        List<Combatant> a = new ArrayList<>();
        for (Combatant c : teamA) {
            if (c != null) {
                a.add(c);
            }
        }

        List<Combatant> b = new ArrayList<>();
        for (Combatant c : teamB) {
            if (c != null) {
                b.add(c);
            }
        }

        a.removeIf(c -> !c.isAlive());
        b.removeIf(c -> !c.isAlive());

        EncounterResult result = new EncounterResult();

        if (a.isEmpty() && b.isEmpty()) {
            result.setWinner("Draw");
            result.setRounds(0);
            result.addLog("Both teams have no living combatants");
            return result;
        }

        if (a.isEmpty()) {
            result.setWinner("Team B");
            result.setRounds(0);
            result.addLog("Team A has no living combatants");
            return result;
        }

        if (b.isEmpty()) {
            result.setWinner("Team A");
            result.setRounds(0);
            result.addLog("Team B has no living combatants");
            return result;
        }

        int rounds = 0;

        while (!a.isEmpty() && !b.isEmpty()) {
            rounds++;

            attackInOrder(a, b, result, rounds, "A", "B");
            if (b.isEmpty()) {
                break;
            }

            attackInOrder(b, a, result, rounds, "B", "A");
        }

        result.setRounds(rounds);
        result.setWinner(a.isEmpty() ? "Team B" : "Team A");
        return result;
    }

    private void attackInOrder(List<Combatant> attackers, List<Combatant> defenders, EncounterResult result, int round, String attackerTeam, String defenderTeam) {
        int i = 0;
        while (i < attackers.size() && !defenders.isEmpty()) {
            Combatant attacker = attackers.get(i);
            if (attacker == null || !attacker.isAlive()) {
                attackers.remove(i);
                continue;
            }

            int targetIndex = i % defenders.size();
            Combatant target = defenders.get(targetIndex);

            int damage = Math.max(0, attacker.getAttackPower());
            boolean crit = random.nextInt(100) < 20;
            if (crit) {
                damage *= 2;
            }

            target.takeDamage(damage);

            String line = "Round " + round + ": " + attackerTeam + "[" + attacker.getName() + "] hits " + defenderTeam + "[" + target.getName() + "] for " + damage + (crit ? " (CRIT)" : "");
            result.addLog(line);

            if (!target.isAlive()) {
                result.addLog(defenderTeam + "[" + target.getName() + "] is defeated");
                defenders.remove(targetIndex);
            }

            i++;
        }

        attackers.removeIf(c -> c == null || !c.isAlive());
        defenders.removeIf(c -> c == null || !c.isAlive());
    }
}