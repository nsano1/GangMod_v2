package net.alexnaomi.GangMod.entity.custom;

import net.alexnaomi.GangMod.GangMod;
import net.alexnaomi.GangMod.entity.goal.FollowPlayerGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.html.HTML;

public class FishyEntity extends Animal {

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState waveAnimationState = new AnimationState();
    public final AnimationState wiggleAnimationState = new AnimationState();

    private int idleTime = 0; // Tracks how long Fishy has been idle
    private int waveCooldown = 0;
    private int wiggleCooldown = 0;
    private static final int WIGGLE_IDLE_TIME = 100; // 5 seconds of idle before wiggle
    private static final int WAVE_COOLDOWN = 60; // 3 seconds
    private static final int WIGGLE_COOLDOWN = 200; // 10 seconds


    private int idleAnimationTimeout = 0;
    private int waveAnimationTimeout = 0;
    private int wiggleAnimationTimeout = 0;


    // sleeping stuff
    private int sleepTimer = 0;
    private static final int DAYTIME_SLEEP_CHANCE = 10000; // More rare than nighttime
    private static final int NIGHTTIME_SLEEP_CHANCE = 6000;
    private boolean isDaytimeNapper = true;
    private static final int MIN_SLEEP_TIME = 200; // 10 seconds minimum
    private static final int MAX_SLEEP_TIME = 1200; // 1 minute maximum
    private boolean hasSpawnedHeart = false; // New class field




