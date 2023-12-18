// package com.adira.contact.service;

// import com.adira.contact.model.User;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Claims.Builder;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;

// import java.util.Date;
// import java.util.Optional;

// @Service
// public class AuthenticationServiceImpl implements AuthenticationService {

//     private final UserRepository userRepository;
//     private final PasswordEncoder passwordEncoder;

//     @Value("${jwt.secret}")
//     private String jwtSecret;

//     @Value("${jwt.expiration}")
//     private int jwtExpiration;

//     public AuthenticationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//         this.userRepository = userRepository;
//         this.passwordEncoder = passwordEncoder;
//     }

//     public Optional<User> authenticateUser(String email, String password) {
//         Optional<User> userOptional = userRepository.findByEmail(email);

//         if (userOptional.isPresent()) {
//             User user = userOptional.get();

//             if (passwordEncoder.matches(password, user.getPassword())) {
//                 return Optional.of(user);
//             }
//         }

//         return Optional.empty();
//     }

//     public User registerUser(String email, String password, boolean admin) {
//         Optional<User> existingUser = userRepository.findByEmail(email);

//         if (existingUser.isPresent()) {
//             return null;
//         }

//         User newUser = new User(email, passwordEncoder.encode(password), admin);
//         return userRepository.save(newUser);
//     }

//     public String generateAccessToken(User user) {
//         return Jwts.builder()
//             .setSubject(user.getEmail())
//             .claim("admin", user.isAdmin())
//             .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration * 1000))
//             .signWith(HS256.fromSecret(jwtSecret))
//             .compact();
//     }

//     public String generateRefreshToken(User user) {
//         return Jwts.builder()
//             .setSubject(user.getEmail())
//             .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration * 2 * 1000))
//             .signWith(HS256.fromSecret(jwtSecret))
//             .compact();
//     }

//     public Claims parseToken(String token) {
//         return Jwts.parser()
//             .setSigningKey(jwtSecret)
//             .parseClaimsJws(token)
//             .getBody();
//     }
// }
