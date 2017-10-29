package greeting.domain.contact;

import java.time.MonthDay;
import java.util.Set;

public interface Contacts {

    Set<Contact> findByBirthDate(MonthDay birthDate);
}
