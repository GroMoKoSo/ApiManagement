# ApiManagement
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

## 🧭 Overview

<div align="center">

<img src="src/main/resources/icon.png" alt="logo" width="200"/>

<p>
ApiManagement is the subservice responsible for managing APis and handling external api calls.  
ApiManagement provides a RESTful API to modify APIs.
</p>

</div>


## 🛠️ Getting Started
Deployment can be easily done by using the Docker compose script inside the 
[meta repository](https://git.thm.de/softwarearchitektur-wz-ss24/studentswa2025/enton/gromokoso).

1. First, clone the meta repository by typing:
```bash
git clone https://git.thm.de/softwarearchitektur-wz-ss24/studentswa2025/enton/gromokoso.git
```

2. Then, use the following script to clone the sub-repositories (including this one):
```bash
bash pull_subrepos.sh
```

3. Make sure docker and docker compose are installed:
```bash
docker --version
docker compose version
```

4. Start all subservices by:
```bash
docker compose up -d
```

5. To stop services, type:
```bash
docker compose down -v
```