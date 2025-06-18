package net.alexnaomi.GangMod.entity.goal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FollowPlayerGoal extends Goal {
    private final Mob mob;
    private Player targetPlayer;
    private final double speed;
    private final float maxDistance;

    public FollowPlayerGoal(Mob mob, double speed, float maxDistance) {
        this.mob = mob;
        this.speed = speed;
        this.maxDistance = maxDistance;
    }

    @Override
    public boolean canUse() {
        Level level = mob.level();
        targetPlayer = level.getNearestPlayer(mob, maxDistance);
        return targetPlayer != null;
    }

    @Override
    public void tick() {
        if (targetPlayer != null) {
            mob.getNavigation().moveTo(targetPlayer, speed);
        }
    }
}
