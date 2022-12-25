package tfar.craftingstation;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tfar.craftingstation.init.ModBlockEntityTypes;
import tfar.craftingstation.init.ModBlocks;
import tfar.craftingstation.init.ModMenuTypes;
import tfar.craftingstation.network.PacketHandler;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CraftingStation.MODID)
public class CraftingStation {
    // Directly reference a log4j logger.

    public static final String MODID = "craftingstation";
    public static final TagKey<BlockEntityType<?>> blacklisted
            = TagKey.create(Registries.BLOCK_ENTITY_TYPE, new ResourceLocation(MODID, "blacklisted"));

    public static final Logger LOGGER = LogManager.getLogger();

    public CraftingStation() {
        // Register the setup method for modloading
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_SPEC);
        IEventBus iEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        iEventBus.addListener(this::setup);
        iEventBus.addListener(this::enqueueIMC);
        iEventBus.addListener(RegistryEvents::block);
        //ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Configs.SERVER_SPEC);
    }

    public static final Configs.Server SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        //final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        //CLIENT_SPEC = specPair.getRight();
        //CLIENT = specPair.getLeft();
        final Pair<Configs.Server, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(Configs.Server::new);
        SERVER_SPEC = specPair2.getRight();
        SERVER = specPair2.getLeft();
    }


    private void setup(final FMLCommonSetupEvent event) {
        PacketHandler.registerMessages(MODID);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo("craftingtweaks", "RegisterProvider", () -> {
            CompoundTag tagCompound = new CompoundTag();
            tagCompound.putString("ContainerClass", CraftingStationMenu.class.getName());
            tagCompound.putString("AlignToGrid", "left");
            return tagCompound;
        });
    }

    public static class RegistryEvents {
        @SubscribeEvent
        public static void block(final RegisterEvent event) {
            // register a new block here
            event.register(Registries.BLOCK, modLoc("crafting_station"), () -> ModBlocks.crafting_station);
            event.register(Registries.BLOCK, modLoc("crafting_station_slab"), () -> ModBlocks.crafting_station_slab);
            // register a new item here
            Item.Properties properties = new Item.Properties();
            event.register(Registries.ITEM, modLoc("crafting_station"), () -> new BlockItem(ModBlocks.crafting_station, properties));
            event.register(Registries.ITEM, modLoc("crafting_station_slab"), () -> new BlockItem(ModBlocks.crafting_station_slab, properties));
            event.register(Registries.MENU, modLoc("crafting_station"), () -> ModMenuTypes.crafting_station);
            event.register(Registries.BLOCK_ENTITY_TYPE, modLoc("crafting_station"), () -> ModBlockEntityTypes.crafting_station);
        }

        public static ResourceLocation modLoc(String s) {
            return new ResourceLocation(MODID, s);
        }
    }
}