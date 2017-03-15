package net.minecraft.client.entity;

import com.capesapi.CapesAPI;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.optifine.Config;
import net.optifine.PlayerConfigurations;
import net.optifine.Reflector;

import java.io.File;

import javax.annotation.Nullable;

// CapesAPI
import com.capesapi.CapesAPI;

public abstract class AbstractClientPlayer extends EntityPlayer {
  private NetworkPlayerInfo playerInfo;
  public float rotateElytraX;
  public float rotateElytraY;
  public float rotateElytraZ;
  private ResourceLocation locationOfCape = null;
  private String nameClear = null;
  private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");

  public AbstractClientPlayer(World worldIn, GameProfile playerProfile) {
    super(worldIn, playerProfile);
    this.nameClear = playerProfile.getName();

    if (this.nameClear != null && !this.nameClear.isEmpty()) {
      this.nameClear = StringUtils.stripControlCodes(this.nameClear);
    }

    //CapeUtils.downloadCape(this); // Optifine capes
    PlayerConfigurations.getPlayerConfiguration(this);

    CapesAPI.loadCape(playerProfile.getId());
  }

  /**
   * Returns true if the player is in spectator mode.
   */
  public boolean isSpectator() {
    NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(this.getGameProfile().getId());
    return networkplayerinfo != null && networkplayerinfo.getGameType() == GameType.SPECTATOR;
  }

  public boolean isCreative() {
    NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(this.getGameProfile().getId());
    return networkplayerinfo != null && networkplayerinfo.getGameType() == GameType.CREATIVE;
  }

  /**
   * Checks if this instance of AbstractClientPlayer has any associated player data.
   */
  public boolean hasPlayerInfo() {
    return this.getPlayerInfo() != null;
  }

  @Nullable
  protected NetworkPlayerInfo getPlayerInfo() {
    if (this.playerInfo == null) {
      this.playerInfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(this.getUniqueID());
    }

    return this.playerInfo;
  }

  /**
   * Returns true if the player has an associated skin.
   */
  public boolean hasSkin() {
    NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
    return networkplayerinfo != null && networkplayerinfo.hasLocationSkin();
  }

  /**
   * Returns true if the player instance has an associated skin.
   */
  public ResourceLocation getLocationSkin() {
    NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
    return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkplayerinfo.getLocationSkin();
  }

  @Nullable
  public ResourceLocation getLocationCape() {
    if (!Config.isShowCapes()) {
      return null;
    } else if (CapesAPI.hasCape(getGameProfile().getId())) {
      return CapesAPI.getCape(getGameProfile().getId());
    } else if (this.locationOfCape != null) {
      return this.locationOfCape;
    } else {
      NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
      return networkplayerinfo == null ? null : networkplayerinfo.getLocationCape();
    }
  }

  public boolean isPlayerInfoSet() {
    return this.getPlayerInfo() != null;
  }

  @Nullable

  /**
   * Gets the special Elytra texture for the player.
   */
  public ResourceLocation getLocationElytra() {
    NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
    return networkplayerinfo == null ? null : networkplayerinfo.getLocationElytra();
  }

  public static ThreadDownloadImageData getDownloadImageSkin(ResourceLocation resourceLocationIn, String username) {
    TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
    ITextureObject itextureobject = texturemanager.getTexture(resourceLocationIn);

    if (itextureobject == null) {
      itextureobject = new ThreadDownloadImageData((File) null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[]{
              StringUtils.stripControlCodes(username)
      }), DefaultPlayerSkin.getDefaultSkin(getOfflineUUID(username)), new ImageBufferDownload());
      texturemanager.loadTexture(resourceLocationIn, itextureobject);
    }

    return (ThreadDownloadImageData) itextureobject;
  }

  /**
   * Returns true if the username has an associated skin.
   */
  public static ResourceLocation getLocationSkin(String username) {
    return new ResourceLocation("skins/" + StringUtils.stripControlCodes(username));
  }

  public String getSkinType() {
    NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
    return networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : networkplayerinfo.getSkinType();
  }

  public float getFovModifier() {
    float f = 1.0 F;

    if (this.capabilities.isFlying) {
      f *= 1.1 F;
    }

    IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
    f = (float) ((double) f * ((iattributeinstance.getAttributeValue() / (double) this.capabilities.getWalkSpeed() + 1.0
    D) /2.0 D));

    if (this.capabilities.getWalkSpeed() == 0.0 F || Float.isNaN(f) || Float.isInfinite(f)){
      f = 1.0 F;
    }

    if (this.isHandActive() && this.getActiveItemStack().getItem() == Items.BOW) {
      int i = this.getItemInUseMaxCount();
      float f1 = (float) i / 20.0 F;

      if (f1 > 1.0 F){
        f1 = 1.0 F;
      } else{
        f1 = f1 * f1;
      }

      f *= 1.0 F - f1 * 0.15 F;
    }

    return Reflector.ForgeHooksClient_getOffsetFOV.exists() ? Reflector.callFloat(Reflector.ForgeHooksClient_getOffsetFOV, new Object[]{
            this,
            Float.valueOf(f)
    }) : f;
  }

  public String getNameClear() {
    return this.nameClear;
  }

  public ResourceLocation getLocationOfCape() {
    return this.locationOfCape;
  }

  public void setLocationOfCape(ResourceLocation p_setLocationOfCape_1_) {
    this.locationOfCape = p_setLocationOfCape_1_;
  }

  public boolean hasElytraCape() {
    ResourceLocation resourcelocation = this.getLocationCape();
    return resourcelocation == null ? false : resourcelocation != this.locationOfCape;
  }
}
