package net.rhian.ipractice.queue.type;

import org.bukkit.Material;

import net.rhian.ipractice.Practice;
import net.rhian.ipractice.ladder.Ladder;
import net.rhian.ipractice.match.Match;
import net.rhian.ipractice.match.MatchBuilder;
import net.rhian.ipractice.match.team.PracticeTeam;
import net.rhian.ipractice.match.team.Team;
import net.rhian.ipractice.player.IPlayer;
import net.rhian.ipractice.player.PlayerState;
import net.rhian.ipractice.queue.Queue;
import net.rhian.ipractice.queue.QueueMatchSet;
import net.rhian.ipractice.queue.QueueType;
import net.rhian.ipractice.queue.member.UnrankedPartyQueueMember;
import net.rhian.ipractice.queue.range.UnrankedQueueRange;

/**
 * Created by 360 on 9/12/2015.
 */
public class UnrankedPartyQueue extends Queue implements PartyQueue {

    public UnrankedPartyQueue() {
        super(QueueType.UNRANKED_PARTY);
    }

    @Override
    public Match createMatch(QueueMatchSet set) {
        MatchBuilder builder = Practice.getMatchManager().matchBuilder(set.getLadder());
        builder.setRanked(false);
        builder.registerTeam(new PracticeTeam(set.getAlpha().getName(), Team.ALPHA));
        builder.registerTeam(new PracticeTeam(set.getBravo().getName(), Team.BRAVO));
        for(IPlayer player : set.getAlpha().getPlayers()){
            builder.withPlayer(player.getPlayer(), set.getAlpha().getName());
        }
        for(IPlayer player : set.getBravo().getPlayers()){
            builder.withPlayer(player.getPlayer(), set.getBravo().getName());
        }
        return builder.build();
    }

    @Override
    public void addToQueue(IPlayer player, Ladder ladder) {
        UnrankedPartyQueueMember queueMember = new UnrankedPartyQueueMember(player.getParty(), ladder, new UnrankedQueueRange());
        getMembers().add(queueMember);
    }

    @Override
    public Material getIcon() {
        return Material.GOLD_INGOT;
    }

    @Override
    public boolean canJoin(IPlayer player) {
        return player.getState() == PlayerState.AT_SPAWN && player.getParty() != null &&
                player.getParty().getLeader().equals(player.getName());
    }
}