package com.adira.contact.exception;

import org.apache.coyote.BadRequestException;
import org.hibernate.NonUniqueResultException;
import org.slf4j.MDC;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.adira.contact.common.LogUtils;
import com.adira.contact.entity.ApiResponse;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EmptyResultDataAccessException.class)
  public ResponseEntity<?> handleEmptyResultDataAccessException(EmptyResultDataAccessException e) {
    LogUtils.logErrorWithTraceId(e.getMessage(), e);
    cleanupMDC();
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiResponse<>(404, "Data Not Found", "Global API Service", e.getMessage()));
  }

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<?> handleNullPointerException(NullPointerException e) {
    LogUtils.logErrorWithTraceId(e.getMessage(), e);
    cleanupMDC();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiResponse<>(500, "Internal Server Error (NullPointerException)",
            "Global API Service", e.getMessage()));
  }

  @ExceptionHandler(NonUniqueResultException.class)
  public ResponseEntity<?> handleNonUniqueResultException(NonUniqueResultException e) {
    LogUtils.logErrorWithTraceId(e.getMessage(), e);
    cleanupMDC();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiResponse<>(500, "Internal Server Error (NonUniqueResultException)",
            "Global API Service", e.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    LogUtils.logErrorWithTraceId(e.getMessage(), e);
    cleanupMDC();
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setTimestamp(new Date());
    errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    errorResponse.setMessage(e.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handleCustomBadRequest(Exception ex) {
    LogUtils.logErrorWithTraceId(ex.getMessage(), ex);
    cleanupMDC();
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setTimestamp(new Date());
    errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
    errorResponse.setMessage(ex.getMessage());

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  private void cleanupMDC() {
    MDC.remove("traceId");
  }
}
