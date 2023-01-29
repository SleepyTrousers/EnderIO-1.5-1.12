package crazypants.enderio.enderface;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;

import com.enderio.core.common.Handlers.Handler;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

@Handler
public enum EnderIOController {

    INSTANCE;

    private TObjectIntMap<UUID> openedContainers = new TObjectIntHashMap<UUID>();
    private int clientWindowId;
    private boolean locked = false;

    public void addContainer(EntityPlayerMP player, Container opened) {
        openedContainers.put(player.getGameProfile().getId(), opened.windowId);
    }

    void lockAndWaitForChange(int windowId) {
        clientWindowId = windowId;
        locked = true;
    }

    void unlock() {
        locked = false;
    }

    @SubscribeEvent
    public void onContainerTick(PlayerOpenContainerEvent event) {
        Container c = event.entityPlayer.openContainer;
        if (c != null && !(c instanceof ContainerPlayer)
                && (c.windowId == clientWindowId || openedContainers.containsValue(c.windowId))) {
            event.setResult(Result.ALLOW);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.phase == Phase.END) {
            if (event.side.isServer()) {
                int windowId = openedContainers.get(event.player.getGameProfile().getId());
                if (event.player.openContainer == null || event.player.openContainer.windowId != windowId) {
                    openedContainers.remove(event.player.getGameProfile().getId());
                }
            } else {
                int windowId = event.player.openContainer.windowId;
                if (windowId != clientWindowId && locked) {
                    clientWindowId = windowId;
                    locked = false;
                    System.out.println("Unlocked and set windowId to " + clientWindowId);
                }
            }
        }
    }
}
