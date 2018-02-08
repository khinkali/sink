package ch.khinkali.cryptowatch.sink.user.control;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.logging.Logger;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class Users {

    @Inject
    Logger logger;

    public void apply(@Observes String userEvent) {
        logger.info("userEvent: " + userEvent);
    }

}