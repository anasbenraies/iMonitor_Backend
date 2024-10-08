package Backend.IoT.User;


import Backend.IoT.Config.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final JwtService jwtService ;


    @Autowired
        UserService(UserRepository userRepository, JwtService jwtService){
            this.userRepository = userRepository;
            this.jwtService=jwtService ;


    }

    public List<User> getUsages(){
        return userRepository.findAll();
    }

//    public User loadUserByEmail(String email){
//        User SearchedUser= userRepository.findUserByEmail(email);
//        return SearchedUser ;
//    }

    public AuthenticationResponse findUserByEmail(User user) {
        //if not correct password or email return null
        User SearchedUser= userRepository.findUserByEmail(user.getEmail());
        if(SearchedUser==null){
            return null ;
        }else {
             BCryptPasswordEncoder bCryptPasswordEncoder= new BCryptPasswordEncoder();
             if(bCryptPasswordEncoder.matches(user.getPassword(),SearchedUser.getPassword())){
                 AuthenticationResponse authResp = new AuthenticationResponse(SearchedUser) ;
                 authResp.setToken(jwtService.generateToken(SearchedUser));
                 return authResp ;
                 //return SearchedUser ;
            }else{
                 return null ;
                }
            }
        }




    public AuthenticationResponse SignUp(User user) {
        if(IsValidUserForSignUp(user.getEmail(),user.getPassword())){
            BCryptPasswordEncoder bCryptPasswordEncoder= new BCryptPasswordEncoder();
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            AuthenticationResponse authResp = new AuthenticationResponse(user) ;
            authResp.setToken(jwtService.generateToken(user));
            return authResp ;
        }else{
            //if user not valid for SignUp
            return null ;
        }
    }




    public boolean IsValidUserForSignUp(String email,String password){
        boolean IsValidEmail=false ;
        boolean IsValidPassword=false;
        if (password == null || email ==null) {
            return false;
        }
        //validating email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern EmailPattern = Pattern.compile(emailRegex);
        Matcher Emailmatcher = EmailPattern.matcher(email);
        IsValidEmail= Emailmatcher.matches();
        if (!IsValidEmail)
            return false ;
        //validating password
        // Password must be at least 8 characters long, contain at least one letter and one number
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        Pattern PasswordPattern = Pattern.compile(passwordRegex);
        Matcher Passwordmatcher = PasswordPattern.matcher(password);
        IsValidPassword= Passwordmatcher.matches();
        return IsValidEmail && IsValidPassword ;

    }
}
