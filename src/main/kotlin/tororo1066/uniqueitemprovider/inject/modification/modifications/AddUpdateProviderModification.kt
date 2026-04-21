package tororo1066.uniqueitemprovider.inject.modification.modifications

import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import tororo1066.tororopluginapi.sItem.SItem
import tororo1066.uniqueitemprovider.UniqueItem
import tororo1066.uniqueitemprovider.inject.AbstractInjectorData
import tororo1066.uniqueitemprovider.inject.AbstractInjectorSettingsMenu
import tororo1066.uniqueitemprovider.inject.ItemInjector
import tororo1066.uniqueitemprovider.inject.modification.AbstractModification
import tororo1066.uniqueitemprovider.inject.modification.AbstractModificationSettingsMenu
import tororo1066.uniqueitemprovider.updateProvider.UpdateProvider

object AddUpdateProviderModification: AbstractModification<AddUpdateProviderModification.AddUpdateProviderModificationData>() {

    override val name: String = "AddUpdateProviderModification"
    override val description: String = "アイテムにUpdateProviderを追加する"
    override val icon: Material = Material.REDSTONE

    override fun modify(itemStack: ItemStack, data: AddUpdateProviderModificationData): ItemStack {
        val uniqueItem = UniqueItem.getOrCreate(itemStack)
        uniqueItem.addProvider(UpdateProvider().also { it.key = data.key })
        return uniqueItem.itemStack
    }

    override fun createSettingsMenu(
        injector: ItemInjector,
        currentData: AddUpdateProviderModificationData
    ): AbstractInjectorSettingsMenu<AddUpdateProviderModificationData> {
        return AddUpdateProviderModificationSettingsMenu(injector, currentData)
    }

    override fun createData(): AddUpdateProviderModificationData {
        return AddUpdateProviderModificationData()
    }

    class AddUpdateProviderModificationData: AbstractInjectorData() {
        var key: String = ""

        override fun load(configurationSection: ConfigurationSection) {
            key = configurationSection.getString("key") ?: ""
        }

        override fun save(configurationSection: ConfigurationSection) {
            configurationSection.set("key", key)
        }
    }

    class AddUpdateProviderModificationSettingsMenu(
        injector: ItemInjector,
        currentData: AddUpdateProviderModificationData
    ): AbstractModificationSettingsMenu<AddUpdateProviderModificationData>(
        injector,
        this@AddUpdateProviderModification,
        currentData
    ) {
        override fun renderMenu(p: Player): Boolean {
            super.renderMenu(p)

            setItem(
                22,
                createInputItem(
                    SItem(Material.NAME_TAG)
                        .setDisplayName("§aUpdateProviderのキー")
                        .addLore("§d現在の値: ${currentData.key}"),
                    String::class.java,
                    message = "UpdateProviderのキーを入力してください"
                ) { str, _ ->
                    currentData.key = str
                    allRenderMenu(p)
                }
            )

            return true
        }
    }
}