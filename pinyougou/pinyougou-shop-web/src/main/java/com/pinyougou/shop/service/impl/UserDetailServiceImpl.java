package com.pinyougou.shop.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;


/**
 * 动态认证授权
 */
public class UserDetailServiceImpl implements UserDetailsService {

    @Reference
    private SellerService sellerService;

    /**
     * 根据用户名查询用户信息
     * @param username 用户在前端页面中输入的用户名
     * @return 用户信息
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       // 角色权限集合:authorities，GrantedAuthority
       List<GrantedAuthority> authorities = new ArrayList<>();
       authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        TbSeller seller = sellerService.findOne(username);
        if (seller!=null && "1".equals(seller.getStatus())){
            //说明商家存在--用户名，密码，权限
            return new User(username,seller.getPassword(),authorities);
        }
        return null;
    }
}
