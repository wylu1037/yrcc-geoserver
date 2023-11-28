/*
 * Copyright (C) 2023 ZKJG, Inc.
 *
 * Licensed under the MIT (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/license/mit/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.gov.yrcc.internal.error;

import cn.gov.yrcc.utils.base.BaseResult;
import com.google.common.base.Throwables;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.StringJoiner;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public BaseResult<Object> jakartaValidationExceptionCapture(MethodArgumentNotValidException e) {
        var bindingResult = e.getBindingResult();
        var errorMessages = new StringJoiner("; ");
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors();
            if (!errors.isEmpty()) {
                errors.forEach(error -> errorMessages.add(error.getDefaultMessage()));
                return BaseResult.error(HttpStatus.BAD_REQUEST.value(), errorMessages.toString());
            }
        }
        return BaseResult.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public BaseResult<Object> constraintViolationExceptionCapture(ConstraintViolationException e) {
        var violations = e.getConstraintViolations();
        var errorMessages = new StringJoiner("; ");
        if (!violations.isEmpty()) {
            for (var violation : violations) {
                errorMessages.add(violation.getMessage());
            }
            return BaseResult.error(HttpStatus.BAD_REQUEST.value(), errorMessages.toString());
        }
        return BaseResult.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    /**
     * 自定义业务异常
     *
     * @param e exception
     * @return BaseResult
     */
    @ExceptionHandler(value = BusinessException.class)
    public BaseResult<Object> businessExceptionCapture(Exception e) {
		log.error("[GlobalExceptionHandler] businessExceptionCapture() called with Params: e = {}, Error message = {}",
			e, Throwables.getStackTraceAsString(e));
        return BaseResult.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = Throwable.class)
    public BaseResult<Object> throwableCapture(Throwable e) {
		log.error("[GlobalExceptionHandler] throwableCapture() called with Params: e = {}, Error message = {}",
			e, Throwables.getStackTraceAsString(e));
        return BaseResult.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
}
