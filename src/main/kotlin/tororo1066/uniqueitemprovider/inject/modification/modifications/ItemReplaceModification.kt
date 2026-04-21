package tororo1066.uniqueitemprovider.inject.modification.modifications

import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.defaultMenus.SingleItemInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.uniqueitemprovider.inject.AbstractInjectorData
import tororo1066.uniqueitemprovider.inject.AbstractInjectorSettingsMenu
import tororo1066.uniqueitemprovider.inject.ItemInjector
import tororo1066.uniqueitemprovider.inject.modification.AbstractModification
import tororo1066.uniqueitemprovider.inject.modification.AbstractModificationSettingsMenu
import java.util.function.Consumer
import kotlin.math.min

object ItemReplaceModification: AbstractModification<ItemReplaceModification.ItemReplaceModificationData>() {
    override val name: String = "ItemReplaceModification"
    override val description: String = "アイテムを指定したアイテムに置き換える"
    override val icon: Material = Material.DIAMOND_BLOCK

    override fun modify(itemStack: ItemStack, data: ItemReplaceModificationData): ItemStack {
//        return data.itemStack ?: itemStack
        val amount = itemStack.amount
        val newItemStack = data.itemStack?.clone() ?: return itemStack
        newItemStack.amount = min(amount, newItemStack.maxStackSize)
        return newItemStack
    }

    override fun createSettingsMenu(
        injector: ItemInjector,
        currentData: ItemReplaceModificationData
    ): AbstractInjectorSettingsMenu<ItemReplaceModificationData> {
        return ItemReplaceModificationSettingsMenu(injector, currentData)
    }

    override fun createData(): ItemReplaceModificationData {
        return ItemReplaceModificationData()
    }

    class ItemReplaceModificationData: AbstractInjectorData() {
        var itemStack: ItemStack? = null

        override fun load(configurationSection: ConfigurationSection) {
            itemStack = configurationSection.getItemStack("itemStack")
        }

        override fun save(configurationSection: ConfigurationSection) {
            configurationSection.set("itemStack", itemStack)
        }
    }

    class ItemReplaceModificationSettingsMenu(
        injector: ItemInjector,
        currentData: ItemReplaceModificationData
    ): AbstractModificationSettingsMenu<ItemReplaceModificationData>(
        injector,
        this@ItemReplaceModification,
        currentData
    ) {
        override fun renderMenu(p: Player): Boolean {
            super.renderMenu(p)

            setItem(
                22,
                SInventoryItem(currentData.itemStack ?: ItemStack((Material.BARRIER)))
                    .setDisplayName("§a変更後のアイテム")
                    .setCanClick(false)
                    .setClickEvent { _ ->
                        val inv = SingleItemInventory(
                            SJavaPlugin.plugin,
                            "変更後のアイテム"
                        )
                        inv.nowItem = currentData.itemStack ?: ItemStack(Material.AIR)
                        inv.onConfirm = Consumer { item ->
                            if (item.type.isAir) {
                                currentData.itemStack = null
                            } else {
                                currentData.itemStack = item
                            }
                            p.closeInventory()
                        }

                        moveChildInventory(inv, p)
                    }
            )

            return true
        }
    }


}