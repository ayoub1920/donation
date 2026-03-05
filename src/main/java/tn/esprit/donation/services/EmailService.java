package tn.esprit.donation.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import tn.esprit.donation.entity.Donation;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private UserService userService;

    public void sendDonationThankYouEmail(Donation donation) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String userEmail = userService.getUserEmail(donation.getUserId());
            
            helper.setFrom("Minolingo <mino.support@minolingo.online>");
            helper.setTo(userEmail);
            helper.setSubject("Merci pour votre généreux don !");

            Context context = new Context();
            context.setVariable("donation", donation);
            context.setVariable("itemName", donation.getItemName());
            context.setVariable("quantity", donation.getQuantity());
            context.setVariable("donatedAt", donation.getDonatedAt());
            context.setVariable("userEmail", userEmail);

            String htmlContent = templateEngine.process("donation-thank-you", context);

            helper.setText(htmlContent, true);

            emailSender.send(message);
            
            System.out.println("Thank you email sent to: " + userEmail);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMerciPointsEmail(Donation donation, int pointsEarned, int totalPoints) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String userEmail = userService.getUserEmail(donation.getUserId());

            helper.setFrom("Minolingo <mino.support@minolingo.online>");
            helper.setTo(userEmail);
            helper.setSubject("🎉 Vous avez gagné " + pointsEarned + " points MERCI !");

            Context context = new Context();
            context.setVariable("itemName", donation.getItemName());
            context.setVariable("quantity", donation.getQuantity());
            context.setVariable("pointsEarned", pointsEarned);
            context.setVariable("totalPoints", totalPoints);

            String htmlContent = templateEngine.process("merci-points", context);

            helper.setText(htmlContent, true);

            emailSender.send(message);

            System.out.println("Merci points email sent to: " + userEmail + " (" + pointsEarned + " pts)");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email merci points: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
