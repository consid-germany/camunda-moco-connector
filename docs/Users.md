## 👩🏼‍💻 Users

### ➕ Create User

Create a user.
See the [API docs](https://hundertzehn.github.io/mocoapp-api-docs/sections/users.html#post-users) for more details

| Name      | Description                                                  | Example      |
|-----------|--------------------------------------------------------------|--------------|
| Firstname | The first name(s) of the new employee                        | `=firstname` |
| Lastname  | The lastname of the new employee                             | `=lastname`  |
| E-Mail    | The company's e-mail address of the new employee             | `=mail`      |
| Team ID   | The numerical team id in Moco, to which the employee belongs | `=teamId`    |

### ❌ Deactivate User

Deactivates a user.
See the [API docs](https://hundertzehn.github.io/mocoapp-api-docs/sections/users.html#put-usersid) for more details

| Name            | Description                                                                                                      | Example          |
|-----------------|------------------------------------------------------------------------------------------------------------------|------------------|
| Employee ID     | The id of the employee in Moco. You should set this as input variable for your process.                          | `=employeeId`    |

