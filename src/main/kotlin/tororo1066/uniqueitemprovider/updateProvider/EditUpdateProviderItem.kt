package tororo1066.uniqueitemprovider.updateProvider

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.defaultMenus.LargeSInventory
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.sItem.SItem
import tororo1066.uniqueitemprovider.UniqueItem
import java.io.File

class EditUpdateProviderItem(private val data: UpdateProviderItem? = null): SInventory(SJavaPlugin.plugin, "Edit Update Provider Item", 6) {

    var itemStack = data?.itemStack ?: ItemStack(Material.STONE)
    val ignores = data?.ignores ?: arrayListOf()

    init {
        setOnClick {
            it.isCancelled = true
            if (it.clickedInventory !is PlayerInventory) return@setOnClick
            val item = it.currentItem ?: return@setOnClick
            if (item.isSimilar(itemStack)) return@setOnClick
            itemStack = item.clone().apply {
                amount = 1
            }
            renderMenu(it.whoClicked as Player)
        }
    }

    override fun renderMenu(p: Player): Boolean {
        fillItem(SInventoryItem(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").setCanClick(false))

        removeItem(24)
        setItem(24, SInventoryItem(itemStack).setCanClick(false))
        setItems(listOf(14, 15, 16, 23, 25, 32, 33, 34), SInventoryItem(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" ").setCanClick(false))

        setItem(29, SInventoryItem(Material.LAPIS_BLOCK)
            .setDisplayName("§9アップデートしない項目を設定する")
            .setCanClick(false)
            .setClickEvent {
                val inv = object : LargeSInventory("§9アップデートしない項目を設定する") {
                    override fun renderMenu(): Boolean {
                        val items = arrayListOf<SInventoryItem>()
                        ItemCopy.copies.forEach { (name, copy) ->
                            val enable = ignores.contains(copy)
                            items.add(
                                SInventoryItem(if (enable) Material.EMERALD_BLOCK else Material.REDSTONE_BLOCK)
                                    .setDisplayName("§9$name")
                                    .setLore("§d現在の値: §e${if (enable) "§f[§a有効§f]" else "§f[§c無効§f]"}")
                                    .setCanClick(false)
                                    .setClickEvent {
                                        if (ignores.contains(copy)) {
                                            ignores.remove(copy)
                                        } else {
                                            ignores.add(copy)
                                        }
                                        allRenderMenu()
                                    }
                            )
                        }

                        setResourceItems(items)
                        return true
                    }
                }

                moveChildInventory(inv, p)
            })

        fun setKeyMeta(itemStack: ItemStack, key: String) {
            UniqueItem.getOrCreate(itemStack).addProvider(UpdateProvider().also { it.key = key })
        }

        setItems(45..53, SInventoryItem(SItem(Material.LIME_STAINED_GLASS_PANE))
            .setDisplayName("§a保存する")
            .setClickEvent {
                if (data == null) {
                    p.closeInventory()
                    SJavaPlugin.sInput.sendInputCUI(p, String::class.java, "キーを入力してください(/cancelでキャンセル)", action = { key ->
                        if (UpdateProviderItem.items.containsKey(key)) {
                            p.sendMessage("§cそのキーは既に使われています")
                            open(p)
                            return@sendInputCUI
                        }
                        SJavaPlugin.sInput.sendInputCUI(p, String::class.java, "保存先を入力してください((folder/file)のように指定する,/cancelでキャンセル)", action = { path ->
                            UpdateProviderItem.items[key] = UpdateProviderItem(
                                key,
                                itemStack.clone().also { setKeyMeta(it, key) },
                                File(SJavaPlugin.plugin.dataFolder, "updateProvider/$path.yml").path,
                                ignores
                            ).also { it.save() }
                            p.sendMessage("§a保存しました")
                        }, onCancel = {
                            open(p)
                        })
                    }, onCancel = {
                        open(p)
                    })
                } else {
                    data.itemStack = itemStack.clone().also { setKeyMeta(it, data.key) }
                    data.save()
                    p.sendMessage("§a保存しました")
                    p.closeInventory()
                }
            })

        return true
    }
}