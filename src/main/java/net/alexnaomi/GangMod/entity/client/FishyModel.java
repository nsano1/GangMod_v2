package net.alexnaomi.GangMod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.alexnaomi.GangMod.GangMod;
import net.alexnaomi.GangMod.entity.custom.FishyEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

import javax.swing.text.html.parser.Entity;

public class FishyModel<T extends FishyEntity> extends HierarchicalModel<T> {

    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(GangMod.MOD_ID, "fishy"), "main");
    private final ModelPart fishy;
    private final ModelPart face;
    private final ModelPart top;
    private final ModelPart mainbody;
    private final ModelPart fins;
    private final ModelPart topfin;
    private final ModelPart leftfin;
    private final ModelPart rightfin;
    private final ModelPart nub;
    private final ModelPart backfin;

    public FishyModel(ModelPart root) {
        this.fishy = root.getChild("fishy");
        this.face = this.fishy.getChild("face");
        this.top = this.fishy.getChild("top");
        this.mainbody = this.fishy.getChild("mainbody");
        this.fins = this.fishy.getChild("fins");
        this.topfin = this.fins.getChild("topfin");
        this.leftfin = this.fins.getChild("leftfin");
        this.rightfin = this.fins.getChild("rightfin");
        this.nub = this.fins.getChild("nub");
        this.backfin = this.fins.getChild("backfin");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition fishy = partdefinition.addOrReplaceChild("fishy", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 18.0F, -0.5F, 0.0F, 1.5708F, 0.0F));

        PartDefinition face = fishy.addOrReplaceChild("face", CubeListBuilder.create().texOffs(0, 10).addBox(3.0F, -5.0F, -3.0F, 2.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.5F));

        PartDefinition top = fishy.addOrReplaceChild("top", CubeListBuilder.create().texOffs(12, 14).addBox(-2.0F, -7.0F, -3.0F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.5F));

        PartDefinition mainbody = fishy.addOrReplaceChild("mainbody", CubeListBuilder.create().texOffs(15, 5).addBox(-5.0F, -5.0F, -3.0F, 2.0F, 4.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(6, 0).addBox(-4.0F, -2.0F, -3.0F, 8.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(2, 20).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.5F));

        PartDefinition fins = fishy.addOrReplaceChild("fins", CubeListBuilder.create(), PartPose.offset(2.0F, 0.0F, -1.5F));

        PartDefinition topfin = fins.addOrReplaceChild("topfin", CubeListBuilder.create().texOffs(3, 23).addBox(-2.0F, -2.0F, 1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition leftfin = fins.addOrReplaceChild("leftfin", CubeListBuilder.create().texOffs(25, 24).addBox(1.0F, -3.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 6.0F, 2.0F));

        PartDefinition rightfin = fins.addOrReplaceChild("rightfin", CubeListBuilder.create().texOffs(25, 24).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 4.0F, 5.0F));

        PartDefinition nub = fins.addOrReplaceChild("nub", CubeListBuilder.create().texOffs(25, 22).addBox(-6.0F, -1.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition backfin = fins.addOrReplaceChild("backfin", CubeListBuilder.create().texOffs(3, 23).addBox(-9.0F, 2.0F, 1.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(FishyEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // Priority order: Wave > Wiggle > Walk > Idle
        if (entity.waveAnimationState.isStarted()) {
            this.animate(entity.waveAnimationState, FishyAnimations.FISHY_WAVE, ageInTicks, 1f);
        } else if (entity.wiggleAnimationState.isStarted()) {
            this.animate(entity.wiggleAnimationState, FishyAnimations.FISHY_WIGGLE, ageInTicks, 1f);
        } else {
            this.animateWalk(FishyAnimations.FISHY_WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        fishy.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root(){
        return fishy;
    }
}
