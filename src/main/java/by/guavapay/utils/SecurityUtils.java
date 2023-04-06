package by.guavapay.utils;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@UtilityClass
public class SecurityUtils {

    public static boolean isUserInRole(String role, Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .anyMatch(a -> role.equals(a.getAuthority()));
    }

    public static boolean isAdmin(Collection<? extends GrantedAuthority> authorities) {
        return isUserInRole("SCOPE_ROLE_ADMIN", authorities);
    }

    public static boolean isCourier(Collection<? extends GrantedAuthority> authorities) {
        return isUserInRole("SCOPE_ROLE_COURIER", authorities);
    }
}