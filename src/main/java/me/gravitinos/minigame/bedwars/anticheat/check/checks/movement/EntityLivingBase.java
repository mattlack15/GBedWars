//package net.minecraft.entity;
//
//import com.google.common.base.Predicate;
//import com.google.common.base.Predicates;
//import com.google.common.collect.Maps;
//import java.util.Collection;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.UUID;
//import net.minecraft.block.Block;
//import net.minecraft.block.Block.SoundType;
//import net.minecraft.block.material.Material;
//import net.minecraft.block.state.IBlockState;
//import net.minecraft.enchantment.EnchantmentHelper;
//import net.minecraft.entity.ai.attributes.AttributeModifier;
//import net.minecraft.entity.ai.attributes.BaseAttributeMap;
//import net.minecraft.entity.ai.attributes.IAttribute;
//import net.minecraft.entity.ai.attributes.IAttributeInstance;
//import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
//import net.minecraft.entity.item.EntityItem;
//import net.minecraft.entity.item.EntityXPOrb;
//import net.minecraft.entity.passive.EntityWolf;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.entity.player.EntityPlayerMP;
//import net.minecraft.entity.projectile.EntityArrow;
//import net.minecraft.init.Blocks;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemArmor;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTBase;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.nbt.NBTTagFloat;
//import net.minecraft.nbt.NBTTagList;
//import net.minecraft.nbt.NBTTagShort;
//import net.minecraft.network.play.server.S04PacketEntityEquipment;
//import net.minecraft.network.play.server.S0BPacketAnimation;
//import net.minecraft.network.play.server.S0DPacketCollectItem;
//import net.minecraft.potion.Potion;
//import net.minecraft.potion.PotionEffect;
//import net.minecraft.potion.PotionHelper;
//import net.minecraft.scoreboard.Team;
//import net.minecraft.util.AxisAlignedBB;
//import net.minecraft.util.BlockPos;
//import net.minecraft.util.CombatTracker;
//import net.minecraft.util.DamageSource;
//import net.minecraft.util.EntitySelectors;
//import net.minecraft.util.EnumParticleTypes;
//import net.minecraft.util.MathHelper;
//import net.minecraft.util.Vec3;
//import net.minecraft.world.World;
//import net.minecraft.world.WorldServer;
//
//public abstract class EntityLivingBase extends Entity {
//    private static final UUID sprintingSpeedBoostModifierUUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
//    private static final AttributeModifier sprintingSpeedBoostModifier;
//    private BaseAttributeMap attributeMap;
//    private final CombatTracker _combatTracker = new CombatTracker(this);
//    private final Map<Integer, PotionEffect> activePotionsMap = Maps.newHashMap();
//    private final ItemStack[] previousEquipment = new ItemStack[5];
//    public boolean isSwingInProgress;
//    public int swingProgressInt;
//    public int arrowHitTimer;
//    public int hurtTime;
//    public int maxHurtTime;
//    public float attackedAtYaw;
//    public int deathTime;
//    public float prevSwingProgress;
//    public float swingProgress;
//    public float prevLimbSwingAmount;
//    public float limbSwingAmount;
//    public float limbSwing;
//    public int maxHurtResistantTime = 20;
//    public float prevCameraPitch;
//    public float cameraPitch;
//    public float randomUnused2;
//    public float randomUnused1;
//    public float renderYawOffset;
//    public float prevRenderYawOffset;
//    public float rotationYawHead;
//    public float prevRotationYawHead;
//    public float jumpMovementFactor = 0.02F;
//    protected EntityPlayer attackingPlayer;
//    protected int recentlyHit;
//    protected boolean dead;
//    protected int entityAge;
//    protected float prevOnGroundSpeedFactor;
//    protected float onGroundSpeedFactor;
//    protected float movedDistance;
//    protected float prevMovedDistance;
//    protected float unused180;
//    protected int scoreValue;
//    protected float lastDamage;
//    protected boolean isJumping;
//    public float moveStrafing;
//    public float moveForward;
//    protected float randomYawVelocity;
//    protected int newPosRotationIncrements;
//    protected double newPosX;
//    protected double newPosY;
//    protected double newPosZ;
//    protected double newRotationYaw;
//    protected double newRotationPitch;
//    private boolean potionsNeedUpdate = true;
//    private EntityLivingBase entityLivingToAttack;
//    private int revengeTimer;
//    private EntityLivingBase lastAttacker;
//    private int lastAttackerTime;
//    private float landMovementFactor;
//    private int jumpTicks;
//    private float absorptionAmount;
//
//    public void onKillCommand() {
//        this.attackEntityFrom(DamageSource.outOfWorld, 3.4028235E38F);
//    }
//
//    public EntityLivingBase(World p_i1594_1_) {
//        super(p_i1594_1_);
//        this.applyEntityAttributes();
//        this.setHealth(this.getMaxHealth());
//        this.preventEntitySpawning = true;
//        this.randomUnused1 = (float)((Math.random() + 1.0D) * 0.009999999776482582D);
//        this.setPosition(this.posX, this.posY, this.posZ);
//        this.randomUnused2 = (float)Math.random() * 12398.0F;
//        this.rotationYaw = (float)(Math.random() * 3.1415927410125732D * 2.0D);
//        this.rotationYawHead = this.rotationYaw;
//        this.stepHeight = 0.6F;
//    }
//
//    protected void entityInit() {
//        this.dataWatcher.addObject(7, 0);
//        this.dataWatcher.addObject(8, (byte)0);
//        this.dataWatcher.addObject(9, (byte)0);
//        this.dataWatcher.addObject(6, 1.0F);
//    }
//
//    protected void applyEntityAttributes() {
//        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.maxHealth);
//        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.knockbackResistance);
//        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.movementSpeed);
//    }
//
//    protected void updateFallState(double p_updateFallState_1_, boolean p_updateFallState_3_, Block p_updateFallState_4_, BlockPos p_updateFallState_5_) {
//        if (!this.isInWater()) {
//            this.handleWaterMovement();
//        }
//
//        if (!this.worldObj.isRemote && this.fallDistance > 3.0F && p_updateFallState_3_) {
//            IBlockState lvt_6_1_ = this.worldObj.getBlockState(p_updateFallState_5_);
//            Block lvt_7_1_ = lvt_6_1_.getBlock();
//            float lvt_8_1_ = (float)MathHelper.ceiling_float_int(this.fallDistance - 3.0F);
//            if (lvt_7_1_.getMaterial() != Material.air) {
//                double lvt_9_1_ = (double)Math.min(0.2F + lvt_8_1_ / 15.0F, 10.0F);
//                if (lvt_9_1_ > 2.5D) {
//                    lvt_9_1_ = 2.5D;
//                }
//
//                int lvt_11_1_ = (int)(150.0D * lvt_9_1_);
//                ((WorldServer)this.worldObj).spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ, lvt_11_1_, 0.0D, 0.0D, 0.0D, 0.15000000596046448D, new int[]{Block.getStateId(lvt_6_1_)});
//            }
//        }
//
//        super.updateFallState(p_updateFallState_1_, p_updateFallState_3_, p_updateFallState_4_, p_updateFallState_5_);
//    }
//
//    public boolean canBreatheUnderwater() {
//        return false;
//    }
//
//    public void onEntityUpdate() {
//        this.prevSwingProgress = this.swingProgress;
//        super.onEntityUpdate();
//        this.worldObj.theProfiler.startSection("livingEntityBaseTick");
//        boolean lvt_1_1_ = this instanceof EntityPlayer;
//        if (this.isEntityAlive()) {
//            if (this.isEntityInsideOpaqueBlock()) {
//                this.attackEntityFrom(DamageSource.inWall, 1.0F);
//            } else if (lvt_1_1_ && !this.worldObj.getWorldBorder().contains(this.getEntityBoundingBox())) {
//                double lvt_2_1_ = this.worldObj.getWorldBorder().getClosestDistance(this) + this.worldObj.getWorldBorder().getDamageBuffer();
//                if (lvt_2_1_ < 0.0D) {
//                    this.attackEntityFrom(DamageSource.inWall, (float)Math.max(1, MathHelper.floor_double(-lvt_2_1_ * this.worldObj.getWorldBorder().getDamageAmount())));
//                }
//            }
//        }
//
//        if (this.isImmuneToFire() || this.worldObj.isRemote) {
//            this.extinguish();
//        }
//
//        boolean lvt_2_2_ = lvt_1_1_ && ((EntityPlayer)this).capabilities.disableDamage;
//        if (this.isEntityAlive()) {
//            if (this.isInsideOfMaterial(Material.water)) {
//                if (!this.canBreatheUnderwater() && !this.isPotionActive(Potion.waterBreathing.id) && !lvt_2_2_) {
//                    this.setAir(this.decreaseAirSupply(this.getAir()));
//                    if (this.getAir() == -20) {
//                        this.setAir(0);
//
//                        for(int lvt_3_1_ = 0; lvt_3_1_ < 8; ++lvt_3_1_) {
//                            float lvt_4_1_ = this.rand.nextFloat() - this.rand.nextFloat();
//                            float lvt_5_1_ = this.rand.nextFloat() - this.rand.nextFloat();
//                            float lvt_6_1_ = this.rand.nextFloat() - this.rand.nextFloat();
//                            this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX + (double)lvt_4_1_, this.posY + (double)lvt_5_1_, this.posZ + (double)lvt_6_1_, this.motionX, this.motionY, this.motionZ, new int[0]);
//                        }
//
//                        this.attackEntityFrom(DamageSource.drown, 2.0F);
//                    }
//                }
//
//                if (!this.worldObj.isRemote && this.isRiding() && this.ridingEntity instanceof EntityLivingBase) {
//                    this.mountEntity((Entity)null);
//                }
//            } else {
//                this.setAir(300);
//            }
//        }
//
//        if (this.isEntityAlive() && this.isWet()) {
//            this.extinguish();
//        }
//
//        this.prevCameraPitch = this.cameraPitch;
//        if (this.hurtTime > 0) {
//            --this.hurtTime;
//        }
//
//        if (this.hurtResistantTime > 0 && !(this instanceof EntityPlayerMP)) {
//            --this.hurtResistantTime;
//        }
//
//        if (this.getHealth() <= 0.0F) {
//            this.onDeathUpdate();
//        }
//
//        if (this.recentlyHit > 0) {
//            --this.recentlyHit;
//        } else {
//            this.attackingPlayer = null;
//        }
//
//        if (this.lastAttacker != null && !this.lastAttacker.isEntityAlive()) {
//            this.lastAttacker = null;
//        }
//
//        if (this.entityLivingToAttack != null) {
//            if (!this.entityLivingToAttack.isEntityAlive()) {
//                this.setRevengeTarget((EntityLivingBase)null);
//            } else if (this.ticksExisted - this.revengeTimer > 100) {
//                this.setRevengeTarget((EntityLivingBase)null);
//            }
//        }
//
//        this.updatePotionEffects();
//        this.prevMovedDistance = this.movedDistance;
//        this.prevRenderYawOffset = this.renderYawOffset;
//        this.prevRotationYawHead = this.rotationYawHead;
//        this.prevRotationYaw = this.rotationYaw;
//        this.prevRotationPitch = this.rotationPitch;
//        this.worldObj.theProfiler.endSection();
//    }
//
//    public boolean isChild() {
//        return false;
//    }
//
//    protected void onDeathUpdate() {
//        ++this.deathTime;
//        if (this.deathTime == 20) {
//            int lvt_1_2_;
//            if (!this.worldObj.isRemote && (this.recentlyHit > 0 || this.isPlayer()) && this.canDropLoot() && this.worldObj.getGameRules().getBoolean("doMobLoot")) {
//                lvt_1_2_ = this.getExperiencePoints(this.attackingPlayer);
//
//                while(lvt_1_2_ > 0) {
//                    int lvt_2_1_ = EntityXPOrb.getXPSplit(lvt_1_2_);
//                    lvt_1_2_ -= lvt_2_1_;
//                    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, lvt_2_1_));
//                }
//            }
//
//            this.setDead();
//
//            for(lvt_1_2_ = 0; lvt_1_2_ < 20; ++lvt_1_2_) {
//                double lvt_2_2_ = this.rand.nextGaussian() * 0.02D;
//                double lvt_4_1_ = this.rand.nextGaussian() * 0.02D;
//                double lvt_6_1_ = this.rand.nextGaussian() * 0.02D;
//                this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, lvt_2_2_, lvt_4_1_, lvt_6_1_, new int[0]);
//            }
//        }
//
//    }
//
//    protected boolean canDropLoot() {
//        return !this.isChild();
//    }
//
//    protected int decreaseAirSupply(int p_decreaseAirSupply_1_) {
//        int lvt_2_1_ = EnchantmentHelper.getRespiration(this);
//        return lvt_2_1_ > 0 && this.rand.nextInt(lvt_2_1_ + 1) > 0 ? p_decreaseAirSupply_1_ : p_decreaseAirSupply_1_ - 1;
//    }
//
//    protected int getExperiencePoints(EntityPlayer p_getExperiencePoints_1_) {
//        return 0;
//    }
//
//    protected boolean isPlayer() {
//        return false;
//    }
//
//    public Random getRNG() {
//        return this.rand;
//    }
//
//    public EntityLivingBase getAITarget() {
//        return this.entityLivingToAttack;
//    }
//
//    public int getRevengeTimer() {
//        return this.revengeTimer;
//    }
//
//    public void setRevengeTarget(EntityLivingBase p_setRevengeTarget_1_) {
//        this.entityLivingToAttack = p_setRevengeTarget_1_;
//        this.revengeTimer = this.ticksExisted;
//    }
//
//    public EntityLivingBase getLastAttacker() {
//        return this.lastAttacker;
//    }
//
//    public int getLastAttackerTime() {
//        return this.lastAttackerTime;
//    }
//
//    public void setLastAttacker(Entity p_setLastAttacker_1_) {
//        if (p_setLastAttacker_1_ instanceof EntityLivingBase) {
//            this.lastAttacker = (EntityLivingBase)p_setLastAttacker_1_;
//        } else {
//            this.lastAttacker = null;
//        }
//
//        this.lastAttackerTime = this.ticksExisted;
//    }
//
//    public int getAge() {
//        return this.entityAge;
//    }
//
//    public void writeEntityToNBT(NBTTagCompound p_writeEntityToNBT_1_) {
//        p_writeEntityToNBT_1_.setFloat("HealF", this.getHealth());
//        p_writeEntityToNBT_1_.setShort("Health", (short)((int)Math.ceil((double)this.getHealth())));
//        p_writeEntityToNBT_1_.setShort("HurtTime", (short)this.hurtTime);
//        p_writeEntityToNBT_1_.setInteger("HurtByTimestamp", this.revengeTimer);
//        p_writeEntityToNBT_1_.setShort("DeathTime", (short)this.deathTime);
//        p_writeEntityToNBT_1_.setFloat("AbsorptionAmount", this.getAbsorptionAmount());
//        ItemStack[] lvt_2_2_ = this.getInventory();
//        int lvt_3_2_ = lvt_2_2_.length;
//
//        int lvt_4_2_;
//        ItemStack lvt_5_2_;
//        for(lvt_4_2_ = 0; lvt_4_2_ < lvt_3_2_; ++lvt_4_2_) {
//            lvt_5_2_ = lvt_2_2_[lvt_4_2_];
//            if (lvt_5_2_ != null) {
//                this.attributeMap.removeAttributeModifiers(lvt_5_2_.getAttributeModifiers());
//            }
//        }
//
//        p_writeEntityToNBT_1_.setTag("Attributes", SharedMonsterAttributes.writeBaseAttributeMapToNBT(this.getAttributeMap()));
//        lvt_2_2_ = this.getInventory();
//        lvt_3_2_ = lvt_2_2_.length;
//
//        for(lvt_4_2_ = 0; lvt_4_2_ < lvt_3_2_; ++lvt_4_2_) {
//            lvt_5_2_ = lvt_2_2_[lvt_4_2_];
//            if (lvt_5_2_ != null) {
//                this.attributeMap.applyAttributeModifiers(lvt_5_2_.getAttributeModifiers());
//            }
//        }
//
//        if (!this.activePotionsMap.isEmpty()) {
//            NBTTagList lvt_2_3_ = new NBTTagList();
//            Iterator lvt_3_3_ = this.activePotionsMap.values().iterator();
//
//            while(lvt_3_3_.hasNext()) {
//                PotionEffect lvt_4_3_ = (PotionEffect)lvt_3_3_.next();
//                lvt_2_3_.appendTag(lvt_4_3_.writeCustomPotionEffectToNBT(new NBTTagCompound()));
//            }
//
//            p_writeEntityToNBT_1_.setTag("ActiveEffects", lvt_2_3_);
//        }
//
//    }
//
//    public void readEntityFromNBT(NBTTagCompound p_readEntityFromNBT_1_) {
//        this.setAbsorptionAmount(p_readEntityFromNBT_1_.getFloat("AbsorptionAmount"));
//        if (p_readEntityFromNBT_1_.hasKey("Attributes", 9) && this.worldObj != null && !this.worldObj.isRemote) {
//            SharedMonsterAttributes.setAttributeModifiers(this.getAttributeMap(), p_readEntityFromNBT_1_.getTagList("Attributes", 10));
//        }
//
//        if (p_readEntityFromNBT_1_.hasKey("ActiveEffects", 9)) {
//            NBTTagList lvt_2_1_ = p_readEntityFromNBT_1_.getTagList("ActiveEffects", 10);
//
//            for(int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_.tagCount(); ++lvt_3_1_) {
//                NBTTagCompound lvt_4_1_ = lvt_2_1_.getCompoundTagAt(lvt_3_1_);
//                PotionEffect lvt_5_1_ = PotionEffect.readCustomPotionEffectFromNBT(lvt_4_1_);
//                if (lvt_5_1_ != null) {
//                    this.activePotionsMap.put(lvt_5_1_.getPotionID(), lvt_5_1_);
//                }
//            }
//        }
//
//        if (p_readEntityFromNBT_1_.hasKey("HealF", 99)) {
//            this.setHealth(p_readEntityFromNBT_1_.getFloat("HealF"));
//        } else {
//            NBTBase lvt_2_2_ = p_readEntityFromNBT_1_.getTag("Health");
//            if (lvt_2_2_ == null) {
//                this.setHealth(this.getMaxHealth());
//            } else if (lvt_2_2_.getId() == 5) {
//                this.setHealth(((NBTTagFloat)lvt_2_2_).getFloat());
//            } else if (lvt_2_2_.getId() == 2) {
//                this.setHealth((float)((NBTTagShort)lvt_2_2_).getShort());
//            }
//        }
//
//        this.hurtTime = p_readEntityFromNBT_1_.getShort("HurtTime");
//        this.deathTime = p_readEntityFromNBT_1_.getShort("DeathTime");
//        this.revengeTimer = p_readEntityFromNBT_1_.getInteger("HurtByTimestamp");
//    }
//
//    protected void updatePotionEffects() {
//        Iterator lvt_1_1_ = this.activePotionsMap.keySet().iterator();
//
//        while(lvt_1_1_.hasNext()) {
//            Integer lvt_2_1_ = (Integer)lvt_1_1_.next();
//            PotionEffect lvt_3_1_ = (PotionEffect)this.activePotionsMap.get(lvt_2_1_);
//            if (!lvt_3_1_.onUpdate(this)) {
//                if (!this.worldObj.isRemote) {
//                    lvt_1_1_.remove();
//                    this.onFinishedPotionEffect(lvt_3_1_);
//                }
//            } else if (lvt_3_1_.getDuration() % 600 == 0) {
//                this.onChangedPotionEffect(lvt_3_1_, false);
//            }
//        }
//
//        if (this.potionsNeedUpdate) {
//            if (!this.worldObj.isRemote) {
//                this.updatePotionMetadata();
//            }
//
//            this.potionsNeedUpdate = false;
//        }
//
//        int lvt_2_2_ = this.dataWatcher.getWatchableObjectInt(7);
//        boolean lvt_3_2_ = this.dataWatcher.getWatchableObjectByte(8) > 0;
//        if (lvt_2_2_ > 0) {
//            boolean lvt_4_1_ = false;
//            if (!this.isInvisible()) {
//                lvt_4_1_ = this.rand.nextBoolean();
//            } else {
//                lvt_4_1_ = this.rand.nextInt(15) == 0;
//            }
//
//            if (lvt_3_2_) {
//                lvt_4_1_ &= this.rand.nextInt(5) == 0;
//            }
//
//            if (lvt_4_1_ && lvt_2_2_ > 0) {
//                double lvt_5_1_ = (double)(lvt_2_2_ >> 16 & 255) / 255.0D;
//                double lvt_7_1_ = (double)(lvt_2_2_ >> 8 & 255) / 255.0D;
//                double lvt_9_1_ = (double)(lvt_2_2_ >> 0 & 255) / 255.0D;
//                this.worldObj.spawnParticle(lvt_3_2_ ? EnumParticleTypes.SPELL_MOB_AMBIENT : EnumParticleTypes.SPELL_MOB, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, lvt_5_1_, lvt_7_1_, lvt_9_1_, new int[0]);
//            }
//        }
//
//    }
//
//    protected void updatePotionMetadata() {
//        if (this.activePotionsMap.isEmpty()) {
//            this.resetPotionEffectMetadata();
//            this.setInvisible(false);
//        } else {
//            int lvt_1_1_ = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
//            this.dataWatcher.updateObject(8, Byte.valueOf((byte)(PotionHelper.getAreAmbient(this.activePotionsMap.values()) ? 1 : 0)));
//            this.dataWatcher.updateObject(7, lvt_1_1_);
//            this.setInvisible(this.isPotionActive(Potion.invisibility.id));
//        }
//
//    }
//
//    protected void resetPotionEffectMetadata() {
//        this.dataWatcher.updateObject(8, (byte)0);
//        this.dataWatcher.updateObject(7, 0);
//    }
//
//    public void clearActivePotions() {
//        Iterator lvt_1_1_ = this.activePotionsMap.keySet().iterator();
//
//        while(lvt_1_1_.hasNext()) {
//            Integer lvt_2_1_ = (Integer)lvt_1_1_.next();
//            PotionEffect lvt_3_1_ = (PotionEffect)this.activePotionsMap.get(lvt_2_1_);
//            if (!this.worldObj.isRemote) {
//                lvt_1_1_.remove();
//                this.onFinishedPotionEffect(lvt_3_1_);
//            }
//        }
//
//    }
//
//    public Collection<PotionEffect> getActivePotionEffects() {
//        return this.activePotionsMap.values();
//    }
//
//    public boolean isPotionActive(int p_isPotionActive_1_) {
//        return this.activePotionsMap.containsKey(p_isPotionActive_1_);
//    }
//
//    public boolean isPotionActive(Potion p_isPotionActive_1_) {
//        return this.activePotionsMap.containsKey(p_isPotionActive_1_.id);
//    }
//
//    public PotionEffect getActivePotionEffect(Potion p_getActivePotionEffect_1_) {
//        return (PotionEffect)this.activePotionsMap.get(p_getActivePotionEffect_1_.id);
//    }
//
//    public void addPotionEffect(PotionEffect p_addPotionEffect_1_) {
//        if (this.isPotionApplicable(p_addPotionEffect_1_)) {
//            if (this.activePotionsMap.containsKey(p_addPotionEffect_1_.getPotionID())) {
//                ((PotionEffect)this.activePotionsMap.get(p_addPotionEffect_1_.getPotionID())).combine(p_addPotionEffect_1_);
//                this.onChangedPotionEffect((PotionEffect)this.activePotionsMap.get(p_addPotionEffect_1_.getPotionID()), true);
//            } else {
//                this.activePotionsMap.put(p_addPotionEffect_1_.getPotionID(), p_addPotionEffect_1_);
//                this.onNewPotionEffect(p_addPotionEffect_1_);
//            }
//
//        }
//    }
//
//    public boolean isPotionApplicable(PotionEffect p_isPotionApplicable_1_) {
//        if (this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
//            int lvt_2_1_ = p_isPotionApplicable_1_.getPotionID();
//            if (lvt_2_1_ == Potion.regeneration.id || lvt_2_1_ == Potion.poison.id) {
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    public boolean isEntityUndead() {
//        return this.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
//    }
//
//    public void removePotionEffectClient(int p_removePotionEffectClient_1_) {
//        this.activePotionsMap.remove(p_removePotionEffectClient_1_);
//    }
//
//    public void removePotionEffect(int p_removePotionEffect_1_) {
//        PotionEffect lvt_2_1_ = (PotionEffect)this.activePotionsMap.remove(p_removePotionEffect_1_);
//        if (lvt_2_1_ != null) {
//            this.onFinishedPotionEffect(lvt_2_1_);
//        }
//
//    }
//
//    protected void onNewPotionEffect(PotionEffect p_onNewPotionEffect_1_) {
//        this.potionsNeedUpdate = true;
//        if (!this.worldObj.isRemote) {
//            Potion.potionTypes[p_onNewPotionEffect_1_.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), p_onNewPotionEffect_1_.getAmplifier());
//        }
//
//    }
//
//    protected void onChangedPotionEffect(PotionEffect p_onChangedPotionEffect_1_, boolean p_onChangedPotionEffect_2_) {
//        this.potionsNeedUpdate = true;
//        if (p_onChangedPotionEffect_2_ && !this.worldObj.isRemote) {
//            Potion.potionTypes[p_onChangedPotionEffect_1_.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), p_onChangedPotionEffect_1_.getAmplifier());
//            Potion.potionTypes[p_onChangedPotionEffect_1_.getPotionID()].applyAttributesModifiersToEntity(this, this.getAttributeMap(), p_onChangedPotionEffect_1_.getAmplifier());
//        }
//
//    }
//
//    protected void onFinishedPotionEffect(PotionEffect p_onFinishedPotionEffect_1_) {
//        this.potionsNeedUpdate = true;
//        if (!this.worldObj.isRemote) {
//            Potion.potionTypes[p_onFinishedPotionEffect_1_.getPotionID()].removeAttributesModifiersFromEntity(this, this.getAttributeMap(), p_onFinishedPotionEffect_1_.getAmplifier());
//        }
//
//    }
//
//    public void heal(float p_heal_1_) {
//        float lvt_2_1_ = this.getHealth();
//        if (lvt_2_1_ > 0.0F) {
//            this.setHealth(lvt_2_1_ + p_heal_1_);
//        }
//
//    }
//
//    public final float getHealth() {
//        return this.dataWatcher.getWatchableObjectFloat(6);
//    }
//
//    public void setHealth(float p_setHealth_1_) {
//        this.dataWatcher.updateObject(6, MathHelper.clamp_float(p_setHealth_1_, 0.0F, this.getMaxHealth()));
//    }
//
//    public boolean attackEntityFrom(DamageSource p_attackEntityFrom_1_, float p_attackEntityFrom_2_) {
//        if (this.isEntityInvulnerable(p_attackEntityFrom_1_)) {
//            return false;
//        } else if (this.worldObj.isRemote) {
//            return false;
//        } else {
//            this.entityAge = 0;
//            if (this.getHealth() <= 0.0F) {
//                return false;
//            } else if (p_attackEntityFrom_1_.isFireDamage() && this.isPotionActive(Potion.fireResistance)) {
//                return false;
//            } else {
//                if ((p_attackEntityFrom_1_ == DamageSource.anvil || p_attackEntityFrom_1_ == DamageSource.fallingBlock) && this.getEquipmentInSlot(4) != null) {
//                    this.getEquipmentInSlot(4).damageItem((int)(p_attackEntityFrom_2_ * 4.0F + this.rand.nextFloat() * p_attackEntityFrom_2_ * 2.0F), this);
//                    p_attackEntityFrom_2_ *= 0.75F;
//                }
//
//                this.limbSwingAmount = 1.5F;
//                boolean lvt_3_1_ = true;
//                if ((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0F) {
//                    if (p_attackEntityFrom_2_ <= this.lastDamage) {
//                        return false;
//                    }
//
//                    this.damageEntity(p_attackEntityFrom_1_, p_attackEntityFrom_2_ - this.lastDamage);
//                    this.lastDamage = p_attackEntityFrom_2_;
//                    lvt_3_1_ = false;
//                } else {
//                    this.lastDamage = p_attackEntityFrom_2_;
//                    this.hurtResistantTime = this.maxHurtResistantTime;
//                    this.damageEntity(p_attackEntityFrom_1_, p_attackEntityFrom_2_);
//                    this.hurtTime = this.maxHurtTime = 10;
//                }
//
//                this.attackedAtYaw = 0.0F;
//                Entity lvt_4_1_ = p_attackEntityFrom_1_.getEntity();
//                if (lvt_4_1_ != null) {
//                    if (lvt_4_1_ instanceof EntityLivingBase) {
//                        this.setRevengeTarget((EntityLivingBase)lvt_4_1_);
//                    }
//
//                    if (lvt_4_1_ instanceof EntityPlayer) {
//                        this.recentlyHit = 100;
//                        this.attackingPlayer = (EntityPlayer)lvt_4_1_;
//                    } else if (lvt_4_1_ instanceof EntityWolf) {
//                        EntityWolf lvt_5_1_ = (EntityWolf)lvt_4_1_;
//                        if (lvt_5_1_.isTamed()) {
//                            this.recentlyHit = 100;
//                            this.attackingPlayer = null;
//                        }
//                    }
//                }
//
//                if (lvt_3_1_) {
//                    this.worldObj.setEntityState(this, (byte)2);
//                    if (p_attackEntityFrom_1_ != DamageSource.drown) {
//                        this.setBeenAttacked();
//                    }
//
//                    if (lvt_4_1_ != null) {
//                        double lvt_5_2_ = lvt_4_1_.posX - this.posX;
//
//                        double lvt_7_1_;
//                        for(lvt_7_1_ = lvt_4_1_.posZ - this.posZ; lvt_5_2_ * lvt_5_2_ + lvt_7_1_ * lvt_7_1_ < 1.0E-4D; lvt_7_1_ = (Math.random() - Math.random()) * 0.01D) {
//                            lvt_5_2_ = (Math.random() - Math.random()) * 0.01D;
//                        }
//
//                        this.attackedAtYaw = (float)(MathHelper.atan2(lvt_7_1_, lvt_5_2_) * 180.0D / 3.1415927410125732D - (double)this.rotationYaw);
//                        this.knockBack(lvt_4_1_, p_attackEntityFrom_2_, lvt_5_2_, lvt_7_1_);
//                    } else {
//                        this.attackedAtYaw = (float)((int)(Math.random() * 2.0D) * 180);
//                    }
//                }
//
//                String lvt_5_3_;
//                if (this.getHealth() <= 0.0F) {
//                    lvt_5_3_ = this.getDeathSound();
//                    if (lvt_3_1_ && lvt_5_3_ != null) {
//                        this.playSound(lvt_5_3_, this.getSoundVolume(), this.getSoundPitch());
//                    }
//
//                    this.onDeath(p_attackEntityFrom_1_);
//                } else {
//                    lvt_5_3_ = this.getHurtSound();
//                    if (lvt_3_1_ && lvt_5_3_ != null) {
//                        this.playSound(lvt_5_3_, this.getSoundVolume(), this.getSoundPitch());
//                    }
//                }
//
//                return true;
//            }
//        }
//    }
//
//    public void renderBrokenItemStack(ItemStack p_renderBrokenItemStack_1_) {
//        this.playSound("random.break", 0.8F, 0.8F + this.worldObj.rand.nextFloat() * 0.4F);
//
//        for(int lvt_2_1_ = 0; lvt_2_1_ < 5; ++lvt_2_1_) {
//            Vec3 lvt_3_1_ = new Vec3(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
//            lvt_3_1_ = lvt_3_1_.rotatePitch(-this.rotationPitch * 3.1415927F / 180.0F);
//            lvt_3_1_ = lvt_3_1_.rotateYaw(-this.rotationYaw * 3.1415927F / 180.0F);
//            double lvt_4_1_ = (double)(-this.rand.nextFloat()) * 0.6D - 0.3D;
//            Vec3 lvt_6_1_ = new Vec3(((double)this.rand.nextFloat() - 0.5D) * 0.3D, lvt_4_1_, 0.6D);
//            lvt_6_1_ = lvt_6_1_.rotatePitch(-this.rotationPitch * 3.1415927F / 180.0F);
//            lvt_6_1_ = lvt_6_1_.rotateYaw(-this.rotationYaw * 3.1415927F / 180.0F);
//            lvt_6_1_ = lvt_6_1_.addVector(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
//            this.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, lvt_6_1_.xCoord, lvt_6_1_.yCoord, lvt_6_1_.zCoord, lvt_3_1_.xCoord, lvt_3_1_.yCoord + 0.05D, lvt_3_1_.zCoord, new int[]{Item.getIdFromItem(p_renderBrokenItemStack_1_.getItem())});
//        }
//
//    }
//
//    public void onDeath(DamageSource p_onDeath_1_) {
//        Entity lvt_2_1_ = p_onDeath_1_.getEntity();
//        EntityLivingBase lvt_3_1_ = this.getAttackingEntity();
//        if (this.scoreValue >= 0 && lvt_3_1_ != null) {
//            lvt_3_1_.addToPlayerScore(this, this.scoreValue);
//        }
//
//        if (lvt_2_1_ != null) {
//            lvt_2_1_.onKillEntity(this);
//        }
//
//        this.dead = true;
//        this.getCombatTracker().reset();
//        if (!this.worldObj.isRemote) {
//            int lvt_4_1_ = 0;
//            if (lvt_2_1_ instanceof EntityPlayer) {
//                lvt_4_1_ = EnchantmentHelper.getLootingModifier((EntityLivingBase)lvt_2_1_);
//            }
//
//            if (this.canDropLoot() && this.worldObj.getGameRules().getBoolean("doMobLoot")) {
//                this.dropFewItems(this.recentlyHit > 0, lvt_4_1_);
//                this.dropEquipment(this.recentlyHit > 0, lvt_4_1_);
//                if (this.recentlyHit > 0 && this.rand.nextFloat() < 0.025F + (float)lvt_4_1_ * 0.01F) {
//                    this.addRandomDrop();
//                }
//            }
//        }
//
//        this.worldObj.setEntityState(this, (byte)3);
//    }
//
//    protected void dropEquipment(boolean p_dropEquipment_1_, int p_dropEquipment_2_) {
//    }
//
//    public void knockBack(Entity p_knockBack_1_, float p_knockBack_2_, double p_knockBack_3_, double p_knockBack_5_) {
//        if (this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue()) {
//            this.isAirBorne = true;
//            float lvt_7_1_ = MathHelper.sqrt_double(p_knockBack_3_ * p_knockBack_3_ + p_knockBack_5_ * p_knockBack_5_);
//            float lvt_8_1_ = 0.4F;
//            this.motionX /= 2.0D;
//            this.motionY /= 2.0D;
//            this.motionZ /= 2.0D;
//            this.motionX -= p_knockBack_3_ / (double)lvt_7_1_ * (double)lvt_8_1_;
//            this.motionY += (double)lvt_8_1_;
//            this.motionZ -= p_knockBack_5_ / (double)lvt_7_1_ * (double)lvt_8_1_;
//            if (this.motionY > 0.4000000059604645D) {
//                this.motionY = 0.4000000059604645D;
//            }
//
//        }
//    }
//
//    protected String getHurtSound() {
//        return "game.neutral.hurt";
//    }
//
//    protected String getDeathSound() {
//        return "game.neutral.die";
//    }
//
//    protected void addRandomDrop() {
//    }
//
//    protected void dropFewItems(boolean p_dropFewItems_1_, int p_dropFewItems_2_) {
//    }
//
//    public boolean isOnLadder() {
//        int lvt_1_1_ = MathHelper.floor_double(this.posX);
//        int lvt_2_1_ = MathHelper.floor_double(this.getEntityBoundingBox().minY);
//        int lvt_3_1_ = MathHelper.floor_double(this.posZ);
//        Block lvt_4_1_ = this.worldObj.getBlockState(new BlockPos(lvt_1_1_, lvt_2_1_, lvt_3_1_)).getBlock();
//        return (lvt_4_1_ == Blocks.ladder || lvt_4_1_ == Blocks.vine) && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).isSpectator());
//    }
//
//    public boolean isEntityAlive() {
//        return !this.isDead && this.getHealth() > 0.0F;
//    }
//
//    public void fall(float p_fall_1_, float p_fall_2_) {
//        super.fall(p_fall_1_, p_fall_2_);
//        PotionEffect lvt_3_1_ = this.getActivePotionEffect(Potion.jump);
//        float lvt_4_1_ = lvt_3_1_ != null ? (float)(lvt_3_1_.getAmplifier() + 1) : 0.0F;
//        int lvt_5_1_ = MathHelper.ceiling_float_int((p_fall_1_ - 3.0F - lvt_4_1_) * p_fall_2_);
//        if (lvt_5_1_ > 0) {
//            this.playSound(this.getFallSoundString(lvt_5_1_), 1.0F, 1.0F);
//            this.attackEntityFrom(DamageSource.fall, (float)lvt_5_1_);
//            int lvt_6_1_ = MathHelper.floor_double(this.posX);
//            int lvt_7_1_ = MathHelper.floor_double(this.posY - 0.20000000298023224D);
//            int lvt_8_1_ = MathHelper.floor_double(this.posZ);
//            Block lvt_9_1_ = this.worldObj.getBlockState(new BlockPos(lvt_6_1_, lvt_7_1_, lvt_8_1_)).getBlock();
//            if (lvt_9_1_.getMaterial() != Material.air) {
//                SoundType lvt_10_1_ = lvt_9_1_.stepSound;
//                this.playSound(lvt_10_1_.getStepSound(), lvt_10_1_.getVolume() * 0.5F, lvt_10_1_.getFrequency() * 0.75F);
//            }
//        }
//
//    }
//
//    protected String getFallSoundString(int p_getFallSoundString_1_) {
//        return p_getFallSoundString_1_ > 4 ? "game.neutral.hurt.fall.big" : "game.neutral.hurt.fall.small";
//    }
//
//    public void performHurtAnimation() {
//        this.hurtTime = this.maxHurtTime = 10;
//        this.attackedAtYaw = 0.0F;
//    }
//
//    public int getTotalArmorValue() {
//        int lvt_1_1_ = 0;
//        ItemStack[] lvt_2_1_ = this.getInventory();
//        int lvt_3_1_ = lvt_2_1_.length;
//
//        for(int lvt_4_1_ = 0; lvt_4_1_ < lvt_3_1_; ++lvt_4_1_) {
//            ItemStack lvt_5_1_ = lvt_2_1_[lvt_4_1_];
//            if (lvt_5_1_ != null && lvt_5_1_.getItem() instanceof ItemArmor) {
//                int lvt_6_1_ = ((ItemArmor)lvt_5_1_.getItem()).damageReduceAmount;
//                lvt_1_1_ += lvt_6_1_;
//            }
//        }
//
//        return lvt_1_1_;
//    }
//
//    protected void damageArmor(float p_damageArmor_1_) {
//    }
//
//    protected float applyArmorCalculations(DamageSource p_applyArmorCalculations_1_, float p_applyArmorCalculations_2_) {
//        if (!p_applyArmorCalculations_1_.isUnblockable()) {
//            int lvt_3_1_ = 25 - this.getTotalArmorValue();
//            float lvt_4_1_ = p_applyArmorCalculations_2_ * (float)lvt_3_1_;
//            this.damageArmor(p_applyArmorCalculations_2_);
//            p_applyArmorCalculations_2_ = lvt_4_1_ / 25.0F;
//        }
//
//        return p_applyArmorCalculations_2_;
//    }
//
//    protected float applyPotionDamageCalculations(DamageSource p_applyPotionDamageCalculations_1_, float p_applyPotionDamageCalculations_2_) {
//        if (p_applyPotionDamageCalculations_1_.isDamageAbsolute()) {
//            return p_applyPotionDamageCalculations_2_;
//        } else {
//            int lvt_3_2_;
//            int lvt_4_2_;
//            float lvt_5_2_;
//            if (this.isPotionActive(Potion.resistance) && p_applyPotionDamageCalculations_1_ != DamageSource.outOfWorld) {
//                lvt_3_2_ = (this.getActivePotionEffect(Potion.resistance).getAmplifier() + 1) * 5;
//                lvt_4_2_ = 25 - lvt_3_2_;
//                lvt_5_2_ = p_applyPotionDamageCalculations_2_ * (float)lvt_4_2_;
//                p_applyPotionDamageCalculations_2_ = lvt_5_2_ / 25.0F;
//            }
//
//            if (p_applyPotionDamageCalculations_2_ <= 0.0F) {
//                return 0.0F;
//            } else {
//                lvt_3_2_ = EnchantmentHelper.getEnchantmentModifierDamage(this.getInventory(), p_applyPotionDamageCalculations_1_);
//                if (lvt_3_2_ > 20) {
//                    lvt_3_2_ = 20;
//                }
//
//                if (lvt_3_2_ > 0 && lvt_3_2_ <= 20) {
//                    lvt_4_2_ = 25 - lvt_3_2_;
//                    lvt_5_2_ = p_applyPotionDamageCalculations_2_ * (float)lvt_4_2_;
//                    p_applyPotionDamageCalculations_2_ = lvt_5_2_ / 25.0F;
//                }
//
//                return p_applyPotionDamageCalculations_2_;
//            }
//        }
//    }
//
//    protected void damageEntity(DamageSource p_damageEntity_1_, float p_damageEntity_2_) {
//        if (!this.isEntityInvulnerable(p_damageEntity_1_)) {
//            p_damageEntity_2_ = this.applyArmorCalculations(p_damageEntity_1_, p_damageEntity_2_);
//            p_damageEntity_2_ = this.applyPotionDamageCalculations(p_damageEntity_1_, p_damageEntity_2_);
//            float lvt_3_1_ = p_damageEntity_2_;
//            p_damageEntity_2_ = Math.max(p_damageEntity_2_ - this.getAbsorptionAmount(), 0.0F);
//            this.setAbsorptionAmount(this.getAbsorptionAmount() - (lvt_3_1_ - p_damageEntity_2_));
//            if (p_damageEntity_2_ != 0.0F) {
//                float lvt_4_1_ = this.getHealth();
//                this.setHealth(lvt_4_1_ - p_damageEntity_2_);
//                this.getCombatTracker().trackDamage(p_damageEntity_1_, lvt_4_1_, p_damageEntity_2_);
//                this.setAbsorptionAmount(this.getAbsorptionAmount() - p_damageEntity_2_);
//            }
//        }
//    }
//
//    public CombatTracker getCombatTracker() {
//        return this._combatTracker;
//    }
//
//    public EntityLivingBase getAttackingEntity() {
//        if (this._combatTracker.func_94550_c() != null) {
//            return this._combatTracker.func_94550_c();
//        } else if (this.attackingPlayer != null) {
//            return this.attackingPlayer;
//        } else {
//            return this.entityLivingToAttack != null ? this.entityLivingToAttack : null;
//        }
//    }
//
//    public final float getMaxHealth() {
//        return (float)this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
//    }
//
//    public final int getArrowCountInEntity() {
//        return this.dataWatcher.getWatchableObjectByte(9);
//    }
//
//    public final void setArrowCountInEntity(int p_setArrowCountInEntity_1_) {
//        this.dataWatcher.updateObject(9, (byte)p_setArrowCountInEntity_1_);
//    }
//
//    private int getArmSwingAnimationEnd() {
//        if (this.isPotionActive(Potion.digSpeed)) {
//            return 6 - (1 + this.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1;
//        } else {
//            return this.isPotionActive(Potion.digSlowdown) ? 6 + (1 + this.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6;
//        }
//    }
//
//    public void swingItem() {
//        if (!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0) {
//            this.swingProgressInt = -1;
//            this.isSwingInProgress = true;
//            if (this.worldObj instanceof WorldServer) {
//                ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S0BPacketAnimation(this, 0));
//            }
//        }
//
//    }
//
//    public void handleStatusUpdate(byte p_handleStatusUpdate_1_) {
//        String lvt_2_2_;
//        if (p_handleStatusUpdate_1_ == 2) {
//            this.limbSwingAmount = 1.5F;
//            this.hurtResistantTime = this.maxHurtResistantTime;
//            this.hurtTime = this.maxHurtTime = 10;
//            this.attackedAtYaw = 0.0F;
//            lvt_2_2_ = this.getHurtSound();
//            if (lvt_2_2_ != null) {
//                this.playSound(this.getHurtSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
//            }
//
//            this.attackEntityFrom(DamageSource.generic, 0.0F);
//        } else if (p_handleStatusUpdate_1_ == 3) {
//            lvt_2_2_ = this.getDeathSound();
//            if (lvt_2_2_ != null) {
//                this.playSound(this.getDeathSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
//            }
//
//            this.setHealth(0.0F);
//            this.onDeath(DamageSource.generic);
//        } else {
//            super.handleStatusUpdate(p_handleStatusUpdate_1_);
//        }
//
//    }
//
//    protected void kill() {
//        this.attackEntityFrom(DamageSource.outOfWorld, 4.0F);
//    }
//
//    protected void updateArmSwingProgress() {
//        int lvt_1_1_ = this.getArmSwingAnimationEnd();
//        if (this.isSwingInProgress) {
//            ++this.swingProgressInt;
//            if (this.swingProgressInt >= lvt_1_1_) {
//                this.swingProgressInt = 0;
//                this.isSwingInProgress = false;
//            }
//        } else {
//            this.swingProgressInt = 0;
//        }
//
//        this.swingProgress = (float)this.swingProgressInt / (float)lvt_1_1_;
//    }
//
//    public IAttributeInstance getEntityAttribute(IAttribute p_getEntityAttribute_1_) {
//        return this.getAttributeMap().getAttributeInstance(p_getEntityAttribute_1_);
//    }
//
//    public BaseAttributeMap getAttributeMap() {
//        if (this.attributeMap == null) {
//            this.attributeMap = new ServersideAttributeMap();
//        }
//
//        return this.attributeMap;
//    }
//
//    public EnumCreatureAttribute getCreatureAttribute() {
//        return EnumCreatureAttribute.UNDEFINED;
//    }
//
//    public abstract ItemStack getHeldItem();
//
//    public abstract ItemStack getEquipmentInSlot(int var1);
//
//    public abstract ItemStack getCurrentArmor(int var1);
//
//    public abstract void setCurrentItemOrArmor(int var1, ItemStack var2);
//
//    public void setSprinting(boolean p_setSprinting_1_) {
//        super.setSprinting(p_setSprinting_1_);
//        IAttributeInstance lvt_2_1_ = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
//        if (lvt_2_1_.getModifier(sprintingSpeedBoostModifierUUID) != null) {
//            lvt_2_1_.removeModifier(sprintingSpeedBoostModifier);
//        }
//
//        if (p_setSprinting_1_) {
//            lvt_2_1_.applyModifier(sprintingSpeedBoostModifier);
//        }
//
//    }
//
//    public abstract ItemStack[] getInventory();
//
//    protected float getSoundVolume() {
//        return 1.0F;
//    }
//
//    protected float getSoundPitch() {
//        return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.5F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
//    }
//
//    protected boolean isMovementBlocked() {
//        return this.getHealth() <= 0.0F;
//    }
//
//    public void dismountEntity(Entity p_dismountEntity_1_) {
//        double lvt_3_1_ = p_dismountEntity_1_.posX;
//        double lvt_5_1_ = p_dismountEntity_1_.getEntityBoundingBox().minY + (double)p_dismountEntity_1_.height;
//        double lvt_7_1_ = p_dismountEntity_1_.posZ;
//        int lvt_9_1_ = 1;
//
//        for(int lvt_10_1_ = -lvt_9_1_; lvt_10_1_ <= lvt_9_1_; ++lvt_10_1_) {
//            for(int lvt_11_1_ = -lvt_9_1_; lvt_11_1_ < lvt_9_1_; ++lvt_11_1_) {
//                if (lvt_10_1_ != 0 || lvt_11_1_ != 0) {
//                    int lvt_12_1_ = (int)(this.posX + (double)lvt_10_1_);
//                    int lvt_13_1_ = (int)(this.posZ + (double)lvt_11_1_);
//                    AxisAlignedBB lvt_2_1_ = this.getEntityBoundingBox().offset((double)lvt_10_1_, 1.0D, (double)lvt_11_1_);
//                    if (this.worldObj.getCollisionBoxes(lvt_2_1_).isEmpty()) {
//                        if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(lvt_12_1_, (int)this.posY, lvt_13_1_))) {
//                            this.setPositionAndUpdate(this.posX + (double)lvt_10_1_, this.posY + 1.0D, this.posZ + (double)lvt_11_1_);
//                            return;
//                        }
//
//                        if (World.doesBlockHaveSolidTopSurface(this.worldObj, new BlockPos(lvt_12_1_, (int)this.posY - 1, lvt_13_1_)) || this.worldObj.getBlockState(new BlockPos(lvt_12_1_, (int)this.posY - 1, lvt_13_1_)).getBlock().getMaterial() == Material.water) {
//                            lvt_3_1_ = this.posX + (double)lvt_10_1_;
//                            lvt_5_1_ = this.posY + 1.0D;
//                            lvt_7_1_ = this.posZ + (double)lvt_11_1_;
//                        }
//                    }
//                }
//            }
//        }
//
//        this.setPositionAndUpdate(lvt_3_1_, lvt_5_1_, lvt_7_1_);
//    }
//
//    public boolean getAlwaysRenderNameTagForRender() {
//        return false;
//    }
//
//    protected float getJumpUpwardsMotion() {
//        return 0.42F;
//    }
//
//    protected void jump() {
//        this.motionY = (double)this.getJumpUpwardsMotion();
//        if (this.isPotionActive(Potion.jump)) {
//            this.motionY += (double)((float)(this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
//        }
//
//        if (this.isSprinting()) {
//            float lvt_1_1_ = this.rotationYaw * 0.017453292F;
//            this.motionX -= (double)(MathHelper.sin(lvt_1_1_) * 0.2F);
//            this.motionZ += (double)(MathHelper.cos(lvt_1_1_) * 0.2F);
//        }
//
//        this.isAirBorne = true;
//    }
//
//    protected void updateAITick() {
//        this.motionY += 0.03999999910593033D;
//    }
//
//    protected void handleJumpLava() {
//        this.motionY += 0.03999999910593033D;
//    }
//
//    public void moveEntityWithHeading(float p_moveEntityWithHeading_1_, float p_moveEntityWithHeading_2_) {
//        double lvt_3_1_;
//        float lvt_7_1_;
//        if (this.isServerWorld()) {
//            float lvt_5_3_;
//            float lvt_6_2_;
//            if (this.isInWater() && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).capabilities.isFlying)) {
//                lvt_3_1_ = this.posY;
//                lvt_5_3_ = 0.8F;
//                lvt_6_2_ = 0.02F;
//                lvt_7_1_ = (float)EnchantmentHelper.getDepthStriderModifier(this);
//                if (lvt_7_1_ > 3.0F) {
//                    lvt_7_1_ = 3.0F;
//                }
//
//                if (!this.onGround) {
//                    lvt_7_1_ *= 0.5F;
//                }
//
//                if (lvt_7_1_ > 0.0F) {
//                    lvt_5_3_ += (0.54600006F - lvt_5_3_) * lvt_7_1_ / 3.0F;
//                    lvt_6_2_ += (this.getAIMoveSpeed() * 1.0F - lvt_6_2_) * lvt_7_1_ / 3.0F;
//                }
//
//                this.moveFlying(p_moveEntityWithHeading_1_, p_moveEntityWithHeading_2_, lvt_6_2_);
//                this.moveEntity(this.motionX, this.motionY, this.motionZ);
//                this.motionX *= (double)lvt_5_3_;
//                this.motionY *= 0.800000011920929D;
//                this.motionZ *= (double)lvt_5_3_;
//                this.motionY -= 0.02D;
//                if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + lvt_3_1_, this.motionZ)) {
//                    this.motionY = 0.30000001192092896D;
//                }
//            } else if (this.isInLava() && (!(this instanceof EntityPlayer) || !((EntityPlayer)this).capabilities.isFlying)) {
//                lvt_3_1_ = this.posY;
//                this.moveFlying(p_moveEntityWithHeading_1_, p_moveEntityWithHeading_2_, 0.02F);
//                this.moveEntity(this.motionX, this.motionY, this.motionZ);
//                this.motionX *= 0.5D;
//                this.motionY *= 0.5D;
//                this.motionZ *= 0.5D;
//                this.motionY -= 0.02D;
//                if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579D - this.posY + lvt_3_1_, this.motionZ)) {
//                    this.motionY = 0.30000001192092896D;
//                }
//            } else {
//                float lvt_3_3_ = 0.91F;
//                if (this.onGround) {
//                    lvt_3_3_ = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
//                }
//
//                float lvt_4_1_ = 0.16277136F / (lvt_3_3_ * lvt_3_3_ * lvt_3_3_);
//                if (this.onGround) {
//                    lvt_5_3_ = this.getAIMoveSpeed() * lvt_4_1_;
//                } else {
//                    lvt_5_3_ = this.jumpMovementFactor;
//                }
//
//                this.moveFlying(p_moveEntityWithHeading_1_, p_moveEntityWithHeading_2_, lvt_5_3_);
//                lvt_3_3_ = 0.91F;
//                if (this.onGround) {
//                    lvt_3_3_ = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
//                }
//
//                if (this.isOnLadder()) {
//                    lvt_6_2_ = 0.15F;
//                    this.motionX = MathHelper.clamp_double(this.motionX, (double)(-lvt_6_2_), (double)lvt_6_2_);
//                    this.motionZ = MathHelper.clamp_double(this.motionZ, (double)(-lvt_6_2_), (double)lvt_6_2_);
//                    this.fallDistance = 0.0F;
//                    if (this.motionY < -0.15D) {
//                        this.motionY = -0.15D;
//                    }
//
//                    boolean lvt_7_2_ = this.isSneaking() && this instanceof EntityPlayer;
//                    if (lvt_7_2_ && this.motionY < 0.0D) {
//                        this.motionY = 0.0D;
//                    }
//                }
//
//                this.moveEntity(this.motionX, this.motionY, this.motionZ);
//                if (this.isCollidedHorizontally && this.isOnLadder()) {
//                    this.motionY = 0.2D;
//                }
//
//                if (!this.worldObj.isRemote || this.worldObj.isBlockLoaded(new BlockPos((int)this.posX, 0, (int)this.posZ)) && this.worldObj.getChunkFromBlockCoords(new BlockPos((int)this.posX, 0, (int)this.posZ)).isLoaded()) {
//                    this.motionY -= 0.08D;
//                } else if (this.posY > 0.0D) {
//                    this.motionY = -0.1D;
//                } else {
//                    this.motionY = 0.0D;
//                }
//
//                this.motionY *= 0.9800000190734863D;
//                this.motionX *= (double)lvt_3_3_;
//                this.motionZ *= (double)lvt_3_3_;
//            }
//        }
//
//        this.prevLimbSwingAmount = this.limbSwingAmount;
//        lvt_3_1_ = this.posX - this.prevPosX;
//        double lvt_5_4_ = this.posZ - this.prevPosZ;
//        lvt_7_1_ = MathHelper.sqrt_double(lvt_3_1_ * lvt_3_1_ + lvt_5_4_ * lvt_5_4_) * 4.0F;
//        if (lvt_7_1_ > 1.0F) {
//            lvt_7_1_ = 1.0F;
//        }
//
//        this.limbSwingAmount += (lvt_7_1_ - this.limbSwingAmount) * 0.4F;
//        this.limbSwing += this.limbSwingAmount;
//    }
//
//    public float getAIMoveSpeed() {
//        return this.landMovementFactor;
//    }
//
//    public void setAIMoveSpeed(float p_setAIMoveSpeed_1_) {
//        this.landMovementFactor = p_setAIMoveSpeed_1_;
//    }
//
//    public boolean attackEntityAsMob(Entity p_attackEntityAsMob_1_) {
//        this.setLastAttacker(p_attackEntityAsMob_1_);
//        return false;
//    }
//
//    public boolean isPlayerSleeping() {
//        return false;
//    }
//
//    public void onUpdate() {
//        super.onUpdate();
//        if (!this.worldObj.isRemote) {
//            int lvt_1_1_ = this.getArrowCountInEntity();
//            if (lvt_1_1_ > 0) {
//                if (this.arrowHitTimer <= 0) {
//                    this.arrowHitTimer = 20 * (30 - lvt_1_1_);
//                }
//
//                --this.arrowHitTimer;
//                if (this.arrowHitTimer <= 0) {
//                    this.setArrowCountInEntity(lvt_1_1_ - 1);
//                }
//            }
//
//            for(int lvt_2_1_ = 0; lvt_2_1_ < 5; ++lvt_2_1_) {
//                ItemStack lvt_3_1_ = this.previousEquipment[lvt_2_1_];
//                ItemStack lvt_4_1_ = this.getEquipmentInSlot(lvt_2_1_);
//                if (!ItemStack.areItemStacksEqual(lvt_4_1_, lvt_3_1_)) {
//                    ((WorldServer)this.worldObj).getEntityTracker().sendToAllTrackingEntity(this, new S04PacketEntityEquipment(this.getEntityId(), lvt_2_1_, lvt_4_1_));
//                    if (lvt_3_1_ != null) {
//                        this.attributeMap.removeAttributeModifiers(lvt_3_1_.getAttributeModifiers());
//                    }
//
//                    if (lvt_4_1_ != null) {
//                        this.attributeMap.applyAttributeModifiers(lvt_4_1_.getAttributeModifiers());
//                    }
//
//                    this.previousEquipment[lvt_2_1_] = lvt_4_1_ == null ? null : lvt_4_1_.copy();
//                }
//            }
//
//            if (this.ticksExisted % 20 == 0) {
//                this.getCombatTracker().reset();
//            }
//        }
//
//        this.onLivingUpdate();
//        double lvt_1_2_ = this.posX - this.prevPosX;
//        double lvt_3_2_ = this.posZ - this.prevPosZ;
//        float lvt_5_1_ = (float)(lvt_1_2_ * lvt_1_2_ + lvt_3_2_ * lvt_3_2_);
//        float lvt_6_1_ = this.renderYawOffset;
//        float lvt_7_1_ = 0.0F;
//        this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
//        float lvt_8_1_ = 0.0F;
//        if (lvt_5_1_ > 0.0025000002F) {
//            lvt_8_1_ = 1.0F;
//            lvt_7_1_ = (float)Math.sqrt((double)lvt_5_1_) * 3.0F;
//            lvt_6_1_ = (float)MathHelper.atan2(lvt_3_2_, lvt_1_2_) * 180.0F / 3.1415927F - 90.0F;
//        }
//
//        if (this.swingProgress > 0.0F) {
//            lvt_6_1_ = this.rotationYaw;
//        }
//
//        if (!this.onGround) {
//            lvt_8_1_ = 0.0F;
//        }
//
//        this.onGroundSpeedFactor += (lvt_8_1_ - this.onGroundSpeedFactor) * 0.3F;
//        this.worldObj.theProfiler.startSection("headTurn");
//        lvt_7_1_ = this.updateDistance(lvt_6_1_, lvt_7_1_);
//        this.worldObj.theProfiler.endSection();
//        this.worldObj.theProfiler.startSection("rangeChecks");
//
//        while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
//            this.prevRotationYaw -= 360.0F;
//        }
//
//        while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
//            this.prevRotationYaw += 360.0F;
//        }
//
//        while(this.renderYawOffset - this.prevRenderYawOffset < -180.0F) {
//            this.prevRenderYawOffset -= 360.0F;
//        }
//
//        while(this.renderYawOffset - this.prevRenderYawOffset >= 180.0F) {
//            this.prevRenderYawOffset += 360.0F;
//        }
//
//        while(this.rotationPitch - this.prevRotationPitch < -180.0F) {
//            this.prevRotationPitch -= 360.0F;
//        }
//
//        while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
//            this.prevRotationPitch += 360.0F;
//        }
//
//        while(this.rotationYawHead - this.prevRotationYawHead < -180.0F) {
//            this.prevRotationYawHead -= 360.0F;
//        }
//
//        while(this.rotationYawHead - this.prevRotationYawHead >= 180.0F) {
//            this.prevRotationYawHead += 360.0F;
//        }
//
//        this.worldObj.theProfiler.endSection();
//        this.movedDistance += lvt_7_1_;
//    }
//
//    protected float updateDistance(float p_updateDistance_1_, float p_updateDistance_2_) {
//        float lvt_3_1_ = MathHelper.wrapAngleTo180_float(p_updateDistance_1_ - this.renderYawOffset);
//        this.renderYawOffset += lvt_3_1_ * 0.3F;
//        float lvt_4_1_ = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.renderYawOffset);
//        boolean lvt_5_1_ = lvt_4_1_ < -90.0F || lvt_4_1_ >= 90.0F;
//        if (lvt_4_1_ < -75.0F) {
//            lvt_4_1_ = -75.0F;
//        }
//
//        if (lvt_4_1_ >= 75.0F) {
//            lvt_4_1_ = 75.0F;
//        }
//
//        this.renderYawOffset = this.rotationYaw - lvt_4_1_;
//        if (lvt_4_1_ * lvt_4_1_ > 2500.0F) {
//            this.renderYawOffset += lvt_4_1_ * 0.2F;
//        }
//
//        if (lvt_5_1_) {
//            p_updateDistance_2_ *= -1.0F;
//        }
//
//        return p_updateDistance_2_;
//    }
//
//    public void onLivingUpdate() {
//        if (this.jumpTicks > 0) {
//            --this.jumpTicks;
//        }
//
//        if (this.newPosRotationIncrements > 0) {
//            double lvt_1_1_ = this.posX + (this.newPosX - this.posX) / (double)this.newPosRotationIncrements;
//            double lvt_3_1_ = this.posY + (this.newPosY - this.posY) / (double)this.newPosRotationIncrements;
//            double lvt_5_1_ = this.posZ + (this.newPosZ - this.posZ) / (double)this.newPosRotationIncrements;
//            double lvt_7_1_ = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double)this.rotationYaw);
//            this.rotationYaw = (float)((double)this.rotationYaw + lvt_7_1_ / (double)this.newPosRotationIncrements);
//            this.rotationPitch = (float)((double)this.rotationPitch + (this.newRotationPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
//            --this.newPosRotationIncrements;
//            this.setPosition(lvt_1_1_, lvt_3_1_, lvt_5_1_);
//            this.setRotation(this.rotationYaw, this.rotationPitch);
//        } else if (!this.isServerWorld()) {
//            this.motionX *= 0.98D;
//            this.motionY *= 0.98D;
//            this.motionZ *= 0.98D;
//        }
//
//        if (Math.abs(this.motionX) < 0.005D) {
//            this.motionX = 0.0D;
//        }
//
//        if (Math.abs(this.motionY) < 0.005D) {
//            this.motionY = 0.0D;
//        }
//
//        if (Math.abs(this.motionZ) < 0.005D) {
//            this.motionZ = 0.0D;
//        }
//
//        this.worldObj.theProfiler.startSection("ai");
//        if (this.isMovementBlocked()) {
//            this.isJumping = false;
//            this.moveStrafing = 0.0F;
//            this.moveForward = 0.0F;
//            this.randomYawVelocity = 0.0F;
//        } else if (this.isServerWorld()) {
//            this.worldObj.theProfiler.startSection("newAi");
//            this.updateEntityActionState();
//            this.worldObj.theProfiler.endSection();
//        }
//
//        this.worldObj.theProfiler.endSection();
//        this.worldObj.theProfiler.startSection("jump");
//        if (this.isJumping) {
//            if (this.isInWater()) {
//                this.updateAITick();
//            } else if (this.isInLava()) {
//                this.handleJumpLava();
//            } else if (this.onGround && this.jumpTicks == 0) {
//                this.jump();
//                this.jumpTicks = 10;
//            }
//        } else {
//            this.jumpTicks = 0;
//        }
//
//        this.worldObj.theProfiler.endSection();
//        this.worldObj.theProfiler.startSection("travel");
//        this.moveStrafing *= 0.98F;
//        this.moveForward *= 0.98F;
//        this.randomYawVelocity *= 0.9F;
//        this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
//        this.worldObj.theProfiler.endSection();
//        this.worldObj.theProfiler.startSection("push");
//        if (!this.worldObj.isRemote) {
//            this.collideWithNearbyEntities();
//        }
//
//        this.worldObj.theProfiler.endSection();
//    }
//
//    protected void updateEntityActionState() {
//    }
//
//    protected void collideWithNearbyEntities() {
//        List<Entity> lvt_1_1_ = this.worldObj.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
//            public boolean apply(Entity p_apply_1_) {
//                return p_apply_1_.canBePushed();
//            }
//        }));
//        if (!lvt_1_1_.isEmpty()) {
//            for(int lvt_2_1_ = 0; lvt_2_1_ < lvt_1_1_.size(); ++lvt_2_1_) {
//                Entity lvt_3_1_ = (Entity)lvt_1_1_.get(lvt_2_1_);
//                this.collideWithEntity(lvt_3_1_);
//            }
//        }
//
//    }
//
//    protected void collideWithEntity(Entity p_collideWithEntity_1_) {
//        p_collideWithEntity_1_.applyEntityCollision(this);
//    }
//
//    public void mountEntity(Entity p_mountEntity_1_) {
//        if (this.ridingEntity != null && p_mountEntity_1_ == null) {
//            if (!this.worldObj.isRemote) {
//                this.dismountEntity(this.ridingEntity);
//            }
//
//            if (this.ridingEntity != null) {
//                this.ridingEntity.riddenByEntity = null;
//            }
//
//            this.ridingEntity = null;
//        } else {
//            super.mountEntity(p_mountEntity_1_);
//        }
//    }
//
//    public void updateRidden() {
//        super.updateRidden();
//        this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
//        this.onGroundSpeedFactor = 0.0F;
//        this.fallDistance = 0.0F;
//    }
//
//    public void setPositionAndRotation2(double p_setPositionAndRotation2_1_, double p_setPositionAndRotation2_3_, double p_setPositionAndRotation2_5_, float p_setPositionAndRotation2_7_, float p_setPositionAndRotation2_8_, int p_setPositionAndRotation2_9_, boolean p_setPositionAndRotation2_10_) {
//        this.newPosX = p_setPositionAndRotation2_1_;
//        this.newPosY = p_setPositionAndRotation2_3_;
//        this.newPosZ = p_setPositionAndRotation2_5_;
//        this.newRotationYaw = (double)p_setPositionAndRotation2_7_;
//        this.newRotationPitch = (double)p_setPositionAndRotation2_8_;
//        this.newPosRotationIncrements = p_setPositionAndRotation2_9_;
//    }
//
//    public void setJumping(boolean p_setJumping_1_) {
//        this.isJumping = p_setJumping_1_;
//    }
//
//    public void onItemPickup(Entity p_onItemPickup_1_, int p_onItemPickup_2_) {
//        if (!p_onItemPickup_1_.isDead && !this.worldObj.isRemote) {
//            EntityTracker lvt_3_1_ = ((WorldServer)this.worldObj).getEntityTracker();
//            if (p_onItemPickup_1_ instanceof EntityItem) {
//                lvt_3_1_.sendToAllTrackingEntity(p_onItemPickup_1_, new S0DPacketCollectItem(p_onItemPickup_1_.getEntityId(), this.getEntityId()));
//            }
//
//            if (p_onItemPickup_1_ instanceof EntityArrow) {
//                lvt_3_1_.sendToAllTrackingEntity(p_onItemPickup_1_, new S0DPacketCollectItem(p_onItemPickup_1_.getEntityId(), this.getEntityId()));
//            }
//
//            if (p_onItemPickup_1_ instanceof EntityXPOrb) {
//                lvt_3_1_.sendToAllTrackingEntity(p_onItemPickup_1_, new S0DPacketCollectItem(p_onItemPickup_1_.getEntityId(), this.getEntityId()));
//            }
//        }
//
//    }
//
//    public boolean canEntityBeSeen(Entity p_canEntityBeSeen_1_) {
//        return this.worldObj.rayTraceBlocks(new Vec3(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ), new Vec3(p_canEntityBeSeen_1_.posX, p_canEntityBeSeen_1_.posY + (double)p_canEntityBeSeen_1_.getEyeHeight(), p_canEntityBeSeen_1_.posZ)) == null;
//    }
//
//    public Vec3 getLookVec() {
//        return this.getLook(1.0F);
//    }
//
//    public Vec3 getLook(float p_getLook_1_) {
//        if (p_getLook_1_ == 1.0F) {
//            return this.getVectorForRotation(this.rotationPitch, this.rotationYawHead);
//        } else {
//            float lvt_2_1_ = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * p_getLook_1_;
//            float lvt_3_1_ = this.prevRotationYawHead + (this.rotationYawHead - this.prevRotationYawHead) * p_getLook_1_;
//            return this.getVectorForRotation(lvt_2_1_, lvt_3_1_);
//        }
//    }
//
//    public float getSwingProgress(float p_getSwingProgress_1_) {
//        float lvt_2_1_ = this.swingProgress - this.prevSwingProgress;
//        if (lvt_2_1_ < 0.0F) {
//            ++lvt_2_1_;
//        }
//
//        return this.prevSwingProgress + lvt_2_1_ * p_getSwingProgress_1_;
//    }
//
//    public boolean isServerWorld() {
//        return !this.worldObj.isRemote;
//    }
//
//    public boolean canBeCollidedWith() {
//        return !this.isDead;
//    }
//
//    public boolean canBePushed() {
//        return !this.isDead;
//    }
//
//    protected void setBeenAttacked() {
//        this.velocityChanged = this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttributeValue();
//    }
//
//    public float getRotationYawHead() {
//        return this.rotationYawHead;
//    }
//
//    public void setRotationYawHead(float p_setRotationYawHead_1_) {
//        this.rotationYawHead = p_setRotationYawHead_1_;
//    }
//
//    public void setRenderYawOffset(float p_setRenderYawOffset_1_) {
//        this.renderYawOffset = p_setRenderYawOffset_1_;
//    }
//
//    public float getAbsorptionAmount() {
//        return this.absorptionAmount;
//    }
//
//    public void setAbsorptionAmount(float p_setAbsorptionAmount_1_) {
//        if (p_setAbsorptionAmount_1_ < 0.0F) {
//            p_setAbsorptionAmount_1_ = 0.0F;
//        }
//
//        this.absorptionAmount = p_setAbsorptionAmount_1_;
//    }
//
//    public Team getTeam() {
//        return this.worldObj.getScoreboard().getPlayersTeam(this.getUniqueID().toString());
//    }
//
//    public boolean isOnSameTeam(EntityLivingBase p_isOnSameTeam_1_) {
//        return this.isOnTeam(p_isOnSameTeam_1_.getTeam());
//    }
//
//    public boolean isOnTeam(Team p_isOnTeam_1_) {
//        return this.getTeam() != null ? this.getTeam().isSameTeam(p_isOnTeam_1_) : false;
//    }
//
//    public void sendEnterCombat() {
//    }
//
//    public void sendEndCombat() {
//    }
//
//    protected void markPotionsDirty() {
//        this.potionsNeedUpdate = true;
//    }
//
//    static {
//        sprintingSpeedBoostModifier = (new AttributeModifier(sprintingSpeedBoostModifierUUID, "Sprinting speed boost", 0.30000001192092896D, 2)).setSaved(false);
//    }
//}
//
