package org.gy.demo.dubbo.provider.filter;

import static org.apache.dubbo.common.constants.CommonConstants.CONSUMER;
import static org.apache.dubbo.common.constants.CommonConstants.PROVIDER;
import static org.apache.dubbo.common.constants.FilterConstants.VALIDATION_KEY;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.ConfigUtils;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.validation.Validation;
import org.apache.dubbo.validation.Validator;
import org.gy.framework.core.dto.Response;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/23 19:05
 */
@Activate(group = {CONSUMER, PROVIDER}, value = "customValidationFilter", order = 10000)
public class CustomValidationFilter implements Filter {

    private Validation validation;

    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws org.apache.dubbo.rpc.RpcException {
        if (validation != null && !invocation.getMethodName().startsWith("$") && ConfigUtils.isNotEmpty(
            invoker.getUrl().getMethodParameter(invocation.getMethodName(), VALIDATION_KEY))) {
            try {
                Validator validator = validation.getValidator(invoker.getUrl());
                if (validator != null) {
                    validator.validate(invocation.getMethodName(), invocation.getParameterTypes(),
                        invocation.getArguments());
                }
            } catch (ConstraintViolationException e) {
                ConstraintViolation<?> constraintViolation = ((ConstraintViolationException) e).getConstraintViolations()
                    .stream().findFirst().get();
                return AsyncRpcResult.newDefaultAsyncResult(Response.asError(1001, constraintViolation.getMessage()),
                    invocation);
            } catch (RpcException e) {
                throw e;
            } catch (ValidationException e) {
                // only use exception's message to avoid potential serialization issue
                return AsyncRpcResult.newDefaultAsyncResult(new ValidationException(e.getMessage()), invocation);
            } catch (Throwable t) {
                return AsyncRpcResult.newDefaultAsyncResult(t, invocation);
            }
        }
        return invoker.invoke(invocation);
    }
}
