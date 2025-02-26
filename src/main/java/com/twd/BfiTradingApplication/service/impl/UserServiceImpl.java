//package com.twd.BfiTradingApplication.service.impl;
//
//import com.twd.BfiTradingApplication.dto.ReqRes;
//import com.twd.BfiTradingApplication.entity.User;
//import com.twd.BfiTradingApplication.repository.UserRepository;
//import com.twd.BfiTradingApplication.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserServiceImpl implements  {
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return userRepository.findByEmail(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
//    }
//
//
//
//
//
//}
