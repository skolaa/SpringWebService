package com.dns.websecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is token handler service. That class main task is manage the token
 * Created by dhiren on 29/12/16.
 * @author dhiren
 * @since 29-12-2016
 * @see RestAuthenticationSuccessHandler
 */
@Component
public class RestTokenHandlerService {

    private static final Logger logger = LoggerFactory.getLogger(RestTokenHandlerService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String key = "dhiren@dns123456";
    private final SecretKey secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
    private Cipher cipher;

    private final CustomAuthToken customAuthToken;

    private RestTokenHandlerService() throws Exception {
        this.cipher = Cipher.getInstance("AES");
        this.customAuthToken = new CustomAuthToken();
    }


    /**
     * This method encrypt jsonString to AES
     * @param jsonString {@link String} as param
     * @return encrypted {@link Byte} array
     */
    private byte[] encryptAES(String jsonString) {
        byte[] encrypted;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encrypted = cipher.doFinal(jsonString.getBytes());
        } catch (Exception ex) {
            logger.error("error in encryption ", ex);
            return null;
        }
        return encrypted;
    }

    /**
     * This method generate token after successfully authentication
     * @param authentication {@link Authentication}
     * @return String as token
     * @throws Exception throw {@link NullPointerException} if authentication is null
     */
    public String generateToken(Authentication authentication) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> auth = new LinkedHashMap<>();
        if (authentication != null)
        {

            customAuthToken.setUsername(((User) authentication.getPrincipal()).getUsername());
            customAuthToken.setRoles(authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));
            customAuthToken.setLoginTime(System.currentTimeMillis());
            String jsonString = objectMapper.writer().writeValueAsString(customAuthToken);
            logger.info("generating token");
            String token = encodeBase64(encryptAES(jsonString));
            auth.put("token", token);
            return objectMapper.writer().writeValueAsString(auth);
        }
        else
        {
            throw  new Exception("Null Authentication");
        }
    }

    /**
     * This method is build authentication using token
     * @param token {@link String} get form {@link RestAuthenticationFilter}
     * @param request {@link HttpServletRequest}
     * @return Authentication
     */
    Authentication buildAuthentication(String token, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = null;
        try {

            String decrypted = new String(decryptAES(decodeBase64(token)));
            CustomAuthToken customAuthToken = objectMapper.readValue(decrypted.getBytes(),CustomAuthToken.class);
            if (customAuthToken.getExpired())
                return authentication;
            logger.info("Token deserialized : " + customAuthToken);
            authentication = new UsernamePasswordAuthenticationToken(customAuthToken.getUsername(),null, AuthorityUtils.createAuthorityList(String.join(",",customAuthToken.getRoles())));
            authentication.setDetails(new WebAuthenticationDetails(request));
        } catch (Exception e) {
            logger.error("Error in token decryption", e);
        }
        return authentication;
    }

    /**
     * This method encrypt AES to normal byte
     * @param dataBytes encrypted AES byte array
     * @return decrypted byte array
     */
    private byte[] decryptAES(byte[] dataBytes) {
        byte[] decrypt;
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            decrypt = cipher.doFinal(dataBytes);
        } catch (Exception ex) {
            logger.error("error in decryption ", ex);
            return null;
        }
        return decrypt;
    }

    /**
     * Encode encrypted AES byte array to encodeBase64 {@link String}
     * @param bytes as param
     * @return String
     */
    private String encodeBase64(byte[] bytes) {
        return new String(Base64.encode(bytes));
    }

    /**
     * Decode encodeBase64 {@link String} to AES byte array
     * @param src encodeBase64 {@link String}
     * @return AES encrypted byte array
     */
    private byte[] decodeBase64(String src) {
        return Base64.decode(src.getBytes());
    }
}
