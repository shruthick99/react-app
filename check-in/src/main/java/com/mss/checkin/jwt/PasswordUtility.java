package com.mss.checkin.jwt;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class PasswordUtility {

//	@Autowired
//	PropertyUtility propertyUtility;
	/*public static void main(String args[]) {
		System.out.println(decryptPwd("@164@232@198@234@198@204@132@118@112@226"));
	}*/
	
	/** Creates a new instance of FormulaEncrypt */
	public static String encryptPwd(String src) {
		// Converting String to array

		char asciiarr[] = src.toCharArray();

		// Finding lnegth and converting into int

		int encryasciiarr[] = new int[src.length()];

		String encrypt = "";

		for (int i = 0; i < asciiarr.length; i++) {
			// System.out.println("The origianl value are"+ (int)asciiarr[i]);
			int asciichar = (int) asciiarr[i] + 2;
			int accharmul2 = asciichar * 2;
			encryasciiarr[i] = accharmul2;

		}

		for (int j = 0; j < encryasciiarr.length; j++) {

			// System.out.println("The ascii char are"+encryasciiarr[j]);
			encrypt = encrypt + "@" + encryasciiarr[j];
		}

		// System.out.println("The enc is"+encrypt);

		return encrypt;
	}// end of the encrypt method

	public static String decryptPwd( String enc) {
		final String[] asarr = enc.split("@");
		int inval = 0;
		String instr = ".";
		String instr2 = "";
		String orval = "";
		for (int lk = 0; lk < asarr.length; ++lk) {
			if (asarr[lk].equalsIgnoreCase("")) {
				instr = asarr[lk];
			} else {
				instr2 = asarr[lk];
				inval = Integer.parseInt(instr2);
				final int divval = inval / 2;
				final int minusval = divval - 2;
				orval += (char) minusval;
			}
		}
		return orval;
	}
	
	 
		public static String checkPassword(String password) {
			String message="success";
			
			if(password== null || "".equals(password.trim())) {
				message = "Password should not be empty";
				return message;
			}else if(password.length()<8) {
				message = "Atleast passoword should be 8 characters";
				return message;
			}else if(password.length()>20) {
				message = "Passoword should be maximum 20 characters";
				return message;
			}
			char specialcharacters[] = {'!','@','#','%','^','*','=','-','+',';','.',':'};
			
		     boolean capitalFlag = false;
		     boolean lowerCaseFlag = false;
		     boolean numberFlag = false;
		     boolean specialCharacterFlag = false;
		     boolean invalidSpecialCharacter = false;
		     char ch;
		     
		     // char nonSpecialChars[] = {'$','&',')','(','<','>','|','`','\'','"','[',']','{','}'};
		     
		     for(int i=0;i < password.length();i++) {
		         ch = password.charAt(i);
		         // To check atleast one number,one uppercase letter and one lower case letter are existed.
		         if( Character.isDigit(ch)) {
		             numberFlag = true;
		         }
		         else if (Character.isUpperCase(ch)) {
		             capitalFlag = true;
		         } else if (Character.isLowerCase(ch)) {
		             lowerCaseFlag = true;
		         }
		         
		         //To check at least one special character is existed 
		         if(!Character.isLetter(ch) && !Character.isDigit(ch)) {
		        	 for(int j=0;j<specialcharacters.length;j++) {
			        	 if(specialcharacters[j]==ch) {
			        		 specialCharacterFlag = true;
			        	 }
			         }
		        	 
		        	
		         }
		         
		         //To check invalid special character is existed in password
		         if(!Character.isLetter(ch) && !Character.isDigit(ch)) {
		        	 invalidSpecialCharacter = true;
		        	 for(int j=0;j<specialcharacters.length;j++) {
		        		 if(specialcharacters[j]==ch) {
		        			 invalidSpecialCharacter = false;
		        		 }
		        	 }
		        	 if(invalidSpecialCharacter) {
	        		 message = "Passoword should contain special character from !@#%^*=-+;.:";
	     	  		return message; 
		        	 }
	        	 }
		         
		        /* for(int j=0;j<specialcharacters.length;j++) {
		        	 if(specialcharacters[j]==ch) {
		        		 specialCharacterFlag = true;
		        	 }
		         }*/
		         //f(!Character.isLetter(c) && !Character.isDigit(c)) {
		         //$ & ) ( < > | ` ' " [ ] { }
		         
		         
		         
		        /* if(numberFlag && capitalFlag && lowerCaseFlag)
		             return true;*/
		     }
		     if(!capitalFlag) {
		    	 message = "Passoword should contain atleast one capital letter";
		  		return message; 
		     }else if(!lowerCaseFlag) {
		    	 message = "Passoword should contain atleast one lowercase letter";
		  		return message; 
		     }else if(!numberFlag) {
		    	 message = "Passoword should contain atleast one digit";
		  		return message; 
		     }else if(!specialCharacterFlag) {
		    	 message = "Passoword should contain atleast one special character from !@#%^*=-+;.:";
		  		return message; 
		     }
		     
			
			return message;
		}
		
		


		//https://xperti.io/blogs/java-aes-encryption-and-decryption/
		/*		public  String decryption(String encrypted) {
			 	    try {
			 	    	String key = propertyUtility.getCipherSecretKey();
			 	    	//System.out.println("key"+key);
			  	       // IvParameterSpec iv = new IvParameterSpec(IV.getBytes("UTF-8"));
			 	    	 IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
			  	        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			  	 
			  	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			  	        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			 	        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
			  	        return new String(original);
			 	    } catch (Exception ex) {
			  	        //ex.printStackTrace();
			 	    	System.out.println(ex.getMessage());
			  	    }
			  	    return null;
			  	}
				
				
				public  String encryption(String value) {
			  	    try {
			  	    	String key = propertyUtility.getCipherSecretKey();
			  	        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
			  	        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			  	 
			  	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			  	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			  	 
			  	        byte[] encrypted = cipher.doFinal(value.getBytes());
			  	        return Base64.getEncoder().encodeToString(encrypted);
			  	    } catch (Exception ex) {
			  	       // ex.printStackTrace();
			  	    	System.out.println(ex.getMessage());
			  	    }
			  	    return null;
			  	}
				*/
			
}