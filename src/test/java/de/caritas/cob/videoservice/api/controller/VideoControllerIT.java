package de.caritas.cob.videoservice.api.controller;

import static de.caritas.cob.videoservice.api.testhelper.PathConstants.PATH_REJECT_VIDEO_CALL;
import static de.caritas.cob.videoservice.api.testhelper.PathConstants.PATH_START_VIDEO_CALL;
import static de.caritas.cob.videoservice.api.testhelper.RequestBodyConstants.VALID_START_VIDEO_CALL_BODY;
import static de.caritas.cob.videoservice.api.testhelper.TestConstants.CREATE_VIDEO_CALL_RESPONSE_DTO;
import static de.caritas.cob.videoservice.api.testhelper.TestConstants.RC_USER_ID_HEADER;
import static de.caritas.cob.videoservice.api.testhelper.TestConstants.RC_USER_ID_VALUE;
import static de.caritas.cob.videoservice.api.testhelper.TestConstants.SESSION_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.videoservice.api.authorization.RoleAuthorizationAuthorityMapper;
import de.caritas.cob.videoservice.api.facade.StartVideoCallFacade;
import de.caritas.cob.videoservice.api.model.RejectVideoCallDTO;
import de.caritas.cob.videoservice.api.service.RejectVideoCallService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(VideoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class VideoControllerIT {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private StartVideoCallFacade startVideoCallFacade;

  @MockBean
  private RejectVideoCallService rejectVideoCallService;

  @MockBean
  private RoleAuthorizationAuthorityMapper roleAuthorizationAuthorityMapper;

  @Test
  public void createVideoCall_Should_ReturnCreated_When_EverythingSucceeded() throws Exception {

    when(startVideoCallFacade.startVideoCall(eq(SESSION_ID), anyString())).thenReturn(
        CREATE_VIDEO_CALL_RESPONSE_DTO);

    mvc.perform(post(PATH_START_VIDEO_CALL)
        .header(RC_USER_ID_HEADER, RC_USER_ID_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(VALID_START_VIDEO_CALL_BODY)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());
  }

  @Test
  public void createVideoCall_Should_ReturnBadRequest_When_SessionIdIsMissing() throws Exception {

    mvc.perform(post(PATH_START_VIDEO_CALL)
        .header(RC_USER_ID_HEADER, RC_USER_ID_VALUE)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void createVideoCall_Should_ReturnBadRequest_When_RcUserIdIsMissing() throws Exception {

    mvc.perform(post(PATH_START_VIDEO_CALL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(VALID_START_VIDEO_CALL_BODY)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void rejectVideoCall_Should_ReturnBadRequest_When_VideoCallRejectDtoIsNull()
      throws Exception {

    mvc.perform(post(PATH_REJECT_VIDEO_CALL)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void rejectVideoCall_Should_ReturnBadRequest_When_VideoCallRejectDtoIsEmpty()
      throws Exception {

    mvc.perform(post(PATH_REJECT_VIDEO_CALL)
        .contentType(MediaType.APPLICATION_JSON)
        .content("")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void rejectVideoCall_Should_ReturnOkAndCallService_When_VideoCallRejectDtoIsValid()
      throws Exception {

    String content = new ObjectMapper().writeValueAsString(
        new RejectVideoCallDTO()
            .initiatorRcUserId("rcUserid")
            .initiatorUsername("username")
            .rcGroupId("rcGroupId"));

    mvc.perform(post(PATH_REJECT_VIDEO_CALL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(content)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(this.rejectVideoCallService, times(1)).rejectVideoCall(any());
  }
}
