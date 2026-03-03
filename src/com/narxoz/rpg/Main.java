package com.narxoz.rpg;

import com.narxoz.rpg.adapter.EnemyCombatantAdapter;
import com.narxoz.rpg.adapter.HeroCombatantAdapter;
import com.narxoz.rpg.battle.BattleEngine;
import com.narxoz.rpg.battle.Combatant;
import com.narxoz.rpg.battle.EncounterResult;
import com.narxoz.rpg.enemy.BasicEnemy;
import com.narxoz.rpg.enemy.Goblin;
import com.narxoz.rpg.hero.Mage;
import com.narxoz.rpg.hero.Warrior;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== RPG Battle Engine Demo ===\n");

        // Create heroes and enemies (different APIs)
        Warrior warrior = new Warrior("Arthas");
        Mage mage = new Mage("Jaina");
        Goblin goblin1 = new Goblin();
        Goblin goblin2 = new Goblin();
        BasicEnemy orc = new BasicEnemy("Orc", 18, 100);

        // Wrap them with adapters so BattleEngine can use a single interface (Combatant)
        List<Combatant> heroes = new ArrayList<>();
        heroes.add(new HeroCombatantAdapter(warrior));
        heroes.add(new HeroCombatantAdapter(mage));

        List<Combatant> enemies = new ArrayList<>();
        enemies.add(new EnemyCombatantAdapter(goblin1));
        enemies.add(new EnemyCombatantAdapter(goblin2));
        enemies.add(new EnemyCombatantAdapter(orc));

        // Demonstrate Singleton behavior
        BattleEngine engineA = BattleEngine.getInstance();
        BattleEngine engineB = BattleEngine.getInstance();
        System.out.println("Same instance? " + (engineA == engineB)
                + " (engineA hash=" + System.identityHashCode(engineA)
                + ", engineB hash=" + System.identityHashCode(engineB) + ")");
        System.out.println();

        // Run battle and print summary
        engineA.setRandomSeed(42L);
        EncounterResult result = engineA.runEncounter(heroes, enemies);

        System.out.println("Winner: " + result.getWinner());
        System.out.println("Rounds: " + result.getRounds());
        for (String line : result.getBattleLog()) {
            System.out.println(line);
        }

        System.out.println("\n=== Demo Complete ===");
    }
}