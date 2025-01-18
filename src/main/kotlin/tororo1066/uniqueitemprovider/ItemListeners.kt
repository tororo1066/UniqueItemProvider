package tororo1066.uniqueitemprovider

import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import tororo1066.tororopluginapi.annotation.SEventHandler

class ItemListeners {

    @SEventHandler
    fun onInteract(e: PlayerInteractEvent) {
        val item = e.item?.let { UniqueItem.get(it) } ?: return
        item.providers.forEach { it.onInteract(e, item) }
    }

    @SEventHandler
    fun onConsume(e: PlayerItemConsumeEvent) {
        val item = UniqueItem.get(e.item) ?: return
        item.providers.forEach { it.onConsume(e, item) }
    }

    @SEventHandler
    fun onDrop(e: PlayerDropItemEvent) {
        val item = UniqueItem.get(e.itemDrop.itemStack) ?: return
        item.providers.forEach { it.onDrop(e, item) }
    }

    @SEventHandler
    fun onItemClick(e: InventoryClickEvent) {
        val item = e.currentItem?.let { UniqueItem.get(it) } ?: return
        item.providers.forEach { it.onItemClick(e, item) }
    }

    @SEventHandler
    fun onHeld(e: PlayerItemHeldEvent) {
        val item = e.player.inventory.getItem(e.newSlot)?.let { UniqueItem.get(it) } ?: return
        item.providers.forEach { it.onHeld(e, item) }
    }
}