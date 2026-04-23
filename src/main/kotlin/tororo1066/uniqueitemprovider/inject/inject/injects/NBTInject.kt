package tororo1066.uniqueitemprovider.inject.inject.injects

import de.tr7zw.nbtapi.NBT
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import tororo1066.tororopluginapi.sItem.SItem
import tororo1066.uniqueitemprovider.inject.AbstractInjectorData
import tororo1066.uniqueitemprovider.inject.AbstractInjectorSettingsMenu
import tororo1066.uniqueitemprovider.inject.ItemInjector
import tororo1066.uniqueitemprovider.inject.inject.AbstractInject
import tororo1066.uniqueitemprovider.inject.inject.AbstractInjectSettingsMenu

object NBTInject: AbstractInject<NBTInject.NBTInjectData>() {

    override val name: String = "NBTInject"
    override val description: String = "アイテムに特定のNBTが含まれているか/含まれていないかどうかで注入を行う"
    override val icon: Material = Material.COMPARATOR

    private val emptyNBT = NBT.createNBTObject()

    override fun canInject(itemStack: ItemStack, data: NBTInjectData): Boolean {
        try {
            val requiredNBT = data.requiredNBT?.let { NBT.parseNBT(it) }
            val forbiddenNBT = data.forbiddenNBT?.let { NBT.parseNBT(it) }
            val nbt = NBT.itemStackToNBT(itemStack)

            if (requiredNBT != null) {
                val difference = requiredNBT.extractDifference(nbt)
                if (difference != emptyNBT) {
                    return false
                }
            }
            if (forbiddenNBT != null) {
                val difference = forbiddenNBT.extractDifference(nbt)
                if (difference != forbiddenNBT) { // forbiddenNBTのどれかの要素がnbtに存在している場合、differenceはforbiddenNBTから存在している要素が抜かれたものになるため、forbiddenNBTと一致しない
                    return false
                }
            }
            return true
        } catch (_: Exception) {
            return false
        }
    }

    override fun createSettingsMenu(
        injector: ItemInjector,
        currentData: NBTInjectData
    ): AbstractInjectorSettingsMenu<NBTInjectData> {
        return NBTInjectSettingsMenu(injector, currentData)
    }

    override fun createData(): NBTInjectData {
        return NBTInjectData()
    }

    class NBTInjectData: AbstractInjectorData() {
        var requiredNBT: String? = null
        var forbiddenNBT: String? = null

        override fun load(configurationSection: ConfigurationSection) {
            requiredNBT = configurationSection.getString("requiredNBT")
            forbiddenNBT = configurationSection.getString("forbiddenNBT")
        }

        override fun save(configurationSection: ConfigurationSection) {
            configurationSection.set("requiredNBT", requiredNBT)
            configurationSection.set("forbiddenNBT", forbiddenNBT)
        }
    }

    class NBTInjectSettingsMenu(
        injector: ItemInjector,
        currentData: NBTInjectData
    ): AbstractInjectSettingsMenu<NBTInjectData>(
        injector,
        this@NBTInject,
        currentData
    ) {
        override fun renderMenu(p: Player): Boolean {
            super.renderMenu(p)

            setItem(
                20,
                createNullableInputItem(
                    SItem(Material.NAME_TAG)
                        .setDisplayName("§c必要なNBT")
                        .addLore("§d現在の値: §e${currentData.requiredNBT ?: "未設定"}")
                        .addLore("§bNBTを文字列で指定します。例: {components:{\"minecraft:custom_data\":{my_plugin:{my_key:1}}}}")
                        .addLore("§c注: マッチした全てのアイテムに適用されるため、正規表現の指定には注意してください"),
                    String::class.java,
                    message = "NBTを入力してください"
                ) { str, _ ->
                    if (str.isNullOrEmpty()) {
                        currentData.requiredNBT = null
                    } else {
                        try {
                            NBT.parseNBT(str)
                            currentData.requiredNBT = str
                        } catch (e: Exception) {
                            p.sendMessage("§cNBTのパースに失敗しました: ${e.message}")
                        }
                    }
                }
            )

            setItem(
                24,
                createNullableInputItem(
                    SItem(Material.BARRIER)
                        .setDisplayName("§c存在してはいけないNBT")
                        .addLore("§d現在の値: §e${currentData.forbiddenNBT ?: "未設定"}")
                        .addLore("§bNBTを文字列で指定します。例: {components:{\"minecraft:custom_data\":{my_plugin:{my_key:1}}}}")
                        .addLore("§c注: マッチした全てのアイテムに適用されるため、正規表現の指定には注意してください"),
                    String::class.java,
                    message = "NBTを入力してください"
                ) { str, _ ->
                    if (str.isNullOrEmpty()) {
                        currentData.forbiddenNBT = null
                    } else {
                        try {
                            NBT.parseNBT(str)
                            currentData.forbiddenNBT = str
                        } catch (e: Exception) {
                            p.sendMessage("§cNBTのパースに失敗しました: ${e.message}")
                        }
                    }
                }
            )

            return true
        }
    }
}