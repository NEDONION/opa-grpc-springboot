# A Demo App with OPA, gRPC, and Spring Boot

![](https://raw.githubusercontent.com/NEDONION/my-pics-space/main/20240103160033.png)

## Requisites
- Java
- Spring Boot
- gRPC
- Open Policy Agent
- Docker

## Get Started

### OPA rego - build one file
```sh
opa build opa/policies/Example.rego -o opa/bundles/bundle.tar.gz
```

### OPA rego - build directory

- It will compile all Rego policy files from the `opa/policies/` directory into a single bundle file `bundle.tar.gz`,
and move this file to output path we define, for deployment in OPA.
- 这条命令会编译 `opa/policies/` 目录下的所有 Rego 文件，并将生成的 bundle 保存到指定的输出路径。

```sh
opa build opa/policies/ -o opa/bundles/bundle.tar.gz
```

this will generate `./bundles/bundles.tar.gz` file that will be mounted to `nginx` web server, where `opa engine` will read the policies from.

### Docker start

```shell
docker compose up
```

```shell
docker compose down
```

### Protobuf generate gRPC code

```shell
mvn clean install
```

### Run Application
```shell
mvn spring-boot:run
```

