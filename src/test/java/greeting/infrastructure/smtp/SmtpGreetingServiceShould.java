package greeting.infrastructure.smtp;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import greeting.domain.contact.Contact;
import org.assertj.core.api.ThrowingConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import test.GenerateDisplayNameFromSourceElements;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;

import static javax.mail.Message.RecipientType.TO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@GenerateDisplayNameFromSourceElements
@DisplayName("SmtpGreetingService should")
class SmtpGreetingServiceShould {

    @Nested
    @DisplayName("validate the port")
    class ValidateThePort {

        @Test
        void reject_a_negative_port() {
            assertThatThrownBy(() -> new SmtpGreetingService("unimportant", -1, "unimportant"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void reject_a_port_equal_to_0() {
            assertThatThrownBy(() -> new SmtpGreetingService("unimportant", 0, "unimportant"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void reject_a_port_greater_than_65535() {
            assertThatThrownBy(() -> new SmtpGreetingService("unimportant", 65536, "unimportant"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void accept_a_port_equal_to_65535() {
            assertThat(new SmtpGreetingService("unimportant", 65535, "unimportant"))
                    .isNotNull();
        }
    }

    @Nested
    @DisplayName("greet the contact")
    class GreetTheContact {

        @RegisterExtension
        static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP.dynamicPort());

        @Test
        void by_sending_an_email_via_smtp() {
            var emailGreetingService = new SmtpGreetingService("localhost", greenMail.getSmtp().getPort(),
                    "noreply@example.test");
            var huey = new Contact("Huey", "Duck", LocalDate.parse("1984-01-21"), "huey.duck@example.test");

            emailGreetingService.greet(huey);

            var receivedMessages = greenMail.getReceivedMessages();
            assertThat(receivedMessages).describedAs("received messages")
                    .singleElement().describedAs("email")
                    .satisfies(
                            subject("Happy birthday!"),
                            from("noreply@example.test"),
                            to("huey.duck@example.test"),
                            content("Happy birthday, dear Huey!")
                    );
        }

        private static ThrowingConsumer<MimeMessage> subject(String expected) {
            return email -> assertThat(email.getSubject()).describedAs("subject")
                    .isEqualTo(expected);
        }

        private static ThrowingConsumer<MimeMessage> from(String expected) {
            return email -> assertThat(email.getFrom()).describedAs("from")
                    .isEqualTo(InternetAddress.parse(expected));
        }

        private static ThrowingConsumer<MimeMessage> to(String expected) {
            return email -> assertThat(email.getRecipients(TO)).describedAs("to")
                    .isEqualTo(InternetAddress.parse(expected));
        }

        private static ThrowingConsumer<MimeMessage> content(String expected) {
            return email -> assertThat(email.getInputStream()).describedAs("content")
                    .hasContent(expected);
        }
    }
}
