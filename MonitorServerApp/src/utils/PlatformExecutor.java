package utils;

import java.util.concurrent.Executor;
import javafx.application.Platform;

public class PlatformExecutor implements Executor {

	public static final PlatformExecutor instance = new PlatformExecutor();

	@Override
	public void execute(Runnable command) {
		Platform.runLater(command);
	}

}
