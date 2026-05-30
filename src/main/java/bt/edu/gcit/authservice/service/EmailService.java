package bt.edu.gcit.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your NSPS Registration OTP");
        message.setText("Your OTP for registration is: " + otp);
        mailSender.send(message);
    }

    public void sendAdminSetupEmail(String to, String setupLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Complete Your College Admin Registration");
        message.setText(
                "You have been added as a College Admin. Please click the link below to set your password and activate your account:\n\n"
                        + setupLink);
        mailSender.send(message);
    }

    public void sendPasswordResetOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("NSPS Password Reset OTP");
        message.setText(
                "Dear user,\n\n" +
                        "Your OTP to reset your NSPS password is: " + otp + "\n\n" +
                        "This OTP will expire in 10 minutes.\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "Regards,\nNSPS Team");
        mailSender.send(message);
    }
}
