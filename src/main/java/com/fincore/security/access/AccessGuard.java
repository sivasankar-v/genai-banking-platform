package com.fincore.security.access;

import com.fincore.entity.Account;
import com.fincore.entity.Customer;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Ownership checks for the logged-in user. Admins may act on any record;
 * customers only on the customer profile linked to their login and what hangs off it.
 */
@Component
public class AccessGuard {

    public static final String NOT_ALLOWED = "You do not have access to this resource";

    public String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException(NOT_ALLOWED);
        }
        return auth.getName();
    }

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    public boolean owns(Customer customer) {
        return customer != null
                && customer.getUser() != null
                && customer.getUser().getEmail().equalsIgnoreCase(currentEmail());
    }

    public void checkCustomer(Customer customer) {
        if (!isAdmin() && !owns(customer)) {
            throw new AccessDeniedException(NOT_ALLOWED);
        }
    }

    public void checkAccount(Account account) {
        checkCustomer(account.getCustomer());
    }
}
