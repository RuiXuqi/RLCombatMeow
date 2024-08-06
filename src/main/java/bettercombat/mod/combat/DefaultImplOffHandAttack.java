package bettercombat.mod.combat;

public class DefaultImplOffHandAttack implements IOffHandAttack {

    private int offhandCooldown;

    @Override
    public int getOffhandCooldown() {
        return this.offhandCooldown;
    }

    @Override
    public void setOffhandCooldown(int amount) {
        this.offhandCooldown = amount;
    }

    @Override
    public void tick() {
        if(this.offhandCooldown > 0) this.offhandCooldown -= 1;
    }
}