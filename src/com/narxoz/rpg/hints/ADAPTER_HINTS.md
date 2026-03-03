# Adapter Hints

## Goal
Unify incompatible Hero and Enemy APIs using a `Combatant` interface.

## Key Idea
Adapters translate method calls without changing the original classes.

## Example Mapping
- `Combatant.getName()` maps to Hero.getName() or Enemy.getTitle()
- `Combatant.getAttackPower()` maps to Hero.getPower() or Enemy.getDamage()
- `Combatant.takeDamage(int)` maps to Hero.receiveDamage(int) or Enemy.applyDamage(int)
- `Combatant.isAlive()` maps to Hero.isAlive() or Enemy.isDefeated()

## Sample Skeleton
```java
public class HeroCombatantAdapter implements Combatant {
    private final Hero hero;

    public HeroCombatantAdapter(Hero hero) {
        this.hero = hero;
    }

    @Override
    public String getName() {
        return hero.getName();
    }
}
```

## Common Mistakes
- Letting `BattleEngine` call Hero or Enemy methods directly
- Returning null from adapter methods

@startuml
interface Combatant {
+ getName(): String
+ getAttackPower(): int
+ takeDamage(amount: int): void
+ isAlive(): boolean
  }

interface Hero {
+ getName(): String
+ getPower(): int
+ receiveDamage(amount: int): void
+ isAlive(): boolean
  }

interface Enemy {
+ getTitle(): String
+ getDamage(): int
+ applyDamage(amount: int): void
+ isDefeated(): boolean
  }

class HeroCombatantAdapter {
- hero : Hero
+ HeroCombatantAdapter(hero: Hero)
  }
  HeroCombatantAdapter ..|> Combatant
  HeroCombatantAdapter --> Hero

class EnemyCombatantAdapter {
- enemy : Enemy
+ EnemyCombatantAdapter(enemy: Enemy)
  }
  EnemyCombatantAdapter ..|> Combatant
  EnemyCombatantAdapter --> Enemy

class BattleEngine
BattleEngine ..> Combatant : uses

class Warrior
class Mage
Warrior ..|> Hero
Mage ..|> Hero

class BasicEnemy
class Goblin
BasicEnemy ..|> Enemy
Goblin --|> BasicEnemy
@enduml