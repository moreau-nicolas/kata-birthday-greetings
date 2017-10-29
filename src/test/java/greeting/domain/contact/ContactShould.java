package greeting.domain.contact;

import org.junit.jupiter.api.Test;
import test.GenerateDisplayNameFromSourceElements;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@GenerateDisplayNameFromSourceElements
class ContactShould {

    @Test
    void have_a_full_name() {
        var contact = new Contact(
                "Scrooge",
                "McDuck",
                LocalDate.of(1867, Month.JULY, 8),
                "scrooge.mcduck@example.test"
        );

        assertThat(contact.fullName()).isEqualTo("Scrooge McDuck");
    }
}
