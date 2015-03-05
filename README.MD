Reactive REST service template.
=================================

REST service template, builded on Play2 Framework with usage of MongoDB and ReactiveMongo driver. Can be used for creation of new application with ready schematic.

# Launch
To launch project, you must install Java (version 7+) and MongoDB. After that use command line to launch activator as:
```
activator run
```

## Vagrant
You can launch project in vagrant virtual environment with predefined config file:
```
cd vagrant
vagrant up
vagrant ssh
cd /project
./activator run
```
Application will be rebinded on 9090 port

# Test API scheme
API scheme can be found by this link http://docs.reactiverest.apiary.io
