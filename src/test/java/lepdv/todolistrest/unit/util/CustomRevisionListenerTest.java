package lepdv.todolistrest.unit.util;

import lepdv.todolistrest.entity.Revision;
import lepdv.todolistrest.util.AuthUser;
import lepdv.todolistrest.util.CustomRevisionListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static lepdv.todolistrest.Constants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CustomRevisionListenerTest {




    @Test
    void newRevision() {
        final CustomRevisionListener listener = new CustomRevisionListener();
        final Revision revisionMock = mock(Revision.class);
        doNothing().when(revisionMock).setModifiedBy(USER.getUsername());
        doNothing().when(revisionMock).setDateTime(any());

        try (MockedStatic<AuthUser> authUserMock = mockStatic(AuthUser.class)) {
            authUserMock.when(AuthUser::getAuthUsername).thenReturn(USER.getUsername());
            listener.newRevision(revisionMock);

            authUserMock.verify(AuthUser::getAuthUsername);
        }
        verify(revisionMock).setModifiedBy(USER.getUsername());
        verify(revisionMock).setDateTime(any());
    }


}