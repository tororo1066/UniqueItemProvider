package tororo1066.uniqueitemprovider

import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

open class UniqueItem(private var itemStack: ItemStack) : IUniqueItem {
    private var providers: MutableList<AbstractItemProvider> = mutableListOf()

    init {
        val providers = getProviders(itemStack)
        this.providers.addAll(providers)
    }

    override fun getItemStack(): ItemStack {
        return itemStack
    }

    override fun setItemStack(itemStack: ItemStack) {
        this.itemStack = itemStack
    }

    override fun getProviders(): List<AbstractItemProvider> {
        return providers
    }

    private fun updateProviders() {
        val itemMeta = itemStack.itemMeta
        itemMeta.persistentDataContainer.set(
            UniqueItemProvider.PROVIDERS_KEY,
            PersistentDataType.TAG_CONTAINER,
            itemMeta.persistentDataContainer.adapterContext.newPersistentDataContainer().also {
                for (provider in this.providers) {
                    it.set(
                        provider.namespacedKey,
                        PersistentDataType.TAG_CONTAINER,
                        provider.createPersistentDataContainer(itemMeta.persistentDataContainer.adapterContext.newPersistentDataContainer())
                    )
                }
            }
        )
        itemStack.itemMeta = itemMeta
    }

    override fun setProviders(providers: List<AbstractItemProvider>) {
        this.providers = providers.toMutableList()
        updateProviders()
    }


    override fun addProvider(provider: AbstractItemProvider) {
        this.providers.add(provider)
        updateProviders()
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
            if (!itemMeta.persistentDataContainer.has(UniqueItemProvider.PROVIDERS_KEY, PersistentDataType.TAG_CONTAINER)) return emptyList()
            val providers = itemMeta.persistentDataContainer.get(UniqueItemProvider.PROVIDERS_KEY, PersistentDataType.TAG_CONTAINER)!!
            return providers.keys.mapNotNull {
                val provider = UniqueItemProvider.providers[it]?.getConstructor()?.newInstance() ?: return@mapNotNull null
                provider.loadPersistentDataContainer(providers.get(it, PersistentDataType.TAG_CONTAINER) ?: return@mapNotNull null)
                provider
            }
        }
    }
}