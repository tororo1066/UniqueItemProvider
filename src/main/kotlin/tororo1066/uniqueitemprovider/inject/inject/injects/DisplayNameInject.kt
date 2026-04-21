package tororo1066.uniqueitemprovider.inject.inject.injects

import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.sItem.SItem
import tororo1066.uniqueitemprovider.UniqueItemProvider
import tororo1066.uniqueitemprovider.inject.inject.AbstractInject
import tororo1066.uniqueitemprovider.inject.AbstractInjectorData
import tororo1066.uniqueitemprovider.inject.AbstractInjectorSettingsMenu
import tororo1066.uniqueitemprovider.inject.ItemInjector
import tororo1066.uniqueitemprovider.inject.inject.AbstractInjectSettingsMenu
import java.util.regex.Pattern

object DisplayNameInject: AbstractInject<DisplayNameInject.DisplayNameInjectData>() {
    override val name: String = "DisplayNameInject"
    override val description: String = "アイテムの表示名に特定のパターンが含まれているかどうかで注入を行う"
    override val icon: Material = Material.NAME_TAG

    override fun canInject(itemStack: ItemStack, data: DisplayNameInjectData): Boolean {
        val pattern = data.pattern ?: return false
        if (!itemStack.hasItemMeta()) return false
        val displayName = itemStack.itemMeta?.displayName() ?: return false
        val string = if (data.useLegacy) {
            UniqueItemProvider.legacyComponentSerializer.serialize(displayName)
        } else {
            UniqueItemProvider.miniMessage.serialize(displayName)
        }

        return pattern.matcher(string).matches()
    }

    override fun createSettingsMenu(injector: ItemInjector, currentData: DisplayNameInjectData): AbstractInjectorSettingsMenu<DisplayNameInjectData> {
        return DisplayNameInjectSettingsMenu(injector, currentData)
    }

    override fun createData(): DisplayNameInjectData {
        return DisplayNameInjectData()
    }

    class DisplayNameInjectData: AbstractInjectorData() {
        var pattern: Pattern? = null
        var useLegacy: Boolean = false

        override fun load(configurationSection: ConfigurationSection) {
            val patternString = configurationSection.getString("pattern") ?: return
            this.pattern = Pattern.compile(patternString)
            this.useLegacy = configurationSection.getBoolean("useLegacy", false)
        }

        override fun save(configurationSection: ConfigurationSection) {
            configurationSection.set("pattern", this.pattern?.pattern())
            configurationSection.set("useLegacy", this.useLegacy)
        }
    }

    class DisplayNameInjectSettingsMenu(
        injector: ItemInjector,
        currentData: DisplayNameInjectData
    ): AbstractInjectSettingsMenu<DisplayNameInjectData>(
        injector,
        this@DisplayNameInject,
        currentData
    ) {
        override fun renderMenu(p: Player): Boolean {
            super.renderMenu(p)

            setItem(
                20,
                createNullableInputItem(
                    SItem(Material.NAME_TAG)
                        .setDisplayName("§aパターンの設定")
                        .addLore("§d現在の値: §e${currentData.pattern?.pattern() ?: "未設定"}")
                        .addLore("§b正規表現でパターンを指定します")
                        .addLore("§c注: マッチした全てのアイテムに適用されるため、正規表現の指定には注意してください"),
                    String::class.java,
                    message = "正規表現を入力してください"
                ) { str, _ ->
                    if (str.isNullOrEmpty()) {
                        currentData.pattern = null
                    } else {
                        try {
                            currentData.pattern = Pattern.compile(str)
                        } catch (e: Exception) {
                            p.sendMessage("§c正規表現のコンパイルに失敗しました: ${e.message}")
                        }
                    }
                }
            )

            setItem(
                24,
                SInventoryItem(Material.REDSTONE_BLOCK)
                    .setDisplayName("§cレガシー形式の使用")
                    .addLore("§d現在の値: §e${if (currentData.useLegacy) "使用する" else "使用しない"}")
                    .addLore("§bレガシー(セクション)形式で表示名を処理するかどうかを指定します")
                    .setCanClick(false)
                    .setClickEvent { _ ->
                        currentData.useLegacy = !currentData.useLegacy
                        allRenderMenu(p)
                    }
            )

            return true
        }
    }
}