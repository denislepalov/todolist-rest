package lepdv.todolistrest.util;

import lepdv.todolistrest.entity.Revision;
import org.hibernate.envers.RevisionListener;

import java.time.Instant;


public class CustomRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object userRevision) {

        String authUsername = AuthUser.getAuthUsername();
        Revision revision = (Revision) userRevision;
        revision.setDateTime(Instant.now());
        revision.setModifiedBy(authUsername);
    }
}
