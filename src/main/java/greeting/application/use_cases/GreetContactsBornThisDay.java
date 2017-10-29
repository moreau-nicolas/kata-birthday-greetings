package greeting.application.use_cases;

import greeting.domain.contact.Contacts;
import greeting.domain.greeting.GreetingService;

import java.time.*;

import static java.util.Objects.requireNonNull;

public class GreetContactsBornThisDay {

    private final Contacts contacts;
    private final GreetingService greetingService;
    private final Clock clock;

    public GreetContactsBornThisDay(Contacts contacts, GreetingService greetingService, Clock clock) {
        this.contacts = requireNonNull(contacts);
        this.greetingService = requireNonNull(greetingService);
        this.clock = requireNonNull(clock);
    }

    private static final MonthDay
            FEBRUARY_28TH = MonthDay.of(Month.FEBRUARY, 28),
            FEBRUARY_29TH = MonthDay.of(Month.FEBRUARY, 29);

    public void invoke() {
        var currentYear = Year.now(clock);
        var today = MonthDay.now(clock);
        greetContactsWithBirthDate(today);
        if (today.equals(FEBRUARY_28TH) && currentYear.isLeap() == false) {
            greetContactsWithBirthDate(FEBRUARY_29TH);
        }
    }

    private void greetContactsWithBirthDate(MonthDay today) {
        contacts.findByBirthDate(today)
                .forEach(greetingService::greet);
    }
}
