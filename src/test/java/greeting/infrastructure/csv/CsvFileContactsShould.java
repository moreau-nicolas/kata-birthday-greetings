package greeting.infrastructure.csv;

import greeting.domain.contact.Contact;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import test.GenerateDisplayNameFromSourceElements;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.MonthDay;

import static java.time.Month.JANUARY;
import static org.assertj.core.api.Assertions.assertThat;

@GenerateDisplayNameFromSourceElements
@DisplayName("CsvFileContacts should")
class CsvFileContactsShould {

    private static final MonthDay
            JANUARY_21ST = MonthDay.of(JANUARY, 21);

    @Test
    void ignore_the_csv_header() {
        var contacts = new CsvFileContacts(createTestInput("only-header.csv", """
                first_name, last_name, birth_date, email
                """));

        var contactsBornOnTheDate = contacts.findByBirthDate(JANUARY_21ST);

        assertThat(contactsBornOnTheDate)
                .describedAs("contacts born on %s", JANUARY_21ST)
                .isEmpty();
    }

    @Test
    void ignore_spaces_around_the_delimiter() {
        var contacts = new CsvFileContacts(createTestInput("only-header.csv", """
                first_name,last_name,birth_date,email
                Scrooge,McDuck,  1984-01-21 , scrooge.mcduck@example.test
                """));

        var contactsBornOnTheDate = contacts.findByBirthDate(JANUARY_21ST);

        assertThat(contactsBornOnTheDate)
                .describedAs("contacts born on %s", JANUARY_21ST)
                .containsExactly(
                        new Contact("Scrooge", "McDuck", LocalDate.parse("1984-01-21"), "scrooge.mcduck@example.test")
                );
    }

    @Test
    void return_all_contacts_born_on_the_requested_month_and_day() {
        var contacts = new CsvFileContacts(createTestInput("ducks.csv", """
                first_name, last_name, birth_date, email
                Scrooge, McDuck, 1867-07-08, scrooge.mcduck@example.test
                Huey, Duck, 1984-01-21, huey.duck@example.test
                Dewey, Duck, 1983-01-21, dewey.duck@example.test
                Louie, Duck, 1982-01-21, louie.duck@example.test
                Phoey, Duck, 1985-02-23, phoey.duck@example.test
                """));

        var contactsBornOnTheDate = contacts.findByBirthDate(JANUARY_21ST);

        assertThat(contactsBornOnTheDate)
                .describedAs("contacts born on %s", JANUARY_21ST)
                .containsExactlyInAnyOrder(
                        new Contact("Huey", "Duck", LocalDate.parse("1984-01-21"), "huey.duck@example.test"),
                        new Contact("Dewey", "Duck", LocalDate.parse("1983-01-21"), "dewey.duck@example.test"),
                        new Contact("Louie", "Duck", LocalDate.parse("1982-01-21"), "louie.duck@example.test")
                );
    }

    @TempDir
    private Path temporaryDirectory;

    private Path createTestInput(String fileName, String contents) {
        try {
            var path = temporaryDirectory.resolve(fileName);
            Files.writeString(path, contents, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            return path;

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
