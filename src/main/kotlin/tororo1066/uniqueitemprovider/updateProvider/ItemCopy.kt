package tororo1066.uniqueitemprovider.updateProvider

import io.papermc.paper.datacomponent.DataComponentType
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.BannerPatternLayers
import io.papermc.paper.datacomponent.item.BlockItemDataProperties
import io.papermc.paper.datacomponent.item.BlocksAttacks
import io.papermc.paper.datacomponent.item.BundleContents
import io.papermc.paper.datacomponent.item.ChargedProjectiles
import io.papermc.paper.datacomponent.item.Consumable
import io.papermc.paper.datacomponent.item.CustomModelData
import io.papermc.paper.datacomponent.item.DamageResistant
import io.papermc.paper.datacomponent.item.DeathProtection
import io.papermc.paper.datacomponent.item.DyedItemColor
import io.papermc.paper.datacomponent.item.Enchantable
import io.papermc.paper.datacomponent.item.Equippable
import io.papermc.paper.datacomponent.item.Fireworks
import io.papermc.paper.datacomponent.item.FoodProperties
import io.papermc.paper.datacomponent.item.ItemAdventurePredicate
import io.papermc.paper.datacomponent.item.ItemArmorTrim
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers
import io.papermc.paper.datacomponent.item.ItemContainerContents
import io.papermc.paper.datacomponent.item.ItemEnchantments
import io.papermc.paper.datacomponent.item.ItemLore
import io.papermc.paper.datacomponent.item.JukeboxPlayable
import io.papermc.paper.datacomponent.item.LodestoneTracker
import io.papermc.paper.datacomponent.item.MapDecorations
import io.papermc.paper.datacomponent.item.MapId
import io.papermc.paper.datacomponent.item.MapItemColor
import io.papermc.paper.datacomponent.item.OminousBottleAmplifier
import io.papermc.paper.datacomponent.item.PotDecorations
import io.papermc.paper.datacomponent.item.PotionContents
import io.papermc.paper.datacomponent.item.Repairable
import io.papermc.paper.datacomponent.item.ResolvableProfile
import io.papermc.paper.datacomponent.item.SuspiciousStewEffects
import io.papermc.paper.datacomponent.item.Tool
import io.papermc.paper.datacomponent.item.TooltipDisplay
import io.papermc.paper.datacomponent.item.UseCooldown
import io.papermc.paper.datacomponent.item.UseRemainder
import io.papermc.paper.datacomponent.item.Weapon
import io.papermc.paper.datacomponent.item.WritableBookContent
import io.papermc.paper.datacomponent.item.WrittenBookContent
import io.papermc.paper.item.MapPostProcessing
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import org.bukkit.FireworkEffect
import org.bukkit.DyeColor
import org.bukkit.block.banner.PatternType
import org.bukkit.inventory.ItemRarity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.trim.TrimMaterial
import io.papermc.paper.registry.tag.TagKey
import org.bukkit.MusicInstrument

@Suppress("UnStableApiUsage")
abstract class ItemCopy {

    companion object {
        val copies = listOf(
            ItemFlagCopy,
            PersistentDataContainerCopy,
            TypeCopy,
            MaxStackSizeCopy,
            MaxDamageCopy,
            DamageCopy,
            UnbreakableCopy,
            CustomNameCopy,
            ItemNameCopy,
            ItemModelCopy,
            LoreCopy,
            RarityCopy,
            EnchantmentsCopy,
            CanPlaceOnCopy,
            CanBreakCopy,
            AttributeModifiersCopy,
            CustomModelDataCopy,
            TooltipDisplayCopy,
            RepairCostCopy,
            EnchantmentGlintOverrideCopy,
            FoodCopy,
            ConsumableCopy,
            UseRemainderCopy,
            UseCooldownCopy,
            DamageResistantCopy,
            ToolCopy,
            WeaponCopy,
            EnchantableCopy,
            EquippableCopy,
            RepairableCopy,
            GliderCopy,
            TooltipStyleCopy,
            DeathProtectionCopy,
            BlocksAttacksCopy,
            StoredEnchantmentsCopy,
            DyedColorCopy,
            MapColorCopy,
            MapIdCopy,
            MapDecorationsCopy,
            MapPostProcessingCopy,
            ChargedProjectilesCopy,
            BundleContentsCopy,
            PotionContentsCopy,
            PotionDurationScaleCopy,
            SuspiciousStewEffectsCopy,
            WritableBookContentCopy,
            WrittenBookContentCopy,
            TrimCopy,
            InstrumentCopy,
            ProvidesTrimMaterialCopy,
            OminousBottleAmplifierCopy,
            JukeboxPlayableCopy,
            ProvidesBannerPatternsCopy,
            RecipesCopy,
            LodestoneTrackerCopy,
            FireworkExplosionCopy,
            FireworksCopy,
            ProfileCopy,
            NoteBlockSoundCopy,
            BannerPatternsCopy,
            BaseColorCopy,
            PotDecorationsCopy,
            ContainerCopy,
            BlockDataCopy
        ).associateBy { it.name }
    }

