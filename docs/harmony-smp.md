# Harmony SMP

Harmony eDelivery ecosystem does not provide easily deployable docker images at this time.
For that reason the software was containerized in the scope of this project using the official [harmony-smp codebase](https://github.com/nordic-institute/harmony-smp/tree/harmony-main).

Harmony SMP codebase offers a `domismp-tests` folder where the effort to containerize the application is made.
Turing the time of writing it seems that this part of the codebase has been entirely unchanged
from when it was forked over from domibus SMP server, resulting in broken scripts in the `domismp-tests` folder.

Harmony SMP codebase has been forked and most of the issues fixed in this repository: https://github.com/Rig14/harmony-smp.
From there it is possible to package the codebase into docker images. These docker images have also been published to docker hub: https://hub.docker.com/r/rig14/harmony-smp-mysql

Please note that the containerized solutions are not production ready and are useful for only research or development purposes.
