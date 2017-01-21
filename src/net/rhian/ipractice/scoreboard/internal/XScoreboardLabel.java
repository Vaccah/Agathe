package net.rhian.ipractice.scoreboard.internal;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.io.netty.util.internal.ConcurrentSet;

//a scoreboard line
public class XScoreboardLabel implements XLabel {

    @Getter
    private final XScoreboard scoreboard;
    @Getter
    private int score;
    @Getter
    private String value;
    @Getter
    @Setter
    private boolean visible = true;
    @Getter
    @Setter
    private String lastValue = "";
    @Getter
    @Setter
    private boolean updated = false;
    @Getter
    private Set<XRemoveLabel> toRemove = new ConcurrentSet<>();

    public XScoreboardLabel(XScoreboard scoreboard, int score, String value) {
        this.scoreboard = scoreboard;
        this.score = score;
        this.value = value;
        this.lastValue = value;
    }

    public final void setValue(String value){
        this.lastValue = this.value;
        this.value = value;
        toRemove.add(new XRemoveLabel(this.value,this.lastValue,this.score,this.visible));
    }

    public void update(){
        scoreboard.updateLabel(this);
    }

    public final void setScore(int score){
        scoreboard.updateScore(this,score,this.score);
        this.score = score;
    }

}