package fusion.com.soicalrpgpuzzle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian on 7/10/2016.
 */
public class MatchDetails {

    public List<Integer> score, condition;
    public List<String> oppFireBaseId, rank, date;

    public MatchDetails() {
        this.score = new ArrayList<>();
        this.oppFireBaseId = new ArrayList<>();
        this.condition = new ArrayList<>();
        this.rank = new ArrayList<>();
        this.date = new ArrayList<>();
    }

    public void setScore(List<Integer> score) {
        this.score = score;
    }

    public List<Integer> getScore() {
        return this.score;
    }

    public void setCondition(List<Integer> condition) {
        this.condition = condition;
    }

    public List<Integer> getCondition() {
        return this.condition;
    }

    public void setOppFireBaseId(List<String> oppFireBaseId) {
        this.oppFireBaseId = oppFireBaseId;
    }

    public List<String> getOppFireBaseId() {
        return this.oppFireBaseId;
    }

    public List<String> getRank() {
        return this.rank;
    }

    public void setRank(List<String> rank) {
        this.rank = rank;
    }

    public void setDate(List<String> date) {
        this.date = date;
    }

    public List<String> getDate() {
        return this.date;
    }






}
