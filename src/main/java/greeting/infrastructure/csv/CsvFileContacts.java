package greeting.infrastructure.csv;

import greeting.domain.contact.Contact;
import greeting.domain.contact.Contacts;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toUnmodifiableSet;

public class CsvFileContacts implements Contacts {

    private final Path filePath;

    public CsvFileContacts(Path filePath) {
        this.filePath = requireNonNull(filePath);
    }

    @Override
    public Set<Contact> findByBirthDate(MonthDay birthDate) {
        try (var lines = Files.lines(filePath)) {
            return skipHeader(lines)
                    .map(this::createContact)
                    .filter(equalsMonthDay(birthDate))
                    .collect(toUnmodifiableSet());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Stream<String> skipHeader(Stream<String> lines) {
        return lines.skip(1);
    }

    private static Predicate<Contact> equalsMonthDay(MonthDay birthDate) {
        return contact -> MonthDay.from(contact.birthDate()).equals(birthDate);
    }

    private Contact createContact(String line) {
        var delimiter = " *, *";
        var parts = line.split(delimiter, -1);
        var firstName = parts[0];
        var lastName = parts[1];
        var birthDate = LocalDate.parse(parts[2]);
        var email = parts[3];
        return new Contact(firstName, lastName, birthDate, email);
    }
}
