# The Camunda Moco Connector

A connector to talk to the MOCO App API. MOCO is a lightweight ERP software. Check out https://www.mocoapp.com

The connector is based on Camunda OOTB REST connector. Features will be added one by one, if you need anything reach out to me.

NOTE: I am **not** a developer working for Moco, if you have any requests about the API you need to reach out to the developers.

## ✅ Features
| Category  | Name            | Description                                                  | API Reference                                                                                  | 
|-----------|-----------------|--------------------------------------------------------------|------------------------------------------------------------------------------------------------|
| Schedules | Create Absences | Create an absence in Moco, e.g. sick leave or vacation days. | [Link](https://hundertzehn.github.io/mocoapp-api-docs/sections/schedules.html#post-schedules)  |

## ⚙️ Configuration

### 📍 General
| Name            | Description                                                                                                                         | Example                              |
|-----------------|-------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------|
| Moco API Url    | The specific endpoint for your company                                                                                              | http://my-company.mocoapp.com/api/v1 |
| Moco API Key    | The reference to your API key for your company. You should not add the value here, but in the secret store. Only add the reference. | {{ secrets.MOCO_API_KEY}}            | 

### 📆 Schedules

#### Absences

| Name            | Description                                                                                                                         | Example          |
|-----------------|-------------------------------------------------------------------------------------------------------------------------------------|------------------|
| Employee ID     | The id of the employee in Moco. You should set this as input variable for your process.                                             | `=employeeId`    |
| Date of absence | The ISO-8601 formatted string for the date. This should also be an input variable.                                                  | `=absenceDate`   |
| Absence Type    | The different types of absences supported by Moco                                                                                   | <Dropdown Value> |
| Absence Mode    | This means if the absence is for half the day or the full day                                                                       | <Dropdown Value> |

## 🏠 Local Tests

I use the local Camunda stack together with mountebank to test my connector:

* start the docker-compose file
* [setup mountebank](mountebank/README.md)
* copy the content of the element template folder into a `.camunda/element-templates` folder in your repository root
  * this way the Desktop Modeler picks it up if you put the process model right next to it, and you can use the connector from there
  * See the [documentation from Camunda for more info](https://docs.camunda.io/docs/components/modeler/desktop-modeler/element-templates/configuring-templates/)
* configure the connector
* NOTE: currently it is **not possible** for the REST connector to reach mountebank under `http://localhost:4545/api/v1` 
so you need to replace _localhost_ with your local IP address for now

## 📬 Contact

If you have any questions regarding this connector you can reach me at stefan.schultz (at) consid.com

## 🔗 Links
* API docs source: https://github.com/hundertzehn/mocoapp-api-docs/tree/master
* API docs: https://hundertzehn.github.io/mocoapp-api-docs/entities.html
* REST Connector template: https://github.com/camunda/connectors/blob/main/connectors/http/rest/element-templates/http-json-connector.json
* FEEL: https://docs.camunda.io/docs/components/modeler/feel/what-is-feel/
