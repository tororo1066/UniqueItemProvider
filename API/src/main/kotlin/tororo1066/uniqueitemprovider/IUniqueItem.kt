package tororo1066.uniqueitemprovider

import org.bukkit.inventory.ItemStack

interface IUniqueItem {
    var itemStack: ItemStack
    var providers: MutableList<AbstractItemProvider>

    fun addProvider(provider: AbstractItemProvider)
}