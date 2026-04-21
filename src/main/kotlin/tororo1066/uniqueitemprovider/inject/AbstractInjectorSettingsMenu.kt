package tororo1066.uniqueitemprovider.inject

import org.bukkit.Material
import org.bukkit.entity.Player
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem

abstract class AbstractInjectorSettingsMenu<T: AbstractInjectorData>(
    val injector: ItemInjector,
    val inject: AbstractInjector<T>,
    var currentData: T,
    val onSave: (T) -> Unit
): SInventory(
    SJavaPlugin.plugin,
    "§a${inject.name}の設定",
    6
) {
    val saveItem: SInventoryItem
        get() {
            return SInventoryItem(Material.LIME_STAINED_GLASS_PANE)
                .setDisplayName("§a保存")
                .setCanClick(false)
                .setClickEvent { e ->
                    onSave(currentData)
                    e.whoClicked.closeInventory()
                }
        }

    init {
        @Suppress("UNCHECKED_CAST")
        currentData = currentData.clone() as T
    }

    override fun renderMenu(p: Player): Boolean {
        fillItem(SInventoryItem(Material.GRAY_STAINED_GLASS_PANE).setCanClick(false))
        setItems(45..53, saveItem)
        return true
    }
}