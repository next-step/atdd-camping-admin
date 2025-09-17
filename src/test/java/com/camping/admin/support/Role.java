package com.camping.admin.support;

public enum Role {
    관리자;

    public Boolean isAdmin() {
        return this == 관리자;
    }
}
