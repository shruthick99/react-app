package com.mss.checkin.jwt;

import java.net.URL;

import java.util.*;

import com.mss.checkin.entity.CheckIn;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
@Tag(name = "Authentication", description = "Endpoint to get jwt token")
public class JwtAuthenticationController {

	//@Autowired
	//public JwtAuthenticationController(AWSSSMClient aWSSSMClient) {
	//this.password = aWSSSMClient.getJWTPasswordDecrypt();
	//}
	@Autowired
	JdbcTemplate jdbcTemplate;


	@Autowired
	PasswordUtility passwordUtility;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserDetailsService jwtInMemoryUserDetailsService;

	@Value("${jwt.password.decrypt}")
	private String password;

	@Autowired
	HttpServletRequest request;
	@Value("${jwt.secret}")
	private String username;

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	@CrossOrigin
	@Operation(hidden = true)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {


		// public ResponseEntity<?> createAuthenticationToken(JwtRequest authenticationRequest) throws Exception {


		System.out.println("In Authenticate");
		// authenticate(authenticationRequest.getUsername(),
		// authenticationRequest.getPassword());
		// authenticate(authenticationRequest.getUsername(), password);
		System.out.println("After  Authenticate");
		// System.out.println(authenticationRequest.getUsername()+" ----->"+
		// authenticationRequest.getPassword());

		final UserDetails userDetails = jwtInMemoryUserDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());
		Map<String, Object> claims = new HashMap<>();
		String loginId = authenticationRequest.getLoginId();
		String ipAddress = authenticationRequest.getIpAddress();
		claims.put("loginId", loginId);
		claims.put("ipAddress", ipAddress);
		claims.put("empId", authenticationRequest.getEmpId());

		// final String token = jwtTokenUtil.generateToken(userDetails);
		final String token = jwtTokenUtil.generateToken(userDetails, claims);

		return ResponseEntity.ok(new JwtResponse(token));
	}

	private void authenticate(String username, String password) throws Exception {
		Objects.requireNonNull(username);
		Objects.requireNonNull(password);

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}

	@CrossOrigin
	@Operation(summary = "Users endpoint", security = @SecurityRequirement(name = "bearerAuth"))
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public List  test(){
		List usersList = new ArrayList();
		Map usersMap = new HashMap();
		usersMap.put("loginId","skola2");
		usersMap.put("name","Santhosh Kumar Kola");
		usersList.add(usersMap);
		usersMap = new HashMap();
		usersMap.put("loginId","atewari");
		usersMap.put("name","Agam Tewari");
		usersList.add(usersMap);
		return usersList;
}
	@Operation(
			summary = "To get authentication token",
			description = "Here you will get the authentication token by passing login id and password",
			tags = { "Authentication" },
			responses = {
					@ApiResponse(
							description = "Success",
							responseCode = "200",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtAuthenticationController.class))
					),
					@ApiResponse(description = "Not found", responseCode = "404", content = @Content),
					@ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
			}

	)

	@CrossOrigin
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Map doEmployeeLogin(@RequestBody Map<String, String> userDetails) throws Exception {

		Map empLoginData = doGetToken(userDetails);

		/*
		 * if (empLoginData.isEmpty()) { throw new
		 * ResourceNotFoundException("Provide valid credentials for this request"); }
		 */
		return empLoginData;
	}

	public Map doGetToken(Map<String, String> userDetails) {
		List<Map<String, Object>> empData = null;

		Map<String, Object> response = new HashMap<String, Object>();

		Map empMap = new HashMap();
		URL urlInput = null;
		String password = "";

		response.put("success", false);
		response.put("message", "Invalid Inputs!");
		response.put("data", empMap);
		try {
			String uname = "";
			String pwd = "";
			if (userDetails.containsKey("loginId"))
				uname = (String) userDetails.get("loginId");
			if (userDetails.containsKey("password"))
				pwd = (String) userDetails.get("password");

			if (uname != null && !"".equals(uname) && pwd != null && !"".equals(pwd)) {

				//String decryptedUserName = passwordUtility.decryption(uname);
			//	String decryptedPassword = passwordUtility.decryption(pwd);

				if (uname == null || "".equals(uname)) {
					response.put("success", false);
					response.put("message", "Invalid username");
					return response;
				}

				if (pwd == null || "".equals(pwd)) {
					response.put("success", false);
					response.put("message", "Invalid password");
					return response;
				}

				String sql = "SELECT `Id`,`LoginId`,`Name`,`Password` FROM `tblUsers` WHERE `Status`='A' AND `LoginId`=?";

				empData = jdbcTemplate.queryForList(sql, uname.trim());

				String ip = request.getHeader("X-FORWARDED-FOR");
				if (ip == null || "".equals(ip)) {
					ip = request.getRemoteAddr();
				}

				// String token = apiUtility.generateToken();

				if (empData != null && !empData.isEmpty()) {

					for (Map<String, Object> row : empData) {

					//	String email = (String) row.get("Email1");
						// salesAccess

						// if (dataSourceDataProvider.uatAccess(email)) {

						if (PasswordUtility.decryptPwd((String) row.get("Password")).equals(pwd)) {
							String token = generateToken(uname.toLowerCase().trim(), ip);
							if (token != null && !"".equals(token)) {

								response.put("success", true);
								response.put("message", "Successfully logged in!");
								// response.put("data", empMap);
								response.put("token", token);

							} else {
								response.put("message", "Oops! something went wrong please try again!");
							}
						} else {
							// response.put("message", "Wrong credentials!");
							response.put("message",
									"Your username (or) password is incorrect, please check and try again!");
						}
						/*
						 * } else { response.put("message", "Unauthorized  to access Hubble UAT!"); }
						 */
					}
				} else {
					response.put("message", "Your username (or) password is incorrect, please check and try again!");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}


	// public String generateToken(String uname,String ip) {
	public String generateToken(String uname, String ipAddress) {

	//	String local_url = propertyUtility.getTokenUrl();
		 String uri = "http://localhost:8080/authenticate";
		String token = "";
		//String uri = uri + "/authenticate";
		RestTemplate restTemplate = new RestTemplate();
		String user = null;
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		try {
			JSONObject jsonCredentials = new JSONObject();
			// jsonCredentials.put("password", "password");
			jsonCredentials.put("username", username);
			jsonCredentials.put("loginId", uname);
			jsonCredentials.put("ipAddress", ipAddress);

			String empDataQuery = "SELECT `Id`,`LoginId`,`Name`,`Password` FROM `tblUsers` WHERE `Status`='A' AND `LoginId`=? ";

			List<Map<String, Object>> empDataList = jdbcTemplate.queryForList(empDataQuery, uname);
System.out.println("empDataList --"+empDataList);
			for (Map<String, Object> emp : empDataList) {
				System.out.println("emp "+emp.toString());
				jsonCredentials.put("empId", emp.get("Id"));

				jsonCredentials.put("Name", "");
				if (emp.get("Name") != null && !"".equals(emp.get("Name").toString().trim())) {
					jsonCredentials.put("Name", emp.get("Name"));
				}


				HttpEntity<String> entityCredentials = new HttpEntity<String>(jsonCredentials.toString(), httpHeaders);
				ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, entityCredentials,
						String.class);

				// ResponseEntity<String> responseEntity = 	createAuthenticationToken(jsonCredentials);
				if (responseEntity != null) {
					user = responseEntity.getBody();
				}



				JSONObject json = new JSONObject(user);
				if (json.has("token")) {
					token = (String) json.get("token");
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return token;
	}


}
