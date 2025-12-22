package com.jujutsu.systems.talent;

import com.jujutsu.event.resource.TalentBranchesResourceLoader;
import com.jujutsu.event.resource.TalentResourceLoader;
import net.minecraft.util.Identifier;

import java.util.Map;

public class TalentTreeValidator {
    private final TalentTree tree;

    public TalentTreeValidator(TalentTree tree) {
        this.tree = tree;
    }

    public boolean validate(Identifier workingBranch, Identifier workingTalent, TalentComponent user) {
        return containsBranch(workingBranch) &&
                branchContainsUpgrade(workingBranch, workingTalent) &&
                enoughPoints(user.points(), workingTalent) &&
                isNextBranch(user.lastPurchasedBranch(), workingBranch) &&
                branchUpgradesNotPurchased(workingBranch, user.purchasedTalents());
    }

    public boolean containsBranch(Identifier branchId) {
        for(Identifier branch : tree.branches()) {
            if(branch.equals(branchId)) {
                return true;
            }
        }
        return false;
    }

    public boolean branchContainsUpgrade(Identifier branchId, Identifier talentId) {
        TalentBranch branch = TalentBranchesResourceLoader.getInstance().get(branchId);

        for(Identifier upgrade : branch.talents()) {
            if(upgrade.equals(talentId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isNextBranch(Identifier previous, Identifier branch) {
        if(previous == null || tree.branches().getFirst().equals(branch)) return true;

        for(int i = 0; i < tree.size(); i++) {
            if(i == tree.size() - 1) continue;

            if(tree.branches().get(i).equals(previous) && tree.branches().get(i + 1).equals(branch)) {
                return true;
            }
        }

        return false;
    }

    public boolean enoughPoints(int userPoints, Identifier talentId) {
        AbilityTalent talent = TalentResourceLoader.getInstance().get(talentId);
        return userPoints >= talent.cost();
    }

    public boolean branchUpgradesNotPurchased(Identifier branchId, Map<Identifier, Identifier> userPurchasedTalents) {
        return userPurchasedTalents.containsKey(branchId);
    }
}
