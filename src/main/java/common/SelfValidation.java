package common;

import javax.validation.*;
import java.util.Set;

/**
 * Bean Validation 애너테이션 검증 클래스
 */
public abstract class SelfValidation<T> {
    private Validator validator;

     public SelfValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    protected void validateSelf() {
        Set<ConstraintViolation<T>> violations = validator.validate((T) this);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