    public FishyEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this)); //control H on Goal class to see all possible goals
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));

        //TODO: ADD MORE GOALS - nana
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.0, stack -> stack.is(Items.COOKIE), false));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(4, new FollowPlayerGoal(this, 1.0, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.5)); // slower stroll
    }

    public static AttributeSupplier.Builder createAttributes(){
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 200.0)
                .add(Attributes.MOVEMENT_SPEED, 0.15D)
                .add(Attributes.OXYGEN_BONUS, 100)
                .add(Attributes.FOLLOW_RANGE, 30D);
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(Items.COOKIE);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    private void setupAnimationStates(){
        //dont animate while sleeping
        if (this.getPose() == Pose.SLEEPING) {
            idleAnimationState.stop();
            waveAnimationState.stop();
            wiggleAnimationState.stop();
            return;
        }

        // Update idle timer
        if (this.isIdle()) {
            idleTime++;
        } else {
            idleTime = 0;
            // Stop animations if moving
            if (waveAnimationState.isStarted()) waveAnimationState.stop();
            if (wiggleAnimationState.isStarted()) wiggleAnimationState.stop();
        }

        // Wave animation - priority when player is looking
        if (this.isIdle() && this.isLookingAtPlayer() && waveCooldown <= 0) {
            if (!waveAnimationState.isStarted()) {
                waveAnimationState.start(this.tickCount);
                // Set cooldowns
                waveCooldown = WAVE_COOLDOWN;
                wiggleCooldown = WIGGLE_COOLDOWN/2; // Shorter cooldown after wave
            }
        }
        // Wiggle animation - after long idle
        else if (this.isIdle() && idleTime > WIGGLE_IDLE_TIME && wiggleCooldown <= 0) {
            if (!wiggleAnimationState.isStarted() && !waveAnimationState.isStarted()) {
                wiggleAnimationState.start(this.tickCount);
                wiggleCooldown = WIGGLE_COOLDOWN;
            }
        }

        // Handle cooldowns
        if (waveCooldown > 0) waveCooldown--;
        if (wiggleCooldown > 0) wiggleCooldown--;

        // Stop conditions
        if (!this.isIdle() || !this.isLookingAtPlayer()) {
            if (waveAnimationState.isStarted()) waveAnimationState.stop();
        }
    }

    private boolean isIdle() {
        // Returns true if Fishy isn't moving (or barely moving)
        return this.getDeltaMovement().lengthSqr() < 0.0001; // Adjust threshold as needed
    }

    private boolean isLookingAtPlayer() {
        Player nearest = this.level().getNearestPlayer(this, 6.0);
        return nearest != null && this.hasLineOfSight(nearest);
    }

    @Override
    public Vec3 getLeashOffset() {
        // 1 pixel = 1/16 block, so about 0.0625 on Y
        return new Vec3(0.0, 0.0625, 0.0);
    }

    private void handleSleepBehavior() {
        // Initialize random daytime napper trait (10% chance)
        if (!this.isDaytimeNapper && this.random.nextInt(10) == 0) {
            this.isDaytimeNapper = true;
        }

        // Wake up if player disturbs
        if (this.getPose() == Pose.SLEEPING) {
            Player nearest = this.level().getNearestPlayer(this, 8.0);
            if (nearest != null && !nearest.isSleeping()) {
                this.wakeUp();
                spawnWakeParticles();
                return;
            }
        }

        // Join sleeping player (higher priority)
        Player sleepingPlayer = this.level().getNearestPlayer(this, 5.0);
        if (sleepingPlayer != null && sleepingPlayer.isSleeping()) {
            this.joinPlayerSleep(sleepingPlayer);
            return;
        }

        // Random sleep when idle
        if (this.isIdle() && !this.isInWater()) {
            sleepTimer++;

            // Determine sleep chance based on time and personality
            int sleepChance = calculateSleepChance();

            if (shouldStartSleeping(sleepChance)) {
                startSleeping();
            }
        }
        // Natural wake up
        else if (this.getPose() == Pose.SLEEPING && sleepTimer++ > MAX_SLEEP_TIME) {
            this.wakeUp();
            spawnWakeParticles();
        }
    }

    private int calculateSleepChance() {
        return this.isDaytimeNapper
                ? (this.level().isDay() ? DAYTIME_SLEEP_CHANCE : NIGHTTIME_SLEEP_CHANCE)
                : (this.level().isNight() ? NIGHTTIME_SLEEP_CHANCE : Integer.MAX_VALUE);
    }

    private boolean shouldStartSleeping(int sleepChance) {
        return sleepTimer > sleepChance && this.random.nextInt(sleepChance) == 0;
    }

    private void startSleeping() {
        this.setPose(Pose.SLEEPING);
        // Set random sleep duration
        sleepTimer = -this.random.nextIntBetweenInclusive(
                this.level().isDay() ? 400 : MIN_SLEEP_TIME, // 20-40 sec day / 10-60 sec night
                this.level().isDay() ? 800 : MAX_SLEEP_TIME
        );

        // Spawn initial sleep particles
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    GangMod.ZZZ_PARTICLE.get(),
                    this.getX(), this.getY() + 0.5, this.getZ(),
                    3, // 3 particles
                    0.3, 0.1, 0.3,
                    0.05
            );
        }
    }

    private void spawnWakeParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    this.getX(), this.getY() + 0.5, this.getZ(),
                    5, // 5 particles
                    0.3, 0.2, 0.3,
                    0.1
            );
        }
    }

    private void joinPlayerSleep(Player player) {
        BlockPos bedPos = player.blockPosition();
        this.getNavigation().moveTo(bedPos.getX(), bedPos.getY(), bedPos.getZ(), 1.0);

        if (this.blockPosition().closerThan(bedPos, 1.5)) {
            this.setPose(Pose.SLEEPING);
            this.setPos(bedPos.getX(), bedPos.getY(), bedPos.getZ());

            // Spawn exactly ONE heart (first time only)
            if (!hasSpawnedHeart && !this.level().isClientSide) {
                ((ServerLevel) this.level()).sendParticles(
                        ParticleTypes.HEART,
                        bedPos.getX() + 0.5,  // Center of bed
                        bedPos.getY() + 1.5,  // Height above bed
                        bedPos.getZ() + 0.5,
                        1,  // Exactly 1 particle
                        0, 0, 0,  // No spread
                        0   // No speed
                );
                hasSpawnedHeart = true;
            }
        } else {
            hasSpawnedHeart = false; // Reset if leaves bed
        }
    }

    private void wakeUp() {
        this.setPose(Pose.STANDING);
        this.sleepTimer = 0;
        hasSpawnedHeart = false; // Reset for next sleep
    }


    @Override
    public void tick(){
        super.tick();

        if (this.level().isClientSide())
        {
            this.setupAnimationStates();
        }
        else{
            //his naps
            handleSleepBehavior();
        }
    }

}
