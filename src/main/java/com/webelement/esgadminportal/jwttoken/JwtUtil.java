package com.webelement.esgadminportal.jwttoken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.webelement.esgadminportal.service.TokenBlacklistService;

import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.expiration}")
	private long accessTokenExpiration; // in ms (e.g., 120000 = 2 minutes)

	@Value("${jwt.refreshExpiration}")
	private long refreshTokenExpiration; // in ms (e.g., 604800000 = 7 days)
	
	 @Value("${jwt.expiration}")
	 private long jwtExpirationMillis;

	private final TokenBlacklistService tokenBlacklistService;

	public JwtUtil(TokenBlacklistService tokenBlacklistService) {
		this.tokenBlacklistService = tokenBlacklistService;
	}

	// ================== CLAIM EXTRACTION ==================

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public String extractJti(String token) {
		return extractClaim(token, Claims::getId);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
	}

	// ================== VALIDATION ==================

	public Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Boolean validateToken(String token, String username) {
		final String extractedUsername = extractUsername(token);
		final String jti = extractJti(token);

		// Check blacklist first
		if (tokenBlacklistService.isBlacklisted(jti)) {
			return false;
		}

		return (extractedUsername.equals(username) && !isTokenExpired(token));
	}

	// ================== GENERATION ==================

	public String generateAccessToken(String username) {
		String jti = UUID.randomUUID().toString();
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

		return Jwts.builder().setId(jti).setSubject(username).setIssuedAt(now).setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, secretKey).compact();
	}

	public String generateRefreshToken(String username) {
		String jti = UUID.randomUUID().toString();
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

		return Jwts.builder().setId(jti).setSubject(username).setIssuedAt(now).setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, secretKey).compact();
	}
	
	 public String generateToken(String username) {
   	  String jti = UUID.randomUUID().toString();
   	  Date now = new Date();
   	  Date expiryDate = new Date(now.getTime() + jwtExpirationMillis);
       return Jwts.builder()
//               .setSubject(username)
//               .setIssuedAt(new Date(System.currentTimeMillis()))
//               .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
//               .signWith(SignatureAlgorithm.HS256, secretKey)
//               .compact();
       		 .setId(jti) // store token ID
       		 .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secretKey) // your secret key
                .compact();
   }
}
