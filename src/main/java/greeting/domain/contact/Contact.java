package greeting.domain.contact;

import java.time.LocalDate;

import static java.util.Objects.requireNonNull;

public record Contact(
        String firstName,
        String lastName,
        LocalDate birthDate,
        String email
) {
    public Contact {
        requireNonNull(firstName);
        requireNonNull(lastName);
        requireNonNull(birthDate);
        requireNonNull(email);
    }

    public String fullName() {
        return "%1$s %2$s".formatted(firstName, lastName);
    }
}
