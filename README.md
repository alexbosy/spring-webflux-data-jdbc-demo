# User management service

It's a demo app to test the following technical stack:

- Java 17
- Spring Boot 3+
- Spring Web Flux
- Spring Data JDBC
- Reactor
- PostgreSQL
- Spock Framework
- Springdoc OpenAPI
- Spring GraphQL
- Spring Security with JWT auth
- Resilience4j

## Test application requirements

We must develop the user service, that allows us to manage basic operations with User and Customer domain
objects, as well as customer registration and authentication (optional subtask).

The service will have the 2 main entities:

- User - represents the "internal application user", that is user for private administration app, and identity, that is
  used for authentication and contains the user type (ADMIN, MANAGER, CUSTOMER).
- Customer - user extension, the "application end customer" or "public application user", that includes
  additional data.

### User data:

- id
- login
- name
- surname
- email
- password
- type - ADMIN/MANAGER/CUSTOMER

### Additional Customer data:

- date of birth
- country of residence
- identity number
- passport number
- registration IP address (not exposed to customer)
- registration country (not exposed to customer)

### Private REST endpoints (will be used only in internal administration application):

#### 1. POST /user - create new user.

Create a new user by supplied data, password must be stored in DB in encrypted form.

##### Request example:

```json
{
  "login": "some login",
  "name": "some name",
  "surname": "some surname",
  "email": "some email",
  "password": "some password",
  "type": "ADMIN"
}
```

##### Response example:

```json
{
  "id": "some id",
  "login": "some login"
}
```

##### Request data validation:

All request data fields must be not empty and have some min/max length limits (on your choice). Email address must be of
a valid form (use corresponding RegEx pattern).

**Sample error responses:**

HTTP/1.1 400 Bad Request

```json
{
  "login": "Login min length is 6 chars",
  "email": "Email is not valid",
  "name": "Name can not be empty",
  "password": "Password min length is 8 chars",
  "surname": "Surname can not be empty"
}
```

##### Business validation:

* Supported types are only ADMIN/MANAGER.
* Login must be unique.
* Email must be unique.

**Sample error responses:**

HTTP/1.1 403 Forbidden

```json
{
  "login": "Supplied login is already taken"
}
```

HTTP/1.1 403 Forbidden

```json
{
  "email": "Supplied email is already taken"
}
```

#### 2. GET /user/{:id} - get user data by id.

Return the User object data except password.

##### Response example:

```json
{
  "id": 35,
  "login": "some-login",
  "name": "some name",
  "surname": "some surname",
  "email": "some@email.com",
  "type": "ADMIN"
}
```

#### 3. PUT /user/{:id} - update user data by id.

##### Request example:

PUT /user/35

body:

```json
{
  "login": "updated login",
  "name": "updated name",
  "surname": "some surname",
  "email": "some@email.com",
  "type": "ADMIN"
}
```

##### Response example:

```json
{
  "id": 35,
  "login": "updated login",
  "name": "updated name",
  "surname": "updated surname",
  "email": "some@email.com",
  "type": "ADMIN"
}
```

##### Request data and business validation:

Must be the same as for "create new user" endpoint.

#### 4. DELETE /user/{:id} - delete user by id.

#### 5. GET /users?offset={:offset}&limit={:limit}

Return a pageable list of users (user type is ADMIN/MANAGER) with specified offset and limit.

##### Request example:

GET /users?offset=0&limit=2

##### Response example:

```json
[
  {
    "id": 26,
    "login": "some login",
    "name": "some name",
    "surname": "some surname",
    "email": "new@new.com",
    "type": "ADMIN"
  },
  {
    "id": 27,
    "login": "some login2",
    "name": "some name2",
    "surname": "some surname2",
    "email": "new@new2.com",
    "type": "MANAGER"
  }
]
```

#### 6. GET /customers?offset={:offset}&limit={:limit}

Return a pageable list of customers (user type is CUSTOMER) with specified offset and limit.
All customer data must be returned, this endpoint will be used only in
admin application.

##### Request example:

GET /customers?offset=0&limit=2

##### Response example:

```json
[
  {
    "id": 1,
    "countryOfResidence": "US",
    "dateOfBirth": "09-12-1997",
    "identityNumber": "101297-10111",
    "passportNumber": "LV9384938498",
    "registrationCountry": "LV",
    "registrationIp": "88.22.33.44",
    "userId": 26,
    "login": "some login",
    "email": "new@new.com",
    "name": "some name",
    "surname": "some surname"
  },
  {
    "id": 2,
    "countryOfResidence": "US",
    "dateOfBirth": "03-12-1980",
    "identityNumber": "041289-15717",
    "passportNumber": "US3948938433",
    "registrationCountry": "US",
    "registrationIp": "92.33.45.122",
    "userId": 27,
    "login": "some login",
    "email": "new@new.com",
    "name": "some name",
    "surname": "some surname"
  }
]
```

#### 7. GET /customer/{login} - get customer user by login.

Return the customer user data for the specified login. All data must be returned, this endpoint will be used only in
admin application.

##### Request example:

GET /customer/login

##### Response example:

