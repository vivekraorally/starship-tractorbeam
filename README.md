# starship-tractorbeam

##Generated Using Starship Neutron

This tool can be used to create new Starship microservice projects. It generates all the necessary build sbt files along with
basic client, model, controller and service layers (along with tests).

### Installation

To install starship neutron and create a new microservice backend project:

* Either run the following command. Note that in this case you cant pass -f options to force clean and begin from start.

```
bash <(curl -H 'Authorization: token <YOUR AUTH TOKEN>' -H 'Accept: application/vnd.github.v3.raw' -L https://raw.githubusercontent.com/AudaxHealthInc/starship-neutron-generator/master/script/neutron.sh)
```

The github personal authorization token can be created following [this](https://help.github.com/articles/creating-an-access-token-for-command-line-use/)

* Or, save https://github.com/AudaxHealthInc/starship-neutron-generator/blob/master/script/neutron.sh to some /temp directory and run

```
chmod +x neutron.sh
./neutron.sh
```

### Usage

It will ask you three question on running the script:
* Enter starship microservice name (default: TestManager):
Provide a name using Upper Camel Case.
* Enter local port for microservice to bind to (default: 18020):
Provide a port that is not used.
* Enter local directory for the project to be created at (default: /code):

### Options

* -f options clean's any previous downloaded/saved neutron generator state. Allows you to start from scratch.

### Note

* Do not run the generator after you have created a project. It will overwrite your code.

### Neutron Developers

This project uses
* [Freemarker](http://freemarker.incubator.apache.org/) templating engine from Apache.
* [FMPP](http://fmpp.sourceforge.net/index.html) for running Freemarker on files recursively from command line.
* [rename](http://man7.org/linux/man-pages/man1/rename.1.html) for renaming directory and files names based on pattern regex
* Bash v4 to allow ```shopt -s globstar``` which aids ```rename``` to work recursively on sub-directories
