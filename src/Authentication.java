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
	
	 private static String get_SHA_1_SecurePassword(String passwordToHash, byte[] salt)
	    {
	        String generatedPassword = null;
	        try {
	            MessageDigest md = MessageDigest.getInstance("SHA-1");
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
	            
	            String saltHashPassword = Base64.getEncoder().encodeToString(salt) + "PASSYWORDY" + get_SHA_256_SecurePassword(password,salt);
//	            System.out.println("PASSWORD ON SINGUP: " + password);
//	            System.out.println("SALT ON SIGNUP: " + Arrays.toString(salt));

	            OutputStream output = new FileOutputStream("config.properties");
	            prop.setProperty(username, saltHashPassword);
	            prop.store(output, null);
	            output.close();
//	            System.out.println(Arrays.toString(salt));
	            return "joined!";

	        } catch (Exception io) {	        	
	            io.printStackTrace();
	            return "An error seems to have occured, please try again.";
	        }

	    	
	    }
	    
	    public static String signIn(String username, String password)
	    {	
//	    	System.out.println("SIGN IN YA 7amadaaaaa");
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
	            	String [] vals = value.split("PASSYWORDY");
	            	byte [] salt = Base64.getDecoder().decode(vals[0]);
//	            	System.out.println("PASSWORD ON SIGNIN: " + password);
//	            	System.out.println("SALT ON SIGNIN " + Arrays.toString(salt));
//	            	System.out.println("SALT STRING ON SIGNIN: " + new String(salt));
	            	String filePassword = vals[1];
	            	
	            	String hashedPassword = get_SHA_256_SecurePassword(password, salt);

	            	if(filePassword.equals(hashedPassword))
	            	{
	            		System.out.println("SIGNED IN");
	            		return "joined!";
	            	}
	            	else 
	            	{
//	            		System.out.println("STORED PASSWORD: " + filePassword);
//	            		System.out.println("COMPUTED PASSWORD: " + hashedPassword);
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
