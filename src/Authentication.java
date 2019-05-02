import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Properties;

public class Authentication {
	
		// credits: https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
	    public static String get_SHA_256_SecurePassword(String passwordToHash, byte[] salt)
	    {
	    	String generatedPassword = null;
	        try {
	            MessageDigest md = MessageDigest.getInstance("SHA-256");
	            md.update(salt);
	            byte[] bytes = md.digest(passwordToHash.getBytes());
	            StringBuilder sb = new StringBuilder();
	            for(int i=0; i< bytes.length ;i++)
	            {
	                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	            }
	            generatedPassword = sb.toString();
	        }
	        catch (NoSuchAlgorithmException e)
	        {
	            e.printStackTrace();
	        }
	        return generatedPassword;
	    }

	    //Add salt
	    private static byte[] getSalt() throws NoSuchAlgorithmException
	    {
	        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
	        byte[] salt = new byte[16];
	        sr.nextBytes(salt);
	        return salt;
	    }
	    
	    // returns sign up response according to entered username and password
	    // saves salt and password in config file
	    public static String signUp(String username, String password)
	    {
	    	Properties prop = new Properties();
	    	try 
	    	{
	    		InputStream input = new FileInputStream("config.properties");
	            prop.load(input);
	            
	            if(prop.getProperty(username) != null)
	            {
	            	return "Username exists.";
	            }
	            input.close();
	            
	            byte [] salt = getSalt();
	            
	            String saltHashPassword = Base64.getEncoder().encodeToString(salt) + "###" + get_SHA_256_SecurePassword(password,salt);


	            OutputStream output = new FileOutputStream("config.properties");
	            prop.setProperty(username, saltHashPassword);
	            prop.store(output, null);
	            output.close();
	            return "joined!";

	        } catch (Exception io) {	        	
	            io.printStackTrace();
	            return "An error seems to have occured, please try again.";
	        }

	    	
	    }
	    
	    // returns response string of sign in according to inserted username and password
	    // searches config file for username and hashed password
	    public static String signIn(String username, String password)
	    {	
	    	Properties prop = new Properties();
	    	try 
	    	{
	    		InputStream input = new FileInputStream("config.properties");
	            prop.load(input);
	            
	            String value = prop.getProperty(username);	            
	            input.close();
	            
	            
	            if( value == null)
	            {
	            	return "Username does not exist.";
	            }
	            else 
	            {
	            	String [] vals = value.split("###");
	            	byte [] salt = Base64.getDecoder().decode(vals[0]);
	            	String filePassword = vals[1];
	            	
	            	String hashedPassword = get_SHA_256_SecurePassword(password, salt);

	            	if(filePassword.equals(hashedPassword))
	            	{
	            		System.out.println("SIGNED IN");
	            		return "joined!";
	            	}
	            	else 
	            	{
	            		return "Incorrect username or password. Please try again!";
	            	}
	            	
	            	
	            }
	           
	        } catch (Exception io) {	        	
	            io.printStackTrace();
	            return "An error seems to have occured, please try again.";
	        }
	    }
//	    
}
