package greeting.application;

import greeting.application.use_cases.GreetContactsBornThisDay;
import greeting.infrastructure.csv.CsvFileContacts;
import greeting.infrastructure.smtp.SmtpGreetingService;

import java.nio.file.Paths;
import java.time.Clock;

public class BirthdayGreetingConsoleApplication {

    public static void main(String[] args) {
        var contacts = new CsvFileContacts(Paths.get("contacts.txt"));
        var smtpService = new SmtpGreetingService("localhost", 25, "noreply@example.test");
        var greetContactsBornThisDay = new GreetContactsBornThisDay(contacts, smtpService, Clock.systemDefaultZone());
        greetContactsBornThisDay.invoke();
    }
}
