# phoss SMP Docker Compose Setup

This directory contains a Docker Compose configuration to run the phoss SMP server using the official Docker Hub images.

## Quick Start

Start the SMP server:
```bash
docker-compose up -d
```

Access the SMP server: http://localhost:8080

Default credentials:
Username: `admin@helger.com`
Password: `password`

## Configuration

The configuration file is located at `config/application.properties`. You can modify this file to customize the SMP server settings.

### Important Configuration Notes

- **Backend**: Currently set to `xml` backend (no database required)
- **Data Storage**: All SMP data is persisted in the `./data` directory. 
- **Port**: The server runs on port 8080 by default

## Additional Resources

- [phoss SMP Wiki](https://github.com/phax/phoss-smp/wiki)
- [Docker Hub Images](https://hub.docker.com/r/phelger/phoss-smp-xml)
- [Configuration Reference](https://github.com/phax/phoss-smp/wiki/Configuration)

