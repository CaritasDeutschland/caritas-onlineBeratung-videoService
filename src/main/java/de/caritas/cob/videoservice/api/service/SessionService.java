package de.caritas.cob.videoservice.api.service;

import de.caritas.cob.videoservice.api.authorization.AuthenticatedUser;
import de.caritas.cob.videoservice.api.exception.httpresponse.BadRequestException;
import de.caritas.cob.videoservice.api.exception.httpresponse.InternalServerErrorException;
import de.caritas.cob.videoservice.api.service.helper.ServiceHelper;
import de.caritas.cob.videoservice.userservice.generated.ApiClient;
import de.caritas.cob.videoservice.userservice.generated.web.UserControllerApi;
import de.caritas.cob.videoservice.userservice.generated.web.model.ConsultantSessionDTO;
import java.util.Arrays;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Service class to provide handle session methods of the UserService.
 */
@Service
@RequiredArgsConstructor
public class SessionService {

  private final @NonNull UserControllerApi userControllerApi;
  private final @NonNull ServiceHelper serviceHelper;
  private final @NonNull AuthenticatedUser authenticatedUser;

  /**
   * Returns the session for the provided consultant.
   *
   * @param sessionId session Id
   * @return {@link ConsultantSessionDTO}
   */
  public ConsultantSessionDTO findSessionOfCurrentConsultant(Long sessionId) {
    addDefaultHeaders(this.userControllerApi.getApiClient());

    try {
      return userControllerApi.fetchSessionForConsultant(sessionId);
    } catch (HttpClientErrorException ex) {

      checkIfIsClientBadRequest(sessionId, ex);

      throw new InternalServerErrorException(
          String.format("Could not get session %s for consultant %s.", sessionId,
              authenticatedUser.getUserId()), ex, LogService::logInternalServerError);
    }
  }

  private void checkIfIsClientBadRequest(Long sessionId, HttpClientErrorException ex) {
    if (isClientBadRequest(ex.getStatusCode())) {
      throw new BadRequestException(
          String.format("Could not get session %s for consultant %s.", sessionId,
              authenticatedUser.getUserId()), LogService::logWarning);
    }
  }

  private void addDefaultHeaders(ApiClient apiClient) {
    HttpHeaders headers = this.serviceHelper.getKeycloakAndCsrfHttpHeaders();
    headers.forEach((key, value) -> apiClient.addDefaultHeader(key, value.iterator().next()));
  }

  private boolean isClientBadRequest(HttpStatus status) {
    return Arrays
        .asList(HttpStatus.FORBIDDEN, HttpStatus.UNAUTHORIZED, HttpStatus.NOT_FOUND)
        .contains(status);
  }
}
