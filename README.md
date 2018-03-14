## PypiRepo for PyGradle - Python Gradle builds

Aim is to simplify work with PyGradle https://github.com/linkedin/pygradle by providing a self-hosted repository for dependencies.

The Docker image provides:
* NGINX container to serve repository files;
* API developed using http://sparkjava.com/ :
    * endpoint to initialize repository with a given set of dependencies;
    * endpoint to add new dependencies;
    * endpoint to upload `requirements.txt` - TO DO.

## Build and Run

Build with gradle:
* gradle wrapper for Gradle 4.2 provided - use `./gradlew`;
* build the ivy-pypi-repo - `./gradlew build` a.k.a. your own artifact for the API;
* run the ivy-pypi-repo - `./gradlew run`
* build the docker image - `./gradlew buildPypiImage` it will generate the `attx-dev/pypirepo` at build (use it to run the container) -  based on [gradle-dcompose-plugin](https://github.com/chrisgahlert/gradle-dcompose-plugin) - see for more tasks.

Other commands:
* `./gradlew run` on the ivy-pypi-repo to run the server locally for adding dependencies
* `./gradlew tasks --all` - see all tasks

Running the docker image:
* without persistence: `docker run -p 5039:5039 -p 5639:5639 -d blankdots/ivy-pypi-repo`
* with persistence: `docker run -p 5039:5039 -p 5639:5639 -d -v /data:/data/pivy blankdots/ivy-pypi-repo`

### PyGradle usage

In order to use this Ivy repository with PyGradle one can set it up in the `build.gradle` as:

``` {groovy}
repositories {
    // the webrepository
    ivy{
      name 'pypi-repo'
  		url "http://pypirepo:5039/"
  		layout 'pattern' , {
  			artifact '[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]'
  			ivy '[organisation]/[module]/[revision]/[module]-[revision].ivy'
  		}
    }
}
```

## Endpoints

The following endpoints are available:
* `http://localhost:5039/pypi` - for retrieving dependencies;
* `http://localhost:5639/init`- for initialising the repository with dependencies;
* `http://localhost:5639/add`- for adding dependencies (using HTTP POST method).

The init dependencies can be managed in `resources/init.json` file.

Adding new repository can be achieved by `http://localhost:5639/add` endpoint. JSON Request example:
```{json}
{
	"dependencies":  [
		{
			"name" : "dependency",
			"version" : "1.0.0"
		}
	],
	"replace" : [
		{
			"name": "dependency",
			"oldVersion": "0.1",
			"newVersion": "0.1.1"
		}
	]
}
```

**NOTE that multile dependencies at the same time might not be possible to import due to the pivy-importer.**

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
