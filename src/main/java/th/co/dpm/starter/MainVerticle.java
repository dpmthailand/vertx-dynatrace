package th.co.dpm.starter;

import io.vertx.core.Vertx;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.core.json.JsonObject;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.handler.ResponseTimeHandler;

public class MainVerticle extends AbstractVerticle {
  @Override
  public void start() throws Exception {
	  
    // Create a Router
    Router router = Router.router(vertx);

	router.route().handler(ResponseTimeHandler.create());

    // Mount the handler for all incoming requests at every path and HTTP method
    router.route().handler(context -> {

      // Get the query parameter "name"
      MultiMap queryParams = context.queryParams();
      String name = queryParams.contains("name") ? queryParams.get("name") : "unknown";
	  
	  System.out.println("New message came for name: " + name);
	  
	  // Prepare request
	  WebClient client = WebClient.create(vertx);
	  
	  HttpRequest<Buffer> request = client.post(443, "notify-api.line.me", "/api/notify")
	  .ssl(true)
	  .bearerTokenAuthentication("REPLACE WITH YOUR TOKEN");  
	  
	  request.putHeader("content-type", "application/x-www-form-urlencoded");
      request.putHeader("accept", "*/*");
	  
	  MultiMap form = MultiMap.caseInsensitiveMultiMap();
	  form.set("message", "Hi " + name + " from Vert.x Line Notify ❤");

	  System.out.println("Posting to Line Notify API....");

	  // Submit the form as a form URL encoded body
	  request.as(BodyCodec.jsonObject()).sendForm(form).onSuccess(res -> {
		JsonObject body = res.body();
		
		System.out.println(
		  "Received response from Line Notify => " + body);
			
		context.response().end("Received response from Line Notify => " + body);
	  }).onFailure(err -> {
		System.out.println("Something went wrong " + err.getMessage());
		context.response().end("Something went wrong " + err.getMessage());
	  });
    });

    // Create the HTTP server
    vertx.createHttpServer()
      // Handle every request using the router
      .requestHandler(router)
      // Start listening
      .listen(8889)
      // Print the port
      .onSuccess(server ->
        System.out.println(
          "HTTP server started on port " + server.actualPort()
        )
      );
  }
}
