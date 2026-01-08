# Harmony SMP

Harmony eDelivery ecosystem does not provide easily deployable docker images at this time.
For that reason the software was containerized in the scope of this project using the official [harmony-smp codebase](https://github.com/nordic-institute/harmony-smp/tree/harmony-main).

Harmony SMP codebase offers a `domismp-tests` folder where the effort to containerize the application is made.
Turing the time of writing it seems that this part of the codebase has been entirely unchanged
from when it was forked over from domibus SMP server, resulting in broken scripts in the `domismp-tests` folder.

Harmony SMP codebase has been forked and most of the issues fixed in this repository: https://github.com/Rig14/harmony-smp.
From there it is possible to package the codebase into docker images. These docker images have also been published to docker hub: https://hub.docker.com/r/rig14/harmony-smp-mysql

Please note that the containerized solutions are not production ready and are useful for only research or development purposes.

# Findings

Some things to note:

- location of the `ServiceGroup` xml is not from the root of the app. it's in the form of `http://localhost:8080/smp/testdomain/smp-1/urn%3Aoasis%3Anames%3Atc%3Aebcore%3Apartyid-type%3Aunregistered%3A%3Areceiver`
- the same applies for the service metadata `http://localhost:8080/smp/urn%3Aoasis%3Anames%3Atc%3Aebcore%3Apartyid-type%3Aunregistered%3Areceiver/services/%3A%3Aeftigateservice`

[ServiceGroup](smp-findings/harmony-smp-group.xml)
[SignedServiceMetadata](smp-findings/harmony-smp.xml)
