package org.gy.demo.dubbo.consumer.filter;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.validation.Validation;
import org.apache.dubbo.validation.Validator;
import org.apache.dubbo.validation.support.jvalidation.JValidator;
import org.gy.framework.core.exception.BizException;
import org.gy.framework.core.exception.CommonErrorCode;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/23 18:04
 */
public class CustomValidation implements Validation {

    @Override
    public Validator getValidator(URL url) {
        return new CustomValidator(url);
    }

    public static class CustomValidator extends JValidator {

        public CustomValidator(URL url) {
            super(url);
        }

        @Override
        public void validate(String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Exception {
            try {
                super.validate(methodName, parameterTypes, arguments);
            } catch (Exception e) {
                if (e instanceof ConstraintViolationException) {
                    ConstraintViolation<?> constraintViolation = ((ConstraintViolationException) e).getConstraintViolations()
                        .stream().findFirst().get();
                    throw new BizException(CommonErrorCode.PARAM_ERROR, constraintViolation.getMessage());
                } else {
                    throw e;
                }
            }
        }
    }
}
