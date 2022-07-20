package love.marblegate.creeperfirework.mixin;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import love.marblegate.creeperfirework.easteregg.ItemRegistry;
import love.marblegate.creeperfirework.misc.Configuration;
import love.marblegate.creeperfirework.network.Networking;
import love.marblegate.creeperfirework.network.Packet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(Creeper.class)
public abstract class MixinCreeperEntity extends Monster {

    @Final
    @Shadow
    private static EntityDataAccessor<Boolean> DATA_IS_POWERED;
    @Shadow
    private int explosionRadius;

    protected MixinCreeperEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "explodeCreeper", at = @At("HEAD"), cancellable = true)
    private void injected(CallbackInfo ci) {
        if (Configuration.ACTIVE_EXPLOSION_TO_FIREWORK.get() && new Random(((Creeper) (Object) this).getUUID().getLeastSignificantBits()).nextDouble() < Configuration.ACTIVE_EXPLOSION_TURNING_PROBABILITY.get()) {
            if (!((Creeper) (Object) this).getLevel().isClientSide()) {
                sendEffectPacket(((Creeper) (Object) this).getLevel(), ((Creeper) (Object) this).blockPosition(), ((Creeper) (Object) this).getEntityData().get(DATA_IS_POWERED));
                var itemStack = new ItemStack(ItemRegistry.CREEPER_HANDMADE_FIREWORK.get(),16);
                if(new Random(((Creeper) (Object) this).getUUID().getLeastSignificantBits()).nextDouble() < 0.02){
                    ((Creeper) (Object) this).getLevel().addFreshEntity(new ItemEntity(((Creeper) (Object) this).getLevel(),
                            ((Creeper) (Object) this).getX(),((Creeper) (Object) this).getY() + 0.5,((Creeper) (Object) this).getZ(), itemStack));
                }
                if (Configuration.ACTIVE_EXPLOSION_HURT_CREATURE.get())
                    simulateExplodeHurtMob();

                if (Configuration.ACTIVE_EXPLOSION_DESTROY_BLOCK.get() && ((Creeper) (Object) this).getLevel().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING))
                    simulateExplodeDestroyBlock();
            }
            ((Creeper) (Object) this).discard();
            ci.cancel();
        }

    }


    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (Configuration.DEATH_TO_FIREWORK.get() && new Random(((Creeper) (Object) this).getUUID().getLeastSignificantBits()).nextDouble() < Configuration.DEATH_EXPLOSION_TURNING_PROBABILITY.get()) {
            if (!((Creeper) (Object) this).getLevel().isClientSide()) {
                sendEffectPacket(((Creeper) (Object) this).getLevel(), ((Creeper) (Object) this).blockPosition(), ((Creeper) (Object) this).getEntityData().get(DATA_IS_POWERED));
                if (Configuration.DEATH_EXPLOSION_HURT_CREATURE.get())
                    simulateExplodeHurtMob();
                if (Configuration.DEATH_EXPLOSION_DESTROY_BLOCK.get() && ((Creeper) (Object) this).getLevel().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING))
                    simulateExplodeDestroyBlock();
            }
        }
    }

    private float getExplosionPower() {
        return ((Creeper) (Object) this).getEntityData().get(DATA_IS_POWERED) ? explosionRadius * 2 : explosionRadius;
    }

    private void sendEffectPacket(Level level, BlockPos pos, boolean powered) {
        List<Player> players = level.players().stream().filter(serverPlayerEntity -> serverPlayerEntity.blockPosition().closerThan(pos, 192)).collect(Collectors.toList());
        for (Player player : players) {
            Networking.INSTANCE.send(
                    PacketDistributor.PLAYER.with(
                            () -> (ServerPlayer) player
                    ),
                    new Packet(pos, powered));
        }

    }

    private void simulateExplodeHurtMob() {
        Vec3 groundZero = ((Creeper) (Object) this).position();
        AABB aabb = new AABB(((Creeper) (Object) this).blockPosition()).inflate(getExplosionPower());
        List<LivingEntity> victims = ((Creeper) (Object) this).getLevel().getEntitiesOfClass(LivingEntity.class, aabb);
        for (LivingEntity victim : victims) {
            if (!victim.ignoreExplosion()) {
                float j = getExplosionPower() * 2.0F;
                double h = Math.sqrt(victim.distanceToSqr(groundZero)) / (double) j;
                if (h <= 1.0D) {
                    double s = victim.getX() - groundZero.x;
                    double t = victim.getEyeY() - groundZero.y;
                    double u = victim.getZ() - groundZero.z;
                    double blockPos = Math.sqrt(s * s + t * t + u * u);
                    if (blockPos != 0.0D) {
                        s /= blockPos;
                        t /= blockPos;
                        u /= blockPos;
                        double fluidState = Explosion.getSeenPercent(groundZero, victim);
                        double v = (1.0D - h) * fluidState;
                        victim.hurt(DamageSource.explosion((Creeper) (Object) this), (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) j + 1.0D)));
                        double w = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity) victim, v);

                        victim.setDeltaMovement(victim.getDeltaMovement().add(s * w, t * w, u * w));
                    }
                }
            }
        }
    }


    private void simulateExplodeDestroyBlock() {
        ((Creeper) (Object) this).getLevel().gameEvent(((Creeper) (Object) this), GameEvent.EXPLODE, ((Creeper) (Object) this).blockPosition());
        Set<BlockPos> explosionRange = Sets.newHashSet();
        BlockPos groundZero = ((Creeper) (Object) this).blockPosition();
        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d = (float) j / 15.0F * 2.0F - 1.0F;
                        double e = (float) k / 15.0F * 2.0F - 1.0F;
                        double f = (float) l / 15.0F * 2.0F - 1.0F;
                        double g = Math.sqrt(d * d + e * e + f * f);
                        d /= g;
                        e /= g;
                        f /= g;
                        float h = getExplosionPower() * (0.7F + ((Creeper) (Object) this).getLevel().random.nextFloat() * 0.6F);
                        double m = groundZero.getX();
                        double n = groundZero.getY();
                        double o = groundZero.getZ();
                        for (; h > 0.0F; h -= 0.22500001F) {
                            BlockPos blockPos = new BlockPos(m, n, o);
                            BlockState blockState = ((Creeper) (Object) this).getLevel().getBlockState(blockPos);
                            FluidState fluidState = ((Creeper) (Object) this).getLevel().getFluidState(blockPos);
                            if (!((Creeper) (Object) this).getLevel().isInWorldBounds(blockPos)) {
                                break;
                            }

                            Optional<Float> optional = blockState.isAir() && fluidState.isEmpty() ? Optional.empty() : Optional.of(Math.max(blockState.getBlock().getExplosionResistance(), fluidState.getExplosionResistance()));
                            if (optional.isPresent()) {
                                h -= (optional.get() + 0.3F) * 0.3F;
                            }

                            if (h > 0.0F) {
                                explosionRange.add(blockPos);
                            }

                            m += d * 0.30000001192092896D;
                            n += e * 0.30000001192092896D;
                            o += f * 0.30000001192092896D;
                        }
                    }
                }
            }
        }

        ObjectArrayList<Pair<ItemStack, BlockPos>> blockDropList = new ObjectArrayList<>();

        /// I really do not want to create an explosion instance here. But there is a method below needs it.
        Explosion simulateExplosionForParameter = new Explosion(((Creeper) (Object) this).getLevel(), null, null, null,
                ((Creeper) (Object) this).getBlockX(), ((Creeper) (Object) this).getBlockY(), ((Creeper) (Object) this).getBlockZ(), getExplosionPower(), false, Explosion.BlockInteraction.DESTROY);

        for (BlockPos affectedPos : explosionRange) {
            BlockState blockStateOfAffected = ((Creeper) (Object) this).getLevel().getBlockState(affectedPos);
            if (!blockStateOfAffected.isAir()) {
                BlockPos blockPos2 = affectedPos.immutable();
                ((Creeper) (Object) this).getLevel().getProfiler().push("explosion_blocks");

                BlockEntity blockEntity = blockStateOfAffected.hasBlockEntity() ? ((Creeper) (Object) this).getLevel().getBlockEntity(affectedPos) : null;
                LootContext.Builder builder = (new LootContext.Builder((ServerLevel) ((Creeper) (Object) this).getLevel())).withRandom(((Creeper) (Object) this).getLevel().random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(affectedPos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity).withOptionalParameter(LootContextParams.THIS_ENTITY, ((Creeper) (Object) this));
                builder.withParameter(LootContextParams.EXPLOSION_RADIUS, getExplosionPower());

                blockStateOfAffected.getDrops(builder).forEach((stack) -> {
                    ExplosionMethodInvoker.invokeAddBlockDrops(blockDropList, stack, blockPos2);
                });

                ((Creeper) (Object) this).getLevel().setBlock(affectedPos, Blocks.AIR.defaultBlockState(), 3);

                // yes here is what I'm talking. This part cannot be deleted.
                blockStateOfAffected.onBlockExploded(((Creeper) (Object) this).getLevel(), affectedPos, simulateExplosionForParameter);
                ((Creeper) (Object) this).getLevel().getProfiler().pop();
            }
        }

        for (Pair<ItemStack, BlockPos> itemStackBlockPosPair : blockDropList) {
            Block.popResource(((Creeper) (Object) this).getLevel(), itemStackBlockPosPair.getSecond(), itemStackBlockPosPair.getFirst());
        }
    }

}
