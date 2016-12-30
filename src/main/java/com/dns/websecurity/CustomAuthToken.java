package com.dns.websecurity;

import java.util.Set;

/**
 * This is custom class for create auth token
 * Created by dhiren on 29/12/16.
 * @author dhiren
 * @since 29-12-2016
 * @see RestTokenHandlerService
 */
public class CustomAuthToken {

    private String username;
    private Set<String> roles;
    private long loginTime;
    private Boolean expired;
    private final long expiryTimeInMileSecond = 1800000; //Token expiry time in Millis

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public long getExpiryTimeInMileSecond() {
        return expiryTimeInMileSecond;
    }

    public Boolean getExpired() {
        return System.currentTimeMillis() > this.loginTime + this.expiryTimeInMileSecond;
    }
}
