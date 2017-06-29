package com.sucy.party;

import org.bukkit.entity.Player;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Parties © 2017
 * com.sucy.party.PartyTest
 */
public class PartyTest {

    private static final int MAX_SIZE = 3;

    private Party subject;

    private Parties plugin;
    private Player  leader;

    @Before
    public void setup() {

        plugin = mock(Parties.class);
        when(plugin.getMaxSize()).thenReturn(MAX_SIZE);
        when(plugin.getInviteTimeout()).thenReturn(1000L);

        leader = mock(Player.class);
        when(leader.getName()).thenReturn("Leader");

        subject = new Party(plugin, leader);
    }

    @Test
    public void emptyParty() {
        Assert.assertFalse(subject.isFull());
        Assert.assertTrue(subject.isEmpty());
        Assert.assertEquals(1, subject.getMembers().size());
        Assert.assertFalse(subject.isInvited(mockPlayer("Leader")));
        Assert.assertTrue(subject.isMember(mockPlayer("Leader")));
        Assert.assertTrue(subject.isLeader(leader));
    }

    @Test
    public void oneInvitee() {
        invite("Jill");

        Assert.assertFalse(subject.isFull());
        Assert.assertFalse(subject.isEmpty());
        Assert.assertEquals(1, subject.getMembers().size());
        Assert.assertTrue(subject.isInvited(mockPlayer("Jill")));
        Assert.assertFalse(subject.isMember(mockPlayer("Jill")));
    }

    @Test
    public void oneMember() {
        addMember("Bob");

        Assert.assertFalse(subject.isFull());
        Assert.assertFalse(subject.isEmpty());
        Assert.assertEquals(2, subject.getMembers().size());
        Assert.assertFalse(subject.isInvited(mockPlayer("Bob")));
        Assert.assertTrue(subject.isMember(mockPlayer("Bob")));
    }

    @Test
    public void oneMemberOneInvitee() {
        addMember("Bob");
        invite("Jill");

        Assert.assertTrue(subject.isFull());
        Assert.assertFalse(subject.isEmpty());
        Assert.assertEquals(2, subject.getMembers().size());
    }

    @Test
    public void isFull_afterReject() {
        addMember("Bob");
        invite("Jill");
        subject.decline(mockPlayer("Jill"));

        Assert.assertFalse(subject.isFull());
        Assert.assertFalse(subject.isEmpty());
        Assert.assertEquals(2, subject.getMembers().size());
        Assert.assertFalse(subject.isInvited(mockPlayer("Jill")));
        Assert.assertFalse(subject.isMember(mockPlayer("Jill")));
    }

    private void addMember(String name) {
        Player member = mockPlayer(name);
        subject.invite(member);
        subject.accept(member);
    }

    private void invite(String name) {
        Player member = mockPlayer(name);
        subject.invite(member);
    }

    private Player mockPlayer(String name) {
        Player player = mock(Player.class);
        when(player.getName()).thenReturn(name);
        return player;
    }
}
