package com.enderio.base.common.block;

import com.enderio.base.config.base.BaseConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class DarkSteelLadderHandler {

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent playerTickEvent) {
        if (playerTickEvent.phase == TickEvent.Phase.START && playerTickEvent.side == LogicalSide.CLIENT && playerTickEvent.player == Minecraft.getInstance().player) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player.onClimbable() && player.level.getBlockState(player.blockPosition()).is(EIOBlocks.DARK_STEEL_LADDER.get())) {
                if (!Minecraft.getInstance().options.keyShift.isDown()) {
                    if (Minecraft.getInstance().options.keyUp.isDown()) {
                        player.move(MoverType.SELF, new Vec3(0, BaseConfig.COMMON.BLOCKS.DARK_STEEL_LADDER_BOOST.get(),0));
                    } else {
                        player.move(MoverType.SELF, new Vec3(0,-BaseConfig.COMMON.BLOCKS.DARK_STEEL_LADDER_BOOST.get(),0));
                    }
                }
            }
        }
    }
}
