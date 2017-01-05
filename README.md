Lightweight mavenized Java web service (configured as a micro service).

Run this via Main.main()

This runs on port 5000 (conveniently configured for deployment to aws elastic beanstalk).

Sample endpoint: http://localhost:5000/gws/test/json

Features:
* Grizzly Web Server
* Jersey REST API
* Jackson Mashalling
* Jwt Authentication / Authorization
* Dispatching an async job to Amazon's SQS
* Sending an email via SendGrid
* Server Side Events (w/ Jersey)
* MBean Monitoring
* Swagger UI
* File Upload http handler
