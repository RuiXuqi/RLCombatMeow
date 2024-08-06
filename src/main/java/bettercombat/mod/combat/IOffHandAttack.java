package bettercombat.mod.combat;

public interface IOffHandAttack {

    int getOffhandCooldown();

    void setOffhandCooldown(int amount);

    void tick();
}