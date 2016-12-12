![][license img]

Web application created primarily for learning purposes. 

It would be cool to ends with such "path to function mapping" : 

```
( "/", new UpsertNoteRequestHandler() ) 

```
maps to 
```
Function<Request, Response> requestHandler = request -> {

	notesRepository.upsertNote(request.getUser(), request.getPayload().get());

	return new Response(HttpResponseStatus.OK);	
};
```

#Powered by
<img src="http://normanmaurer.me/presentations/2014-netflix-netty/images/netty_logo.png" height="75" width="150">

[license img]:https://img.shields.io/badge/License-Apache%202-blue.svg