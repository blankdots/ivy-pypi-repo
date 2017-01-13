## PypiRepo for Python Gradle builds

Aim is to simplify work with PyGrale https://github.com/linkedin/pygradle by providing a self-hosted repository for dependencies.

The Docker image provides:
* Nginx container to serve repository files;
* API to initialize repository with a given set of dependencies - TO DO;
* API to add new dependencies;

The Gradle build provides tasks:
* build your own artifact for the API;
* build docker image based on [gradle-dcompose-plugin](https://github.com/chrisgahlert/gradle-dcompose-plugin).

## Build and Run

Running:
* without persistance: `docker run -p 5039:5039 -p 5639:5639 -d imageName`
* with persistance: `docker run -p 5039:5039 -p 5639:5639 -d -v /data:/data imageName`

## Endpoints

The following endpoints are available:
* - for retrieving depenencies
* - for adding depenencies

`curl -X GET -H http://localhost:5039/pypi/{dependencyName}/{version}/dependencyName-version.tar.gz` to retrieve dependency with a specific version number

Structure of the POST body:
```
{
	"dependencies":  [
		{
			"name" : "pytest",
			"version" : "3.0.5"
		},
		{
			"name" : "elizabeth",
			"version" : "0.3.11"
		}
	],
	"replace" : [
		{
			"name": "alabaster",
			"oldVersion": "0.7",
			"newVersion": "0.7.1"
		}
	]
}
```
Example of curl request:

```
curl -X POST -H "Content-Type: application/json" -H "Cache-Control: no-cache" -H "Postman-Token: 434b0f96-728d-f97e-c016-ffcf61b3f54a" -d '{
	"dependencies":  [
		{
			"name" : "pytest",
			"version" : "3.0.5"
		},
		{
			"name" : "elizabeth",
			"version" : "0.3.11"
		}
	],
	"replace" : [
		{
			"name": "alabaster",
			"oldVersion": "0.7",
			"newVersion": "0.7.1"
		}
	]
}' "http://localhost:5639/add"
```
