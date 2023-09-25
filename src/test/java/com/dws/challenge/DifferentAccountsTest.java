package com.dws.challenge;

import com.dws.challenge.domain.Transfer;
import com.dws.challenge.validators.differentAccounts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
class DifferentAccountsTest {
    private final differentAccounts uut = new differentAccounts();

    @Test
    void isValid() {
        final Transfer notOkTransfer = new Transfer("ac1", "ac1", new BigDecimal(100));
        assertFalse(uut.isValid(notOkTransfer, null));

        final Transfer okTransfer = new Transfer("ac1", "ac2", new BigDecimal(100));
        assertTrue(uut.isValid(okTransfer, null));
    }
}