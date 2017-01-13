## PivyRepo for Python Gradle builds

Requires an already build local repo in a folde called pypi

## Instructions for creating the local pivyrepo
Requires the pivy repository locally check build.gradle
* download from https://bintray.com/linkedin/maven/pivy-importer#files/com/linkedin/pygradle/pivy-importer/0.3.37
* run as below
```
java -jar ~/Software/pivy-importer-0.3.37-all.jar --repo /home/stefanne/Software/pivy virtualenv:15.0.1 pip:7.1.2 connexion:1.0.129 gunicorn:19.6.0 lxml:3.6.4 rdflib:4.2.1 rdflib-jsonld:0.4.0 PyMySQL:0.7.9 setuptools:19.1.1 swagger-spec-validator:2.0.2 pathlib:1.0.1 wheel:0.26.0 setuptools-git:1.1 flake8:2.5.4 Sphinx:1.4.1 pex:1.1.4 pytest:2.9.1 pytest-cov:2.2.1 pytest-xdist:1.14 setuptools:28.0.0 --replace alabaster:0.7=alabaster:0.7.1,pytz:0a=pytz:2016.4,Babel:0.8=Babel:1.0,sphinx_rtd_theme:0.1=sphinx_rtd_theme:0.1.1,idna:2.0.0=idna:2.1,chardet:2.2=chardet:2.3,setuptools:0.6a2=setuptools:32.1.0

```
