package net.rhian.ipractice.ladder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import net.rhian.ipractice.Practice;
import net.rhian.ipractice.kite.KiteMatch;
import net.rhian.ipractice.player.IPlayer;
import net.rhian.ipractice.queue.QueueType;
import net.rhian.ipractice.queue.member.QueueMember;
import net.rhian.ipractice.util.ItemBuilder;

public abstract class LadderSelect implements Listener {

    @Getter private IPlayer p;

    @Getter private Inventory inv;

    public LadderSelect(IPlayer p) {
        this.p = p;
        Bukkit.getServer().getPluginManager().registerEvents(this, Practice.getPlugin());

        inv = Bukkit.createInventory(null, 9, ChatColor.BLUE+"Select a Ladder");

        for(Ladder ladder : Ladder.getLadders()){
            if(ladder.getName().equalsIgnoreCase(KiteMatch.KITE_LADDER_CHASER)
                    || ladder.getName().equalsIgnoreCase(KiteMatch.KITE_LADDER_RUNNER)) continue;
            inv.addItem(new ItemBuilder(ladder.getIcon()).name(ChatColor.AQUA+ladder.getName()).build());
        }

        p.getPlayer().openInventory(inv);
    }

    public LadderSelect(IPlayer p, QueueType queueType) {
        this.p = p;
        Bukkit.getServer().getPluginManager().registerEvents(this, Practice.getPlugin());

        inv = Bukkit.createInventory(null, 9, ChatColor.BLUE+"Select a Ladder");

        for(Ladder ladder : Ladder.getLadders()){
            if(ladder.getName().equalsIgnoreCase(KiteMatch.KITE_LADDER_CHASER)
                    || ladder.getName().equalsIgnoreCase(KiteMatch.KITE_LADDER_RUNNER)) continue;
            int playersIn = 0;
            for(QueueMember queueMember : Practice.getQueueManager().getQueues().get(queueType).getMembers()){
                if(queueMember.getLadder().getName().equalsIgnoreCase(ladder.getName())){
                    playersIn++;
                }
            }
            inv.addItem(new ItemBuilder(ladder.getIcon()).name(ChatColor.AQUA + ladder.getName())
                    .lore(ChatColor.GREEN+ladder.getName() + " ELO: "+ChatColor.YELLOW+p.getElo(ladder))
                    .lore(ChatColor.GRAY + "Players in " + WordUtils.capitalizeFully(queueType.toString().replaceAll("_", " "))
                            + " " + ladder.getName() + " queue: " + ChatColor.AQUA + playersIn)
                    .lore(ChatColor.GRAY + "Players in " + ladder.getName() + " matches: " + ChatColor.AQUA +
                            Practice.getMatchManager().getAmountOfPlayersInMatches(ladder))
                    .build());
        }

        p.getPlayer().openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player)) return;
        if(((Player)e.getWhoClicked()).getName().equals(p.getName())){
            if(e.getInventory().getName().equalsIgnoreCase(ChatColor.BLUE+"Select a Ladder")){
                if(e.getCurrentItem() != null){
                    if(e.getCurrentItem().getType() != Material.AIR){
                        e.setCancelled(true);
                        e.setResult(Event.Result.DENY);
                        p.getPlayer().closeInventory();
                        ItemStack i = e.getCurrentItem();
                        if(i.hasItemMeta() && i.getItemMeta().getDisplayName() != null){
                            String name = ChatColor.stripColor(i.getItemMeta().getDisplayName());
                            if(Ladder.getLadder(name) != null){
                                onSelect(Ladder.getLadder(name));
                                HandlerList.unregisterAll(this);
                                return;
                            }
                            else{
                                p.getPlayer().sendMessage(ChatColor.RED+"Could not find a ladder that matches that");
                                HandlerList.unregisterAll(this);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if(e.getPlayer() instanceof Player){
            Player p = (Player) e.getPlayer();
            if(this.p.getName().equals(p.getName())){
                if(e.getInventory().getName().equalsIgnoreCase(ChatColor.BLUE+"Select a Ladder")){
                    HandlerList.unregisterAll(this);
                }
            }
        }
    }

    public abstract void onSelect(Ladder ladder);

}