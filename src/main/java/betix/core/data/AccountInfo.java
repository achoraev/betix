package betix.core.data;

import betix.core.config.Stake;
import lombok.Data;

import java.util.Set;
import java.util.TreeSet;

@Data
public class AccountInfo {

    private String username;
    private String password;
    private double balance;
    private Set<MatchInfo> matchInfoPending = new TreeSet<>();
    private Set<MatchInfo> matchInfoFinished = new TreeSet<>();
    private Set<Team> teams = new TreeSet<>();

    public void addPending(MatchInfo info) {
        matchInfoPending.add(info);
        matchInfoFinished.remove(info);

        info.getEvent().getFirstTeam().setStake(info.getStake());
        info.getEvent().getSecondTeam().setStake(info.getStake());
    }

    public void addFinished(MatchInfo info) {
        matchInfoFinished.add(info);
        matchInfoPending.remove(info);

        info.getEvent().getFirstTeam().setStake(info.getStake());
        info.getEvent().getSecondTeam().setStake(info.getStake());

        setStakes(info);
    }

    private void setStakes(MatchInfo matchInfo) {
        setStake(matchInfo, matchInfo.getEvent().getFirstTeam());
        setStake(matchInfo, matchInfo.getEvent().getSecondTeam());
    }

    private void setStake(MatchInfo matchInfo, Team team) {
        if (this.contains(team.getName())) {
            team = getTeam(team.getName());

            for (MatchInfo info : matchInfoFinished) {
                if (info.getEvent().isParticipant(team.getName())) {
                    if (MatchState.losing.equals(matchInfo.getState())) {
                        team.calculateStakes(Stake.get(matchInfo.getStake()));
                    } else {
                        team.calculateStakes(Stake.noStake);
                    }
                    break;
                }
            }
        }
    }

    public Team getTeam(String name) {
        Team team = new Team(name);
        for (Team t : teams) {
            if (t.equals(team)) {
                return t;
            }
        }
        return team;
    }

    public boolean contains(String name) {
        for (Team t : teams) {
            if (t.isSame(name)) {
                return true;
            }
        }
        return false;
    }

}
