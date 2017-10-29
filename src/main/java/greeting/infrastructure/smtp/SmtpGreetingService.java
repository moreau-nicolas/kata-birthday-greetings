package greeting.infrastructure.smtp;

import greeting.domain.contact.Contact;
import greeting.domain.greeting.GreetingService;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import static java.util.Objects.requireNonNull;

public class SmtpGreetingService implements GreetingService {

    private final Mailer mailer;
    private final String from;

    public SmtpGreetingService(String host, int port, String from) {
        requireNonNull(host, "host should not be null");
        requireValidPort(port);
        this.mailer = MailerBuilder.withSMTPServer(host, port).buildMailer();
        this.from = requireNonNull(from);
    }

    private static void requireValidPort(int port) {
        int MIN_PORT = 1;
        int MAX_PORT = 65535;
        if (port >= MIN_PORT && port <= MAX_PORT) return;
        throw new IllegalArgumentException("port should be in range [1,65535] inclusive, got: " + port);
    }

    @Override
    public void greet(Contact contact) {
        var email = generateEmail(contact);
        mailer.sendMail(email);
    }

    private Email generateEmail(Contact contact) {
        var subject = "Happy birthday!";
        var content = "Happy birthday, dear %s!";
        return EmailBuilder.startingBlank()
                .from(from)
                .to(contact.email())
                .withSubject(subject)
                .appendText(content.formatted(contact.firstName()))
                .buildEmail();
    }
}
