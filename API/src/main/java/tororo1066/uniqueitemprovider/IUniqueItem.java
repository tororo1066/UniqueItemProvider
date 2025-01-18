package tororo1066.uniqueitemprovider;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IUniqueItem {
    @NotNull ItemStack getItemStack();
    void setItemStack(@NotNull ItemStack itemStack);

    @NotNull List<AbstractItemProvider> getProviders();
    void setProviders(@NotNull List<AbstractItemProvider> providers);

    void addProvider(@NotNull AbstractItemProvider provider);
}