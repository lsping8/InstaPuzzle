package fusion.com.soicalrpgpuzzle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian on 7/14/2016.
 */
public class Challenger {

    public List<String> challengerList;

    public Challenger() {
        challengerList = new ArrayList<>();
    }

    public Challenger(List<String> challengerList) {
        this.challengerList = challengerList;
    }

    public List<String> getChallengerList() {
        return challengerList;
    }
}
