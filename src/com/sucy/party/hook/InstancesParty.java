/**
 * Parties
 * com.sucy.party.hook.InstancesParty
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.party.hook;

import com.rit.sucy.config.Filter;
import com.sucy.party.IParty;
import com.sucy.party.Parties;
import com.sucy.party.lang.PartyNodes;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ExpSource;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import org.bukkit.entity.Player;
import org.cyberiantiger.minecraft.instances.Party;

import java.util.List;

public class InstancesParty implements IParty
{
    private Parties plugin;
    private Party party;

    private int nextId = -1;

    public InstancesParty(Parties plugin, Party party)
    {
        this.plugin = plugin;
        this.party = party;
    }

    public Party getParty()
    {
        return party;
    }

    @Override
    public Player getSequentialPlayer()
    {
        nextId = (nextId + 1) % party.getMembers().size();
        return party.getMembers().get(nextId);
    }

    @Override
    public Player getRandomPlayer()
    {
        return party.getMembers().get((int)(party.getMembers().size() * Math.random()));
    }

    @Override
    public void giveExp(Player source, double amount, ExpSource expSource)
    {
        if (isEmpty()) return;

        // Member modifier
        double baseAmount = amount / (1 + (party.getMembers().size() - 1) * plugin.getMemberModifier());
        PlayerData data = SkillAPI.getPlayerData(source);
        PlayerClass main = data.getMainClass();
        int level = main == null ? 0 : main.getLevel();

        // Grant exp to all members
        for (Player member : party.getMembers()) {

            // Player must be online
            PlayerData info = SkillAPI.getPlayerData(member);
            main = info.getMainClass();
            int lvl = main == null ? 0 : main.getLevel();
            int exp = (int)Math.ceil(baseAmount);

            // Level modifier
            if (plugin.getLevelModifier() > 0) {
                int dl = lvl - level;
                exp = (int)Math.ceil(baseAmount * Math.pow(2, -plugin.getLevelModifier() * dl * dl));
            }

            info.giveExp(exp, expSource);
        }
    }

    /**
     * Sends a message to all members in the party
     *
     * @param sender  the player who sent a party message
     * @param message message the player typed
     */
    @Override
    public void sendMessage(Player sender, String message) {
        List<String> messages = plugin.getMessage(PartyNodes.CHAT_MESSAGE, true, Filter.PLAYER.setReplacement(sender.getName()), Filter.MESSAGE.setReplacement(message));
        for (String line : messages)
            party.sendAll(line);
    }

    /**
     * Checks whether or not no players are left in the party
     *
     * @return true if empty
     */
    @Override
    public boolean isEmpty()
    {
        return party.getMembers().size() == 0;
    }
}
