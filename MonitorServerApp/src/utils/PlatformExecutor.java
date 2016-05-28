package utils;

import java.util.concurrent.Executor;
import javafx.application.Platform;

/**
 * @author ROHRER Michaël
 *
 */
public class PlatformExecutor implements Executor {

	public static final PlatformExecutor instance = new PlatformExecutor();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Executor#execute(java.lang.Runnable)
	 */
	@Override
	public void execute(Runnable command) {
		Platform.runLater(command);
	}

}
