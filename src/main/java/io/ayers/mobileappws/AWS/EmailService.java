package io.ayers.mobileappws.AWS;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import io.ayers.mobileappws.shared.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    final String TOKEN_PLACEHOLDER = "$TOKEN";

    // HTML BODY FOR EMAIL
    final String VERIFY_EMAIL_SUBJECT = "One last step to complete your registration!";
    final String VERIFY_EMAIL_HTML_BODY = String.format(
            "<a href='http://localhost:8081/verification/email-verification.html?token=%s'>Final step to complete your registration</a>",
            TOKEN_PLACEHOLDER);
    final String VERIFY_EMAIL_TEXT_BODY = String.format(
            "http://localhost:8081/verification/email-verification.html?token=%s",
            TOKEN_PLACEHOLDER);

    final String PASSWORD_RESET_SUBJECT = "Reset Your Password";
    final String PASSWORD_RESET_HTML_BODY = String.format(
            "<a href='http://localhost:8081/verification/password-reset.html?token=%s'>Reset your password</a>",
            TOKEN_PLACEHOLDER);
    final String PASSWORD_RESET_TEXT_BODY = String.format(
            "http://localhost:8081/verification/password-reset.html?token=%s",
            TOKEN_PLACEHOLDER);

    final String FROM = "zayers.dev@gmail.com";
    final String CHAR_SET = "UTF-8";

    public void sendVerficationEmail(UserDto userDto) {
        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.US_EAST_2).build();

        String email = userDto.getEmail();
        String emailVerificationToken = userDto.getEmailVerificationToken();

        String htmlBodyWithToken = VERIFY_EMAIL_HTML_BODY.replace(TOKEN_PLACEHOLDER, emailVerificationToken);
        String textBodyWithToken = VERIFY_EMAIL_TEXT_BODY.replace(TOKEN_PLACEHOLDER, emailVerificationToken);

        sendEmail(email, client, htmlBodyWithToken, textBodyWithToken, VERIFY_EMAIL_SUBJECT);

        log.info("Verification Email sent to: " + email);
    }

    public void sendPasswordResetRequest(String email, String verificationToken) {
        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.US_EAST_2).build();

        String htmlBodyWithToken = PASSWORD_RESET_HTML_BODY.replace(TOKEN_PLACEHOLDER, verificationToken);
        String textBodyWithToken = PASSWORD_RESET_TEXT_BODY.replace(TOKEN_PLACEHOLDER, verificationToken);

        sendEmail(email, client, htmlBodyWithToken, textBodyWithToken, PASSWORD_RESET_SUBJECT);

        log.info("Password Reset Email sent to: " + email);
    }

    private void sendEmail(String email,
                           AmazonSimpleEmailService client,
                           String htmlBodyWithToken,
                           String textBodyWithToken, String password_reset_subject) {

        Content htmlContent = new Content().withCharset(CHAR_SET).withData(htmlBodyWithToken);
        Content textContent = new Content().withCharset(CHAR_SET).withData(textBodyWithToken);
        Content subjectContent = new Content().withCharset(CHAR_SET).withData(password_reset_subject);

        Body body = new Body().withHtml(htmlContent).withText(textContent);

        Message message = new Message().withBody(body).withSubject(subjectContent);

        Destination destination = new Destination().withToAddresses(email);

        SendEmailRequest sendEmailRequest =
                new SendEmailRequest()
                        .withDestination(destination)
                        .withMessage(message)
                        .withSource(FROM);

        client.sendEmail(sendEmailRequest);
    }
}
