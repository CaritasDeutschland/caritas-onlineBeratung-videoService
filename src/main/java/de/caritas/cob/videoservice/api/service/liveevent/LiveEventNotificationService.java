package de.caritas.cob.videoservice.api.service.liveevent;

import de.caritas.cob.videoservice.liveservice.generated.web.LiveControllerApi;
import de.caritas.cob.videoservice.liveservice.generated.web.model.LiveEventMessage;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

/**
 * Service class to provide live event triggers to the LiveService.
 */
@Service
@RequiredArgsConstructor
public class LiveEventNotificationService {

  private static final int TIMEOUT = 3;

  private final @NonNull LiveControllerApi liveControllerApi;

  /**
   * Sends a live event to the LiveService to inform the video call receivers.
   *
   * @param liveEventMessage {@link LiveEventMessage}
   * @param userIds          list of receiver user Ids
   */
  public void sendVideoCallRequestLiveEvent(LiveEventMessage liveEventMessage,
      List<String> userIds) {
    await();
    liveControllerApi.sendLiveEvent(liveEventMessage.userIds(userIds));
  }

  @SneakyThrows
  private void await() {
    TimeUnit.SECONDS.sleep(TIMEOUT);
  }
}
