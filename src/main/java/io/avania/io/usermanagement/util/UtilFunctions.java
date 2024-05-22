package io.avania.io.usermanagement.util;

import com.google.common.base.Joiner;
import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author David C Makuba
 * @created 22/05/2022
 **/
@UtilityClass
public class UtilFunctions {
    public static String generate4Digits() {
        SecureRandom r = new SecureRandom();
        int low = 1000;
        int high = 9999;
        int result = r.nextInt(high - low) + low;
        return String.valueOf(result);

    }
    public Tuple<Boolean, String> isValidPassword(String password) {
        Pattern regexPattern = Pattern.compile ("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$");
        if (password.contains (" ")) {
            return Tuple.<Boolean, String>builder ().t (false).v ("Password cannot contain empty space").build ();
        }
        if (!regexPattern.matcher (password).matches ()) {
            return Tuple.<Boolean, String>builder ().t (false).v ("Password must be more than 8 characters, contain at least one uppercase, one lower case and one special character.").build ();
        }
        return Tuple.<Boolean, String>builder ().t (true).v (null).build ();
    }

    public String generate8CharactersComplexPassword() {
        SecureRandom r = new SecureRandom ();
        String noChars = "0123456789";
        String capChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerChars = "abcdefghijklmnopqrstuvwxyz";
        String specialChars = "!#$&";

        List<Character> passwordList = new ArrayList<> (List.of (
                noChars.charAt (r.nextInt (noChars.length () - 1)),
                noChars.charAt (r.nextInt (noChars.length () - 1)),
                capChars.charAt (r.nextInt (capChars.length () - 1)),
                capChars.charAt (r.nextInt (capChars.length () - 1)),
                lowerChars.charAt (r.nextInt (lowerChars.length () - 1)),
                noChars.charAt (r.nextInt (noChars.length () - 1)),
                lowerChars.charAt (r.nextInt (lowerChars.length () - 1)),
                specialChars.charAt (r.nextInt (specialChars.length () - 1))));
        Collections.shuffle (passwordList,r);
        return Joiner.on("").join(passwordList);
    }

    public Date toDate(LocalDateTime dateToConvert) {
        return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Function<String,String> capitalizeAndRemoveSpaces(){
        return str-> str.toUpperCase ().trim ()
                .replace (" ","_");
    }




}
