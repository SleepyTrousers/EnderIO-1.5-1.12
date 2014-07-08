package crazypants.enderio.item.darksteel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.IconUtil;
import crazypants.render.RenderUtil;
import crazypants.util.BlockCoord;
import crazypants.util.Util;
import crazypants.vecmath.Camera;
import crazypants.vecmath.Matrix4d;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.ViewFrustum;

public class SoundDetector {

  public static SoundDetector instance = new SoundDetector();
  
  private List<SoundSource> soundQueue = new ArrayList<SoundDetector.SoundSource>();
  
  private List<SoundSource> sounds = new LinkedList<SoundSource>();
  
  private Minecraft mc = Minecraft.getMinecraft();
  
  boolean enabled = false;
  
  @SubscribeEvent
  public void onSound(PlaySoundAtEntityEvent evt) {
    if(enabled && evt.entity != null && evt.entity != Minecraft.getMinecraft().thePlayer) {
      synchronized (soundQueue) {        
        soundQueue.add(new SoundSource(evt.entity));  
      }     
    }    
  }
  
  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onClientTick(TickEvent.ClientTickEvent event) {
    
    if(!enabled) {
      return;
    }
    
    synchronized (soundQueue) {
      sounds.addAll(soundQueue);
      soundQueue.clear();
    }
    
    if(mc.thePlayer == null || mc.thePlayer.worldObj == null) {
      return;
    }
    
    for(SoundSource ss : sounds) {
      mc.thePlayer.worldObj.spawnEntityInWorld(new SoundEntity(mc.thePlayer.worldObj, ss.pos)); 
    }    
    sounds.clear();
    
  }

  private static class SoundSource {
    
    int expiresTicks;
    Vector3d pos;
    
    public SoundSource(Entity ent) {
      AxisAlignedBB bb = ent.boundingBox;
      if(bb != null) {        
        pos = new Vector3d(bb.minX + (bb.maxX - bb.minX)/2, bb.minY + (bb.maxY - bb.minY)/2,bb.minZ + (bb.maxZ - bb.minZ)/2);
      } else {        
        pos = new Vector3d(ent.posX, ent.posY,ent.posZ);
      }
      expiresTicks = 40;
    }
    
  }
  
  
}
