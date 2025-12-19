package com.dewmark.smartcampuscommunity.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
//    /**
//     * 生成jwt
//     * 使用Hs256算法, 私匙使用固定秘钥
//     *
//     * @param secretKey jwt秘钥
//     * @param ttlMillis jwt过期时间(毫秒)
//     * @param claims    设置的信息
//     * @return
//     */
//    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
//        // 指定签名的时候使用的签名算法，也就是header那部分
//        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
//
//        // 生成JWT的时间
//        long expMillis = System.currentTimeMillis() + ttlMillis;
//        Date exp = new Date(expMillis);
//
//        // 设置jwt的body
//        JwtBuilder builder = Jwts.builder()
//                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
//                .setClaims(claims)
//                // 设置签名使用的签名算法和签名使用的秘钥
//                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
//                // 设置过期时间
//                .setExpiration(exp);
//
//        return builder.compact();
//    }
//
//    /**
//     * Token解密
//     *
//     * @param secretKey jwt秘钥 此秘钥一定要保留好在服务端, 不能暴露出去, 否则sign就可以被伪造, 如果对接多个客户端建议改造成多个
//     * @param token     加密后的token
//     * @return
//     */
//    public static Claims parseJWT(String secretKey, String token) {
//        // 得到DefaultJwtParser
//        Claims claims = Jwts.parser()
//                // 设置签名的秘钥
//                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
//                // 设置需要解析的jwt
//                .parseClaimsJws(token).getBody();
//        return claims;
//    }
    /**
     * 生成 JWT
     *
     * @param secretKey  签名密钥（建议至少32位）
     * @param ttlMillis  过期时间（毫秒）
     * @param claims     自定义载荷（如 userId, role 等）
     * @return JWT 字符串
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 将字符串密钥转换为 SecretKey（HMAC-SHA256）
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        // 计算过期时间
        long nowMillis = System.currentTimeMillis();
        Date exp = new Date(nowMillis + ttlMillis);

        return Jwts.builder()
                .claims(claims)           // 设置自定义声明
                .expiration(exp)          // 设置过期时间（替代 setExpiration）
                .signWith(key, SignatureAlgorithm.HS256) // 显式指定算法
                .compact();
    }

    /**
     * 解析 JWT
     *
     * @param secretKey 密钥（必须与生成时一致）
     * @param token     JWT 字符串
     * @return Claims 载荷内容
     * @throws ExpiredJwtException     JWT 已过期
     * @throws MalformedJwtException   JWT 格式错误
     * @throws SignatureException      签名验证失败
     * @throws IllegalArgumentException JWT 为空或无效
     */
    public static Claims parseJWT(String secretKey, String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()
                .verifyWith(key)          // 设置验证密钥
                .build()
                .parseSignedClaims(token) // 解析 JWS（带签名的 JWT）
                .getPayload();            // 获取 payload（即 Claims）
    }
}
