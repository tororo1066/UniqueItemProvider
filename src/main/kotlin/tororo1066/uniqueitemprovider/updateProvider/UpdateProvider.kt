package tororo1066.uniqueitemprovider.updateProvider

import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.uniqueitemprovider.AbstractItemProvider
import tororo1066.uniqueitemprovider.IUniqueItem
import tororo1066.uniqueitemprovider.UniqueItemProvider
import java.util.concurrent.CompletableFuture

class UpdateProvider: AbstractItemProvider(NamespacedKey(SJavaPlugin.plugin, "update")) {

    var key: String = ""

    override fun createPersistentDataContainer(container: PersistentDataContainer): PersistentDataContainer {
        container.set(NamespacedKey(SJavaPlugin.plugin, "key"), PersistentDataType.STRING, key)
        return container
    }

    override fun loadPersistentDataContainer(container: PersistentDataContainer) {
        key = container.get(NamespacedKey(SJavaPlugin.plugin, "key"), PersistentDataType.STRING) ?: ""
    }

    override fun onHeld(e: PlayerItemHeldEvent, item: IUniqueItem) {
        val itemStack = UpdateProviderItem.items[key]?.itemStack ?: return
        val rawItem = e.player.inventory.getItem(e.newSlot) ?: return
        if (!itemStack.isSimilar(rawItem)) {
            e.player.inventory.setItem(e.newSlot, itemStack)
        }
    }
}