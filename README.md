![][license img]

Web application created primarily for learning purposes (mainly of Netty) - additionally entire stack call pretend to be non-blocking

Actually mapping looks like 

```
(Route.just("/", PUT), new HomeHandler());
```

maps to 

```
public class HomeHandler extends SimpleChannelInboundHandler<Request>
{
	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Request request) throws Exception
	{
		supplyAsync(() -> new Response(HttpResponseStatus.NO_CONTENT, "Hello World"), ConcurrencyManager.HTTP_OPERATION_STAGE)//
				.thenAcceptAsync(response -> //
						ctx.writeAndFlush(response)//
								.addListener(ChannelFutureListener.CLOSE));
	}

}
```

#Powered by
<img src="http://normanmaurer.me/presentations/2014-netflix-netty/images/netty_logo.png" height="75" width="150">

[license img]:https://img.shields.io/badge/License-Apache%202-blue.svg