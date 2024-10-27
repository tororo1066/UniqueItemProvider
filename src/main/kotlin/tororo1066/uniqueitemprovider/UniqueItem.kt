package tororo1066.uniqueitemprovider

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

open class UniqueItem(override var itemStack: ItemStack) : IUniqueItem {
    override var providers: MutableList<AbstractItemProvider> = mutableListOf()

    init {
        val providers = getProviders(itemStack)
        this.providers.addAll(providers)
    }

    override fun addProvider(provider: AbstractItemProvider) {
        providers.add(provider)
        val itemMeta = itemStack.itemMeta
        val providers = providers.map { it.namespacedKey.toString() }
        itemMeta.persistentDataContainer.set(UniqueItemProvider.PROVIDERS_KEY, PersistentDataType.LIST.strings(), providers)
        itemStack.itemMeta = itemMeta
    }

    companion object {

        fun get(itemStack: ItemStack): UniqueItem? {
            if (!itemStack.hasItemMeta()) return null
            val itemMeta = itemStack.itemMeta
            if (!itemMeta.persistentDataContainer.has(UniqueItemProvider.UNIQUE_ITEM_KEY, PersistentDataType.STRING)) return null
            return UniqueItem(itemStack)
        }

        fun getOrCreate(itemStack: ItemStack): UniqueItem {
            return get(itemStack) ?: run {
                val itemMeta = if (itemStack.hasItemMeta()) itemStack.itemMeta else Bukkit.getItemFactory().getItemMeta(itemStack.type)
                itemMeta.persistentDataContainer.set(UniqueItemProvider.UNIQUE_ITEM_KEY, PersistentDataType.STRING, "")
                itemStack.itemMeta = itemMeta
                return UniqueItem(itemStack)
            }
        }

        fun getProviders(itemStack: ItemStack): List<AbstractItemProvider> {
            if (!itemStack.hasItemMeta()) return emptyList()
            val itemMeta = itemStack.itemMeta
            if (!itemMeta.persistentDataContainer.has(UniqueItemProvider.PROVIDERS_KEY, PersistentDataType.LIST.strings())) return emptyList()
            val providers = itemMeta.persistentDataContainer.get(UniqueItemProvider.PROVIDERS_KEY, PersistentDataType.LIST.strings())!!
            val namespacedKeys = providers.map { NamespacedKey.fromString(it)!! }
            return namespacedKeys.mapNotNull { UniqueItemProvider.providers[it] }
        }
    }
}