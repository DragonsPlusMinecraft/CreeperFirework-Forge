package love.marblegate.creeperfirework.easteregg;

import love.marblegate.creeperfirework.misc.FireworkManufacturer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class HandmadeFirework extends Item {
    public HandmadeFirework() {
        super(new Item.Properties().rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if (!player.isFallFlying()) {
            ItemStack itemstack = player.getItemInHand(interactionHand);
            if (!level.isClientSide()) {
                generateFireworkRocket(level, itemstack, player.position(),player);
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
            }
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(interactionHand), level.isClientSide());
        } else {
            // TODO Elytra Boost
            return InteractionResultHolder.pass(player.getItemInHand(interactionHand));
        }
    }

    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        if (!level.isClientSide()) {
            var itemstack = useOnContext.getItemInHand();
            var player = useOnContext.getPlayer();
            generateFireworkRocket(level, itemstack, useOnContext.getClickLocation() ,player);
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private void generateFireworkRocket(Level level, ItemStack itemstack, Vec3 vec3, Player player) {
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(level, player, vec3.x, vec3.y, vec3.z, FireworkManufacturer.generateHandmade(itemstack));
                fireworkrocketentity.setPos(randomValidPos(level, vec3));
                level.addFreshEntity(fireworkrocketentity);
            }
        }
    }
    private Vec3 randomValidPos(Level level, Vec3 vec3){
        Random random = new Random();
        var offsetX = 80 * random.nextGaussian(0,0.5);
        var offsetZ = 80 * random.nextGaussian(0,0.5);
        vec3 = vec3.add(offsetX,0,offsetZ);
        while(!level.isEmptyBlock(new BlockPos(vec3))){
            vec3 = vec3.add(0,5,0);
        }
        return vec3;
    }
}
