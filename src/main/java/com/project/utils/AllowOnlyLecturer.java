package com.project.utils;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

/**
 * Adnotacja określająca, że dostęp do danej metody kontrolera powinien być dostępny
 * tylko dla użytkowników posiadających uprawnienia wykładowcy.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@Inherited
@PreAuthorize("hasAnyRole('lecturers', 'ROLE_lecturers')")
public @interface AllowOnlyLecturer {
    Class<?>[] exclude() default {};
}
