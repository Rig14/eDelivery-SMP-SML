# eDelivery-SMP-SML
This is my B.Sc. research project in TalTech. Example usage of eDelivery dynamic discovery with SML/SMP components in the context of eFTI.

## Subprojects
### plugin-user
Simple client to send messages to access point.
Set your message and configure receiver details.
To run it, execute the main method.

## Quick Start

1. Start the services using docker:
```bash
docker compose up
```

## Links

SMP server admin: http://localhost:8080/secure
SMP server public info: http://localhost:8080

Default admin credentials:
Username: `admin@helger.com`
Password: `password`

## Additional Resources

- [phoss SMP Wiki](https://github.com/phax/phoss-smp/wiki)
- [domismp-tomcat-docker](https://github.com/nordic-institute/harmony-smp/tree/harmony-main/domismp-tests/domismp-docker/images/domismp-tomcat-mysql)
- [Docker Hub Images](https://hub.docker.com/r/phelger/phoss-smp-xml)
- [Configuration Reference](https://github.com/phax/phoss-smp/wiki/Configuration)
- [SMP Configuration Guide PDF DE4A](https://wiki.de4a.eu/images/2/2c/Setting_up_a_Service_Metadata_Publisher_for_DE4A.pdf)
- [Fintraffic eFTI interoperability repository](https://github.com/fintraffic-efti/efti-gate-io/tree/master)
- [PMode to SMP mapping](https://ec.europa.eu/digital-building-blocks/sites/spaces/DIGITAL/pages/467117988/eDelivery+SMP+-+1.10)