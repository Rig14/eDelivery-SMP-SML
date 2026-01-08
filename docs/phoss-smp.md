# phoss SMP
GitHub link: https://github.com/phax/phoss-smp 

This project is mainly made for PEPPOL related use cases, but it's also CEF eDelivery conformant.

It is containerized and easily deployable on local system making it a good choice for these kind of use cases. 
Usually PEPPOL related software is very expensive or very hard to deploy. This is an exception to that.

Using the XML variant of the software the deployment is even easier, no need to dedicate a separate container for a database.

# Findings

Some things to note:

- validation of form fields is very strict. It is not possible to use the values that eFTI currently uses for its services.
- compared to [harmony-smp](harmony-smp.md), service metadata is served from the root, with no prefixed path variables.
- group url is: `http://localhost:8080/%3A%3Aurn%3Aoasis%3Anames%3Atc%3Aebcore%3Apartyid-type%3Aunregistered%3AC1`
- service metadata url is `http://localhost:8080/%3A%3Aurn%3Aoasis%3Anames%3Atc%3Aebcore%3Apartyid-type%3Aunregistered%3AC1/services/busdox-docid-qns%3A%3AeftiGateAction`

[ServiceGroup](smp-findings/phoss-smp-group.xml)
[SignedServiceMetadata](smp-findings/phoss-smp.xml)

