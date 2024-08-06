# Gitlet Design Document

**Name**:freewind

## Classes and Data Structures

### Main
* It takes in arguments from the command line and based on the command (the first element of the `args` array) calls the corresponding command in `Repository` which will actually execute the logic of the command.
* It also validates the arguments based on the command to ensure that enough arguments were passed in.
#### Fields
This class has no fields and hence no associated state: it simply validates arguments and defers the execution to the `Repository` class.
***


### Commit
* point corresponding file 
`list structure`
* point parent Commit
* once made never change, 
just make new commit
* contain log message
(hash number, time, commit message)


#### Fields
* `private String message` 
* `private ZonedDateTime time`
* `private Commit parent`
* head? point current branch latest commit
* branch? commit tree?
* a constructor
* read a`file`return a`Commit`class
* write method



***


### Repository

#### Fields

1. Field 1
2. Field 2
***

## Algorithms

## Persistence

