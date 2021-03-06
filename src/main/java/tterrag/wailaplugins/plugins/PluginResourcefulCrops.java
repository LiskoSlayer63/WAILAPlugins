package tterrag.wailaplugins.plugins;

import static net.minecraft.client.renderer.GlStateManager.disableTexture2D;
import static net.minecraft.client.renderer.GlStateManager.enableTexture2D;
import static net.minecraft.client.renderer.GlStateManager.popMatrix;
import static net.minecraft.client.renderer.GlStateManager.pushMatrix;
import static net.minecraft.client.renderer.GlStateManager.scale;
import static net.minecraft.client.renderer.GlStateManager.translate;

import com.enderio.core.client.render.RenderUtil;

import mcp.mobius.waila.api.IWailaBlockDecorator;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import tehnut.resourceful.crops.block.BlockResourcefulCrop;
import tehnut.resourceful.crops.block.tile.TileSeedContainer;
import tehnut.resourceful.crops.core.RegistrarResourcefulCrops;
import tehnut.resourceful.crops.core.data.Seed;
import tehnut.resourceful.crops.item.ItemResourceful;
import tterrag.wailaplugins.api.Plugin;

@Plugin(deps = "resourcefulcrops")
public class PluginResourcefulCrops extends PluginBase implements IWailaBlockDecorator {

    private static EntityItem item;

    @Override
    public void load(IWailaRegistrar registrar)
    {
        super.load(registrar);

        registrar.registerDecorator(this, BlockResourcefulCrop.class);
        addConfig("showHover");
        addConfig("showOutputItem", false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void decorateBlock(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (!getConfig("showHover") || !enabled())
            return;

        Vec3d pos = accessor.getRenderingPosition();
        if (pos == null) {
            return;
        }

        if (accessor.getBlock() instanceof BlockResourcefulCrop)
        {
            TileEntity cropTile = accessor.getTileEntity();
            if (cropTile != null && cropTile instanceof TileSeedContainer)
            {
                IForgeRegistry<Seed> seedRegistry = GameRegistry.findRegistry(Seed.class);
                Seed seed = seedRegistry.getValue(((TileSeedContainer) cropTile).getSeedKey());
                
                ItemStack hoverStack;
                if (getConfig("showOutputItem")) {
                    hoverStack = seed.getOutputs()[0].getItem().copy();
                } else {
                    hoverStack = ItemResourceful.getResourcefulStack(RegistrarResourcefulCrops.SHARD, seed.getRegistryName());
                }
                
                if (hoverStack == null) {
                    return;
                }

                if (item == null)
                    item = new EntityItem(accessor.getWorld(), 0, 0, 0, hoverStack);
                else
                    item.setItem(hoverStack);

                pushMatrix();
                disableTexture2D();
                enableTexture2D();
                RenderHelper.enableStandardItemLighting();
                translate(pos.x + 0.5, pos.y + 0.9, pos.z + 0.5);
                pushMatrix();
                scale(0.75f, 0.75f, 0.75f);
                RenderUtil.render3DItem(item, true);
                popMatrix();
                popMatrix();
            }
        }
    }
}