    abstract val name: String

    protected abstract fun copy(to: ItemStack, from: ItemStack): ItemStack

    fun cloneAndCopy(to: ItemStack, from: ItemStack): ItemStack {
        return copy(to.clone(), from.clone())
    }

    abstract fun isSimilar(itemStack: ItemStack, itemStack1: ItemStack): Boolean

    object ItemFlagCopy: ItemCopy() {

        override val name: String = "itemFlag"

        override fun copy(to: ItemStack, from: ItemStack): ItemStack {
            from.itemFlags.forEach {
                to.addItemFlags(it)
            }
            return to
        }

        override fun isSimilar(itemStack: ItemStack, itemStack1: ItemStack): Boolean {
            return itemStack.itemFlags == itemStack1.itemFlags
        }
    }

    object PersistentDataContainerCopy: ItemCopy() {

        override val name: String = "persistentDataContainer"

        override fun copy(to: ItemStack, from: ItemStack): ItemStack {
            val fromMeta = from.itemMeta ?: return to
            to.editMeta {
                fromMeta.persistentDataContainer.keys.forEach { key ->
                    it.persistentDataContainer.remove(key)
                }
                fromMeta.persistentDataContainer.copyTo(it.persistentDataContainer, true)
            }
            return to
        }

        override fun isSimilar(itemStack: ItemStack, itemStack1: ItemStack): Boolean {
            return itemStack.itemMeta.persistentDataContainer.serializeToBytes()
                .contentEquals(itemStack1.itemMeta.persistentDataContainer.serializeToBytes())
        }
    }

    object TypeCopy: ItemCopy() {

        override val name: String = "type"

        override fun copy(to: ItemStack, from: ItemStack): ItemStack {
            return to.withType(from.type)
        }

        override fun isSimilar(itemStack: ItemStack, itemStack1: ItemStack): Boolean {
            return itemStack.type == itemStack1.type
        }
    }

    abstract class ValuedDataComponentCopy<T: Any>(private val type: DataComponentType.Valued<T>): ItemCopy() {

        override val name: String = type.key.key

        override fun copy(to: ItemStack, from: ItemStack): ItemStack {
            val data = from.getData(type) ?: return to
            to.setData(type, data)
            return to
        }

        override fun isSimilar(itemStack: ItemStack, itemStack1: ItemStack): Boolean {
            val data = itemStack.getData(type)
            val data1 = itemStack1.getData(type)
            if (data == null && data1 == null) return true
            if ((data == null) != (data1 == null)) return false
            return data == data1
        }
    }

    abstract class NonValuedDataComponentCopy(private val type: DataComponentType.NonValued): ItemCopy() {

        override val name: String = type.key.key

        override fun copy(to: ItemStack, from: ItemStack): ItemStack {
            if (!from.hasData(type)) return to
            to.setData(type)
            return to
        }

        override fun isSimilar(itemStack: ItemStack, itemStack1: ItemStack): Boolean {
            return itemStack.hasData(type) == itemStack1.hasData(type)
        }
    }

