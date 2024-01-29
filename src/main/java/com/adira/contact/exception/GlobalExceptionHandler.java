package com.adira.contact.exception;

import org.apache.coyote.BadRequestException;
import org.hibernate.NonUniqueResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.adira.contact.common.LogUtils;
import com.adira.contact.controller.ContactController;
import com.adira.contact.entity.ApiResponse;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

      private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    LogUtils logUtils = new LogUtils(LOGGER);

  @ExceptionHandler(PermissionDeniedDataAccessException.class)
  public ResponseEntity<ErrorResponse> handlePermissionDeniedDataAccessException(Exception e) {
    logUtils.logErrorWithTraceId(e.getMessage(), e);
    cleanupMDC();
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setTimestamp(new Date());
    errorResponse.setStatus(HttpStatus.FORBIDDEN.value());
    errorResponse.setMessage(e.getMessage());

    return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(EmptyResultDataAccessException.class)
  public ResponseEntity<?> handleEmptyResultDataAccessException(EmptyResultDataAccessException e) {
    logUtils.logErrorWithTraceId(e.getMessage(), e);
    cleanupMDC();
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiResponse<>(404, "Data Not Found", "Global API Service", e.getMessage()));
  }

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<?> handleNullPointerException(NullPointerException e) {
    logUtils.logErrorWithTraceId(e.getMessage(), e);
    cleanupMDC();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiResponse<>(500, "Internal Server Error (NullPointerException)",
            "Global API Service", e.getMessage()));
  }

  @ExceptionHandler(NonUniqueResultException.class)
  public ResponseEntity<?> handleNonUniqueResultException(NonUniqueResultException e) {
    logUtils.logErrorWithTraceId(e.getMessage(), e);
    cleanupMDC();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiResponse<>(500, "Internal Server Error (NonUniqueResultException)",
            "Global API Service", e.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    logUtils.logErrorWithTraceId(e.getMessage(), e);
    cleanupMDC();
    ErrorResponse errorResponse = new ErrorResponse();
    errorResponse.setTimestamp(new Date());
    errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    errorResponse.setMessage(e.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handleCustomBadRequest(Exception ex) {
    logUtils.logErrorWithTraceId(ex.getMessage(), ex);
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