```json
{
  "id": 64,
  "login": "login",
  "email": "email@email.com",
  "name": "some name",
  "surname": "some surname",
  "countryOfResidence": "US",
  "dateOfBirth": "06-12-1982",
  "identityNumber": "identity number",
  "passportNumber": "passport number",
  "registrationCountry": "XX",
  "registrationIp": "127.0.0.1",
  "userId": 320
}
```

#### 8. DELETE /customer/{userId} - delete customer user by supplied user id.

### Public REST endpoints (will be used in public web/mobile apps):

All customer object responses must not contain "id" field.

#### 1. POST /customer/registration - register a new customer.

Create a new user and related customer by supplied data.

**The stored Customer domain object must include the following data:**

1. **Registration IP address**. It must be resolved using incoming HTTP request taking into account, that our service
   can be deployed behind a reverse proxy or load balancer. This field must not be exposed to the end public users of our
   public application and will be accessible only for admins and managers in our administration application.
2. **Registration country**. It must be resolved via some public external GeoIP HTTP service,
   e.g. http://reallyfreegeoip.org.
   When implementing this external service call, you must think about application end users (system responsiveness)
   and reduce the response time of the parent REST endpoint, so the resolution can be done in async way. You also should
   think about the overall system stability and how to protect it from external service performance
   degradation and failures (system resiliency). This field also must not be exposed to the end public users.

##### Request example:

POST /customer/registration

body:

```json
{
  "login": "some login",
  "name": "some name",
  "surname": "some surname",
  "email": "some@email.com",
  "password": "some password",
  "dateOfBirth": "12-12-1981",
  "countryOfResidence": "DE",
  "identityNumber": "identity number",
  "passportNumber": "passport number"
}
```

##### Response example:

```json
{
  "login": "some login",
  "email": "some@email.com",
  "name": "some name",
  "surname": "some surname",
  "dateOfBirth": "12-12-1981",
  "countryOfResidence": "UK",
  "identityNumber": "identity number",
  "passportNumber": "passport number"
}
```

##### Request data validation:

All request data fields must be not empty and have some min/max length limits (on your choice). Email address must be of
a valid form (use corresponding RegEx pattern).

##### Business validation:

* Login must be unique.
* Email must be unique.

#### 2. GET /customer/profile/{:login} - get customer public profile by login.

Show only login, name, surname, email, date of birth and country of residence.

##### Request example:

GET /customer/profile/some-login

##### Response example:

```json
{
  "login": "some-login",
  "name": "some name",
  "surname": "some surname",
  "email": "1670610229829@at-tests.lv",
  "dateOfBirth": "09-12-1997",
  "countryOfResidence": "US"
}
```

### An OPTIONAL endpoints/tasks that can be skipped:

#### 1. POST /auth - JWT authentication. Returns JWT token.

##### Request example:

POST /auth

body:

```json
{
  "login": "some login",
  "password": "some password"
}
```

##### Response example:

```json
{
  "token": "JWT token"
}
```

#### 2. PUT /customer/my/profile - update current (authenticated) customer's profile by id (requires JWT auth with user type CUSTOMER).

Return all data except id, password, IP and registration country.

##### Request example:

PUT /customer/my/profile <br>
Authorization:"Bearer ${JWT_TOKEN}"

body:

```json
{
  "name": "some name",
  "surname": "some surname",
  "email": "new@newemail.com",
  "dateOfBirth": "11-11-1971",
  "countryOfResidence": "UK",
  "identityNumber": "identity number",
  "passportNumber": "passport number"
}
```

##### Response example:

```json
{
  "login": "somelogin16",
  "name": "some name",
  "surname": "some surname",
  "email": "new@newemail.com",
  "dateOfBirth": "11-11-1971",
  "countryOfResidence": "DE",
  "identityNumber": "identity number",
  "passportNumber": "passport number"
}
```

#### 3. GET /customer/my/profile - get current (authenticated) customer's profile (requires JWT auth with user type CUSTOMER).

Show all data except id, password, IP and registration country.

##### Request example:

GET /customer/my/profile <br>
Authorization:"Bearer ${JWT_TOKEN}"

##### Response example:

```json
{
  "login": "some login",
  "name": "some name",
  "email": "email@xxx.com",
  "surname": "some surname",
  "countryOfResidence": "US",
  "dateOfBirth": "12-12-1981",
  "identityNumber": "identity number",
  "passportNumber": "passport number"
}
```

#### 4. GET /me - get current(authenticated) user. (requires JWT auth with user type ADMIN or MANAGER)

Return the current authenticated user (ADMIN or MANAGER).

##### Request example:

GET /me <br>
Authorization:"Bearer ${JWT_TOKEN}"

##### Response example:

```json
{
  "id": 26,
  "login": "login",
  "name": "some name",
  "surname": "some surname",
  "email": "new@new.com",
  "type": "ADMIN"
}
```

#### 5. Add authentication to some other endpoints on your choice using different roles.

#### 6. Besides unit and integration tests implement at least some e2e (end to end/acceptance) tests.

#### 7. Implement also another API protocols (not HTTP REST, e.g GraphQL, gRPC, WebSocket) for any of already existing API endpoints.