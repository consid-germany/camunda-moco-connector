## 📆 Schedules

### ⛔️ Absences

Creates an absence in the planning overview, like sick day or other absences.
See the [API docs](https://hundertzehn.github.io/mocoapp-api-docs/sections/schedules.html#post-schedules) for more details

| Name            | Description                                                                                                      | Example          |
|-----------------|------------------------------------------------------------------------------------------------------------------|------------------|
| Employee ID     | The id of the employee in Moco. You should set this as input variable for your process.                          | `=employeeId`    |
| Date of Absence | The ISO-8601 formatted string for the date. This should be an input variable.                                    | `=absenceDate`   |
| Absence Type    | The different types of absences supported by Moco                                                                | <Dropdown Value> |
| Absence Mode    | Is the absence for half the day or the full day                                                                  | <Dropdown Value> |
