package com.narxoz.rpg.battle;

import java.util.List;
import java.util.Random;

public final class BattleEngine {
    private static BattleEngine instance;
    private static final long DEFAULT_SEED = 1L;
    private static final int MAX_ROUNDS = 10_000;

    private Random random = new Random(DEFAULT_SEED);

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
        // Resets any engine state back to defaults.
        this.random = new Random(DEFAULT_SEED);
    }

    public EncounterResult runEncounter(List<Combatant> teamA, List<Combatant> teamB) {
        EncounterResult result = new EncounterResult();

        if (teamA == null || teamB == null) {
            result.setWinner("No contest");
            result.setRounds(0);
            result.addLog("Encounter failed: one of the teams is null.");
            return result;
        }

        // Work with copies so the caller's lists are not mutated.
        List<Combatant> a = new java.util.ArrayList<>();
        for (Combatant c : teamA) {
            if (c != null && c.isAlive()) {
                a.add(c);
            }
        }

        List<Combatant> b = new java.util.ArrayList<>();
        for (Combatant c : teamB) {
            if (c != null && c.isAlive()) {
                b.add(c);
            }
        }

        result.addLog("Encounter starts: Team A (" + a.size() + ") vs Team B (" + b.size() + ")");

        // Handle empty team edge cases.
        if (a.isEmpty() && b.isEmpty()) {
            result.setWinner("Draw");
            result.setRounds(0);
            result.addLog("Both teams are empty or already defeated.");
            return result;
        }
        if (a.isEmpty()) {
            result.setWinner("Team B");
            result.setRounds(0);
            result.addLog("Team A has no living combatants.");
            return result;
        }
        if (b.isEmpty()) {
            result.setWinner("Team A");
            result.setRounds(0);
            result.addLog("Team B has no living combatants.");
            return result;
        }

        int rounds = 0;
        while (!a.isEmpty() && !b.isEmpty() && rounds < MAX_ROUNDS) {
            rounds++;
            result.addLog("\n--- Round " + rounds + " ---");

            doAttackPhase(a, b, "A", "B", result);
            if (b.isEmpty()) {
                break;
            }

            doAttackPhase(b, a, "B", "A", result);
        }

        result.setRounds(rounds);
        if (rounds >= MAX_ROUNDS) {
            result.setWinner("Draw");
            result.addLog("Battle stopped: reached max rounds (" + MAX_ROUNDS + ").");
            return result;
        }

        String winner = a.isEmpty() ? "Team B" : "Team A";
        result.setWinner(winner);
        result.addLog("\nBattle ends. Winner: " + winner);
        return result;
    }

    private void doAttackPhase(
            List<Combatant> attackers,
            List<Combatant> defenders,
            String attackersLabel,
            String defendersLabel,
            EncounterResult result
    ) {
        // Attackers act in list order.
        int attackIndex = 0;
        for (Combatant attacker : attackers) {
            if (defenders.isEmpty()) {
                return;
            }
            if (attacker == null || !attacker.isAlive()) {
                continue;
            }

            // Target selection: in-order (stable and easy to reason about).
            int targetIndex = attackIndex % defenders.size();
            Combatant target = defenders.get(targetIndex);

            int damage = computeDamage(attacker.getAttackPower());
            result.addLog("Team " + attackersLabel + ": " + attacker.getName() + " hits " + target.getName()
                    + " for " + damage + " dmg");

            target.takeDamage(damage);
            if (!target.isAlive()) {
                defenders.remove(targetIndex);
                result.addLog("Team " + defendersLabel + ": " + target.getName() + " is defeated!");
            }

            attackIndex++;
        }
    }

    private int computeDamage(int basePower) {
        // Small variation + occasional critical hit (2x).
        int base = Math.max(1, basePower);

        // +/- up to 20% variation
        int swing = Math.max(1, (int) Math.round(base * 0.2));
        int delta = random.nextInt(swing * 2 + 1) - swing;
        int damage = Math.max(1, base + delta);

        boolean crit = random.nextDouble() < 0.15;
        if (crit) {
            damage *= 2;
        }
        return damage;
    }
}