    object MaxStackSizeCopy: ValuedDataComponentCopy<Int>(DataComponentTypes.MAX_STACK_SIZE)
    object MaxDamageCopy: ValuedDataComponentCopy<Int>(DataComponentTypes.MAX_DAMAGE)
    object DamageCopy: ValuedDataComponentCopy<Int>(DataComponentTypes.DAMAGE)
    object UnbreakableCopy: NonValuedDataComponentCopy(DataComponentTypes.UNBREAKABLE)
    object CustomNameCopy: ValuedDataComponentCopy<Component>(DataComponentTypes.CUSTOM_NAME)
    object ItemNameCopy: ValuedDataComponentCopy<Component>(DataComponentTypes.ITEM_NAME)
    object ItemModelCopy: ValuedDataComponentCopy<Key>(DataComponentTypes.ITEM_MODEL)
    object LoreCopy: ValuedDataComponentCopy<ItemLore>(DataComponentTypes.LORE)
    object RarityCopy: ValuedDataComponentCopy<ItemRarity>(DataComponentTypes.RARITY)
    object EnchantmentsCopy: ValuedDataComponentCopy<ItemEnchantments>(DataComponentTypes.ENCHANTMENTS)
    object CanPlaceOnCopy: ValuedDataComponentCopy<ItemAdventurePredicate>(DataComponentTypes.CAN_PLACE_ON)
    object CanBreakCopy: ValuedDataComponentCopy<ItemAdventurePredicate>(DataComponentTypes.CAN_BREAK)
    object AttributeModifiersCopy: ValuedDataComponentCopy<ItemAttributeModifiers>(DataComponentTypes.ATTRIBUTE_MODIFIERS)
    object CustomModelDataCopy: ValuedDataComponentCopy<CustomModelData>(DataComponentTypes.CUSTOM_MODEL_DATA)
    object TooltipDisplayCopy: ValuedDataComponentCopy<TooltipDisplay>(DataComponentTypes.TOOLTIP_DISPLAY)
    object RepairCostCopy: ValuedDataComponentCopy<Int>(DataComponentTypes.REPAIR_COST)
    object EnchantmentGlintOverrideCopy: ValuedDataComponentCopy<Boolean>(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE)
    object FoodCopy: ValuedDataComponentCopy<FoodProperties>(DataComponentTypes.FOOD)
    object ConsumableCopy: ValuedDataComponentCopy<Consumable>(DataComponentTypes.CONSUMABLE)
    object UseRemainderCopy: ValuedDataComponentCopy<UseRemainder>(DataComponentTypes.USE_REMAINDER)
    object UseCooldownCopy: ValuedDataComponentCopy<UseCooldown>(DataComponentTypes.USE_COOLDOWN)
    object DamageResistantCopy: ValuedDataComponentCopy<DamageResistant>(DataComponentTypes.DAMAGE_RESISTANT)
    object ToolCopy: ValuedDataComponentCopy<Tool>(DataComponentTypes.TOOL)
    object WeaponCopy: ValuedDataComponentCopy<Weapon>(DataComponentTypes.WEAPON)
    object EnchantableCopy: ValuedDataComponentCopy<Enchantable>(DataComponentTypes.ENCHANTABLE)
    object EquippableCopy: ValuedDataComponentCopy<Equippable>(DataComponentTypes.EQUIPPABLE)
    object RepairableCopy: ValuedDataComponentCopy<Repairable>(DataComponentTypes.REPAIRABLE)
    object GliderCopy: NonValuedDataComponentCopy(DataComponentTypes.GLIDER)
    object TooltipStyleCopy: ValuedDataComponentCopy<Key>(DataComponentTypes.TOOLTIP_STYLE)
    object DeathProtectionCopy: ValuedDataComponentCopy<DeathProtection>(DataComponentTypes.DEATH_PROTECTION)
    object BlocksAttacksCopy: ValuedDataComponentCopy<BlocksAttacks>(DataComponentTypes.BLOCKS_ATTACKS)
    object StoredEnchantmentsCopy: ValuedDataComponentCopy<ItemEnchantments>(DataComponentTypes.STORED_ENCHANTMENTS)
    object DyedColorCopy: ValuedDataComponentCopy<DyedItemColor>(DataComponentTypes.DYED_COLOR)
    object MapColorCopy: ValuedDataComponentCopy<MapItemColor>(DataComponentTypes.MAP_COLOR)
    object MapIdCopy: ValuedDataComponentCopy<MapId>(DataComponentTypes.MAP_ID)
    object MapDecorationsCopy: ValuedDataComponentCopy<MapDecorations>(DataComponentTypes.MAP_DECORATIONS)
    object MapPostProcessingCopy: ValuedDataComponentCopy<MapPostProcessing>(DataComponentTypes.MAP_POST_PROCESSING)
    object ChargedProjectilesCopy: ValuedDataComponentCopy<ChargedProjectiles>(DataComponentTypes.CHARGED_PROJECTILES)
    object BundleContentsCopy: ValuedDataComponentCopy<BundleContents>(DataComponentTypes.BUNDLE_CONTENTS)
    object PotionContentsCopy: ValuedDataComponentCopy<PotionContents>(DataComponentTypes.POTION_CONTENTS)
    object PotionDurationScaleCopy: ValuedDataComponentCopy<Float>(DataComponentTypes.POTION_DURATION_SCALE)
    object SuspiciousStewEffectsCopy: ValuedDataComponentCopy<SuspiciousStewEffects>(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS)
    object WritableBookContentCopy: ValuedDataComponentCopy<WritableBookContent>(DataComponentTypes.WRITABLE_BOOK_CONTENT)
    object WrittenBookContentCopy: ValuedDataComponentCopy<WrittenBookContent>(DataComponentTypes.WRITTEN_BOOK_CONTENT)
    object TrimCopy: ValuedDataComponentCopy<ItemArmorTrim>(DataComponentTypes.TRIM)
    object InstrumentCopy: ValuedDataComponentCopy<MusicInstrument>(DataComponentTypes.INSTRUMENT)
    object ProvidesTrimMaterialCopy: ValuedDataComponentCopy<TrimMaterial>(DataComponentTypes.PROVIDES_TRIM_MATERIAL)
    object OminousBottleAmplifierCopy: ValuedDataComponentCopy<OminousBottleAmplifier>(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER)
    object JukeboxPlayableCopy: ValuedDataComponentCopy<JukeboxPlayable>(DataComponentTypes.JUKEBOX_PLAYABLE)
    object ProvidesBannerPatternsCopy: ValuedDataComponentCopy<TagKey<PatternType>>(DataComponentTypes.PROVIDES_BANNER_PATTERNS)
    object RecipesCopy: ValuedDataComponentCopy<List<Key>>(DataComponentTypes.RECIPES)
    object LodestoneTrackerCopy: ValuedDataComponentCopy<LodestoneTracker>(DataComponentTypes.LODESTONE_TRACKER)
    object FireworkExplosionCopy: ValuedDataComponentCopy<FireworkEffect>(DataComponentTypes.FIREWORK_EXPLOSION)
    object FireworksCopy: ValuedDataComponentCopy<Fireworks>(DataComponentTypes.FIREWORKS)
    object ProfileCopy: ValuedDataComponentCopy<ResolvableProfile>(DataComponentTypes.PROFILE)
    object NoteBlockSoundCopy: ValuedDataComponentCopy<Key>(DataComponentTypes.NOTE_BLOCK_SOUND)
    object BannerPatternsCopy: ValuedDataComponentCopy<BannerPatternLayers>(DataComponentTypes.BANNER_PATTERNS)
    object BaseColorCopy: ValuedDataComponentCopy<DyeColor>(DataComponentTypes.BASE_COLOR)
    object PotDecorationsCopy: ValuedDataComponentCopy<PotDecorations>(DataComponentTypes.POT_DECORATIONS)
    object ContainerCopy: ValuedDataComponentCopy<ItemContainerContents>(DataComponentTypes.CONTAINER)
    object BlockDataCopy: ValuedDataComponentCopy<BlockItemDataProperties>(DataComponentTypes.BLOCK_DATA)
}