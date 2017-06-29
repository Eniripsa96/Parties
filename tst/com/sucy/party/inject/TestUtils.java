package com.sucy.party.inject;

import static org.mockito.Mockito.mock;

/**
 * Parties © 2017
 * com.sucy.party.inject.TestUtils
 */
public class TestUtils {

    public static void nullServerMock() {
        Server.setContext(mock(InjectInterface.class));
    }
}
