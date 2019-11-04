package com.chenu.pvcstumansys.db.bean;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 用户信息
 */
public class User implements UserDetails {

    /**
     * 外键映射
     */
    private UInfo uinfo;
    public void setUinfo(UInfo uinfo) {
        this.uinfo = uinfo;
    }
    public UInfo getUinfo() {
        return uinfo;
    }

    private Integer pUid;

    private String name;

    private String pwd;

    private Byte role;

    private Byte privilege;

    private Integer fInfo;

    private boolean enabled = false;                        /* 账号可用情况 */

    private Collection<GrantedAuthority> authorities;       /* 账号所属角色集合 */

    /**
     * 一些常用字段
     */
    public static final int ROLE_ROOT = 0;              /* 唯一的省级root用户 */
    public static final int ROLE_NOR = 1;               /* 校级root用户 */
    public static final int PRIVILEGE_SHENG = 0;        /* 省级权限 */
    public static final int PRIVILEGE_XIAO = 1;         /* 校级权限 */


    public User(){

    }

    public User(Integer pUid, String name, String pwd, Byte role, Byte privilege, Integer fInfo) {
        this.pUid = pUid;
        this.name = name;
        this.pwd = pwd;
        this.role = role;
        this.privilege = privilege;
        this.fInfo = fInfo;
    }

    public Integer getpUid() {
        return pUid;
    }

    public void setpUid(Integer pUid) {
        this.pUid = pUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Byte getRole() {
        return role;
    }

    public void setRole(Byte role) {
        this.role = role;
    }

    public Byte getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Byte privilege) {
        this.privilege = privilege;
    }

    public Integer getfInfo() {
        return fInfo;
    }

    public void setfInfo(Integer fInfo) {
        this.fInfo = fInfo;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAuthorities(Collection<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String toString() {
        return "User{" +
                "pUid=" + pUid +
                ", name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                ", role=" + role +
                ", privilege=" + privilege +
                ", fInfo=" + fInfo +
                ", enabled=" + enabled +
                ", authorities=" + authorities +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", accountNonExpired=" + accountNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                ", credentialsNonExpired=" + credentialsNonExpired +
                '}';
    }

    /**
     * 得到权限
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    private String password;

    public void setPassword(String password) {
        this.password = pwd;
    }

    @Override
    public String getPassword() {
        return pwd;
    }

    private String username;

    public void setUsername(String username) {
        this.username = name;
    }

    @Override
    public String getUsername() {
        return name;
    }

    /**
     * 帐户是否没有过期
     */
    private Boolean accountNonExpired = true;

    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    /**
     * 帐户是否没有冻结
     */
    private Boolean accountNonLocked = true;

    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    /**
     *  密码是否没有过期，一般有的密码要求性高的系统会使用到，比较每隔一段时间就要求用户重置密码
     */
    private Boolean credentialsNonExpired = true;

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    /**
     * 帐号是否可用
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}