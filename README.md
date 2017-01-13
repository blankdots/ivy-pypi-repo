## PypiRepo for PyGradle - Python Gradle builds

Aim is to simplify work with PyGradle https://github.com/linkedin/pygradle by providing a self-hosted repository for dependencies.

The Docker image provides:
* Nginx container to serve repository files;
* API to initialize repository with a given set of dependencies - TO DO;
* API to add new dependencies;
* API developed using http://sparkjava.com/

The Gradle build provides tasks:
* gradle wrapper for Gradle 3.3 provided
* build your own artifact for the API; 
* build docker image based on [gradle-dcompose-plugin](https://github.com/chrisgahlert/gradle-dcompose-plugin).

## Build and Run

Build with gradle:
* build the ivy-pypi-repo - `./gradlew build`
* build the docker image - `./gradlew gradle buildpypiImage` it will generate a imageName at build (use it to run the container)

Other commands:
* `./gradlew run` on the ivy-pypi-repo to run the server locally for adding dependencies
* `./gradlew tasks --all` - see all tasks

Running the docker image:
* without persistance: `docker run -p 5039:5039 -p 5639:5639 -d imageName`
* with persistance: `docker run -p 5039:5039 -p 5639:5639 -d -v /data:/data imageName`

## Endpoints

The following endpoints are available:
* `http://localhost:5039/pypi` - for retrieving depenencies
* `http://localhost:5639/add`- for adding depenencies

Structure of the POST body for `http://localhost:5639/add`:
```
{
	"dependencies":  [
		{
			"name" : "dependency",
			"version" : "1.0.0"
		},
		...
	],
	"replace" : [
		{
			"name": "dependency",
			"oldVersion": "0.1",
			"newVersion": "0.1.1"
		},
    ....
	]
}
```


Example of curl requests:

* `curl -X GET -H http://localhost:5039/pypi/{dependencyName}/{version}/dependencyName-version.tar.gz` to retrieve dependency with a specific version number


* `curl -X POST -H "Content-Type: application/json" -H "Cache-Control: no-cache" -H "Postman-Token: 434b0f96-728d-f97e-c016-ffcf61b3f54a" -d '{
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
` - for adding dependencies
