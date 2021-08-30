package th.co.dpm.starter;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.Launcher;
import io.vertx.core.impl.launcher.VertxLifecycleHooks;
import io.vertx.tracing.zipkin.ZipkinTracingOptions;
import io.vertx.tracing.opentelemetry.OpenTelemetryOptions;

public class MyLauncher extends Launcher implements VertxLifecycleHooks {


  @Override
  public void beforeStartingVertx(VertxOptions options) {
	
	/*
    options.setTracingOptions(
		new ZipkinTracingOptions().setServiceName("LineHello")
	  );
	*/
	
	options.setTracingOptions(
		new OpenTelemetryOptions()
	);
  }

	public static void main(String[] args) {
		new MyLauncher().dispatch(args);
	}

	public static void executeCommand(String cmd, String... args) {
		new MyLauncher().execute(cmd, args);
	}

}