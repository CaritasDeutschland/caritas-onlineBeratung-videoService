package de.caritas.cob.videoservice.api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.io.PrintWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

@RunWith(MockitoJUnitRunner.class)
public class LogServiceTest {

  @Mock
  Exception exception;

  @Mock
  private Logger logger;

  @Before
  public void setup() {
    setInternalState(LogService.class, "LOGGER", logger);
  }

  @Test
  public void logInfo_Should_LogInfoMessage() {
    LogService.logInfo("info message");

    verify(logger, atLeastOnce()).info(eq("info message"));
  }

  @Test
  public void logWarning_Should_LogWarnMessage_When_onlyExceptionIsProvided() {
    LogService.logWarning(exception);

    verify(logger, atLeastOnce()).warn(eq("VideoService API: {}"), anyString());
    verify(exception, atLeastOnce()).printStackTrace(any(PrintWriter.class));
  }

  @Test
  public void logWarning_Should_LogWarnMessage_When_onlyExceptionAndStatusProvided() {
    LogService.logWarning(HttpStatus.MULTI_STATUS, exception);

    verify(logger, atLeastOnce()).warn(eq("VideoService API: {}: {}"),
        eq("Multi-Status"), anyString());
    verify(exception, atLeastOnce()).printStackTrace(any(PrintWriter.class));
  }

  @Test
  public void logInternalServerError_Should_LogError() {
    LogService.logInternalServerError(exception);
    
    verify(logger, atLeastOnce()).error(eq("VideoService API: 500 Internal Server Error: {}"),
        anyString());
    verify(exception, atLeastOnce()).printStackTrace(any(PrintWriter.class));
  }

}
