package net.miller.bluepenguinmod.entity.custom;

import net.miller.bluepenguinmod.entity.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class BluePenguinEntity extends AnimalEntity {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState happyAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    private int happyAnimationTimeout = 0;

    // Define the data tracker parameter properly
    private static final TrackedData<Boolean> IS_SWIMMING = DataTracker.registerData(BluePenguinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public BluePenguinEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
        this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 0.0F);
        this.setPathfindingPenalty(PathNodeType.WALKABLE, 0.0F);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(Items.COD);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.BLUE_PENGUIN.create(world);
    }

    // Override the feeding method to trigger happy animation when fed cod
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isOf(Items.COD)) {
            if (this.canEat() || (this.getBreedingAge() == 0 && !this.isInLove())) {

                // Set animation on BOTH client and server
                this.happyAnimationTimeout = 60;

                // Only do game logic on server
                if (!this.getWorld().isClient) {
                    if (!player.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }
                    this.heal(2.0F);

                    if (this.isBaby()) {
                        this.growUp((int)(-this.getBreedingAge() * 0.1F), true);
                    } else if (this.getBreedingAge() == 0) {
                        this.lovePlayer(player);
                    }
                }

                return ActionResult.success(this.getWorld().isClient);
            }
        }
        return super.interactMob(player, hand);
    }

    // Helper method to check if penguin can eat
    @Override
    public boolean canEat() {
        return this.getHealth() < this.getMaxHealth() || this.isBaby() || this.getBreedingAge() == 0;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(2, new TemptGoal(this, 1.25, Ingredient.ofItems(Items.COD), false));
        this.goalSelector.add(3, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(4, new HuntCodGoal(this)); // Hunt cod in water
        this.goalSelector.add(5, new SeekWaterGoal(this)); // Seek water when on land
        this.goalSelector.add(6, new SwimAroundGoal(this)); // Swim around in water (now includes underwater)
        this.goalSelector.add(7, new GoToLandGoal(this)); // Sometimes go to land
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0)); // Fallback wandering
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(10, new LookAroundGoal(this));

    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PARROT_AMBIENT;
    }
    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_FOX_HURT;
    }
    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PARROT_DEATH;
    }
    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_DOLPHIN_SWIM;
    }

    @Override
    public SoundEvent getEatSound(ItemStack stack) {
        return SoundEvents.ENTITY_FOX_EAT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_COD_FLOP, 0.15f, 1.0f);
    }

    // New goal to hunt cod
    private static class HuntCodGoal extends Goal {
        private final BluePenguinEntity penguin;
        private LivingEntity target;
        private int attackDelay;

        public HuntCodGoal(BluePenguinEntity penguin) {
            this.penguin = penguin;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.TARGET));
        }

        @Override
        public boolean canStart() {
            if (!this.penguin.isTouchingWater()) {
                return false;
            }

            LivingEntity target = this.findNearestCod();
            if (target != null && target.isAlive()) {
                this.target = target;
                return true;
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            return this.target != null &&
                    this.target.isAlive() &&
                    this.penguin.isTouchingWater() &&
                    this.penguin.squaredDistanceTo(this.target) < 64.0; // 8 block range
        }

        @Override
        public void start() {
            this.penguin.setTarget(this.target);
            this.attackDelay = 0;
        }

        @Override
        public void stop() {
            this.penguin.setTarget(null);
            this.target = null;
        }

        @Override
        public void tick() {
            if (this.target == null) return;

            double distance = this.penguin.squaredDistanceTo(this.target);

            if (distance < 4.0) { // Close enough to attack
                if (this.attackDelay <= 0) {
                    this.penguin.tryAttack(this.target);
                    this.attackDelay = 20; // 1 second cooldown
                }
            } else {
                // Move towards the cod
                this.penguin.getNavigation().startMovingTo(this.target, 1.2);
            }

            if (this.attackDelay > 0) {
                this.attackDelay--;
            }
        }

        private LivingEntity findNearestCod() {
            return this.penguin.getWorld().getClosestEntity(
                    CodEntity.class,
                    TargetPredicate.createNonAttackable(),
                    this.penguin,
                    this.penguin.getX(),
                    this.penguin.getY(),
                    this.penguin.getZ(),
                    this.penguin.getBoundingBox().expand(8.0)
            );
        }
    }

    // Goal to seek water when on land
    private static class SeekWaterGoal extends Goal {
        private final BluePenguinEntity penguin;
        private BlockPos targetPos;

        public SeekWaterGoal(BluePenguinEntity penguin) {
            this.penguin = penguin;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return !this.penguin.isTouchingWater() &&
                    this.penguin.getRandom().nextInt(100) == 0;
        }

        @Override
        public void start() {
            this.targetPos = this.findNearestWater();
            if (this.targetPos != null) {
                this.penguin.getNavigation().startMovingTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), 1.2);
            }
        }

        @Override
        public boolean shouldContinue() {
            return this.targetPos != null &&
                    !this.penguin.isTouchingWater() &&
                    !this.penguin.getNavigation().isIdle() &&
                    this.penguin.squaredDistanceTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ()) > 4.0;
        }

        private BlockPos findNearestWater() {
            BlockPos penguinPos = this.penguin.getBlockPos();
            World world = this.penguin.getWorld();

            for (int i = 0; i < 20; i++) {
                int x = penguinPos.getX() + this.penguin.getRandom().nextInt(32) - 16;
                int z = penguinPos.getZ() + this.penguin.getRandom().nextInt(32) - 16;

                for (int y = penguinPos.getY() + 4; y >= penguinPos.getY() - 8; y--) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (world.getBlockState(pos).getFluidState().isStill() &&
                            world.getBlockState(pos.up()).isAir()) {
                        return pos;
                    }
                }
            }
            return null;
        }
    }

    // Updated goal to swim around in water including underwater
    private static class SwimAroundGoal extends Goal {
        private final BluePenguinEntity penguin;
        private BlockPos targetPos;
        private int timer;
        private boolean preferUnderwater;

        public SwimAroundGoal(BluePenguinEntity penguin) {
            this.penguin = penguin;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return this.penguin.isTouchingWater() &&
                    (this.penguin.getNavigation().isIdle() || this.penguin.getRandom().nextInt(80) == 0);
        }

        @Override
        public void start() {
            this.timer = 100 + this.penguin.getRandom().nextInt(200);
            this.preferUnderwater = this.penguin.getRandom().nextBoolean(); // 50% chance to prefer underwater
            this.targetPos = this.findSwimTarget();
            if (this.targetPos != null) {
                this.penguin.getNavigation().startMovingTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), 1.0);
            }
        }

        @Override
        public boolean shouldContinue() {
            return this.timer > 0 &&
                    this.penguin.isTouchingWater() &&
                    this.targetPos != null;
        }

        @Override
        public void tick() {
            this.timer--;

            // If reached target or navigation failed, find new target
            if (this.penguin.getNavigation().isIdle() ||
                    (this.targetPos != null && this.penguin.squaredDistanceTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ()) < 4.0)) {
                this.targetPos = this.findSwimTarget();
                if (this.targetPos != null) {
                    this.penguin.getNavigation().startMovingTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), 1.0);
                }
            }
        }

        private BlockPos findSwimTarget() {
            BlockPos penguinPos = this.penguin.getBlockPos();
            World world = this.penguin.getWorld();

            for (int i = 0; i < 20; i++) {
                int x = penguinPos.getX() + this.penguin.getRandom().nextInt(16) - 8;
                int z = penguinPos.getZ() + this.penguin.getRandom().nextInt(16) - 8;

                // Enhanced Y calculation for better underwater swimming
                int y;
                if (this.preferUnderwater) {
                    // Prefer deeper water
                    y = penguinPos.getY() - this.penguin.getRandom().nextInt(6) - 1;
                } else {
                    // Allow both surface and underwater
                    y = penguinPos.getY() + this.penguin.getRandom().nextInt(8) - 4;
                }

                BlockPos pos = new BlockPos(x, y, z);
                if (world.getBlockState(pos).getFluidState().isIn(FluidTags.WATER)) {
                    return pos;
                }
            }
            return null;
        }
    }

    // Goal to occasionally go to land from water
    private static class GoToLandGoal extends Goal {
        private final BluePenguinEntity penguin;
        private BlockPos targetPos;

        public GoToLandGoal(BluePenguinEntity penguin) {
            this.penguin = penguin;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return this.penguin.isTouchingWater() &&
                    this.penguin.getRandom().nextInt(300) == 0; // Rare chance to go to land
        }

        @Override
        public void start() {
            this.targetPos = this.findNearestLand();
            if (this.targetPos != null) {
                this.penguin.getNavigation().startMovingTo(this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), 1.0);
            }
        }

        @Override
        public boolean shouldContinue() {
            return this.targetPos != null &&
                    this.penguin.isTouchingWater() &&
                    !this.penguin.getNavigation().isIdle();
        }

        private BlockPos findNearestLand() {
            BlockPos penguinPos = this.penguin.getBlockPos();
            World world = this.penguin.getWorld();

            for (int i = 0; i < 30; i++) {
                int x = penguinPos.getX() + this.penguin.getRandom().nextInt(20) - 10;
                int z = penguinPos.getZ() + this.penguin.getRandom().nextInt(20) - 10;

                // Find the surface level
                for (int y = world.getSeaLevel() + 10; y >= world.getBottomY(); y--) {
                    BlockPos checkPos = new BlockPos(x, y, z);

                    // Check if this is a solid block with air above it
                    if (world.getBlockState(checkPos).isSolidBlock(world, checkPos) &&
                            world.getBlockState(checkPos.up()).isAir() &&
                            !world.getBlockState(checkPos).getFluidState().isIn(FluidTags.WATER)) {
                        return checkPos.up(); // Return the air block above the solid block
                    }
                }
            }
            return null;
        }
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4); // Reduced from 10 to be more reasonable
    }

    private void setupAnimationStates() {
        // Handle happy animation first (priority)
        if (this.happyAnimationTimeout > 0) {
            System.out.println("Happy animation timeout: " + this.happyAnimationTimeout);
            --this.happyAnimationTimeout;
            this.happyAnimationState.startIfNotRunning(this.age);
            // Stop other animations while happy
            this.idleAnimationState.stop();
            return;
        } else {
            this.happyAnimationState.stop();
        }

        // Only play idle animation when not moving and not in water
        if (this.getVelocity().horizontalLengthSquared() < 0.01 && !this.isTouchingWater()) {
            if(this.idleAnimationTimeout <= 0) {
                this.idleAnimationTimeout = 100;
                this.idleAnimationState.start(this.age);
            } else {
                --this.idleAnimationTimeout;
            }
        } else {
            // Stop idle animation when moving or swimming
            this.idleAnimationState.stop();
            this.idleAnimationTimeout = 0;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if(this.getWorld().isClient()) {
            this.setupAnimationStates();
        }
        this.setSwimming(this.isTouchingWater());
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_SWIMMING, false);
    }

    public boolean isSwimming() {
        return this.dataTracker.get(IS_SWIMMING);
    }

    public void setSwimming(boolean swimming) {
        this.dataTracker.set(IS_SWIMMING, swimming);
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.canMoveVoluntarily() && this.isTouchingWater()) {
            // Improved underwater movement with proper 3D navigation
            this.updateVelocity(0.2f, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.8));

            // Force underwater movement when navigation is active
            if (!this.getNavigation().isIdle()) {
                BlockPos targetBlockPos = this.getNavigation().getTargetPos();
                if (targetBlockPos != null) {
                    Vec3d targetPos = Vec3d.ofCenter(targetBlockPos);
                    Vec3d currentPos = this.getPos();

                    // Calculate direction to target including Y component
                    Vec3d direction = targetPos.subtract(currentPos).normalize();
                    double distance = currentPos.distanceTo(targetPos);

                    if (distance > 1.0) {
                        // Apply movement in all 3 dimensions
                        Vec3d velocity = this.getVelocity();
                        double speed = 0.05;

                        this.setVelocity(
                                velocity.x + direction.x * speed,
                                velocity.y + direction.y * speed,
                                velocity.z + direction.z * speed
                        );
                    }
                }
            } else {
                // Random underwater movement when idle
                if (this.getRandom().nextInt(100) == 0) {
                    Vec3d randomVel = new Vec3d(
                            (this.getRandom().nextDouble() - 0.5) * 0.1,
                            (this.getRandom().nextDouble() - 0.5) * 0.1,
                            (this.getRandom().nextDouble() - 0.5) * 0.1
                    );
                    this.setVelocity(this.getVelocity().add(randomVel));
                }
            }
        } else {
            super.travel(movementInput);
        }
    }

    // Add navigation capabilities for water and land
    @Override
    protected EntityNavigation createNavigation(World world) {
        return new AmphibiousNavigation(this, world);
    }

    // Custom navigation class that handles both water and land
    private static class AmphibiousNavigation extends MobNavigation {
        public AmphibiousNavigation(MobEntity entity, World world) {
            super(entity, world);
        }

        @Override
        protected PathNodeNavigator createPathNodeNavigator(int range) {
            this.nodeMaker = new LandPathNodeMaker();
            this.nodeMaker.setCanEnterOpenDoors(true);
            return new PathNodeNavigator(this.nodeMaker, range);
        }

        @Override
        protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target) {
            // Allow direct pathing through water and air
            return true;
        }

        @Override
        public boolean isValidPosition(BlockPos pos) {
            // Allow both water and solid blocks as valid positions
            BlockState state = this.world.getBlockState(pos);
            return !state.isOf(Blocks.LAVA) &&
                    (state.getFluidState().isIn(FluidTags.WATER) ||
                            this.world.getBlockState(pos.down()).isSolidBlock(this.world, pos.down()));
        }
    }

    @Override
    public boolean isPushedByFluids() {
        return false; // Prevents water from pushing the penguin around
    }

    // Override air management to prevent drowning
    @Override
    public void baseTick() {
        super.baseTick();
        // Keep air at maximum when in water to prevent drowning
        if (this.isTouchingWater()) {
            this.setAir(this.getMaxAir());
        }
    }
}
