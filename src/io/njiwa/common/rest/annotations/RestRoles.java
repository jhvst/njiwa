package io.njiwa.common.rest.annotations;

import org.apache.deltaspike.security.api.authorization.SecurityBindingType;

import javax.enterprise.util.Nonbinding;
import java.lang.annotation.*;

@Retention(value = RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@SecurityBindingType
public @interface RestRoles {
    @Nonbinding
    String [] value() default {};
}
