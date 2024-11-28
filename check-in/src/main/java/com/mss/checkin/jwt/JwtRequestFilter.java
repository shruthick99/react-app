package com.mss.checkin.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.PrematureJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");

		String username = null;
		String jwtToken = null;
		String insertQuery ="";
		String api ="";
		String loginId ="";
		String tokenIpAdress ="";
		String apiIpAdress = "";
		
		
	
		
		// JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
				Claims claims = Jwts.parser().setSigningKey(username).parseClaimsJws(jwtToken).getBody();
				 loginId = (String) claims.get("loginId");
				 tokenIpAdress = (String) claims.get("ipAddress");
				//System.out.println("tokenIpAdress: " + claims.get("ipAddress"));
				 //api = request.getRequestURL().toString();
				 api = request.getMethod().toString()+" "+request.getRequestURL().toString();

				 apiIpAdress = request.getHeader("X-FORWARDED-FOR");	
				if (apiIpAdress == null || "".equals(apiIpAdress)) {
					apiIpAdress = request.getRemoteAddr();
				}	
				
				
			
				
				
				
				
			} 
			
			catch (MalformedJwtException e) {
				System.out.println("JWT Token not valid");
			}catch (PrematureJwtException e) {
				System.out.println("JWT Token was pre matured");
			}
			catch (SignatureException e) {
				System.out.println("JWT Token verification is failed");
			}
			catch (UnsupportedJwtException e) {
				System.out.println("JWT Token not supported for this application");
			}
			
			catch (IllegalArgumentException e) {
				System.out.println("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				System.out.println("JWT Token has expired");
			}
		} else {
			logger.warn("JWT Token does not begin with Bearer String");
		}

		//Once we get the token validate it.
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

			// if token is valid configure Spring Security to manually set authentication
			if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
			//	int result = namedParameterJdbcTemplate.update(insertQuery, in);
			//	 insertQuery = "INSERT INTO tblUserLogV2(LoginId,tokenIpAddress,ApiIpAddress,Api) VALUES(?,?,?,?)";
			//		int result = jdbcTemplate.update(insertQuery, loginId,tokenIpAdress,apiIpAdress,api);

				 
				 
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				// After setting the Authentication in the context, we specify
				// that the current user is authenticated. So it passes the Spring Security Configurations successfully.
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		chain.doFilter(request, response);
	}

}
