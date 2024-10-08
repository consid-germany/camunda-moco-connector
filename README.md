# The Camunda MOCO Connector

[![Maven Package](https://github.com/consid-germany/camunda-moco-connector/actions/workflows/maven-build.yml/badge.svg)](https://github.com/consid-germany/camunda-moco-connector/actions/workflows/maven-build.yml)

A connector to talk to the MOCO App API. MOCO is a lightweight ERP software. Check out https://www.mocoapp.com

<p></p>
<p align="center" style="margin: 50px">
  <img src="example.png" width="500" alt="Example process with the MOCO connector"/>
</p>
<p></p>

The connector is based on Camunda's out of the box REST connector (v8.5+). Features will be added one by one, if you need anything reach out to me.

NOTE: I am **not** a developer working for MOCO, if you have any requests about the API you need to reach out to the developers.


## ✅ Features
| Name            | Description                                                                      | API Reference                                                                                 | 
|-----------------|----------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|
| Create User     | During on-boarding it is necessary to create a user                              | [Link](https://hundertzehn.github.io/mocoapp-api-docs/sections/users.html#post-users)         |
| Deactivate User | During off-boarding it is necessary to deactivate a user, instead of a deletion. | [Link](https://hundertzehn.github.io/mocoapp-api-docs/sections/users.html#put-usersid)        |
| Create Absences | Create an absence in MOCO, e.g. sick leave or vacation days.                     | [Link](https://hundertzehn.github.io/mocoapp-api-docs/sections/schedules.html#post-schedules) |

## ⚙️ Configuration

### 📍 General
| Name            | Description                                                                                | Example                                |
|-----------------|--------------------------------------------------------------------------------------------|----------------------------------------|
| MOCO API Url    | The specific endpoint for your company                                                     | `http://my-company.mocoapp.com/api/v1` |
| MOCO API Key    | The reference to your API key for your company. Keep your secrets in the secret store.     | `{{ secrets.MOCO_API_KEY}}`            | 

### 🛠️ Operation Specific

* [Schedule Endpoint](./docs/Schedules.md)
* [Users Endpoint](./docs/Users.md)

## 🏠 Local Tests

I use the local Camunda stack together with Wiremock to test my template:

* Camunda Connector Runtime
* Camunda REST Connector
* Wiremock to simulate the MOCO Rest API
* Camunda Starter Test for the in-memory Zeebe engine

<p align="center" style="margin: 50px">
  <img src="test_setup.png" width="500" alt="Test setup"/>
</p>

## 📬 Contact

If you have any questions regarding this connector you can reach me at camunda-connector-support (at) consid.se

## 🔗 Links
* API docs source: https://github.com/hundertzehn/mocoapp-api-docs/tree/master
* API docs: https://hundertzehn.github.io/mocoapp-api-docs/entities.html
* REST Connector template: https://github.com/camunda/connectors/blob/main/connectors/http/rest/element-templates/http-json-connector.json
* FEEL: https://docs.camunda.io/docs/components/modeler/feel/what-is-feel/
