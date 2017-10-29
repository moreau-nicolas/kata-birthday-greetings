package greeting.application.use_cases;

import greeting.domain.contact.Contact;
import greeting.domain.contact.Contacts;
import greeting.domain.greeting.GreetingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import test.GenerateDisplayNameFromSourceElements;

import java.time.*;
import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.*;

@GenerateDisplayNameFromSourceElements
@DisplayName("GreetContactsBornThisDay should")
class GreetContactsBornThisDayShould {

    private final Contacts mockContacts = mock(Contacts.class);
    private final GreetingService mockGreetingService = mock(GreetingService.class);

    private static final Contact
            HUEY = new Contact("Huey", "Duck", LocalDate.parse("1984-01-21"), "huey.duck@example.test"),
            DEWEY = new Contact("Dewey", "Duck", LocalDate.parse("1983-01-21"), "dewey.duck@example.test"),
            LOUIE = new Contact("Louie", "Duck", LocalDate.parse("1982-01-21"), "louie.duck@example.test"),
            DONALD = new Contact("Donald", "Duck", LocalDate.parse("2016-02-29"), "donald.duck@example.test");

    private static final MonthDay
            JANUARY_21ST = MonthDay.of(Month.JANUARY, 21),
            FEBRUARY_28TH = MonthDay.of(Month.FEBRUARY, 28),
            FEBRUARY_29TH = MonthDay.of(Month.FEBRUARY, 29);

    private static final Year
            ANY_LEAP_YEAR = Year.of(2020),
            ANY_COMMON_YEAR = Year.of(2023),
            ANY_YEAR = Year.now();

    private static Clock fixedClock(LocalDate date) {
        var instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return Clock.fixed(instant, ZoneId.systemDefault());
    }

    @Test
    void greet_all_contacts_born_on_same_day_and_month() {
        when(mockContacts.findByBirthDate(JANUARY_21ST))
                .thenReturn(Set.of(HUEY, DEWEY, LOUIE));
        var greetContactsBornThisDay = new GreetContactsBornThisDay(mockContacts, mockGreetingService,
                fixedClock(ANY_YEAR.atMonthDay(JANUARY_21ST))
        );

        greetContactsBornThisDay.invoke();

        verify(mockGreetingService).greet(HUEY);
        verify(mockGreetingService).greet(DEWEY);
        verify(mockGreetingService).greet(LOUIE);
        verifyNoMoreInteractions(mockGreetingService);
    }

    @Test
    void greet_contacts_born_on_February_29th_on_February_29th_when_year_is_leap() {
        when(mockContacts.findByBirthDate(FEBRUARY_29TH))
                .thenReturn(Set.of(DONALD));
        var greetContactsBornThisDay = new GreetContactsBornThisDay(mockContacts, mockGreetingService,
                fixedClock(ANY_LEAP_YEAR.atMonthDay(FEBRUARY_29TH))
        );

        greetContactsBornThisDay.invoke();

        verify(mockGreetingService).greet(DONALD);
        verifyNoMoreInteractions(mockGreetingService);
    }

    @Test
    void greet_contacts_born_on_February_29th_on_February_28th_when_year_is_common() {
        when(mockContacts.findByBirthDate(FEBRUARY_28TH))
                .thenReturn(Collections.emptySet());
        when(mockContacts.findByBirthDate(FEBRUARY_29TH))
                .thenReturn(Set.of(DONALD));
        var greetContactsBornThisDay = new GreetContactsBornThisDay(mockContacts, mockGreetingService,
                fixedClock(ANY_COMMON_YEAR.atMonthDay(FEBRUARY_28TH))
        );

        greetContactsBornThisDay.invoke();

        verify(mockGreetingService).greet(DONALD);
        verifyNoMoreInteractions(mockGreetingService);
    }
}
