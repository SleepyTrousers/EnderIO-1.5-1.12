package crazypants.enderio.machines.machine.obelisk.attractor.handlers;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.machines.machine.obelisk.attractor.TileAttractor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AIAttractionHandler implements IMobAttractionHandler {

  @Override
  public boolean canAttract(TileAttractor attractor, EntityLiving entity) {
    return !entity.isAIDisabled() && findAITask(null, entity) == null;
  }

  @Override
  public void startAttracting(TileAttractor attractor, EntityLiving entity) {
    EntityAIBase remove = findAITask(attractor, entity);
    if (remove != null) {
      entity.tasks.removeTask(remove);
    }
    cancelCurrentTasks(entity);
    entity.tasks.addTask(0, new AttractTask(entity, attractor.getTarget(), attractor.getPos()));
  }

  private EntityAIBase findAITask(TileAttractor attractor, EntityLiving entity) {
    for (EntityAITaskEntry entry : entity.tasks.taskEntries) {
      if (entry.action instanceof AttractTask) {
        AttractTask at = (AttractTask) entry.action;
        if (attractor == null || at.coord.equals(BlockCoord.get(attractor)) || !at.continueExecuting()) {
          return entry.action;
        } else {
          return null;
        }
      }
    }
    return null;
  }

  private void cancelCurrentTasks(EntityLiving ent) {
    Iterator<EntityAITaskEntry> iterator = ent.tasks.taskEntries.iterator();

    List<EntityAITasks.EntityAITaskEntry> currentTasks = new ArrayList<EntityAITasks.EntityAITaskEntry>();
    while (iterator.hasNext()) {
      EntityAITaskEntry entityaitaskentry = iterator.next();
      if (entityaitaskentry != null) {
        currentTasks.add(entityaitaskentry);
      }
    }
    // Only available way to stop current execution is to remove all current
    // tasks, then re-add them
    for (EntityAITaskEntry task : currentTasks) {
      ent.tasks.removeTask(task.action);
      ent.tasks.addTask(task.priority, task.action);
    }
  }


  @Override
  public void tick(TileAttractor attractor, EntityLiving entity) {
  }

  @Override
  public void release(TileAttractor attractor, EntityLiving entity) {
    EntityAIBase remove = findAITask(attractor, entity);
    if (remove != null) {
      entity.tasks.removeTask(remove);
      cancelCurrentTasks(entity);
    }
  }

}
